package com.howbuy.appframework.homo.queryapi.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装缓存要更新的一条记录对应的信息.
 * @author li.zhang
 *
 */
public class CacheField
{
    /** rowId **/
    private String rowid;
    
    /** opType: insert,update,delete的枚举值. **/
    private String opType;
    
    /** key为表的column_name, value为表的column_value. **/
    private Map<String, Object> columnMap = new HashMap<String, Object>();
    
    public String getRowid()
    {
        return rowid;
    }
    
    public void setRowid(String rowid)
    {
        this.rowid = rowid;
    }
    
    public String getOpType()
    {
        return opType;
    }
    
    public void setOpType(String opType)
    {
        this.opType = opType;
    }
    
    public Map<String, Object> getColumnMap()
    {
        return columnMap;
    }
}
