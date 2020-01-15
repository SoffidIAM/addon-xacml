package com.soffid.addons.xacml.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
import com.soffid.iam.addons.xacml.service.PolicySetService;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datasource.DataSource;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.jxpath.Pointer;

public class Duplicator {
		
	public Duplicator()
	{
		super();
	}

	
	public static String duplicatePolicySet(DataSource ds, String sourcePath, String targetPath) throws Exception
	{
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		PolicySet sourcePS = (PolicySet) sourceNode.getInstance();
		PolicySet nova = new PolicySet(sourcePS);
		String versio = calculaVersio(sourcePS.getVersion());
		int number = calculaOrdre(ds, (PolicySet) sourcePS, targetPath);
		nova.setTarget(new LinkedList<Target>());
		nova.setPolicySetIdReference(new LinkedList<PolicySetIdReference>());
		nova.setPolicyIdReference(new LinkedList<PolicyIdReference>());
		nova.setVersion(versio);
		nova.setParentPolicySet(null);
		nova.setOrder(number + 1);
		nova.setId(null);
	
		String newPath = XPathUtils.createPath(ds, targetPath+"/policySet", nova);
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/target");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateTarget (ds, p.asPath(), newPath);
		}
		
		for ( Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/policySet");
				iterator.hasNext(); )
		{
			Pointer p = (Pointer) iterator.next();
			duplicatePolicySet (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/policy");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicatePolicy (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/policySetIdRef");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicatePolicySetIdRef (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/policyIdRef");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicatePolicyIdRef (ds, p.asPath(), newPath);
		}
		
		return newPath;	
	}

	
	private static String duplicatePolicyIdRef(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		PolicyIdReference sourcePIRef = (PolicyIdReference) sourceNode.getInstance();
		PolicyIdReference nova = new PolicyIdReference(sourcePIRef);
		DataNode psNode = (DataNode) ds.getJXPathContext().getValue(targetPath);
		PolicySet pare = (PolicySet) psNode.getInstance();
		int number = calculaOrdre(ds, (PolicySet) pare, targetPath);
		nova.setId(null);
		nova.setOrder(number + 1);
		String newPath = XPathUtils.createPath(ds, targetPath+"/policyIdRef", nova);
		return newPath;
	}

	
	private static String duplicatePolicySetIdRef(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		PolicySetIdReference sourcePSIRef = (PolicySetIdReference) sourceNode.getInstance();
		PolicySetIdReference nova = new PolicySetIdReference(sourcePSIRef);
		DataNode psNode = (DataNode) ds.getJXPathContext().getValue(targetPath);
		PolicySet pare = (PolicySet) psNode.getInstance();
		int number = calculaOrdre(ds, (PolicySet) pare, targetPath);
		nova.setId(null);
		nova.setOrder(number + 1);
		String newPath = XPathUtils.createPath(ds, targetPath+"/policySetIdRef", nova);
		return newPath;
	}

	
	public static String duplicatePolicy(DataSource ds, String sourcePath, String targetPath) throws Exception{
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		Policy sourcePO = (Policy) sourceNode.getInstance();
		Policy nova = new Policy (sourcePO);
		String versio = calculaVersio(sourcePO.getVersion());
		DataNode psNode = (DataNode) ds.getJXPathContext().getValue(targetPath);
		PolicySet pare = (PolicySet) psNode.getInstance();
		int number = calculaOrdre(ds, (PolicySet) pare, targetPath);
		
		nova.setTarget(new LinkedList<Target>());
		nova.setRule(new LinkedList<Rule>());
		nova.setVariableDefinition(new LinkedList<VariableDefinition>());
		
		nova.setVersion(versio);
		nova.setOrder(number + 1);
		nova.setId(null);
		nova.setPolicySetId(pare.getId()); 
		
		String newPath = XPathUtils.createPath(ds, targetPath+"/policy", nova);
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/target");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateTarget (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/rule");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateRule (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/variableDefinition");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateVariableDefinition (ds, p.asPath(), newPath);
		}
		
		return newPath;
	}

	
	private static String duplicateVariableDefinition(DataSource ds,
			String sourcePath, String targetPath) throws Exception{
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		VariableDefinition sourceVD = (VariableDefinition) sourceNode.getInstance();
		Expression acopiar = (Expression) sourceVD.getExpression();
		Expression copiada = new Expression();
		VariableDefinition nova = new VariableDefinition (sourceVD);
		nova.setId(null);
		nova.setExpression(null);
		
		duplicateExpression (acopiar, copiada);
		nova.setExpression(copiada);
		
		String newPath = XPathUtils.createPath(ds, targetPath+"/variableDefinition", nova);
		
		return newPath;
	}


	private static void duplicateExpression(Expression source, Expression target) throws Exception {
		
		Collection<Expression> colExp = source.getSubexpression();
		Integer i = colExp.size();
	
		target.setAttributeDesignator(source.getAttributeDesignator());
		target.setAttributeSelector(source.getAttributeSelector());
		target.setAttributeValue(source.getAttributeValue());
		target.setExpressionType(source.getExpressionType());
		target.setName(source.getName());
		target.setOrder(i + 1);
		target.setVariableId(source.getVariableId());
		for(Expression acopiar : colExp)
		{
			Expression copiada = new Expression();
			duplicateExpression(acopiar, copiada);
		}
	}


	private static String  duplicateRule(DataSource ds, String sourcePath,
			String targetPath) throws Exception{
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		Rule sourceR = (Rule) sourceNode.getInstance();
		Rule nova = new Rule (sourceR);
		nova.setId(null);
		nova.setTarget(new LinkedList<Target>());
		nova.setCondicion(new LinkedList<Condition>());
		
		String newPath = XPathUtils.createPath(ds, targetPath+"/rule", nova);
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/target");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateTarget (ds, p.asPath(), newPath);
		}
		
		for (Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/condicion");
				iterator.hasNext();)
		{
			Pointer p = (Pointer) iterator.next();
			duplicateCondicion (ds, p.asPath(), newPath);
		}
		
		
		return newPath;
	}


	private static String duplicateCondicion(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		Condition sourceC = (Condition) sourceNode.getInstance();
		Expression acopiar = (Expression) sourceC.getExpression();
		Expression copiada = new Expression();
		Condition nova = new Condition (sourceC);
		nova.setExpression(null);
		nova.setId(null);
		
		duplicateExpression (acopiar, copiada);
		nova.setExpression(copiada);
		
		String newPath = XPathUtils.createPath(ds, targetPath+"/condicion", nova);
		
		return newPath;
	}


	public static String duplicateTarget (DataSource ds, String sourcePath, String targetPath) throws Exception
	{
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		Target sourceTarget = (Target) sourceNode.getInstance();
		Target t = new Target(sourceTarget);
		t.setId(null);
		t.setActionMatch(new LinkedList<ActionMatch>());
		t.setEnvironmentMatch(new LinkedList<EnvironmentMatch>());
		t.setResourceMatch(new LinkedList<ResourceMatch>());
		t.setSubjectMatch(new LinkedList<SubjectMatch>());
		
		String newPath = XPathUtils.createPath(ds, targetPath+"/target", t);
		
		for(Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/subjectMatch");
				iterator.hasNext();)
			{
				Pointer p = (Pointer) iterator.next();
				duplicateSubjectMatch(ds, p.asPath(), newPath);
			}
		
		for(Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/resourceMatch");
				iterator.hasNext();)
			{
				Pointer p = (Pointer) iterator.next();
				duplicateResourceMatch(ds, p.asPath(), newPath);
			}
		
		for(Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/actionMatch");
				iterator.hasNext();)
			{
				Pointer p = (Pointer) iterator.next();
				duplicateActionMatch(ds, p.asPath(), newPath);
			}
		
		for(Iterator iterator = ds.getJXPathContext().iteratePointers(sourcePath+"/environmentMatch");
				iterator.hasNext();)
			{
				Pointer p = (Pointer) iterator.next();
				duplicateEnvironmentMatch(ds, p.asPath(), newPath);
			}
		
		return newPath;
	}

	
	private static String duplicateEnvironmentMatch(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		EnvironmentMatch sourceEnv = (EnvironmentMatch) sourceNode.getInstance();
		EnvironmentMatch nova = new EnvironmentMatch(sourceEnv);
		nova.setId(null);
		String newPath = XPathUtils.createPath(ds, targetPath+"/environmentMatch", nova);
		return newPath;
	}

	
	private static String duplicateActionMatch(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		ActionMatch sourceAct = (ActionMatch) sourceNode.getInstance();
		ActionMatch nova = new ActionMatch(sourceAct);
		nova.setId(null);
		String newPath = XPathUtils.createPath(ds, targetPath+"/actionMatch", nova);
		return newPath;
	}

	
	private static String duplicateResourceMatch(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		ResourceMatch sourceRes = (ResourceMatch) sourceNode.getInstance();
		ResourceMatch nova = new ResourceMatch(sourceRes);
		nova.setId(null);
		String newPath = XPathUtils.createPath(ds, targetPath+"/resourceMatch", nova);
		return newPath;
	}

	
	private static String duplicateSubjectMatch(DataSource ds, String sourcePath,
			String targetPath) throws Exception {
		DataNode sourceNode = (DataNode) ds.getJXPathContext().getValue(sourcePath);
		SubjectMatch sourceSub = (SubjectMatch) sourceNode.getInstance();
		SubjectMatch nova = new SubjectMatch(sourceSub);
		nova.setId(null);
		String newPath = XPathUtils.createPath(ds, targetPath+"/subjectMatch", nova);
		return newPath;		
	}
	
	
	private static String calculaVersio(String vella) throws NumberFormatException
	{
		try
		{
			int i = vella.lastIndexOf(".");
			String nova = new String();
			if(i > 0)
			{
				nova = vella.substring(i + 1);
				vella = vella.substring(0, i + 1);
				Integer n = java.lang.Integer.parseInt(nova);
				n = n + 1;
				return vella + n;
			}
			else
			{
				Integer n = java.lang.Integer.parseInt(vella);
				n ++;
				return n.toString();
			}
		}
		catch(NumberFormatException e)
		{
			return vella + " + 1";
		}
	}
	
	
	private static Integer calculaOrdre(DataSource ds, PolicySet actual, String sourcePath)
	{
		int number = 0;
		
		Iterator it = ds.getJXPathContext().iteratePointers(sourcePath+"/policySet");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		it = ds.getJXPathContext().iteratePointers(sourcePath+"/policy");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		it = ds.getJXPathContext().iteratePointers(sourcePath+"/policySetIdRef");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		it = ds.getJXPathContext().iteratePointers(sourcePath+"/policyIdRef");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		return number;
	}
		
}
