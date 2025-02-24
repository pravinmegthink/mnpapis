package com.megthink.gateway.xmlconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFactory {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesFactory.class);
	
	private final String CORE_CONF_TAG = "com.megthink.portme.ConfigD";
	private String coreConfDir = null;

	public PropertiesFactory() {
		coreConfDir = System.getProperty(CORE_CONF_TAG);
		if (coreConfDir==null) {
			coreConfDir = System.getProperty("user.home") + File.separator + "config";
			logger.warn("PropertiesFactory() - System Property ["+CORE_CONF_TAG+"] is not defined, default directory ["+coreConfDir+"] will be used.");
		}
	}

	public static PropertiesFactory getInstance() {
		return PropertiesFactoryHolder.instance;
	}

	/**
	 * get the value of coreConfDir
	 * @return the value of coreConfDir
	 */
	public String getCoreConfDir(){
		return this.coreConfDir;
	}

	public Properties getProperties(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException(getClass().getName()+".getProperties(String filename) must be given a filename!");
		}
		File file = null;
		Properties result = null;

		file = new File(coreConfDir,filename);
		result = new Properties();
		try {
			result.load(new FileInputStream(file));
		} catch(FileNotFoundException fnfe) {
			logger.error("getProperties() - ["+file.getAbsolutePath()+"] is not found.", fnfe);
		} catch(IOException ioe) {
			logger.error("getProperties() - Fail to load ["+file.getAbsolutePath()+"].", ioe);
		}
		return result;
	}

	public Properties getPropertiesFromXml(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException(getClass().getName()+".getPropertiesFromXml(String filename) must be given a filename!");
		}
		File file = null;
		Properties result = null;

		file = new File(coreConfDir,filename);
		result = new Properties();
		try {
			result.loadFromXML(new FileInputStream(file));
		} catch(FileNotFoundException fnfe) {
			logger.error("getPropertiesFromXml() - ["+file.getAbsolutePath()+"] is not found.", fnfe);
		} catch(IOException ioe) {
			logger.error("getPropertiesFromXml() - Fail to load ["+file.getAbsolutePath()+"].", ioe);
		}
		return result;
	}

	private static class PropertiesFactoryHolder {
		static final PropertiesFactory instance = new PropertiesFactory();
	}
}
