package com.worldpay.sdk.wpg.internal.xml;

import com.worldpay.sdk.wpg.exception.WpgMalformedException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class XmlBuilder
{
    private XmlEndpoint endpoint;
    private Document document;
    private Element current;

    private XmlBuilder(XmlEndpoint endpoint, Document document, Element current)
    {
        this.endpoint = endpoint;
        this.document = document;
        this.current = current;
    }

    public XmlBuilder(XmlEndpoint endpoint)
    {
        try
        {
            this.endpoint = endpoint;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            current = document.createElement(endpoint.XML_ROOT_ELEMENT);
            current.setAttribute("version", endpoint.VERSION);
            document.appendChild(current);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Unable to prepare xml document", e);
        }
    }

    public XmlBuilder a(String key, String value)
    {
        current.setAttribute(key, value);
        return this;
    }

    public XmlBuilder a(String key, int value)
    {
        String text = String.valueOf(value);
        return a(key, text);
    }

    public String a(String key)
    {
        String value = current.getAttribute(key);
        if (value != null && value.length() == 0)
        {
            value = null;
        }
        return value;
    }

    public long aToLong(String key) throws WpgRequestException
    {
        String value = a(key);
        try
        {
            long longValue = Long.parseLong(value);
            return longValue;
        }
        catch (NumberFormatException e)
        {
            throw new WpgRequestException("Failed to read attribute '" + key + "' at " + getPath() + " - value: " + value, e);
        }
    }

    public Integer aToIntegerOptional(String key) throws WpgRequestException
    {
        Integer result = null;
        String value = a(key);

        if (value != null)
        {
            try
            {
                result = Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {
                throw new WpgRequestException("Failed to read attribute '" + key + "' at " + getPath() + " - value: " + value, e);
            }
        }

        return result;
    }

    public int aToInt(String key) throws WpgRequestException
    {
        String value = a(key);
        try
        {
            int intValue = Integer.parseInt(value);
            return intValue;
        }
        catch (NumberFormatException e)
        {
            throw new WpgRequestException("Failed to read attribute '" + key + "' at " + getPath() + " - value: " + value, e);
        }
    }

    public XmlBuilder e(String name)
    {
        e(name, false);
        return this;
    }

    public XmlBuilder e(String name, boolean addNew)
    {
        // Create if doesn't exist
        Element first;

        if (addNew || ((first = findFirst(current, name)) == null))
        {
            Element element = document.createElement(name);
            current.appendChild(element);
            current = element;
        }
        else
        {
            current = first;
        }

        return this;
    }

    /*
        Retrieves child elements by name, which are immediate children of the provided node/parent.
     */
    private Element findFirst(Element parent, String tagName)
    {
        Element result = null;

        NodeList list = current.getElementsByTagName(tagName);
        Node item;
        for (int i = 0; result == null && i < list.getLength(); i++)
        {
            item = list.item(i);
            if (item instanceof Element && parent.equals(item.getParentNode()))
            {
                result = (Element) item;
            }
        }
        return result;
    }

    public boolean isCurrentTag(String tagName)
    {
        return current.getTagName().equals(tagName);
    }

    public boolean hasE(String name)
    {
        NodeList list = current.getElementsByTagName(name);
        boolean present = list.getLength() > 0;

        if (present)
        {
            current = (Element) list.item(0);
        }

        return present;
    }

    public XmlBuilder cdata(String value)
    {
        // Some versions of Java will throw NPE when serializing blank text nodes
        if (value != null && value.length() > 0)
        {
            Text text = document.createTextNode(value);
            current.appendChild(text);
        }
        return this;
    }

    public XmlBuilder cdata(int value)
    {
        String text = String.valueOf(value);
        return cdata(text);
    }

    public XmlBuilder cdata(long value)
    {
        String text = String.valueOf(value);
        return cdata(text);
    }

    public String cdata()
    {
        String result = null;
        Node firstChild = current.getFirstChild();

        if (firstChild instanceof CharacterData)
        {
            CharacterData data = (CharacterData) firstChild;
            result = data.getData();
        }
        return result;
    }

    public String getCdata(String elementName)
    {
        String result = null;

        // Find node
        NodeList list = current.getElementsByTagName(elementName);
        Element element = (Element) list.item(0);

        if (element != null)
        {
            Node firstChild = element.getFirstChild();

            if (firstChild instanceof CharacterData)
            {
                CharacterData data = (CharacterData) firstChild;
                result = data.getData();
            }
        }

        return result;
    }

    public Long getCdataLong(String elementName) throws WpgRequestException
    {
        String value = getCdata(elementName);
        try
        {
            long longValue = Long.parseLong(value);
            return longValue;
        }
        catch (NumberFormatException e)
        {
            throw new WpgRequestException("Failed to read attribute '" + elementName + "' at " + getPath() + " - value: " + value, e);
        }
    }

    public XmlBuilder up()
    {
        Element element = (Element) current.getParentNode();
        if (element != null)
        {
            current = element;
        }
        return this;
    }

    public XmlBuilder reset()
    {
        current = document.getDocumentElement();
        return this;
    }

    public List<XmlBuilder> childTags(String tagName)
    {
        NodeList list = current.getElementsByTagName(tagName);
        int len = list.getLength();
        List<XmlBuilder> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
        {
            Node node = list.item(i);
            XmlBuilder clone = new XmlBuilder(endpoint, document, (Element) node);
            result.add(clone);
        }
        return result;
    }

    public boolean hasChildNodes()
    {
        return current.hasChildNodes();
    }

    public XmlBuilder getElementByName(String elementName)
    {
        XmlBuilder result = null;

        NodeList list = document.getElementsByTagName(elementName);
        if (list.getLength() > 0)
        {
            Node node = list.item(0);
            result = new XmlBuilder(endpoint, document, (Element) node);
        }

        return result;
    }

    @Override
    public String toString()
    {
        try
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMImplementation domImplementation = document.getImplementation();
            DocumentType docType = domImplementation.createDocumentType(
                    "doctype", endpoint.XML_DOCTYPE_PUBLIC_ID, endpoint.XML_DOCTYPE_SYSTEM_ID
            );

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());

            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter(4096);
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
            String text = writer.toString();
            return text;
        }
        catch (TransformerException e)
        {
            throw new RuntimeException("Failed to convert xml to text", e);
        }
    }

    public static XmlBuilder parse(XmlEndpoint service, String text) throws WpgMalformedException
    {
        try
        {
            XmlBuilder xmlBuilder = new XmlBuilder(service);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Apply security settings to prevent XEE attacks (unlikely from gateway though)
            // -- https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#JAXP_DocumentBuilderFactory.2C_SAXParserFactory_and_DOM4J
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            // Parse as XML
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(text));
            xmlBuilder.document = builder.parse(inputSource);
            xmlBuilder.reset();

            // Check root element is not HTML i.e. webpage
            Element root = xmlBuilder.current;
            if ("html".equals(root.getTagName()))
            {
                throw new WpgMalformedException("Data has HTML rather than expected XML");
            }

            return xmlBuilder;
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new WpgMalformedException("Unable to parse data as XML");
        }
    }

    public String getPath()
    {
        String path = "";
        Node parent = current;

        do
        {
            path = "/" + parent.getNodeName() + path;
            parent = parent.getParentNode();
        }
        while (parent != null);

        return path.toString();
    }

}
