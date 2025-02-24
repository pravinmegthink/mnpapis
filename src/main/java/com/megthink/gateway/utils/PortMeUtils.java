package com.megthink.gateway.utils;

import com.megthink.gateway.api.response.PortMeAPIResponse;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;

public class PortMeUtils {

	static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	final private static Random rng = new SecureRandom();

	public static boolean validateRequestParam(String jsonSubs) {
		if (jsonSubs == null || jsonSubs.trim().equals("")) {
			return false;
		} else {
			return true;
		}

	}

	public static String generateJsonResponse(PortMeAPIResponse bean, String operation) {
		String jsonMessage = new Gson().toJson(bean);
		return jsonMessage;
	}

	public static Map<String, String> convertJsonToMap(String json) throws ParseException {
		//String regExStr = "[a-zA-Z0-9 ]*$";
		JSONParser parser = new JSONParser();
		Map<String, String> details = new HashMap<String, String>();
		Object obj;
		obj = parser.parse(json);
		JSONObject jsonObject = (JSONObject) obj;
		Set<String> keySet = jsonObject.keySet();
		for (String key : keySet) {
			String value = (String) jsonObject.get(key);
			value = value.trim();
			details.put(key, value);
		}
		return details;
	}

	static char randomChar() {
		return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
	}

	public static String randomUUID(int length, int spacing, char spacerChar) {
		StringBuilder sb = new StringBuilder();
		int spacer = 0;
		while (length > 0) {
			if (spacer == spacing) {
				sb.append(spacerChar);
				spacer = 0;
			}
			length--;
			spacer++;
			sb.append(randomChar());
		}
		return sb.toString();
	}

	static final private String DIGIT = "0123456789";
	final private static Random random = new SecureRandom();

	static char randomDIGIT() {
		return DIGIT.charAt(random.nextInt(DIGIT.length()));
	}

	public static String randomUniqueUUID() {
		int length = 14;
		int spacing = 0;
		char spacerChar = '1';
		StringBuilder sb = new StringBuilder();
		int spacer = 0;
		while (length > 0) {
			if (spacer == spacing) {
				sb.append(spacerChar);
				spacer = 0;
			}
			length--;
			spacer++;
			sb.append(randomDIGIT());
		}
		return sb.toString();
	}
}
