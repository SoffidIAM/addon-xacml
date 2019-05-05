//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@Criteria 
public class PolicySetCriteria {

	public java.lang.String policySetId;

	public java.lang.String description;

	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm policyCombiningAlgId;

	public java.lang.String targetId;

	public java.lang.String ruleId;

	public java.lang.String version;

}
