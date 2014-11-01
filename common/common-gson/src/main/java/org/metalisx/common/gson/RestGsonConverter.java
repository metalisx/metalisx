package org.metalisx.common.gson;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Converter to serialize and deserialize Java objects to and from JSON objects.
 * It uses GSON for the convertion with extra serialize and deserialize
 * functionality for date and byte[]/base64 conversion.
 * 
 * @author Stefan.Oude.Nijhuis
 */
@Named
public class RestGsonConverter {

    private static final String DATE_FORMAT_TO_JSON = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public RestGsonConverter() {
    }

    public String toJson(Object object) {
        // serializeNulls Is required to generate all properties in the JSON
        // object.
        GsonBuilder builder = new GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT_TO_JSON);
        builder.setExclusionStrategies(new GsonAnnotationExclusionStrategy());
        builder.registerTypeAdapter(byte[].class, new GsonSerializeByteArrayToBase64());
        Gson gson = builder.create();
        return gson.toJson(object);
    }

    public <T> T fromJson(String object, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeBase64ToByteArray());
        Gson gson = builder.create();
        return (T) gson.fromJson(object, clazz);
    }

    public <T> T fromJson(String object, Type type) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeBase64ToByteArray());
    	Gson gson = builder.create();
    	return gson.fromJson(object, type);
    }
    
    public <T> T fromJson(InputStreamReader inputStreamReader, Type type) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeBase64ToByteArray());
    	Gson gson = builder.create();
    	return gson.fromJson(inputStreamReader, type);
    }
    
}
