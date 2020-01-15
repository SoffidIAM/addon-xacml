//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_CONDIT" )
@Depends ({com.soffid.iam.addons.xacml.common.Condition.class,
	com.soffid.iam.addons.xacml.model.ExpressionEntity.class,
	com.soffid.iam.addons.xacml.model.RuleEntity.class})
public abstract class ConditionEntity {

	@Column (name="CON_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="CON_EXPRES")
	public com.soffid.iam.addons.xacml.model.ExpressionEntity expression;

	@Column (name="CON_RULE")
	public com.soffid.iam.addons.xacml.model.RuleEntity rule;

	@Column (name="CON_CONDID", length=50)
	@Nullable
	public java.lang.String conditionId;

	@Column (name="CON_TEN_ID")
	TenantEntity tenant;


	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ConditionEntity create(
		com.soffid.iam.addons.xacml.common.Condition condition, 
		com.soffid.iam.addons.xacml.model.RuleEntity rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.Condition condition)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.Condition condition)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
