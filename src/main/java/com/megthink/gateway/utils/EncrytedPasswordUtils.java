package com.megthink.gateway.utils;

import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedPasswordUtils {
 
    // Encryte Password with BCryptPasswordEncoder
    public static String encrytePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
 
    public static void main(String[] args) {
    	//String originalData="O9qwp&uv";    
    	String originalData="WO6je7sfS";
    	byte[] encodedBytes = Base64.getEncoder().encode(originalData.getBytes());

        // Convert the encoded bytes to a Base64 string
        String encodedString = new String(encodedBytes);
    	byte[] actualByte =Base64.getDecoder().decode(encodedString);
    	
		String password = new String(actualByte);
       // String password = "portdb";
       // String encrytedPassword = encrytePassword(password);
        System.out.println("Encryted Password: " + encodedString);
        System.out.println("Dncryted Password: " + password);
    }

}
