package org.metalisx.common.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.metalisx.common.gson.RestGsonConverter;
import org.metalisx.common.rest.dto.entity.EntitiesDto;
import org.metalisx.common.rest.dto.entity.EntityDto;

/**
 * Abstract class to initiate a HttpClient for simple rest communication.
 * 
 * It retrieves the runtime class from the generic type. The constructor
 * parameter listType is so GSON can create a list of the runtime class. The
 * constructor parameter endpoint points to an URL handling REST operations for
 * the runtime class and listType.
 * 
 * When the instance of this class is not relevant anymore, the
 * {@link #shutdown()} method should be called to close and free all resources.
 */
public abstract class AbstractRestClient<T, I> {

	private RestGsonConverter restGsonConverter = new RestGsonConverter();

	private String endpoint;
	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public AbstractRestClient(String endpoint) {
		this.endpoint = endpoint;
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public EntitiesDto get() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(endpoint + "/" + entityClass.getName());
		String body = execute(httpGet);
		return restGsonConverter.fromJson(body, EntitiesDto.class);
	}

	public EntityDto get(I id) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(endpoint + "/" + entityClass.getName() + "/" + String.valueOf(id));
		String responseBody = execute(httpGet);
		return restGsonConverter.fromJson(responseBody, EntityDto.class);
	}

	public T put(T entity) throws ClientProtocolException, IOException {
		HttpPut httpPut = new HttpPut(endpoint + "/" + entityClass.getName());
		httpPut.addHeader("Content-Type", "application/json");
		String body = restGsonConverter.toJson(entity);
		httpPut.setEntity(new StringEntity(body));
		String responseBody = execute(httpPut);
		return restGsonConverter.fromJson(responseBody, entityClass);
	}

	public Long delete(I id) throws ClientProtocolException, IOException {
		HttpDelete httpDelete = new HttpDelete(endpoint + "/" + entityClass.getName() + "/" + String.valueOf(id));
		String responseBody = execute(httpDelete);
		return restGsonConverter.fromJson(responseBody, Long.class);
	}

	private String execute(HttpRequestBase httpRequestBase) throws ClientProtocolException, IOException {
		return execute(httpRequestBase, null);
	}

	private String execute(HttpRequestBase httpRequestBase, T entity) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			httpRequestBase.addHeader("accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(httpRequestBase);
			try {
				if (response == null) {
					throw new IllegalStateException("The response is null.");
				}
				if (response.getStatusLine() == null) {
					throw new IllegalStateException("Status line in the response is null.");
				}
				if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
					throw new IllegalStateException("Response status is not 200 (OK) but was "
					        + response.getStatusLine().getStatusCode() + ". " + response.getStatusLine().getReasonPhrase());
				}
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				return stringBuilder.toString();
			} finally {
				response.close();
			}
		} finally{ 
			httpClient.close();
		}
	}
	
}
