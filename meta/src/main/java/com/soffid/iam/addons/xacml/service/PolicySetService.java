//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.service;
import com.soffid.iam.model.VaultFolderEntity;
import com.soffid.mda.annotation.*;

import es.caib.seycon.ng.comu.Account;
import es.caib.seycon.ng.model.AccountEntity;
import es.caib.seycon.ng.servei.ConfiguracioService;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="PolicySetService",
	 translatedPackage="com.soffid.iam.addons.xacml.service")
@Depends ({com.soffid.iam.addons.xacml.model.PolicySetEntity.class,
	AccountEntity.class,
	VaultFolderEntity.class,
	com.soffid.iam.addons.xacml.model.PolicyEntity.class,
	com.soffid.iam.addons.xacml.model.RuleEntity.class,
	com.soffid.iam.addons.xacml.model.TargetEntity.class,
	com.soffid.iam.addons.xacml.model.ExpressionEntity.class,
	ConfiguracioService.class})
public abstract class PolicySetService {

	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.PolicySet create(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.PolicySet update(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.PolicySet test(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.addons.xacml.common.PolicySet policySet)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Policy create(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Policy update(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.addons.xacml.common.Policy policy)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Rule create(
		com.soffid.iam.addons.xacml.common.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Rule update(
		com.soffid.iam.addons.xacml.common.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.addons.xacml.common.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.Target> findTargetByCriteria(
		com.soffid.iam.addons.xacml.common.TargetCriteria criteria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.Policy> findPolicyByCriteria(
		com.soffid.iam.addons.xacml.common.PolicyCriteria criteria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.Rule> findRuleByCriteria(
		com.soffid.iam.addons.xacml.common.RuleCriteria criteria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.PolicySet> findPolicySetByCriteria(
		com.soffid.iam.addons.xacml.common.PolicySetCriteria criteria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.PolicySet> findChildrenPolicySet(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.Policy> findPolicyChildrenPolicySet(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.Rule> findRuleChildrenPolicy(
		java.lang.String policyId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.xacml.common.PolicySet> findMasterPolicySet()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Expression finExpressionById(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Expression findExpressionByconditionId(
		java.lang.Long conditionId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.xacml.common.Expression findExpressionByvariableId(
		java.lang.Long variableId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void importXACMLPolicySet(
		java.io.InputStream inputStream)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void exportXACMLPolcySet(
		java.lang.String policySetId, 
		java.lang.String version, 
		java.io.OutputStream outputStream)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.anonymous.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public org.jboss.security.xacml.interfaces.ResponseContext evaluate(
		com.soffid.iam.addons.xacml.common.PDPConfiguration config, 
		org.jboss.security.xacml.interfaces.RequestContext requestContext)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.xacml.service.policySet_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void exportXACMLPolicy(
		java.lang.String policyId, 
		java.lang.String version, 
		java.io.OutputStream outputStream)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	
	public Collection<Account> findFolderAccounts(String folder)
	{ return null; }
}
