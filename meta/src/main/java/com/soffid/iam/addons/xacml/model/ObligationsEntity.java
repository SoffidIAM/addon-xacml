//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_OBLIGS" )
@Depends ({com.soffid.iam.addons.xacml.model.PolicySetEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class,
	com.soffid.iam.addons.xacml.model.ObligationEntity.class})
public abstract class ObligationsEntity {

	@Column (name="OBS_POLSET")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@Column (name="OBS_POLICY")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

	@ForeignKey (foreignColumn="OBL_OBLIGS")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ObligationEntity> obligation;

	@Column (name="OBS_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

}
