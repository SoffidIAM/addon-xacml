//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;

/** 
 * DAO ExpressionEntity implementation
 */ 
public class ExpressionEntityDaoImpl extends ExpressionEntityDaoBase
{

	
	@Override
	public void toExpression(com.soffid.iam.addons.xacml.model.ExpressionEntity source, com.soffid.iam.addons.xacml.common.Expression target) {
		super.toExpression(source, target);
		// Attributes for Expression
		// Incompatible types source.subExpression and target.subExpression
		if(source.getSubexpression() != null)
		{
			List<Expression> expressionList = getExpressionEntityDao().toExpressionList(source.getSubexpression());
			Collections.sort(expressionList, new Comparator<Expression>() {
			public int compare(Expression o1, Expression o2) {
				return o1.getOrder().compareTo(o2.getOrder());
			}
			});
			target.setSubexpression(expressionList);
		}
		else
			target.setSubexpression(new HashSet<Expression>());
	}
	
	
	@Override
	public void expressionToEntity (com.soffid.iam.addons.xacml.common.Expression source, com.soffid.iam.addons.xacml.model.ExpressionEntity target, boolean copyIfNull) {
		super.expressionToEntity(source, target, copyIfNull);
		// Attributes for ExpressionEntity
		// Missing attribute variableDefinition on entity
		// Missing attribute condicion on entity
		// Missing attribute expression on entity
		if (copyIfNull || source.getSubexpression() != null)
		{
			// Incompatible types source.subexpression and target.subexpression
		}
	}
	
	
	@Override
	public void remove(ExpressionEntity entity) {
		for (ExpressionEntity exp: entity.getSubexpression())
		{
			getExpressionEntityDao().remove(exp);
		}
		super.remove(entity);
	}
	

	@Override
	protected ExpressionEntity handleCreate(Expression expression,
			VariableDefinitionEntity variable) throws Exception {
		ExpressionEntity ee = expressionToEntity(expression);
		ee.setVariableDefinition(variable);

		super.create(ee);
		if (expression.getSubexpression() == null)
		{
			expression.setSubexpression(new HashSet<Expression>());
		}
		
		for (Expression exp : expression.getSubexpression())
		{
			ExpressionEntity expE = getExpressionEntityDao().create(exp, variable);
			expE.setExpression(ee);
			getExpressionEntityDao().update(expE);
			ee.getSubexpression().add(expE);
		}
		expression.setId(ee.getId());
		return ee;
	}
	

	@Override
	protected void handleUpdate(Expression expression) throws Exception {
		ExpressionEntity ee = expressionToEntity(expression);
		if (ee.getId() == null)
		{
			super.create(ee);
			expression.setId(ee.getId());
		}
		else
			super.update(ee);
		for (ExpressionEntity subE: ee.getSubexpression())
		{
			boolean found = false;
			for (Expression exp : expression.getSubexpression())
			{
				if (subE.getId().equals (exp.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getExpressionEntityDao().remove(subE);
		}
		for (Expression sub : expression.getSubexpression())
		{
			if (sub.getId() == null){
				ExpressionEntity se = getExpressionEntityDao().create(sub);
				se.setExpression(ee);
				ee.getSubexpression().add(se);
				getExpressionEntityDao().update(ee);
				getExpressionEntityDao().update(se);
			}else{
				getExpressionEntityDao().update(sub);
			}
		}
	}

	
	@Override
	protected ExpressionEntity handleCreate(Expression expression,
			ConditionEntity condition) throws Exception {
		ExpressionEntity ee = expressionToEntity(expression);
		ee.setCondicion(condition);
		
		super.create(ee);
		if (expression.getSubexpression() == null)
		{
			expression.setSubexpression(new HashSet<Expression>());
		}
		
		for (Expression exp : expression.getSubexpression())
		{
			ExpressionEntity expE = getExpressionEntityDao().create(exp, condition);
			expE.setExpression(ee);
			getExpressionEntityDao().update(expE);
			ee.getSubexpression().add(expE);
		}
		expression.setId(ee.getId());
		return ee;
	}
	
	
	@Override
	protected void handleRemove(Expression vo) throws Exception {
		ExpressionEntity ee =expressionToEntity(vo);
		for (ExpressionEntity expE: ee.getSubexpression())
		{
			remove(expE);
		}
		super.remove (ee);
		
	}

	
	@Override
	protected ExpressionEntity handleCreate(Expression expression)
			throws Exception {
		ExpressionEntity ee = expressionToEntity(expression);
		super.create(ee);
		if (expression.getSubexpression() == null)
		{
			expression.setSubexpression(new HashSet<Expression>());
		}
		
		for (Expression exp : expression.getSubexpression())
		{
			ExpressionEntity expE = getExpressionEntityDao().create(exp);
			ee.getSubexpression().add(expE);
			getExpressionEntityDao().update(ee);
			expE.setExpression(ee);
			getExpressionEntityDao().update(expE);
			exp.setId(expE.getId());
			
		}
		expression.setId(ee.getId());
		return ee;
	}

}
