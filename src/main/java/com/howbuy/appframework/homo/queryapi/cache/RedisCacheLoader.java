package com.howbuy.appframework.homo.queryapi.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.howbuy.appframework.homo.configure.db.table.logrunningdata.LogRunningDataField;
import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.common.HomoConstants;
import com.howbuy.appframework.homo.queryapi.common.utils.HomoLogUtils;
import com.howbuy.appframework.homo.queryapi.db.QueryService;

/**
 * 一个部署环境上应该只有一个对应的redisCacheLoader，也就是说
 * 这个东东实际上可以看成是单例模式.事实上，在spring配置文件中
 * 配置它时就使用的是默认的配置，即是单例模式.
 * 
 * 但是，关于这一点我们就不在代码中再显式使用单例模式了,了解了这一点就好.
 * @author li.zhang
 *
 */
public class RedisCacheLoader implements CacheLoader
{
    /** 是否已经被初始化. **/
    private boolean inited;
    
    private QueryService queryService;
    
    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * 数据加载进缓存.
     */
    public void load2cache()
    {
        if (!inited)
        {
            logger.info("Init redis cache begin...................");
            
            //初始化监听器, 增量更新
            initMonitor();
            
            inited = true;
            
            logger.info("Init redis cache end...................");
        }
    }

    private void initMonitor()
    {
        //TODO li.zhang 线程应该统一管理，这种方式不好，待优化.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new RedisDataMonitor());
        executor.shutdown();
    }
    
    public QueryService getQueryService()
    {
        return queryService;
    }

    public void setQueryService(QueryService queryService)
    {
        this.queryService = queryService;
    }
    
    public boolean isInited()
    {
        return inited;
    }
    
    /**
     * 监听器，用于数据库有更新时，通过扫描审计视图得到相关信息，增量更新对应的缓存.
     * @author li.zhang
     *
     */
    class RedisDataMonitor implements Runnable
    {
        @Override
        public void run()
        {
            logger.info("Start RedisDataMonitor thread...");
            
            Connection conn = null;
            while (true)
            {
                /**
                 *  注意:
                 *     下面的conn连接不要随意关掉.这一个conn来组织管理下面一系列操作的事务的.
                 */
                try
                {                    
                    Thread.sleep(3000);
                    
                    conn = QueryUtils.getHomoConnection();
                    conn.setAutoCommit(false);
                    long beginSeqId = getLastSeqId(conn);
                    
                    logger.info("increment refresh cache from seq_id : [" + beginSeqId + "]");
                    
                    //这个查询时在监控的库中查询的.
                    List<LogRunningDataField> records = queryLogDataRunning(beginSeqId);
                    
                    if (CollectionUtils.isEmpty(records))
                    {
                        continue;
                    }
                    else
                    {
                        long endSeqId = records.get(records.size() - 1).getSeqId();
                        
                        Map<String, List<CacheField>> maps = new HashMap<String, List<CacheField>>();
                        
                        //预处理查询结果集
                        process(records, maps);
                        
                        //分发到对应表的缓存处理器.
                        dispatch(maps, beginSeqId, endSeqId);
                        
                        //更新seq_id.
                        updateSeqId(conn, endSeqId);
                        
                        logger.info("increment refresh cache to seq_id : [" + endSeqId + "]");
                        
                        conn.commit();
                    }
                }
                catch (Exception e)
                {
                    onError(conn, e);
                }
                finally
                {
                    onFinally(conn);
                }
            }
        }

        /**
         * 注意: 这个方法内部不要关闭conn.因为整个事务是一个conn贯穿的.如果关掉conn，就是一个陷阱.
         * @param conn
         * @return
         * @throws SQLException
         */
        private long getLastSeqId(Connection conn) throws SQLException
        {
            long seqId = 0;
            PreparedStatement pstmt = conn.prepareStatement("select * from hm_fincache_heartbeat_status");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                String lastSeqId = rs.getString("last_seq_id");
                if (!StringUtils.isEmpty(lastSeqId))
                {
                    seqId = Long.valueOf(lastSeqId);
                }
            }
            
            return seqId;
        }
        
        /**
         * 查询监控库的log_data_running表.
         * @param seqId
         * @return
         */
        private List<LogRunningDataField> queryLogDataRunning(long seqId)
        {
            List<LogRunningDataField> records = null;
            ResultSet rs = null;
            Connection conn = null;
            try
            {
                conn = QueryUtils.getMonitorDBConn();
                
                //每次增量处理500条, 这个是做系统保护的.如果一下子量比较大,redis连接池中数目不够很容易出异常.
                PreparedStatement pstmt = createStatement(conn, seqId, HomoConstants.INCREMENT_STEP);
                rs = pstmt.executeQuery();
                records = new ArrayList<LogRunningDataField>();
                while (rs.next())
                {
                    LogRunningDataField record = new LogRunningDataField();
                    record.setSeqId(rs.getLong("seq_id"));
                    record.setDmlToTimestamp(rs.getString("dml_to_timestamp"));
                    record.setTableName(rs.getString("table_name"));
                    record.setStatementType(rs.getString("statement_type"));
                    record.setDataRowId(rs.getString("data_rowid"));
                    
                    records.add(record);
                }
            }
            catch (Exception e)
            {
                HomoLogUtils.printStackTrace(logger, e);
            }
            finally
            {
                onFinally(conn);
            }
           
            return records;
        }

        /**
         * 创建查询语句.
         * @param conn 数据库连接.
         * @return
         * @throws SQLException
         */
        private PreparedStatement createStatement(Connection conn, long seqId, int perSize) throws SQLException
        {
            PreparedStatement pstmt = null;
            
            //需要按照seq_id排序，防止对同一条记录先修改再删除，保证刷新内存操作的序列.
            String sql = "select * from LOG_RUNNING_DATA where seq_id > ? and seq_id <= ? order by seq_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, seqId);
            pstmt.setLong(2, seqId + perSize);
            return pstmt;
        }
        
        /**
         * 预处理结果集.这里我们按照表名将对应的结果集分组组织在一起.
         * @param records 查询结果集
         * @throws SQLException
         */
        private void process(List<LogRunningDataField> records, Map<String, List<CacheField>> maps) throws Exception
        {
            for (int i = 0; i < records.size(); i++)
            {
                LogRunningDataField record = records.get(i);
                String tableName = record.getTableName();
                String opType = record.getStatementType();
                String rowid = record.getDataRowId();
                
                List<CacheField> fields = maps.get(tableName);
                if (null == fields)
                {
                    fields = new ArrayList<CacheField>();
                    maps.put(tableName, fields);
                }
                
                CacheField field = new CacheField();
                field.setOpType(opType);
                field.setRowid(rowid);
                fields.add(field);
            }
        }

        /**
         * 更新seq_id, 注意不要在这里关闭conn, jdbc一个conn默认来管理一个事务, conn.close()或者conn.commit()方法意味着一个事务的结束. 
         * 
         * 如果在这里做了这个动作，就埋下了一个陷阱，注意.
         * @param conn
         * @param seqId
         * @throws SQLException
         */
        private void updateSeqId(Connection conn, long seqId) throws SQLException
        {
            PreparedStatement pstmt = conn.prepareStatement("update hm_fincache_heartbeat_status set last_seq_id = ?");
            pstmt.setLong(1, seqId);
            pstmt.executeUpdate();
        }

        /**
         * 根据表名分发到对应的表的缓存处理器处理自身记录的增量更新.
         * @param maps key为tableName, value为需要刷新到缓存中对应的增量更新的记录集.
         * @param beginSeqId log_running_data开始的seq_id
         * @param endSeqId long_running_data结束的seq_id. 注意log_running_data的data_rowid字段各个记录可能相同.
         * @throws Exception
         */
        private void dispatch(Map<String, List<CacheField>> maps, long beginSeqId, long endSeqId) 
            throws Exception
        {
            if (CollectionUtils.isEmpty(maps))
            {
               return;
            }
            
            Iterator<String> keys = maps.keySet().iterator();
            while (keys.hasNext())
            {
                String tableName = keys.next();
                CacheDispatcher dipatcher = CacheManager.getInstance().getCacheDispatcher();
                dipatcher.refreshCache(tableName, maps.get(tableName), beginSeqId, endSeqId);
            }
        }
        
        private void onError(Connection conn, Exception e)
        {
            HomoLogUtils.printStackTrace(logger, e);
            
            try
            {
                if (null != conn)
                {
                    conn.rollback();
                }
            }
            catch (SQLException e1)
            {
                HomoLogUtils.printStackTrace(logger, e1);
            }
            
            //TODO 短信告警.
            alarm(e);
        }
        
        private void onFinally(Connection conn)
        {
            if (null != conn)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException e)
                {
                    HomoLogUtils.printStackTrace(logger, e);
                }
            }
        }
        
        /**
         * 告警.
         * @param e 异常信息
         */
        private void alarm(Exception e)
        {
            //TODO li.zhang
        }
    }
}
