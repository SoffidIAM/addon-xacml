//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_POLICY" )
@Depends ({com.soffid.iam.addons.xacml.common.Policy.class,
	com.soffid.iam.addons.xacml.model.ObligationsEntity.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class,
	com.soffid.iam.addons.xacml.model.RuleEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyDefaults.class,
	com.soffid.iam.addons.xacml.model.CombinerParametersEntity.class,
	com.soffid.iam.addons.xacml.model.RuleCombinerParametersEntity.class,
	com.soffid.iam.addons.xacml.model.VariableDefinitionEntity.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class})
public abstract class PolicyEntity {

	@ForeignKey (foreignColumn="OBS_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ObligationsEntity> obligations;

	@ForeignKey (foreignColumn="TAR_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.TargetEntity> target;

	@ForeignKey (foreignColumn="RUL_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.RuleEntity> rule;

	@Column (name="POL_DESCRI", length=250)
	@Nullable
	public java.lang.String description;

	@Column (name="POL_POLIID", length=50)
	public java.lang.String policyId;

	@Column (name="POL_VERSIO",
		defaultValue="\"1.0\"", length=25)
	@Nullable
	public java.lang.String version;

	@ForeignKey (foreignColumn="POD_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicyDefaults> policyDefaults;

	@Column (name="POL_ALGORI", length=25)
	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm ruleCombiningAlgId;

	@Column (name="POL_ORDER")
	@Nullable
	public java.lang.Integer order;

	@ForeignKey (foreignColumn="COP_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.CombinerParametersEntity> combinerParameters;

	@ForeignKey (foreignColumn="RCP_POLPAR")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.RuleCombinerParametersEntity> ruleCombinerParameters;

	@ForeignKey (foreignColumn="VAD_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.VariableDefinitionEntity> variableDefinition;

	@Column (name="POL_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="POL_POLSET")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity policySet;

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicyEntity> findPolicyByCriteria(
		com.soffid.iam.addons.xacml.common.PolicyCriteria criteria) {
	 return null;
	}
	@DaoFinder
	public com.soffid.iam.addons.xacml.model.PolicyEntity findPolicyById(
		java.lang.String policyId) {
	 return null;
	}
	@DaoFinder("select pe\nfrom com.soffid.iam.addons.xacml.model.PolicyEntity as pe\njoin pe.policySet as policySet\nwhere policySet.policySetId = :policySet")
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicyEntity> findPolicyByPolicySetId(
		java.lang.String policySet) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.PolicyEntity create(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoFinder("select pe\nfrom com.soffid.iam.addons.xacml.model.PolicyEntity as pe\njoin pe.policySet as policySet\nwhere policySet.id = :id")
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicyEntity> findPolicyByPolicySetId(
		java.lang.Long id) {
	 return null;
	}
}
