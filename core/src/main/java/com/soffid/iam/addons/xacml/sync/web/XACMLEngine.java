package com.soffid.iam.addons.xacml.sync.web;

import java.io.ByteArrayOutputStream;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.Indenter;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.xacml.XacmlServiceLocator;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.addons.xacml.service.SoffidAttributeFinderModule;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyDocument;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyElement;
import com.soffid.iam.addons.xacml.service.xpath.SoffidXPathBean;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Host;
import com.soffid.iam.api.System;
import com.soffid.iam.api.UserAccount;
import com.soffid.iam.service.NetworkService;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

public class XACMLEngine {
	static PolicySetService policySetService = XacmlServiceLocator.instance().getPolicySetService();
	static NetworkService networkService = ServiceLocator.instance().getNetworkService();
	static Log log = LogFactory.getLog(XACMLEngine.class);
	public Set<Result> testResource (PolicyStatus ps, String remoteAddress, Map<String,Object> token, String resource, String method,
			Map<String,String> context) throws Exception
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
				environmentAttributes, context);

	}

	private Set<Result> test(PolicyStatus ps, String remoteAddress, Map<String, Object> token,
			LinkedList<Attribute> subjectAttributes, LinkedList<Attribute> resourceAttributes,
			LinkedList<Attribute> actionAttributes, LinkedList<Attribute> environmentAttributes,
			Map<String,String> context)
			throws UnknownHostException, URISyntaxException, InternalErrorException, ParserConfigurationException 
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

		Host host = networkService.findHostByIp(remoteAddress);
		if (host != null)
		{
			subjectAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_DNS_NAME), (String) null, null, 
					new StringAttribute(host.getName())));
			subjectAttributes.add(new Attribute (new URI("urn:com:soffid:host:os"), (String) null, null, 
					new StringAttribute(host.getOs())));
			subjectAttributes.add(new Attribute (new URI("urn:com:soffid:host:dhcp"), (String) null, null, 
					new StringAttribute(host.getDhcp())));
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
		Document d;
		Element e;
		d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		e = d.createElement("Request");
		d.appendChild(e);
		if (context != null)
		{
			for ( Entry<String, String> pair: context.entrySet())
			{
				e.setAttribute(pair.getKey(), pair.getValue());
				environmentAttributes.add(new Attribute (new URI(pair.getKey()), (String) null, null, 
						new StringAttribute( pair.getValue() )));
			}
		}
		
		RequestCtx ctx = new RequestCtx(Collections.singletonList(new Subject (subjectAttributes)), 
				resourceAttributes, actionAttributes, environmentAttributes, e);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ctx.encode(out, new Indenter(5));
		log.info ("Testing rules for:\n"+out.toString());

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
			String system, Map<String,String> context) throws Exception 
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
		
		Account acc = ServiceLocator.instance().getAccountService().findAccount(account, system);
		if (acc != null) {
			if (acc.getVaultFolder() != null)
				resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.VAULT_IDENTIFIER), (String) null, null, 
					new StringAttribute( acc.getVaultFolder() )));

			resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.LOGIN_IDENTIFIER), (String) null, null, 
					new StringAttribute( acc.getLoginName() == null ? acc.getName(): acc.getLoginName() )));

			resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.ACCESS_LEVEL_IDENTIFIER), (String) null, null, 
					new StringAttribute( acc.getAccessLevel().getValue() )));
		}
		
		return test(ps, remoteAddress, token, subjectAttributes, resourceAttributes, actionAttributes,
				environmentAttributes, context);
	}

}
