package com.howbuy.appframework.homo.queryapi.analyzer.common;

import org.apache.commons.lang.StringUtils;

/**
 * 运算符类型
 * @author li.zhang
 *
 */
public enum ExpressOp
{
    /** 等于 **/
    EQUAL("="),
    
    /** 小于. **/
    LT("<"),
    
    /** 大于 **/
    GT(">"),
    
    /** 小于等于. **/
    LE("<="),
    
    /** 大于等于. **/
    GE(">=");
    
    private String operator;
    
    /**
     * 构造方法
     * @param operator 
     */
    private ExpressOp(String operator)
    {
        this.operator = operator;
    }
    
    public String getValue()
    {
        return this.operator;
    }
    
    /**
     * 根据运算符得到对应的操作的枚举值.
     * @param operator 运算符
     * @return
     */
    public static ExpressOp get(String operator)
    {
        ExpressOp operationType = null;
        if (StringUtils.isEmpty(operator))
        {
            return operationType;
        }
        
        ExpressOp[] operationTypes = ExpressOp.values();
        for (int i = 0; i < operationTypes.length; i++)
        {
            operationType = operationTypes[i];
            if (operator.equalsIgnoreCase(operationType.getValue()))
            {
                break;
            }
        }
        return operationType;
    }
}
