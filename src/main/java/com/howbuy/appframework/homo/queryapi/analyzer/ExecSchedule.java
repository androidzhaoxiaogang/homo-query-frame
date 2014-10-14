package com.howbuy.appframework.homo.queryapi.analyzer;

/**
 * 执行计划.
 * @author li.zhang
 *
 */
public class ExecSchedule
{
    /** 执行计划树的根节点. **/
    private ScheduleNode root;

    public ScheduleNode getRoot()
    {
        return root;
    }

    public void setRoot(ScheduleNode root)
    {
        this.root = root;
    }
}
