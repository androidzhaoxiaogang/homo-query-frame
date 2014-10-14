package com.howbuy.appframework.homo.configure.db.table;

import com.howbuy.appframework.homo.configure.db.table.vhmtablemeta.VHmTableMetaTable;

/**
 * 表加载器
 * @author li.zhang
 *
 */
public class TableLoader
{
    /** 单例 **/
    private static final TableLoader INSTANCE = new TableLoader();
    
    /**
     * 私有构造方法
     */
    private TableLoader()
    {
        load();
    }
    
    public static TableLoader getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 加载homo配置表,可覆写这个方法.
     */
    protected void load()
    {
        //目前只加载v_hm_table_meta视图.
        loadVHmTableMeta();
    }

    private static void loadVHmTableMeta()
    {
        VHmTableMetaTable.getInstance();
    }
}
