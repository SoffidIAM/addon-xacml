package com.soffid.iam.addons.wsso.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.EffectTypeEnumeration;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.MatchIdEnumeration;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.addons.xacml.service.XACMLBootService;
import com.soffid.iam.addons.xacml.sync.web.XACMLPolicyServlet;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

public class XACMLTest extends AbstractTest
{
	protected XACMLBootService xacml;
	protected XACMLPolicyServlet xacmlGenerator = new XACMLPolicyServlet();
	PolicySetService policySetService;
	
	@Override 
	protected void setUp() throws Exception
	{
		super.setUp();
		xacml = (XACMLBootService) context.getBean(XACMLBootService.SERVICE_NAME);
		policySetService = (PolicySetService) context.getBean(policySetService.SERVICE_NAME);
		
		//Creació primera PolicySet: amb target i dues policies, la segona amb rule i target
		PolicySet polSet = new PolicySet();
		polSet.setVersion("12");
		polSet.setDescription("Primera política");
		polSet.setOrder(new Integer(1));
		polSet.setPolicySetId("FIRST 1");
		polSet.setPolicyCombiningAlgId(PolicyCombiningAlgorithm.fromString("PERMIT_OVERRIDES"));
		polSet = policySetService.create(polSet);
		
		ResourceMatch rm = new ResourceMatch();
		rm.setMatchId(MatchIdEnumeration.TYPE_MATCH);
		rm.setAttributeValue("attributeValue de Resource");
		rm.setResourceAttributeDesignator("target-namespace");
		rm.setDataTypeAttributeValue(DataType.STRING_TYPE);
		Target tar = new Target();
		tar.setResourceMatch(new HashSet<ResourceMatch>());
		tar.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
		tar.setSubjectMatch(new HashSet<SubjectMatch>());
		tar.setActionMatch(new HashSet<ActionMatch>());
		tar.getResourceMatch().add(rm);
		polSet.getTarget().add(tar);
		
		PolicySetIdReference polSetIdRef = new PolicySetIdReference();
		polSetIdRef.setIdReferenceTypeValue("Policy Set tercera");
		polSetIdRef.setOrder(2);
	
		//pol.setPolicySetIdReference(new HashSet<PolicySetIdReference>());
		//pol.getPolicySetIdReference().add(polSetIdRef);
		
		Policy policy = new Policy();
		policy.setPolicyId("Policy dos");
		policy.setOrder(1);
		policy.setRuleCombiningAlgId(PolicyCombiningAlgorithm.DENY_OVERRIDES);
		policy.setPolicySetId(polSet.getId());
		policy = policySetService.create(policy);
		polSet = policySetService.update(polSet);
		
		Policy policy2 = new Policy();
		policy2.setPolicyId("Policy tres");
		policy2.setDescription("Segona policy fillla");
		policy2.setOrder(2);
		policy2.setRuleCombiningAlgId(PolicyCombiningAlgorithm.DENY_OVERRIDES);
		policy2.setPolicySetId(polSet.getId());
		policy2 = policySetService.create(policy2);
		polSet = policySetService.update(polSet);
		
		Rule rule1 = new Rule();
		rule1.setRuleId("Rule 1");
		rule1.setEffectType(EffectTypeEnumeration.PERMIT);
		rule1.setDescription("Descripció de la rule 1");
		rule1.setOrder(1);
		SubjectMatch sm = new SubjectMatch();
		sm.setMatchId(MatchIdEnumeration.TYPE_MATCH);
		sm.setAttributeValue("attributeValue de Subject");
		sm.setSubjectAttributeDesignator("subject");
		sm.setDataTypeAttributeValue(DataType.STRING_TYPE);
		Target tarRule = new Target();
		tarRule.setResourceMatch(new HashSet<ResourceMatch>());
		tarRule.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
		tarRule.setSubjectMatch(new HashSet<SubjectMatch>());
		tarRule.setActionMatch(new HashSet<ActionMatch>());
		tarRule.getSubjectMatch().add(sm);
		rule1.setPolicyId(policy2.getId());
		rule1 = policySetService.create(rule1);
		
		rule1.setTarget(new HashSet<Target>());
		rule1.getTarget().add(tarRule);
		rule1 = policySetService.update(rule1);
		
		policy2.getRule().add(rule1);
				
		policy2 = policySetService.update(policy2);
		polSet = policySetService.update(polSet);
		
		
		//Segona policySet: sense target, amb una policy amb rule i target, una variableDefinition amb una 
		//expressió que té dues subExpressions i finalment una condition amb una expressió de tipus variable reference
		PolicySet policySet2 = new PolicySet();
		policySet2.setVersion("1");
		policySet2.setDescription("Segona política");
		policySet2.setOrder(new Integer(2));
		policySet2.setPolicySetId("SECOND");
		policySet2.setPolicyCombiningAlgId(PolicyCombiningAlgorithm.fromString("PERMIT_OVERRIDES"));
		policySet2 = policySetService.create(policySet2);
		
		Policy policy3 = new Policy();
		policy3.setPolicyId("Policy primera de la segona PolicySet");
		policy3.setDescription("Primera policy fillla");
		policy3.setOrder(1);
		policy3.setRuleCombiningAlgId(PolicyCombiningAlgorithm.DENY_OVERRIDES);
		policy3.setPolicySetId(policySet2.getId());
		policy3 = policySetService.create(policy3);
		policySet2 = policySetService.update(policySet2);
		
		Rule rule2 = new Rule();
		rule2.setRuleId("Rule 1");
		rule2.setEffectType(EffectTypeEnumeration.PERMIT);
		rule2.setDescription("Descripció de la rule 1 de la primera policy de la segona Policy Set");
		rule2.setOrder(1);
		ActionMatch am = new ActionMatch();
		am.setMatchId(MatchIdEnumeration.TYPE_MATCH);
		am.setAttributeValue("attributeValue de action");
		am.setActionAttributeDesignator("action");
		am.setDataTypeAttributeValue(DataType.STRING_TYPE);
		EnvironmentMatch em = new EnvironmentMatch();
		em.setMatchId(MatchIdEnumeration.TYPE_MATCH);
		em.setAttributeValue("attributeValue de environment");
		em.setEnvironmentAttributeDesignator("environment");
		em.setDataTypeAttributeValue(DataType.STRING_TYPE);
		EnvironmentMatch em2 = new EnvironmentMatch();
		em2.setMatchId(MatchIdEnumeration.TYPE_MATCH);
		em2.setAttributeValue("attributeValue de environment2");
		em2.setEnvironmentAttributeDesignator("environment2");
		em2.setDataTypeAttributeValue(DataType.STRING_TYPE);
		Target tarRule2 = new Target();
		tarRule2.setResourceMatch(new HashSet<ResourceMatch>());
		tarRule2.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
		tarRule2.setSubjectMatch(new HashSet<SubjectMatch>());
		tarRule2.setActionMatch(new HashSet<ActionMatch>());
		tarRule2.getActionMatch().add(am);
		tarRule2.getEnvironmentMatch().add(em);
		tarRule2.getEnvironmentMatch().add(em2);
		rule2.setPolicyId(policy3.getId());
		rule2 = policySetService.create(rule2);
		
		rule2.setTarget(new HashSet<Target>());
		rule2.getTarget().add(tarRule2);
		rule2 = policySetService.update(rule2);
		
		policy3.getRule().add(rule2);
		
		Expression exp1 = new Expression();
		exp1.setExpressionType("function_Comparison");
		exp1.setName(FunctionEnumeration.STRING_EQUAL);
		exp1.setOrder(1);
		
		VariableDefinition varDef = new VariableDefinition();
		varDef.setVariableId("Primera Variable");
		varDef.setExpression(exp1);
		
		policy3.setVariableDefinition(new HashSet<VariableDefinition>());
		policy3.getVariableDefinition().add(varDef);
		policy3 = policySetService.update(policy3);
		policySet2 = policySetService.update(policySet2);
		
		Expression subExp1 = new Expression();
		subExp1.setExpressionType("function_Higher");
		subExp1.setName(FunctionEnumeration.TYPE_ONE_AND_ONLY);
		subExp1.setAttributeDesignator("STRING");
		subExp1.setOrder(1);
		Expression subExp2 = new Expression();
		subExp2.setExpressionType("function_Higher");
		subExp2.setName(FunctionEnumeration.TYPE_ONE_AND_ONLY);
		subExp2.setAttributeDesignator("STRING");
		subExp2.setAttributeSelector("valor de att selector");
		subExp2.setOrder(2);
		
		Collection<VariableDefinition> vdCol = policy3.getVariableDefinition();
		VariableDefinition vd = vdCol.iterator().next();
		Expression e1 = vd.getExpression();
		e1.setSubexpression(new HashSet<Expression>());
		e1.getSubexpression().add(subExp1);
		e1.getSubexpression().add(subExp2);
		vd.setExpression(e1);
				
		policy3 = policySetService.update(policy3);
		policySet2 = policySetService.update(policySet2);
		
		Expression fi1 = new Expression();
		fi1.setExpressionType("subject");
		fi1.setAttributeDesignator("un atribut");
		fi1.setOrder(1);
		fi1.setName(FunctionEnumeration.STRING_EQUAL);
		
		subExp1.setSubexpression(new HashSet<Expression>());
		subExp1.getSubexpression().add(fi1);
		
		Expression fi2 = new Expression();
		fi2.setExpressionType("attributeSelector");
		fi2.setAttributeSelector("un atribut  ...");
		fi2.setOrder(1);
		fi2.setName(FunctionEnumeration.STRING_EQUAL);
		
		subExp2.setSubexpression(new HashSet<Expression>());
		subExp2.getSubexpression().add(fi2);
		
		
		Condition con = new Condition();
		con.setConditionId("Primera condicion");
		Expression expdecon = new Expression();
		expdecon.setVariableId("Id de variable reference");
		expdecon.setExpressionType("variable");
		expdecon.setName(FunctionEnumeration.STRING_EQUAL);
		con.setExpression(expdecon);
		rule2.setCondicion(new HashSet<Condition>());
		rule2.getCondicion().add(con);
		
		policy3 = policySetService.update(policy3);
		policySet2 = policySetService.update(policySet2);
		
		//Afegim una segona Rule a una nova policy
		Policy policy4 = new Policy();
		policy4.setPolicyId("Policy segona de la segona PolicySet");
		policy4.setDescription("Segona policy fillla");
		policy4.setOrder(2);
		policy4.setRuleCombiningAlgId(PolicyCombiningAlgorithm.DENY_OVERRIDES);
		policy4.setPolicySetId(policySet2.getId());
		policy4 = policySetService.create(policy4);
		policySet2 = policySetService.update(policySet2);
		
		VariableDefinition vd2 = new VariableDefinition();
		vd2.setVariableId(" filla de la segona PolicySet");
		Expression exp4 = new Expression();
		exp4.setExpressionType("function_Comparison");
		exp4.setName(FunctionEnumeration.DATE_LESS_THAN_OR_EQUAL);
		exp4.setOrder(1);
		vd2.setExpression(exp4);
		
		policy4.setVariableDefinition(new HashSet<VariableDefinition>());
		policy4.getVariableDefinition().add(vd2);
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
		
		Expression subExp3 = new Expression();
		subExp3.setExpressionType("function_Bag");
		subExp3.setName(FunctionEnumeration.TYPE_ONE_AND_ONLY);
		subExp3.setAttributeDesignator("DATE");
		subExp3.setOrder(1);
		
		exp4.setSubexpression(new HashSet<Expression>());
		exp4.getSubexpression().add(subExp3);
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
		
		Expression subExp3sub1 = new Expression();
		subExp3sub1.setExpressionType("environment");
		subExp3sub1.setAttributeDesignator("current date");
		subExp3sub1.setAttributeValue("attribute value");
		subExp3sub1.setName(FunctionEnumeration.STRING_EQUAL);
		subExp3sub1.setOrder(1);
		
		subExp3.setSubexpression(new HashSet<Expression>());
		subExp3.getSubexpression().add(subExp3sub1);
		
		Expression subExp4 = new Expression();
		subExp4.setExpressionType("function_Arithmetic");
		subExp4.setName(FunctionEnumeration.DATE_ADD_YEAR_MONTH_DURATION);
		//subExp4.setAttributeSelector("attribute ID");
		subExp4.setOrder(2);
		
		exp4.getSubexpression().add(subExp4);
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
		
		Expression subExp4sub1 = new Expression();
		subExp4sub1.setExpressionType("function_Bag");
		subExp4sub1.setName(FunctionEnumeration.TYPE_ONE_AND_ONLY);
		subExp4sub1.setAttributeDesignator("DATETIME");
		subExp4sub1.setOrder(1);
		
		subExp4.setSubexpression(new HashSet<Expression>());
		subExp4.getSubexpression().add(subExp4sub1);
		
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
		
		Expression subExp4sub1sub = new Expression();
		subExp4sub1sub.setExpressionType("attributeSelector");
		subExp4sub1sub.setName(FunctionEnumeration.STRING_EQUAL);
		subExp4sub1sub.setAttributeSelector("date");
		
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
		
		subExp4sub1.setSubexpression(new HashSet<Expression>());
		subExp4sub1.getSubexpression().add(subExp4sub1sub);
		
		policy4 = policySetService.update(policy4);
		policySet2 = policySetService.update(policySet2);
	}
	
	public void testXACMLGenerator() throws InternalErrorException, ServletException, 
		ParserConfigurationException, TransformerException, UnrecoverableKeyException, InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IllegalStateException, NoSuchProviderException, SignatureException, SAXException, IOException
	{
		Security.nestedLogin("Test", new String[] { 
				Security.AUTO_AUTHORIZATION_ALL });
		try 
		{
			//xacmlGenerator.setPolicySetService(policySetService);
			//xacmlGenerator.generate(System.out, null);
			//new XACMLPolicyGenerator().generate(System.out);
		} finally {
			Security.nestedLogoff();
		}

	}
}
