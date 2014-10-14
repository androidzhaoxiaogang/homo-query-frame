package com.howbuy.appframework.homo.queryapi;

import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class JsonQueryResp implements java.io.Serializable
{
    private String templateId;
    
    private String pojoClass;
    
    private String resultCode;
    
    private String resultMsg;
    
    private List<Map<String, String>> records;
    
    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getPojoClass()
    {
        return pojoClass;
    }

    public void setPojoClass(String pojoClass)
    {
        this.pojoClass = pojoClass;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getResultMsg()
    {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg)
    {
        this.resultMsg = resultMsg;
    }

    public List<Map<String, String>> getRecords()
    {
        return records;
    }

    public void setRecords(List<Map<String, String>> records)
    {
        this.records = records;
    }
}
