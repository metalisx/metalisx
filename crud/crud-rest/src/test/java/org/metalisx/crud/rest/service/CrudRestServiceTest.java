package org.metalisx.crud.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipException;

import javax.ws.rs.ApplicationPath;

import org.apache.http.client.ClientProtocolException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.crud.domain.model.User;
import org.metalisx.crud.rest.application.RestApplication;
import org.metalisx.crud.rest.client.AbstractRestClient;
import org.metalisx.crud.rest.dto.EntitiesDto;
import org.metalisx.crud.rest.dto.EntityDto;
import org.metalisx.crud.rest.dto.EntityFieldDto;

@RunAsClient
@RunWith(Arquillian.class)
public class CrudRestServiceTest {

	private static final String REST_PATH = RestApplication.class.getAnnotation(ApplicationPath.class).value()
	        .substring(1)
	        + "/crud";

	@ArquillianResource
	private URL deploymentUrl;

	private AbstractRestClient<User, Long> restClient;

	public CrudRestServiceTest() {
	}

	@Deployment(testable = false)
	public static Archive<?> createTestArchive() throws ZipException, IOException {
		return Deployments.createDeployment();
	}

	@Before
	public void before() {
		restClient = new AbstractRestClient<User, Long>(deploymentUrl.toString() + REST_PATH) {
		};
	}

	@Test
	@InSequence(1)
	public void addUser() throws ClientProtocolException, IOException {
		try {
	        Thread.sleep(5000);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		User user = new User();
		user.setName("Bruce");
		User userResult = restClient.put(user);
		assertNotNull(userResult);
		assertEquals(2, userResult.getId().intValue());
		assertEquals("Bruce", userResult.getName());
	}

	@Test
	@InSequence(2)
	@SuppressWarnings("unchecked")
	public void getUser() throws Exception {
		EntityDto entityDto = restClient.get(2L);
		HashMap<String, Object> item = (HashMap<String, Object>) entityDto.getItem();
		assertNotNull(entityDto.getMetadata());
		List<EntityFieldDto> entityFieldDtos = entityDto.getMetadata().getFields();
		assertNotNull(entityFieldDtos);
		assertEquals(2, entityFieldDtos.size());
		assertEquals(0, entityFieldDtos.get(0).getIndex());
		assertEquals("id", entityFieldDtos.get(0).getName());
		assertEquals(1, entityFieldDtos.get(1).getIndex());
		assertEquals("name", entityFieldDtos.get(1).getName());
		assertNotNull(entityDto);
		assertEquals(2, ((Double) item.get("id")).intValue());
		assertEquals("Bruce", item.get("name"));
	}

	@Test
	@InSequence(3)
	public void deleteUser() throws ClientProtocolException, IOException {
		Long id = restClient.delete(2L);
		assertEquals(2, id.intValue());
		EntityDto entityDto = restClient.get(2L);
		assertNotNull(entityDto);
		assertNull(entityDto.getItem());
	}

	@Test
	@InSequence(4)
	@SuppressWarnings("unchecked")
	public void getUsers() throws ClientProtocolException, IOException {
		EntitiesDto entitiesDto = restClient.get();
		assertNotNull(entitiesDto);
		assertNotNull(entitiesDto.getItems());
		ArrayList<Object> items = (ArrayList<Object>) entitiesDto.getItems();
		assertEquals(1, items.size());
		HashMap<String, Object> item0 = (HashMap<String, Object>) items.get(0);
		assertEquals(1, ((Double) item0.get("id")).intValue());
		assertEquals("Peter Parker", item0.get("name"));
	}

}
