package com.howbuy.appframework.homo.queryapi.common.struct;

@SuppressWarnings("serial")
public class Case implements java.io.Serializable
{
    private String columeName;
    
    private String columeValue;
    
    private String expressOp;
    
    public Case()
    {
        
    }
    
    public Case(String colName, String colVal, String exprOp)
    {
        this.columeName = colName;
        this.columeValue = colVal;
        this.expressOp = exprOp;
    }
    
    public String getColumeName()
    {
        return columeName;
    }

    public void setColumeName(String columeName)
    {
        this.columeName = columeName;
    }

    public String getColumeValue()
    {
        return columeValue;
    }

    public void setColumeValue(String columeValue)
    {
        this.columeValue = columeValue;
    }


    public String getExpressOp()
    {
        return expressOp;
    }


    public void setExpressOp(String expressOp)
    {
        this.expressOp = expressOp;
    }


    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        jsonstr.append("\"-colume-name\":").append("\"").append(columeName).append("\"").append(",")
               .append("\"-colume_value\":").append("\"").append(columeValue).append("\"").append(",")
               .append("\"-express_op\":").append("\"").append(expressOp).append("\"");
        
        return jsonstr.toString();
    }
}
