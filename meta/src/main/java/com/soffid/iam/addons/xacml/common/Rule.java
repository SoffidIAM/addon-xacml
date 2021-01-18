//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class Rule {

	@Nullable
	public java.lang.String ruleId;

	@Nullable
	public java.lang.String description;

	@Nullable
	public java.lang.Integer order;

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Target> target;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Condition> condition;

	public java.lang.Long policyId;

	@Nullable
	public com.soffid.iam.addons.xacml.common.EffectTypeEnumeration effectType;

}
