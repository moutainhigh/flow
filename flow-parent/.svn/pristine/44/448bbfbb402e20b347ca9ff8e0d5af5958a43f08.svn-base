package com.aspire.etl.tool;

import com.aspire.common.encryption.DES;
import com.aspire.common.encryption.DESKey;
import com.aspire.common.encryption.DESLimit;

public class KeyUtil
{
    private byte tag1;
    private byte tag2;
 
    public KeyUtil()
    {
        tag1 = -34;
        tag2 = -118;
    }
 
    public String getANewCipherKey()
    {
        try {
			DESLimit deslimit = new DESLimit(tag1, tag2);
			DESKey deskey = new DESKey(deslimit);
			return deskey.genCipherKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
 
    public String encrpyt(String cipherKey, String value)
    {
        try {
        	String s;
			DESKey deskey = new DESKey();
			String s2 = deskey.toPlainKey(cipherKey);
			DESLimit deslimit = new DESLimit(tag1, tag2);
			DES des = new DES(s2, deslimit);
			s = des.encrypt(value);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public String decrpyt(String cipherKey, String value)
    {
    	try {
    		String s;
			DESKey desKey = new DESKey();  
			String plainKey = desKey.toPlainKey(cipherKey);
			DES des = new DES(plainKey);
			s = des.decrypt(value);
			return s;
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return null;
    }
}

