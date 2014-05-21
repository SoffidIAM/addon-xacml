//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="" ,
		discriminatorValue="b" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicyCombinerParametersEntity extends com.soffid.iam.addons.xacml.model.CombinerParametersEntity {

	@Column (name="PCP_POIDRE")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policyIdRef;

	@Column (name="PCP_ID")
	@Nullable
	public java.lang.Long iden;

}
