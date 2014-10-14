package com.howbuy.appframework.homo.queryapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.howbuy.appframework.homo.queryapi.common.struct.Case;
import com.howbuy.appframework.homo.queryapi.common.struct.Condition;
import com.howbuy.appframework.homo.queryapi.common.struct.Operation;
import com.howbuy.appframework.homo.queryapi.common.struct.ResultSet;
import com.howbuy.appframework.homo.queryapi.common.struct.Where;

/**
 * json格式的查询请求对应的实体类.
 * @author li.zhang
 *
 */
@SuppressWarnings("serial")
public class JsonQueryRequest implements java.io.Serializable
{
    /** 模板id. 一个templateId对应一个要查找的表或者视图**/
    private String templateId;
    
    private Where where;
    
    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public Where getWhere()
    {
        return where;
    }

    public void setWhere(Where where)
    {
        this.where = where;
    }
    
    /**
     * 转换成json格式的字符串
     * @return
     */
    public String toJsonString()
    {
        StringBuilder jsonstr = new StringBuilder();
        if (null == this)
        {
            return jsonstr.toString();
        }
        
        jsonstr.append("{")
                   .append("\"query-json-req\":")
                   .append("{")
                       .append("\"template-id\":").append("\"").append(templateId).append("\"").append(null == where ? "" : ",")
                       .append(null == where ? "" : where.toJsonString())
                   .append("}")
               .append("}");
        return jsonstr.toString();
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        JsonQueryRequest req = new JsonQueryRequest();
        Where where = new Where();
        req.setTemplateId("TR1001102001002001");
        req.setWhere(where);
        
        Operation operation = new Operation();
        where.setOperation(operation);
        
        operation.setCommand("or");
       
        ResultSet rs01 = new ResultSet();
        ResultSet rs02 = new ResultSet();
        
        Condition condition = new Condition();
        condition.setCommand("and");
        condition.addCase(new Case("fundCode", "482002", "="));
        condition.addCase(new Case("fundName", "工银瑞信货币市场基金", "="));
        Operation op = new Operation();
        rs01.setCondition(condition);
        
        ResultSet rs03 = new ResultSet();
        Condition cond03 = new Condition();
        cond03.setCommand("and");
        cond03.addCase(new Case("fundCode", "020005", "="));
        cond03.addCase(new Case("fundName", "国泰金马稳健回报证券投资基金", "="));
        op.setCommand("and");
        op.addResultset(rs03);
        rs03.setCondition(cond03);
        rs02.setOperation(op);
        
        operation.addResultset(rs01);
        operation.addResultset(rs02);
        
        System.out.println("REQUEST JSONSTRING: " + req.toJsonString() + "\n");
        System.out.println("RESPONSE JSONSTRING(UTF-8 * 1): " + URLEncoder.encode(req.toJsonString(), "utf-8") + "\n");
        System.out.println("RESPONSE JSONSTRING(UTF-8 * 2): " + URLEncoder.encode(URLEncoder.encode(req.toJsonString(), "utf-8"), "utf-8") + "\n");
    }
}