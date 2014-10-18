package org.metalisx.common.gson;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Date;

import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        Gson gson = builder.create();
        return gson.toJson(object);
    }

    public <T> T fromJson(String object, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeHtml5Base64());
        Gson gson = builder.create();
        return (T) gson.fromJson(object, clazz);
    }

    public <T> T fromJson(String object, Type type) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeHtml5Base64());
    	Gson gson = builder.create();
    	return gson.fromJson(object, type);
    }
    
    public <T> T fromJson(InputStreamReader inputStreamReader, Type type) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDeserializeDate());
        builder.registerTypeAdapter(byte[].class, new GsonDeserializeHtml5Base64());
    	Gson gson = builder.create();
    	return gson.fromJson(inputStreamReader, type);
    }
    
}
