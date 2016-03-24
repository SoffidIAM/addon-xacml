//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="" ,
		discriminatorValue="d" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicyEntity.class})
public abstract class RuleCombinerParametersEntity extends com.soffid.iam.addons.xacml.model.CombinerParametersEntity {

	@Column (name="RCP_RUIDRE")
	public java.lang.String ruleIdRef;

	@Column (name="RCP_ID")
	@Nullable
	public java.lang.Long iden;

	@Column (name="RCP_POLPAR")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policyParent;

}
