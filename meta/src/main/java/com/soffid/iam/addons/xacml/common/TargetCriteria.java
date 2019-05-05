//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@Criteria 
public class TargetCriteria {

	public java.lang.Long id;

	public java.lang.String targetComment;

	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration subjectMatchId;

	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration resourceMatchId;

	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration actionMatchId;

	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration environmentMatchId;

	public java.lang.String ruleId;

	public java.lang.String policyId;

	public java.lang.String policySetId;

}
