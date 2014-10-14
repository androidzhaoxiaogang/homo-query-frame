package com.howbuy.appframework.homo.queryapi.parser.redis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.howbuy.appframework.homo.configure.commons.redis.ModeType;
import com.howbuy.appframework.homo.configure.db.table.vhmtablemeta.VHmTableMetaField;
import com.howbuy.appframework.homo.configure.db.table.vhmtablemeta.VHmTableMetaTable;
import com.howbuy.appframework.homo.fincache.redis.cluster.RedisServManager;
import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.analyzer.common.DmlType;
import com.howbuy.appframework.homo.queryapi.cache.CacheField;
import com.howbuy.appframework.homo.queryapi.common.Symbols;
import com.howbuy.appframework.homo.queryapi.common.utils.HomoLogUtils;

/**
 * redis缓存处理指挥官.
 * RCCommander 是redis cache command缩写.
 * @author li.zhang
 */
public final class RCCommander
{
    /** 向redis写服务器发送写操作的redis客户端. **/
    private static final JedisPool jedisWriterPool;  
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    static
    {
        jedisWriterPool = RedisServManager.getInstance().getJedisClientProxy(ModeType.WRITE_ONLY.getStrValue());
    }
    
    /**
     * 清空缓存
     */
    public void clearCache()
    {
        Jedis jedisWriter = jedisWriterPool.getResource();
        jedisWriter.flushAll();
        jedisWriterPool.returnResource(jedisWriter);
    }
    
    /**
     * 全量加载所有配置的表进redis缓存.
     */
    public void refreshAllData()
    {
        logger.info("Load all data to redis begin.................");
        
        //根据v_hm_table_meta视图，来决定哪些表的哪些字段需要被加载到缓存中
        Map<String, List<VHmTableMetaField>> recordMaps = VHmTableMetaTable.getInstance().getRecordMaps();
        if (!CollectionUtils.isEmpty(recordMaps))
        {
            String[] templateIds = new String[recordMaps.keySet().size()];
            recordMaps.keySet().toArray(templateIds);
            Arrays.sort(templateIds);
            
            //一个templateId对应一个表或者一个视图.
            for (String templateId : templateIds)
            { 
                logger.info("Load all data to redis begin, the templateId is " + templateId);
                try
                {
                    refreshAllData(templateId);
                }
                catch (Exception e)
                {
                    logger.error("Load all data to redis error, the templateId is " + templateId + ", error stack is " + e);
                    HomoLogUtils.printStackTrace(logger, e);
                    continue;
                }
                
                logger.info("Load all data to redis end, the templateId is " + templateId);
            }
        }
        logger.info("Load all data to redis end...................");
    }

    /**
     * 将表名为tableName的所有记录导入到redis中.
     * @param templateId 将模板对应的表记录导入到redis中.
     * @throws Exception
     */
    public void refreshAllData(final String templateId) throws Exception
    {
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return;
        }
        
        List<VHmTableMetaField> columns = recordMap.get(templateId);
        if (CollectionUtils.isEmpty(columns))
        {
            return;
        }
        
        //得到表的总记录数.
        long count = getRecordNum(templateId);
        
        //对于大表，需要多线程加载..
        int threadNum = (int)((count % 10000 == 0) ? (count / 10000) : (count / 10000) + 1);
        
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++)
        {
            final int pageNo = i + 1;
            threads[i] = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        doBusiness(templateId, pageNo, 10000);
                    }
                    catch (Exception e)
                    {
                        HomoLogUtils.printStackTrace(logger, e);
                    }
                }
            });
            
            threads[i].start();
        }
        
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
    }

    private long getRecordNum(String templateId) throws Exception
    {
        long recordNum = 0;
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return recordNum;
        }
        
        List<VHmTableMetaField> columns = recordMap.get(templateId);
        if (CollectionUtils.isEmpty(columns))
        {
            return recordNum;
        }
        
        String tableName = columns.get(0).getTableName();
        Connection conn = QueryUtils.getMonitorDBConn();
        PreparedStatement pstmt = conn.prepareStatement("select count(*) from " + tableName + " t ");
        ResultSet rs = pstmt.executeQuery();
        if (rs.next())
        {
            recordNum = rs.getLong(1);
        }
        
        return recordNum;
    }

    private void doBusiness(String templateId, int pageNo, int perSize) 
        throws Exception
    {
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return;
        }
        
        List<VHmTableMetaField> columns = recordMap.get(templateId);
        if (CollectionUtils.isEmpty(columns))
        {
            return;
        }
        
        String pojoClass = columns.get(0).getPojoClass();
        String className = pojoClass.substring(pojoClass.lastIndexOf(Symbols.DOT) + 1);
        String tableName = columns.get(0).getTableName();
        
        long begin = (pageNo - 1) * perSize + 1;
        long end = pageNo * perSize;
        Connection conn = QueryUtils.getMonitorDBConn();
        PreparedStatement pstmt = conn.prepareStatement("select * from (select t.*, rowid as rid, rownum as rnum from " + tableName + " t order by rnum) where rnum between " + begin + " and " + end);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next())
        {
            Jedis jedisWriter = jedisWriterPool.getResource();
            
            String rowid = rs.getString("rid");
            String[] members = new String[columns.size()];
            
            //对于表的每条记录，对其按照列配置的columnIndex
            for (int i = 0; i < columns.size(); i++)
            {
                VHmTableMetaField column = columns.get(i);
                String columnValue = rs.getString(column.getColumnName());
                if (null == columnValue)
                {
                    columnValue = Symbols.NULL;
                }
                
                StringBuilder key = new StringBuilder(); 
                key.append(className).append(Symbols.UNDER_LINE)
                        .append(column.getPropertyName())
                        .append(Symbols.UNDER_LINE)
                        .append(columnValue);
                
                /** 
                 * sadd BpFundBasicInfo_fundCode_482002 rowid 
                 * 
                 * 特别地如果对应的column在数据库中为null, sadd BpFundBasicInfo_fundCode_NULL rowid,
                 * 如果对应的column在数据库中为空字符串, sadd BpFundBasicInfo_fundCode_  rowid
                 */
                jedisWriter.sadd(key.toString(), rowid);
                
                members[i] = columnValue;
            }
            
            jedisWriter.rpush(rowid, members);
            
            jedisWriterPool.returnResource(jedisWriter);
        }
    }
    
    /**
     * 刷新缓存.
     * @param tableName 表名.
     * @param fields 要增量刷新的记录集合.
     * @param isFull 是否全量更新.
     * @throws Exception 
     */
    public void refreshCache(String tableName, List<CacheField> fields, boolean isFull) throws Exception
    {
        String templateId = VHmTableMetaTable.getInstance().getTableTmpIdMap().get(tableName);
        
        //如果是全量更新动作.
        if (isFull)
        {
            refreshAllData(templateId);
        }
        //如果是增量更新.
        else
        {
            if (CollectionUtils.isEmpty(fields))
            {
                return;
            }
            
            Jedis jedisWriter = jedisWriterPool.getResource();

            DmlType opType = null;
            CacheField field = null;
            for (int i = 0; i < fields.size(); i++)
            {
                field = fields.get(i);
                opType = DmlType.get(field.getOpType());
                switch (opType)
                {
                    case INSERT:
                        String members = doInsert(templateId, jedisWriter, field);
                        recordInsertLog(templateId, field.getRowid(), members);//记录插入日志.
                        break;
                        
                    case UPDATE:
                        doUpdate(templateId, jedisWriter, field);
                        break;
    
                    case DELETE:
                        String oldMembers = doDelete(templateId, jedisWriter, field);
                        recordDeleteLog(templateId, field.getRowid(), oldMembers);
                        break;
                }
            }
            
            jedisWriterPool.returnResource(jedisWriter);
        }
    }

    private String doInsert(String templateId, Jedis jedisWriter, CacheField field) throws Exception
    {
        StringBuilder insertInfos = new StringBuilder();
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return insertInfos.toString();
        }
        
        List<VHmTableMetaField> columns = recordMap.get(templateId);
        if (CollectionUtils.isEmpty(columns))
        {
            return insertInfos.toString();
        }
        
        String pojoClass = columns.get(0).getPojoClass();
        String className = pojoClass.substring(pojoClass.lastIndexOf(Symbols.DOT) + 1);
        String rowid = field.getRowid();
        String[] members = new String[columns.size()];
        
        insertInfos.append("members is [ ");
        for (int i = 0; i < columns.size(); i++)
        {
            VHmTableMetaField column = columns.get(i);
            
            //得到值.
            String columnValue = getColumnValue(field, column.getColumnName());
            if (null == columnValue)
            {
                columnValue = Symbols.NULL;
            }
            
            StringBuilder key = new StringBuilder(); 
            key.append(className).append(Symbols.UNDER_LINE)
                    .append(column.getPropertyName())
                    .append(Symbols.UNDER_LINE)
                    .append(columnValue);
            
            jedisWriter.sadd(key.toString(), rowid);
            members[i] = columnValue;
            
            if (i == columns.size() - 1)
            {
                insertInfos.append(column.getColumnName()).append(Symbols.COLON).append(columnValue);
            }
            else
            {
                insertInfos.append(column.getColumnName()).append(Symbols.COLON).append(columnValue).append(Symbols.COMMA);
            }
        }
        
        jedisWriter.rpush(rowid, members);
        
        insertInfos.append("]");
        return insertInfos.toString();
    }

    private void doUpdate(String templateId, Jedis jedisWriter, CacheField field) throws Exception
    {
        String oldMembers = doDelete(templateId, jedisWriter, field);
        String members = doInsert(templateId, jedisWriter, field);
        
        //记录更新日志.
        recordUpdateLog(templateId, field.getRowid(), oldMembers, members);
    }

    /**
     * 删除redis缓存中的对应记录。返回老的记录信息.
     * @param templateId
     * @param jedisWriter
     * @param field
     * @return
     * @throws Exception
     */
    private String doDelete(String templateId, Jedis jedisWriter, CacheField field) throws Exception
    {
        StringBuilder oldMembers = new StringBuilder();
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return oldMembers.toString();
        }
        
        List<VHmTableMetaField> columns = recordMap.get(templateId);
        if (CollectionUtils.isEmpty(columns))
        {
            return oldMembers.toString();
        }
        
        String pojoClass = columns.get(0).getPojoClass();
        String className = pojoClass.substring(pojoClass.lastIndexOf(Symbols.DOT) + 1);
        String rowid = field.getRowid();
        
        oldMembers.append("members is [");
        for (int i = 0; i < columns.size(); i++)
        {
            VHmTableMetaField column = columns.get(i);
            String columnValue = getColumnValue(field, column.getColumnName());
            if (null == columnValue)
            {
                columnValue = Symbols.NULL;
            }
            
            StringBuilder key = new StringBuilder(); 
            key.append(className).append(Symbols.UNDER_LINE)
                    .append(column.getPropertyName())
                    .append(Symbols.UNDER_LINE)
                    .append(columnValue);
            
            jedisWriter.srem(key.toString(), rowid);
            
            if (i == columns.size() - 1)
            {
                oldMembers.append(column.getColumnName()).append(Symbols.COLON).append(columnValue);
            }
            else
            {
                oldMembers.append(column.getColumnName()).append(Symbols.COLON).append(columnValue).append(Symbols.COMMA);
            }
        }
        
        jedisWriter.del(rowid);
        
        oldMembers.append("]");
        return oldMembers.toString();
    }

    private String getColumnValue(CacheField field, String columnName)
    {
        if (null == field)
        {
            return null;
        }
        
        if (CollectionUtils.isEmpty(field.getColumnMap()))
        {
            return null;
        }
        
        Object value = field.getColumnMap().get(columnName);
        return null == value ? null : value.toString();
    }
    
    /**
     * 记录插入日志.
     * @param templateId templateId
     * @param rowid rowid
     * @param members members
     */
    private void recordInsertLog(String templateId, String rowid, String members)
    {
        StringBuilder message = new StringBuilder();
        message.append("Insert into redis, templateid is ").append(templateId)
               .append(",rowid is ").append(rowid)
               .append(",").append(members);
        logger.info(message);
    }
    
    /**
     * 记录更新日志
     * @param templateId templateId
     * @param rowid rowid
     * @param oldMembers 更新前记录中的各个成员.
     * @param members 更新进redis中的成员.
     */
    private void recordUpdateLog(String templateId, String rowid, String oldMembers, String members)
    {
        StringBuilder message = new StringBuilder();
        message.append("Update redis cache, templateId is ")
               .append(templateId)
               .append(",rowid is ").append(rowid)
               .append(",")
               .append("update members from ").append(oldMembers)
               .append(" to ")
               .append(members);
        logger.info(message);
    }


    private void recordDeleteLog(String templateId, String rowid, String oldMembers)
    {
        StringBuilder message = new StringBuilder();
        message.append("Insert into redis, templateId is ").append(templateId)
               .append(",rowid is ").append(rowid)
               .append(",").append(oldMembers);
        logger.info(message);
    }
}
