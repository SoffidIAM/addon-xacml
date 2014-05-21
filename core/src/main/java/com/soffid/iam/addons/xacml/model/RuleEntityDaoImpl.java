//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;

import es.caib.seycon.ng.exception.InternalErrorException;

/**
 * DAO RuleEntity implementation
 */
public class RuleEntityDaoImpl extends RuleEntityDaoBase
{
	@Override
	public void toRule(RuleEntity source, Rule target){
		super.toRule(source, target);
		// Missing attribute policyId on entity
		if(source.getPolicy() != null){
			PolicyEntity policyEntity = source.getPolicy();
			target.setPolicyId(policyEntity.getId());
		}
		
		// Incompatible types source.target and target.target
		Target t;
		if (source.getTarget() != null && source.getTarget().size() >= 1)
			t = getTargetEntityDao().toTarget(source.getTarget().iterator().next());
		else
		{
			t = new Target();
			t.setActionMatch(new HashSet<ActionMatch>());
			t.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
			t.setResourceMatch(new HashSet<ResourceMatch>());
			t.setSubjectMatch(new HashSet<SubjectMatch>());
		}
		target.setTarget(new HashSet<Target>());
		target.getTarget().add(t);
		
		// Incompatible types source.condicion and target.condicion!!!!!!!!!!!!!!!!!!!!
		target.setCondicion(new LinkedList<Condition>());
		for (ConditionEntity conEntity: source.getCondicion()){
			target.getCondicion().add(getConditionEntityDao().toCondition(conEntity));
		}

	}
	
	@Override
	public void ruleToEntity(Rule source, RuleEntity target, boolean copyIfNull){
		super.ruleToEntity(source, target, copyIfNull);

		// Missing attribute policy on entity
		if (copyIfNull || source.getPolicyId() != null)
		{
			target.setPolicy(getPolicyEntityDao().load(source.getPolicyId()));
		}
		
	}
	
	@Override
	public void remove(RuleEntity entity) {
		for (TargetEntity target: entity.getTarget())
		{
			getTargetEntityDao().remove(target);
		}
		for (ConditionEntity con: entity.getCondicion()){
			getConditionEntityDao().remove(con);
		}
		super.remove(entity);
	}
	

	@Override
	protected RuleEntity handleCreate(Rule vo, PolicyEntity pe) throws Exception {
		String ruleId = vo.getRuleId().replace(" ", "");
		vo.setRuleId(ruleId);
		RuleEntity re = ruleToEntity(vo);
		re.setPolicy(pe);
		super.create (re);
		if (vo.getTarget() == null)
		{
			Target t;
			t = new Target();
			t.setActionMatch(new HashSet<ActionMatch>());
			t.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
			t.setResourceMatch(new HashSet<ResourceMatch>());
			t.setSubjectMatch(new HashSet<SubjectMatch>());
			vo.setTarget(new HashSet<Target>());
			vo.getTarget().add(t);
		}
		for (Target target : vo.getTarget())
		{
			TargetEntity te = getTargetEntityDao().create(target);
			te.setRule(re);
			getTargetEntityDao().update(te);
			re.getTarget().add(te);
		}
		if(vo.getCondicion() != null){
			for (Condition con : vo.getCondicion()){
				ConditionEntity conE = getConditionEntityDao().create(con, re);
				conE.setRule(re);
				getConditionEntityDao().update(conE);
				re.getCondicion().add(conE);
			}
		}
		return re;
	}

	@Override
	protected void handleUpdate(Rule vo) throws Exception {
		String ruleId = vo.getRuleId().replace(" ", "");
		vo.setRuleId(ruleId);
		RuleEntity re = ruleToEntity(vo);
		super.update (re);
		for (TargetEntity targetE: re.getTarget())
		{
			boolean found = false;
			for (Target target : vo.getTarget())
			{
				if (targetE.getId().equals (target.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getTargetEntityDao().remove(targetE);
		}
		for (Target target : vo.getTarget())
		{
			if (target.getId() == null){
				TargetEntity te = getTargetEntityDao().create(target);
				te.setRule(re);
				getTargetEntityDao().update(te);
			}else
				getTargetEntityDao().update(target);
		}
		
		for (ConditionEntity conE : re.getCondicion()){
			boolean found = false;
			for (Condition con : vo.getCondicion()){
				if (conE.getId().equals(con.getId())){
					found = true;
					break;
				}
			}
			if (!found)
				getConditionEntityDao().remove(conE);
		}
		for (Condition con : vo.getCondicion()){
			if (con.getId() == null && con.getConditionId() != null){
				ConditionEntity ce = getConditionEntityDao().create(con, re);
				//ce.setRule(re);
				//getConditionEntityDao().update(ce);
				re.getCondicion().add(ce);
				//update(re);
			}else if(con.getId() != null)
				getConditionEntityDao().update(con);
		}
	}

	@Override
	protected void handleRemove(Rule vo) throws Exception {
		RuleEntity re = ruleToEntity(vo);
		for (TargetEntity targetE: re.getTarget())
		{
			getTargetEntityDao().remove(targetE);
		}
		for (ConditionEntity conE : re.getCondicion()){
			getConditionEntityDao().remove(conE);
		} 
		super.remove (re);
	}
	
}
