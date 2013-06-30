package org.metalisx.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.ccil.cowan.tagsoup.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Utility class for pretty printing.
 * 
 * It support pretty printing for the content types for JSON (uses GSON), XML
 * and HTML (uses TagSoup).
 * 
 * The JSON and XML pretty printer are not messing with the content, the HTML
 * pretty printer does. The HTML pretty printer will fix malformed HTML code.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class PrettyPrintUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrettyPrintUtils.class);

    /**
     * Pretty print JSON.
     * 
     * @param bytes The byte array to pretty print.
     * @return Pretty printed string representation of the byte array.
     */
    public static String prettyPrintJson(byte[] bytes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(new String(bytes));
        return gson.toJson(jsonElement);
    }

    /**
     * Pretty print HTML.
     * 
     * @param bytes The byte array to pretty print.
     * @return Pretty printed string representation of the byte array.
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static String prettyPrintHtml(byte[] bytes) throws TransformerException {
        Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        XMLReader reader = new Parser();
        Source xmlSource = new SAXSource(reader, new InputSource(new ByteArrayInputStream(bytes)));
        StreamResult res = new StreamResult(new ByteArrayOutputStream());
        serializer.transform(xmlSource, res);
        return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
    }

    /**
     * Pretty print XML.
     * 
     * @param bytes The byte array to pretty print.
     * @return Pretty printed string representation of the byte array.
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    public static String prettyPrintXml(byte[] bytes) throws TransformerException {
        Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        // serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(bytes)));
        StreamResult res = new StreamResult(new ByteArrayOutputStream());
        serializer.transform(xmlSource, res);
        return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
    }

    /**
     * Pretty print Javascript. It is slow as it uses the embedded Javascript
     * engine.
     * 
     * @param bytes The byte array to pretty print.
     * @return Pretty printed string representation of the byte array.
     */
    public static String prettyPrintJavascript(byte[] bytes) {
        InputStream inputStream = PrettyPrintUtils.class.getResourceAsStream("/beautify.js");
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
        try {
            Reader reader = new InputStreamReader(inputStream);
            jsEngine.eval(reader);
            Invocable invocableEngine = (Invocable) jsEngine;
            return (String) invocableEngine.invokeFunction("js_beautify", new String(bytes));
        } catch (ScriptException e) {
            logger.error("Pretty print failed.", e);
        } catch (NoSuchMethodException e) {
            logger.error("Pretty print failed.", e);
        }
        return "";
    }

    /**
     * It will detect if the input <code>bytes</code> is a string or binary. If
     * it is a string it will detect the following: - javascript - XML When one
     * of the list is detected it will pretty print it.
     * 
     * @param contentType The content type
     * @param bytes The bytes to pretty print
     * @return A pretty print string
     */
    public static String prettyPrint(String contentType, byte[] bytes) {
        logger.debug("prettyPrint -> contentType = " + contentType);
        String result = null;
        if (contentType != null) {
            if (contentType.startsWith("application/json")) {
                result = prettyPrintJson(bytes);
            } else if (contentType.startsWith("text/javascript")) {
                result = prettyPrintJavascript(bytes);
            } else if (isXml(contentType)) {
                try {
                    result = prettyPrintXml(bytes);
                } catch (Exception e) {
                    logger.error("Pretty print failed.", e);
                }
            } else if (contentType.startsWith("text/html")) {
                try {
                    result = prettyPrintHtml(bytes);
                } catch (TransformerFactoryConfigurationError e) {
                    logger.error("Pretty print failed.", e);
                } catch (TransformerException e) {
                    logger.error("Pretty print failed.", e);
                }
            }
        }
        if (result == null) {
            result = new String(bytes);
        }
        return result;
    }

    private static boolean isXml(String contentType) {
        return contentType.startsWith("text/xml") || contentType.startsWith("application/xml")
                || contentType.startsWith("application/soap+xml");
    }

    /**
     * Returns if <code>contentType</code> is pretty printable.
     * 
     * @param contentType The content type
     * @return Returns true if the content type is pretty printable.
     */
    public static boolean isPrettyPrintable(String contentType) {
        if (contentType != null
                && (isXml(contentType) || contentType.startsWith("text/html")
                        || contentType.startsWith("text/javascript") || contentType.startsWith("application/json"))) {
            return true;
        }
        return false;
    }

}
