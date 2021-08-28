package com.soffid.iam.addons.xacml.service.xpath;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class SoffidDummyElement implements Element {
	String tag;
	private Document document;
	private Object underlyingObject;
	private SoffidDummyElement parent;
	
	public SoffidDummyElement(Document doc, SoffidDummyElement parent, String tag, Object value) {
		this.document = doc;
		this.tag =tag;
		this.underlyingObject = value;
		this.parent = parent;
	}
	
	public Object getUnderlyingObject() {
		return underlyingObject;
	}

	public void setUnderlyingObject(Object underlyingObject) {
		this.underlyingObject = underlyingObject;
	}

	public String getNodeName() {
		return tag;
	}

	public String getNodeValue() throws DOMException {
		return  underlyingObject == null ? "" : underlyingObject.toString();
	}

	public void setNodeValue(String nodeValue) throws DOMException {
	}

	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}

	public Node getParentNode() {
		return null;
	}

	SoffidNodeList children = null;
	private SoffidNodeList getChildren () {
		if (children == null)
			children = new SoffidNodeList();
		PropertyUtilsBean bean = new PropertyUtilsBean();
		Map prop;
		try {
			prop = bean.describe(underlyingObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (Object p: prop.keySet()) {
			Object v;
			try {
				v = bean.getProperty(bean, p.toString());
				if (v == null) {
					// Nothing to do
				} else if (v instanceof Collection) {
					for (Object vv: (Collection)v) {
						children.add(new SoffidDummyElement(document, parent, p.toString(), vv));
					}
				} else if (v.getClass().isArray()) {
					for (int i = 0; i < Array.getLength(v); i++) {
						children.add(new SoffidDummyElement(document, parent, p.toString(), Array.get(v, i)));
					}
				} else {
					children.add(new SoffidDummyElement(document, parent, p.toString(), v));
				}
			} catch (Exception e) {
			}
		}
		return children;
		
	}
	
	public NodeList getChildNodes() {
		return getChildren();
	}

	public Node getFirstChild() {
		NodeList l = getChildNodes();
		if (l.getLength() > 0) return l.item(0);
		else return null;
	}

	public Node getLastChild() {
		NodeList l = getChildNodes();
		if (l.getLength() > 0) return l.item(l.getLength()-1);
		else return null;
	}

	public Node getPreviousSibling() {
		if (parent == null)
			return null;
		SoffidNodeList l = parent.getChildren();
		Node previous = null;
		for (int i = 0; i < l.getLength(); i++)
			if (l.item(i) == this) 
				break;
			else
				previous = l.item(i);
		return previous;
	}

	public Node getNextSibling() {
		if (parent == null)
			return null;
		SoffidNodeList l = parent.getChildren();
		int i = 0;
		while (i < l.getLength() && l.item(i) != this)
			i++;
		return l.item(i);
	}

	public NamedNodeMap getAttributes() {
		return new SoffidNamedNodeMap();
	}

	public Document getOwnerDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return null;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return null;
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return null;
	}

	public Node appendChild(Node newChild) throws DOMException {
		return null;
	}

	public boolean hasChildNodes() {
		return false;
	}

	public Node cloneNode(boolean deep) {
		return null;
	}

	public void normalize() {

	}

	public boolean isSupported(String feature, String version) {
		return false;
	}

	public String getNamespaceURI() {
		return null;
	}

	public String getPrefix() {
		return null;
	}

	public void setPrefix(String prefix) throws DOMException {

	}

	public String getLocalName() {
		return tag;
	}

	public boolean hasAttributes() {
		return false;
	}

	public String getBaseURI() {
		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return 0;
	}

	public String getTextContent() throws DOMException {
		return underlyingObject == null ? null: underlyingObject.toString();
	}

	public void setTextContent(String textContent) throws DOMException {
	}

	public boolean isSameNode(Node other) {
		return other == this;
	}

	public String lookupPrefix(String namespaceURI) {
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return false;
	}

	public String lookupNamespaceURI(String prefix) {
		return null;
	}

	public boolean isEqualNode(Node arg) {
		return arg == this;
	}

	public Object getFeature(String feature, String version) {
		return null;
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return null;
	}

	public Object getUserData(String key) {
		return null;
	}

	public String getTagName() {
		return null;
	}

	public String getAttribute(String name) {
		return null;
	}

	public void setAttribute(String name, String value) throws DOMException {
	}

	public void removeAttribute(String name) throws DOMException {
	}

	public Attr getAttributeNode(String name) {
		return null;
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return null;
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return null;
	}

	public NodeList getElementsByTagName(String name) {
		SoffidNodeList nl = new SoffidNodeList();
		SoffidNodeList nl2 = new SoffidNodeList();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n = nl.item(i);
			if (n instanceof Element && ((Element)n).getTagName().equals(name))
				nl2.add(n);
		}
		return nl2;
	}

	public String getAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		return null;
	}

	public void setAttributeNS(String namespaceURI, String qualifiedName,
			String value) throws DOMException {

	}

	public void removeAttributeNS(String namespaceURI, String localName)
			throws DOMException {

	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName)
			throws DOMException {
		return null;
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
			throws DOMException {
		return null;
	}

	public boolean hasAttribute(String name) {
		return false;
	}

	public boolean hasAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		return false;
	}

	public TypeInfo getSchemaTypeInfo() {
		return null;
	}

	public void setIdAttribute(String name, boolean isId) throws DOMException {

	}

	public void setIdAttributeNS(String namespaceURI, String localName,
			boolean isId) throws DOMException {

	}

	public void setIdAttributeNode(Attr idAttr, boolean isId)
			throws DOMException {
	}

}
