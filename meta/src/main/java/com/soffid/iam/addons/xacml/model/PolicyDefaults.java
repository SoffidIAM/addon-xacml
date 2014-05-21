//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_POLICD" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicyEntity.class})
public abstract class PolicyDefaults {

	@Column (name="POD_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="POD_XPAVER",
		defaultValue="\"http://www.w3.org/TR/1999/Rec-xpath-19991116 \"", length=250)
	@Nullable
	public java.lang.String xpathVersion;

	@Column (name="POD_POLICY")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

}
