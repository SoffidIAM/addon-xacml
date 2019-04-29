//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class IdReference {

	public java.lang.String idReferenceTypeValue;

	@Nullable
	public java.lang.String version;

	@Nullable
	public java.lang.String earliestVersion;

	@Nullable
	public java.lang.String latestVersion;

	@Nullable
	public java.lang.Long id;

	public java.lang.Integer order;

}
