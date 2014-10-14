package com.howbuy.appframework.homo.queryapi.common.struct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

@SuppressWarnings("serial")
public class Operation implements java.io.Serializable
{
    private String command;
    
    private List<ResultSet> resultset;
    
    
    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public List<ResultSet> getResultset()
    {
        return resultset;
    }

    public void addResultset(ResultSet rs)
    {
        List<ResultSet> temp = new ArrayList<ResultSet>();
        if (!CollectionUtils.isEmpty(resultset))
        {
            temp.addAll(resultset);
        }
        
        temp.add(rs);
        this.resultset = temp;
    }
    
    public void addResultset(List<ResultSet> rs)
    {
        List<ResultSet> temp = new ArrayList<ResultSet>();
        if (!CollectionUtils.isEmpty(resultset))
        {
            temp.addAll(resultset);
        }
        
        temp.addAll(rs);
        this.resultset = temp;
    }

    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        jsonstr.append("\"operation\":")
               .append("{")
                   .append("\"-command\":").append("\"").append(command).append("\"").append(CollectionUtils.isEmpty(resultset) ? "" : ",")
                   .append(contructResultset())
               .append("}");
        return jsonstr.toString();
    }
    
    private String contructResultset()
    {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(resultset))
        {
            return builder.toString();
        }
        
        builder.append("\"resultset\":")
               .append("[");
        int size = resultset.size();
        for (int i = 0; i < resultset.size(); i++)
        {
            ResultSet rs = resultset.get(i);
            builder.append("{")
                   .append(rs.toJsonString())
                   .append("}");
            
            //如果不是最后一个元素,最后一个元素不需要","号.
            if (i != size - 1)
            {
                builder.append(",");
            }
        }
        
        builder.append("]");
        return builder.toString();
    }
}
