package com.howbuy.appframework.homo.configure.commons.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 对象集合
	 */
	private List<Object> result = null;

	/**
	 * 是否成功 true=成功；false=失败
	 */
	private boolean isSuccess = false;
	
	/**
	 * 失败信息
	 */
	private String errorMessage;
	
	/**
	 * 超时标示 true=超时；false=未超时
	 */
	private boolean timeOut = false;
	
	/**
	 * 动态对象有效字段数
	 */
	private int dynamicColunmCount;
	
	/**
	 * 动态对象表头
	 */
	private String[] dynamicColunmHead;
	
	/**
	 * @return the dynamicColunmHead
	 */
	public String[] getDynamicColunmHead() {
		return dynamicColunmHead;
	}

	/**
	 * @param dynamicColunmHead the dynamicColunmHead to set
	 */
	public void setDynamicColunmHead(String[] dynamicColunmHead) {
		this.dynamicColunmHead = dynamicColunmHead;
	}

	/**
	 * @return the dynamicColunmCount
	 */
	public int getDynamicColunmCount() {
		return dynamicColunmCount;
	}

	/**
	 * @param dynamicColunmCount the dynamicColunmCount to set
	 */
	public void setDynamicColunmCount(int dynamicColunmCount) {
		this.dynamicColunmCount = dynamicColunmCount;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public List<Object> getResult() {
		return result;
	}

	public void setResult(List<Object> result) {
		this.result = result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setTimeOut(boolean timeOut) {
		this.timeOut = timeOut;
	}
	public boolean isTimeOut() {
		return timeOut;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@SuppressWarnings("static-access")
	public void setiParams(Map<Object, Object> iParams) {
		this.iParams = iParams;
	}

	public static Map<Object, Object> getiParams() {
		return iParams;
	}

	private static Map<Object, Object> iParams = new HashMap<Object, Object>();
	
}
