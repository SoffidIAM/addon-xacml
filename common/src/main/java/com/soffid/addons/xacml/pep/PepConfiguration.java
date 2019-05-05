package com.soffid.addons.xacml.pep;

public class PepConfiguration {
	boolean testing;

	PolicyStatus webPolicy = new PolicyStatus();
	
	PolicyStatus rolePolicy = new PolicyStatus();

	PolicyStatus authPolicy = new PolicyStatus();

	PolicyStatus externalPolicy = new PolicyStatus();

	PolicyStatus vaultPolicy = new PolicyStatus();


	public PepConfiguration() {
		
	}

	public PepConfiguration(PepConfiguration pc) {
		testing = pc.testing;
		webPolicy = new PolicyStatus(pc.webPolicy);
		rolePolicy = new PolicyStatus(pc.rolePolicy);
		authPolicy = new PolicyStatus(pc.authPolicy);
		externalPolicy = new PolicyStatus(pc.externalPolicy);
		vaultPolicy = new PolicyStatus(pc.vaultPolicy);
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

	public PolicyStatus getExternalPolicy() {
		return externalPolicy;
	}

	public void setExternalPolicy(PolicyStatus externalPolicy) {
		this.externalPolicy = externalPolicy;
	}

	public PolicyStatus getVaultPolicy() {
		return vaultPolicy;
	}

	public void setVaultPolicy(PolicyStatus vaultPolicy) {
		this.vaultPolicy = vaultPolicy;
	}
	
	
}
