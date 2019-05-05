//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_RESMAT" )
@Depends ({com.soffid.iam.addons.xacml.common.ResourceMatch.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class})
public abstract class ResourceMatchEntity {

	@Column (name="RES_MATCID")
	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId;

	@Column (name="RES_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RES_ATTSEL", length=250)
	@Nullable
	public java.lang.String attributeSelector;

	@Column (name="RES_TARGET")
	public com.soffid.iam.addons.xacml.model.TargetEntity target;

	@Column (name="RES_ATTVAL", length=250)
	public java.lang.String attributeValue;

	@Column (name="RES_ATTDES", length=250)
	@Nullable
	public java.lang.String resourceAttributeDesignator;

	@Column (name="RES_DATVAL", length=250)
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Column (name="RES_DATDES", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeResourceDesignator;

//	@DaoFinder
//	public com.soffid.iam.addons.xacml.common.ResourceMatch findResourceMatchById(
//		com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId) {
//	 return null;
//	}

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.ResourceMatchEntity> findByTarget(
		com.soffid.iam.addons.xacml.model.TargetEntity target) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ResourceMatchEntity create(
		com.soffid.iam.addons.xacml.common.ResourceMatch resource, 
		com.soffid.iam.addons.xacml.model.TargetEntity target)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.ResourceMatch resource)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.ResourceMatch resourceMatch)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
