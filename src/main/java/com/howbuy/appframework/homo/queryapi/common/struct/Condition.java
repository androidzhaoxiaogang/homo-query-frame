package com.howbuy.appframework.homo.queryapi.common.struct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

@SuppressWarnings("serial")
public class Condition implements java.io.Serializable
{
    private String command;
    
    private List<Case> caseList;
    
    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public List<Case> getCaseList()
    {
        return caseList;
    }

    public void addCase(Case cs)
    {
        List<Case> temp = new ArrayList<Case>();
        if (!CollectionUtils.isEmpty(caseList))
        {
            temp.addAll(caseList);
        }
        temp.add(cs);
        
        caseList = temp;
    }
    
    public void addCase(List<Case> cs)
    {
        List<Case> temp = new ArrayList<Case>();
        if (!CollectionUtils.isEmpty(caseList))
        {
            temp.addAll(caseList);
        }
        temp.addAll(cs);
        
        caseList = temp;
    }

    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        jsonstr.append("\"condition\":")
               .append("{")
                   .append("\"-command\":").append("\"").append(command).append("\"").append(CollectionUtils.isEmpty(caseList) ? "" : ",")
                   .append(contructCaseList())
               .append("}");
        return jsonstr.toString();
    }

    private String contructCaseList()
    {
        StringBuilder builder = new StringBuilder();
        if (null == this)
        {
            return builder.toString();
        }
        
        if (CollectionUtils.isEmpty(caseList))
        {
            return builder.toString();
        }
        
        builder.append("\"case\":")
               .append("[");
        
        int size = caseList.size();
        for (int i = 0; i < size; i++)
        {
            builder.append("{")
                   .append(caseList.get(i).toJsonString())
                   .append("}");
            //如果不是最后一个元素..
            if (i != size - 1)
            {
                builder.append(",");
            }
        }
        
        builder.append("]");
        return builder.toString();
    }
}
