//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_ENVMAT" )
@Depends ({com.soffid.iam.addons.xacml.common.EnvironmentMatch.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class})
public abstract class EnvironmentMatchEntity {

	@Column (name="ENV_MATCID")
	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId;

	@Column (name="ENV_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="ENV_ATTSEL", length=250)
	@Nullable
	public java.lang.String attributeSelector;

	@Column (name="ENV_TARGET")
	public com.soffid.iam.addons.xacml.model.TargetEntity target;

	@Column (name="ENV_DATVAL", length=250)
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Column (name="ENV_DATDES", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeEnvironmentDesignator;

	@Column (name="ENV_ATTVAL", length=250)
	public java.lang.String attributeValue;

	@Column (name="ENV_ATTDES", length=250)
	@Nullable
	public java.lang.String environmentAttributeDesignator;

	@Column (name="ENV_TEN_ID")
	TenantEntity tenant;


	@DaoFinder
	public com.soffid.iam.addons.xacml.model.EnvironmentMatchEntity findEnvironmentMatchById(
		com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId) {
	 return null;
	}
	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.EnvironmentMatchEntity> findByTarget(
		com.soffid.iam.addons.xacml.model.TargetEntity target) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.EnvironmentMatchEntity create(
		com.soffid.iam.addons.xacml.common.EnvironmentMatch environment, 
		com.soffid.iam.addons.xacml.model.TargetEntity target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.EnvironmentMatch environment)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.EnvironmentMatch environmentMatch)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
