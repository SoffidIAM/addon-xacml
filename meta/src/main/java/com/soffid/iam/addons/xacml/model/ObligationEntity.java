//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.addons.xacml.common.Obligation;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_OBLIGA" )
@Depends({Obligation.class})
public abstract class ObligationEntity {
	@Column (name="OBL_TEN_ID")
	TenantEntity tenant;

	@Nullable
	@Column (name="OBL_POLSET", reverseAttribute = "obligation")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@Nullable
	@Column (name="OBL_POLICY", reverseAttribute = "obligation")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

	@Column (name="OBL_OBLIID", length=100)
	public java.lang.String obligationId;

	@Column (name="OBL_FUFION")
	public com.soffid.iam.addons.xacml.common.EffectTypeEnumeration fulfillOn;

	@Column (name="OBL_NAME", length = 512)
	@Nullable
	public String attributeName;

	@Column (name="OBL_VALUE", length = 512)
	@Nullable
	public String attributeValue;


	@Column (name="OBL_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@DaoOperation
	public ObligationEntity create(Obligation obligation) {
		return null;
	}
	@DaoOperation
	public void update(Obligation obligation) {
	}
	@DaoOperation
	public void remove(Obligation obligation) {
	}
}
