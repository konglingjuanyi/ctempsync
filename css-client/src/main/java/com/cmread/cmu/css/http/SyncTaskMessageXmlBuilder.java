package com.cmread.cmu.css.http;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SyncTaskMessageXmlBuilder {

	private static Logger logger = LoggerFactory.getLogger(SyncTaskMessageXmlBuilder.class);
	
	public String convertRequestToXmlStr(SyncTaskRequest request) {
		try {
			Document doc = convertRequestToXmlDocument(request);

			return xmlDocumentToString(doc);
		} catch (Exception e) {
			logger.error("convert request to string failed.", e);
			return "";
		}
	}
	
	public SyncTaskRequest convertXmlStrToRequest(String requestStr) {
		try {
			SyncTaskRequest request = new SyncTaskRequest();
			
			DocumentBuilder builder = newDocBuilder();
			
			Document doc = builder.parse(new ByteArrayInputStream(requestStr.getBytes("UTF-8")));
			
			Element synctask = doc.getDocumentElement();
			
			String taskType = getTextElementValue(synctask, "tasktype");
			request.setTaskType(taskType);
			
			String createTime = getTextElementValue(synctask, "createtime");
			request.setCreateTime(new Date(Long.parseLong(createTime)));
			
			Map<String, String> content = elementToMap(synctask, "content");
			request.setContent(content);
			
			return request;
		} catch (Exception e) {
			logger.error("convert xml string to request object failed.", e);
			return null;
		} 
	}

	private Map<String, String> elementToMap(Element element, String elementName) {
		Map<String, String> content = new HashMap<String, String>();
		NodeList contentNodes = element.getElementsByTagName(elementName);
		
		Node contentNode = contentNodes.item(0); //FIXME
		if (contentNode != null) {
			NodeList contentItemNodes = contentNode.getChildNodes();
			for (int i = 0; i < contentItemNodes.getLength(); ++i) {
				Node item = contentItemNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					String name = item.getNodeName();
					String value = item.getTextContent();
					content.put(name, value);					
				}
			}
			return content;
		}
		return new HashMap<String, String>(0);
	}

	public String convertResponseToXmlStr(SyncTaskResponse response) {
		try {
			Document doc = convertResponseToXmlDocument(response);

			return xmlDocumentToString(doc);
		} catch (Exception e) {
			logger.error("convert response to string failed.", e);
			return "";
		}
	}

	public SyncTaskResponse convertXmlStrToResponse(String responseStr) {
		try {
			SyncTaskResponse response = new SyncTaskResponse();
			
			DocumentBuilder builder = newDocBuilder();
			
			Document doc = builder.parse(new ByteArrayInputStream(responseStr.getBytes("UTF-8")));
			
			Element responseNode = doc.getDocumentElement();
			
			String taskID = getTextElementValue(responseNode, "taskid");
			response.setTaskID(taskID);
			
			//读取result结点，如果存在的话
			Map<String, String> result = elementToMap(responseNode, "result");
			response.setResult(result);
			
			return response;
		} catch (Exception e) {
			logger.error("convert xml string to response object failed.", e);
			return null;
		} 
	}

	
	private String getTextElementValue(Element ele, String tagName) {
		NodeList nodelist = ele.getElementsByTagName(tagName);
		if (nodelist.getLength() > 0) {
			return nodelist.item(0).getTextContent();
		}
		return "";
	}
	
	private Document convertResponseToXmlDocument(SyncTaskResponse resp) throws ParserConfigurationException {
		DocumentBuilder builder = this.newDocBuilder();
		Document doc = builder.newDocument();
		doc.setXmlStandalone(true);

		Element responseNode = doc.createElement("response");

		Element taskIDNode = createTextElement(doc, "taskid", resp.getTaskID());
		responseNode.appendChild(taskIDNode);
		
		Map<String, String> result = resp.getResult();
		if ((result != null) && (result.size() > 0)) {
			Element contentNode = doc.createElement("result");
			for (Map.Entry<String, String> entry : result.entrySet()) {
				Element contentItemNode = createTextElement(doc, entry.getKey(), entry.getValue());
				contentNode.appendChild(contentItemNode);
			}
			responseNode.appendChild(contentNode);
		}
		
		doc.appendChild(responseNode);
		
		return doc;
	}
	
	private Document convertRequestToXmlDocument(SyncTaskRequest taskMsg) throws ParserConfigurationException {
		DocumentBuilder builder = this.newDocBuilder();
		Document doc = builder.newDocument();
		doc.setXmlStandalone(true);

		Element synctaskNode = doc.createElement("synctask");

		Element taskTypeNode = createTextElement(doc, "tasktype", taskMsg.getTaskType());
		Element createTimeNode = createTextElement(doc, "createtime",
				String.valueOf(taskMsg.getCreateTime().getTime()));
		
		Element contentNode = doc.createElement("content");
		Map<String, String> content = taskMsg.getContent();
		for (Map.Entry<String, String> entry : content.entrySet()) {
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			
			Element contentItemNode = createTextElement(doc, entry.getKey(), value);
			contentNode.appendChild(contentItemNode);
		}

		synctaskNode.appendChild(taskTypeNode);
		synctaskNode.appendChild(createTimeNode);
		synctaskNode.appendChild(contentNode);
		doc.appendChild(synctaskNode);
		return doc;
	}
	
	private String xmlDocumentToString(Document doc)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(doc);

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter str = new StringWriter();
		StreamResult result = new StreamResult(str);
		transformer.transform(source, result);
		
		return str.toString();
	}


	private Element createTextElement(Document doc, String tag, String text) {
		Element textElement = doc.createElement(tag);
		textElement.appendChild(doc.createTextNode(text));
		
		return textElement;
	}
	
	private DocumentBuilder newDocBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		return dbf.newDocumentBuilder();
	}


}
