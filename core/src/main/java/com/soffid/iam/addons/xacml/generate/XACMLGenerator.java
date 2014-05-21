package com.soffid.iam.addons.xacml.generate;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyCriteria;
import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
import com.soffid.iam.addons.xacml.service.PolicySetService;

import es.caib.seycon.ng.exception.InternalErrorException;

public class XACMLGenerator {
	
	private static final String identationReference = "{http://xml.apache.org/xslt}indent-amount";
	Document doc;
    private DocumentBuilder dBuilder;
    PolicySetService policySetService;
    
	final static String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	final static String POLICY_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:policy:schema:os";
	final static String CONTEXT_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:context:schema:os";
	final static String DATATYPEDATE = "http://www.w3.org/2001/XMLSchema#date";
	final static String DATATYPETIME = "http://www.w3.org/2001/XMLSchema#time";
	final static String DATATYPEDATETIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	final static String DATATYPESTRING = "http://www.w3.org/2001/XMLSchema#string";
	final static String DATATYPEINTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	final static String DATATYPEANYURI = "http://www.w3.org/2001/XMLSchema#anyURI";
	final static String DATATYPEBOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	final static String DATATYPEDOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	final static String DATATYPEHEXBINARY = "http://www.w3.org/2001/XMLSchema#hexBinary"; 
	final static String DATATYPEBASE64BINARY = "http://www.w3.org/2001/XMLSchema#base64Binary";
	final static String DATATYPEYEARMONTH = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration";
	final static String DATATYPEDAYTIME = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration";
	final static String DATATYPEIPADDRESS = "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress";
	final static String DATATYPEDNSNAME= "urn:oasis:names:tc:xacml:2.0:data-type:dnsName";
	final static String DATATYPERFC822NAME = "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";
	final static String DATATYPEX500NAME = "urn:oasis:names:tc:xacml:1.0:data-type:x500Name";
	final static String function10 = "urn:oasis:names:tc:xacml:1.0:function:";
	final static String function20 = "urn:oasis:names:tc:xacml:2.0:function:";
	
	public XACMLGenerator(PolicySetService pss){
		super(); 
		policySetService = pss;
	}
	
	public void generate (OutputStream out, String policySetId, String version) throws Exception{
		PolicySetCriteria criteria = new PolicySetCriteria();
		criteria.setPolicySetId(policySetId);
		criteria.setVersion(version);
		try{
			Collection<PolicySet> polSetCollection = policySetService.findPolicySetByCriteria(criteria);
			if(polSetCollection != null){
				for(PolicySet polSet: polSetCollection){
					generate(out, polSet);
				} 
			}
		}catch (Exception e) {
			throw new Exception("Error generating xml file: " + e.getMessage());	
		}
	}
	
	public void generatePolicy (OutputStream out, String policyId, String version) throws Exception{
		PolicyCriteria criteria = new PolicyCriteria();
		criteria.setPolicyId(policyId);
		criteria.setVersion(version);
		try{
			Collection<Policy> polCollection = policySetService.findPolicyByCriteria(criteria);
			if(polCollection != null){
				for(Policy pol: polCollection){
					generate(out, pol);
				} 
			}
		}catch (Exception e) {
			throw new Exception("Error generating xml file: " + e.getMessage());	
		}
	}
	
	
	public void generate (OutputStream out, Object polSet) throws SAXException, IOException, ParserConfigurationException, 
		TransformerException, UnrecoverableKeyException, InvalidKeyException, KeyStoreException, 
		NoSuchAlgorithmException, CertificateException, IllegalStateException, NoSuchProviderException, 
		SignatureException, InternalErrorException {
		
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 dbFactory.setNamespaceAware(true);
		 dBuilder = dbFactory.newDocumentBuilder();
		 doc = dBuilder.newDocument();
		 
		 Element root;
		 if(polSet instanceof PolicySet){
			 root = doc.createElementNS(POLICY_NAMESPACE, "PolicySet");
			 generatePolicySet(root, (PolicySet) polSet);
		 }
		 else{
			 root = doc.createElementNS(POLICY_NAMESPACE, "Policy");
			 generatePolicy(root, (Policy) polSet);
		 }
		 doc.appendChild(root);
		     
		 // write the content into xml file
		 TransformerFactory transformerFactory = TransformerFactory.newInstance();
		 Transformer transformer = transformerFactory.newTransformer();
		 transformer.setOutputProperty(identationReference, "2");
		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");  
		 
		 DOMSource source = new DOMSource(doc);
		 StreamResult result = new StreamResult(out);
		 transformer.transform(source, result);
		 
		}


	private void generatePolicySet(Element node, PolicySet policySet) throws SAXException, IOException, 
			InternalErrorException
	{
	  node.setAttribute("PolicySetId", policySet.getPolicySetId());
	  String version = policySet.getVersion();
	  if (version == null || version.isEmpty())
	 	 node.setAttribute("Version", "1");
	  else
	 	 node.setAttribute("Version", version);
	  node.setAttribute("PolicyCombiningAlgId", 
	 		 "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:" + 
	 				 policySet.getPolicyCombiningAlgId().toString().toLowerCase().replace('_', '-'));
	  
	  String description = policySet.getDescription();
	  if(description != null && !description.isEmpty())
	  {
	 	 Element descrip = doc.createElement("Description");
	 	 descrip.setTextContent(description);
	 	 node.appendChild(descrip);
	  }
	  
	  Collection<Target> targetCollection = policySet.getTarget();
	  if(targetCollection != null && !targetCollection.isEmpty())
	  {
	 	 for(Iterator<Target> it = targetCollection.iterator(); it.hasNext();)
	 	 {
	 		 Target target = (Target) it.next();
	 		 generateTarget(node, target);
	 	 }
	  }
	  
	  //S'han d'ordenar entre ells i construir el nodes segons l'ordre
	  Collection<PolicySet> policySetCollectionChilds = policySetService.findChildrenPolicySet(policySet.getId());
	  Collection<Policy> policyCollectionChilds = policySetService.findPolicyChildrenPolicySet(policySet.getId());
	  Collection<PolicySetIdReference> policySetIdRefCollectionChilds = policySet.getPolicySetIdReference();
	  Collection<PolicyIdReference> policyIdReferenceCollectionChilds = policySet.getPolicyIdReference();
	  int childs = policySetCollectionChilds.size() + policyCollectionChilds.size() + policySetIdRefCollectionChilds
	 		 .size() + policyIdReferenceCollectionChilds.size();

	  for( int i = 0; i < childs; i ++)
	  {
	 	 Object comp = findElementAt(i + 1 , policySetCollectionChilds, policyCollectionChilds, policySetIdRefCollectionChilds,
	 			 policyIdReferenceCollectionChilds);
	 	 if(comp instanceof PolicySet)
	 	 {
	 		 Element nodenou = doc.createElementNS(POLICY_NAMESPACE, "PolicySet");
	 		 generatePolicySet(nodenou, (PolicySet) comp);
	 		 node.appendChild(nodenou);
	 	 }
	 	 else if(comp instanceof Policy)
	 		 generatePolicy(node, (Policy) comp);
	 	 else if(comp instanceof PolicySetIdReference)
	 		 generatePolicySetIdReference(node, (PolicySetIdReference) comp);
	 	 else if(comp instanceof PolicyIdReference)
	 		 generatePolicyIdReference(node, (PolicyIdReference) comp);
	  }
	}
	
	
	private void generatePolicyIdReference(Element node, PolicyIdReference comp) {
		Element polIdRef = doc.createElement("PolicyIdReference");
		String version = comp.getVersion();
		String earliest = comp.getEarliestVersion();
		String latest = comp.getLatestVersion();
		String idReferenceType = comp.getIdReferenceTypeValue();
		if(version != null && !version.isEmpty())
			polIdRef.setAttribute("Version", version);
		if(earliest != null && !earliest.isEmpty())
			polIdRef.setAttribute("EarliestVersion", earliest);
		if(latest != null && !latest.isEmpty())
			polIdRef.setAttribute("LatestVersion", latest);
		polIdRef.setTextContent(idReferenceType);
		node.appendChild(polIdRef);
	}
	
	
	private void generatePolicySetIdReference(Element node,
			PolicySetIdReference comp) {
		Element polSetIdRef = doc.createElement("PolicySetIdReference");
		String version = comp.getVersion();
		String earliest = comp.getEarliestVersion();
		String latest = comp.getLatestVersion();
		String idReferenceType = comp.getIdReferenceTypeValue();
		if(version != null && !version.isEmpty())
			polSetIdRef.setAttribute("Version", version);
		if(earliest != null && !earliest.isEmpty())
			polSetIdRef.setAttribute("EarliestVersion", earliest);
		if(latest != null && !latest.isEmpty())
			polSetIdRef.setAttribute("LatestVersion", latest);
		polSetIdRef.setTextContent(idReferenceType);
		node.appendChild(polSetIdRef);
	}
	
	
	private void generatePolicy(Element node, Policy comp) {
		Element policy;
		if(node == null)
			policy = doc.createElementNS(POLICY_NAMESPACE, "Policy");
		else
			policy = doc.createElement("Policy");
		String version = comp.getVersion();
		policy.setAttribute("PolicyId", comp.getPolicyId());
		if(version != null && !version.isEmpty())
			policy.setAttribute("Version", version);
		policy.setAttribute("RuleCombiningAlgId", 
				"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:" + 
						comp.getRuleCombiningAlgId().toString().toLowerCase().replace('_', '-'));
		
		String description = comp.getDescription();
	 if(description != null && !description.isEmpty())
	 {
		 Element descrip = doc.createElement("Description");
		 descrip.setTextContent(description);
		 policy.appendChild(descrip);
	 }
	 
	 Collection<Target> targetCollection = comp.getTarget();
	 for(Iterator<Target> it = targetCollection.iterator(); it.hasNext();)
	 {
	 	Target target = (Target) it.next();
		 	generateTarget(policy, target);
	 }
	 
	 Collection<VariableDefinition> variableDefinitionCollection = comp.getVariableDefinition();
	 for(Iterator<VariableDefinition> it = variableDefinitionCollection.iterator(); it.hasNext();)
	 {
	 	VariableDefinition variableDefinition = (VariableDefinition) it.next();
	 	generateVariableDefinition(policy, variableDefinition);
	 }
	 
	 Collection<Rule> ruleCollection = comp.getRule();
	 for(Iterator<Rule> it = ruleCollection.iterator(); it.hasNext();)
	 {
	 	Rule rule = (Rule) it.next();
	 	generateRule(policy, rule);
	 }
	 if(node != null)
	 	node.appendChild(policy);
	 else
	 	doc.appendChild(policy);
	}
	
	
	private void generateRule(Element node, Rule rule) {
		Element rul = doc.createElement("Rule");
		String effect = rule.getEffectType().toString().toLowerCase();
		effect = effect.substring(0, 1).toUpperCase() + effect.substring(1);
		rul.setAttribute("Effect", effect);
		rul.setAttribute("RuleId", rule.getRuleId());
		
		String description = rule.getDescription();
	 if(description != null && !description.isEmpty())
	 {
		 	Element descrip = doc.createElement("Description");
		 	descrip.setTextContent(description);
		 	rul.appendChild(descrip);
	 }
	 
	 Collection<Target> targetCollection = rule.getTarget();
	 for(Iterator<Target> it = targetCollection.iterator(); it.hasNext();)
	 {
	 	Target target = (Target) it.next();
	 	generateTarget(rul, target);
	 }
	 
	 Collection<Condition> conditionCollection = rule.getCondicion();
	 for(Iterator<Condition> it = conditionCollection.iterator(); it.hasNext();)
	 {
	 	Condition condition = (Condition) it.next();
	 	generateCondition(rul, condition);
	 }
 
	node.appendChild(rul);
	}
	
	
	private void generateCondition(Element rul, Condition condition) {
		Element conElement = doc.createElement("Condition");
		
		Expression expression =  condition.getExpression();
		generateExpression(conElement, expression, null);
		rul.appendChild(conElement);
	}
	
	
	private void generateExpression(Element node, Expression expression, String tipusExpressioPare) {
		
		String expressionType = expression.getExpressionType();
		String type = null;
		if(tipusExpressioPare != null && !tipusExpressioPare.isEmpty())
			type = tipusExpressioPare;
		else
			type = DATATYPESTRING;
		if(expressionType.startsWith("function"))
		{
			Element expElement = doc.createElement("Apply");
			generateApply(expElement, expression, tipusExpressioPare);
			node.appendChild(expElement);
		}
		else if(expressionType.equals("attributeSelector"))
		{
			generateAttributeSelector(node, expression.getAttributeSelector(), type);
		}
		else if(expressionType.equals("attributeValue"))
		{
			generateAttributeValue(node, expression.getAttributeValue(), expression.getDataTypeAttributeValue());
		}
		else if(expressionType.equals("name"))
		{
			generateFunctio(node, expression.getName().toString());
		}
		else if(expressionType.equals("variable"))
		{
			generateVariableReference(node, expression.getVariableId());
		}
		else if(expressionType.equals("action"))
		{
			generateActionAttributeDesignator(node, expression.getAttributeDesignator(), expression.getDataTypeAttributeDesignator());
		}
		else if(expressionType.equals("resource"))
		{
			generateResourceAttributeDesignator(node, expression.getAttributeDesignator(), expression.getDataTypeAttributeDesignator());
		}
		else if(expressionType.equals("subject"))
		{
			generateSubjectAttributeDesignator(node, expression.getAttributeDesignator(), expression.getDataTypeAttributeDesignator());
		}
		else if(expressionType.equals("environment"))
		{
			generateEnvironmentAttributeDesignator(node, expression.getAttributeDesignator(), expression.getDataTypeAttributeDesignator());
		}
		
	}
	
	
	private void generateVariableReference(Element expElement, String variableId) {
		Element variableIdElement = doc.createElement("VariableReference");
		variableIdElement.setAttribute("VariableId", variableId);
		expElement.appendChild(variableIdElement);
	}
	
	
	private void generateApply(Element expElement, Expression expression, String tipusExpressioPare) {
		
		String nom = formatFunctionId(expression);
		expElement.setAttribute("FunctionId", nom);
		
		String tipus = tipusFunction(nom);
		List<Expression> expressionCollection = (List<Expression>) expression.getSubexpression();
		Collections.sort(expressionCollection, new Comparator<Expression>(){
			public int compare(Expression e1, Expression e2) {
				return e1.getOrder().compareTo(e2.getOrder());
			}	
		});
		if(tipus != null && !tipus.isEmpty())
			tipusExpressioPare = tipus;
		for(Iterator<Expression> it = expressionCollection.iterator(); it.hasNext();)
		{
			Expression subExpression = it.next();
			generateExpression(expElement, subExpression, tipusExpressioPare);
		}
	}
	
	
	private void generateFunctio(Element expElement, String functio) {
		expElement.setAttribute("Function", functio);
	}
	
	
	private void generateVariableDefinition(Element node,
			VariableDefinition variableDefinition) {
		Element varDefElement = doc.createElement("VariableDefinition");
		varDefElement.setAttribute("VariableId", variableDefinition.getVariableId());
		
		Expression expression =  variableDefinition.getExpression();
		generateExpression(varDefElement, expression, null);
		node.appendChild(varDefElement);
	}
	
	
	private Object findElementAt(int i,
			Collection<PolicySet> policySetCollectionChilds,
			Collection<Policy> policyCollectionChilds,
			Collection<PolicySetIdReference> policySetIdRefCollectionChilds,
			Collection<PolicyIdReference> policyIdReferenceCollectionChilds) {
		Object litoca = new Object();
		for(Iterator<PolicySet> it = policySetCollectionChilds.iterator(); it.hasNext();)
		{
			PolicySet policySet = it.next();
			if(policySet.getOrder() == i)
			{
				litoca = (Object) policySet;
				break;
			}
		}
		for(Iterator<Policy> it = policyCollectionChilds.iterator(); it.hasNext();)
		{
			Policy policy = it.next();
			if(policy.getOrder() == i)
			{
				litoca = (Object) policy;
				break;
			}
		}
		for(Iterator<PolicySetIdReference> it = policySetIdRefCollectionChilds.iterator(); it.hasNext();)
		{
			PolicySetIdReference policySetIdRef = it.next();
			if(policySetIdRef.getOrder() == i)
			{
				litoca = (Object) policySetIdRef;
				break;
			}
		}	
		for(Iterator<PolicyIdReference> it = policyIdReferenceCollectionChilds.iterator(); it.hasNext();)
		{
			PolicyIdReference policyIdRef = it.next();
			if(policyIdRef.getOrder() == i)
			{
				litoca = (Object) policyIdRef;
				break;
			}
		}
		
		return litoca;
	}
	
	
	private void generateTarget(Element node, Target target)
	{
		Element tar = doc.createElement("Target");
		
		Collection<SubjectMatch> subjectMatchCollection = target.getSubjectMatch();
		if(subjectMatchCollection != null && !subjectMatchCollection.isEmpty())
		{
			Element subjects = doc.createElement("Subjects");
			Element subject = doc.createElement("Subject");
			for(Iterator<SubjectMatch> it = subjectMatchCollection.iterator(); it.hasNext();)
			{
				SubjectMatch subjectMatch = (SubjectMatch) it.next();
				generateSubjectMatch(subject, subjectMatch);
				
			}
			subjects.appendChild(subject);
			tar.appendChild(subjects);
		}
		
		Collection<ResourceMatch> resourceMatchCollection = target.getResourceMatch();
		if(resourceMatchCollection != null && !resourceMatchCollection.isEmpty())
		{
			Element resources = doc.createElement("Resources");
			Element resource = doc.createElement("Resource");
			for(Iterator<ResourceMatch> it = resourceMatchCollection.iterator(); it.hasNext();)
			{
				ResourceMatch resourceMatch = (ResourceMatch) it.next();
				generateResourceMatch(resource, resourceMatch);
			}
			resources.appendChild(resource);
			tar.appendChild(resources);
		}
		
		Collection<ActionMatch> actionMatchCollection = target.getActionMatch();
		if(actionMatchCollection != null && !actionMatchCollection.isEmpty())
		{
			Element actions = doc.createElement("Actions");
			Element action = doc.createElement("Action");
			for(Iterator<ActionMatch> it = actionMatchCollection.iterator(); it.hasNext();)
			{
				ActionMatch actionMatch = (ActionMatch) it.next();
				generateActionMatch(action, actionMatch);
			}
			actions.appendChild(action);
			tar.appendChild(actions);
		}
		
		Collection<EnvironmentMatch> environmentMatchCollection = target.getEnvironmentMatch();
		if(environmentMatchCollection != null && !environmentMatchCollection.isEmpty())
		{
			Element environments = doc.createElement("Environments");
			Element environment = doc.createElement("Environment");
			for(Iterator<EnvironmentMatch> it = environmentMatchCollection.iterator(); it.hasNext();)
			{
				EnvironmentMatch environmentMatch = (EnvironmentMatch) it.next();
				generateEnvironmentMatch(environment, environmentMatch);
			}
			environments.appendChild(environment);
			tar.appendChild(environments);
		}
		node.appendChild(tar);
	}
	
	
	private void generateSubjectMatch(Element subject, SubjectMatch subjectMatch)
	{
		Element subMatch = doc.createElement("SubjectMatch");
		String matchId = calculateMatchId(subjectMatch.getMatchId().toString().toLowerCase(), subjectMatch.getDataTypeAttributeValue());
		matchId = matchId.replace('_', '-');
		matchId = selectFunctionVersion(matchId);
		subMatch.setAttribute("MatchId", matchId);
		generateAttributeValue(subMatch, subjectMatch.getAttributeValue(), subjectMatch.getDataTypeAttributeValue());
		String selector = subjectMatch.getAttributeSelector();
		if(selector != null && !selector.isEmpty())
			generateAttributeSelector(subMatch, selector, "http://www.w3.org/2001/XMLSchema#string");
		else
			generateSubjectAttributeDesignator(subMatch, subjectMatch.getSubjectAttributeDesignator(), subjectMatch.getDataTypeSubjectDesignator());
		subject.appendChild(subMatch);
	}
	
	private String selectFunctionVersion(String nom) {
		if(nom.startsWith("time-in-range") || nom.startsWith("string-concatenate") || nom.startsWith("anyURI-regexp-match")
				|| nom.startsWith("ipAddress-regexp-match") || nom.startsWith("dnsName-regexp-match") 
				|| nom.startsWith("uri-string-concatenate") || nom.startsWith("rfc822Name-regexp-match")
				|| nom.startsWith("x500Name-regexp-match"))
			nom = function20 + nom;
		else
			nom = function10 + nom;
		return nom;
	}

	//Aquesta funció hauria de controlar el tipus segons el dataType del segon atribut i no passar sempre String!!
	private String calculateMatchId(String matchId,
			DataType dataTypeAttributeValue) {
		String finalMatchId = new String();
		matchId = matchId.substring(4);
		String dataType = dataTypeAttributeValue.getValue();
		if(dataType.equals("S")){
			if(matchId.equals("_match"))
				finalMatchId = "string-regexp";
			else
				finalMatchId = "string";
		}else if(dataType.equals("B")){
			finalMatchId = "boolean";
		}else if(dataType.equals("I")){
			finalMatchId = "integer";
		}else if(dataType.equals("DO")){
			finalMatchId = "double";
		}else if(dataType.equals("DT")){
			finalMatchId = "dateTime";
		}else if(dataType.equals("DATE")){
			finalMatchId = "date";
		}else if(dataType.equals("T")){
			finalMatchId = "time";
		}else if(dataType.equals("H")){
			finalMatchId = "hexBinary";
		}else if(dataType.equals("ANY")){
			if(matchId.equals("_match"))
				finalMatchId = "anyURI-regexp";
			else
				finalMatchId = "anyURI";
		}else if(dataType.equals("YM")){
			finalMatchId = "yearMonthDuration";
		}else if(dataType.equals("D")){
			finalMatchId = "dayTimeDuration";
		}else if(dataType.equals("B64")){
			finalMatchId = "base64Binary";
		}else if(dataType.equals("X")){
			if(matchId.equals("_match"))
				finalMatchId = "x500Name-regexp";
			else
				finalMatchId = "x500Name";
		}else if(dataType.equals("R")){
			if(matchId.equals("_match"))
				finalMatchId = "rfc822Name-regexp";
			else
				finalMatchId = "rfc822Name";
		}
		
		return finalMatchId + matchId;
	}

	private void generateSubjectAttributeDesignator(Element subMatch, String subjectAttributeDesignator, DataType dataType) {
		Element subjectAttribute = doc.createElement("SubjectAttributeDesignator");
		subjectAttribute.setAttribute("AttributeId", subjectAttributeDesignator);
		subjectAttribute.setAttribute("DataType", tipusDada(dataType));
		subMatch.appendChild(subjectAttribute);
	}
	
	
	private void generateAttributeSelector(Element subMatch, String selector, String dataType) {
		Element attributeSelector = doc.createElement("AttributeSelector");
		attributeSelector.setAttribute("RequestContextPath", selector);
		attributeSelector.setAttribute("DataType", dataType);
		subMatch.appendChild(attributeSelector);
	}
	
	
	private void generateAttributeValue(Element match, String attributeValue, DataType dataType) {
		Element attribute = doc.createElement("AttributeValue");
		attribute.setAttribute("DataType", tipusDada(dataType));
		attribute.setTextContent(attributeValue);
		match.appendChild(attribute);
	}
	
	
	private void generateResourceMatch(Element resource, ResourceMatch resourceMatch)
	{
		Element resMatch = doc.createElement("ResourceMatch");
		String matchId = calculateMatchId(resourceMatch.getMatchId().toString().toLowerCase(), resourceMatch.getDataTypeAttributeValue());
		matchId = matchId.replace('_', '-');
		matchId = selectFunctionVersion(matchId);
		resMatch.setAttribute("MatchId", matchId);
		generateAttributeValue(resMatch, resourceMatch.getAttributeValue(), resourceMatch.getDataTypeAttributeValue());
		String selector = resourceMatch.getAttributeSelector();
		if(selector != null && !selector.isEmpty())
			generateAttributeSelector(resMatch, selector, "http://www.w3.org/2001/XMLSchema#string");
		else
			generateResourceAttributeDesignator(resMatch, resourceMatch.getResourceAttributeDesignator(), resourceMatch.getDataTypeResourceDesignator());
		resource.appendChild(resMatch);
	}
	
	
	private void generateResourceAttributeDesignator(Element resMatch, 
			String resourceAttributeDesignator, DataType dataType) {
		Element resourceAttribute = doc.createElement("ResourceAttributeDesignator");
		resourceAttribute.setAttribute("AttributeId", resourceAttributeDesignator);
		resourceAttribute.setAttribute("DataType", tipusDada(dataType));
		resMatch.appendChild(resourceAttribute);
	}
	
	
	private void generateActionMatch(Element action, ActionMatch actionMatch)
	{
		Element actMatch = doc.createElement("ActionMatch");
		String matchId = calculateMatchId(actionMatch.getMatchId().toString().toLowerCase(), actionMatch.getDataTypeAttributeValue());
		matchId = matchId.replace('_', '-');
		matchId = selectFunctionVersion(matchId);
		actMatch.setAttribute("MatchId", matchId);
		generateAttributeValue(actMatch, actionMatch.getAttributeValue(), actionMatch.getDataTypeAttributeValue());
		String selector = actionMatch.getAttributeSelector();
		if(selector != null && !selector.isEmpty())
			generateAttributeSelector(actMatch, selector, "http://www.w3.org/2001/XMLSchema#string");
		else
			generateActionAttributeDesignator(actMatch, actionMatch.getActionAttributeDesignator(), actionMatch.getDataTypeActionDesignator());
		action.appendChild(actMatch);
	}
	
	
	private void generateActionAttributeDesignator(Element actMatch, String actionAttributeDesignator, DataType dataType) {
		Element actionAttribute = doc.createElement("ActionAttributeDesignator");
		actionAttribute.setAttribute("AttributeId", actionAttributeDesignator);
		actionAttribute.setAttribute("DataType", tipusDada(dataType));
		actMatch.appendChild(actionAttribute);
	}
	
	
	private void generateEnvironmentMatch(Element environment, EnvironmentMatch environmentMatch)
	{
		Element envMatch = doc.createElement("EnvironmentMatch");
		String matchId = calculateMatchId(environmentMatch.getMatchId().toString().toLowerCase(), environmentMatch.getDataTypeAttributeValue());
		matchId = matchId.replace('_', '-');
		matchId = selectFunctionVersion(matchId);
		envMatch.setAttribute("MatchId", matchId);
		generateAttributeValue(envMatch, environmentMatch.getAttributeValue(), environmentMatch.getDataTypeAttributeValue());
		String selector = environmentMatch.getAttributeSelector();
		if(selector != null && !selector.isEmpty())
			generateAttributeSelector(envMatch, selector, "http://www.w3.org/2001/XMLSchema#string");
		else
			generateEnvironmentAttributeDesignator(envMatch, environmentMatch.getEnvironmentAttributeDesignator(), environmentMatch.getDataTypeEnvironmentDesignator());
		environment.appendChild(envMatch);
	}
	
	
	private void generateEnvironmentAttributeDesignator(Element envMatch,  
			String environmentAttributeDesignator, DataType dataType) {
		Element environmentAttribute = doc.createElement("EnvironmentAttributeDesignator");
		environmentAttribute.setAttribute("AttributeId", environmentAttributeDesignator);
		environmentAttribute.setAttribute("DataType", tipusDada(dataType));
		envMatch.appendChild(environmentAttribute);	
	}
	
	
	private String formatFunctionId(Expression expression) {
		String nom = new String();
		nom = expression.getName().toString().toLowerCase();
		nom = nom.replace('_', '-');
		if(nom.startsWith("type"))
		{
			nom = nom.substring(4);
			nom = expression.getAttributeDesignator().toLowerCase() + nom;
		}
		if(nom.startsWith("datetime"))
			nom = "dateTime" + nom.substring(8);
		else if (nom.startsWith("dayTimeDuration"))
			nom = "dayTimeDuration" + nom.substring(15);
		else if (nom.startsWith("yearMonthDuration"))
			nom = "yearMonthDuration" + nom.substring(17);
		else if (nom.startsWith("anyuri"))
			nom = "anyURI" + nom.substring(6);
		else if (nom.startsWith("x500name"))
			nom = "x500Name" + nom.substring(8);
		else if (nom.startsWith("rfc822Name"))
			nom = "rfc822Name" + nom.substring(10);
		else if (nom.startsWith("hexbinary"))
			nom = "hexBinary" + nom.substring(9);
		else if (nom.startsWith("base64binary"))
			nom = "base64Binary" + nom.substring(12);
		else if (nom.startsWith("ipaddresss"))
			nom = "ipAddress" + nom.substring(9);
		else if (nom.startsWith("dnsname"))
			nom = "dnsName" + nom.substring(7);
		
		if(nom.startsWith("time-in-range") || nom.startsWith("string-concatenate") || nom.startsWith("anyURI-regexp-match")
				|| nom.startsWith("ipAddress-regexp-match") || nom.startsWith("dnsName-regexp-match") 
				|| nom.startsWith("uri-string-concatenate") || nom.startsWith("rfc822Name-regexp-match")
				|| nom.startsWith("x500Name-regexp-match"))
			nom = function20 + nom;
		else
			nom = function10 + nom;
		
		return nom;
	}
	
	
	private String tipusFunction(String nom) {
		String tipus = new String();
		int i  = nom.indexOf('-');
		if(i>0)
			nom = nom.substring(0, i);
		if(nom.equals("string"))
			tipus = DATATYPESTRING;
		else if (nom.equals("boolean"))
			tipus = DATATYPEBOOLEAN;
		else if (nom.equals("double"))
			tipus = DATATYPEDOUBLE;
		else if (nom.equals("date"))
			tipus = DATATYPEDATE;
		else if (nom.equals("time"))
			tipus = DATATYPETIME;
		else if (nom.equals("datetime"))
			tipus = DATATYPEDATETIME;
		else if (nom.equals("dayTimeDuration"))
			tipus = DATATYPEDAYTIME;
		else if (nom.equals("yearmonthduration"))
			tipus = DATATYPEYEARMONTH;
		else if (nom.equals("anyuri"))
			tipus = DATATYPEANYURI;
		else if (nom.equals("x500name"))
			tipus = DATATYPEX500NAME;
		else if (nom.equals("rfc822name"))
			tipus = DATATYPERFC822NAME;
		else if (nom.equals("hexbinary"))
			tipus = DATATYPEHEXBINARY;
		else if (nom.equals("base64binary"))
			tipus = DATATYPEBASE64BINARY;
		else if (nom.equals("ipaddress"))
			tipus = DATATYPEIPADDRESS;
		else if (nom.equals("dnsname"))
			tipus = DATATYPEDNSNAME;
		else
			tipus = DATATYPESTRING;
		
		return tipus;
	}
	
	private String tipusDada(DataType tipus){
		String torna = new String();
		if (tipus != null)
		{	
			String nom = tipus.getValue();
			if(nom.equals("S"))
				torna = DATATYPESTRING;
			else if (nom.equals("B"))
				torna = DATATYPEBOOLEAN;
			else if (nom.equals("I"))
				torna = DATATYPEINTEGER;
			else if (nom.equals("DO"))
				torna = DATATYPEDOUBLE;
			else if (nom.equals("DATE"))
				torna = DATATYPEDATE;
			else if (nom.equals("T"))
				torna = DATATYPETIME;
			else if (nom.equals("DT"))
				torna = DATATYPEDATETIME;
			else if (nom.equals("D"))
				torna = DATATYPEDAYTIME;
			else if (nom.equals("YM"))
				torna = DATATYPEYEARMONTH;
			else if (nom.equals("ANY"))
				torna = DATATYPEANYURI;
			else if (nom.equals("H"))
				torna = DATATYPEHEXBINARY;
			else if (nom.equals("B"))
				torna = DATATYPEBASE64BINARY;
			else
				torna = DATATYPESTRING;
		}
	
		return torna;
	}

}
