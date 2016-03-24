package com.soffid.iam.addons.xacml.service.xpath;

import java.util.LinkedList;
import java.util.List;

import org.jboss.security.xacml.sunxacml.ctx.Attribute;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.Subject;

public class RequestBean {

	private RequestCtx ctx;
	private Object targetObject;

	public RequestBean(RequestCtx ctx, Object targetObject) {
		this.ctx = ctx;
		this.targetObject = targetObject;
	}

	public List<AttributeListBean> getSubject ()
	{
		LinkedList<AttributeListBean> sb = new LinkedList<AttributeListBean>();
		for (Subject subject : (List<Subject>) ctx.getSubjectsAsList())
		{
			sb.add (new AttributeListBean((List<Attribute>) subject.getAttributes()));
		}
		return sb;
	}

	public AttributeListBean getEnvironment ()
	{
		return new AttributeListBean((List<Attribute>) ctx.getActionAsList());
	}

	public AttributeListBean getResource ()
	{
		AttributeListBean alb = new AttributeListBean((List<Attribute>) ctx.getActionAsList());
		alb.setResourceContent (targetObject);
		return alb;
	}

}
