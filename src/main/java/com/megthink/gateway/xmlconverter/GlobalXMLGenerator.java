package com.megthink.gateway.xmlconverter;

import java.io.*;
import java.util.*;
//import java.util.logging.Level;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xpath.XPathAPI;

//import com.sun.org.apache.xpath.internal.XPathAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.xpath.*;
//import sun.misc.BASE64Decoder;

public class GlobalXMLGenerator {

	private static final Logger portmeLog = LoggerFactory.getLogger(GlobalXMLGenerator.class);

	public static Map configurations;
	public static GlobalXMLGenerator messageConvertor;
	public static Object lock_getInstance = new Object();
	public static Object lock_reload = new Object();

	public static String transferToString(String xmlIn) throws GlobalException {
		return transferToString(null, xmlIn);
	}

	public static String transferToString(String path, String xmlIn) throws GlobalException {

		try {
			ArrayList results = GlobalXMLGenerator.getInstance(path).convert(xmlIn, "ParentHirarchy");

			if (results == null) {
				GlobalException mcexp = new GlobalException("internal exception");
				throw mcexp;
			} else if (results.size() == 0) {
				GlobalException mcexp = new GlobalException("no result items");
				throw mcexp;
			} else if (results.size() > 1) {
				GlobalException mcexp = new GlobalException("result more then 1 items");
				throw mcexp;
			}

			return (String) ((Map) results.get(0)).get("XML");

		} catch (FileNotFoundException exp) {
			GlobalException mcexp = new GlobalException("Unable to read configuration file", exp);
			throw mcexp;
		} catch (TransformerException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (SAXException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (ParserConfigurationException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (IOException exp) {
			GlobalException mcexp = new GlobalException("Unable to read configuration file", exp);
			throw mcexp;
		}
	}

	public static List<Map> transferToList(String path, String xmlIn, String selector) throws GlobalException {
		try {

			List<Map> results = null;

			if (selector == null) {
				results = GlobalXMLGenerator.getInstance(path).convert(xmlIn, "ParentHirarchy");
			} else {

				results = GlobalXMLGenerator.getInstance(path).convert(xmlIn, selector);
			}

			if (results == null) {
				GlobalException mcexp = new GlobalException("internal exception");
				throw mcexp;
			}
			// } else if (results.size() == 0) {
			// GlobalException mcexp = new GlobalException("no result items");
			// throw mcexp;
			// }

			return results;

		} catch (FileNotFoundException exp) {
			GlobalException mcexp = new GlobalException("Unable to read configuration file", exp);
			throw mcexp;
		} catch (TransformerException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (SAXException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (ParserConfigurationException exp) {
			GlobalException mcexp = new GlobalException("Unable to convert the XML based on given configuration", exp);
			throw mcexp;
		} catch (IOException exp) {
			GlobalException mcexp = new GlobalException("Unable to read configuration file", exp);
			throw mcexp;
		}
	}

	public static List<Map> transferToList(String xmlIn) throws GlobalException {
		return transferToList(null, xmlIn, "ParentHirarchy");
	}

	public static List<Map> transferToList(String path, String xmlIn) throws GlobalException {
		return transferToList(path, xmlIn, "ParentHirarchy");
	}

	static GlobalXMLGenerator getInstance(String path) throws FileNotFoundException, IOException {
		synchronized (lock_getInstance) {
			if (GlobalXMLGenerator.messageConvertor == null) {
				GlobalXMLGenerator.messageConvertor = new GlobalXMLGenerator();
				reload(path);
			}
			return messageConvertor;
		}
	}

	public static void reload() throws FileNotFoundException, IOException {
		reload(null);
	}

	public static void reload(String path) throws FileNotFoundException, IOException {
		synchronized (lock_reload) {
			if (GlobalXMLGenerator.configurations == null) {
				GlobalXMLGenerator.configurations = new HashMap();
			}

			String configurationFileName = null;

			if (path == null) {
				configurationFileName = PropertiesFactory.getInstance().getCoreConfDir() + File.separator
						+ "globalconf.dat";
			} else {
				configurationFileName = path;
			}

			GlobalXMLReader reader = new GlobalXMLReader();
			GlobalXMLGenerator.configurations = reader.readConfig(configurationFileName);
			GlobalXMLGenerator.getInstance(path).doValidation();
		}
	}

	ArrayList convert(String xmlIn, String selector)
			throws TransformerException, SAXException, ParserConfigurationException, IOException, GlobalException {

		Document resultDocument;
		Element resultRoot;
		Element referenceRoot;

		ArrayList results = new ArrayList();

		Document referenceDocument = stringToDom(xmlIn);
		referenceRoot = referenceDocument.getDocumentElement();

		Map selectors = (Map) GlobalXMLGenerator.configurations.get("selectors");
		Map schemas = (Map) GlobalXMLGenerator.configurations.get("schemas");
		selectors.forEach((k, v) -> System.out.println(k + ":" + v));
		System.out.println(selectors.size() + " " + selector);
		String workingSelector = (String) selectors.get(selector);
		StringTokenizer workingSelectors = new StringTokenizer(workingSelector, "\n");

		while (workingSelectors.hasMoreTokens()) {
			String selection = workingSelectors.nextToken();
			StringTokenizer parameters = new StringTokenizer(selection, " ");

			if (parameters.countTokens() < 4) {
				GlobalException mcexp = new GlobalException("Invalid selector in configuration");
				throw mcexp;
			}

			String action = parameters.nextToken();
			String selectCondition = parameters.nextToken();
			String callTo = parameters.nextToken();
			String target = parameters.nextToken();

			String key = null;
			String keyValue = null;
			String destination = null;
			String destinationValue = null;
			String messageName = null;
			String messageNameValue = null;

			while (parameters.hasMoreTokens()) {
				String parameter = parameters.nextToken();

				if (parameters.hasMoreTokens()) {
					String parameterValue = parameters.nextToken();

					if (parameter.equals("key")) {
						key = parameter;
						keyValue = parameterValue;
					} else if (parameter.equals("destination")) {
						destination = parameter;
						destinationValue = parameterValue;
					} else if (parameter.equals("message_name")) {
						messageName = parameter;
						messageNameValue = parameterValue;
					} else {
						GlobalException mcexp = new GlobalException("Invalid selector in configuration");
						throw mcexp;
					}

				} else {
					GlobalException mcexp = new GlobalException("Invalid selector in configuration");
					throw mcexp;
				}
			}

			if (checkSelectCondition(referenceRoot, selectCondition)) {

				if (callTo.equals("use_selector")) {
					ArrayList subResults = convert(xmlIn, target);

					if (subResults.size() > 0) {
						for (int i_subResults = 0; i_subResults < subResults.size(); i_subResults++) {
							results.add(subResults.get(i_subResults));
						}
					}

				} else if (callTo.equals("xml_def")) {
					Document configurationDocument = stringToDom((String) schemas.get(target));
					Element configurationRoot = configurationDocument.getDocumentElement();

					ArrayList configuratonRootList = new ArrayList();

					String configurationValue = getValue(configurationRoot).value;
					if (configurationValue == null) {
						configuratonRootList.add(configurationRoot);
					}

					Node configRootNode = null;

					if (configurationValue == null) {

						configRootNode = configurationRoot.getChildNodes().item(1);
						configurationValue = getValue(configRootNode).value;

						while (configurationValue == null) {
							if (configurationValue == null) {
								configuratonRootList.add(configRootNode);
							}

							configRootNode = configRootNode.getChildNodes().item(1);
							configurationValue = getValue(configRootNode).value;
						}
					}

					String tag = configurationValue.substring(1, configurationValue.length());

					ArrayList referenceNodes = retrieve(referenceRoot, tag);

					for (int i_referenceNodes = 0; i_referenceNodes < referenceNodes.size(); i_referenceNodes++) {

						resultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
						resultRoot = resultDocument.createElement("ResultDocumetRoot");
						Node resultNode = resultRoot;

						for (int i_configuratonRootList = 0; i_configuratonRootList < configuratonRootList
								.size(); i_configuratonRootList++) {
							Node newNode = resultDocument
									.importNode((Node) configuratonRootList.get(i_configuratonRootList), false);
							resultNode.appendChild(newNode);
							resultNode = newNode;
						}

						Node referenceNode = (Node) referenceNodes.get(i_referenceNodes);

						Annotation annotation = new Annotation();
						annotation.type = AnnotationType.ReferenceWithoutDefaultValue;

						if (configRootNode == null) {
							process(resultNode, referenceNode, configurationRoot, null, referenceRoot, annotation);
						} else {
							process(resultNode, referenceNode, configRootNode, null, referenceRoot, annotation);
						}

						String xmlOut = domToString(resultRoot.getFirstChild());

						HashMap result = new HashMap();

						result.put("XML", GlobalXMLGenerator.replaceAll(GlobalXMLGenerator.replaceAll(xmlOut, "\n", ""),
								"\r", ""));

						result.put("XML_PRETTY", xmlOut);

						if (key != null && keyValue != null) {
							result.put("KEY", getInnerValue(referenceNode, keyValue));
						}

						if (destination != null & destinationValue != null) {
							result.put("DESTINATION", destinationValue);
						}

						if (messageName != null & messageNameValue != null) {
							result.put("MESSAGE_NAME", messageNameValue);
						}

						results.add(result);
					}
				}
			}
		}

		return results;
	}

	boolean checkSelectCondition(Node node, String selectCondition)
			throws TransformerException, SAXException, ParserConfigurationException, IOException {

		boolean result = false;

		StringTokenizer selectConditions = new StringTokenizer(selectCondition, "|");

		String nextCondition = null;

		String currentCondition = selectConditions.nextToken();

		while (selectConditions.hasMoreTokens()) {
			if (nextCondition == null) {
				nextCondition = selectConditions.nextToken();
			} else {
				nextCondition = nextCondition + "|" + selectConditions.nextToken();
			}
		}

		ArrayList checkResult = retrieve(node, currentCondition);

		for (int i_checkResult = 0; i_checkResult < checkResult.size(); i_checkResult++) {
			if (checkResult.size() > 0 && nextCondition == null) {
				result = true;
			} else if (checkResult.size() > 0 && nextCondition != null) {

				Node nextNode = (Node) checkResult.get(i_checkResult);

				if (getValue(nextNode).value == null) {
					return false;
				} else {
					Document nextdoc = stringToDom(getValue(nextNode).value);
					Element nextRoot = nextdoc.getDocumentElement();

					result = checkSelectCondition(nextRoot, nextCondition);
				}
			}
		}

		return result;

	}

	String getInnerValue(Node node, String selectKeyCondition)
			throws TransformerException, SAXException, ParserConfigurationException, IOException {

		String innerValue = null;

		StringTokenizer selectConditions = new StringTokenizer(selectKeyCondition, "|");

		String nextCondition = null;

		String currentCondition = selectConditions.nextToken();

		while (selectConditions.hasMoreTokens()) {
			if (nextCondition == null) {
				nextCondition = selectConditions.nextToken();
			} else {
				nextCondition = nextCondition + "|" + selectConditions.nextToken();
			}
		}

		ArrayList checkResult = retrieve(node, currentCondition);

		for (int i_checkResult = 0; i_checkResult < checkResult.size(); i_checkResult++) {
			if (checkResult.size() > 0 && nextCondition == null) {
				innerValue = getValue((Node) checkResult.get(0)).value;
			} else if (checkResult.size() > 0 && nextCondition != null) {

				Node nextNode = (Node) checkResult.get(i_checkResult);

				Document nextdoc = stringToDom(getValue(nextNode).value);
				Element nextRoot = nextdoc.getDocumentElement();

				innerValue = getInnerValue(nextRoot, nextCondition);
			}
		}

		return innerValue;

	}

	ArrayList retrieveRecur(Node node, String selectKeyCondition)
			throws TransformerException, SAXException, ParserConfigurationException, IOException {

		ArrayList result = new ArrayList();

		StringTokenizer selectConditions = new StringTokenizer(selectKeyCondition, "|");

		String nextCondition = null;

		String currentCondition = selectConditions.nextToken();

		while (selectConditions.hasMoreTokens()) {
			if (nextCondition == null) {
				nextCondition = selectConditions.nextToken();
			} else {
				nextCondition = nextCondition + "|" + selectConditions.nextToken();
			}
		}

		ArrayList checkResult = retrieve(node, currentCondition);

		for (int i_checkResult = 0; i_checkResult < checkResult.size(); i_checkResult++) {
			if (checkResult.size() > 0 && nextCondition == null) {
				result = checkResult;
			} else if (checkResult.size() > 0 && nextCondition != null) {

				Node nextNode = (Node) checkResult.get(i_checkResult);

				Document nextdoc = stringToDom(getValue(nextNode).value);
				Element nextRoot = nextdoc.getDocumentElement();

				result = retrieve(nextRoot, nextCondition);
			}
		}

		return result;

	}

	void process(Node resultNode, Node referenceNode, Node configurationNode, String parent, Element referenceRoot,
			Annotation prevAnnotation)
			throws SAXException, TransformerException, ParserConfigurationException, IOException {

		if (prevAnnotation.type == AnnotationType.HardCode) {
			NodeValue nodeValue = new NodeValue();
			nodeValue.isCdata = false;
			nodeValue.value = prevAnnotation.value;
			resultNode = copyNode(resultNode, nodeValue, configurationNode);
		} else if (prevAnnotation.type == AnnotationType.NoAnnotation) {
			resultNode = copyNode(resultNode, configurationNode);
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithDefaultValue) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode);

			if (getValue(resultNode).value == null) {
				resultNode.setTextContent(prevAnnotation.defaultValue);
			}
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithoutDefaultValue) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode);
		} else if (prevAnnotation.type == AnnotationType.RetrieveByPathWithDefaultValue) {
		} else if (prevAnnotation.type == AnnotationType.RetrieveByPathWithoutDefaultValue) {
		} else if (prevAnnotation.type == AnnotationType.EmbedSourceAsBase64) {
		} else if (prevAnnotation.type == AnnotationType.EmbedSourceAsCDATA) {
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInBase64OutWithProcess) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode, prevAnnotation);
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInCdataOutWithProcess) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode, prevAnnotation);
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode, prevAnnotation);
		} else if (prevAnnotation.type == AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess) {
			resultNode = copyNode(resultNode, referenceNode, configurationNode, prevAnnotation);
		} else if (prevAnnotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
		} else if (prevAnnotation.type == AnnotationType.UnKnown) {
			// "Exception";
		} else {
			// "Exception";
		}

		ArrayList configurationChilds = getChildNodes(configurationNode);

		for (int i_configurationChild = 0; i_configurationChild < configurationChilds.size(); i_configurationChild++) {
			Node nextConfigurationNode = (Node) getChildNodes(configurationNode).get(i_configurationChild);

			Annotation annotation = getAnnotation(getValue(nextConfigurationNode).value);

			if (annotation.type == AnnotationType.HardCode) {
				process(resultNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
			} else if (annotation.type == AnnotationType.NoAnnotation) {
				process(resultNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValue) {

				String tag = referenceNode.getNodeName() + "/" + annotation.value;
				Document doc = stringToDom(domToString(referenceNode));

				ArrayList referenceChilds = retrieve(doc.getDocumentElement(), tag);

				if (referenceChilds.size() < 1) {
					process(resultNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				} else {
					for (int i_referenceChilds = 0; i_referenceChilds < referenceChilds.size(); i_referenceChilds++) {
						Node nextReferenceNode = (Node) referenceChilds.get(i_referenceChilds);
						String newParent = "";

						if (parent == null) {
							newParent = referenceNode.getNodeName() + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						} else {
							newParent = parent + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						}

						process(resultNode, nextReferenceNode, nextConfigurationNode, newParent, referenceRoot,
								annotation);
					}
				}
			} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValue) {

				String tag = referenceNode.getNodeName() + "/" + annotation.value;
				Document doc = stringToDom(domToString(referenceNode));

				ArrayList referenceChilds = retrieve(doc.getDocumentElement(), tag);

				if (referenceChilds.size() >= 1) {
					for (int i_referenceChilds = 0; i_referenceChilds < referenceChilds.size(); i_referenceChilds++) {
						Node nextReferenceNode = (Node) referenceChilds.get(i_referenceChilds);
						String newParent = "";

						if (parent == null) {
							newParent = referenceNode.getNodeName() + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						} else {
							newParent = parent + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						}

						process(resultNode, nextReferenceNode, nextConfigurationNode, newParent, referenceRoot,
								annotation);
					}
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithDefaultValue) {

				annotation.value = resolveParentPath(annotation.value, parent);

				ArrayList subNodeList = retrieveRecur(referenceRoot, annotation.value);

				Node resultPathNode;

				if (subNodeList.size() < 1) {
					resultPathNode = copyNode(resultNode, referenceNode, nextConfigurationNode);
					resultPathNode.setTextContent(annotation.defaultValue);
				} else {
					Node referNode = (Node) subNodeList.get(0);
					resultPathNode = copyNode(resultNode, referNode, nextConfigurationNode);
				}

				if (getChildNodes(nextConfigurationNode).size() > 0) {
					process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				}

			} else if (annotation.type == AnnotationType.RetrieveByPathWithoutDefaultValue) {

				annotation.value = resolveParentPath(annotation.value, parent);

				ArrayList subNodeList = retrieveRecur(referenceRoot, annotation.value);

				if (subNodeList.size() >= 1) {
					Node referNode = (Node) subNodeList.get(0);
					Node resultPathNode = copyNode(resultNode, referNode, nextConfigurationNode);

					if (getChildNodes(nextConfigurationNode).size() > 0) {
						process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot,
								annotation);
					}
				}
			} else if (annotation.type == AnnotationType.EmbedSourceAsBase64) {

				NodeValue nodeValue = new NodeValue();
				nodeValue.isCdata = false;
				nodeValue.value = embed(referenceRoot, annotation.type);

				Node resultPathNode = copyNode(resultNode, nodeValue, nextConfigurationNode);

				if (getChildNodes(nextConfigurationNode).size() > 0) {
					process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				}

			} else if (annotation.type == AnnotationType.EmbedSourceAsCDATA) {

				NodeValue nodeValue = new NodeValue();
				nodeValue.isCdata = false;
				nodeValue.value = embed(referenceRoot, annotation.type);

				Node resultPathNode = copyNode(resultNode, nodeValue, nextConfigurationNode);

				if (getChildNodes(nextConfigurationNode).size() > 0) {
					process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				}
			} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInBase64OutWithProcess
					|| annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInCdataOutWithProcess) {

				String tag = referenceNode.getNodeName() + "/" + annotation.value;
				Document doc = stringToDom(domToString(referenceNode));

				ArrayList referenceChilds = retrieve(doc.getDocumentElement(), tag);

				if (referenceChilds.size() >= 1) {
					for (int i_referenceChilds = 0; i_referenceChilds < referenceChilds.size(); i_referenceChilds++) {
						Node nextReferenceNode = (Node) referenceChilds.get(i_referenceChilds);
						String newParent = "";

						if (parent == null) {
							newParent = referenceNode.getNodeName() + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						} else {
							newParent = parent + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						}

						process(resultNode, nextReferenceNode, nextConfigurationNode, newParent, referenceRoot,
								annotation);
					}
				}
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess
					|| annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess) {

				String tag = referenceNode.getNodeName() + "/" + annotation.value;
				Document doc = stringToDom(domToString(referenceNode));

				ArrayList referenceChilds = retrieve(doc.getDocumentElement(), tag);

				if (referenceChilds.size() < 1) {
					process(resultNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				} else {
					for (int i_referenceChilds = 0; i_referenceChilds < referenceChilds.size(); i_referenceChilds++) {
						Node nextReferenceNode = (Node) referenceChilds.get(i_referenceChilds);
						String newParent = "";

						if (parent == null) {
							newParent = referenceNode.getNodeName() + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						} else {
							newParent = parent + "/" + annotation.value + "["
									+ new Integer(i_referenceChilds + 1).toString() + "]";
						}

						process(resultNode, nextReferenceNode, nextConfigurationNode, newParent, referenceRoot,
								annotation);
					}
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithDefaultValueXmlInCdataOutWithProcess) {

				NodeValue nodeValue = new NodeValue();
				nodeValue.isCdata = true;

				annotation.value = resolveParentPath(annotation.value, parent);
				ArrayList subNodeList = retrieveRecur(referenceRoot, annotation.value);

				if (subNodeList.size() >= 1) {
					Node referNode = (Node) subNodeList.get(0);

					List<Map> innserResults = GlobalXMLGenerator.transferToList(null, domToString(referNode),
							annotation.selector);

					if (innserResults.size() > 0) {
						nodeValue.value = (String) innserResults.get(0).get("XML");
					}
				}

				if (nodeValue.value == null) {
					nodeValue.value = annotation.defaultValue;
				}

				Node resultPathNode = copyNode(resultNode, nodeValue, nextConfigurationNode);

				if (getChildNodes(nextConfigurationNode).size() > 0) {
					process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
				}

			} else if (annotation.type == AnnotationType.RetrieveByPathWithoutDefaultValueXmlInCdataOutWithProcess) {

				NodeValue nodeValue = new NodeValue();
				nodeValue.isCdata = true;

				annotation.value = resolveParentPath(annotation.value, parent);
				ArrayList subNodeList = retrieveRecur(referenceRoot, annotation.value);

				if (subNodeList.size() >= 1) {
					Node referNode = (Node) subNodeList.get(0);

					List<Map> innserResults = GlobalXMLGenerator.transferToList(null, domToString(referNode),
							annotation.selector);

					if (innserResults.size() > 0) {
						nodeValue.value = (String) innserResults.get(0).get("XML");
					}
				}

				if (nodeValue.value != null) {
					Node resultPathNode = copyNode(resultNode, nodeValue, nextConfigurationNode);

					if (getChildNodes(nextConfigurationNode).size() > 0) {
						process(resultPathNode, referenceNode, nextConfigurationNode, parent, referenceRoot,
								annotation);
					}
				} else {
					if (getChildNodes(nextConfigurationNode).size() > 0) {
						process(resultNode, referenceNode, nextConfigurationNode, parent, referenceRoot, annotation);
					}
				}

			} else if (annotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
				String tag = referenceNode.getNodeName() + "/" + annotation.value;

				Document doc = stringToDom(domToString(referenceNode));

				ArrayList referenceChilds = retrieve(doc.getDocumentElement(), tag);

				if (referenceChilds.size() >= 1) {
					for (int i_referenceChilds = 0; i_referenceChilds < referenceChilds.size(); i_referenceChilds++) {
						Node nextReferenceNode = (Node) referenceChilds.get(i_referenceChilds);

						copyNode(resultNode, nextReferenceNode, nextConfigurationNode, annotation);

					}
				}
			}
		}
	}

	String resolveParentPath(String path, String parentPath) {

		String resultPath = "";

		if (parentPath != null) {
			StringTokenizer pathItems = new StringTokenizer(path, "|");
			StringTokenizer pathTokens = new StringTokenizer(pathItems.nextToken(), "/");
			StringTokenizer parentTokens = new StringTokenizer(parentPath, "/");

			int pathTokenCount = pathTokens.countTokens();
			int parentTokenCount = parentTokens.countTokens();

			if (pathTokenCount <= (parentTokenCount + 1)) {

				for (int idx = 0; idx < pathTokenCount; idx++) {

					if (idx < (pathTokenCount - 1)) {
						resultPath = resultPath + "/" + parentTokens.nextToken();
						pathTokens.nextToken();
					} else {
						resultPath = resultPath + "/" + pathTokens.nextToken();
					}
				}
				while (pathItems.hasMoreTokens()) {
					resultPath = resultPath + "|" + pathItems.nextToken();
				}
			} else {
				resultPath = path;
			}
		} else {
			resultPath = path;
		}

		return resultPath;

	}

	String embed(Node rootNode, AnnotationType embedType)
			throws TransformerConfigurationException, TransformerException, ParserConfigurationException {

		String result = "";

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = doc.createElement("ResultDocumetRoot");

		ArrayList<Node> prefixNodes = new ArrayList();
		Node startNode = root;
		Node currentNode = rootNode;

		while (currentNode.getParentNode() != null && currentNode.getParentNode().getNodeType() != 9) {
			prefixNodes.add(currentNode.getParentNode());
			currentNode = currentNode.getParentNode();
		}

		Node processingNode = null;

		if (prefixNodes.size() > 0) {
			for (int i_prefixNodes = prefixNodes.size() - 1; i_prefixNodes >= 0; i_prefixNodes--) {
				processingNode = doc.importNode(prefixNodes.get(i_prefixNodes), false);
				startNode.appendChild(processingNode);
				startNode = processingNode;
			}
		}

		processingNode = doc.importNode(rootNode, true);
		startNode.appendChild(processingNode);

		if (embedType == AnnotationType.EmbedSourceAsBase64) {
			result = GlobalXMLGenerator.getBASE64(domToString(root.getChildNodes().item(0)));
		} else if (embedType == AnnotationType.EmbedSourceAsCDATA) {
			result = domToString(root.getChildNodes().item(0));
		}

		return result;
	}

	Annotation getAnnotation(String annotationValue) {

		Annotation annotation = new Annotation();
		annotation.type = AnnotationType.UnKnown;

		if (annotationValue == null) {
			annotation.type = AnnotationType.NoAnnotation;
		} else {
			if (annotationValue.trim().startsWith("@")) {

				if (annotationValue.trim().length() > 1) {

					annotation.value = annotationValue.substring(1, annotationValue.length());

					if (annotation.value.startsWith("/")) {

						StringTokenizer valueTokens = new StringTokenizer(annotation.value, " ");

						if (valueTokens.hasMoreTokens()) {
							annotation.value = valueTokens.nextToken();

							if (valueTokens.hasMoreTokens()) {
								String subAnnotation = valueTokens.nextToken();

								if (subAnnotation.equals("default")) {

									String defaultValue = "";

									while (valueTokens.hasMoreTokens()) {
										defaultValue = defaultValue + " " + valueTokens.nextToken();
									}

									annotation.defaultValue = defaultValue.trim();
									// annotation.type = AnnotationType.RetrieveByPathWithDefaultValue;
								} else if (subAnnotation.equals("selector")) {

									if (valueTokens.hasMoreTokens()) {
										annotation.selector = valueTokens.nextToken();

										if (valueTokens.hasMoreTokens()) {
											annotation.in = valueTokens.nextToken();

											if (valueTokens.hasMoreTokens()) {
												annotation.out = valueTokens.nextToken();

												if (valueTokens.hasMoreTokens()) {
													if (valueTokens.nextToken().equals("default")) {

														String defaultValue = "";

														while (valueTokens.hasMoreTokens()) {
															defaultValue = defaultValue + " " + valueTokens.nextToken();
														}

														annotation.defaultValue = defaultValue.trim();

													}
												}
											}
										}
									}
								}
							} // else {
								// annotation.type = AnnotationType.RetrieveByPathWithoutDefaultValue;
								// }

							if (annotation.defaultValue != null && annotation.selector != null && annotation.in != null
									&& annotation.in.equals("XML") && annotation.out != null
									&& annotation.out.equals("CDATA")) {
								annotation.type = AnnotationType.RetrieveByPathWithDefaultValueXmlInCdataOutWithProcess;
							} else if (annotation.defaultValue == null && annotation.selector != null
									&& annotation.in != null && annotation.in.equals("XML") && annotation.out != null
									&& annotation.out.equals("CDATA")) {
								annotation.type = AnnotationType.RetrieveByPathWithoutDefaultValueXmlInCdataOutWithProcess;
							} else if (annotation.defaultValue != null && annotation.selector == null
									&& annotation.in == null && annotation.out == null) {
								annotation.type = AnnotationType.RetrieveByPathWithDefaultValue;
							} else if (annotation.defaultValue == null && annotation.selector == null
									&& annotation.in == null && annotation.out == null) {
								annotation.type = AnnotationType.RetrieveByPathWithoutDefaultValue;
							}
						}
					} else {
						StringTokenizer valueTokens = new StringTokenizer(annotation.value, " ");

						if (valueTokens.hasMoreTokens()) {
							annotation.value = valueTokens.nextToken();

							if (valueTokens.hasMoreTokens()) {
								String subAnnotation = valueTokens.nextToken();

								if (subAnnotation.equals("default")) {

									String defaultValue = "";

									while (valueTokens.hasMoreTokens()) {
										defaultValue = defaultValue + " " + valueTokens.nextToken();
									}

									annotation.defaultValue = defaultValue.trim();
								} else if (subAnnotation.equals("selector")) {

									if (valueTokens.hasMoreTokens()) {
										annotation.selector = valueTokens.nextToken();

										if (valueTokens.hasMoreTokens()) {
											annotation.in = valueTokens.nextToken();

											if (valueTokens.hasMoreTokens()) {
												annotation.out = valueTokens.nextToken();

												if (valueTokens.hasMoreTokens()) {
													if (valueTokens.nextToken().equals("default")) {

														String defaultValue = "";

														while (valueTokens.hasMoreTokens()) {
															defaultValue = defaultValue + " " + valueTokens.nextToken();
														}

														annotation.defaultValue = defaultValue.trim();

													}
												}
											}
										}
									}
								} else if (subAnnotation.equals("copy_hierarchy_as")) {
									if (valueTokens.hasMoreTokens()) {

										String outputType = valueTokens.nextToken();

										if (valueTokens.hasMoreTokens()) {
											// Default
											// if (valueTokens.nextToken().equals("default")) {
											//
											// String defaultValue = "";
											//
											// while (valueTokens.hasMoreTokens()) {
											// defaultValue = defaultValue + " " + valueTokens.nextToken();
											// }
											//
											// if (outputType.equals("CDATA")) {
											// annotation.defaultValue = defaultValue.trim();
											// annotation.type =
											// AnnotationType.ReferenceCopyHierachyAsCdataWithDefaultValue;
											// }
											// }
										} else {
											if (outputType.equals("CDATA")) {
												annotation.type = AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue;
											}
										}
									}
								}
							}
						}

						if (annotation.defaultValue != null && annotation.selector != null && annotation.in != null
								&& annotation.in.equals("CDATA") && annotation.out != null
								&& annotation.out.equals("BASE64")) {
							annotation.type = AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess;
						} else if (annotation.defaultValue != null && annotation.selector != null
								&& annotation.in != null && annotation.in.equals("CDATA") && annotation.out != null
								&& annotation.out.equals("CDATA")) {
							annotation.type = AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess;
						} else if (annotation.defaultValue == null && annotation.selector != null
								&& annotation.in != null && annotation.in.equals("CDATA") && annotation.out != null
								&& annotation.out.equals("BASE64")) {
							annotation.type = AnnotationType.ReferenceWithoutDefaultValueCdataInBase64OutWithProcess;
						} else if (annotation.defaultValue == null && annotation.selector != null
								&& annotation.in != null && annotation.in.equals("CDATA") && annotation.out != null
								&& annotation.out.equals("CDATA")) {
							annotation.type = AnnotationType.ReferenceWithoutDefaultValueCdataInCdataOutWithProcess;
							// } else if (annotation.type ==
							// AnnotationType.ReferenceCopyHierachyAsCdataWithDefaultValue) {
						} else if (annotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
						} else if (annotation.defaultValue != null && annotation.selector == null
								&& annotation.in == null && annotation.out == null) {
							annotation.type = AnnotationType.ReferenceWithDefaultValue;
						} else if (annotation.defaultValue == null && annotation.selector == null
								&& annotation.in == null && annotation.out == null) {
							annotation.type = AnnotationType.ReferenceWithoutDefaultValue;
						}
					}
				}
			} else if (annotationValue.startsWith("+")) {
				if (annotationValue.equals("+")) {
					annotation.type = AnnotationType.EmbedSourceAsBase64;
				} else {
					annotation.value = annotationValue.substring(1, annotationValue.length()).trim();
					if (annotation.value.equals("CDATA")) {
						annotation.type = AnnotationType.EmbedSourceAsCDATA;
					} else if (annotation.value.equals("BASE64")) {
						annotation.type = AnnotationType.EmbedSourceAsBase64;
					}
				}
			} else {
				annotation.type = AnnotationType.HardCode;
				annotation.value = annotationValue;
			}
		}

		return annotation;
	}

	Node copyNode(Node resultNode, Node configurationNode) {
		Node newNode = resultNode.getOwnerDocument().importNode(configurationNode, false);
		resultNode.appendChild(newNode);
		return newNode;
	}

	Node copyNode(Node resultNode, NodeValue nodeValue, Node configurationNode) {
		Node newNode = resultNode.getOwnerDocument().importNode(configurationNode, false);

		if (!nodeValue.isCdata) {
			newNode.setTextContent(nodeValue.value);
		} else {
			CDATASection cdata = resultNode.getOwnerDocument().createCDATASection(nodeValue.value);
			newNode.appendChild(cdata);
		}

		resultNode.appendChild(newNode);
		return newNode;
	}

	Node copyNode(Node resultNode, NodeValue nodeValue, Node configurationNode, Annotation annotation) {
		Node newNode = resultNode.getOwnerDocument().importNode(configurationNode, false);

		if (nodeValue.value != null) {
			List<Map> innserResults = GlobalXMLGenerator.transferToList(null, nodeValue.value, annotation.selector);

			if (innserResults.size() > 0) {
				if (annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInBase64OutWithProcess) {
					newNode.setTextContent(GlobalXMLGenerator.getBASE64((String) innserResults.get(0).get("XML")));
				} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInCdataOutWithProcess) {
					newNode.setTextContent((String) innserResults.get(0).get("XML"));
				} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess) {
					newNode.setTextContent(GlobalXMLGenerator.getBASE64((String) innserResults.get(0).get("XML")));
				} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess) {
					newNode.setTextContent((String) innserResults.get(0).get("XML"));
				}
			}
		} else {
			if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess) {
				newNode.setTextContent(GlobalXMLGenerator.getBASE64(annotation.defaultValue));
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess) {
				newNode.setTextContent(annotation.defaultValue);
			}
		}

		resultNode.appendChild(newNode);
		return newNode;
	}

	Node copyNode(Node resultNode, Node referenceNode, Node configurationNode, Annotation annotation)
			throws TransformerConfigurationException, TransformerException {

		Node newNode = null;

		if (annotation == null) {
			newNode = copyNode(resultNode, getValue(referenceNode), configurationNode);
		} else if (annotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
			newNode = copyNode(resultNode, getValue(referenceNode, annotation), configurationNode);
		} else {
			newNode = copyNode(resultNode, getValue(referenceNode), configurationNode, annotation);
		}

		NamedNodeMap newAttributes = newNode.getAttributes();

		for (int i_newAttributes = 0; i_newAttributes < newAttributes.getLength(); i_newAttributes++) {

			Annotation attributeAnnotation = getAnnotation(newAttributes.item(i_newAttributes).getNodeValue());

			if (newAttributes.item(i_newAttributes).getNodeValue().trim().startsWith("@")) {

				if (attributeAnnotation.type == AnnotationType.HardCode) {
					newAttributes.item(i_newAttributes).setNodeValue(attributeAnnotation.value);
				} else if (attributeAnnotation.type == AnnotationType.ReferenceWithDefaultValue) {
					if (referenceNode.getAttributes().getNamedItem(attributeAnnotation.value) != null) {
						newAttributes.item(i_newAttributes).setNodeValue(
								referenceNode.getAttributes().getNamedItem(attributeAnnotation.value).getNodeValue());
					} else {
						newAttributes.item(i_newAttributes).setNodeValue(attributeAnnotation.defaultValue);
					}
				}
				if (attributeAnnotation.type == AnnotationType.ReferenceWithoutDefaultValue) {
					if (referenceNode.getAttributes().getNamedItem(attributeAnnotation.value) != null) {
						newAttributes.item(i_newAttributes).setNodeValue(
								referenceNode.getAttributes().getNamedItem(attributeAnnotation.value).getNodeValue());
					}
				}
			}
		}

		return newNode;
	}

	Node copyNode(Node resultNode, Node referenceNode, Node configurationNode)
			throws TransformerConfigurationException, TransformerException {
		return copyNode(resultNode, referenceNode, configurationNode, null);
	}

	ArrayList getChildNodes(Node node) {
		ArrayList childNodes = new ArrayList();
		NodeList childs = node.getChildNodes();

		for (int idx = 0; idx < childs.getLength(); idx++) {
			if (childs.item(idx).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				childNodes.add(childs.item(idx));
			}
		}
		return childNodes;
	}

	String domToString(Node node) throws TransformerConfigurationException, TransformerException {
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(node);
		trans.transform(source, result);
		String xmlString = sw.toString();
		return xmlString;
	}

	public static Document stringToDom(String xmlSource)
			throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xmlSource)));
		return doc;
	}

	ArrayList retrieve(Node startNode, String xql) throws TransformerException {
		return retrieve(startNode, xql, true);
	}

	ArrayList retrieve(Node startNode, String xql, boolean isPrintInvalidtoLog) throws TransformerException {
		ArrayList resultNodes = new ArrayList();

		try {
			if (xql.indexOf(":") != -1) {
				xql = xql.substring(xql.indexOf(":") + 1, xql.length());
			}

			NodeList targetNodes = XPathAPI.selectNodeList(startNode, "/" + xql);

			for (int idx = 0; idx < targetNodes.getLength(); idx++) {
				if (targetNodes.item(idx).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					resultNodes.add(targetNodes.item(idx));
				}
			}
		} catch (TransformerException e) {
			if (isPrintInvalidtoLog) {
				portmeLog
						.warn("retrieve() [Invalid annotation with respected to input XML] - Node in configuration is ["
								+ startNode.getNodeName() + "]. Annotation is [" + xql + "] and error message is ["
								+ e.getMessageAndLocation() + "]");
			}
			throw e;
		}

		return resultNodes;
	}

	NodeValue getValue(Node node) {
		NodeValue nodeValue = new NodeValue();
		NodeList nodeList = node.getChildNodes();
		if (nodeList != null) {
			for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {

				if (nodeList.item(nodeIdx).getNodeType() == org.w3c.dom.Node.TEXT_NODE
						&& nodeList.item(nodeIdx).getNodeValue() != null
						&& nodeList.item(nodeIdx).getNodeValue().trim().length() > 0) {
					nodeValue.value = nodeList.item(nodeIdx).getNodeValue().trim();
					nodeValue.isCdata = false;
				} else if (nodeList.item(nodeIdx).getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE
						&& nodeList.item(nodeIdx).getNodeValue() != null
						&& nodeList.item(nodeIdx).getNodeValue().trim().length() > 0) {
					nodeValue.value = nodeList.item(nodeIdx).getNodeValue().trim();
					nodeValue.isCdata = true;
				}
			}
		}
		return nodeValue;
	}

	NodeValue getValue(Node node, Annotation annotation)
			throws TransformerConfigurationException, TransformerException {
		NodeValue nodeValue = new NodeValue();

		if (annotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
			nodeValue.value = domToString(node);
			nodeValue.isCdata = true;
		}

		return nodeValue;
	}

	public static String replaceAll(String target, String from, String to) {
		int start = target.indexOf(from);
		if (start == -1) {
			return target;
		}
		int lf = from.length();
		char[] targetChars = target.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append(targetChars, copyFrom, start - copyFrom);
			buffer.append(to);
			copyFrom = start + lf;
			start = target.indexOf(from, copyFrom);
		}
		buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}

	public static String getBASE64(String s) {
		if (s == null) {
			return null;
		}
		//return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
		return Base64.getEncoder().encode(s.getBytes()).toString();
	}

	public static String getFromBASE64(String s) {
		if (s == null) {
			return null;
		}
//		byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
		//SE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = Base64.getDecoder().decode(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	class Annotation {

		public AnnotationType type;
		public String selector;
		public String value;
		public String defaultValue;
		public String in;
		public String out;
	}

	enum AnnotationType {

		EmbedSourceAsCDATA, EmbedSourceAsBase64, HardCode, ReferenceWithDefaultValue, ReferenceWithoutDefaultValue, RetrieveByPathWithDefaultValue, RetrieveByPathWithoutDefaultValue, ReferenceWithoutDefaultValueCdataInBase64OutWithProcess, ReferenceWithoutDefaultValueCdataInCdataOutWithProcess, ReferenceWithDefaultValueCdataInBase64OutWithProcess, ReferenceWithDefaultValueCdataInCdataOutWithProcess, RetrieveByPathWithDefaultValueXmlInCdataOutWithProcess, RetrieveByPathWithoutDefaultValueXmlInCdataOutWithProcess, ReferenceCopyHierachyAsCdataWithoutDefaultValue, NoAnnotation, UnKnown
	}

	class NodeValue {

		String value;
		boolean isCdata = false;
	}

	public static void validate(String path) throws IOException {
		GlobalXMLGenerator.getInstance(path);
	}

	public void doValidation() {

		portmeLog.info("doValidation() - start validation on configuration");

		Map schemas = (Map) configurations.get("schemas");
		Map selectors = (Map) configurations.get("selectors");

		if (selectors == null || selectors.size() == 0) {
			portmeLog.warn("doValidation() [Invalid Configuration] - no valid selector found");
		}

		if (schemas == null || schemas.size() == 0) {
			portmeLog.warn("readCdoValidationonfig() [Invalid Configuration] - no valid schema found");
		}

		if (selectors != null && selectors.size() > 0) {
			Object[] keys = selectors.keySet().toArray();

			for (int i_selectors = 0; i_selectors < keys.length; i_selectors++) {
				validateSelector((String) keys[i_selectors], (String) selectors.get(keys[i_selectors]), schemas,
						selectors);
			}
		}

		if (schemas != null || schemas.size() > 0) {
			Object[] keys = schemas.keySet().toArray();

			for (int i_schemas = 0; i_schemas < keys.length; i_schemas++) {
				String schema = (String) schemas.get(keys[i_schemas]);
				validateSchema((String) keys[i_schemas], schema, schemas, selectors);
			}
		}

		portmeLog.info("doValidation() - finish validation on configuration");
	}

	void validateSchema(String schemaXml, String schemaName, Map schemas, Map selectors) {

		schemaXml = schemaXml.trim();
		if (schemaXml.startsWith("<") && schemaXml.endsWith(">")) {
			try {
				Document doc = stringToDom(schemaXml);
				validateSchema(doc.getDocumentElement(), schemaXml, schemas, selectors);
			} catch (SAXException e) {
				portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schemaName
						+ "] error is found at/close to line [ " + ((SAXParseException) e).getLineNumber()
						+ "] column [" + ((SAXParseException) e).getColumnNumber() + "]. Error type is ["
						+ ((SAXParseException) e).getMessage() + "]", e);
			} catch (ParserConfigurationException e) {
				portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schemaName + "] error", e);
			} catch (IOException e) {
				portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schemaName + "] error", e);
			}
		}
	}

	void validateSelector(String selectorName, String selectorContents, Map schemas, Map selectors) {

		StringTokenizer selections = new StringTokenizer(selectorContents, "\n");

		if (!selections.hasMoreTokens()) {
			portmeLog.warn("validateSelector() [Invalid Configuration]  - The selector [" + selectorName
					+ "] contain nothing");
			return;
		}

		while (selections.hasMoreTokens()) {
			String selection = selections.nextToken();
			StringTokenizer parameters = new StringTokenizer(selection, " ");

			int selectionItemCount = parameters.countTokens();

			if (selectionItemCount < 4 || selectionItemCount > 10) {
				portmeLog.warn("validateSelector() [Invalid Configuration]  - Invalid selection item [" + selection
						+ "] in selector [" + selectorName + "]");
				return;
			}

			String action = parameters.nextToken();
			String selectCondition = parameters.nextToken();
			String callTo = parameters.nextToken();
			String target = parameters.nextToken();

			String key = null;
			String keyValue = null;
			String destination = null;
			String destinationValue = null;
			String messageName = null;
			String messageNameValue = null;

			if (!action.equals("@on")) {
				portmeLog.warn("validateSelector() [Invalid Configuration]  - Invalid statement [" + selection
						+ "] in selector [" + selectorName + "]");
				return;
			}

			while (parameters.hasMoreTokens()) {
				String parameter = parameters.nextToken();

				if (parameters.hasMoreTokens()) {
					String parameterValue = parameters.nextToken();

					if (parameter.equals("key")) {
						key = parameter;
						keyValue = parameterValue;
					} else if (parameter.equals("destination")) {
						destination = parameter;
						destinationValue = parameterValue;
					} else if (parameter.equals("message_name")) {
						messageName = parameter;
						messageNameValue = parameterValue;
					} else {
						portmeLog.warn("validateSelector() [Invalid Configuration]  - Unknow parameter name ["
								+ parameter + "] in statement [" + selection + "] of selector [" + selectorName + "]");
					}

				} else {
					portmeLog.warn(
							"validateSelector() [Invalid Configuration]  - Parameter [" + parameter + "] in statement ["
									+ selection + "] of selector [" + selectorName + "] didn't contains value");
				}
			}

			if (callTo.equals("use_selector")) {
				if (selectionItemCount != 4) {
					portmeLog.warn("validateSelector() [Invalid Configuration]  - Invalid selection item [" + selection
							+ "] in selector [" + selectorName + "]");
				}

				if (!selectors.containsKey(target)) {
					portmeLog.warn("validateSelector() [Invalid Configuration]  - Unable to find the selector ["
							+ target + "] - which is required in selection statement [" + selection + "]");
				}

			} else if (callTo.equals("xml_def")) {
				try {
					if (!checkXpathSyntax(selectCondition)) {
						portmeLog.warn(
								"validateSelector() [Invalid Configuration]  - invalid selection syntax in selection statement ["
										+ selection + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn(
							"validateSelector() [Invalid Configuration]  - invalid selection syntax in selection statement ["
									+ selection + "] - Error is [" + ex.getMessageAndLocation() + "]");
				}

				if (!schemas.containsKey(target)) {
					portmeLog.warn("validateSelector() [Invalid Configuration]  - Unable to find the schema [" + target
							+ "] - which is required in selection statement [" + selection + "]");
				} else {
					String sampleXml = (String) schemas.get(target);
					sampleXml = sampleXml.trim();
					// if (sampleXml.startsWith("<") && sampleXml.endsWith(">")) {
					try {
						Document doc = stringToDom(sampleXml);
						validateSchema(doc.getDocumentElement(), target, schemas, selectors);
					} catch (SAXException e) {
						portmeLog.warn("validateSelector() [Invalid Configuration]  - in schema [" + target
								+ "] error is found at/close to line [ " + ((SAXParseException) e).getLineNumber()
								+ "] column [" + ((SAXParseException) e).getColumnNumber() + "]. Error type is ["
								+ ((SAXParseException) e).getMessage() + "]", e);
					} catch (ParserConfigurationException e) {
						portmeLog.warn("validateSelector() [Invalid Configuration]  - in schema [" + target
								+ "] error is found", e);
					} catch (IOException e) {
						portmeLog.warn("validateSelector() [Invalid Configuration]  - in schema [" + target
								+ "] error is found", e);
					}
					// }
				}

			}
		}

	}

	boolean checkXpath(String xpath) {

		StringTokenizer paths = new StringTokenizer(xpath, "/");
		boolean validXpath = true;

		while (paths.hasMoreTokens()) {
			String path = GlobalXMLGenerator.replaceAll(paths.nextToken(), "_", "");

			validXpath = path.matches("[A-Za-z0-9]+");
			if (!validXpath) {
				int startIdx = path.indexOf("[");

				if (startIdx != -1) {
					int endIdx = path.indexOf("]", startIdx);

					if (endIdx != -1) {
						path = path.substring(0, startIdx) + path.substring(endIdx + 1, path.length());
						validXpath = path.matches("[A-Za-z0-9]+");
					}

				}
			}

			if (!validXpath) {
				return validXpath;
			}
		}

		return true;
	}

	boolean checkXpathSyntax(String selectCondition) throws TransformerException {

		StringTokenizer selectConditions = new StringTokenizer(selectCondition, "|");

		try {
			Document doc = stringToDom("<Dummy><N1>a</N1></Dummy>");

			Node node = doc.getFirstChild();

			while (selectConditions.hasMoreTokens()) {
				String XPath = selectConditions.nextToken();

				retrieve(node, XPath, false);

				if (!checkXpath(XPath)) {
					return false;
				}
			}
		} catch (SAXException ex) {
		} catch (ParserConfigurationException ex) {
		} catch (IOException ex) {
		}

		return true;
	}

	void validateSchema(Node node, String schema, Map schemas, Map selectors) {

		NodeValue nodeValue = getValue(node);
		if (nodeValue.value != null) {
			Annotation annotation = getAnnotation(nodeValue.value);

			if (annotation.type == AnnotationType.UnKnown || annotation.type == AnnotationType.NoAnnotation) {
				portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
						+ "] invalid annotation is found in node [" + node.getNodeName() + "] invalid annotation is ["
						+ nodeValue.value + "]");
			} else if (annotation.type == AnnotationType.EmbedSourceAsCDATA) {
			} else if (annotation.type == AnnotationType.EmbedSourceAsBase64) {
			} else if (annotation.type == AnnotationType.HardCode) {
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValue) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValue) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithDefaultValue) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithoutDefaultValue) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInBase64OutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.ReferenceWithoutDefaultValueCdataInCdataOutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInBase64OutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.ReferenceWithDefaultValueCdataInCdataOutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithDefaultValueXmlInCdataOutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
				if (!selectors.containsKey(annotation.selector)) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found [selector not found] - in node [" + node.getNodeName()
							+ "] selector name [" + annotation.selector + "]");
				}
			} else if (annotation.type == AnnotationType.RetrieveByPathWithoutDefaultValueXmlInCdataOutWithProcess) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}

				if (!selectors.containsKey(annotation.selector)) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found [selector not found] - in node [" + node.getNodeName()
							+ "] selector name [" + annotation.selector + "] ");
				}
			} else if (annotation.type == AnnotationType.ReferenceCopyHierachyAsCdataWithoutDefaultValue) {
				try {
					if (!checkXpathSyntax(annotation.value)) {
						portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
								+ "] invalid annotation is found in node [" + node.getNodeName()
								+ "] invalid annotation is [" + annotation.value + "]");
					}
				} catch (TransformerException ex) {
					portmeLog.warn("validateSchema() [Invalid Configuration]  - in schema [" + schema
							+ "] invalid annotation is found in node [" + node.getNodeName()
							+ "] invalid annotation is [" + annotation.value + "]  - Error is ["
							+ ex.getMessageAndLocation() + "]");
				}
			}

		}

		ArrayList configurationChilds = getChildNodes(node);

		for (int i_configurationChild = 0; i_configurationChild < configurationChilds.size(); i_configurationChild++) {
			Node child = (Node) getChildNodes(node).get(i_configurationChild);
			validateSchema(child, schema, schemas, selectors);
		}
	}
}
