//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_VARDEF" )
@Depends ({com.soffid.iam.addons.xacml.common.VariableDefinition.class,
	com.soffid.iam.addons.xacml.model.ExpressionEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class})
public abstract class VariableDefinitionEntity {
	@Column (name="VAR_TEN_ID")
	TenantEntity tenant;


	@Column (name="VAD_VARIID", length=50)
	@Nullable
	public java.lang.String variableId;

	@Column (name="VAD_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="VAD_POLICY")
	public com.soffid.iam.addons.xacml.model.PolicyEntity policy;

	@Column (name="VAD_EXPRES")
	public com.soffid.iam.addons.xacml.model.ExpressionEntity expression;

	@DaoOperation
	public com.soffid.iam.addons.xacml.model.VariableDefinitionEntity create(
		com.soffid.iam.addons.xacml.common.VariableDefinition variable, 
		com.soffid.iam.addons.xacml.model.PolicyEntity policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.VariableDefinition variable)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.VariableDefinition variable)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
