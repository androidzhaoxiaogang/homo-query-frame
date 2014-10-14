package com.howbuy.appframework.homo.configure.db.table.vhmtablemeta;


/**
 * VHmTableMeta视图中的一条记录对应的数据封装.
 * @author li.zhang
 *
 */
public class VHmTableMetaField
{
    /** 模板Id **/
    private String templateId;
    
    /** 实体类，形如com.howbuy.**.BpFundBasicInfo. **/
    private String pojoClass;
    
    /** 表代码 **/
    private String tableCode;
    
    /** 表名称. **/
    private String tableName;
    
    /** 列代码. **/
    private String columnId;
    
    /** 列在表中的位置. 最小从1开始. **/
    private String columnIndex;
    
    /** 列名. **/
    private String columnName;
    
    /** 表对应的实体bean中列名为columnName对应的属性名. **/
    private String propertyName;
    
    /** 数据源id **/
    private String dsId;
    
    /** 数据库驱动. **/
    private String drivers;
    
    /** 数据库连接url. **/
    private String url;
    
    /** 数据库用户名.**/
    private String userName;
    
    /** 数据库密码. **/
    private String passwd;
    
    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getPojoClass()
    {
        return pojoClass;
    }

    public void setPojoClass(String pojoClass)
    {
        this.pojoClass = pojoClass;
    }

    public String getTableCode()
    {
        return tableCode;
    }

    public void setTableCode(String tableCode)
    {
        this.tableCode = tableCode;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

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

    public String getDsId()
    {
        return dsId;
    }

    public void setDsId(String dsId)
    {
        this.dsId = dsId;
    }

    public String getDrivers()
    {
        return drivers;
    }

    public void setDrivers(String drivers)
    {
        this.drivers = drivers;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPasswd()
    {
        return passwd;
    }

    public void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }
}
