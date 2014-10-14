/**************************************************************************
 * $$RCSfile: ExectionUtil.java,v $$  $$Revision: 1.6 $$  $$Date: 2010/04/20 02:08:06 $$
 *
 * $$Log: ExectionUtil.java,v $
 * $Revision 1.6  2010/04/20 02:08:06  wudawei
 * $20100420
 * $$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.util;

import org.apache.log4j.Logger;

import com.howbuy.appframework.homo.configure.commons.container.Container;
import com.howbuy.appframework.homo.configure.commons.container.ContainerFactory;
import com.howbuy.appframework.homo.configure.commons.context.Context;
/**
*
* @author kidd
*/
public class ExectionUtil {
	
	private static Container container = ContainerFactory.getContainer();
	
	private static Context context = container.newContext();
	
	protected Logger logger = Logger.getLogger(ExectionUtil.class);
	
	/**
	 * @return
	 */
	public static Context getContext() {
		return context;
	}

}
