package com.soffid.iam.addons.xacml.service.xpath;

import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;

public class SoffidXPathBean {
	RequestCtx ctx;
	private Object targetObject;
	private Object Request;

	public SoffidXPathBean(RequestCtx ctx, Object targetObject) {
		super();
		this.ctx = ctx;
		this.targetObject = targetObject;
	}

	public Object getRequest () {
		return new RequestBean (ctx, targetObject);
	}
}
