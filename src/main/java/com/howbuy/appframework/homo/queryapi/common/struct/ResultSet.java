package com.howbuy.appframework.homo.queryapi.common.struct;

/**
 * 结果集.
 * 这个结果集可能是一个操作operation对应返回的结果集，也可能是一个条件condition对应的结果集，二者
 * 
 * 取其一.这是我们的设定。
 * @author li.zhang
 *
 */
@SuppressWarnings("serial")
public class ResultSet implements java.io.Serializable
{
    private Operation operation;
    
    private Condition condition;

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
    }
    
    public Condition getCondition()
    {
        return condition;
    }

    public void setCondition(Condition condition)
    {
        this.condition = condition;
    }

    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        if (null != operation)
        {
            jsonstr.append(operation.toJsonString());
        }
        //我们的设定是operation和condition在一个ResultSet对象中不可能都不为null,即二者至少有一个为null.这是我们的设定.
        else if (null != condition)
        {
            jsonstr.append(condition.toJsonString());
        }
        
        return jsonstr.toString();
    }
}
