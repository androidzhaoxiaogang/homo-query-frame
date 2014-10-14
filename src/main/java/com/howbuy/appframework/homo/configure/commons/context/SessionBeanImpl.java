/**************************************************************************
 * $$RCSfile: SessionBeanImpl.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: SessionBeanImpl.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.context;

import org.apache.log4j.Logger;

/**
* @author  kidd
* @version
*/
public abstract class SessionBeanImpl implements SessionBean{

	protected Context context = null;
	
	protected Logger logger = Logger.getLogger(SessionBeanImpl.class);

	/**
	 * @return
	 */
	public Context getContext() {
		return new ContextImpl();
	}

	/**
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	
}
