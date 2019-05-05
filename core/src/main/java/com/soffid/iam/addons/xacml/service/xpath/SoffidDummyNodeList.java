package com.soffid.iam.addons.xacml.service.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SoffidDummyNodeList implements NodeList {
	SoffidDummyElement node;
	
	public SoffidDummyNodeList(SoffidDummyElement node) {
		super();
		this.node = node;
	}

	public Node item(int index) {
		if (index == 0)
			return node;
		else
			return null;
	}

	public int getLength() {
		return node == null ? 0 : 1;
	}

}
