package com.howbuy.appframework.homo.queryapi.analyzer;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.howbuy.appframework.homo.queryapi.JsonQueryResp;
import com.howbuy.appframework.homo.queryapi.QueryUtils;
import com.howbuy.appframework.homo.queryapi.analyzer.common.ExpressOp;
import com.howbuy.appframework.homo.queryapi.analyzer.common.OperationType;
import com.howbuy.appframework.homo.queryapi.analyzer.exception.ExecuteScheduleException;

/**
 * 计划执行器
 * @author li.zhang
 *
 */
public class ScheduleExecutor
{

    /** log **/
    private static final Logger LOG = LogManager.getLogger(ScheduleExecutor.class);
    
    /** Instance **/
    private static final ScheduleExecutor INSTANCE = new ScheduleExecutor();
    
    /**
     * 获得实例
     * @return
     */
    public static ScheduleExecutor getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 执行解析后的执行计划.
     * @param schedule 执行计划.
     * @return 查询结果.
     */
    public JsonQueryResp execute(ExecSchedule schedule) throws ExecuteScheduleException
    {
        JsonQueryResp queryResultSet = null;
        ExecResultSet resultSet = null;
        ScheduleNode root = schedule.getRoot();
        String templateId = root.getTemplateId();
        List<ScheduleNode> children = root.getChildren();
        
        //1.校验
        doCheck(root);
        
        /**
         * 开始执行执行计划. 
         * 1.根据左子树条件得到结果集rs01.
         * 2.根据右子树条件得到结果集rs02. 
         * 3.根据command类型对rs01和rs02做归并，可能是两者取交集，也可能是两者取并集，这个取决于command是and还是or.
         * .......
         * 
         * 注释是以二叉树来说明的，其实这里是根据各个子树的条件，得到一个结果集集合列表resultset数组,然后对这个数组做合并处理.
         */
        ScheduleNode node = null;
        if (1 == children.size())
        {
            node = children.get(0);
            resultSet = doSelect(templateId, node);
        }
        //如果子元素大于1.
        else
        {
            String command = root.getCommand();
            ExecResultSet[] rsList = new ExecResultSet[children.size()];
            for (int i = 0; i < children.size(); i++)
            {
                node = children.get(i);
                rsList[i] = doSelect(templateId, node);
            }
            
            //归并各个查询结果集，这些结果集之间，可能是取交集也可能是取并集.
            resultSet = doArchive(command, rsList);
        }
        
        /**
         * 3.最后, 根据resultSet(resultSet中封装了表中记录的一个唯一标识，这个标识可以是rowId,
         * 对于视图而言可以是构造视图的时候拼装起来的记录的唯一标识uniqueId)
         * 查询出结果集，并封装到结果集中返回.
         */
        queryResultSet = onFinal(templateId, resultSet);

        return queryResultSet;
    }

    /**
     * 根据rowIds集合列表，从缓存中得到这些rowIds对应的所有结果集.
     * @param templateId templateId
     * @param resultSet 目前这里存储的是rowId的集合.
     * @return
     */
    private JsonQueryResp onFinal(String templateId, ExecResultSet resultSet)
    {
        JsonQueryResp queryResp = new JsonQueryResp();
        if (null == resultSet || CollectionUtils.isEmpty(resultSet.getResultSet()))
        {
            queryResp.setTemplateId(templateId);
            return queryResp;
        }
        
        return QueryUtils.archiveRecords(resultSet.getResultSet(), templateId);
    }

    /**
     * 将各个子节点的结果集归并，可能是取交集也可能是取并集，这点取决于command是and还是or.
     * @param command 命令字, 例如and或者or操作
     * @param rsList 结果集列表
     * @return
     */
    private ExecResultSet doArchive(String command, ExecResultSet[] rsList)
    {
        ExecResultSet resultSet = null;
        switch (OperationType.get(command))
        {
            case AND:
                resultSet = doAnd(rsList);
                break;
                
            case OR:
                resultSet = doOr(rsList);
                break;
        }
        
        return resultSet;
    }

    private void doCheck(ScheduleNode root) throws ExecuteScheduleException
    {
        String command = root.getCommand();
        List<ScheduleNode> children = root.getChildren();
        if (StringUtils.isEmpty(command))
        {
            LOG.warn("This command property of execute schedule's root node is empty, please check.");
            throw new ExecuteScheduleException("This command property of execute schedule's root node is empty, please check.");
        }
        
        if (CollectionUtils.isEmpty(children))
        {
            LOG.warn("This execute schedule's root node not have child nodes, please check.");
            throw new ExecuteScheduleException("This execute schedule's root node not have child nodes, please check.");
        }
    }

    /**
     * and运算
     * @param templateId 模板ID
     * @param nodes 做and操作的节点
     * @return
     */
    private ExecResultSet doAnd(ExecResultSet[] rsList)
    {
        ExecResultSet resultset = null;
        if (null == rsList || 0 == rsList.length)
        {
            return resultset;
        }
        
        resultset = new ExecResultSet();
        if (1 == rsList.length)
        {
            resultset.add(rsList[0].getResultSet());
        }
        else if (2 == rsList.length)
        {
            if (null == rsList[0] || null == rsList[1])
            {
                return null;
            }
            
            List<String> member01 = rsList[0].getResultSet();
            List<String> member02 = rsList[1].getResultSet();
            
            member01.retainAll(member02);//二者取交集, 交集的结果反映在member01中.
            resultset.add(member01);
        }
        else
        {
            ExecResultSet[] otherList = new ExecResultSet[rsList.length - 1];
            System.arraycopy(rsList, 1, otherList, 0, rsList.length - 1);
            
            ExecResultSet first = rsList[0]; 
            ExecResultSet another = doAnd(otherList);
            
            if (null == first)
            {
                return null;
            }
            
            first.getResultSet().retainAll(another.getResultSet());//取交集.
            
            resultset.add(first.getResultSet());
        }
        
        return resultset;
    }
    
    /**
     * or运算
     * @param templateId 模板ID
     * @param nodes 做or操作的节点
     * @return 
     */
    private ExecResultSet doOr(ExecResultSet[] rsList)
    {
        ExecResultSet resultset = null;
        if (null == rsList || 0 == rsList.length)
        {
            return resultset;
        }
        
        resultset = new ExecResultSet();
        for (int i = 0; i < rsList.length; i++)
        {
            if (null == rsList[i])
            {
                continue;
            }
            
            resultset.add(rsList[i].getResultSet());
        }
        return resultset;
    }

    /**
     * 根据node中的条件查找
     * @param templateId 模板ID
     * @param node 根据node中的条件查找
     * @return 返回查询结果集
     */
    private ExecResultSet doSelect(String templateId, ScheduleNode node)
    {
        ExecResultSet resultset = null;
        
        //如果当前node节点是叶子节点
        if (CollectionUtils.isEmpty(node.getChildren()))
        {
            Map<String, String> conditionMap = node.getConditionMap();
            
            //在创建执行计划的时候，已经过滤了无效的条件，所以这里的conditionMap肯定不为空.
            resultset = doQueryStrategy(templateId, conditionMap);
        }
        //如果不是叶子节点.
        else
        {
            //如果只有一个子节点.
            if (1 == node.getChildren().size())
            {
                resultset = doSelect(templateId, node.getChildren().get(0));
            }
            else
            {
                String command = node.getCommand();
                List<ScheduleNode> children = node.getChildren();
                ExecResultSet[] rsList = new ExecResultSet[children.size()];
                for (int i = 0; i < children.size(); i++)
                {
                    ScheduleNode child = children.get(i);
                    rsList[i] = doSelect(templateId, child);
                }
                
                resultset = doArchive(command, rsList);
            }
           
        }
        return resultset;
    }

    /**
     * 执行查询策略
     * @param templateId
     * @param conditionMap
     */
    private ExecResultSet doQueryStrategy(String templateId, Map<String, String> conditionMap)
    {
        ExecResultSet resultset = new ExecResultSet();
        String columeName = conditionMap.get("-colume-name");
        String columeValue = conditionMap.get("-colume_value");
        String expressOp = conditionMap.get("-express_op");
        
        switch (ExpressOp.get(expressOp))
        {
            case EQUAL:
                onEqual(resultset, templateId, columeName, columeValue);
                break;
            case LT:
                break;
            case GT:
                break;
            case LE:
                break;
            case GE:
                break;
        }
        
        return resultset;
    }

    private void onEqual(ExecResultSet resultset, String templateId, String columeName, String columeValue)
    {
        List<String> uniqueIds = QueryUtils.queryUniqueIdsFromRedis(templateId, columeName, columeValue);
        if (!CollectionUtils.isEmpty(uniqueIds))
        {
            resultset.add(uniqueIds);
        }
    }
}
