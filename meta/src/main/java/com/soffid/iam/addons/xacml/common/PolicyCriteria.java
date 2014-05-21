//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@Criteria 
public abstract class PolicyCriteria {

	public java.lang.String policyId;

	public java.lang.String description;

	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm ruleCombiningAlgId;

	public java.lang.String ruleId;

	public java.lang.String targetId;

	public java.lang.String version;

}
