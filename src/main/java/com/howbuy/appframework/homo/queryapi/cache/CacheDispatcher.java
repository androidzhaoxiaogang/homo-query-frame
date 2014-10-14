package com.howbuy.appframework.homo.queryapi.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.parser.redis.RCCommander;

public class CacheDispatcher
{
    /** redis缓冲处理指挥官. **/
    private RCCommander cacheCommander;
    
    /**
     * 构造方法
     * @param commander
     */
    protected CacheDispatcher(RCCommander commander)
    {
        this.cacheCommander = commander;
    }
    
    /**
     * 根据fields更新table对应的cache.
     * @param incrFields incrFields是已经按照seq_no排好序的待增量更新的记录列表.
     * @throws Exception 
     */
    public void refreshCache(String tableName, List<CacheField> incrFields, long beginSeqId, long endSeqId) throws Exception
    {
        String sql = constructSql(tableName, incrFields, beginSeqId, endSeqId);
        if (StringUtils.isEmpty(sql))
        {
            return;
        }
        
        List<CacheField> fields = executeQuery(tableName, sql);
        
        cacheCommander.refreshCache(tableName, fields, false);
    }

    private String constructSql(String tableName, List<CacheField> fields, long beginSeqId, long endSeqId)
    {
        StringBuilder sql = new StringBuilder();
        if (CollectionUtils.isEmpty(fields))
        {
            return sql.toString();
        }
        
        sql.append("select t1.*, t1.rowid, t2.* from ").append(tableName).append(" t1, ")
               .append("(select * from log_running_data where seq_id > ")
                   .append(beginSeqId)
                   .append(" and seq_id <= ")
                   .append(endSeqId)
                   .append(") t2")
               .append(" where t1.rowid=t2.data_rowid and t1.rowid in ( ");
        int size = fields.size();
        for (int i = 0; i < size; i++)
        {
            CacheField field = fields.get(i);
            if (i == size - 1)
            {
                sql.append("'").append(field.getRowid()).append("'");
            }
            else
            {
                sql.append("'").append(field.getRowid()).append("'").append(",");
            }
        }
        sql.append(" ) order by t2.seq_id ");
        
        return sql.toString();
    }
    
    private List<CacheField> executeQuery(String tableName, String sql) throws Exception
    {
        //key为rowid, value为rowId对应的记录的信息.
        List<CacheField> fields = new ArrayList<CacheField>();
        Connection conn = QueryUtils.getMonitorDBConn();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        List<String> tableColumns = getColumnNames(tableName);
        while (rs.next())
        {
            CacheField field = new CacheField();
            field.setRowid(rs.getString("rowid"));
            field.setOpType(rs.getString("statement_type"));
            for (int i = 0 ; i < tableColumns.size(); i++)
            {
                String columnName = tableColumns.get(i);
                Object columnValue =  rs.getObject(tableColumns.get(i));
                
                field.getColumnMap().put(columnName, columnValue);
            }
            
            fields.add(field);
        }
        
        return fields;
    }

    /**
     * 根据表名获得该表的列名列表.
     * @param tableName 表名
     * @return
     * @throws SQLException
     */
    private List<String> getColumnNames(String tableName) throws Exception
    {
        List<String> columnNames = new ArrayList<String>();
        
        String sql = "select * from user_tab_columns where table_name=? order by column_id";
        Connection conn = QueryUtils.getMonitorDBConn();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, tableName);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next())
        {
            String columnName = rs.getString("column_name");
            columnNames.add(columnName);
        }
        
        return columnNames;
    }
}
