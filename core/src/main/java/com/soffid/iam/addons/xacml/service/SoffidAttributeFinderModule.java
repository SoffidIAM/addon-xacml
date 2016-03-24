/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.soffid.iam.addons.xacml.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.attr.Base64BinaryAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.sunxacml.ctx.Status;
import org.jboss.security.xacml.sunxacml.finder.AttributeFinderModule;
import org.w3c.dom.Node;

import com.soffid.iam.addons.xacml.service.xpath.SoffidDummyElement;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.comu.Account;
import es.caib.seycon.ng.comu.DadaUsuari;
import es.caib.seycon.ng.comu.RolGrant;
import es.caib.seycon.ng.comu.UserAccount;
import es.caib.seycon.ng.comu.Usuari;
import es.caib.seycon.ng.comu.UsuariGrup;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.servei.AccountService;
import es.caib.seycon.ng.servei.AplicacioService;
import es.caib.seycon.ng.servei.GrupService;
import es.caib.seycon.ng.servei.InternalPasswordService;
import es.caib.seycon.ng.servei.UsuariService;
import es.caib.zkib.jxpath.JXPathContext;
import es.caib.zkib.jxpath.JXPathContextFactory;
import es.caib.zkib.jxpath.Pointer;

//$Id: TestRoleAttributeFinderModule.java 58115 2006-11-04 08:42:14Z scott.stark@jboss.org $

/**
 * An attribute finder module for testing that only deals with the role
 * identifier called as "urn:oasis:names:tc:xacml:1.0:example:attribute:role"
 * 
 * @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 * @since May 26, 2006
 * @version $Revision: 58115 $
 */
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class SoffidAttributeFinderModule extends AttributeLocator {
	/**
	 * XACML Identifier supported by this module
	 */

	public static final String SUBJECT_IDENTIFIER = XACMLConstants.ATTRIBUTEID_SUBJECT_ID;
	public static final String USER_IDENTIFIER = "urn:com:soffid:xacml:subject:user";
	public static final String ACCOUNT_IDENTIFIER = "urn:com:soffid:xacml:subject:account";
	public static final String SYSTEM_IDENTIFIER = "urn:com:soffid:xacml:subject:system";
	public static final String ROLE_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:subject:role";
	public static final String GROUP_IDENTIFIER = "urn:com:soffid:xacml:subject:groups";
	public static final String PRIMARY_GROUP_IDENTIFIER = "urn:com:soffid:xacml:subject:primaryGroup";
	public static final String CUSTOM_ATTRIBUTE_PREFIX = "urn:com:soffid:xacml:subject:user:att:";

	// subject-id standard identifier
	private static URI SUBJECT_IDENTIFIER_URI = null;

	// initialize the standard subject identifier
	static {
		try {
			SUBJECT_IDENTIFIER_URI = new URI(SUBJECT_IDENTIFIER);
		} catch (URISyntaxException ex) {
		}
	};

	/**
	 * Default constructor.
	 */
	public SoffidAttributeFinderModule() {

	}

	/**
	 * @see AttributeFinderModule#isDesignatorSupported()
	 * 
	 * @return true
	 */
	public boolean isDesignatorSupported() {
		return true;
	}

	/**
	 * @see AttributeFinderModule#getSupportedDesignatorTypes() Returns only
	 *      <code>SUBJECT_TARGET</code> since this module only supports Subject
	 *      attributes.
	 * 
	 * @return a <code>Set</code> with an <code>Integer</code> of value
	 *         <code>AttributeDesignator.SUBJECT_TARGET</code>
	 */
	public Set getSupportedDesignatorTypes() {
		Set set = new HashSet();
		set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));
		return set;
	}

	/**
	 * @see AttributeFinderModule#getSupportedIds()
	 * 
	 * @return a <code>Set</code> containing <code>ROLE_IDENTIFIER</code>
	 */
	public Set getSupportedIds() {
		Set set = new HashSet();
		set.add(ROLE_IDENTIFIER);
		set.add(GROUP_IDENTIFIER);
		set.add(PRIMARY_GROUP_IDENTIFIER);
		set.add(USER_IDENTIFIER);
		set.add(ACCOUNT_IDENTIFIER);
		set.add(SUBJECT_IDENTIFIER);
		return set;
	}

	/**
	 * Supports the retrieval of exactly one kind of attribute.
	 */
	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			URI issuer, URI subjectLogger, EvaluationCtx context,
			int designatorType) {
		// Check the identifier
		if (!attributeId.toString().equals(ROLE_IDENTIFIER)
				&& !attributeId.toString().equals(PRIMARY_GROUP_IDENTIFIER)
				&& !attributeId.toString().equals(GROUP_IDENTIFIER)
				&& !attributeId.toString().equals(USER_IDENTIFIER)
				&& !attributeId.toString().equals(SUBJECT_IDENTIFIER)
				&& !attributeId.toString().equals(ACCOUNT_IDENTIFIER)
				&& !attributeId.toString().startsWith(CUSTOM_ATTRIBUTE_PREFIX))
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// Did they ask for a String??
		if (!attributeType.toString().equals(StringAttribute.identifier))
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// Retrieve the subject identifer from the context
		try {
			String account = null;
			String domain = null;
			if (! attributeId.toString().equals(SUBJECT_IDENTIFIER) &&
				! attributeId.toString().equals(SYSTEM_IDENTIFIER))
			{
				account = getAttributeValue(SUBJECT_IDENTIFIER, issuer, subjectLogger, context);
				domain = getAttributeValue(XACMLConstants.ATTRIBUTEID_SUBJECT_ID_QUALIFIER, issuer, subjectLogger, context);
			}
			InternalPasswordService ips = ServiceLocator.instance().getInternalPasswordService();
			if (account == null)
			{
				account = Security.getCurrentAccount();
			}
			if (domain == null)
				domain = ips.getDefaultDispatcher();
	
			if (account == null)
			{
				ArrayList code = new ArrayList();
				code.add(Status.STATUS_MISSING_ATTRIBUTE);
				Status status = new Status(code, "missing subject-id");
				return new EvaluationResult(status);
			}
			
			// Finally search for the subject with the role-mapping defined,
			// and if there is a match, add their role
			BagAttribute returnBag = null;
			returnBag = retrieveAttributtes(account, domain, attributeType,attributeId);
			return new EvaluationResult(returnBag);
		} catch (URISyntaxException e) {
			ArrayList code = new ArrayList();
			code.add(Status.STATUS_PROCESSING_ERROR);
			Status status = new Status(code, e.toString());
			return new EvaluationResult(status);
		} catch (InternalErrorException e) {
			ArrayList code = new ArrayList();
			code.add(Status.STATUS_PROCESSING_ERROR);
			Status status = new Status(code, e.toString());
			return new EvaluationResult(status);
		}
	}

	private String getAttributeValue(String attribute, URI issuer,
			URI subjectLogger, EvaluationCtx context) throws URISyntaxException {
		String value = null;
		EvaluationResult result = context.getSubjectAttribute(URI.create(StringAttribute.identifier),
				new URI(attribute), issuer, subjectLogger);
		
		if (! result.indeterminate()) {
			// Check that we succeeded in getting the subject identifier
			if (result.getAttributeValue() == null)
				return null;
			else if (result.getAttributeValue().isBag())
			{
				BagAttribute bag = (BagAttribute) (result.getAttributeValue());
				if (! bag.isEmpty()) {
					Iterator it = bag.iterator();
					while (it.hasNext()) 
					{
						StringAttribute attr = (StringAttribute) (it.next());
						value = attr.getValue();
					}
				}
			} else {
				return result.getAttributeValue().getValue().toString();
			}
		}
		return value;
	}

	
	private BagAttribute retrieveAttributtes(String accountName, String domain,
			URI attributeType, URI attributeId) throws InternalErrorException {
		BagAttribute returnBag = null;

		UsuariService usuariService = ServiceLocator.instance()
				.getUsuariService();
		AccountService accountService = ServiceLocator.instance()
				.getAccountService();
		AplicacioService aplicacioService = ServiceLocator.instance()
				.getAplicacioService();
		InternalPasswordService ips = ServiceLocator.instance()
				.getInternalPasswordService();
		GrupService grupService = ServiceLocator.instance().getGrupService();
		String defaultDomain = ips.getDefaultDispatcher();
		
		Account account = accountService.findAccount(accountName, domain);
		String userName = null;
		if (account instanceof UserAccount)
			userName = ((UserAccount)account).getUser();
		
		
		Set set = new HashSet();
		if (attributeId.toString().equals(ROLE_IDENTIFIER)) {
			Collection<RolGrant> grants;
			if (userName == null)
				grants = aplicacioService.findEffectiveRolGrantByAccount(account.getId());
			else
			{
				Usuari usuari = usuariService.findUsuariByCodiUsuari(userName);
				grants = aplicacioService.findEffectiveRolGrantByUser(usuari.getId());
			}
			for (RolGrant rg : grants) {
				set.add(new StringAttribute(rg.getRolName() + "@"
						+ rg.getDispatcher()));
				if (defaultDomain.equals(rg.getDispatcher()))
					set.add(new StringAttribute(rg.getRolName()));
				if (rg.getDomainValue() != null
						&& !rg.getDomainValue().isEmpty()) {
					set.add(new StringAttribute(rg.getRolName() + "/"
							+ rg.getDomainValue() + "@"
							+ rg.getDispatcher()));
					if (defaultDomain.equals(rg.getDispatcher()))
						set.add(new StringAttribute(rg.getRolName() + "/"
								+ rg.getDomainValue()));
				}
			}
		} else if (attributeId.toString().equals(GROUP_IDENTIFIER) && userName != null) {
			for (UsuariGrup grup : grupService
					.findUsuariGrupsByCodiUsuari(userName)) {
				set.add(new StringAttribute(grup.getCodiGrup()));
			}
		} else if (attributeId.toString().equals(PRIMARY_GROUP_IDENTIFIER) && userName != null) {
			Usuari usuari = usuariService.findUsuariByCodiUsuari(userName);
			set.add (new StringAttribute(usuari.getCodiGrupPrimari()));
		} else if (attributeId.toString().equals(SUBJECT_IDENTIFIER) ||
				attributeId.toString().equals(ACCOUNT_IDENTIFIER)) {
			set.add (new StringAttribute(accountName));
		} else if (attributeId.toString().equals(SYSTEM_IDENTIFIER) ||
				attributeId.toString().equals(XACMLConstants.ATTRIBUTEID_SUBJECT_ID_QUALIFIER)) {
			set.add (new StringAttribute(domain));
		} else if (attributeId.toString().equals(USER_IDENTIFIER) && userName != null) {
			set.add (new StringAttribute(userName));
		} else if (attributeId.toString().startsWith(CUSTOM_ATTRIBUTE_PREFIX) && userName != null)
		{
			String metaData = attributeId.toString().substring(CUSTOM_ATTRIBUTE_PREFIX.length()+1);
			DadaUsuari data = usuariService.findDadaByCodiTipusDada(userName, metaData);
			if (data != null)
			{
				if (data.getValorDadaDate() != null)
					set.add (new DateAttribute(data.getValorDadaDate().getTime()));
				else if (data.getBlobDataValue() != null)
					set.add (new Base64BinaryAttribute(data.getBlobDataValue()));
				else
					set.add (new StringAttribute(data.getValorDada()));
			}
		}
		return new BagAttribute(attributeType, set);
	}
    public EvaluationResult findAttribute(String contextPath,
            Node namespaceNode,
            URI attributeType,
            EvaluationCtx context,
            String xpathVersion) {
    	
    	Node node = context.getRequestRoot();
    	if (! (node instanceof SoffidDummyElement))
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    	
    	SoffidDummyElement sdn = (SoffidDummyElement) node;
    	
    	JXPathContext xpCtx = JXPathContextFactory.newInstance().
    			newContext(null, sdn.getUnderlyingObject());
    	
    	StringBuffer newPath = new StringBuffer();
    	boolean bs = false;
    	boolean quote = false;
    	boolean apos = false;
    	boolean slash = false;
    	for ( int i = 0; i < contextPath.length(); i++)
    	{
    		char ch = contextPath.charAt(i);
    		if (bs) {
    			bs = false;
    		} else if (ch == '\\') {
    			bs = true;
    		} else if (slash) {
    			ch = Character.toLowerCase(ch);
    			slash = false;
    		} else if (quote) {
    			quote = ch != '\"';
    		} else if (apos) {
    			apos = ch != '\'';
    		} else if (ch == '\"') {
    			quote = true;
    		} else if (ch == '\'') {
    			apos = true;
    		} else if (ch == '/') {
    			slash = true;
    		}
			newPath.append (ch);
    	}
    	

    	try {
			Set<AttributeValue> set = new HashSet<AttributeValue>();
	    	for (Iterator it = xpCtx.iteratePointers(newPath.toString()); it.hasNext();)
	    	{
	    		Pointer p = (Pointer) it.next();
	    		Object obj = p.getValue();
	    		if (obj != null)
	    		{
	    			if (obj instanceof Date)
	    				set.add (new DateAttribute((Date) obj));
	    			else if (obj instanceof byte[])
	    				set.add (new Base64BinaryAttribute((byte[]) obj));
	    			else if (obj instanceof Calendar)
	    				set.add (new DateAttribute(((Calendar) obj).getTime()));
	    			else 
	    				set.add (new StringAttribute(obj.toString()));
	    		}
	    	}
	    	return new EvaluationResult(new BagAttribute(attributeType, set));
    	} catch (Exception e) {
    		return new EvaluationResult (
    				new Status(
    						Collections.singletonList(Status.STATUS_PROCESSING_ERROR),
    						e.toString()));
    	}
	}
}
