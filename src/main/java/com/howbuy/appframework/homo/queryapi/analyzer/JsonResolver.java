package com.howbuy.appframework.homo.queryapi.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.analyzer.exception.CreateExecScheduleException;
import com.howbuy.appframework.homo.queryapi.common.Symbols;

/**
 * json请求解析器.
 * @author li.zhang
 *
 */
public class JsonResolver
{
    /** 实例. **/
    private static final JsonResolver INSTANCE = new JsonResolver();
    
    /** 日志. **/
    private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
    
    /**
     * 获取单例
     * @return
     */
    public static JsonResolver getInstance()
    {
        return INSTANCE;
    }

    /**
     * 根据前端传入的json查询请求创建对应的查询计划.
     * @param queryJsonStr json查询请求.
     * @return 得到查询计划.
     */
    public ExecSchedule createExecSchedule(String queryJsonStr) throws CreateExecScheduleException
    {
        ExecSchedule schedule = new ExecSchedule();
        StringBuilder message = new StringBuilder();
        message.delete(0, message.length());
        
        JSONObject root = JSONObject.fromObject(queryJsonStr);
        JSONObject jsonReq = (JSONObject)root.get("query-json-req");
        String templateId = (String)jsonReq.get("template-id");
        
        JSONObject where = jsonReq.getJSONObject("where");
        if (where.isNullObject())
        {
            message.append("创建执行计划失败，请确认json请求字符串符合格式要求,")
                   .append(" json-query-str is [ ")
                   .append(queryJsonStr)
                   .append(" ] ");
            throw new CreateExecScheduleException(message.toString());
        }
        
        JSONObject operation = where.getJSONObject("operation");
        
        //校验operation
        validateOperation(operation, queryJsonStr);
        
        //设置执行计划根节点.
        schedule.setRoot(createRoot(templateId));
        
        parseOperation(operation, queryJsonStr, schedule.getRoot());
        
        return schedule;
    }

    private ScheduleNode createRoot(String templateId)
    {
        ScheduleNode root = new ScheduleNode();
        root.setTemplateId(templateId);
        return root;
    }

    private void validateOperation(JSONObject operation, String queryJsonStr)
        throws CreateExecScheduleException
    {
        StringBuilder message = new StringBuilder();
        message.delete(0, message.length());
        
        //1.校验operation
        if (operation.isNullObject())
        {
            message.append("创建执行计划失败，请确认json请求字符串符合格式要求,")
                   .append(" json-query-str is [ ")
                   .append(queryJsonStr)
                   .append(" ] ");
            throw new CreateExecScheduleException(message.toString());
        }
        
        //2.校验operation下的resultset. operation是在resultset上操作的.
        JSONArray rsArray = (JSONArray)operation.getJSONArray("resultset");
        if (rsArray.isEmpty())
        {
            message.delete(0, message.length());
            message.append("创建执行计划失败，请确认json请求字符串符合格式要求,")
                   .append(" json-query-str is [ ")
                   .append(queryJsonStr)
                   .append(" ] ");
            throw new CreateExecScheduleException(message.toString());
        }
        
        //3.operation的command属性不能为空.
        String operationCmd = (String)operation.get("-command");
        if (StringUtils.isEmpty(operationCmd) && 1 < rsArray.size())
        {
            message.delete(0, message.length());
            message.append("-command's value in json-query-req message can not be empty when operation own multi resultset child node.");
            throw new CreateExecScheduleException(message.toString());
        }
        
    }

    private void parseOperation(JSONObject operation, String queryJsonStr, ScheduleNode node)
        throws CreateExecScheduleException
    {
        //1.校验operation
        validateOperation(operation, queryJsonStr);
        
        //2.获得operation对象的command属性.
        String operationCmd = (String)operation.get("-command");
        node.setCommand(operationCmd);
        
        //3.设置子节点
        JSONArray rsArray = (JSONArray)operation.getJSONArray("resultset");
        List<ScheduleNode> rsNodes = new ArrayList<ScheduleNode>();
        for (int i = 0; i < rsArray.size(); i++)
        {
            JSONObject resultset = rsArray.getJSONObject(i);
            
            ScheduleNode rsNode = new ScheduleNode();
            rsNodes.add(rsNode);
            
            //解析resultset节点.
            parseResultSet(resultset, queryJsonStr, rsNode);
        }
        
        node.addChild(rsNodes);
    }

    private void validateResultSet(JSONObject resultset, String queryJsonStr)
        throws CreateExecScheduleException
    {
        
    }
        
    private void parseResultSet(JSONObject resultset, String queryJsonStr, ScheduleNode node)
        throws CreateExecScheduleException
    {
        //校验resultset
        validateResultSet(resultset, queryJsonStr);
        
        JSONObject rsOperation = resultset.getJSONObject("operation");
        if (rsOperation.isNullObject())
        {
            JSONObject condition = resultset.getJSONObject("condition");
            if (condition.isNullObject())
            {
                return;
            }
            
            ScheduleNode condNode = new ScheduleNode();
            node.addChild(condNode);
            
            parseCondition(condition, condNode);
        }
        else
        {
            ScheduleNode rsOpNode = new ScheduleNode();
            node.addChild(rsOpNode);
            parseOperation(rsOperation, queryJsonStr, rsOpNode);
        }
    }
    
    private void parseCondition(JSONObject condition, ScheduleNode node)
    {
        String conditionCmd = (String)condition.get("-command");
        node.setCommand(conditionCmd);
        
        //condition下的各个case解析.
        JSONArray caseArray = (JSONArray)condition.getJSONArray("case");
        if (caseArray.isEmpty())
        {
            return;
        }
        
        List<ScheduleNode> caseNodes = new ArrayList<ScheduleNode>();
        for (int i = 0; i < caseArray.size(); i++)
        {
            ScheduleNode caseNode = new ScheduleNode();
            JSONObject jsonCase = caseArray.getJSONObject(i);
            
            if (!isValidConditon(jsonCase))
            {
                LOGGER.warn("The fragment of query condition is invalid, the fragment is : " + jsonCase.toString());
                continue;
            }
            
            String columeName = (String)jsonCase.get("-colume-name");
            Object columeVal = jsonCase.get("-colume_value");
            String expressOp = (String)jsonCase.get("-express_op");
            
            String columnValue = null;
            if (JSONNull.class.isInstance(columeVal))
            {
                //因为column为null的列，在存储时是以"NULL"形式来存储的，所以这里有这样的操作.
                columnValue = Symbols.NULL;
            }
            else
            {
                columnValue = (String)columeVal;
            }
            
            caseNode.put("-colume-name", columeName);
            caseNode.put("-colume_value", columnValue);
            caseNode.put("-express_op", expressOp);
            
            caseNodes.add(caseNode);
        }
        
        node.addChild(caseNodes);
    }

    private boolean isValidConditon(JSONObject jsonCase)
    {
        boolean isValid = false;
        
        String columeName = (String)jsonCase.get("-colume-name");
        String expressOp = (String)jsonCase.get("-express_op");
        
        /**
         * 默认是2元运算，所以这里就这样简单的判断下是否是有效的条件.
         * 这里后续可以考虑，根据运算符expressOp是一元元算、二元运算还是三元运算来做不同的策略判断
         * 有效性.目前先简单处理下.
         */
        if (StringUtils.isEmpty(columeName) || StringUtils.isEmpty(expressOp))
        {
            return isValid;
        }
        
        isValid = true;
        return isValid;
    }
}
