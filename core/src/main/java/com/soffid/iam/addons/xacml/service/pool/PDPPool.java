package com.soffid.iam.addons.xacml.service.pool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.xacml.core.JBossPDP;

import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.PDPPolicySetReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.utils.ConfigurationCache;

import es.caib.seycon.ng.exception.InternalErrorException;

public class PDPPool extends AbstractPool<JBossPDP> {
	PDPConfiguration config;
	PolicySetService policySetService;
	
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

	Log log = LogFactory.getLog(getClass());
	public PDPPool(PDPConfiguration config, PolicySetService policySetService) {
		super();
		setMinSize(1);
		setMaxSize(5);
		this.config = config;
		this.policySetService = policySetService;
		String s = ConfigurationCache.getProperty ("soffid.xacml.pool.minSize");
		try {
			if (s != null)
				setMinSize(Integer.parseInt(s));
			else
				log.info("Missing setting for XACML min pool size (soffid.xacml.pool.minSize ");
			} catch (Exception e) {
			log.warn("Error setting xacml pool size to "+s,
					e);
		}

		s = ConfigurationCache.getProperty ("soffid.xacml.pool.maxSize");
		try {
			if (s != null)
				setMaxSize(Integer.parseInt(s));
			else
				log.info("Missing setting for XACML min pool size (soffid.xacml.pool.maxSize ");
		} catch (Exception e) {
			log.warn("Error setting xacml pool size to "+s,
					e);
		}
	}

	@Override
	protected JBossPDP createConnection() throws Exception {
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
				f.deleteOnExit();
				sb.append("<ns:PolicySet>")
					.append("<ns:Location>")
					.append(f.toURI().toString())
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
		JBossPDP jbossPDP = new JBossPDP(new ByteArrayInputStream(result.getBytes("UTF-8")));
		return jbossPDP;
	}

	@Override
	protected void closeConnection(JBossPDP connection) throws Exception {
	};
}
