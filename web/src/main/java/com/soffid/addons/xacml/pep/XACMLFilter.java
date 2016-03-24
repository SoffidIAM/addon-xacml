package com.soffid.addons.xacml.pep;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.attr.AnyURIAttribute;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
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

import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;

public class XACMLFilter implements Filter {

	WebPolicyManager policyManager;
	private PolicySetService policySetService;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		policyManager  = new WebPolicyManager ();
		try {
			InitialContext ctx = new InitialContext ();
			PolicySetServiceHome home = (PolicySetServiceHome) ctx.lookup(PolicySetServiceHome.JNDI_NAME);
			policySetService = home.create();
		} catch (NamingException e) {
			throw new ServletException (e);
		} catch (CreateException e) {
			throw new ServletException (e);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			PepConfiguration pc  = policyManager.getCurrentPolicy(httpRequest);
			PolicyStatus ps = pc.getWebPolicy();
			if (ps != null && ps.isEnabled())
			{
				
				URL originalUrl;
				String originalUri= (String) request.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH");
				if (originalUri == null)
				{
					originalUri = httpRequest.getRequestURI();
					originalUrl = new URL (httpRequest.getRequestURL().toString());
				}
				else
				{
					originalUrl = new URL (httpRequest.getScheme()+"://"+httpRequest.getServerName()+":"+httpRequest.getServerPort()+
							originalUri);
				}
					
				RequestContext req = RequestResponseContextFactory.createRequestCtx();
				
				
				LinkedList<Attribute> subjectAttributes = new LinkedList<Attribute>();
				LinkedList<Attribute> resourceAttributes = new LinkedList<Attribute>();
				LinkedList<Attribute> actionAttributes = new LinkedList<Attribute>();
				LinkedList<Attribute> environmentAttributes = new LinkedList<Attribute>();

				// Subject
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
				
				// Resource
				resourceAttributes.add(new Attribute(new URI(XACMLConstants.ATTRIBUTEID_RESOURCE_ID), (String) null, null, 
						new StringAttribute( originalUri)));
				resourceAttributes.add(new Attribute(new URI(XACMLConstants.ATTRIBUTEID_RESOURCE_LOCATION), (String) null, null, 
						new StringAttribute( originalUri)));
				resourceAttributes.add(new Attribute(new URI(XACMLConstants.ATTRIBUTEID_RESOURCE_LOCATION), (String) null, null, 
						new AnyURIAttribute( new URI(originalUri))));
				
				
				// Action
				actionAttributes.add(new Attribute (new URI("urn:com:soffid:xacml:action:method"), (String) null, null, 
						new StringAttribute( httpRequest.getMethod())));
				
				
				// Enviromment
				environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_TIME), (String) null, null, 
						new TimeAttribute( new Date())));

				environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE_TIME), (String) null, null, 
						new DateTimeAttribute( new Date())));
				
				environmentAttributes.add(new Attribute (new URI(XACMLConstants.ATTRIBUTEID_CURRENT_DATE), (String) null, null, 
						new DateAttribute( new Date())));

				RequestCtx ctx = new RequestCtx(Collections.singletonList(new Subject (subjectAttributes)), 
						resourceAttributes, actionAttributes, environmentAttributes);
				req.set(XACMLConstants.REQUEST_CTX, ctx);
				
				ResponseContext resp = policySetService.evaluate(ps.getPDPConfig(), req);
				ResponseCtx responseCtx = resp.get(XACMLConstants.RESPONSE_CTX);
				@SuppressWarnings("unchecked")
				Set<Result> results = responseCtx.getResults();
				for (Result result: results)
				{
					if (result.getDecision() == XACMLConstants.DECISION_DENY)
					{
						ServletOutputStream out = httpResponse.getOutputStream();
						httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
						httpResponse.setContentType("text/html");
						httpResponse.setCharacterEncoding("utf-8");
						out.println("<html><body><p>Access is forbidden due to XACML restrictions</p>");
						if (result.getResource() != null)
						{
							out.print("<p>Resource: ");
							out.print(result.getResource());
							out.println("</p>");
						}
						if (result.getStatus().getMessage() != null)
						{
							out.print("<p>Status: ");
							out.print(result.getStatus().getMessage());
							out.println("</p>");
						}
						out.println("</body></html>");
						out.close();
						return;
					}
					if (result.getDecision() == XACMLConstants.DECISION_INDETERMINATE)
					{
						ServletOutputStream out = httpResponse.getOutputStream();
						httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
						httpResponse.setContentType("text/html");
						httpResponse.setCharacterEncoding("utf-8");
						out.println("<html><body><p>Access is forbidden due to an internal error produced checking XACML restrictions</p>");
						if (result.getResource() != null)
						{
							out.print("<p>Resource: " + originalUrl);
							out.println("</p>");
						}
						if (result.getStatus().getMessage() != null)
						{
							out.print("<p>Status: ");
							out.print(result.getStatus().getMessage());
							out.println("</p>");
						}
						out.println("</body></html>");
						out.close();
						return;
					}
				}
			}
		} catch (Exception e) {
			ServletOutputStream out = httpResponse.getOutputStream();
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			httpResponse.setContentType("text/html");
			httpResponse.setCharacterEncoding("utf-8");
			out.println("<html><body><p>An error has been detected while evaluating XACML rules</p>");
			out.print("<p><b><pre>");
			out.println(e.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
			out.println ("</pre></b></p>");
			out.println("</body></html>");
			out.close();
			return;
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}

}
