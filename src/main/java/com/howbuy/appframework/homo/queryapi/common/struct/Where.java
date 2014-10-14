package com.howbuy.appframework.homo.queryapi.common.struct;

@SuppressWarnings("serial")
public class Where implements java.io.Serializable
{
    private Operation operation;
    
    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }

    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        jsonstr.append("\"where\":")
               .append("{")
                   .append(null == operation ? "" : operation.toJsonString())
               .append("}");
        return jsonstr.toString();
    }
}
