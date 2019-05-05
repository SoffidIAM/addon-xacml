package com.soffid.iam.addons.xacml.service.xpath;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.DateTimeAttribute;
import org.jboss.security.xacml.sunxacml.ctx.Attribute;

public class AttributeBean {

	private Attribute att;
	
	public String getAttributeId() {
		return att.getId().toString();
	}
	public URI getDataType() {
		return att.getType();
	}
	public String getIssuer() {
		return att.getIssuer();
	}
	public DateTimeAttribute getIssueInstant() {
		return att.getIssueInstant();
	}
	public AttributeBean(Attribute att) {
		this.att = att;
	}
	public String getAttributeValue ()
	{
		return att.encode();
	}
}
