//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="" ,
		discriminatorValue="c" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicySetCombinerParameters extends com.soffid.iam.addons.xacml.model.CombinerParametersEntity {

	@Column (name="PSC_POSEID")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySetIdRef;

	@Column (name="PSC_ID")
	@Nullable
	public java.lang.Long iden;

}
