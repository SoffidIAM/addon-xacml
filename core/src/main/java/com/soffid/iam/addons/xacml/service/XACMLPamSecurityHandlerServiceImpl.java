package com.soffid.iam.addons.xacml.service;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.Obligation;
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

import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyDocument;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyElement;
import com.soffid.iam.addons.xacml.service.xpath.SoffidXPathBean;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.PamSecurityCheck;
import com.soffid.iam.api.RequestedObligation;
import com.soffid.iam.api.VaultFolder;
import com.soffid.iam.model.AccountEntity;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

public class XACMLPamSecurityHandlerServiceImpl extends XACMLPamSecurityHandlerServiceBase {

	@Override
	protected PamSecurityCheck handleCheckPermissionImpl(AccountEntity accountEntity, String action) throws Exception {
		PepConfiguration cfg = new PolicyManager().getCurrentPolicy();
		if (!cfg.getVaultPolicy().isEnabled())
			return null;
		else
		{
			PolicyStatus ps = cfg.getVaultPolicy();
			Account account = getAccountEntityDao().toAccount(accountEntity);
			Set<Result> r = evaluatePolicy(ps, account, action);
			if (r == null)
			{
				return null;
			}
			else
			{
				PamSecurityCheck psc = new PamSecurityCheck();
				psc.setAllowed(true);
				psc.setObligations(new LinkedList<RequestedObligation>());
				for (Result rr: r)
				{
					int decision = rr.getDecision() ;
					if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
					{
					}
					else if (decision == Result.DECISION_PERMIT)
					{
						psc.setAllowed(true);
					}
					else
					{
						psc.setAllowed(false);
					}
					for (Obligation ob : (Collection<Obligation>) rr.getObligations()) {
						RequestedObligation rob = new RequestedObligation();
						rob.setObligation(ob.getId().toString());
						rob.setAttributes(new HashMap<String, String>());
						for ( Attribute att : (Collection<Attribute>) ob.getAssignments()) {
							String key = att.getId().toString();
							String value = att.getValue().getValue().toString();
							rob.getAttributes().put(key, value);
						}
						psc.getObligations().add(rob);
					}
				}
				return psc;
			}
		}
	}

	@Override
	protected PamSecurityCheck handleGetObligations(AccountEntity account, String action) throws Exception {
		return null;
	}

	@Override
	protected void handleCheckPermission(AccountEntity account, String action) throws Exception {
	}

	private Set<Result> evaluatePolicy (PolicyStatus ps, Account account, String action) throws UnknownHostException, URISyntaxException, InternalErrorException, ParserConfigurationException
	{
		LinkedList<Attribute> subjectAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> resourceAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> actionAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> environmentAttributes = new LinkedList<Attribute>();

		String ip = Security.getClientIp();
		InetAddress addr = InetAddress.getByName(ip);
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
		
		// Split authorization
		// Action
		actionAttributes.add(new Attribute (new URI("urn:com:soffid:xacml:action:method"), (String) null, null, 
				new StringAttribute( action)));
		
		// Enviromment
		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_TIME), (String) null, null, 
				new TimeAttribute( new Date())));

		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE_TIME), (String) null, null, 
				new DateTimeAttribute( new Date())));
		
		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE), (String) null, null, 
				new DateAttribute( new Date())));
		
		// Resources
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.ACCESS_LEVEL_IDENTIFIER), (String) null, null, 
				new StringAttribute(account.getAccessLevel().getValue())));
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.ACCOUNT_IDENTIFIER), (String) null, null, 
				new StringAttribute(account.getName())));
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.SYSTEM_IDENTIFIER), (String) null, null, 
				new StringAttribute(account.getSystem())));
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.LOGIN_IDENTIFIER), (String) null, null, 
				new StringAttribute(account.getLoginName())));
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.URL_RESOURCE_IDENTIFIER), (String) null, null, 
				new StringAttribute(account.getLoginUrl())));
		String vaultFolder = "";
		Long folder = account.getVaultFolderId();
		while (folder != null) {
			VaultFolder vf = getVaultService().findFolder(folder);
			if (vf == null) break;
			vaultFolder = "/"+vf.getName()+vaultFolder;
			folder = vf.getParentId();
		}
		resourceAttributes.add(new Attribute (new URI(SoffidAttributeFinderModule.VAULT_IDENTIFIER), (String) null, null, 
				new StringAttribute(vaultFolder)));
		// Create empty document
		SoffidDummyDocument d = new SoffidDummyDocument();
		SoffidDummyElement e = new SoffidDummyElement();
		e.setDocument(d);
		d.setRootNode(e);
		d.appendChild( d.createElement("Request") );
		
		RequestCtx ctx = new RequestCtx(Collections.singletonList(new Subject (subjectAttributes)), 
				resourceAttributes, actionAttributes, environmentAttributes, e);
		e.setUnderlyingObject(new SoffidXPathBean(ctx, account));

		RequestContext req = RequestResponseContextFactory.createRequestCtx();
		req.set(XACMLConstants.REQUEST_CTX, ctx);
		
		ResponseContext resp = getPolicySetService().evaluate(ps.getPDPConfig(), req);
		ResponseCtx responseCtx = resp.get(XACMLConstants.RESPONSE_CTX);
		@SuppressWarnings("unchecked")
		Set<Result> results = responseCtx.getResults();
		return results;
	}
}
