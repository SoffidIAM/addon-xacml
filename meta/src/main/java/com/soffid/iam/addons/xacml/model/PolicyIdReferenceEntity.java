//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="" ,
		discriminatorValue="c" )
@Depends ({com.soffid.iam.addons.xacml.common.PolicyIdReference.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicyIdReferenceEntity extends com.soffid.iam.addons.xacml.model.IdReferenceEntity {

	@Column (name="PRE_POLICY")
	@Description("Corresponding with policySet associated. Not policy!!")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policy;

}
