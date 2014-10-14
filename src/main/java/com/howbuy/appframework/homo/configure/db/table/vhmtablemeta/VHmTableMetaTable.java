package com.howbuy.appframework.homo.configure.db.table.vhmtablemeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.howbuy.appframework.homo.queryapi.QueryUtils;

/**
 * VHmTableMeta视图对应的数据封装.
 * @author li.zhang
 *
 * 
 */
//TODO 待优化，这个地方是应该有一个类似AbstractTable的类作为父类的，来统一加载table配置.先简单来.
public class VHmTableMetaTable
{
    private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
    
    /** 单例. **/
    private static final VHmTableMetaTable INSTANCE = new VHmTableMetaTable();
    
    /** key为templateId, value为对应的记录集的集合.**/
    private Map<String, List<VHmTableMetaField>> recordMaps = new HashMap<String, List<VHmTableMetaField>>();
    
    /** key为tablename,value为templateid. **/
    private Map<String, String> tableTmpIdMap = new HashMap<String, String>();
    
    /** key为templateid,value为tablename. **/
    private Map<String, String> tmpIdTableMap = new HashMap<String, String>();
    
    /**
     * 私有构造方法
     */
    private VHmTableMetaTable()
    {
        loadAllRecord();
    }
    
    /**
     * 单例模式
     * @return
     */
    public static VHmTableMetaTable getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 刷新v_hm_table_meta视图,暴露给外部工具用来刷新内存数据的.
     */
    public void refresh()
    {
        loadAllRecord();
    }
    
    /**
     * 加载该表中所有记录.
     */
    private void loadAllRecord()
    {
        Connection conn = null;
        List<VHmTableMetaField> rsGroup = null;
        Map<String, List<VHmTableMetaField>> rsMaps = new HashMap<String, List<VHmTableMetaField>>();
        Map<String, String> tableTmpMap = new HashMap<String, String>();
        Map<String, String> tmpTableMap = new HashMap<String, String>();
        try
        {
            conn = QueryUtils.getHomoConnection();
            PreparedStatement pstmt = conn.prepareStatement("select * from v_hm_table_meta");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                VHmTableMetaField record = new VHmTableMetaField();
                String templeteId = rs.getString("templateId");
                String tableName = rs.getString("tableName");
                tableTmpMap.put(tableName, templeteId);
                tmpTableMap.put(templeteId, tableName);
                
                rsGroup = rsMaps.get(templeteId);
                if (CollectionUtils.isEmpty(rsGroup))
                {
                    rsGroup = new ArrayList<VHmTableMetaField>();
                    rsMaps.put(templeteId, rsGroup);
                }
                
                record.setTemplateId(templeteId);
                record.setPojoClass(rs.getString("pojoClass"));
                record.setTableCode(rs.getString("tableCode"));
                record.setTableName(tableName);
                record.setColumnId(rs.getString("columnId"));
                record.setColumnIndex(rs.getString("columnIndex"));
                record.setColumnName(rs.getString("columnName"));
                record.setPropertyName(rs.getString("propertyName"));
                record.setDsId(rs.getString("dsId"));
                record.setDrivers(rs.getString("drivers"));
                record.setUrl(rs.getString("url"));
                record.setUserName(rs.getString("username"));
                record.setPasswd(rs.getString("passwd"));
                
                rsGroup.add(record);
            }
            
            recordMaps = rsMaps;
            tableTmpIdMap = tableTmpMap;
            tmpIdTableMap = tmpTableMap;
        }
        catch (SQLException e)
        {
            LOGGER.error(e);
        }
        finally
        {
            try
            {
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                LOGGER.error(e);
            }
        }
    }

    /**
     * 返回一个只读的map.
     * @return
     */
    public Map<String, List<VHmTableMetaField>> getRecordMaps()
    {
        return Collections.unmodifiableMap(recordMaps);
    }

    public Map<String, String> getTableTmpIdMap()
    {
        return Collections.unmodifiableMap(tableTmpIdMap);
    }

    public Map<String, String> getTmpIdTableMap()
    {
        return Collections.unmodifiableMap(tmpIdTableMap);
    }
}
