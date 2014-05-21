package com.soffid.iam.addons.xacml.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;

import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.PDPPolicySetReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;

import es.caib.seycon.ng.exception.InternalErrorException;

public class MultiPDPHandler {

	public void reset ()
	{
		map.clear();
	}
	
	Map<PDPConfiguration, WeakReference<JBossPDP>> map =
			new HashMap<PDPConfiguration, WeakReference<JBossPDP>>();
	private PolicySetService policySetService;

	public MultiPDPHandler(PolicySetService svc) {
		this.policySetService = svc;
	}
	
	protected JBossPDP getJBossPDP(PDPConfiguration config) throws InternalErrorException, IOException
	{
		WeakReference<JBossPDP> ref = map.get(config);
		JBossPDP jbossPDP = ref == null ? null : ref.get();
		if (jbossPDP == null)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='utf-8'?>")
				.append("<ns:jbosspdp xmlns:ns='urn:jboss:xacml:2.0'>")
				.append("<ns:Policies>");
			LinkedList<File> policySetFiles = new LinkedList<File>();
			for (PDPPolicySetReference ppr: config.getPolicies())
			{
				PolicySet foundPolicySet = foundPolicySet = findPolicySet(ppr);
				if (foundPolicySet != null)
				{
					File f = File.createTempFile("policySet", ".xml");
					FileOutputStream out = new FileOutputStream(f);
					policySetService.exportXACMLPolcySet(foundPolicySet.getPolicySetId(), foundPolicySet.getVersion(), out);
					out.close ();
					policySetFiles.add(f);
					sb.append("<ns:PolicySet>")
						.append("<ns:Location>")
						.append(f.toURL())
						.append("</ns:Location>")
						.append("</ns:PolicySet>");
				}
			}
			sb.append("</ns:Policies>")
				.append("<ns:Locators>")
				.append("<ns:Locator Name='org.jboss.security.xacml.locators.JBossPolicySetLocator'/>")
				.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidAttributeFinderModule'/>")
				.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidPolicyLocator'/>")
				.append("</ns:Locators>")
				.append("</ns:jbosspdp>");
			String result = sb.toString();
			jbossPDP = new JBossPDP(new ByteArrayInputStream(result.getBytes("UTF-8")));
			map.put(config, new WeakReference<JBossPDP>(jbossPDP));
		}
		return jbossPDP;
	}

	private PolicySet findPolicySet(PDPPolicySetReference ppr) throws InternalErrorException {
		PolicySet foundPolicySet = null;
		PolicySetCriteria criteria = new PolicySetCriteria();
		criteria.setPolicySetId(ppr.getPolicySet());
		for (PolicySet ps : policySetService.findPolicySetByCriteria(criteria))
		{
			if (ppr.getPolicySetVersion() == null)
			{
				if (foundPolicySet == null)
					foundPolicySet = ps;
				else
				{
					try {
						if (Integer.decode(ps.getVersion()) > Integer.decode(foundPolicySet.getVersion()))
							foundPolicySet = ps;
					} catch ( NumberFormatException e) {
						if (ps.getVersion().compareTo(foundPolicySet.getVersion()) > 0)
						{
							foundPolicySet = ps;
						}
					}
				}
			}
			else if (ps.getVersion().equals( ppr.getPolicySetVersion() ))
			{
				foundPolicySet = ps;
				break;
			}
		}
		return foundPolicySet;
	}
	public ResponseContext handle(PDPConfiguration config,
			RequestContext requestContext) throws InternalErrorException, IOException {
		JBossPDP jbosspdp = getJBossPDP(config);
		return jbosspdp.evaluate(requestContext);
	}

}
