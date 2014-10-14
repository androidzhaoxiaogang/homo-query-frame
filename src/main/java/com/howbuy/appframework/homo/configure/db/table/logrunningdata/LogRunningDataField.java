package com.howbuy.appframework.homo.configure.db.table.logrunningdata;

public class LogRunningDataField
{
    private long seqId;
    
    private String dmlToTimestamp;
    
    private String tableName;
    
    private String statementType;
    
    private String dataRowId;

    public long getSeqId()
    {
        return seqId;
    }

    public void setSeqId(long seqId)
    {
        this.seqId = seqId;
    }

    public String getDmlToTimestamp()
    {
        return dmlToTimestamp;
    }

    public void setDmlToTimestamp(String dmlToTimestamp)
    {
        this.dmlToTimestamp = dmlToTimestamp;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getStatementType()
    {
        return statementType;
    }

    public void setStatementType(String statementType)
    {
        this.statementType = statementType;
    }

    public String getDataRowId()
    {
        return dataRowId;
    }

    public void setDataRowId(String dataRowId)
    {
        this.dataRowId = dataRowId;
    }
}
