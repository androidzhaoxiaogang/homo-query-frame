/**************************************************************************
 * $$RCSfile: ContainerFactory.java $$  $$Revision: 1.1 $$  $$Date: 2010/01/19 02:05:44$$
 *
 * $$Log: ContainerFactory.java$$
 **************************************************************************/
package com.howbuy.appframework.homo.configure.commons.container;

/**
 *
 * @author  Kidd
 */
public class ContainerFactory {
    
    private static Container container = null;
    
    public synchronized static Container getContainer() {
        if(container == null) {
        	container = (Container) ContainerImpl.getInstance();
        }
        return container;
    }
    
    public static void setContainer(Container con) {
        container = con;
    }

}
