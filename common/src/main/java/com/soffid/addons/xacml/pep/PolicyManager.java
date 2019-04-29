package com.soffid.addons.xacml.pep;

import java.util.Collection;
import java.util.Random;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;
import com.soffid.iam.utils.ConfigurationCache;

import es.caib.seycon.ng.comu.Configuracio;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.servei.ejb.ConfiguracioService;
import es.caib.seycon.ng.servei.ejb.ConfiguracioServiceHome;

public class PolicyManager {
	private static final String CONFIG_XACML_WEB_POLICY_SET_VERSION = "soffid.xacml.web.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_WEB_POLICY_SET_ID = "soffid.xacml.web.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_WEB_ENABLE = "soffid.xacml.web.enable"; //$NON-NLS-1$

	private static final String CONFIG_XACML_ROLE_POLICY_SET_VERSION = "soffid.xacml.role.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_ROLE_POLICY_SET_ID = "soffid.xacml.role.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_ROLE_ENABLE = "soffid.xacml.role.enable"; //$NON-NLS-1$

	private static final String CONFIG_XACML_AUTH_POLICY_SET_VERSION = "soffid.xacml.auth.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_AUTH_POLICY_SET_ID = "soffid.xacml.auth.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_AUTH_ENABLE = "soffid.xacml.auth.enable"; //$NON-NLS-1$

	private static final String CONFIG_XACML_EXTERNAL_POLICY_SET_VERSION = "soffid.xacml.external.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_EXTERNAL_POLICY_SET_ID = "soffid.xacml.external.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_EXTERNAL_ENABLE = "soffid.xacml.external.enable"; //$NON-NLS-1$

	private static final String CONFIG_XACML_VAULT_POLICY_SET_VERSION = "soffid.xacml.vault.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_VAULT_POLICY_SET_ID = "soffid.xacml.vault.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_VAULT_ENABLE = "soffid.xacml.vault.enable"; //$NON-NLS-1$

	public final String COOKIE_NAME = "Soffid-XACML-PolicySet-Test"; //$NON-NLS-1$
	private static String random = null;

	private static PepConfiguration currentPolicy = null;
	private static ThreadLocal<PepConfiguration> testPolicies = new ThreadLocal<PepConfiguration>();
	
	public PepConfiguration getCurrentPolicy() throws NamingException,
			CreateException, InternalErrorException {
		
		if (currentPolicy != null)
			return currentPolicy;

		PepConfiguration pm = new PepConfiguration();
		pm.getWebPolicy().setEnabled(false);
		pm.getRolePolicy().setEnabled(false);
		pm.getAuthPolicy().setEnabled(false);
		pm.getVaultPolicy().setEnabled(false);
		pm.getExternalPolicy().setEnabled(false);

		configure(pm.getWebPolicy(), CONFIG_XACML_WEB_ENABLE, CONFIG_XACML_WEB_POLICY_SET_ID, CONFIG_XACML_WEB_POLICY_SET_VERSION);
		configure(pm.getRolePolicy(), CONFIG_XACML_ROLE_ENABLE, CONFIG_XACML_ROLE_POLICY_SET_ID, CONFIG_XACML_ROLE_POLICY_SET_VERSION);
		configure(pm.getAuthPolicy(), CONFIG_XACML_AUTH_ENABLE, CONFIG_XACML_AUTH_POLICY_SET_ID, CONFIG_XACML_AUTH_POLICY_SET_VERSION);
		configure(pm.getExternalPolicy(), CONFIG_XACML_EXTERNAL_ENABLE, CONFIG_XACML_EXTERNAL_POLICY_SET_ID, CONFIG_XACML_EXTERNAL_POLICY_SET_VERSION);
		configure(pm.getVaultPolicy(), CONFIG_XACML_VAULT_ENABLE, CONFIG_XACML_VAULT_POLICY_SET_ID, CONFIG_XACML_VAULT_POLICY_SET_VERSION);

		currentPolicy = pm;
		
		return currentPolicy;
	}

	private void configure(PolicyStatus ps, String policyEnableProperty, String policySetProperty, String policyVersionProperty) {
		String config = ConfigurationCache.getProperty(policySetProperty);
		if (config != null && config.length() > 0) {
			ps.setPolicyId(config);
			config = ConfigurationCache.getProperty(policyVersionProperty);
			if (config != null && config.length() > 0)
				ps.setPolicyVersion(config);
		}
		config = ConfigurationCache.getProperty(policyEnableProperty);
		if (config != null && config.equals("true")) { //$NON-NLS-1$
			ps.setEnabled(true);
		}
	}

	public void applyProperty (ConfiguracioService configuracioService, String property, String value) throws InternalErrorException
	{
		Configuracio config = configuracioService.findParametreByCodiAndCodiXarxa(property, null);
		if (config == null)
		{
			if (value != null && value.trim().length() > 0 )
				configuracioService.create( new Configuracio(property, value) );
		} else  {
			if (value == null || value.trim().length() == 0)
				configuracioService.delete(config);
			else
			{
				config.setValor(value);
				configuracioService.update( config );
			}
		}
	}

	public void applyPolicyStatus ( PolicyStatus ps, ConfiguracioService configuracioService,
			PolicySetService policySetService,
			String policyEnableProperty, String policySetProperty, String policyVersionProperty ) throws InternalErrorException
	{
		if (ps.isEnabled())
		{
			PolicySetCriteria criteria = new PolicySetCriteria();
			criteria.setPolicySetId(ps.getPolicyId());
			criteria.setVersion(ps.getPolicyVersion());
			Collection<PolicySet> policySetCollection = policySetService.findPolicySetByCriteria(criteria);
			if(policySetCollection == null || policySetCollection.isEmpty()){
				throw new InternalErrorException(String.format(Messages.getString(
						"PolicyManager.Validation"), ps.getPolicyId(), ps.getPolicyVersion()));//$NON-NLS-1$ //$NON-NLS-2$
			}
			for(PolicySet polS: policySetCollection){
				if(!polS.getPolicySetId().equals(ps.getPolicyId()) || !polS.getVersion().equals(ps.getPolicyVersion()))
					throw new InternalErrorException(String.format(Messages.getString(
							"PolicyManager.Validation"), ps.getPolicyId(), ps.getPolicyVersion()));//$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		applyProperty(configuracioService, policyEnableProperty, Boolean.toString(ps.isEnabled()));
		applyProperty(configuracioService, policySetProperty, ps.getPolicyId());
		applyProperty(configuracioService, policyVersionProperty, ps.getPolicyVersion());
		
	}
	
	public void apply(PepConfiguration pc) throws InternalErrorException, NamingException, CreateException {
		InitialContext ctx = new InitialContext ();
		PolicySetService policySetService = (PolicySetService) ctx.lookup(PolicySetServiceHome.JNDI_NAME);
		ConfiguracioService configuracioService = (ConfiguracioService) ctx.lookup(ConfiguracioServiceHome.JNDI_NAME);

		if (!pc.isTesting()) {
			applyPolicyStatus(pc.getWebPolicy(), configuracioService, policySetService, 
					CONFIG_XACML_WEB_ENABLE, CONFIG_XACML_WEB_POLICY_SET_ID, CONFIG_XACML_WEB_POLICY_SET_VERSION);
			applyPolicyStatus(pc.getRolePolicy(), configuracioService, policySetService, 
					CONFIG_XACML_ROLE_ENABLE, CONFIG_XACML_ROLE_POLICY_SET_ID, CONFIG_XACML_ROLE_POLICY_SET_VERSION);
			applyPolicyStatus(pc.getAuthPolicy(), configuracioService, policySetService, 
					CONFIG_XACML_AUTH_ENABLE, CONFIG_XACML_AUTH_POLICY_SET_ID, CONFIG_XACML_AUTH_POLICY_SET_VERSION);
			applyPolicyStatus(pc.getExternalPolicy(), configuracioService, policySetService, 
					CONFIG_XACML_EXTERNAL_ENABLE, CONFIG_XACML_EXTERNAL_POLICY_SET_ID, CONFIG_XACML_EXTERNAL_POLICY_SET_VERSION);
			applyPolicyStatus(pc.getVaultPolicy(), configuracioService, policySetService, 
					CONFIG_XACML_VAULT_ENABLE, CONFIG_XACML_VAULT_POLICY_SET_ID, CONFIG_XACML_VAULT_POLICY_SET_VERSION);

			currentPolicy = null;
		}
	}
	
	public void setTestConfiguration (PepConfiguration config)
	{
		testPolicies.set(config);
	}
}
