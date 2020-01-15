//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_ATTASS" )
@Depends ({com.soffid.iam.addons.xacml.model.ObligationEntity.class})
public abstract class AttributeAssignment {

	@Column (name="AAS_ATTASS")
	@Nullable
	public java.lang.Long attributeAssignmentId;

	@Column (name="AAS_OBLIGA")
	public com.soffid.iam.addons.xacml.model.ObligationEntity obligation;

	@Column (name="ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="AAS_TEN_ID")
	TenantEntity tenant;

}
