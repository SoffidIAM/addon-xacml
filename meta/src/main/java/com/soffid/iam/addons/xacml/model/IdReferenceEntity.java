//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_IDREFE" ,
		discriminatorValue="a" ,
		discriminatorColumn="IDR_CLASS" )
@Depends ({com.soffid.iam.addons.xacml.common.IdReference.class})
public abstract class IdReferenceEntity {

	@Column (name="IDR_TYPVAL", length=50)
	public java.lang.String idReferenceTypeValue;

	@Column (name="IDR_VERSIO", length=25)
	@Nullable
	public java.lang.String version;

	@Column (name="IDR_EARLIE", length=25)
	@Nullable
	public java.lang.String earliestVersion;

	@Column (name="IDR_LATEST", length=25)
	@Nullable
	public java.lang.String latestVersion;

	@Column (name="IDR_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="IDR_ORDER")
	@Nullable
	public java.lang.Integer order;

	@Column (name="IDR_TEN_ID")
	TenantEntity tenant;
}
