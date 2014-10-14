/**************************************************************************
 * $$RCSfile: SystemNotFoundException.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: SystemNotFoundException.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.exception;

/**
 *
 * @author kidd
 */
public class SystemNotFoundException extends ClassNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SystemNotFoundException() {
		super();
	}

	public SystemNotFoundException(String string, Throwable root) {
		super(string, root);
	}

	public SystemNotFoundException(String s) {
		super(s);
	}

}






