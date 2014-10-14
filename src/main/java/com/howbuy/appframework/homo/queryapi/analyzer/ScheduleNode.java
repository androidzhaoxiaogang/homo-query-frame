package com.howbuy.appframework.homo.queryapi.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

/**
 * 执行计划树中的一个节点.
 * @author li.zhang
 *
 */
public class ScheduleNode
{
    /** templateId, 对应json请求中的template-id. **/
    private String templateId;
    
    /** 命令， 例如: and, or **/
    private String command;
    
    private Map<String, String> conditionMap;
    
    /** 子树节点 **/
    private List<ScheduleNode> children;
    
    /**
     * 添加子节点.
     * @param child 子节点
     */
    public void addChild(ScheduleNode child)
    {
        List<ScheduleNode> temp = new ArrayList<ScheduleNode>();
        if (!CollectionUtils.isEmpty(children))
        {
            temp.addAll(children);
        }
        temp.add(child);
        
        children = temp;
    }
    
    /**
     * 添加子节点
     * @param childs 子节点集合
     */
    public void addChild(Collection<ScheduleNode> childs)
    {
        List<ScheduleNode> temp = new ArrayList<ScheduleNode>();
        if (!CollectionUtils.isEmpty(children))
        {
            temp.addAll(children);
        }
        temp.addAll(childs);
        
        children = temp;
    }
    
    public void put(String key, String value)
    {
        Map<String, String> temp = new HashMap<String, String>();
        if (!CollectionUtils.isEmpty(this.conditionMap))
        {
            temp.putAll(this.conditionMap);
        }
        
        temp.put(key, value);
        
        this.conditionMap = temp;
    }
    
    public Map<String, String> getConditionMap()
    {
        return conditionMap;
    }

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public List<ScheduleNode> getChildren()
    {
        return children;
    }
}
