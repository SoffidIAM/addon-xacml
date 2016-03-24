//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class ResourceMatch {

	public com.soffid.iam.addons.xacml.common.MatchIdEnumeration matchId;

	@Nullable
	public java.lang.String attributeSelector;

	@Nullable
	public java.lang.Long id;

	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeResourceDesignator;

	public java.lang.String attributeValue;

	@Nullable
	public java.lang.String resourceAttributeDesignator;

}
