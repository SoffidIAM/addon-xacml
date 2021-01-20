//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.HashSet;

import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;


/**
 * DAO VariableDefinitionEntity implementation
 */
public class VariableDefinitionEntityDaoImpl extends VariableDefinitionEntityDaoBase
{
	@Override
	public void toVariableDefinition(com.soffid.iam.addons.xacml.model.VariableDefinitionEntity source, com.soffid.iam.addons.xacml.common.VariableDefinition target) {
		super.toVariableDefinition(source, target);
		// Incompatible types source.expression and target.expression
		
		if(source.getExpression() != null){
			target.setExpression(getExpressionEntityDao().toExpression(source.getExpression()));
		}
	}
	
	
	@Override
	public void variableDefinitionToEntity (com.soffid.iam.addons.xacml.common.VariableDefinition source, com.soffid.iam.addons.xacml.model.VariableDefinitionEntity target, boolean copyIfNull) {
		super.variableDefinitionToEntity(source, target, copyIfNull);
		// Attributes for VariableDefinitionEntity
		// Missing attribute policy on entity
		// Missing attribute expression on entity
	}

	
	@Override
	public void remove(VariableDefinitionEntity entity) {
		ExpressionEntity ee = entity.getExpression();
		ee.setVariableDefinition(null);
		getExpressionEntityDao().update(ee);
		super.remove(entity);
		getExpressionEntityDao().remove(ee);
	}

	
	@Override
	protected VariableDefinitionEntity handleCreate(VariableDefinition variable, PolicyEntity policy) throws Exception {
		VariableDefinitionEntity vde = variableDefinitionToEntity(variable);
		ExpressionEntity ee = getExpressionEntityDao().create(variable.getExpression());
		vde.setPolicy(policy);
		vde.setExpression(ee);
		super.create(vde);
		if(variable.getExpression() != null)
		{
			ee.setVariableDefinition(vde);
			getExpressionEntityDao().update(ee);
		}
		variable.setId(vde.getId());
		return vde;
	}

	
	@Override
	protected void handleUpdate(VariableDefinition variable) throws Exception {
		VariableDefinitionEntity vde = variableDefinitionToEntity(variable);
		Expression exp = variable.getExpression();
		if(vde.getExpression() != null)
		{
			if (!vde.getExpression().getId().equals (exp.getId()))	
				getExpressionEntityDao().remove(vde.getExpression());
		}
		if (exp.getId() == null || getExpressionEntityDao().load(exp.getId()) == null){
			ExpressionEntity ee = getExpressionEntityDao().create(exp, vde);
			ee.setVariableDefinition(vde);
			exp.setId(ee.getId());
		}else
			getExpressionEntityDao().update(exp);
		super.update (vde);
	}

	@Override
	protected void handleRemove(VariableDefinition variable) throws Exception {
		VariableDefinitionEntity vde = variableDefinitionToEntity(variable);
		getExpressionEntityDao().remove(vde.getExpression());
		super.remove (vde);
	}
	
}
