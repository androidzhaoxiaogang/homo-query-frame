package com.howbuy.appframework.homo.queryapi.analyzer.common;

import org.apache.commons.lang.StringUtils;

/**
 * 运算类型
 * @author li.zhang
 *
 */
public enum OperationType
{
    AND("and"),
    
    OR("or");
    
    private String command;
    
    /**
     * 构造方法
     * @param command 
     */
    private OperationType(String command)
    {
        this.command = command;
    }
    
    public String getValue()
    {
        return this.command;
    }
    
    /**
     * 根据命令字得到对应的操作的枚举值.
     * @param command 命令字
     * @return
     */
    public static OperationType get(String command)
    {
        OperationType operationType = null;
        if (StringUtils.isEmpty(command))
        {
            return operationType;
        }
        
        OperationType[] operationTypes = OperationType.values();
        for (int i = 0; i < operationTypes.length; i++)
        {
            operationType = operationTypes[i];
            if (command.equalsIgnoreCase(operationType.getValue()))
            {
                break;
            }
        }
        return operationType;
    }
}
