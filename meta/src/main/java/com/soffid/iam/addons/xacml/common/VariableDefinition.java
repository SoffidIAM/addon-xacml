//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class VariableDefinition {

	@Nullable
	public java.lang.String variableId;

	@Nullable
	public java.lang.Long id;

	public com.soffid.iam.addons.xacml.common.Expression expression;

}
