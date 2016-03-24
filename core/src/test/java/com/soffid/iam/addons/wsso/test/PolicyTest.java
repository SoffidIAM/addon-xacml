package com.soffid.iam.addons.wsso.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.EffectTypeEnumeration;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm;
import com.soffid.iam.addons.xacml.common.PolicyCriteria;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.RuleCriteria;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;


public class PolicyTest extends AbstractTest
{
	protected PolicySetService pss;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		pss = (PolicySetService) context.getBean(PolicySetService.SERVICE_NAME);
	}
	
	public void testPolicySet() throws InternalErrorException
	{
		Security.nestedLogin("Test", new String[] { 
				Security.AUTO_AUTHORIZATION_ALL });
		try {
			
			//CREATE POLICYSET
			PolicySet pol = new PolicySet();
			pol.setVersion("V");
			pol.setDescription("Primera política");
			pol.setOrder(new Integer(0));
			pol.setPolicySetId("FIRST");
			pol.setPolicyCombiningAlgId(PolicyCombiningAlgorithm.fromString("PERMIT_OVERRIDES"));
			pol = pss.create(pol);
			
			//CREATE POLICY
			Policy policy = new Policy();
			Long idPolSet = pol.getId();
			policy.setPolicySetId(idPolSet);
			policy.setRuleCombiningAlgId(PolicyCombiningAlgorithm.fromString("PERMIT_OVERRIDES"));
			policy.setDescription("Primera política");
			policy.setOrder(new Integer(0));
			policy.setPolicyId("FIRST OF FIRST");
			policy.setRule(new HashSet<Rule>());
			policy = pss.create(policy);
			
			//CREATE RULE
			Rule rul = new Rule();
			rul.setPolicyId(policy.getId());
			rul.setRuleId("PRIMERA REGLA");
			rul.setOrder(new Integer(0));
			rul.setEffectType(EffectTypeEnumeration.fromString("PERMIT"));
			rul = pss.create(rul);
			
			//UPDATE POLICYSET
			PolicySetCriteria criteriaPol = new PolicySetCriteria();
			criteriaPol.setPolicySetId(pol.getPolicySetId());
			List<PolicySet> llistaPol = pss.findPolicySetByCriteria(criteriaPol);
			PolicySet polup = llistaPol.get(0);
			polup.setDescription("He modificat el primer PolicySet");
			polup = pss.update(polup);
			
			//UPDATE POLICY
			PolicyCriteria criteriaPoli = new PolicyCriteria();
			criteriaPoli.setPolicyId(policy.getPolicyId());
			List<Policy> llistaPoli = pss.findPolicyByCriteria(criteriaPoli);
			Policy poliup = llistaPoli.get(0);
			poliup.setDescription("He modificat el primer Policy");
			Expression expression = new Expression();
			expression.setExpressionType("function_Bag");
			expression.setName(FunctionEnumeration.fromString("TYPE_BAG"));
			VariableDefinition una = new VariableDefinition();
			una.setVariableId("Nova");
			una.setExpression(expression);
			Collection<VariableDefinition> var = new HashSet<VariableDefinition>();
			var.add(una);
			poliup.setVariableDefinition(var);
			poliup = pss.update(poliup);
			
			criteriaPoli.setPolicyId(poliup.getPolicyId());
			llistaPoli = pss.findPolicyByCriteria(criteriaPoli);
			Policy poli = llistaPoli.get(0);
			
			Expression nove = new Expression();
			nove.setExpressionType("resource");
			nove.setName(FunctionEnumeration.fromString("STRING_EQUAL"));
			
			nove.setAttributeValue("Valor1");
			nove.setDataTypeAttributeDesignator(DataType.STRING_TYPE);
			nove.setAttributeDesignator("Valor");
			nove.setDataTypeAttributeValue(DataType.STRING_TYPE);
			
			for(VariableDefinition v: poli.getVariableDefinition())
			{
				Collection<Expression> lala = new HashSet<Expression>();
				lala.add(nove);
				Expression lala2 = v.getExpression();
				lala2.setSubexpression(lala);
			}
			
			poli = pss.update(poli);
			criteriaPoli.setPolicyId(poli.getPolicyId());
			llistaPoli = pss.findPolicyByCriteria(criteriaPoli);
			int i = llistaPoli.size();
			poli = llistaPoli.get(i-1);
			
			//UPDATE RULE
			RuleCriteria criteriaRule = new RuleCriteria();
			criteriaRule.setRuleId(rul.getRuleId());
			List<Rule> llistaRule = pss.findRuleByCriteria(criteriaRule);
			Rule rolup = llistaRule.get(0);
			rolup.setDescription("He modificat el primer Rule");
			rolup = pss.update(rolup);
			
			//DELETES
			//pss.delete(rolup);
			//pss.delete(poli);
			//pss.delete(polup);
			
		} finally {
			Security.nestedLogoff();
		}
	}
	
}
