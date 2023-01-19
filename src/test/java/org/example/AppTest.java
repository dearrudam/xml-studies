package org.example;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static final String FILENAME = "pom.xml";

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // optional, but recommended
        // process XML securely, avoid attacks like XML External Entities (XXE)
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(false);

        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new File(FILENAME));

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/project/dependencies";

        NodeList dependencies = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        var newDependency = doc.createElement("dependency");
        newDependency.appendChild(createElement(doc,"groupId","teste.id"));
        newDependency.appendChild(createElement(doc,"artifactId","teste2"));
        newDependency.appendChild(createElement(doc,"version","1.1"));
        newDependency.appendChild(createElement(doc,"optional","true"));

        dependencies.item(0).appendChild(newDependency);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        writeXml(doc,outputStream);
        System.out.print(outputStream.toString());
    }

    private Node createElement(Document doc, String tag, String value) {
        var element=doc.createElement(tag);
        element.setTextContent(value);
        return element;
    }

    private void print(String previous, Node element) {
        String value = "%s%s".formatted(
                Optional.ofNullable(previous)
                        .map(p -> "%s/".formatted(p))
                        .orElse("/"),
                Node.TEXT_NODE == element.getNodeType() ?
                        element.getNodeValue()
                        :
                        element.getNodeName());
        if(Node.TEXT_NODE == element.getNodeType()) {
            System.out.println(value);
        }
        print(value, element.getChildNodes());
    }

    private void print(String previous, NodeList childNodes) {
        IntStream.range(0, childNodes.getLength())
                .boxed()
                .forEach(index -> print(previous, childNodes.item(index)));
    }

    // write doc to output stream
    private void writeXml(Document doc,
                          OutputStream output)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//
//        // pretty print
//        transformer.setOutputProperty(OutputKeys.INDENT, "no");
//
//        DOMSource source = new DOMSource(doc);
//        StreamResult result = new StreamResult(output);
//
//        transformer.transform(source, result);


        final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        final LSSerializer writer = impl.createLSSerializer();
        final LSOutput lsOutput=impl.createLSOutput();
        lsOutput.setByteStream(output);

        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
        writer.getDomConfig().setParameter("xml-declaration", true); // Set this to true if the declaration is needed to be outputted.

        writer.write(doc,lsOutput);

    }
}
