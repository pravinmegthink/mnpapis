package com.megthink.gateway.utils;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {
	
	public static String key = "497dce3961e60e94a1c443f94f83ced9";
	
	public static void main(String a[])
	{
		try
		{
			String org = "WO6je7sfS";
			String enStr = encrypt(org);
			System.out.println(org+" ENC "+enStr);
			String deStr = decrypt(enStr, key);
			System.out.println(org+", EN "+enStr+", DE "+deStr);
		}
		catch(Exception E)
		{
			System.out.println("Exception "+E);
		}
	}

	public static String encrypt(String str) {
		String encryptedNumber = "";
			try {
				    
				    encryptedNumber=encrypt(str, key);
				} catch (Exception e) {
				    //logger.error("", e);
				}
        return encryptedNumber;
    }
	
	public static String encrypt(String value, String key) throws GeneralSecurityException {
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return byteArrayToHexString(encrypted);
    }
    
    public static String decrypt(String message, String key) throws GeneralSecurityException {
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted);
    }
    
    public static String decrypt(String message) throws GeneralSecurityException {
        SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(key), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
        return new String(decrypted);
    }
    
    private static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
   
    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }
}
