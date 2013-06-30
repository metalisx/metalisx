package org.metalisx.monitor.context;

import org.junit.Assert;
import org.junit.Test;
import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;

public class MonitorContextSimpleTest {

	@Test
	public void testMonitorContext() {
		String key = "theKey";
		InterfaceMonitorContext monitorContextSimple = MonitorContextFactory.getInstance(key);
		Assert.assertNotNull(MonitorContextFactory.getCurrentInstance());
		monitorContextSimple.setSessionId("a-sessionid-1");
		monitorContextSimple.setRequestId("a-requestid-1");
		monitorContextSimple.setParentRequestId("a-parentrequest-id-1");
		monitorContextSimple.setOrganization("Daily Bugle");
		monitorContextSimple.setUsername("Peter");
		monitorContextSimple.increaseDepth();
		String expected = "[SessionId: a-sessionid-1, RequestId: a-requestid-1, ParentRequestId: a-parentrequest-id-1, Organisatie: Daily Bugle, Gebruikersnaam: Peter] (Depth: 2) A message, time: 1ms";
		Assert.assertEquals(expected, monitorContextSimple.profileFormat("A message", 1));
		monitorContextSimple.decreaseDepth();
		Assert.assertEquals(1, monitorContextSimple.getDepth().intValue());
		MonitorContextFactory.clear(key);
		Assert.assertNull(MonitorContextFactory.getCurrentInstance());
	}

	@Test
	public void testMonitorContextNullFields() {
		String key = "theKey";
		InterfaceMonitorContext monitorContextSimple = MonitorContextFactory.getInstance(key);
		monitorContextSimple.setRequestId("a-requestid-1");
		String expected = "[SessionId: , RequestId: a-requestid-1, ParentRequestId: , Organisatie: , Gebruikersnaam: ] (Depth: 1) A message, time: 1ms";
		Assert.assertEquals(expected, monitorContextSimple.profileFormat("A message", 1));
	}

}
