//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class Condition {

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.lang.String conditionId;

	public com.soffid.iam.addons.xacml.common.Expression expression;

}
