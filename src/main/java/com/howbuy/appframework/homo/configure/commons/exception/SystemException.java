/**************************************************************************
 * $$RCSfile: SystemException.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: SystemException.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.exception;

/**
*
* @author kidd
*/

public class SystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SystemException(Throwable root) {
		super(root);
	}

	public SystemException(String string, Throwable root) {
		super(string, root);
	}

	public SystemException(String s) {
		super(s);
	}
}






