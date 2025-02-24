package com.megthink.gateway.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadConfigFile {

	public static Properties getProperties() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(System.getProperty("user.home") + "/app-config/mnp1/config.properties")) {
            prop.load(input);                                                 // C:\Users\Pravin\app-config
        } catch (IOException ex) {
            throw new RuntimeException("Error reading config properties: " + ex.getMessage(), ex);
        }
        return prop;
    }
}
