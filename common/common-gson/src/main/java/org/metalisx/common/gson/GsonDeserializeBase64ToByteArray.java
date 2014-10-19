package org.metalisx.common.gson;

import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link JsonDeserializer} adapter for handling a JSON base64 string.
 * 
 * This adapter handles base64 input and converts it to a byte[] for
 * use in the JPA persistence.
 */
public class GsonDeserializeBase64ToByteArray implements JsonDeserializer<byte[]> {

	@Override
	public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		if (json.getAsString() == null || "".equals(json.getAsString())) {
			return null;
		} else {
			return base64ToByteArray(json.getAsString());
		}
	}

	private byte[] base64ToByteArray(String value) {
		byte[] data = null;
		String base64 = null;
		// First we check if there is a base64 marker in the value.
		int index = value.indexOf("base64");
		if (index != -1) {
			base64 = value.substring(index + 7);
		} else { 
			base64 = value;
		}
		data = Base64.decodeBase64(base64.getBytes());
		if (data == null) {
			throw new JsonParseException("Could not parse base64 data.");
		}
		return data;
	}

}
