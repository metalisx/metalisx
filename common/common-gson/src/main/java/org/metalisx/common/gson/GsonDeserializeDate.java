package org.metalisx.common.gson;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link JsonDeserializer} adapter for handling a JSON date string.
 * 
 * This adapter handles multiple date formats as input. The adapter also needs
 * to process dates with an empty string as value because the default adapter
 * can only process null values and not empty string values.
 * 
 * @author Stefan.Oude.Nijhuis
 */
public class GsonDeserializeDate implements JsonDeserializer<Date> {

	private static List<String> dateFormatsFromJson;

	static {
		dateFormatsFromJson = new ArrayList<String>();
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mm:ssZ");
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mm:ss");
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mmZ");
		dateFormatsFromJson.add("yyyy-MM-dd'T'HH:mm");
	}

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		if (json.getAsString() == null || "".equals(json.getAsString())) {
			return null;
		} else {
			return dateFromJson(json.getAsString());
		}
	}

	private Date dateFromJson(String value) {
		Date date = null;
		for (String format : dateFormatsFromJson) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(format);
				date = dateFormat.parse(value);
				break;
			} catch (ParseException e) {
				// silent
			}
		}
		if (date == null) {
			try {
				date = new Date(Long.valueOf(value));
			} catch (Exception e) {
				// silent
				e.printStackTrace();
			}
		}
		if (date == null) {
			throw new JsonParseException("Could not parse date parameter " + value + "." + " Valid formats are number or "
			        + formatsToString() + ".");
		}
		return date;
	}

	private String formatsToString() {
		String value = "";
		for (String format : dateFormatsFromJson) {
			value = "".equals(value) ? format : value + ", " + format;
		}
		return value;
	}

}
