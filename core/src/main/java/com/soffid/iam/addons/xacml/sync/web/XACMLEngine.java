package com.soffid.iam.addons.xacml.sync.web;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.attr.DateAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateTimeAttribute;
import org.jboss.security.xacml.sunxacml.attr.IPv4AddressAttribute;
import org.jboss.security.xacml.sunxacml.attr.IPv6AddressAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.attr.TimeAttribute;
import org.jboss.security.xacml.sunxacml.ctx.Attribute;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.ResponseCtx;
import org.jboss.security.xacml.sunxacml.ctx.Result;
import org.jboss.security.xacml.sunxacml.ctx.Subject;

import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.xacml.XacmlServiceLocator;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.addons.xacml.service.SoffidAttributeFinderModule;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyDocument;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyElement;
import com.soffid.iam.addons.xacml.service.xpath.SoffidXPathBean;
import com.soffid.iam.api.System;
import com.soffid.iam.api.UserAccount;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

public class XACMLEngine {
	static PolicySetService policySetService = XacmlServiceLocator.instance().getPolicySetService();
	
	public Set<Result> testResource (PolicyStatus ps, String remoteAddress, Map<String,Object> token, String resource, String method) throws Exception
	{
		LinkedList<Attribute> subjectAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> resourceAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> actionAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> environmentAttributes = new LinkedList<Attribute>();

		resourceAttributes.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-location"), (String) null, null, 
				new StringAttribute( resource )));
		// Action
		actionAttributes.add(new Attribute (new URI("urn:com:soffid:xacml:action:method"), (String) null, null, 
				new StringAttribute( method)));
		
		return test(ps, remoteAddress, token, subjectAttributes, resourceAttributes, actionAttributes,
				environmentAttributes);

	}

	private Set<Result> test(PolicyStatus ps, String remoteAddress, Map<String, Object> token,
			LinkedList<Attribute> subjectAttributes, LinkedList<Attribute> resourceAttributes,
			LinkedList<Attribute> actionAttributes, LinkedList<Attribute> environmentAttributes)
			throws UnknownHostException, URISyntaxException, InternalErrorException 
	{
		InetAddress addr = InetAddress.getByName(remoteAddress);
		if (addr instanceof Inet4Address)
		{
			subjectAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_IP_ADDRESS), (String) null, null, 
					new IPv4AddressAttribute(addr)));
			subjectAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_IP_ADDRESS), (String) null, null, 
					new StringAttribute(addr.getHostAddress())));
		}
		if (addr instanceof Inet6Address)
		{
			subjectAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_IP_ADDRESS), (String) null, null, 
					new IPv6AddressAttribute(addr)));
			subjectAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_IP_ADDRESS), (String) null, null, 
					new StringAttribute(addr.getHostAddress())));
		}

		String subject = "anonymous";
		// Split authorization
		for (Entry<String, Object> entry: token.entrySet())
		{
			Object v = entry.getValue();
			if (v != null)
			{
				if (entry.getKey().equals("sub"))
				{
					subjectAttributes.add(new Attribute(new URI(SoffidAttributeFinderModule.USER_IDENTIFIER), (String) null, null, 
							new StringAttribute( v.toString() )));
					System soffid = ServiceLocator.instance().getDispatcherService().findSoffidDispatcher();
					for ( UserAccount account: ServiceLocator.instance().getAccountService().findUsersAccounts(v.toString(), soffid.getName()))
					{
						if (!account.isDisabled())
						{
							subject = account.getName();
							subjectAttributes.add(new Attribute(new URI(SoffidAttributeFinderModule.SUBJECT_IDENTIFIER), (String) null, null, 
									new StringAttribute( account.getName() )));
							subjectAttributes.add(new Attribute(new URI(SoffidAttributeFinderModule.SYSTEM_IDENTIFIER), (String) null, null, 
									new StringAttribute( soffid.getName() )));
							break;
						}
					}
				} 
				else if (entry.getKey().equals("iss"))
					subjectAttributes.add(new Attribute(new URI("urn:com:soffid:oidc:iss"), (String) null, null, 
							new StringAttribute( v.toString() )));
				else if (entry.getKey().equals("aud"))
					subjectAttributes.add(new Attribute(new URI("urn:com:soffid:oidc:aud"), (String) null, null, 
							new StringAttribute( v.toString() )));
				if (v instanceof Date)
					subjectAttributes.add(new Attribute(new URI("urn:com:soffid:xacml:subject:user:att:"+entry.getKey()), (String) null, null, 
						new DateTimeAttribute( (Date) v )));
				else
					subjectAttributes.add(new Attribute(new URI("urn:com:soffid:xacml:subject:user:att:"+entry.getKey()), (String) null, null, 
						new StringAttribute( v.toString() )));
			}
		}
		// Enviromment
		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE_TIME), (String) null, null, 
				new DateTimeAttribute( new Date())));

		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_TIME), (String) null, null, 
				new TimeAttribute( new Date())));
		
		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE), (String) null, null, 
				new DateAttribute( new Date())));

		// Create empty document
		SoffidDummyDocument d = new SoffidDummyDocument();
		SoffidDummyElement e = new SoffidDummyElement();
		e.setDocument(d);
		d.setRootNode(e);
		d.appendChild( d.createElement("Request") );

		RequestCtx ctx = new RequestCtx(Collections.singletonList(new Subject (subjectAttributes)), 
				resourceAttributes, actionAttributes, environmentAttributes, e);

		RequestContext req = RequestResponseContextFactory.createRequestCtx();
		req.set(XACMLConstants.REQUEST_CTX, ctx);

		Security.nestedLogin(subject, Security.ALL_PERMISSIONS);
		try {

			ResponseContext resp = policySetService.evaluate(ps.getPDPConfig(), req);
			ResponseCtx responseCtx = resp.get(XACMLConstants.RESPONSE_CTX);
			@SuppressWarnings("unchecked")
			Set<Result> results = responseCtx.getResults();
	
			return results;
		} finally {
			Security.nestedLogoff();
		}
	}

	
	public Set<Result> testAccount(PolicyStatus ps, String remoteAddress, Map<String, Object> token, String account,
			String system) throws Exception 
	{
		LinkedList<Attribute> subjectAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> resourceAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> actionAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> environmentAttributes = new LinkedList<Attribute>();

		resourceAttributes.add(new Attribute(new URI(SoffidAttributeFinderModule.ACCOUNT_IDENTIFIER), (String) null, null, 
				new StringAttribute( account )));
		// Action
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.SYSTEM_IDENTIFIER), (String) null, null, 
				new StringAttribute( system )));
		
		return test(ps, remoteAddress, token, subjectAttributes, resourceAttributes, actionAttributes,
				environmentAttributes);
	}

}
