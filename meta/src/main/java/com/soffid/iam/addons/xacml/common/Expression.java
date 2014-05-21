//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class Expression {

	@Nullable
	public java.lang.Integer order;

	@Nullable
	public java.lang.Long id;

	public com.soffid.iam.addons.xacml.common.FunctionEnumeration name;

	public java.lang.String expressionType;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.Expression> subexpression;

	@Nullable
	public java.lang.String attributeSelector;

	@Description("or conditionId")
	@Nullable
	public java.lang.String variableId;

	@Nullable
	public java.lang.String attributeValue;

	@Nullable
	public java.lang.String attributeDesignator;

	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeDesignator;

}
