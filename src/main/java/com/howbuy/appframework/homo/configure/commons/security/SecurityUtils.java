package com.howbuy.appframework.homo.configure.commons.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * MD5加密.
 * @author li.zhang
 * 
 */
public class SecurityUtils
{
    /**
     * 加密(base64+DES+base64)
     * @param content content
     * @param secretKey secretKey
     * @return
     * @throws Exception
     */
    public String encrypt(String content, String secretKey) throws Exception
    {
        secretKey = base64Encrypt(secretKey.getBytes());
        byte[] contentBytes = content.getBytes("utf-8");
        String base64Encrypt = base64Encrypt(contentBytes);
        byte[] desEncrypt = desEncrypt(base64Encrypt.getBytes(), secretKey);
        return base64Encrypt(desEncrypt);
    }
    
    /**
     * 解密(base64+DES+base64)
     * @param encryptBytes
     * @throws Exception 
     */
    public String decrypt(byte[] encryptBytes, String secretKey) 
        throws Exception
    {
        String decrypt = null;
        secretKey = base64Encrypt(secretKey.getBytes());
        byte[] base64Decrypt = base64Decrypt(new String(encryptBytes));
        byte[] desDecrypt = desDecrypt(base64Decrypt, secretKey);
        base64Decrypt = base64Decrypt(new String(desDecrypt));
        decrypt = new String(base64Decrypt, "utf-8");
        return decrypt;
    }
    
    /**
     * DES加密
     * @param content 要加密的内容
     * @param secretKey 加密的秘钥
     * @return
     * @throws Exception
     */
    public byte[] desEncrypt(byte[] content, String secretKey) throws Exception
    {
        /** DES加密.**/
        byte[] encryptBytes = null;
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());  
        
        //创建一个密匙工厂，然后用它把DESKeySpec转换成  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        
        //Cipher对象实际完成加密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        
        //用密匙初始化Cipher对象  
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);  
        
        encryptBytes = cipher.doFinal(content);
        
        return encryptBytes;
    }

    /**
     * 解密
     * @param encryptBytes 加密的字节流,即待解密的字节流.
     * @param secretKey 秘钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public byte[] desDecrypt(byte[] encryptBytes, String secretKey) throws Exception
    {
        /** DES解密. **/
        SecureRandom random = new SecureRandom();  
        //创建一个DESKeySpec对象  
        DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());  
        //创建一个密匙工厂  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        //将DESKeySpec对象转换成SecretKey对象  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        //Cipher对象实际完成解密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        // 用密匙初始化Cipher对象  
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        
        return decryptBytes;
    }
    
    /**
     * Base64加密.
     * @param content content
     * @return
     */
    public String base64Encrypt(byte[] content)
    {
        return new BASE64Encoder().encode(content);
    }

    /**
     * Base64解密
     * @param encryptStr 加密的串
     * @return
     * @throws Exception
     */
    public byte[] base64Decrypt(String encryptStr) throws Exception
    {
        return new BASE64Decoder().decodeBuffer(encryptStr);
    }
    
    /**
     * @param args
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public static void main(String[] args) throws Exception
    {
        SecurityUtils utils = new SecurityUtils();
        String msg = "郭XX-精品小品";
        
        //1. des加密和解密
        byte[] encryptBytes = utils.desEncrypt(msg.getBytes("utf-8"), "12345678");
        System.out.println(new String(utils.desDecrypt(encryptBytes,  "12345678")));
        
        //2.base64+des加密和解密.
        String base64 = utils.base64Encrypt(msg.getBytes("utf-8"));
        encryptBytes = utils.desEncrypt(base64.getBytes(), base64);
        byte[] decryptBytes = utils.desDecrypt(encryptBytes, base64);
        System.out.println(new String(decryptBytes));
        decryptBytes = utils.base64Decrypt(new String(decryptBytes));
        System.out.println(new String(decryptBytes, "utf-8"));
        
        //3.
        System.out.println("=====================================");
        String secretKey = "我是秘钥，^O^";
        String encrypt = utils.encrypt(msg, secretKey);
        System.out.println(encrypt);
        System.out.println(utils.decrypt(encrypt.getBytes(), secretKey));
    }

}