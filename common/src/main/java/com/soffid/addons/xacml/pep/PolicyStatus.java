package com.soffid.addons.xacml.pep;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.PDPPolicySetReference;

public class PolicyStatus {
	boolean enabled;
	String policyId;
	String policyVersion;
	
	public PolicyStatus() {
	
	}

	public PolicyStatus(PolicyStatus authPolicy) {
		enabled = authPolicy.enabled;
		policyId = authPolicy.policyId;
		policyVersion = authPolicy.policyVersion;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getPolicyVersion() {
		return policyVersion;
	}
	public void setPolicyVersion(String policyVersion) {
		this.policyVersion = policyVersion;
	}
	PDPConfiguration config;
	public PDPConfiguration getPDPConfig ()
	{
		if (config == null)
		{
			config = new PDPConfiguration();
			PDPPolicySetReference ref = new PDPPolicySetReference(policyId, policyVersion);
			config.setPolicies(Collections.singletonList(ref));
		}
		return config;
	}
}	
