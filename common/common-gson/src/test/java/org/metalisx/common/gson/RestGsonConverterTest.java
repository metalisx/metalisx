package org.metalisx.common.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.junit.Test;
import org.metalisx.common.domain.model.User;
import org.metalisx.common.domain.model.UserWrapper;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class RestGsonConverterTest {

	private RestGsonConverter restGsonConverter = new RestGsonConverter();
	
    public RestGsonConverterTest() {
    }

    @Test
    public void toJsonWithString() {
    	User user = new User(1L, "A name");
    	String userJson = restGsonConverter.toJson(user);
    	assertEquals(userJson, "{\"id\":1,\"name\":\"A name\"}");
    }
    	
    @Test
    public void toJsonWithType() {
    	User user = new User(1L, "A name");
    	UserWrapper<User> userWrapper = new UserWrapper<User>(user);
    	String userJsonWrapper = restGsonConverter.toJson(userWrapper);
    	assertEquals(userJsonWrapper, "{\"user\":{\"id\":1,\"name\":\"A name\"}}");
    }

    @Test
    public void fromJsonWithStringAndClass() {
    	String userJson = "{\"id\":1,\"name\":\"A name\"}";
    	User user = restGsonConverter.fromJson(userJson, User.class);
    	assertEquals(1L, user.getId().longValue());
    	assertEquals("A name", user.getName());
    }

	@SuppressWarnings("unchecked")
	@Test
    public void fromJsonWithStringAndClassMappedToLinkedTreeMap() {
    	String userJsonWrapper = "{\"user\":{\"id\":1,\"name\":\"A name\"}}";
    	UserWrapper<Object> userWrapper = (UserWrapper<Object>) restGsonConverter.fromJson(userJsonWrapper, UserWrapper.class);
    	assertNotNull(userWrapper.getUser());
		LinkedTreeMap<String, Object> user = (LinkedTreeMap<String, Object>) userWrapper.getUser();
    	assertEquals(1L, ((Double) user.get("id")).longValue());
    	assertEquals("A name", user.get("name"));
    }
    
    @Test
    public void fromJsonWithStringAndType() {
    	String userJsonWrapper = "{\"user\":{\"id\":1,\"name\":\"A name\"}}";
    	Type userWrapperType = new TypeToken<UserWrapper<User>>(){}.getType();
    	UserWrapper<User> userWrapper = restGsonConverter.fromJson(userJsonWrapper, userWrapperType);
    	assertNotNull(userWrapper.getUser());
    	assertEquals(1L, userWrapper.getUser().getId().longValue());
    	assertEquals("A name", userWrapper.getUser().getName());
    }
    
    @Test
    public void fromJsonWithInputStreamReaderAndType() {
    	String userJsonWrapper = "{\"user\":{\"id\":1,\"name\":\"A name\"}}";
    	Type userWrapperType = new TypeToken<UserWrapper<User>>(){}.getType();
    	InputStream inputStream = new ByteArrayInputStream(userJsonWrapper.getBytes());
    	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    	UserWrapper<User> userWrapper = restGsonConverter.fromJson(inputStreamReader, userWrapperType);
    	assertNotNull(userWrapper.getUser());
    	assertEquals(1L, userWrapper.getUser().getId().longValue());
    	assertEquals("A name", userWrapper.getUser().getName());
    }
    
}
