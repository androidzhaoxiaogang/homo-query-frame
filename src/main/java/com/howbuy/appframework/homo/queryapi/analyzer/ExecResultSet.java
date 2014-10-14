package com.howbuy.appframework.homo.queryapi.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * 结果集
 * @author li.zhang
 *
 */
public class ExecResultSet
{
    /** resultSet集合中封装的是逻辑上的记录唯一标识uniqueId,可以简单的认为是rowId的集合. **/
    private List<String> resultSet = new ArrayList<String>();

    /**
     * 添加元素.
     * @param member
     */
    public void add(String member)
    {
        List<String> temp = new ArrayList<String>();
        if (!CollectionUtils.isEmpty(resultSet))
        {
            temp.addAll(resultSet);
        }
        
        if (!temp.contains(member))
        {
            temp.add(member);
        }
        
        resultSet = temp;
    }
    
    public void add(List<String> members)
    {
        List<String> temp = new ArrayList<String>();
        if (!CollectionUtils.isEmpty(resultSet))
        {
            temp.addAll(resultSet);
        }
        
        if (!CollectionUtils.isEmpty(members))
        {
            for (String member : members)
            {
                if (!temp.contains(member))
                {
                    temp.add(member);
                }
            }
        }
        
        resultSet = temp;
    }
    
    public List<String> getResultSet()
    {
        return resultSet;
    }
}
