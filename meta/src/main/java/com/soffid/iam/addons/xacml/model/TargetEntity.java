//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_TARGET" )
@Depends ({com.soffid.iam.addons.xacml.common.Target.class,
	com.soffid.iam.addons.xacml.model.ActionMatchEntity.class,
	com.soffid.iam.addons.xacml.model.SubjectMatchEntity.class,
	com.soffid.iam.addons.xacml.model.ResourceMatchEntity.class,
	com.soffid.iam.addons.xacml.model.EnvironmentMatchEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class,
	com.soffid.iam.addons.xacml.model.RuleEntity.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class TargetEntity {
	@Column (name="TAR_TEN_ID")
	TenantEntity tenant;


	@Column (name="TAR_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="TAR_COMMEN", length=100)
	@Nullable
	public java.lang.String targetComment;

	@Column (name="TAR_POLICY")
	@Nullable
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

	@Column (name="TAR_RULE")
	@Nullable
	public com.soffid.iam.addons.xacml.model.RuleEntity rule;

	@Column (name="TAR_POLSET")
	@Nullable
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@ForeignKey (foreignColumn="RES_TARGET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ResourceMatchEntity> resourceMatch;

	@ForeignKey (foreignColumn="SUB_TARGET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.SubjectMatchEntity> subjectMatch;

	@ForeignKey (foreignColumn="ACT_TARGET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ActionMatchEntity> actionMatch;

	@ForeignKey (foreignColumn="ENV_TARGET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.EnvironmentMatchEntity> environmentMatch;

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.TargetEntity> findTargetByCriteria(
		com.soffid.iam.addons.xacml.common.TargetCriteria criteria) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.TargetEntity create(
		com.soffid.iam.addons.xacml.common.Target target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.Target target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.Target target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
