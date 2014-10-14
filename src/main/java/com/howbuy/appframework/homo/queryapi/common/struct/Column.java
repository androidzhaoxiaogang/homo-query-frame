package com.howbuy.appframework.homo.queryapi.common.struct;

/**
 * 封装列信息.
 * @author li.zhang
 *
 */
public class Column
{
    /** columnId, 对应HM_TABLE_COLUMN表中的columnId字段. **/
    private String columnId;
    
    /** 表的列名的位置.最小从1开始 **/
    private String columnIndex;
    
    /** 表的列表. **/
    private String columnName;
    
    /** 表列对应的实体类中的成员变量. **/
    private String propertyName;

    public String getColumnId()
    {
        return columnId;
    }

    public void setColumnId(String columnId)
    {
        this.columnId = columnId;
    }

    public String getColumnIndex()
    {
        return columnIndex;
    }

    public void setColumnIndex(String columnIndex)
    {
        this.columnIndex = columnIndex;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }
}
