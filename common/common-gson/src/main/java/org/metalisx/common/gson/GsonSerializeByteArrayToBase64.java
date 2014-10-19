package org.metalisx.common.gson;

import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link JsonDeserializer} adapter for handling a JSON base64 string.
 * 
 * This adapter handles base64 input and converts it to a byte[] for
 * use in the JPA persistence.
 */
public class GsonSerializeByteArrayToBase64 implements JsonSerializer<byte[]> {

	@Override
	public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
		if (src == null) {
			return null;
		} else {
			return new JsonPrimitive(byteArrayToBase64(src));
		}
	}

	private String byteArrayToBase64(byte[] value) {
		String data = Base64.encodeBase64String(value);
		if (data == null) {
			throw new JsonParseException("Could not parse byte array to base64 data.");
		}
		return data;
	}

}
