//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Obligation;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
import com.soffid.iam.addons.xacml.common.Target;

import es.caib.seycon.ng.exception.InternalErrorException;

/**
 * DAO PolicyEntity implementation
 */
public class PolicyEntityDaoImpl extends PolicyEntityDaoBase
{
	@Override
	public void toPolicy(PolicyEntity sourceEntity, Policy targetPolicy){
		super.toPolicy(sourceEntity, targetPolicy);
		
		// Incompatible types source.target and target.target
		Target t;
		targetPolicy.setTarget(new HashSet<Target>());
		if (sourceEntity.getTarget() != null && sourceEntity.getTarget().size() >= 1)
		{
			t = getTargetEntityDao().toTarget(sourceEntity.getTarget().iterator().next());
			targetPolicy.getTarget().add(t);
		}
		
		// Incompatible types source.rule and target.rule
		LinkedList<Rule> rules = new LinkedList<Rule>();
		for (RuleEntity ruleEntity: sourceEntity.getRule())
		{
			rules.add(getRuleEntityDao().toRule(ruleEntity));
		}
		Collections.sort(rules, new Comparator<Rule>() {
			public int compare(Rule o1, Rule o2) {
				return o1.getOrder().compareTo(o2.getOrder());
			}
		});
		targetPolicy.setRule(rules);
		
		
		// Incompatible types source.variableDefinition and target.variableDefinition
		targetPolicy.setVariableDefinition(new LinkedList<VariableDefinition>());
		for (VariableDefinitionEntity varEntity: sourceEntity.getVariableDefinition()){
			targetPolicy.getVariableDefinition().add(getVariableDefinitionEntityDao().toVariableDefinition(varEntity));
		}
		
		// Missing attribute policySetId on entity
		if(sourceEntity.getPolicySet() != null){
			PolicySetEntity policySetEntity = sourceEntity.getPolicySet();
			targetPolicy.setPolicySetId(policySetEntity.getId());
		}				
		targetPolicy.setObligation(new LinkedList<Obligation>());
		for (ObligationEntity oe: sourceEntity.getObligation()) {
			targetPolicy.getObligation().add(getObligationEntityDao().toObligation(oe));
		}
	}
	
	
	@Override
	public void policyToEntity(Policy source, PolicyEntity target, boolean copyIfNull){
		super.policyToEntity(source, target, copyIfNull);
		// Missing attribute obligations on entity
		// Missing attribute rule on entity
		// Incompatible types source.target and target.target
		// Missing attribute policyDefaults on entity
		// Missing attribute combinerParameters on entity
		// Missing attribute ruleCombinerParameters on entity
		// Missing attribute policySet on entity
		if (copyIfNull || source.getPolicySetId() != null)
		{
			target.setPolicySet(getPolicySetEntityDao().load(source.getPolicySetId()));
		}
		
		// Incompatible types source.variableDefinition and target.variableDefinition
	}

	@Override
	public void remove(PolicyEntity entity) {
		for(TargetEntity target: entity.getTarget())
			getTargetEntityDao().remove(target);
		for(RuleEntity rule : entity.getRule())
			getRuleEntityDao().remove(rule);
		for(VariableDefinitionEntity var : entity.getVariableDefinition())
			getVariableDefinitionEntityDao().remove(var);
		super.remove(entity);
	}


	@Override
	protected PolicyEntity handleCreate(Policy vo) throws Exception {
		String policyId = vo.getPolicyId().replace(" ", "");
		vo.setPolicyId(policyId);
		PolicyEntity pe = policyToEntity(vo);
		super.create (pe);
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
			te.setPolicy(pe);
			getTargetEntityDao().update(te);
			pe.getTarget().add(te);
		}
		
		if(vo.getRule() != null){
			for (Rule rule : vo.getRule())
			{
				RuleEntity re = getRuleEntityDao().create(rule, pe);
				re.setPolicy(pe);
				//rule.setId(re.getId());
				getRuleEntityDao().update(re);
			}
		}
		
		if(vo.getVariableDefinition() != null){
			for (VariableDefinition var : vo.getVariableDefinition()){
				VariableDefinitionEntity varE = getVariableDefinitionEntityDao().create(var, pe);
				varE.setPolicy(pe);
				getVariableDefinitionEntityDao().update(varE);
			}
		}

		if (vo.getObligation() != null)
		{
			for (Obligation o : vo.getObligation())
			{
				ObligationEntity oe = getObligationEntityDao().create(o);
				oe.setPolicy(pe);
				getObligationEntityDao().update(oe);
				pe.getObligation().add(oe);
			}
		}
		return pe;
	}


	@Override
	protected void handleUpdate(Policy vo) throws Exception {
		PolicyEntity pe = policyToEntity(vo);
		super.update (pe);
		for (TargetEntity targetE: new LinkedList<TargetEntity>( pe.getTarget()) )
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
			if (!found) {
				getTargetEntityDao().remove(targetE);
				pe.getTarget().remove(targetE);
			}
		}
		for (Target target : vo.getTarget())
		{
			if (target.getId() == null || getTargetEntityDao().load(target.getId()) == null){
				TargetEntity te = getTargetEntityDao().create(target);
				te.setPolicy(pe);
				getTargetEntityDao().update(te);
				pe.getTarget().add(te);
			}else 
				getTargetEntityDao().update(target);
		}
		
		
 		for(RuleEntity ruleE: new LinkedList<RuleEntity>(pe.getRule()))
		{
			boolean found = false;
			for(Rule rule : vo.getRule())
			{
				if(ruleE.getId().equals(rule.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getRuleEntityDao().remove(ruleE);
				pe.getRule().remove(ruleE);
			}
		}
		for (Rule rule : vo.getRule())
		{
			if(rule.getId() == null || getRuleEntityDao().load(rule.getId()) == null){
				rule.setPolicyId(pe.getId());
				RuleEntity re = getRuleEntityDao().create(rule, pe);
				rule.setId(re.getId());
				pe.getRule().add(re);
			}else if(rule.getId() != null){
				rule.setPolicyId(pe.getId());
				getRuleEntityDao().update(rule);
			}
		}
		
		for (VariableDefinitionEntity varE: new LinkedList<VariableDefinitionEntity> (pe.getVariableDefinition()))
		{
			boolean found = false;
			for (VariableDefinition var : vo.getVariableDefinition())
			{
				if (varE.getId().equals (var.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getVariableDefinitionEntityDao().remove(varE);
				pe.getVariableDefinition().remove(varE);
			}
		}
		for (VariableDefinition var : vo.getVariableDefinition())
		{
			if (var.getId() == null || getVariableDefinitionEntityDao().load(var.getId()) == null){
				VariableDefinitionEntity ve = getVariableDefinitionEntityDao().create(var, pe);
				pe.getVariableDefinition().add(ve);
			}else
				getVariableDefinitionEntityDao().update(var);
		}
		for (ObligationEntity oe: new LinkedList<ObligationEntity>( pe.getObligation()))
		{
			boolean found = false;
			for (Obligation o : vo.getObligation())
			{
				if (oe.getId().equals (o.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getObligationEntityDao().remove(oe);
				pe.getObligation().remove(oe);
			}
		}
		for (Obligation o : vo.getObligation())
		{
			if (o.getId() == null || getObligationEntityDao().load(o.getId()) == null){
				ObligationEntity oe = getObligationEntityDao().create(o);
				oe.setPolicy(pe);
				getObligationEntityDao().update(oe);
			}else {
				getObligationEntityDao().update(o);
			}
		}
	}


	@Override
	protected void handleRemove(Policy vo) throws Exception {
		PolicyEntity pse = policyToEntity(vo);
		for (TargetEntity targetE: pse.getTarget())
		{
			getTargetEntityDao().remove(targetE);
		}
		for (RuleEntity ruleE: pse.getRule())
		{
			getRuleEntityDao().remove(ruleE);
		}
		for (VariableDefinitionEntity varE: pse.getVariableDefinition())
		{
			getVariableDefinitionEntityDao().remove(varE);
		}
		for (ObligationEntity oe: pse.getObligation())
			getObligationEntityDao().remove(oe);
		super.remove (pse);
	}
}
