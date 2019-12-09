package com.soffid.iam.addons.xacml.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;

import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyCriteria;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.RuleCriteria;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.common.TargetCriteria;
import com.soffid.iam.addons.xacml.generate.XACMLGenerator;
import com.soffid.iam.addons.xacml.model.ExpressionEntity;
import com.soffid.iam.addons.xacml.model.ExpressionEntityDao;
import com.soffid.iam.addons.xacml.model.PolicyEntity;
import com.soffid.iam.addons.xacml.model.PolicyEntityDao;
import com.soffid.iam.addons.xacml.model.PolicySetEntity;
import com.soffid.iam.addons.xacml.model.PolicySetEntityDao;
import com.soffid.iam.addons.xacml.model.RuleEntity;
import com.soffid.iam.addons.xacml.model.RuleEntityDao;
import com.soffid.iam.addons.xacml.model.TargetEntity;
import com.soffid.iam.addons.xacml.model.TargetEntityDao;
import com.soffid.iam.addons.xacml.utils.ImportData;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Configuration;
import com.soffid.iam.model.AccountEntity;
import com.soffid.iam.model.VaultFolderEntity;
import com.soffid.iam.model.VaultFolderEntityDao;

import es.caib.seycon.ng.exception.InternalErrorException;

public class PolicySetServiceImpl extends com.soffid.iam.addons.xacml.service.PolicySetServiceBase {
	
	protected com.soffid.iam.addons.xacml.common.PolicySet handleCreate(
			com.soffid.iam.addons.xacml.common.PolicySet policySet) throws java.lang.Exception
	{
		String policySetId = policySet.getPolicySetId().replace(" ", "");
		String version = policySet.getVersion();
		PolicySetCriteria criteria = new PolicySetCriteria();
		criteria.setPolicySetId(policySetId);
		criteria.setVersion(version);
		Collection<PolicySet> polSetCollection = findPolicySetByCriteria(criteria);
		if(polSetCollection != null && !polSetCollection.isEmpty()){
			throw new InternalErrorException(String.format(Messages.getString("PolicySetServiceImpl.PolicySetDuplicated"),  //$NON-NLS-1$
					policySetId));
		}
		else{
			updateTimeStamp();
			return getPolicySetEntityDao().toPolicySet(getPolicySetEntityDao().create(policySet));
		}
	}


	public void updateTimeStamp() throws InternalErrorException {
//		handler.reset ();
		Configuration c = getConfigurationService().findParameterByNameAndNetworkName("soffid.xacml.timestamp", null);
		if (c == null)
		{
			c = new Configuration();
			c.setCode("soffid.xacml.timestamp");
			c.setDescription("XCML Policy set timestamp");
			c.setValue(Long.toString(System.currentTimeMillis()));
			getConfigurationService().create(c);
		}
		else
		{
			c.setValue(Long.toString(System.currentTimeMillis()));
			getConfigurationService().update(c);
		}
	}
	
	
	
	protected com.soffid.iam.addons.xacml.common.PolicySet handleUpdate(
			com.soffid.iam.addons.xacml.common.PolicySet policySet) throws java.lang.Exception
	{
		getPolicySetEntityDao().update(policySet);
		updateTimeStamp();
		return policySet;
	}
	
	//Borrar un Policy Set i amb ell el Target associat
	protected void handleDelete(
			com.soffid.iam.addons.xacml.common.PolicySet policySet) throws java.lang.Exception
	{
		getPolicySetEntityDao().remove(policySet);
		updateTimeStamp();
	}
	
	
	protected com.soffid.iam.addons.xacml.common.Policy handleCreate(
			com.soffid.iam.addons.xacml.common.Policy policy) throws java.lang.Exception
	{
		String policyId = policy.getPolicyId();
		String version = policy.getVersion();
		PolicyCriteria criteria = new PolicyCriteria();
		criteria.setPolicyId(policyId);
		criteria.setVersion(version);
		Collection<Policy> policyCollection = findPolicyByCriteria(criteria);
		if(policyCollection != null && !policyCollection.isEmpty()){
			throw new InternalErrorException(String.format(Messages.getString("PolicySetServiceImpl.PolicyDuplicated"),  //$NON-NLS-1$
					policyId));
		}else{
			updateTimeStamp();
			return getPolicyEntityDao().toPolicy(getPolicyEntityDao().create(policy));
		}
	}
	
	
	protected com.soffid.iam.addons.xacml.common.Policy handleUpdate(
			com.soffid.iam.addons.xacml.common.Policy policy) throws java.lang.Exception
	{
		getPolicyEntityDao().update(policy);
		updateTimeStamp();
		return policy;
	}
	
	
	protected void handleDelete(
			com.soffid.iam.addons.xacml.common.Policy policy) throws java.lang.Exception
	{
		getPolicyEntityDao().remove(policy);
		updateTimeStamp();
	}
	
	
	protected com.soffid.iam.addons.xacml.common.Rule handleCreate(
			com.soffid.iam.addons.xacml.common.Rule rule) throws java.lang.Exception
	{
		PolicyEntity pe = getPolicyEntityDao().load(rule.getPolicyId());
		updateTimeStamp();
		return getRuleEntityDao().toRule(getRuleEntityDao().create(rule, pe ));	
	}
	
	
	protected com.soffid.iam.addons.xacml.common.Rule handleUpdate(
			com.soffid.iam.addons.xacml.common.Rule rule) throws java.lang.Exception
	{
		getRuleEntityDao().update(rule);
		updateTimeStamp();
		return rule;
	}
	
	
	protected void handleDelete(
			com.soffid.iam.addons.xacml.common.Rule rule) throws java.lang.Exception
	{
		getRuleEntityDao().remove(rule);
		updateTimeStamp();
	}
	
	@Override
	protected List<Target> handleFindTargetByCriteria(TargetCriteria criteria)
			throws Exception {
		TargetEntityDao dao = getTargetEntityDao();
		List<TargetEntity> result = dao.findTargetByCriteria(criteria);
		return dao.toTargetList(result);
	}


	@Override
	protected List<Policy> handleFindPolicyByCriteria(PolicyCriteria criteria)
			throws Exception {
		PolicyEntityDao dao = getPolicyEntityDao();
		List<PolicyEntity> result = dao.findPolicyByCriteria(criteria);
		return dao.toPolicyList(result);
	}


	@Override
	protected List<Rule> handleFindRuleByCriteria(RuleCriteria criteria)
			throws Exception {
		RuleEntityDao dao = getRuleEntityDao();
		List<RuleEntity> result = dao.findRuleByCriteria(criteria);
		return dao.toRuleList(result);
	}


	@Override
	protected List<PolicySet> handleFindPolicySetByCriteria(PolicySetCriteria criteria)
			throws Exception {
		PolicySetEntityDao dao = getPolicySetEntityDao();
		List<PolicySetEntity> result = dao.findPolicySetByCriteria(criteria);
		return dao.toPolicySetList(result);
	}


	@Override
	protected List<PolicySet> handleFindChildrenPolicySet(Long id)
			throws Exception {
		if(id != null){
			PolicySetEntityDao dao = getPolicySetEntityDao();
			List<PolicySetEntity> policies = dao.findPolicySetById(id);
			List<PolicySet> list = dao.toPolicySetList(policies);
			Collections.sort(list, new Comparator<PolicySet>(){
				public int compare(PolicySet o1, PolicySet o2) {
					return o1.getOrder().compareTo(o2.getOrder());
				}	
			});
			return list; 
		}else{
			return null;
		}
	}


	@Override
	protected List<Policy> handleFindPolicyChildrenPolicySet(Long id)
			throws Exception {
		if(id != null){
			PolicyEntityDao dao = getPolicyEntityDao();
			List<PolicyEntity> policies = dao.findPolicyByPolicySetId(id);
			List<Policy> list = dao.toPolicyList(policies);
			Collections.sort(list, new Comparator<Policy>(){
				public int compare(Policy o1, Policy o2) {
					return o1.getOrder().compareTo(o2.getOrder());
				}
			});
			return list;
		}else{
			return null;
		}
	}


	@Override
	protected List<Rule> handleFindRuleChildrenPolicy(String policyId)
			throws Exception {
		if(policyId != null){
			RuleEntityDao dao = getRuleEntityDao();
			List<RuleEntity> rules = dao.findRuleChildrenPolicy(policyId);
			List<Rule> list = dao.toRuleList(rules);
			Collections.sort(list, new Comparator<Rule>(){
				public int compare(Rule o1, Rule o2){
					return o1.getOrder().compareTo(o2.getOrder());
				}
			});
			return list;
		}else{
			return null;
		}
	}


	@Override
	protected List<PolicySet> handleFindMasterPolicySet() throws Exception {
		PolicySetEntityDao dao = getPolicySetEntityDao();
		List<PolicySetEntity> policies = dao.findMasterPolicySet();
		List<PolicySet> list = dao.toPolicySetList(policies);
		Collections.sort(list, new Comparator<PolicySet>() {
			public int compare(PolicySet o1, PolicySet o2) {
				return o1.getOrder().compareTo(o2.getOrder());
			}
		});
		return list;
	}


	@Override
	protected Expression handleFinExpressionById(Long id) throws Exception {
		if(id != null){
			ExpressionEntityDao dao = getExpressionEntityDao();
			ExpressionEntity expE = dao.findByExpressionId(id);
			return dao.toExpression(expE);
		}else
			return null;
	}


	@Override
	protected Expression handleFindExpressionByconditionId(Long conditionId)
			throws Exception {
		if (conditionId != null){
			ExpressionEntityDao dao = getExpressionEntityDao();
			ExpressionEntity expression = dao.findByCondition(conditionId);
			Expression exp = dao.toExpression(expression);
		return exp;
		}
		else
			return null;
	}


	@Override
	protected Expression handleFindExpressionByvariableId(Long variableId)
			throws Exception {
		if (variableId != null){
			ExpressionEntityDao dao = getExpressionEntityDao();
			ExpressionEntity expression = dao.findByVariable(variableId);
			Expression exp = dao.toExpression(expression);
		return exp;
		}
		else
			return null;
	}


	@Override
	protected void handleImportXACMLPolicySet(InputStream in) throws Exception {
		ImportData imp = new ImportData(this);	
		imp.importXACML(in);
	}


	@Override
	protected void handleExportXACMLPolcySet(String policySetId, String version,
			OutputStream outputStream) throws Exception {
		XACMLGenerator xacmlGenerator = new XACMLGenerator(this);
		xacmlGenerator.generate(outputStream, policySetId, version);
	}
	
	@Override
	protected void handleExportXACMLPolicy (String policyId, String version,
			OutputStream outputStream) throws Exception {
		XACMLGenerator xacmlGenerator = new XACMLGenerator(this);
		xacmlGenerator.generatePolicy(outputStream, policyId, version);
	}


	
	MultiPDPHandler handler = new MultiPDPHandler(this);
	@Override
	protected ResponseContext handleEvaluate(PDPConfiguration config,
			RequestContext requestContext) throws Exception {
		return handler.handle(config, requestContext);
	}


	@Override
	protected Collection<Account> handleFindFolderAccounts(String folder) throws Exception {
		List<VaultFolderEntity> list = getVaultFolderEntityDao().findPublicRoots();
		
		StringBuffer sb = new StringBuffer();
		VaultFolderEntity f = findFolder ( getVaultFolderEntityDao(), list, folder.split("/"), 0, sb);
		if (f == null)
			return null;
		LinkedList<Account> r = new LinkedList<Account>();
		for (AccountEntity account: f.getAccounts())
			r.add( getAccountEntityDao().toAccount(account));
	
		return r;
	}


	private VaultFolderEntity findFolder(VaultFolderEntityDao dao, Collection<VaultFolderEntity> folders, String[] split, int i, StringBuffer sb) throws InternalErrorException {
		sb.append("Checking folders\n");
		for (VaultFolderEntity folder: folders)
		{
			sb.append(">> "+folder.getName()+" vs "+split[i]+"\n");
			if (folder.getName().equals(split[i]))
			{
				i++;
				if (i >= split.length)
					return folder;
				else
					return findFolder (dao, folder.getChildren(), split, i, sb);
			}
		}
		return null;
	}

}
