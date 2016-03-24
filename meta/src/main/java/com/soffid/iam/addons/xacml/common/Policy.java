//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class Policy {

	@Nullable
	public java.lang.String description;

	public java.lang.String policyId;

	@Nullable
	@Attribute(defaultValue = "\"1.0\"")
	public java.lang.String version;

	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm ruleCombiningAlgId;

	public java.lang.Integer order;

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Target> target;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.VariableDefinition> variableDefinition;

	public java.lang.Long policySetId;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Rule> rule;

}
