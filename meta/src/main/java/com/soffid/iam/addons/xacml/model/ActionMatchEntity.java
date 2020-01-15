//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_ACTMAT" )
@Depends ({com.soffid.iam.addons.xacml.common.ActionMatch.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class})
public abstract class ActionMatchEntity {

	@Column (name="ACT_MATCID")
	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId;

	@Column (name="ACT_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="ACT_TEN_ID")
	TenantEntity tenant;

	@Column (name="ACT_ATTSEL", length=250)
	@Nullable
	public java.lang.String attributeSelector;

	@Column (name="ACT_TARGET")
	public com.soffid.iam.addons.xacml.model.TargetEntity target;

	@Column (name="ACT_ATTVAL", length=250)
	public java.lang.String attributeValue;

	@Column (name="ACT_ATTDES", length=250)
	@Nullable
	public java.lang.String actionAttributeDesignator;

	@Column (name="ACT_DATVAL", length=250)
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Column (name="ACT_DATDES", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeActionDesignator;

//	@DaoFinder
//	public com.soffid.iam.addons.xacml.common.ActionMatch findActionMatchById(
//		com.soffid.iam.addons.xacml.common.MatchIdEnumeration MatchId) {
//	 return null;
//	}
	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.ActionMatchEntity> findByTarget(
		com.soffid.iam.addons.xacml.model.TargetEntity target) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ActionMatchEntity create(
		com.soffid.iam.addons.xacml.common.ActionMatch action, 
		com.soffid.iam.addons.xacml.model.TargetEntity target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.ActionMatch action)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.ActionMatch actionMatch)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
