/**************************************************************************
 * $$RCSfile: ContextImpl.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: ContextImpl.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.context;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;


import org.apache.log4j.*;

import com.howbuy.appframework.homo.configure.commons.container.Container;
import com.howbuy.appframework.homo.configure.commons.container.ContainerException;
import com.howbuy.appframework.homo.configure.commons.container.ContainerFactory;
import com.howbuy.appframework.homo.configure.commons.container.ContainerImpl;


/** context implementation of the db container
 *
 * @author kidd
 * @version 1.0
 */
public class ContextImpl implements Context {
	
    public ContextImpl(){
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Logger logger = Logger.getLogger(ContextImpl.class);

    private Container container = null;

    /**
	 * @return Container
	 */
	private Container getContainer(){
    	return ContainerFactory.getContainer();
    }

    public SessionBean lookupSessionBean(String name) throws ContainerException {
		SessionBeanImpl sessionBean = (SessionBeanImpl)((ContainerImpl)getContainer()).lookupSessionBean(name);
		sessionBean.setContext(this);
		logger.debug("Object call from context: SessionBean lookupSessionBean("+name+")");
		return (SessionBean) sessionBean;
    }
    
    public Object lookupElementDef(String name) throws ContainerException {
		Object baseInterface = (Object)((ContainerImpl)getContainer()).lookupElementDef(name);
		logger.debug("Object call from context: Object lookupElementDef("+name+")");
		return baseInterface;
    }
    
    public String[] lookupDataSource(String dsId) throws ContainerException {
		String[] bobject = (String[])((ContainerImpl)getContainer()).lookupDataSource(dsId);
		logger.debug("Object created from context: String[] lookupDataSource("+dsId+")");
		return bobject;
    }
    
    public ArrayList<String[]> lookupDataSourceList() throws ContainerException {
	    ArrayList<String[]> dataSourceList = (ArrayList<String[]>)((ContainerImpl)getContainer()).lookupDataSourceList();
		logger.info("Object call from context: ArrayList<String[]> lookupDataSourceList()");
		return dataSourceList;
    }

    /** set container of this context
     * @param container container
     */
    public void setContainer(Container container) {
        this.container = (ContainerImpl) container;        
    }
}