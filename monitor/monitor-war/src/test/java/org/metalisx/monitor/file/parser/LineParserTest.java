package org.metalisx.monitor.file.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.metalisx.monitor.domain.model.MonitorLog;

public class LineParserTest {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    @Test
    public void parseTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: 9517C5C58183FDBF58A9E534D1EB6592, RequestId: fe806651-e0bb-4ac4-993b-7c6bfeaccf22, ParentRequestId: ha516455-h8ca-3xa8-267m-3k4cjqoyun43, Organization: Organization A, Username: User A] (Depth: 3) doFilter(): url: http://localhost:8080/some-context/index.xhtml, time: 386ms";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals("9517C5C58183FDBF58A9E534D1EB6592", monitorLog.getSessionId());
        assertEquals("fe806651-e0bb-4ac4-993b-7c6bfeaccf22", monitorLog.getRequestId());
        assertEquals("ha516455-h8ca-3xa8-267m-3k4cjqoyun43", monitorLog.getParentRequestId());
        assertEquals("Organization A", monitorLog.getOrganization());
        assertEquals("User A", monitorLog.getUsername());
        assertEquals(new Integer(3), monitorLog.getDepth());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(386L, monitorLog.getDuration());
    }

    @Test
    public void parseLegacyOrganisatieAndLegacyGebruikersnaamTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: 9517C5C58183FDBF58A9E534D1EB6592, RequestId: fe806651-e0bb-4ac4-993b-7c6bfeaccf22, ParentRequestId: ha516455-h8ca-3xa8-267m-3k4cjqoyun43, Organisatie: Organization A, Gebruikersnaam: User A] (Depth: 3) doFilter(): url: http://localhost:8080/some-context/index.xhtml, time: 386ms";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals("9517C5C58183FDBF58A9E534D1EB6592", monitorLog.getSessionId());
        assertEquals("fe806651-e0bb-4ac4-993b-7c6bfeaccf22", monitorLog.getRequestId());
        assertEquals("ha516455-h8ca-3xa8-267m-3k4cjqoyun43", monitorLog.getParentRequestId());
        assertEquals("Organization A", monitorLog.getOrganization());
        assertEquals("User A", monitorLog.getUsername());
        assertEquals(new Integer(3), monitorLog.getDepth());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(386L, monitorLog.getDuration());
    }

    @Test
    public void parseWithoutParentRequestIdTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: 9517C5C58183FDBF58A9E534D1EB6592, RequestId: fe806651-e0bb-4ac4-993b-7c6bfeaccf22, Organization: Organization A, Username: User A] (Depth: 3) doFilter(): url: http://localhost:8080/some-context/index.xhtml, time: 386ms";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals("9517C5C58183FDBF58A9E534D1EB6592", monitorLog.getSessionId());
        assertEquals("fe806651-e0bb-4ac4-993b-7c6bfeaccf22", monitorLog.getRequestId());
        assertEquals("Organization A", monitorLog.getOrganization());
        assertEquals("User A", monitorLog.getUsername());
        assertEquals(new Integer(3), monitorLog.getDepth());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(386L, monitorLog.getDuration());
    }

    @Test
    public void parseWithoutTimeTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: 9517C5C58183FDBF58A9E534D1EB6592, RequestId: fe806651-e0bb-4ac4-993b-7c6bfeaccf22, ParentRequestId: ha516455-h8ca-3xa8-267m-3k4cjqoyun43, Organization: Organization A, Username: User A] (Depth: 3) doFilter(): url: http://localhost:8080/some-context/index.xhtml";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals("9517C5C58183FDBF58A9E534D1EB6592", monitorLog.getSessionId());
        assertEquals("fe806651-e0bb-4ac4-993b-7c6bfeaccf22", monitorLog.getRequestId());
        assertEquals("Organization A", monitorLog.getOrganization());
        assertEquals("User A", monitorLog.getUsername());
        assertEquals(new Integer(3), monitorLog.getDepth());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(0L, monitorLog.getDuration());
    }

    @Test
    public void parseWithoutDepthTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: 9517C5C58183FDBF58A9E534D1EB6592, RequestId: fe806651-e0bb-4ac4-993b-7c6bfeaccf22, ParentRequestId: ha516455-h8ca-3xa8-267m-3k4cjqoyun43, Organization: Organization A, Username: User A] doFilter(): url: http://localhost:8080/some-context/index.xhtml, time: 386ms";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals("9517C5C58183FDBF58A9E534D1EB6592", monitorLog.getSessionId());
        assertEquals("fe806651-e0bb-4ac4-993b-7c6bfeaccf22", monitorLog.getRequestId());
        assertEquals("Organization A", monitorLog.getOrganization());
        assertEquals("User A", monitorLog.getUsername());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(new Integer(1), monitorLog.getDepth());
        assertEquals(386L, monitorLog.getDuration());
    }

    @Test
    public void parseEmptyFieldsTest() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) [SessionId: , RequestId: , ParentRequestId: , Organization: , Username: ] (Depth: ) doFilter(): url: http://localhost:8080/some-context/index.xhtml, time: 386ms";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNotNull(monitorLog);
        assertEquals(formatter.parse("2012-06-24 13:43:25,807"), monitorLog.getLogDate());
        assertEquals("DEBUG", monitorLog.getLogLevel());
        assertEquals("nl.some.package.name.SomeClassName", monitorLog.getLogClass());
        assertEquals("http-localhost%2F127.0.0.1-8080-5", monitorLog.getThread());
        assertEquals(null, monitorLog.getSessionId());
        assertEquals(null, monitorLog.getRequestId());
        assertEquals(null, monitorLog.getParentRequestId());
        assertEquals(null, monitorLog.getOrganization());
        assertEquals(null, monitorLog.getUsername());
        assertEquals("doFilter(): url: http://localhost:8080/some-context/index.xhtml", monitorLog.getMessage());
        assertEquals(new Integer(1), monitorLog.getDepth());
        assertEquals(386L, monitorLog.getDuration());
    }

    @Test
    public void skipInvalidLineTest() throws ParseException {
        String line = "2012-06-24 13:43:25,807 DEBUG [nl.some.package.name.SomeClassName] (http-localhost%2F127.0.0.1-8080-5) some other log line";
        LineParser lineParser = new LineParser();
        MonitorLog monitorLog = lineParser.parse(line);
        assertNull(monitorLog);
    }
}
