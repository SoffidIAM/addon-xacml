//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_RULEEN" )
@Depends ({com.soffid.iam.addons.xacml.common.Rule.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class,
	com.soffid.iam.addons.xacml.model.ConditionEntity.class})
public abstract class RuleEntity {
	@Column (name="RUL_TEN_ID")
	TenantEntity tenant;


	@Column (name="RUL_POLICY")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

	@ForeignKey (foreignColumn="TAR_RULE")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.TargetEntity> target;

	@ForeignKey (foreignColumn="CON_RULE")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ConditionEntity> condicion;

	@Column (name="RUL_DESCRI", length=250)
	@Nullable
	public java.lang.String description;

	@Column (name="RUL_RULEID", length=50)
	@Nullable
	public java.lang.String ruleId;

	@Column (name="RUL_ORDER")
	@Nullable
	public java.lang.Integer order;

	@Column (name="RUL_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RUL_EFTYPE")
	public com.soffid.iam.addons.xacml.common.EffectTypeEnumeration effectType;

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.RuleEntity> findRuleByCriteria(
		com.soffid.iam.addons.xacml.common.RuleCriteria criteria) {
	 return null;
	}
	@DaoFinder("select re\nfrom com.soffid.iam.addons.xacml.model.RuleEntity as re\njoin re.policy as policy\nwhere policy.policyId = :policyId")
	public java.util.List<com.soffid.iam.addons.xacml.model.RuleEntity> findRuleChildrenPolicy(
		java.lang.String policyId) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.RuleEntity create(
		com.soffid.iam.addons.xacml.common.Rule rule, 
		com.soffid.iam.addons.xacml.model.PolicyEntity policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
