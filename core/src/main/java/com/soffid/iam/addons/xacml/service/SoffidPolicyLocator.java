package com.soffid.iam.addons.xacml.service;

import java.util.ServiceLoader;
import java.util.Set;

import org.jboss.security.xacml.bridge.PolicySetFinderModule;
import org.jboss.security.xacml.bridge.WrapperPolicyFinderModule;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.interfaces.XACMLPolicy;
import org.jboss.security.xacml.locators.AbstractJBossPolicyLocator;
import org.jboss.security.xacml.sunxacml.Policy;

import es.caib.seycon.ng.ServiceLocator;

public class SoffidPolicyLocator extends AbstractJBossPolicyLocator {

	@Override
	public void setPolicies(Set<XACMLPolicy> policies) {
		this.policies = policies;

		SoffidPolicyFinderModule spfm = new SoffidPolicyFinderModule();
		pfml.add(spfm);
		this.map.put(XACMLConstants.POLICY_FINDER_MODULE, pfml);

	}

}
