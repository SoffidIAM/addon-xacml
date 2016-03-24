//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_POLISD" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicySetDefaults {

	@Column (name="PSD_POLSET")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@Column (name="PSD_DXPATH",
		defaultValue="\"1.0\"", length=250)
	public java.lang.String defaultXPath;

	@Column (name="PSD_XPAVER",
		defaultValue="\"http://www.w3.org/TR/1999/Rec-xpath-19991116 \"", length=250)
	public java.lang.String xpathVersion;

	@Column (name="PSD_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

}
