package com.soffid.iam.addons.xacml.service.xpath;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SoffidNodeList implements NodeList {
	List<Node> l = new LinkedList<Node>();
	
	public void add(Node n) {
		l.add(n);
	}
	
	public Node item(int index) {
		return index >= 0 && index < l.size() ? l.get(index) : null;
	}

	public int getLength() {
		return l.size();
	}

}
