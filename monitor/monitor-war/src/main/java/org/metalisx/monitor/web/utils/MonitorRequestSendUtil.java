package org.metalisx.monitor.web.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorRequestCookie;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameter;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameterValue;
import org.metalisx.monitor.domain.model.MonitorRequestPart;
import org.metalisx.utils.HttpUtils;

/**
 * Crude implementation of sending a request from a {@link MoitorRequest}.
 */
public class MonitorRequestSendUtil {

    private static final String METHOD_NAME_POST = "POST";
    private static final String METHOD_NAME_GET = "GET";
    private static final String METHOD_NAME_PUT = "PUT";
    private static final String METHOD_NAME_DELETE = "DELETE";
    private static final String METHOD_NAME_HEAD = "HEAD";
    
    private static final ContentType contentType = ContentType.TEXT_PLAIN;
    private static final String characterSet = "UTF-8";

    private MonitorRequestSendUtil() {
    }

    public static String send(MonitorRequest monitorRequest) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        BasicHttpContext basicHttpContext = toBasicHttpContext(monitorRequest);
        String result = null;
        try {
            HttpRequestBase httpRequestBase = toHttpRequestBase(monitorRequest);
            CloseableHttpResponse response = httpClient.execute(httpRequestBase, basicHttpContext);
           	try {
           		result = response.getStatusLine().toString();
           	} finally {
           		response.close();
           	}
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (ClientProtocolException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
        	try {
				httpClient.close();
			} catch (IOException e) {
	            throw new IllegalStateException(e);
			}
        }
        return result;
    }

    private static HttpRequestBase toHttpRequestBase(MonitorRequest monitorRequest)
            throws UnsupportedEncodingException, URISyntaxException {
        HttpRequestBase httpRequestBase;
        if (METHOD_NAME_POST.equals(monitorRequest.getMethod().toUpperCase())) {
            httpRequestBase = toHttpPost(monitorRequest);
        } else if (METHOD_NAME_GET.equals(monitorRequest.getMethod().toUpperCase())) {
            httpRequestBase = new HttpGet();
        } else if (METHOD_NAME_PUT.equals(monitorRequest.getMethod().toUpperCase())) {
            httpRequestBase = toHttpPut(monitorRequest);
        } else if (METHOD_NAME_DELETE.equals(monitorRequest.getMethod().toUpperCase())) {
            httpRequestBase = new HttpDelete();
        } else if (METHOD_NAME_HEAD.equals(monitorRequest.getMethod().toUpperCase())) {
            httpRequestBase = new HttpHead();
        } else {
            throw new IllegalStateException("No implementation for HTTP method " + monitorRequest.getMethod());
        }
        String url = monitorRequest.getUrl().replaceAll(" ","%20");
        httpRequestBase.setURI(new URI(url));
        // setHeaders(httpRequestBase, monitorRequest);
        return httpRequestBase;
    }

    /**
     * The cookies are only send if the domain is set. Because of this the
     * following failsafe is implemented: if the domain is null then the domain
     * is set to the servername.
     */
    private static BasicHttpContext toBasicHttpContext(MonitorRequest monitorRequest) {
        BasicHttpContext basicHttpContext = new BasicHttpContext();
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        for (MonitorRequestCookie monitorRequestCookie : monitorRequest.getCookies()) {
            BasicClientCookie cookie = new BasicClientCookie(monitorRequestCookie.getName(),
                    monitorRequestCookie.getValue());
            if (monitorRequestCookie.getDomain() != null) {
                cookie.setDomain(monitorRequestCookie.getDomain());
            } else {
                cookie.setDomain(monitorRequest.getServerName());
            }
            if (monitorRequestCookie.getMaxAge() != -1) {
                cookie.setExpiryDate(new Date(System.currentTimeMillis() + monitorRequestCookie.getMaxAge() * 1000L));
            }
            cookie.setPath(monitorRequestCookie.getPath());
            cookie.setSecure(monitorRequestCookie.getSecure());
            cookie.setVersion(monitorRequestCookie.getVersion());
            basicCookieStore.addCookie(cookie);
        }
        basicHttpContext.setAttribute(HttpClientContext.COOKIE_STORE, basicCookieStore);
        return basicHttpContext;
    }

    private static HttpPost toHttpPost(MonitorRequest monitorRequest) throws UnsupportedEncodingException {
        HttpPost httpPost;
        if (HttpUtils.isMultipart(monitorRequest.getContentType())) {
            httpPost = toMultipartHttpPost(monitorRequest);
        } else if (HttpUtils.isForm(monitorRequest.getContentType())
                && monitorRequest.getFormParameters() != null) {
            httpPost = toFormHttpPost(monitorRequest);
        } else {
            httpPost = toBasicHttpPost(monitorRequest);
        }
        return httpPost;
    }

    private static HttpPost toMultipartHttpPost(MonitorRequest monitorRequest) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        for (MonitorRequestPart monitorRequestPart : monitorRequest.getParts()) {
            if (monitorRequestPart.getFilename() == null) {
                multipartEntityBuilder.addTextBody(monitorRequestPart.getName(), monitorRequestPart.getValue(), contentType);
            } else {
                multipartEntityBuilder.addBinaryBody(monitorRequestPart.getName(), monitorRequestPart.getContent(),
                        ContentType.parse(monitorRequestPart.getContentType()), monitorRequestPart.getFilename());
            }
        }
        httpPost.setEntity(multipartEntityBuilder.build());
        return httpPost;
    }

    private static HttpPost toFormHttpPost(MonitorRequest monitorRequest) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost();
        List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        for (MonitorRequestFormParameter monitorRequestFormParameter : monitorRequest.getFormParameters()) {
            for (MonitorRequestFormParameterValue monitorRequestFormParameterValue : monitorRequestFormParameter
                    .getValues()) {
                valuePairs.add(new BasicNameValuePair(monitorRequestFormParameter.getName(),
                        monitorRequestFormParameterValue.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(valuePairs, characterSet));
        return httpPost;
    }

    private static HttpPost toBasicHttpPost(MonitorRequest monitorRequest) {
        HttpPost httpPost = new HttpPost();
        AbstractHttpEntity abstractHttpEntity = new ByteArrayEntity(monitorRequest.getContent());
        abstractHttpEntity.setContentType(monitorRequest.getContentType());
        abstractHttpEntity.setContentEncoding(monitorRequest.getCharacterEncoding());
        httpPost.setEntity(abstractHttpEntity);
        return httpPost;
    }

    private static HttpPut toHttpPut(MonitorRequest monitorRequest) throws URISyntaxException {
        HttpPut httpPut = new HttpPut();
        AbstractHttpEntity abstractHttpEntity = new ByteArrayEntity(monitorRequest.getContent());
        abstractHttpEntity.setContentType(monitorRequest.getContentType());
        abstractHttpEntity.setContentEncoding(monitorRequest.getCharacterEncoding());
        httpPut.setEntity(abstractHttpEntity);
        return httpPut;
    }

    /*
    private static void setHeaders(HttpRequestBase httpRequestBase, MonitorRequest monitorRequest) {
        for (MonitorRequestHeader monitorRequestHeader : monitorRequest.getHeaders()) {
            if (!monitorRequestHeader.getValue().toLowerCase().contains("content-length")) {
                httpRequestBase.setHeader(monitorRequestHeader.getName(), monitorRequestHeader.getValue());
            }
        }
    }
    */

}
