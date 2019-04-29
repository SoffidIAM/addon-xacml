//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class PolicySet {

	public java.lang.String policySetId;

	@Nullable
	public java.lang.String description;

	@Nullable
	public java.lang.String version;

	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm policyCombiningAlgId;

	public java.lang.Integer order;

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Target> target;

	@Nullable
	public java.lang.Long parentPolicySet;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.PolicySetIdReference> policySetIdReference;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.PolicyIdReference> policyIdReference;

}
