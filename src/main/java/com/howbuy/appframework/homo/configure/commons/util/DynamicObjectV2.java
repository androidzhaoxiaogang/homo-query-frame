package com.howbuy.appframework.homo.configure.commons.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

/**
 * 动态生成实体bean.
 * @author li.zhang
 *
 */
public class DynamicObjectV2 implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Object object;

    public BeanMap beanMap;

    /** default constructor */
    public DynamicObjectV2(Map<String, Class<?>> fieldsMap)
    {
        this.object = generateBean(fieldsMap);
        this.beanMap = BeanMap.create(this.object);
    }

    private Object generateBean(Map<String, Class<?>> fieldsMap)
    {
        BeanGenerator generator = new BeanGenerator();
        Set<String> keySet = fieldsMap.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();)
        {
            String key = it.next();
            generator.addProperty(key, fieldsMap.get(key));
        }
        return generator.create();
    }

    /**
     * 通过属性名得到属性值
     * 
     * @param property
     *            属性名
     * @return 值
     */
    public Object getValue(String property)
    {
        return beanMap.get(property);
    }

    /**
     * 给bean属性赋值
     * 
     * @param property
     *            属性名
     * @param value
     *            值
     */
    public void setValue(String property, Object value)
    {
        beanMap.put(property, value);
    }

    /**
     * 得到该实体bean对象
     * 
     * @return
     */
    public Object getObject()
    {
        return this.object;
    }

    /**
     * 用法示范.
     * @param args
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {

        //设置类成员属性
        HashMap<String, Class<?>> propertyMap = new HashMap<String, Class<?>>();

        propertyMap.put("id", Class.forName("java.lang.Integer"));
        propertyMap.put("name", Class.forName("java.lang.String"));
        propertyMap.put("address", Class.forName("java.lang.String"));

        //生成动态 Bean
        DynamicObjectV2 bean = new DynamicObjectV2(propertyMap);

        //给 Bean 设置值
        bean.setValue("id", new Integer(123));
        bean.setValue("name", "454");
        bean.setValue("address", "789");

        //从 Bean 中获取值，当然了获得值的类型是 Object, 这个是获取属性值的方法一.
        System.out.println("  >> id      = " + bean.getValue("id"));
        System.out.println("  >> name    = " + bean.getValue("name"));
        System.out.println("  >> address = " + bean.getValue("address"));

        //获得bean的实体
        Object object = bean.getObject();

        //通过反射查看所有方法名
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            System.out.println(methods[i].getName());
            if (methods[i].getName().startsWith("get"))
            {
                //获取属性值的方法2. 方法2用到了反射，性能上会差点.
                System.out.println(methods[i].getName() + " : " + methods[i].invoke(object));
            }
        }
    }

}
