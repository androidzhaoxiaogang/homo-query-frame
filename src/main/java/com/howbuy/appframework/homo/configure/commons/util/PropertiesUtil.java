/*
 * Created on 2005-9-15
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.howbuy.appframework.homo.configure.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Title:<p>
 * Description:<p>
 * Copyright:Copyright (c) 2005<p>
 * Company: <p>
 * @author kidd
 * @version 1.0
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PropertiesUtil {
	
	private static String propsName = "jdbc.properties";
	/**
	 * @param propertiesName
	 * @return
	 */
	/**
	 * @param propertiesName
	 * @return
	 */
	public static String getPropertiesValue(String propertiesName){
		String proValue = "";
		try {
			URL path = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation();

			if(path!=null&&path.getPath().lastIndexOf(".jar")>0){
				JarFile jarFile = new JarFile(path.getPath());
				JarEntry entry = jarFile.getJarEntry(propsName);
				InputStream input = jarFile.getInputStream(entry);
				Properties p = new Properties();
				p.load(input);
				proValue = p.getProperty(propertiesName);
				p.clear();
			}else{
				String temp = PropertiesUtil.class.getClassLoader().getResource(propsName).getPath();	
				Properties p = new Properties();
				p.load(new FileInputStream(new File(temp)));
				proValue = p.getProperty(propertiesName);
				p.clear();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proValue;
	}

	/**
	 * @param propertiesName
	 * @param propsName
	 * @return
	 */
	public static String getPropertiesValue(String propertiesName,String propName){
		String proValue = "";
		try {
			URL path = PropertiesUtil.class.getProtectionDomain().getCodeSource().getLocation();

			if(path!=null&&path.getPath().lastIndexOf(".jar")>0){
				JarFile jarFile = new JarFile(path.getPath());
				JarEntry entry = jarFile.getJarEntry(propsName);
				InputStream input = jarFile.getInputStream(entry);
				Properties p = new Properties();
				p.load(input);
				proValue = p.getProperty(propertiesName);
				p.clear();
			}else{
				ClassLoader classes = PropertiesUtil.class.getClassLoader();
				URL url = classes.getResource(propName);
				
				String temp = url.getPath();	
				Properties p = new Properties();
				p.load(new FileInputStream(new File(temp)));
				proValue = p.getProperty(propertiesName);
				p.clear();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proValue;
	}
	
	public static String getPropertiesValue(Class classes,String propertiesName,String propName){
		String proValue = "";
		try {
			URL path = classes.getProtectionDomain().getCodeSource().getLocation();

			if(path!=null&&path.getPath().lastIndexOf(".jar")>0){
				JarFile jarFile = new JarFile(path.getPath());
				JarEntry entry = jarFile.getJarEntry(propName);
				InputStream input = jarFile.getInputStream(entry);
				Properties p = new Properties();
				p.load(input);
				proValue = p.getProperty(propertiesName);
				p.clear();
			}else{
				String temp = PropertiesUtil.class.getClassLoader().getResource(propName).getPath();	
				Properties p = new Properties();
				p.load(new FileInputStream(new File(temp)));
				proValue = p.getProperty(propertiesName);
				p.clear();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proValue;
	}
	
	public static void writeProperties(String filePath, String parameterName,String parameterValue) {
		   Properties props = new Properties();
		   try {
		    File f = new File(filePath);

		     if (f.exists()) {

		         InputStream fis = new FileInputStream(filePath);
		         props.load(fis);
		         fis.close();

		        } else {
		        f.createNewFile();
		        }

		    OutputStream fos = new FileOutputStream(filePath);
		    props.setProperty(parameterName, parameterValue);

		    props.store(fos, "");
		    PrintStream fw = new PrintStream(filePath);
		    props.list(fw);
		    fos.close();
		   } catch (IOException e) {
		    e.printStackTrace();
		   }
	}

	public static Properties readProperties(String filePath) {
	   Properties props = new Properties();
	   InputStream is;
	   try {
		   is = new FileInputStream(filePath);
		   props.load(is);
		   is.close();
		   return props;
	   } catch (Exception e1) {
	    // TODO Auto-generated catch block
		   e1.printStackTrace();
		   return null;
	   }
    }
	
	/**
	 * �޸�
	 * @param propertiesName
	 * @param propertiesValue
	 */
	public static void writeData(String propertiesName, String propertiesValue) {  
        Properties prop = new Properties();  
        InputStream fis = null;  
        OutputStream fos = null;  
        try {  
            java.net.URL  url = PropertiesUtil.class.getClassLoader().getResource(propsName);  
            File file = new File(url.toURI());  
            if (!file.exists())  
                file.createNewFile();  
            fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//һ��Ҫ���޸�ֵ֮ǰ�ر�fis  
            fos = new FileOutputStream(file);  
            prop.setProperty(propertiesName, propertiesValue);  
            prop.store(fos, "Update '" + propertiesName + "' value");  
            fos.close();  
              
        } catch (IOException e) {  
            System.err.println("Visit " + propsName + " for updating "  
            + propertiesValue + " value error");  
        } catch (URISyntaxException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                fos.close();  
                fis.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }
}
