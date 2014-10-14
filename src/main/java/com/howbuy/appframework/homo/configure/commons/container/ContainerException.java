/**************************************************************************
 * $$RCSfile: ContainerException.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: ContainerException.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.container;

import java.rmi.RemoteException;

/** Exception when error happens to create entity
 * 
 * @author kidd
 */
public class ContainerException extends RemoteException

{



    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** entity home creation error
     * @param s reason
     */    
    public ContainerException(String s)

    {

        super(s);

    }
}
