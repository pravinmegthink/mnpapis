package com.megthink.gateway.xmlconverter;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalXMLReader {

	private static final Logger portmeLog = LoggerFactory.getLogger(GlobalXMLReader.class);

	public Map readConfig(String fileLocation) throws FileNotFoundException, IOException {

		FileReader inputStream = null;

		try {
			Map configurations = new HashMap();
			Map schemas = new HashMap();
			Map selectors = new HashMap();

			portmeLog.info("readConfig() - reading config file [" + fileLocation + "]");

			inputStream = new FileReader(fileLocation);
			BufferedReader reader = new BufferedReader(inputStream);

			String readed = read(reader).trim();

			while (readed != null) {

				if (readed.startsWith("@open_msg")) {
					String schema = "";
					StringTokenizer tokenizer = new StringTokenizer(readed, " ");

					if (tokenizer.countTokens() != 2) {
						portmeLog.error(
								"readConfig() - [Invalid Configuration] invalid schema annotation (start) : " + readed);
					} else {
						tokenizer.nextToken();
						String schemaName = tokenizer.nextToken();

						readed = read(reader).trim(); // read content

						while (!readed.equals("@close_msg " + schemaName)) {

							if (readed.startsWith("@close_msg") && !readed.equals("@close_msg " + schemaName)) {
								portmeLog.error("readConfig() - [Invalid Configuration] schemaname [" + schemaName
										+ "] invalid schema annotation (end) : " + readed + " expected : @close_msg "
										+ schemaName);
								break;
							}

							if (readed.trim().startsWith("+")) {
								if (schemas.get(readed.trim().substring(1, readed.trim().length())) == null) {
									portmeLog.error("readConfig() - [Invalid Configuration] schemaname [" + schemaName
											+ "] unable to get nested schema : " + readed);
								} else {
									readed = (String) schemas.get(readed.trim().substring(1, readed.trim().length()));
								}
							}

							schema = schema + readed + "\n";
							readed = read(reader).trim();
						}

						if (schemaName == null || schemaName.length() == 0) {
							portmeLog.error("readConfig() - [Invalid Configuration] schemaname [" + schemaName
									+ "] invalid schema name");
						} else if (schema == null || schema.length() == 0) {
							portmeLog.error("readConfig() - [Invalid Configuration] schemaname [" + schemaName
									+ "] invalid schema content : [" + schema + "]");
						} else {
							schemas.put(schemaName, schema);
							portmeLog.info("readConfig() - Got schema [" + schemaName + "] schema ["
									+ GlobalXMLGenerator.replaceAll(schema, "\n", "") + "]");
						}
					}
				} else if (readed.startsWith("@defin_open")) {
					String selector = "";
					StringTokenizer tokenizer = new StringTokenizer(readed, " ");

					if (tokenizer.countTokens() != 2) {
						portmeLog.error("readConfig() - [Invalid Configuration] invalid selector annotation (start) : "
								+ readed);
					} else {

						tokenizer.nextToken();
						String selectorName = tokenizer.nextToken();

						readed = read(reader).trim(); // read content

						while (!readed.equals("@defin_close " + selectorName)) {

							if (readed.startsWith("@defin_close") && !readed.equals("@defin_close " + selectorName)) {
								portmeLog.error("readConfig() - [Invalid Configuration] selectorName [" + selectorName
										+ "] invalid selector annotation (end) : " + readed
										+ " expected : @defin_close " + selectorName);
								break;
							}

							selector = selector + readed + "\n";
							readed = read(reader).trim();
						}

						if (selectorName == null || selectorName.length() == 0) {
							portmeLog.error("readConfig() - [Invalid Configuration] selectorName [" + selectorName
									+ "] invalid selector name");
						} else if (selector == null || selector.length() == 0) {
							portmeLog.error("readConfig() - [Invalid Configuration] selectorName [" + selectorName
									+ "] invalid selector content : [" + selector + "]");
						} else {
							selectors.put(selectorName, selector);
							portmeLog.info("readConfig() - Got selector [" + selectorName + "] schema ["
									+ GlobalXMLGenerator.replaceAll(selector, "\n", "") + "]");
						}
					}
				}

				readed = read(reader);
			}

			inputStream.close();

			configurations.put("selectors", selectors);
			configurations.put("schemas", schemas);

			if (selectors == null || selectors.size() == 0) {
				portmeLog.warn("readConfig() [Invalid Configuration] - no valid selector found in the config file "
						+ fileLocation + "]");
			}

			if (schemas == null || schemas.size() == 0) {
				portmeLog.warn("readConfig() [Invalid Configuration] - no valid schema found in the config file "
						+ fileLocation + "]");
			}

			return configurations;

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	String read(BufferedReader reader) throws IOException {

		String readed = reader.readLine();

		while (readed != null && readed.trim().startsWith("//")) {
			readed = reader.readLine();
		}

		return readed;
	}
}
