package com.soffid.iam.addons.xacml.common;

import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.Nullable;
import com.soffid.mda.annotation.ValueObject;

@ValueObject
public class Obligation {
	@Nullable
	Long id;
	
	public java.lang.String obligationId;

	public com.soffid.iam.addons.xacml.common.EffectTypeEnumeration fulfillOn;

	@Nullable
	public String attributeName;

	@Nullable
	public String attributeValue;

}
