package com.howbuy.appframework.homo.queryapi;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.howbuy.appframework.homo.configure.commons.container.Container;
import com.howbuy.appframework.homo.configure.commons.container.ContainerBootStrap;
import com.howbuy.appframework.homo.configure.commons.container.ContainerFactory;
import com.howbuy.appframework.homo.configure.commons.redis.ModeType;
import com.howbuy.appframework.homo.configure.db.table.vhmtablemeta.VHmTableMetaField;
import com.howbuy.appframework.homo.configure.db.table.vhmtablemeta.VHmTableMetaTable;
import com.howbuy.appframework.homo.fincache.redis.cluster.RedisServManager;
import com.howbuy.appframework.homo.queryapi.common.HomoProperty;
import com.howbuy.appframework.homo.queryapi.common.Symbols;
import com.howbuy.appframework.homo.queryapi.common.struct.Column;

public final class QueryUtils
{
    /** 日志. **/
    private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
    
    /**
     * 
     * @param templateId
     * @param columeName
     * @param columeValue
     * @return
     */
    public static List<String> queryUniqueIdsFromRedis(String templateId, String columeName, String columeValue)
    {
        List<String> uniqueIds = null;
        JedisPool jedisPool = RedisServManager.getInstance().getJedisClientProxy(ModeType.READ_ONLY.getStrValue());
        Jedis conn = jedisPool.getResource();

        List<VHmTableMetaField> view = queryVHmTableMetaByTempId(templateId);
        if (CollectionUtils.isEmpty(view))
        {
            LOGGER.error("The template no configed, templateId is " + templateId);
            return uniqueIds;
        }
        
        //构造key
        String pojoClass = view.get(0).getPojoClass();
        String className = pojoClass.substring(pojoClass.lastIndexOf(Symbols.DOT) + 1);
        StringBuilder key = new StringBuilder();
        key.append(className).append(Symbols.UNDER_LINE).append(columeName)
           .append(Symbols.UNDER_LINE).append(columeValue);

        Set<String> members = conn.smembers(key.toString());
        jedisPool.returnResource(conn);//释放连接
        
        //记录日志.
        recordLog(templateId, key, members);
        
        if (CollectionUtils.isEmpty(members))
        {
            return uniqueIds;
        }

        uniqueIds = new ArrayList<String>();
        Iterator<String> iterator = members.iterator();
        while (iterator.hasNext())
        {
            uniqueIds.add(iterator.next());
        }

        return uniqueIds;
    }

    private static void recordLog(String templateId, StringBuilder key, Set<String> members)
    {
        StringBuilder message = new StringBuilder();
        message.append("query redis, templateId is ").append(templateId)
               .append(", key is ").append(key.toString())
               .append(", members is [");
        
        if (!CollectionUtils.isEmpty(members))
        {
            int i = 0;
            Iterator<String> iterator = members.iterator();
            while (iterator.hasNext())
            {
                if (i == members.size() - 1)
                {
                    message.append(iterator.next());
                }
                else
                {
                    message.append(iterator.next()).append(Symbols.COMMA);
                }
                
                i++;
            }
        }
        
        message.append("]");
    }

    public static List<VHmTableMetaField> queryVHmTableMetaByTempId(String templateId)
    {
        List<VHmTableMetaField> records = null;
        Map<String, List<VHmTableMetaField>> recordMap = VHmTableMetaTable.getInstance().getRecordMaps();
        if (CollectionUtils.isEmpty(recordMap))
        {
            return records;
        }
        
        records = recordMap.get(templateId);
        return records;
    }

    /**
     * 将各个记录归并在一起.
     * @param uniqueIds 记录的uniqueId组成的集合，可以简单的认为是rowid组成的集合.
     */
    public static JsonQueryResp archiveRecords(List<String> uniqueIds, String templateId)
    {
        JsonQueryResp queryResp = new JsonQueryResp();
        queryResp.setTemplateId(templateId);
        List<VHmTableMetaField> views = queryVHmTableMetaByTempId(templateId);
        if (CollectionUtils.isEmpty(views))
        {
            return queryResp;
        }
        
        //记录日志.
        recordUniqueIds(uniqueIds);
        
        //一个templateId对应一个表的配置，所以pojoClass是唯一的.
        String pojoClass = views.get(0).getPojoClass();
        queryResp.setPojoClass(pojoClass);
        
        Map<String, List<Column>> columnMaps = new HashMap<String, List<Column>>();
        for (int i = 0; i < views.size(); i++)
        {
            VHmTableMetaField view = views.get(i);
            if (!pojoClass.equalsIgnoreCase(view.getPojoClass()))
            {
                //这种情况是不允许出现的,这里是处于代码的严谨加上的.
                continue;
            }
            
            List<Column> columns = columnMaps.get(pojoClass);
            if (null == columns)
            {
                columns = new ArrayList<Column>();
                //一个templateId对应一个pojoClass,所以这里map的key只有一个pojoClass.
                columnMaps.put(pojoClass, columns);
            }
            
            Column column = new Column();
            column.setColumnId(view.getColumnId());
            column.setColumnIndex(view.getColumnIndex());
            column.setColumnName(view.getColumnName());
            column.setPropertyName(view.getPropertyName());
            
            columns.add(column);    
        }
       
        String key = null;
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        JedisPool jedisPool = RedisServManager.getInstance().getJedisClientProxy(ModeType.READ_ONLY.getStrValue());
        Jedis conn = jedisPool.getResource();
        
        for (int i = 0; i < uniqueIds.size(); i++)
        {
            Map<String, String> record = new HashMap<String, String>();

            key = uniqueIds.get(i);
            List<String> members = conn.lrange(key, 0, -1);
            
            writeRecordLog(key, members);
            if (CollectionUtils.isEmpty(members))
            {
                continue;
            }
           
            for (int columnIndex = 0; columnIndex < members.size(); columnIndex++)
            {
                //column肯定不为null.
                Column column = getColumnInfo(pojoClass, columnMaps, columnIndex);
                
                String columnValue = members.get(columnIndex);
                
                /**
                 * 对外的时候，columnValue如果为Symbols.NULL说明这个属性是真的null(注:会空指针的那个null,不是字符串.)
                 */
                if (Symbols.NULL.equals(columnValue) || StringUtils.isEmpty(columnValue))
                {
                    columnValue = Symbols.EMPTY;
                }
                record.put(column.getPropertyName(), columnValue);
            }

            records.add(record);
        }

        jedisPool.returnResource(conn);//释放连接
        
        queryResp.setRecords(records);
        
        return queryResp;
    }

    private static void recordUniqueIds(List<String> uniqueIds)
    {
        StringBuilder message = new StringBuilder();
        message.append("Returned rowid set is : [");
        for (int i = 0; i < uniqueIds.size(); i++)
        {
            if (i == uniqueIds.size() - 1)
            {
                message.append(uniqueIds.get(i));
            }
            else
            {
                message.append(uniqueIds.get(i)).append(",");
            }
        }
        
        LOGGER.info(message);
    }
    
    private static void writeRecordLog(String rowid, List<String> members)
    {
        StringBuilder message = new StringBuilder();
        message.append("Rowid is ").append(rowid)
               .append(",members is [ ");
        if (!CollectionUtils.isEmpty(members))
        {
            for (int i = 0; i < members.size(); i++)
            {
                if (i == members.size() - 1)
                {
                    message.append(members.get(i));
                }
                else
                {
                    message.append(members.get(i)).append(Symbols.COMMA);
                }
            }
        }
        
        message.append("]");
        
        LOGGER.info(message);
    }

    /**
     * 这边目前获取数据库连接的方式不太好，应该在系统启动的时候，初始化连接...这里待优化.
     * 如果初次以这种方式获取连接的代价是0.5秒左右.
     * 
     * 获取homo配置库的连接.
     * @return
     * @throws SQLException
     */
    public static Connection getHomoConnection() throws SQLException
    {
        Connection conn = null;
        Container container = ContainerFactory.getContainer();
        DataSource datasource = container.getDataSource("dataSource");
        conn = datasource.getConnection();
        return conn;
        
    }
    
    /**
     * 得到缓存监控的db的数据库连接，即得到要缓存到redis中的库的数据库连接.
     * @return
     * @throws SQLException
     */
    public static Connection getMonitorDBConn() throws Exception
    {
        Connection conn = null;
        Properties props = new Properties();
        InputStream conf = ContainerBootStrap.class.getClassLoader().getResourceAsStream("conf.properties");
        props.load(conf);
        
        String driver = System.getProperty(HomoProperty.MONITOR_DB_DRIVER);
        String url = System.getProperty(HomoProperty.MONITOR_DB_URL);
        String username = System.getProperty(HomoProperty.MONITOR_DB_USERNAME);
        String passwd = System.getProperty(HomoProperty.MONITOR_DB_PASSWORD);
        
        Class.forName(driver);
        conn = DriverManager.getConnection(url, username, passwd);
       
        return conn;
    }

    private static Column getColumnInfo(String pojoClass, Map<String, List<Column>> columnMaps, int columnIndex)
    {
        //这里得到的columnList是已经按照tableCode和columnIndex升序拍好序的..
        List<Column> columnList = columnMaps.get(pojoClass);
        return columnList.get(columnIndex);
    }
}
