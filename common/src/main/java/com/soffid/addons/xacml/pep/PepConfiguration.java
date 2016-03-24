package com.soffid.addons.xacml.pep;

public class PepConfiguration {
	boolean testing;

	PolicyStatus webPolicy = new PolicyStatus();
	
	PolicyStatus rolePolicy = new PolicyStatus();

	PolicyStatus authPolicy = new PolicyStatus();

	public PepConfiguration() {
		
	}

	public PepConfiguration(PepConfiguration pc) {
		testing = pc.testing;
		webPolicy = new PolicyStatus(pc.webPolicy);
		rolePolicy = new PolicyStatus(pc.rolePolicy);
		authPolicy = new PolicyStatus(pc.authPolicy);
		
	}
	public boolean isTesting() {
		return testing;
	}
	public void setTesting(boolean testing) {
		this.testing = testing;
	}
	

	public PolicyStatus getWebPolicy() {
		return webPolicy;
	}

	public void setWebPolicy(PolicyStatus webPolicy) {
		this.webPolicy = webPolicy;
	}

	public PolicyStatus getRolePolicy() {
		return rolePolicy;
	}

	public void setRolePolicy(PolicyStatus rolePolicy) {
		this.rolePolicy = rolePolicy;
	}

	public PolicyStatus getAuthPolicy() {
		return authPolicy;
	}

	public void setAuthPolicy(PolicyStatus authPolicy) {
		this.authPolicy = authPolicy;
	}
	
	
}
