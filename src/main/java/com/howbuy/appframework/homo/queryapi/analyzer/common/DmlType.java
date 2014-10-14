package com.howbuy.appframework.homo.queryapi.analyzer.common;

import org.apache.commons.lang.StringUtils;

/**
 * 运算类型
 * @author li.zhang
 *
 */
public enum DmlType
{
    INSERT(1, "insert"),
    
    UPDATE(2, "update"),
    
    DELETE(3, "delete");
    
    private int type;
    private String dmlType;
    
    /**
     * 构造方法
     * @param dmlType 
     */
    private DmlType(int type ,String dmlType)
    {
        this.type = type;
        this.dmlType = dmlType;
    }
    
    public String getStrValue()
    {
        return this.dmlType;
    }
    
    public int getValue()
    {
        return type;
    }
    
    /**
     * 根据dml操作类型得到对应的操作的枚举值.
     * @param dmlType insert or update or delete
     * @return
     */
    public static DmlType get(String dmlType)
    {
        DmlType operationType = null;
        if (StringUtils.isEmpty(dmlType))
        {
            return operationType;
        }
        
        DmlType[] operationTypes = DmlType.values();
        for (int i = 0; i < operationTypes.length; i++)
        {
            operationType = operationTypes[i];
            if (dmlType.equalsIgnoreCase(operationType.getStrValue()))
            {
                break;
            }
        }
        return operationType;
    }
    
    /**
     * 根据type得到对应的操作的枚举值.
     * @param type type
     * @return
     */
    public static DmlType get(int type)
    {
        DmlType operationType = null;
        
        DmlType[] operationTypes = DmlType.values();
        for (int i = 0; i < operationTypes.length; i++)
        {
            operationType = operationTypes[i];
            if (type == operationType.getValue())
            {
                break;
            }
        }
        return operationType;
    }
}
