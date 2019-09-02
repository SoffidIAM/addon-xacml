//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.SubjectMatch;

/**
 * DAO ConditionEntity implementation
 */
public class ConditionEntityDaoImpl extends ConditionEntityDaoBase
{
	@Override
	public void toCondition(ConditionEntity source, Condition target){
		// Incompatible types source.expression and target.expression
		super.toCondition(source, target);

		if(source.getExpression() != null){
			target.setExpression(getExpressionEntityDao().toExpression(source.getExpression()));
		}
	}
	
	/*@Override
	public void conditionToEntity(Condition source, ConditionEntity target, boolean copyIfNull){
		super.conditionToEntity(source, target, copyIfNull);
		// Incompatible types source.expression and target.expression
		// Missing attribute expression on entity
		// Missing attribute rule on entity
	}*/
	

	@Override
	public void remove(ConditionEntity entity) {
		ExpressionEntity ee = entity.getExpression();
		ee.setCondicion(null);
		getExpressionEntityDao().update(ee);
		super.remove(entity);
		getExpressionEntityDao().remove(ee);
	}
	

	@Override
	protected ConditionEntity handleCreate(Condition condition, RuleEntity rule) throws Exception {
		ConditionEntity ce = conditionToEntity(condition);
		ExpressionEntity ee = getExpressionEntityDao().create(condition.getExpression());
		ce.setRule(rule);
		ce.setExpression(ee);
		super.create(ce);
		if(condition.getExpression() != null)
		{
			ee.setCondicion(ce);
			getExpressionEntityDao().update(ee);
		}
		condition.setId(ce.getId());
		return ce;
	}

	
	@Override
	protected void handleUpdate(Condition condition) throws Exception {
		ConditionEntity ce = conditionToEntity(condition);
		Expression exp = condition.getExpression();
		if(ce.getExpression() != null && ce.getExpression().getId() != null && exp.getId() != null)
		{
			if (!ce.getExpression().getId().equals (exp.getId()))	
				getExpressionEntityDao().remove(ce.getExpression());
		}
		if (exp.getId() == null){
			ExpressionEntity ee = getExpressionEntityDao().create(exp, ce);
			ee.setCondicion(ce);
		}else
			getExpressionEntityDao().update(exp);
		super.update (ce);
	}

	
	@Override
	protected void handleRemove(Condition condition) throws Exception {
		ConditionEntity ce = conditionToEntity(condition);
		getExpressionEntityDao().remove(ce.getExpression());
		super.remove (ce);
	}
}
