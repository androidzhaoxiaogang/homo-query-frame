package com.howbuy.appframework.homo.configure.commons.redis;

import org.apache.commons.lang.StringUtils;

/**
 * 模式类型
 * @author li.zhang
 *
 */
public enum ModeType
{
    READ_ONLY(1, "read-only"),
    
    WRITE_ONLY(2, "write-only"),
    
    BOTH(3, "both");
    
    private int type;
    private String poolType;
    
    /**
     * 构造方法
     * @param type
     * @param poolType 
     */
    private ModeType(int type ,String poolType)
    {
        this.type = type;
        this.poolType = poolType;
    }
    
    public String getStrValue()
    {
        return this.poolType;
    }
    
    public int getValue()
    {
        return type;
    }
    
    /**
     * 根据poolType得到对应的操作的枚举值.
     * @param poolType
     * @return
     */
    public static ModeType get(String poolType)
    {
        ModeType connPoolType = null;
        if (StringUtils.isEmpty(poolType))
        {
            return connPoolType;
        }
        
        ModeType[] operationTypes = ModeType.values();
        for (int i = 0; i < operationTypes.length; i++)
        {
            connPoolType = operationTypes[i];
            if (poolType.equalsIgnoreCase(connPoolType.getStrValue()))
            {
                break;
            }
        }
        return connPoolType;
    }
    
    /**
     * 根据type得到对应的操作的枚举值.
     * @param type type
     * @return
     */
    public static ModeType get(int type)
    {
        ModeType operationType = null;
        
        ModeType[] operationTypes = ModeType.values();
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
