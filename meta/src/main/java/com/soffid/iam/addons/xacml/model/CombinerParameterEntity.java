//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_COMPAM" )
@Depends ({com.soffid.iam.addons.xacml.model.CombinerParametersEntity.class})
public abstract class CombinerParameterEntity {

	@Column (name="CPE_PARNAM", length=250)
	public java.lang.String parameterName;

	@Column (name="CPE_ATTVAL", length=250)
	public java.lang.String attributeValue;

	@Column (name="CPE_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="CPE_COMPAR")
	public com.soffid.iam.addons.xacml.model.CombinerParametersEntity combinerParameters;

}
