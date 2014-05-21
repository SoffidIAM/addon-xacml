//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="" ,
		discriminatorValue="b" )
@Depends ({com.soffid.iam.addons.xacml.common.PolicySetIdReference.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicySetIdReferenceEntity extends com.soffid.iam.addons.xacml.model.IdReferenceEntity {

	@Column (name="SRE_POLSET")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

}
