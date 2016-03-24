package com.soffid.iam.addons.xacml.service.xpath;

import java.util.LinkedList;
import java.util.List;

import org.jboss.security.xacml.sunxacml.ctx.Attribute;
import org.jboss.security.xacml.sunxacml.ctx.Subject;

public class AttributeListBean {

	private List<Attribute> attribute;
	private Object resourceContent;

	public AttributeListBean(List<Attribute> attributes) {
		this.attribute = attributes;
	}
	
	public List<AttributeBean> getAttribute ()
	{
		LinkedList<AttributeBean> sb = new LinkedList<AttributeBean>();
		for (Attribute att : attribute)
		{
			sb.add (new AttributeBean(att));
		}
		return sb;

	}

	public void setResourceContent(Object resourceContentObject) {
		this.resourceContent = resourceContentObject;
	}

	public Object getResourceContent() {
		return resourceContent;
	}

}
