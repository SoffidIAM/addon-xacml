//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class PDPPolicySetReference {

	public java.lang.String policySet;

	@Nullable
	public java.lang.String policySetVersion;

}
