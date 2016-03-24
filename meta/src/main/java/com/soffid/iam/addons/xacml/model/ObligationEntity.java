//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_OBLIGA" )
@Depends ({com.soffid.iam.addons.xacml.model.ObligationsEntity.class,
	com.soffid.iam.addons.xacml.model.AttributeAssignment.class})
public abstract class ObligationEntity {

	@Column (name="OBL_OBLIID", length=50)
	public java.lang.String obligationId;

	@Column (name="OBL_FUFION")
	public com.soffid.iam.addons.xacml.common.EffectTypeEnumeration fulfillOn;

	@Column (name="OBL_OBLIGS")
	public com.soffid.iam.addons.xacml.model.ObligationsEntity obligations;

	@ForeignKey (foreignColumn="AAS_OBLIGA")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.AttributeAssignment> attributeAssignment;

	@Column (name="OBL_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

}
