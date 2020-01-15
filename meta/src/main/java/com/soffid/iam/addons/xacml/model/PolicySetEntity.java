//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_POLSET" )
@Depends ({com.soffid.iam.addons.xacml.common.PolicySet.class,
	com.soffid.iam.addons.xacml.model.PolicySetEntity.class,
	com.soffid.iam.addons.xacml.model.ObligationsEntity.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class,
	com.soffid.iam.addons.xacml.model.PolicySetDefaults.class,
	com.soffid.iam.addons.xacml.model.CombinerParametersEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyCombinerParametersEntity.class,
	com.soffid.iam.addons.xacml.model.PolicySetCombinerParameters.class,
	com.soffid.iam.addons.xacml.model.PolicySetIdReferenceEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyIdReferenceEntity.class})
public abstract class PolicySetEntity {
	@Column (name="PSE_TEN_ID")
	TenantEntity tenant;


	@Column (name="PSE_POLIID", length=50)
	public java.lang.String policySetId;

	@Column (name="PSE_DESCRI", length=250)
	@Nullable
	public java.lang.String description;

	@ForeignKey (foreignColumn="PSE_PARENT")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicySetEntity> childrenPolicySet;

	@ForeignKey (foreignColumn="OBS_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ObligationsEntity> obligations;

	@ForeignKey (foreignColumn="TAR_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.TargetEntity> target;

	@ForeignKey (foreignColumn="POL_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicyEntity> policy;

	@ForeignKey (foreignColumn="PSD_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicySetDefaults> policySetDefaults;

	@Column (name="PSE_VERSIO",
		defaultValue="\"1.0\"", length=25)
	@Nullable
	public java.lang.String version;

	@Column (name="PSE_ALGORI", length=25)
	public com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm policyCombiningAlgId;

	@Column (name="PSE_ORDER")
	@Nullable
	public java.lang.Integer order;

	@ForeignKey (foreignColumn="COP_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.CombinerParametersEntity> combinerParameters;

	@ForeignKey (foreignColumn="PCP_POIDRE")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicyCombinerParametersEntity> policyCombinerParameters;

	@ForeignKey (foreignColumn="PSC_POSEID")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicySetCombinerParameters> policySetCombinerParameters;

	@Column (name="PSE_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="PSE_PARENT")
	@Nullable
	public com.soffid.iam.addons.xacml.model.PolicySetEntity parentPolicySet;

	@ForeignKey (foreignColumn="SRE_POLSET")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicySetIdReferenceEntity> policySetIdReference;

	@ForeignKey (foreignColumn="PRE_POLICY")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.PolicyIdReferenceEntity> policyIdReference;

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicySetEntity> findPolicySetByCriteria(
		com.soffid.iam.addons.xacml.common.PolicySetCriteria criteria) {
	 return null;
	}
	@DaoFinder("Select pse from com.soffid.iam.addons.xacml.model.PolicySetEntity \nas pse\nwhere pse.id = :id")
	public com.soffid.iam.addons.xacml.model.PolicySetEntity findByPolicySetId(
		java.lang.Long id) {
	 return null;
	}
	@DaoFinder("Select pse from com.soffid.iam.addons.xacml.model.PolicySetEntity as pse\njoin pse.parentPolicySet as policySet\nwhere policySet.policySetId = :policySet")
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicySetEntity> findPolicySetByPolicySet(
		java.lang.String policySet) {
	 return null;
	}
	@DaoFinder("Select pse from com.soffid.iam.addons.xacml.model.PolicySetEntity as pse\nwhere pse.parentPolicySet is null")
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicySetEntity> findMasterPolicySet() {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.PolicySetEntity create(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoFinder("Select pse from com.soffid.iam.addons.xacml.model.PolicySetEntity as pse\njoin pse.parentPolicySet as policySet\nwhere policySet.id = :id")
	public java.util.List<com.soffid.iam.addons.xacml.model.PolicySetEntity> findPolicySetById(
		java.lang.Long id) {
	 return null;
	}
}
