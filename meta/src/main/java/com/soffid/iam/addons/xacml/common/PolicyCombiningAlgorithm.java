//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@Enumeration 
public class PolicyCombiningAlgorithm {

	public java.lang.String deny_overrides="DENY_OVERRIDES";

	public java.lang.String permit_overrides="PERMIT_OVERRIDES";

	public java.lang.String first_applicable="FIRST_APPLICABLE";

	public java.lang.String only_one_applicable="ONLY_ONE_APPLICABLE";

	public java.lang.String ordered_deny_overrides="ORDERED_DENY_OVERRIDES";

	public java.lang.String ordered_permit_overrides="ORDERED_PERMIT_OVERRIDES";

}
