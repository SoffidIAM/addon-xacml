package com.soffid.iam.addons.xacml.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

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

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.exception.InternalErrorException;

public class ImportData {
	
	PolicySetService policySetService;
	private int order=0;
	
	final static String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
	final static String POLICY_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:policy:schema:os"; //$NON-NLS-1$
	final static String CONTEXT_NAMESPACE = "urn:oasis:names:tc:xacml:2.0:context:schema:os"; //$NON-NLS-1$
	final static String DATATYPEDATE = "http://www.w3.org/2001/XMLSchema#date"; //$NON-NLS-1$
	final static String DATATYPETIME = "http://www.w3.org/2001/XMLSchema#time"; //$NON-NLS-1$
	final static String DATATYPEDATETIME = "http://www.w3.org/2001/XMLSchema#dateTime"; //$NON-NLS-1$
	final static String DATATYPESTRING = "http://www.w3.org/2001/XMLSchema#string"; //$NON-NLS-1$
	final static String DATATYPEINTEGER = "http://www.w3.org/2001/XMLSchema#integer"; //$NON-NLS-1$
	final static String DATATYPEANYURI = "http://www.w3.org/2001/XMLSchema#anyURI"; //$NON-NLS-1$
	final static String DATATYPEBOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean"; //$NON-NLS-1$
	final static String DATATYPEDOUBLE = "http://www.w3.org/2001/XMLSchema#double"; //$NON-NLS-1$
	final static String DATATYPEHEXBINARY = "http://www.w3.org/2001/XMLSchema#hexBinary";  //$NON-NLS-1$
	final static String DATATYPEBASE64BINARY = "http://www.w3.org/2001/XMLSchema#base64Binary"; //$NON-NLS-1$
	final static String DATATYPEYEARMONTH = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration"; //$NON-NLS-1$
	final static String DATATYPEDAYTIME = "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration"; //$NON-NLS-1$
	final static String DATATYPEIPADDRESS = "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress"; //$NON-NLS-1$
	final static String DATATYPEDNSNAME= "urn:oasis:names:tc:xacml:2.0:data-type:dnsName"; //$NON-NLS-1$
	final static String DATATYPERFC822NAME = "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name"; //$NON-NLS-1$
	final static String DATATYPEX500NAME = "urn:oasis:names:tc:xacml:1.0:data-type:x500Name"; //$NON-NLS-1$
	 
	
	public ImportData(PolicySetService svc){
		super();
		policySetService = svc;
	}
	
	
	public void importXACML(InputStream in) throws InterruptedException, IOException, Exception{
		
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document document = reader.read (in);
			in.close();
			Element rootNode = document.getRootElement();
			importPolicySet(rootNode, null);
	}

	
	private void importPolicySet(Element rootNode, Long parent) throws InternalErrorException{
		Attribute policySetId = rootNode.attribute("PolicySetId"); //$NON-NLS-1$
		if(policySetId == null)
			throw new InternalErrorException(Messages.getString("ImportData.NoWellForm"));
		Attribute policyCombiningAlgId = rootNode.attribute("PolicyCombiningAlgId"); //$NON-NLS-1$
		Attribute version = rootNode.attribute("Version"); //$NON-NLS-1$
		
		PolicySetCriteria criteria = new PolicySetCriteria();
		if(version != null && version.getValue()!= null)
			criteria.setVersion(version.getValue());
		criteria.setPolicySetId(policySetId.getValue());
		
		Collection<PolicySet> polSetCollection = policySetService.findPolicySetByCriteria(criteria);
		if(polSetCollection != null && !polSetCollection.isEmpty())
			throw new InternalErrorException(String.format(Messages.getString("ImportData.PolicySetDuplicated"), 
					policySetId.getValue()));
		
		PolicySet newPolicySet = new PolicySet();
		if(version != null)
			newPolicySet.setVersion(version.getValue());
		else
			newPolicySet.setVersion("1"); //$NON-NLS-1$
		newPolicySet.setPolicyCombiningAlgId(formatPolicyCombiningAlg(policyCombiningAlgId));
		if(policySetId.getValue() != null)
			newPolicySet.setPolicySetId(policySetId.getValue());
		else
			throw new InternalErrorException(Messages.getString("ImportData.PolicySetNotCreated")); //$NON-NLS-1$
		if(parent != null){
			newPolicySet.setParentPolicySet(parent);
			order = order + 1;
			newPolicySet.setOrder(order);
		}
		else 
			newPolicySet.setOrder(maxPolicySet() + 1);
		PolicySet create = new PolicySet();
		
		try {
			create = policySetService.create(newPolicySet);
		} catch (InternalErrorException e) {
			throw new InternalErrorException(String.format(Messages.getString("ImportData.ErrorCreatingPolicySet"),new Object [] {e.getMessage()}));  
		}
		
		Element description = rootNode.element("Description"); //$NON-NLS-1$
		if(description != null){
			create.setDescription(description.getText());
		}
		
		List<Element> listPolicySet = rootNode.elements("PolicySet"); //$NON-NLS-1$
		if(listPolicySet != null){
			for(Element element: listPolicySet){
				importPolicySet(element, create.getId());
			}
		}
		
		List<Element> listPolicy = rootNode.elements("Policy"); //$NON-NLS-1$
		if(listPolicy != null){
			for(Element element: listPolicy){
				importPolicy(element, create.getId());
			}
		}
		
		Element target = rootNode.element("Target"); //$NON-NLS-1$
		if(target != null){
			importTarget(target, create.getTarget());
		}
		
		List<Element> listPolicySetIdReference = rootNode.elements("PolicySetIdReference"); //$NON-NLS-1$
		if(listPolicySetIdReference != null){
			for(Element element: listPolicySetIdReference){
				PolicySetIdReference polIdRef = importPolicySetIdReference(element);
				create.getPolicySetIdReference().add(polIdRef);
			}
		}
		
		List<Element> listPolicyIdReference = rootNode.elements("PolicyIdReference"); //$NON-NLS-1$
		if(listPolicyIdReference != null){
			for(Element element: listPolicyIdReference){
				PolicyIdReference polIdRef = importPolicyIdReference(element);
				create.getPolicyIdReference().add(polIdRef);
			}
		}
		create = policySetService.update(create);
	}

	
	private PolicyIdReference importPolicyIdReference(Element element) throws InternalErrorException {
		order = order + 1;
		String idReferenceTypeValue = element.getText();
		
		Attribute latest = element.attribute("LatestVersion"); //$NON-NLS-1$
		Attribute earliest = element.attribute("EarliestVersion"); //$NON-NLS-1$
		Attribute version = element.attribute("Version"); //$NON-NLS-1$
		
		PolicyIdReference polIdRef = new PolicyIdReference();
		if(idReferenceTypeValue != null && !idReferenceTypeValue.isEmpty())
			polIdRef.setIdReferenceTypeValue(idReferenceTypeValue);
		else
			throw new InternalErrorException(Messages.getString("ImportData.PolicyIdReferenceError")); //$NON-NLS-1$
		polIdRef.setOrder(order);
		if(version != null)
			polIdRef.setVersion(version.getValue());
		if(latest != null)
			polIdRef.setLatestVersion(latest.getValue());
		if(earliest != null)
			polIdRef.setEarliestVersion(earliest.getValue());
		return polIdRef;
	}

	
	private PolicySetIdReference importPolicySetIdReference(Element element) throws InternalErrorException {
		order = order + 1;
		String idReferenceTypeValue = element.getText();
		
		Attribute latest = element.attribute("LatestVersion"); //$NON-NLS-1$
		Attribute earliest = element.attribute("EarliestVersion"); //$NON-NLS-1$
		Attribute version = element.attribute("Version"); //$NON-NLS-1$
		
		PolicySetIdReference polIdRef = new PolicySetIdReference();
		if(idReferenceTypeValue != null && !idReferenceTypeValue.isEmpty())
			polIdRef.setIdReferenceTypeValue(idReferenceTypeValue);
		else
			throw new InternalErrorException(Messages.getString("ImportData.PolicySetIdReferenceError")); //$NON-NLS-1$
		polIdRef.setOrder(order);
		if(version != null)
			polIdRef.setVersion(version.getValue());
		if(latest != null)
			polIdRef.setLatestVersion(latest.getValue());
		if(earliest != null)
			polIdRef.setEarliestVersion(earliest.getValue());
		return polIdRef;
	}

	
	private Target importTarget(Element target, Collection<Target> targetCol) throws InternalErrorException {
		Target targetVO;
		if(targetCol.size()>0)
			targetVO = targetCol.iterator().next();
		else{
			targetVO = new Target();
			targetVO.setActionMatch(new HashSet<ActionMatch>());
			targetVO.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
			targetVO.setResourceMatch(new HashSet<ResourceMatch>());
			targetVO.setSubjectMatch(new HashSet<SubjectMatch>());
		}
		Element subjects = target.element("Subjects"); //$NON-NLS-1$
		if(subjects != null){
			List<Element> listSubject = subjects.elements("Subject"); //$NON-NLS-1$
			for(Element subject: listSubject){
				List<Element> listSubjectMatch = subject.elements("SubjectMatch"); //$NON-NLS-1$
				for(Element subjectMatch: listSubjectMatch){
					SubjectMatch subjectMatchVO = importSubjectMatch(subjectMatch);
					targetVO.getSubjectMatch().add(subjectMatchVO);
				}
			}
		}
		
		Element resources = target.element("Resources"); //$NON-NLS-1$
		if(resources != null){
			List<Element> listResource = resources.elements("Resource"); //$NON-NLS-1$
			for(Element resource: listResource){
				List<Element> listResourceMatch = resource.elements("ResourceMatch"); //$NON-NLS-1$
				Collection<ResourceMatch> resourceMatchCol = targetVO.getResourceMatch();
				for(Element resourceMatch: listResourceMatch){
					ResourceMatch resourceMatchVO = importResourceMatch(resourceMatch);
					targetVO.getResourceMatch().add(resourceMatchVO);
				}
			}
		}
		
		Element actions = target.element("Actions"); //$NON-NLS-1$
		if(actions != null){
			List<Element> listAction = actions.elements("Action"); //$NON-NLS-1$
			for(Element action: listAction){
				List<Element> listActionMatch = action.elements("ActionMatch"); //$NON-NLS-1$
				for(Element actionMatch: listActionMatch){
					ActionMatch actionMatchVO = importActionMatch(actionMatch);
					targetVO.getActionMatch().add(actionMatchVO);
				}
			}
		}
		
		Element environments = target.element("Environments"); //$NON-NLS-1$
		if(environments != null){
			List<Element> listEnvironment = environments.elements("Environment"); //$NON-NLS-1$
			for(Element environment: listEnvironment){
				List<Element> listEnvironmentMatch = environment.elements("EnvironmentMatch"); //$NON-NLS-1$
				for(Element environmentMatch: listEnvironmentMatch){
					EnvironmentMatch environmentMatchVO = importEnvironmentMatch(environmentMatch);
					targetVO.getEnvironmentMatch().add(environmentMatchVO);
				}
			}
		}
		return targetVO;
	}

	
	private EnvironmentMatch importEnvironmentMatch(Element environmentMatch) throws InternalErrorException {
		EnvironmentMatch environmentMatchVO = new EnvironmentMatch();
		
		Attribute matchId = environmentMatch.attribute("MatchId"); //$NON-NLS-1$
		environmentMatchVO.setMatchId(MatchIdEnumeration.fromString(convert(matchId.getValue())));
		
		Element attributeValue = environmentMatch.element("AttributeValue"); //$NON-NLS-1$
		importAttributeValue(attributeValue, environmentMatchVO);
		
		Element attributeSelector = environmentMatch.element("AttributeSelector"); //$NON-NLS-1$
		Element environmentAttributeDesignator = environmentMatch.element("EnvironmentAttributeDesignator"); //$NON-NLS-1$
		if(attributeSelector != null)
			importAttributeSelector(attributeSelector, environmentMatchVO);
		else
			importEnvironmentAttributeDesignator(environmentAttributeDesignator, environmentMatchVO);
		
		return environmentMatchVO;
	}


	private void importAttributeValue(Element attributeValue,
			EnvironmentMatch environmentMatchVO) throws InternalErrorException {
		Attribute dataType = attributeValue.attribute("DataType"); //$NON-NLS-1$
		String attribute = attributeValue.getText();
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			environmentMatchVO.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeeded")); //$NON-NLS-1$
		environmentMatchVO.setAttributeValue(attribute);
	}


	private void importEnvironmentAttributeDesignator(
			Element environmentAttributeDesignator,
			EnvironmentMatch environmentMatchVO) throws InternalErrorException {
		boolean found = false;
		Attribute attributeId = environmentAttributeDesignator.attribute("AttributeId"); //$NON-NLS-1$
		Attribute dataType = environmentAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String[] possibilities = { //$NON-NLS-1$
				"urn:com:soffid:xacml:environment:country", "urn:oasis:names:tc:xacml:1.0:environment:current-time", 
				"urn:oasis:names:tc:xacml:1.0:environment:current-date", 
				"urn:oasis:names:tc:xacml:1.0:environment:current-dateTime"}; //$NON-NLS-1$ //$NON-NLS-2$
		for(int i = 0; i < possibilities.length; i++){
			if(possibilities[i].equals(attributeId.getValue())){
				found = true;
				break;
			}
		}
		if(found){
			String dataTypeValue = dataType.getValue();
			if(dataTypeValue != null && !dataTypeValue.isEmpty())
				environmentMatchVO.setDataTypeEnvironmentDesignator(dataTypeConvert(dataTypeValue));
			else
				throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeededEnv")); //$NON-NLS-1$
			environmentMatchVO.setEnvironmentAttributeDesignator(attributeId.getValue());
		}else
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoEnvironment"),new Object [] {attributeId.getValue()})); 
	}


	private void importSubjectAttributeDesignator(
			Element subjectAttributeDesignator,
			SubjectMatch subjectMatchVO) throws InternalErrorException {
		boolean found = false;
		Attribute attributeId = subjectAttributeDesignator.attribute("AttributeId"); //$NON-NLS-1$
		Attribute dataType = subjectAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String[] possibilities = {"urn:oasis:names:tc:xacml:1.0:subject:subject-id",  //$NON-NLS-1$
				"urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier",   //$NON-NLS-1$ //$NON-NLS-2$
				"urn:com:soffid:xacml:subject:group", 
				"urn:com:soffid:xacml:subject:primaryGroup",
				"urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name", 
				"urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address", 
				"urn:com:soffid:xacml:subject:user", 
				"urn:oasis:names:tc:xacml:2.0:subject:role",
				"urn:com:soffid:oidc:aud",
				"urn:com:soffid:host:os", 
				"urn:com:soffid:host:dhcp"}; //$NON-NLS-1$ //$NON-NLS-2$
		for(int i = 0; i < possibilities.length; i++){
			if(possibilities[i].equals(attributeId.getValue())){
				found = true;
				break;
			}
		}
		if(found){
			String dataTypeValue = dataType.getValue();
			if(dataTypeValue != null && !dataTypeValue.isEmpty())
				subjectMatchVO.setDataTypeSubjectDesignator(dataTypeConvert(dataTypeValue));
			else
				throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeededSub")); //$NON-NLS-1$
			subjectMatchVO.setSubjectAttributeDesignator(attributeId.getValue());
		}else
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoSubject"), new Object[] {attributeId.getValue()}));
	}


	private void importAttributeSelector(Element attributeSelector,
			EnvironmentMatch environmentMatchVO) throws InternalErrorException{
		Attribute dataType = attributeSelector.attribute("DataType"); //$NON-NLS-1$
		Attribute requestContextPath = attributeSelector.attribute("RequestContextPath"); //$NON-NLS-1$
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			environmentMatchVO.setDataTypeEnvironmentDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeedeSelector")); //$NON-NLS-1$
		environmentMatchVO.setAttributeSelector(requestContextPath.getValue());
	}


	private void importAttributeValue(Element attributeValue,
			ActionMatch actionMatchVO) throws InternalErrorException{
		Attribute dataType = attributeValue.attribute("DataType"); //$NON-NLS-1$
		String attribute = attributeValue.getText();
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			actionMatchVO.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeededValue")); //$NON-NLS-1$
		actionMatchVO.setAttributeValue(attribute);
	}


	private ActionMatch importActionMatch(Element actionMatch) throws InternalErrorException{
		ActionMatch actionMatchVO = new ActionMatch();
		
		Attribute matchId = actionMatch.attribute("MatchId"); //$NON-NLS-1$
		actionMatchVO.setMatchId(MatchIdEnumeration.fromString(convert(matchId.getValue())));
		
		Element attributeValue = actionMatch.element("AttributeValue"); //$NON-NLS-1$
		importAttributeValue(attributeValue, actionMatchVO);
		
		Element attributeSelector = actionMatch.element("AttributeSelector"); //$NON-NLS-1$
		Element actionAttributeDesignator = actionMatch.element("ActionAttributeDesignator"); //$NON-NLS-1$
		if(attributeSelector != null)
			importAttributeSelector(attributeSelector, actionMatchVO);
		else
			importActionAttributeDesignator(actionAttributeDesignator, actionMatchVO);
		
		return actionMatchVO;
	}


	private void importActionAttributeDesignator(
			Element actionAttributeDesignator, ActionMatch actionMatchVO) throws InternalErrorException{
		Attribute attributeId = actionAttributeDesignator.attribute("AttributeId"); //$NON-NLS-1$
		Attribute dataType = actionAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		
		if(!attributeId.getValue().equals("urn:com:soffid:xacml:action:method")) //$NON-NLS-1$
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoAction"), 
					new Object[] {attributeId.getValue()}));
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			actionMatchVO.setDataTypeActionDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeed")); //$NON-NLS-1$
		actionMatchVO.setActionAttributeDesignator("urn:com:soffid:xacml:action:method");	 //$NON-NLS-1$
	}


	private void importAttributeSelector(Element attributeSelector,
			ActionMatch actionMatchVO) throws InternalErrorException{
		Attribute dataType = attributeSelector.attribute("DataType"); //$NON-NLS-1$
		Attribute requestContextPath = attributeSelector.attribute("RequestContextPath"); //$NON-NLS-1$
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			actionMatchVO.setDataTypeActionDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeed22")); //$NON-NLS-1$
		actionMatchVO.setAttributeSelector(requestContextPath.getValue());
	}


	private ResourceMatch importResourceMatch(Element resourceMatch) throws InternalErrorException{
		ResourceMatch resourceMatchVO = new ResourceMatch();
		
		Attribute matchId = resourceMatch.attribute("MatchId"); //$NON-NLS-1$
		resourceMatchVO.setMatchId(MatchIdEnumeration.fromString(convert(matchId.getValue())));
		
		Element attributeValue = resourceMatch.element("AttributeValue"); //$NON-NLS-1$
		importAttributeValue(attributeValue, resourceMatchVO);
		
		Element attributeSelector = resourceMatch.element("AttributeSelector"); //$NON-NLS-1$
		Element resourceAttributeDesignator = resourceMatch.element("ResourceAttributeDesignator"); //$NON-NLS-1$
		if(attributeSelector != null)
			importAttributeSelector(attributeSelector, resourceMatchVO);
		else
			importResourceAttributeDesignator(resourceAttributeDesignator, resourceMatchVO);
		
		return resourceMatchVO;
	}


	private void importResourceAttributeDesignator(
			Element resourceAttributeDesignator, ResourceMatch resourceMatchVO) throws InternalErrorException{
		Attribute attributeId = resourceAttributeDesignator.attribute("AttributeId"); //$NON-NLS-1$
		Attribute dataType = resourceAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		
		String[] possibilities = {"urn:oasis:names:tc:xacml:1.0:resource:resource-location", 
				"urn:com:soffid:xacml:subject:account",   //$NON-NLS-1$ //$NON-NLS-2$
				"urn:com:soffid:xacml:subject:system",
				"urn:com:soffid:xacml:resource:vault",
				"urn:com:soffid:xacml:resource:login",
				"urn:com:soffid:xacml:resource:access-level",
				"urn:oasis:names:tc:xacml:2.0:subject:role",
				"urn:com:soffid:xacml:subject:groups",
				"urn:com:soffid:xacml:subject:primaryGroup",
				"urn:com:soffid:xacml:resource:server-url",
				"com:soffid:iam:xacml:1.0:resource:soffid-object"}; 
		boolean found = false;
		for(int i = 0; i < possibilities.length; i++){
			if(possibilities[i].equals(attributeId.getValue())){
				found = true;
				break;
			}
		}
		if (attributeId.getValue().startsWith("urn:com:soffid:xacml:subject:user:att:"))
			found = true;
		if(!found) //$NON-NLS-1$
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoResource"), 
					new Object[] {attributeId.getValue()}));
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			resourceMatchVO.setDataTypeResourceDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeed23")); //$NON-NLS-1$
		resourceMatchVO.setResourceAttributeDesignator(attributeId.getValue());	 //$NON-NLS-1$
	}


	private void importAttributeSelector(Element attributeSelector,
			ResourceMatch resourceMatchVO) throws InternalErrorException {
		Attribute dataType = attributeSelector.attribute("DataType"); //$NON-NLS-1$
		Attribute requestContextPath = attributeSelector.attribute("RequestContextPath"); //$NON-NLS-1$
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			resourceMatchVO.setDataTypeResourceDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeed24")); //$NON-NLS-1$
		resourceMatchVO.setAttributeSelector(requestContextPath.getValue());
	}


	private void importAttributeValue(Element attributeValue,
			ResourceMatch resourceMatchVO) throws InternalErrorException{
		Attribute dataType = attributeValue.attribute("DataType"); //$NON-NLS-1$
		String attribute = attributeValue.getText();
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			resourceMatchVO.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeed30")); //$NON-NLS-1$
		resourceMatchVO.setAttributeValue(attribute);
	}


	private SubjectMatch importSubjectMatch(Element subjectMatch) throws InternalErrorException{
		SubjectMatch subjectMatchVO = new SubjectMatch();
		
		Attribute matchId = subjectMatch.attribute("MatchId"); //$NON-NLS-1$
		subjectMatchVO.setMatchId(MatchIdEnumeration.fromString(convert(matchId.getValue())));
		
		Element attributeValue = subjectMatch.element("AttributeValue"); //$NON-NLS-1$
		importAttributeValue(attributeValue, subjectMatchVO);
		
		Element attributeSelector = subjectMatch.element("AttributeSelector"); //$NON-NLS-1$
		Element subjectAttributeDesignator = subjectMatch.element("SubjectAttributeDesignator"); //$NON-NLS-1$
		if(attributeSelector != null)
			importAttributeSelector(attributeSelector, subjectMatchVO);
		else
			importSubjectAttributeDesignator(subjectAttributeDesignator, subjectMatchVO);
		
		return subjectMatchVO;
	}



	private void importAttributeSelector(Element attributeSelector,
			SubjectMatch subjectMatchVO) throws InternalErrorException{
		Attribute dataType = attributeSelector.attribute("DataType"); //$NON-NLS-1$
		Attribute requestContextPath = attributeSelector.attribute("RequestContextPath"); //$NON-NLS-1$
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			subjectMatchVO.setDataTypeSubjectDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeeded27")); //$NON-NLS-1$
		if(requestContextPath != null)
			subjectMatchVO.setAttributeSelector(requestContextPath.getValue());
		else
			throw new InternalErrorException(Messages.getString("ImportData.RequestContextPath1")); //$NON-NLS-1$
	}


	private void importAttributeValue(Element attributeValue,
			SubjectMatch subjectMatchVO) throws InternalErrorException {
		Attribute dataType = attributeValue.attribute("DataType"); //$NON-NLS-1$
		String attribute = attributeValue.getText();
		
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			subjectMatchVO.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeNeeded29")); //$NON-NLS-1$
		if(attribute != null && !attribute.isEmpty())
			subjectMatchVO.setAttributeValue(attribute);
		else
			throw new InternalErrorException(Messages.getString("ImportData.ValueNeededAttributeValue")); //$NON-NLS-1$
	}


	private void importPolicy(Element polElement, Long id) throws InternalErrorException{
		order = order + 1;
		Policy policy = new Policy();
		
		policy.setPolicySetId(id);
		String policyId = polElement.attributeValue("PolicyId"); //$NON-NLS-1$
		if(policyId != null && !policyId.isEmpty())
			policy.setPolicyId(policyId);
		else
			throw new InternalErrorException(Messages.getString("ImportData.PoliciIdNeeded")); //$NON-NLS-1$
		policy.setRuleCombiningAlgId(formatPolicyCombiningAlg(polElement.attribute("RuleCombiningAlgId"))); //$NON-NLS-1$
		if(polElement.attributeValue("Version") != null) //$NON-NLS-1$
			policy.setVersion(polElement.attributeValue("Version")); //$NON-NLS-1$
		Element description = polElement.element("Description"); //$NON-NLS-1$
		if(description != null){
			policy.setDescription(description.getText());
		}
		policy.setOrder(order);
		
		policy = policySetService.create(policy);
		
		Element target = polElement.element("Target"); //$NON-NLS-1$
		if(target != null){
			importTarget(target, policy.getTarget());
		}
		
		List<Element> variableDefinitionList = polElement.elements("VariableDefinition"); //$NON-NLS-1$
		if(variableDefinitionList != null){
			for(Element element: variableDefinitionList){
				VariableDefinition varDef = importVariableDefinition(element);
				policy.getVariableDefinition().add(varDef);
			}
		}
		
		List<Element> ruleList = polElement.elements("Rule"); //$NON-NLS-1$
		if(ruleList != null){
			int i = 0;
			for(Element element: ruleList){
				i++;
				Rule rule = importRule(element, policy);
				rule.setOrder(i);
				policy.getRule().add(rule);
			}
		}
		policy = policySetService.update(policy);
	}

	
	private Rule importRule(Element ruleElement, Policy policy) throws InternalErrorException{
		Rule rule = new Rule();
		rule.setCondition(new HashSet<Condition>());
		rule.setPolicyId(policy.getId());
		String ruleId = ruleElement.attributeValue("RuleId"); //$NON-NLS-1$
		if(ruleId != null && !ruleId.isEmpty())
			rule.setRuleId(ruleId);
		else
			throw new InternalErrorException(Messages.getString("ImportData.RuleIdNeeded")); //$NON-NLS-1$
		try{
			rule.setEffectType(EffectTypeEnumeration.fromString(ruleElement.attributeValue("Effect").toUpperCase())); //$NON-NLS-1$
		}catch(IllegalArgumentException e){
			throw new InternalErrorException(Messages.getString("ImportData.EffectNotAllowed")); //$NON-NLS-1$
		}
		
		Element description = ruleElement.element("Description"); //$NON-NLS-1$
		if(description != null){
			rule.setDescription(description.getText());
		}
		
		Element target = ruleElement.element("Target"); //$NON-NLS-1$
		if(target != null){
			Collection<Target> targetCol = new HashSet<Target>();
			Target targetVO = importTarget(target, targetCol);
			targetCol.add(targetVO);
			rule.setTarget(targetCol);
		}
		
		List<Element> conditionList = ruleElement.elements("Condition"); //$NON-NLS-1$
		int i = 0;
		if(conditionList != null){
			for(Element element: conditionList){
				i++;
				Condition condition = importCondition(element);
				condition.setConditionId("Condition" + i); //$NON-NLS-1$
				rule.getCondition().add(condition);
			}
		}
		
		return rule;
	}


	private Condition importCondition(Element conditionElement) throws InternalErrorException{
		Condition condition = new Condition();
		Expression expression = importExpression(conditionElement, 1, ""); //$NON-NLS-1$
		condition.setExpression(expression);
		return condition;
	}


	private Expression importExpression(Element element, int i, String elementName) throws InternalErrorException{
		
		Expression expression = new Expression();
		expression.setSubexpression(new HashSet<Expression>());
		
		Element apply = null;
		Element attributeSelector = null;
		Element attributeValue = null;
		Element subjectAttributeDesignator = null;
		Element resourceAttributeDesignator = null;
		Element actionAttributeDesignator = null;
		Element environmentAttributeDesignator = null;
		Element variableReference = null;
		Attribute function = null;
		
		if(elementName.isEmpty()){
			apply = element.element("Apply"); //$NON-NLS-1$
			attributeSelector = element.element("AttributeSelector"); //$NON-NLS-1$
			attributeValue = element.element("AttributeValue"); //$NON-NLS-1$
			subjectAttributeDesignator = element.element("SubjectAttributeDesignator"); //$NON-NLS-1$
			resourceAttributeDesignator = element.element("ResourceAttributeDesignator"); //$NON-NLS-1$
			actionAttributeDesignator = element.element("ActionAttributeDesignator"); //$NON-NLS-1$
			environmentAttributeDesignator = element.element("EnvironmentAttributeDesignator"); //$NON-NLS-1$
			variableReference = element.element("VariableReference"); //$NON-NLS-1$
			function = element.attribute("Function"); //$NON-NLS-1$
			
			if(apply != null)
			{
				importApply(apply, expression, i);
			}
			else if(attributeSelector != null)
			{
				importAttributeSelector(attributeSelector, expression, i);
			}
			else if(attributeValue != null)
			{
				importAttributeValue(attributeValue, expression, i);
			}
			else if(subjectAttributeDesignator != null)
			{
				importSubjectAttributeDesignator(subjectAttributeDesignator, expression, i);
			}
			else if (resourceAttributeDesignator != null)
			{
				importResourceAttributeDesignator(resourceAttributeDesignator, expression, i);
			}
			else if (actionAttributeDesignator != null)
			{
				importActionAttributeDesignator(actionAttributeDesignator, expression, i);
			}
			else if (environmentAttributeDesignator != null)
			{
				importEnvironmentAttributeDesignator(environmentAttributeDesignator, expression, i);
			}
			else if (variableReference != null)
			{
				importVariableReference(variableReference, expression, i);
			}
			else if (function != null)
			{
				importFunction(function, expression, i);
			}
		}else{
			if(elementName.equals("Apply")) //$NON-NLS-1$
			{
				importApply(element, expression, i);
			}
			else if(elementName.equals("AttributeSelector")) //$NON-NLS-1$
			{
				importAttributeSelector(element, expression, i);
			}
			else if(elementName.equals("AttributeValue")) //$NON-NLS-1$
			{
				importAttributeValue(element, expression, i);
			}
			else if(elementName.equals("SubjectAttributeDesignator")) //$NON-NLS-1$
			{
				importSubjectAttributeDesignator(element, expression, i);
			}
			else if (elementName.equals("ResourceAttributeDesignator")) //$NON-NLS-1$
			{
				importResourceAttributeDesignator(element, expression, i);
			}
			else if (elementName.equals("ActionAttributeDesignator")) //$NON-NLS-1$
			{
				importActionAttributeDesignator(element, expression, i);
			}
			else if (elementName.equals("EnvironmentAttributeDesignator")) //$NON-NLS-1$
			{
				importEnvironmentAttributeDesignator(element, expression, i);
			}
			else if (elementName.equals("VariableReference")) //$NON-NLS-1$
			{
				importVariableReference(element, expression, i);
			}
			else if (elementName.equals("Function")) //$NON-NLS-1$
			{
				importFunction(element.attribute("Function"), expression, i); //$NON-NLS-1$
			}
		}
		
		return expression;
	}


	private void importFunction(Attribute function, Expression expression, int i) throws InternalErrorException{
		String functionName = function.getText();
		try{
			expression.setName(FunctionEnumeration.fromString(functionName.toUpperCase()));
		}
		catch(Exception e){
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoFunctionName"), 
					new Object[] {functionName.toUpperCase()}));
		}
		expression.setOrder(i);
		expression.setExpressionType("name"); //$NON-NLS-1$
	}


	private void importVariableReference(Element variableReference,
			Expression expression, int i) throws InternalErrorException {
		expression.setExpressionType("variable"); //$NON-NLS-1$
		expression.setOrder(i);
		String variableId = variableReference.attributeValue("VariableId"); //$NON-NLS-1$
		if(variableId != null && !variableId.isEmpty())
			expression.setVariableId(variableId);
		else
			throw new InternalErrorException(Messages.getString("ImportData.VariableIdNeeded1")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
	}


	private void importEnvironmentAttributeDesignator(
			Element environmentAttributeDesignator, Expression expression, int i) throws InternalErrorException{
		Attribute dataType = environmentAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String value = environmentAttributeDesignator.attributeValue("AttributeId"); //$NON-NLS-1$
		if(value != null && !value.isEmpty())
			expression.setAttributeDesignator(value);
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDes")); //$NON-NLS-1$
		expression.setExpressionType("environment"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDesEnvNed")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importActionAttributeDesignator(
			Element actionAttributeDesignator, Expression expression, int i) throws InternalErrorException{
		Attribute dataType = actionAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String value = actionAttributeDesignator.attributeValue("AttributeId"); //$NON-NLS-1$
		if(value != null && !value.isEmpty())
			expression.setAttributeDesignator(value);
		else
			throw new InternalErrorException(Messages.getString("ImportData.ValueNeeded2")); //$NON-NLS-1$
		expression.setExpressionType("action"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDesignatorAction")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importResourceAttributeDesignator(
			Element resourceAttributeDesignator, Expression expression, int i) throws InternalErrorException{
		Attribute dataType = resourceAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String value = resourceAttributeDesignator.attributeValue("AttributeId"); //$NON-NLS-1$
		if(value != null && !value.isEmpty())
			expression.setAttributeDesignator(value);
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDesignatorResource")); //$NON-NLS-1$
		expression.setExpressionType("resource"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDesignator")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importSubjectAttributeDesignator(
			Element subjectAttributeDesignator, Expression expression, int i) throws InternalErrorException{
		Attribute dataType = subjectAttributeDesignator.attribute("DataType"); //$NON-NLS-1$
		String value = subjectAttributeDesignator.attributeValue("AttributeId"); //$NON-NLS-1$
		if(value != null && !value.isEmpty())
			expression.setAttributeDesignator(value);
		else
			throw new InternalErrorException(Messages.getString("ImportData.AttDesignatorSubject")); //$NON-NLS-1$
		expression.setExpressionType("subject"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeDesignator(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.ErrorDataType2")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importAttributeValue(Element attributeValue,
			Expression expression, int i) throws InternalErrorException{
		Attribute dataType = attributeValue.attribute("DataType"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		DataType dt = dataTypeConvert(dataTypeValue);
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.ErrorDataType")); //$NON-NLS-1$
		if(dt.toString().equals('S')){
			String value = attributeValue.getText();
			expression.setAttributeValue(value);
		}else{
			String tag = searchTag(dt);
			String elementValue = attributeValue.elementText(tag);
			if (elementValue == null)
				expression.setAttributeValue(attributeValue.getText());
			else
				expression.setAttributeValue(elementValue);
		}
		expression.setExpressionType("attributeValue"); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importAttributeSelector(Element attributeSelector,
			Expression expression, int i) throws InternalErrorException{
		Attribute dataType = attributeSelector.attribute("DataType"); //$NON-NLS-1$
		String value = attributeSelector.attributeValue("RequestContextPath"); //$NON-NLS-1$
		if(value != null && !value.isEmpty())
			expression.setAttributeSelector(value);
		else
			throw new InternalErrorException(Messages.getString("ImportData.ValueNeededError")); //$NON-NLS-1$
		expression.setExpressionType("attributeSelector"); //$NON-NLS-1$
		String dataTypeValue = dataType.getValue();
		if(dataTypeValue != null && !dataTypeValue.isEmpty())
			expression.setDataTypeAttributeValue(dataTypeConvert(dataTypeValue));
		else
			throw new InternalErrorException(Messages.getString("ImportData.DataTypeError")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		expression.setOrder(i);
	}


	private void importApply(Element apply, Expression expression, int i) throws InternalErrorException {
		String valueReturned[] = formatFunctionId(apply.attributeValue("FunctionId")); //$NON-NLS-1$
		expression.setName(FunctionEnumeration.fromString(valueReturned[0]));
		expression.setOrder(i);
		if(valueReturned[1] != null)
			expression.setAttributeDesignator(valueReturned[1]);
		expression.setExpressionType(formatExpressionType(valueReturned[0]));
		
		List<Element> subExpressionList = apply.elements();
		if(!subExpressionList.isEmpty()){
			for(int j = 0; j < subExpressionList.size(); j++)
			{
				Element subExpressio = subExpressionList.get(j);
				Expression newSubExpression = importExpression(subExpressio, j+1, subExpressio.getName());
				expression.getSubexpression().add(newSubExpression);
			}
		}
	}


	private VariableDefinition importVariableDefinition(Element element) throws InternalErrorException{
		VariableDefinition varDef = new VariableDefinition();
		Expression expression = importExpression(element, 1, ""); //$NON-NLS-1$
		String variableId = element.attributeValue("VariableId"); //$NON-NLS-1$
		if(variableId != null && !variableId.isEmpty())
			varDef.setVariableId(variableId);
		else
			throw new InternalErrorException(Messages.getString("ImportData.VariableIdNeeded")); //$NON-NLS-1$
		varDef.setExpression(expression);
		return varDef;
	}


	private Integer maxPolicySet() throws InternalErrorException {
		Collection<PolicySet> collection = policySetService.findMasterPolicySet();
		if(collection.size()>0)
			return collection.size();
		else
			return 1;
	}

	
	private PolicyCombiningAlgorithm formatPolicyCombiningAlg(
			Attribute policyCombiningAlgId) throws InternalErrorException {
		String url = policyCombiningAlgId.getValue();
		PolicyCombiningAlgorithm pca = null;
		if(url != null)
		{
			int i  = url.lastIndexOf(':');
			url = url.substring(i + 1);
			url = url.toUpperCase();
			url = url.replace('-', '_');
		}
		else
		{
			throw new InternalErrorException(Messages.getString("ImportData.PolicyCombiningAlgorithmError")); //$NON-NLS-1$
		}
		try{
			pca = PolicyCombiningAlgorithm.fromString(url);
		}catch(IllegalArgumentException e)
		{
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoPolicyComAlg"), 
					new Object[] {url}));
		}
		return pca;
	}

	
	private DataType dataTypeConvert(String value) throws InternalErrorException{
		String torna = new String();
		if (value != null)
		{	
			if(value.equals(DATATYPESTRING))
				torna = "S"; //$NON-NLS-1$
			else if (value.equals(DATATYPEBOOLEAN))
				torna = "B" ; //$NON-NLS-1$
			else if (value.equals(DATATYPEINTEGER))
				torna = "I"; //$NON-NLS-1$
			else if (value.equals(DATATYPEDOUBLE))
				torna = "DO"; //$NON-NLS-1$
			else if (value.equals(DATATYPEDATE))
				torna = "DATE"; //$NON-NLS-1$
			else if (value.equals(DATATYPETIME))
				torna = "T"; //$NON-NLS-1$
			else if (value.equals(DATATYPEDATETIME))
				torna = "DT"; //$NON-NLS-1$
			else if (value.equals(DATATYPEDAYTIME))
				torna = "D"; //$NON-NLS-1$
			else if (value.equals(DATATYPEYEARMONTH))
				torna = "YM"; //$NON-NLS-1$
			else if (value.equals(DATATYPEANYURI))
				torna = "ANY"; //$NON-NLS-1$
			else if (value.equals(DATATYPEHEXBINARY))
				torna = "H"; //$NON-NLS-1$
			else if (value.equals(DATATYPEBASE64BINARY))
				torna = "B64"; //$NON-NLS-1$
			else if (value.isEmpty())
				torna = null;
			else
				throw new InternalErrorException(String.format(Messages.getString("ImportData.NoDataType"), 
						new Object[] {value}));
		}
	
		return DataType.fromString(torna);
	}

	
	private String convert(String value) throws InternalErrorException{
		int i = value.lastIndexOf(':');
		String newString = value.substring(i + 1);
		newString = newString.toUpperCase();
		newString = newString.replace('-', '_');
		if(newString.contains("GREATER_THAN_OR_EQUAL")) //$NON-NLS-1$
			newString = "TYPE_GREATER_THAN_OR_EQUAL"; //$NON-NLS-1$
		else if(newString.contains("LESS_THAN_OR_EQUAL")) //$NON-NLS-1$
			newString = "TYPE_LESS_THAN_OR_EQUAL"; //$NON-NLS-1$
		else if(newString.contains("GREATER_THAN")) //$NON-NLS-1$
			newString = "TYPE_GREATER_THAN"; //$NON-NLS-1$
		else if(newString.contains("LESS_THAN")) //$NON-NLS-1$
			newString = "TYPE_LESS_THAN"; //$NON-NLS-1$
		else if(newString.contains("EQUAL")) //$NON-NLS-1$
			newString = "TYPE_EQUAL"; //$NON-NLS-1$
		else if(newString.contains("MATCH")) //$NON-NLS-1$
			newString = "TYPE_MATCH"; //$NON-NLS-1$
		else
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoMatchId"), 
					new Object[] {value}));
		
		return newString;
	}
	
	private String[] formatFunctionId(String name) throws InternalErrorException {
		String nom = new String();
		String attributeDesignator = new String();
		int i = name.lastIndexOf(':');
		name = name.substring(i + 1);
		name = name.toUpperCase();
		name = name.replace('-', '_');
		int j = 0;
		
		try{
			if(name.contains("ONE_AND_ONLY")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("BAG_SIZE")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("AT_LEAST_ONE_MEMBER_OF")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("INTERSECTION")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("UNION")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("SUBSET")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("SET_EQUALS")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("IS_IN")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else if(name.contains("BAG")) //$NON-NLS-1$
			{
				j = name.indexOf('_');
				attributeDesignator = name.substring(0, j);
				nom = name.substring(j+1);
				nom = "TYPE_" + nom; //$NON-NLS-1$
			}
			else
			{
				j = name.indexOf('_');
				if(j > 0)
					attributeDesignator = name.substring(0, j);
				nom = name;
			}
			
			String valueToReturn[] = {nom, attributeDesignator};
			return valueToReturn;
		}catch(StringIndexOutOfBoundsException e){
			throw new InternalErrorException(String.format(Messages.getString("ImportData.NoFunctionId"), 
					new Object[] {name}));
		}
	}
	
	private String formatExpressionType(String name) {
		String type = new String();
		if(name.contains("XPATH_NODE")) //$NON-NLS-1$
			type = "function_Xpath"; //$NON-NLS-1$
		else if(name.contains("THAN") || name.contains("EQUAL") || name.contains("TIME_IN_RANGE") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("MATCH")) //$NON-NLS-1$
			type = "function_Comparison"; //$NON-NLS-1$
		else if(name.contains("NORMALIZE") || name.contains("DOUBLE_TO_INTEGER") ||  //$NON-NLS-1$ //$NON-NLS-2$
				name.contains("INTEGER_TO_DOUBLE")) //$NON-NLS-1$
			type = "function_Conversions"; //$NON-NLS-1$
		else if(name.contains("ANY_OF") || name.contains("ALL_OF") || name.contains("ANY_OF_ANY") ||  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("ALL_OF_ANY") || name.contains("ANY_OF_ALL") || name.contains("ALL_OF_ALL") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("MAP")) //$NON-NLS-1$
			type = "function_Higher"; //$NON-NLS-1$
		else if(name.contains("INTERSECTION") || name.contains("AT_LEAST_ONE_MEMBER_OF") || //$NON-NLS-1$ //$NON-NLS-2$
				name.contains("UNION") || name.contains("SUBSET") || name.contains("SET_EQUALS")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			type = "function_Set"; //$NON-NLS-1$
		else if(name.contains("ONE_AND_ONLY") || name.contains("BAG_SIZE") || name.contains("IS_IN") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("BAG")) //$NON-NLS-1$
			type = "function_Bag"; //$NON-NLS-1$
		else if(name.contains("CONCATENATE")) //$NON-NLS-1$
			type = "function_String"; //$NON-NLS-1$
		else if(name.contains("SUBSTRACT") || name.contains("ADD") || name.contains("FLOOR") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("MULTIPLY") || name.contains("DIVIDE") || name.contains("MOD") ||  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				name.contains("ABS") || name.contains("ROUND") || name.contains("FLOOR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			type = "function_Arithmetic"; //$NON-NLS-1$
		else if(name.contains("AND") || name.contains("OR") || name.contains("N_OF") || name.contains("NOT")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			type = "function_Boolean"; //$NON-NLS-1$
		return type;
	}
	
	private String searchTag(DataType dt) {
		String attributeName = new String();
		String dataType = dt.toString();
		if(dataType.equals("YM")) //$NON-NLS-1$
			attributeName = "dt-yearMonthDuration"; //$NON-NLS-1$
		else if(dataType.equals("D")) //$NON-NLS-1$
			attributeName = "dt-dayTimeDuration"; //$NON-NLS-1$
		else if(dataType.equals("B64")) //$NON-NLS-1$
			attributeName = "dt-base64Binary"; //$NON-NLS-1$
		else if(dataType.equals("ANY")) //$NON-NLS-1$
			attributeName = "dt-anyURI"; //$NON-NLS-1$
		else if(dataType.equals("H")) //$NON-NLS-1$
			attributeName = "dt-hexBinary"; //$NON-NLS-1$
		else if(dataType.equals("T")) //$NON-NLS-1$
			attributeName = "dt-time"; //$NON-NLS-1$
		else if(dataType.equals("DT")) //$NON-NLS-1$
			attributeName = "dt-dateTime"; //$NON-NLS-1$
		else if(dataType.equals("DO")) //$NON-NLS-1$
			attributeName = "dt-double"; //$NON-NLS-1$
		else if(dataType.equals("I")) //$NON-NLS-1$
			attributeName = "dt-integer"; //$NON-NLS-1$
		else if(dataType.equals("B")) //$NON-NLS-1$
			attributeName = "dt-boolean"; //$NON-NLS-1$
		return attributeName;
	}
}
