//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_SUBMAT" )
@Depends ({com.soffid.iam.addons.xacml.common.SubjectMatch.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class})
public abstract class SubjectMatchEntity {
	@Column (name="SUB_TEN_ID")
	TenantEntity tenant;


	@Column (name="SUB_MATCHID")
	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId;

	@Column (name="SUB_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="SUB_ATTSEL", length=250)
	@Nullable
	public java.lang.String attributeSelector;

	@Column (name="SUB_TARGET")
	public com.soffid.iam.addons.xacml.model.TargetEntity target;

	@Column (name="SUB_ATTVAL", length=250)
	public java.lang.String attributeValue;

	@Column (name="SUB_DATVAL", length=250)
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Column (name="SUB_ATTDES", length=250)
	@Nullable
	public java.lang.String subjectAttributeDesignator;

	@Column (name="SUB_DATDES", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeSubjectDesignator;

//	@DaoFinder
//	public com.soffid.iam.addons.xacml.common.SubjectMatch findSubjectMatchById(
//		com.soffid.iam.addons.xacml.common.MatchIdEnumeration MatchId) {
//	 return null;
//	}
	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.SubjectMatchEntity> findByTarget(
		com.soffid.iam.addons.xacml.model.TargetEntity target) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.SubjectMatchEntity create(
		com.soffid.iam.addons.xacml.common.SubjectMatch subject, 
		com.soffid.iam.addons.xacml.model.TargetEntity target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.SubjectMatch subject)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.SubjectMatch subjectMatch)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
