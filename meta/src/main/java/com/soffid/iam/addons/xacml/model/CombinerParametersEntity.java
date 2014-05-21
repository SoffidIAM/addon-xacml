//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_COMPAS" ,
		discriminatorValue="a" ,
		discriminatorColumn="discrimina" )
@Depends ({com.soffid.iam.addons.xacml.model.CombinerParameterEntity.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class})
public abstract class CombinerParametersEntity {

	@ForeignKey (foreignColumn="CPE_COMPAR")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.CombinerParameterEntity> combinerParameter;

	@Column (name="COP_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="COP_POLSET")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@Column (name="COP_POLICY")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

}
