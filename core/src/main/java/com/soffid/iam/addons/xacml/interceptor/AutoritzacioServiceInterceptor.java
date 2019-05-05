package com.soffid.iam.addons.xacml.interceptor;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyDocument;
import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyElement;
import com.soffid.iam.addons.xacml.service.xpath.SoffidXPathBean;
import com.soffid.iam.utils.Security;
import com.soffid.iam.utils.SoffidAuthorization;

import es.caib.seycon.ng.comu.AutoritzacioRol;
import es.caib.seycon.ng.comu.ValorDomini;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.servei.AutoritzacioService;

public class AutoritzacioServiceInterceptor implements MethodInterceptor
{
	private static final String URN_OASIS_NAMES_TC_XACML_2_0_CONTEXT = "urn:oasis:names:tc:xacml:2.0:context";
	// Service bean
	private PolicySetService policySetService;
	private AutoritzacioService autoritzacioService;
	
	public AutoritzacioService getAutoritzacioService() {
		return autoritzacioService;
	}

	public void setAutoritzacioService(AutoritzacioService autoritzacioService) {
		this.autoritzacioService = autoritzacioService;
	}

	public PolicySetService getPolicySetService() {
		return policySetService;
	}

	public void setPolicySetService(PolicySetService policySetService) {
		this.policySetService = policySetService;
	}

	private Set<Result> evaluatePolicy (PolicyStatus ps, String authName, Object targetObject) throws UnknownHostException, URISyntaxException, InternalErrorException, ParserConfigurationException
	{
		LinkedList<Attribute> subjectAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> resourceAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> actionAttributes = new LinkedList<Attribute>();
		LinkedList<Attribute> environmentAttributes = new LinkedList<Attribute>();

		Execution execution = Executions.getCurrent();
		if (execution != null)
		{
			HttpServletRequest httpRequest = (HttpServletRequest) execution.getNativeRequest();
			// Subject
			if (httpRequest != null)
			{
				InetAddress addr = InetAddress.getByName(httpRequest.getRemoteAddr());
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
			}
		}
		
		// Split authorization
		int i = authName.lastIndexOf(':');
		if ( i >= 0)
		{
			resourceAttributes.add(new Attribute(new URI("com:soffid:iam:xacml:1.0:resource:soffid-object"), (String) null, null, 
				new StringAttribute( authName.substring(0, i))));
			// Action
			actionAttributes.add(new Attribute (new URI("urn:com:soffid:xacml:action:method"), (String) null, null, 
					new StringAttribute( authName.substring(i+1))));
		} else {
			resourceAttributes.add(new Attribute(new URI("com:soffid:iam:xacml:1.0:resource:soffid-object"), (String) null, null, 
					new StringAttribute( authName)));
		}
		
		
		// Enviromment
		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_TIME), (String) null, null, 
				new TimeAttribute( new Date())));

		environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE_TIME), (String) null, null, 
				new DateTimeAttribute( new Date())));
		
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
		e.setUnderlyingObject(new SoffidXPathBean(ctx, targetObject));

		RequestContext req = RequestResponseContextFactory.createRequestCtx();
		req.set(XACMLConstants.REQUEST_CTX, ctx);
		
		ResponseContext resp = policySetService.evaluate(ps.getPDPConfig(), req);
		ResponseCtx responseCtx = resp.get(XACMLConstants.RESPONSE_CTX);
		@SuppressWarnings("unchecked")
		Set<Result> results = responseCtx.getResults();
		return results;
	}

	public Object invoke(MethodInvocation mi) throws Throwable {
		if (Security.isSyncServer())
			return mi.proceed();
		
		PepConfiguration cfg = new PolicyManager().getCurrentPolicy();
		if (cfg.getRolePolicy().isEnabled())
		{
			if (mi.getMethod().getName().equals(("getUserAuthorizations")))
			{
				Collection<AutoritzacioRol> auths = (Collection<AutoritzacioRol>) mi.proceed();
				return filterAuths (cfg.getRolePolicy(), auths);
				
			}
			else if (mi.getMethod().getName().equals(("getUserAuthorizationsString"))) 
			{
				String[] auths = (String[]) mi.proceed();
				String user = null;
				if (mi.getArguments().length > 0) user = (String) mi.getArguments()[0];
				return filterStrings (cfg.getRolePolicy(), user, auths);
			}
		}
		if (cfg.getAuthPolicy().isEnabled())
		{
			if (mi.getMethod().getName().equals(("hasPermission"))) 
			{
				String action = (String) mi.getArguments()[0];
				Object object = mi.getArguments()[1];
				return handleHasPermission(action, object, mi);
			}
		}
		return mi.proceed();
	}

	private Collection<AutoritzacioRol>  filterAuths(PolicyStatus ps, Collection<AutoritzacioRol> previousAuths) throws InternalErrorException, UnknownHostException, URISyntaxException, ParserConfigurationException {
		LinkedList<AutoritzacioRol> result = new LinkedList<AutoritzacioRol>();
		
		@SuppressWarnings("unchecked")
		Collection<SoffidAuthorization> autList = getAutoritzacioService().findAuthorizations(null, null, null);
		
		for ( SoffidAuthorization aut: autList)
		{
			String authName = aut.getCodi();
			int decision = Result.DECISION_INDETERMINATE;
			for (Result r: evaluatePolicy(ps, authName, null))
			{
				decision  = r.getDecision();
			}
			if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
			{
				// Use previous auth
				for (AutoritzacioRol prev: previousAuths)
				{
					if (prev.getAutoritzacio().equals (authName))
						result.add(prev);
				}
			}
			else if (decision == Result.DECISION_PERMIT)
			{
				AutoritzacioRol auth = new AutoritzacioRol();
				auth.setAmbit(aut.getAmbit());
				auth.setAutoritzacio(aut.getCodi());
				auth.setDescripcio(aut.getDescripcio());
				auth.setHereta(aut.getHereta());
				auth.setScope(aut.getScope());
				auth.setTipusDomini(aut.getTipusDomini());
				List<ValorDomini> vd = Collections.emptyList();
				auth.setValorDominiRolUsuari(vd);
				result.add(auth);
			}
		}
		
		return result;
	}

	private String[]  filterStrings(PolicyStatus ps, String user, String[] auths) 
			throws InternalErrorException, UnknownHostException, URISyntaxException, ParserConfigurationException {
		LinkedList<String> result = new LinkedList<String>();
		
		@SuppressWarnings("unchecked")
		Collection<SoffidAuthorization> autList = getAutoritzacioService().findAuthorizations(null, null, null);
		
		Security.nestedLogin(user, auths);
		try 
		{
			for ( SoffidAuthorization aut: autList)
			{
				String authName = aut.getCodi();
				int decision = Result.DECISION_INDETERMINATE;
				for (Result r: evaluatePolicy(ps, authName, null))
				{
					decision  = r.getDecision();
				}
				if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
				{
					// Use previous auth
					for (String prev: auths)
					{
						if (prev.equals (authName) || prev.startsWith(authName+"/"))
							result.add(prev);
					}
				}
				else if (decision == Result.DECISION_PERMIT)
				{
					result.add(authName);
					result.add(authName+"/*");
				}
			}
		} finally {
			Security.nestedLogoff();
		}
			
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Interceptor for handleHasPermissions
	 * @param action
	 * @param object
	 * @param mi 
	 * @return
	 * @throws Throwable 
	 */
	protected boolean handleHasPermission(String action, Object object, MethodInvocation mi)
			throws Throwable {
		PepConfiguration cfg = new PolicyManager().getCurrentPolicy();
		if (cfg.getAuthPolicy().isEnabled())
		{
			PolicyStatus ps = cfg.getAuthPolicy();
			int decision = Result.DECISION_INDETERMINATE;
			for (Result r: evaluatePolicy(ps, action, object))
			{
				decision  = r.getDecision();
			}
			if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
				return (Boolean) mi.proceed();
			else if (decision == Result.DECISION_PERMIT)
				return true;
			else
				return false;
		}
		else
			return (Boolean) mi.proceed();
	}

}
