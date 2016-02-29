package test.example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 这个测试用例只是用来测试JDK内置的xml处理类的用法，无实际用途；
 * 
 * @author zhangtieying
 *
 */
public class XmlRWTest {
	
	@Test
	public void testXmlWrite() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element synctask = doc.createElement("synctask");

		Element taskType = doc.createElement("tasktype");
		taskType.appendChild(doc.createTextNode("test_user.insert"));

		synctask.appendChild(taskType);
		
		doc.appendChild(synctask);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(doc);

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter str = new StringWriter();
		StreamResult result = new StreamResult(str);
		transformer.transform(source, result);
		
		System.out.println(str.toString());
	}

	@Test
	public void testXmlRead() throws ParserConfigurationException, SAXException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" 
				+ "<synctask>" 
				+ "<tasktype>test_user.insert</tasktype>"
				+ "</synctask>";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		
		Element synctask = doc.getDocumentElement();
		NodeList nodelist = synctask.getElementsByTagName("tasktype");
		String taskType = nodelist.item(0).getTextContent();
		System.out.println("taskType = " + taskType);
	}
}
