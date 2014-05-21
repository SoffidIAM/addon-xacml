package com.soffid.addons.xacml.pep;

import java.util.Collection;
import java.util.Random;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zul.Box;
import org.zkoss.zul.Label;

import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;

import es.caib.seycon.ng.comu.Configuracio;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.servei.ejb.ConfiguracioServiceHome;

public class PolicyManager {
	private static final String CONFIG_XACML_POLICY_SET_VERSION = "soffid.xacml.policySetVersion"; //$NON-NLS-1$
	private static final String CONFIG_XACML_POLICY_SET_ID = "soffid.xacml.policySetId"; //$NON-NLS-1$
	private static final String CONFIG_XACML_ENABLE = "soffid.xacml.enable"; //$NON-NLS-1$
	public final String COOKIE_NAME = "Soffid-XACML-PolicySet-Test"; //$NON-NLS-1$
	private static String random = null;

	private static PolicyStatus currentPolicy = null;
	private static PolicyStatus testPolicy = null;
	
	public PolicyStatus getCurrentPolicy() throws NamingException,
			CreateException, InternalErrorException {
		return getCurrentPolicy((HttpServletRequest) Executions.getCurrent()
				.getNativeRequest());
	}

	public PolicyStatus getCurrentPolicy(HttpServletRequest request)
			throws NamingException, CreateException, InternalErrorException {
		if (request.getCookies() != null)
		{
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(COOKIE_NAME))
				{
					PolicyStatus ps;
					ps = parseCookie(cookie);
					if (ps != null)
						return ps;
				}
			}
		}
		
		if (currentPolicy != null)
			return currentPolicy;

		PolicyStatus ps = new PolicyStatus();
		ps.setEnabled(false);

		String config = System.getProperty(CONFIG_XACML_POLICY_SET_ID);
		if (config != null && config.length() > 0) {
			ps.setPolicyId(config);
			config = System.getProperty(CONFIG_XACML_POLICY_SET_VERSION);
			if (config != null && config.length() > 0)
				ps.setPolicyVersion(config);
		}
		config = System.getProperty(CONFIG_XACML_ENABLE);
		if (config != null && config.equals("true")) { //$NON-NLS-1$
			ps.setEnabled(true);
		}
		currentPolicy = ps;
		
		return ps;
	}

	private PolicyStatus parseCookie(Cookie cookie) {
		if (cookie.getValue().equals(random))
			return testPolicy;
		else
			return null;
	}

	public void apply(PolicyStatus ps) throws InternalErrorException, NamingException, CreateException {
		InitialContext ctx = new InitialContext ();
		PolicySetServiceHome pshome = (PolicySetServiceHome) ctx.lookup(PolicySetServiceHome.JNDI_NAME);
		PolicySetService policySetService = pshome.create();
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
		if (ps.isTesting()) {
			testPolicy = ps;
			StringBuffer b = new StringBuffer ();
			Random r = new Random(System.currentTimeMillis());
			for (int i = 0; i < 50; i++)
			{
				int n = r.nextInt(62);
				if ( n < 10)	
					b.append ((char) (n + '0'));
				else if ( n < 36)
					b.append ((char) (n + 'a'  - 10));
				else 
					b.append ((char) (n + 'A'  - 36));
			}
			random = b.toString();
			HttpServletResponse response = (HttpServletResponse) Executions
					.getCurrent().getNativeResponse();
			Cookie cookie = new Cookie(COOKIE_NAME, random);
			response.addCookie(cookie);
			generateMessage(ps);
		} else {
			ConfiguracioServiceHome home = (ConfiguracioServiceHome) new javax.naming.InitialContext()
					.lookup(ConfiguracioServiceHome.JNDI_NAME);
			es.caib.seycon.ng.servei.ejb.ConfiguracioService configuracioService = home
					.create();
			es.caib.seycon.ng.comu.Configuracio config;
			config = configuracioService.findParametreByCodiAndCodiXarxa(CONFIG_XACML_ENABLE, null);
			if (config == null)
			{
				configuracioService.create( new Configuracio(CONFIG_XACML_ENABLE, Boolean.toString(ps.isEnabled())) );
			} else {
				config.setValor(Boolean.toString(ps.isEnabled()));
				configuracioService.update( config );
			}
			
			if (ps.isEnabled())
			{
				config = configuracioService.findParametreByCodiAndCodiXarxa(
						CONFIG_XACML_POLICY_SET_ID, null);
				if (config == null)
				{
					configuracioService.create( new Configuracio(CONFIG_XACML_POLICY_SET_ID, ps.getPolicyId()) );
				} else {
					config.setValor(ps.getPolicyId());
					configuracioService.update( config );
				}

				config = configuracioService.findParametreByCodiAndCodiXarxa(
						CONFIG_XACML_POLICY_SET_VERSION, null);
				if (config == null)
				{
					configuracioService.create( new Configuracio(CONFIG_XACML_POLICY_SET_VERSION, ps.getPolicyVersion()) );
				} else {
					config.setValor(ps.getPolicyVersion());
					configuracioService.update( config );
				}
			}
			currentPolicy = null;
			
			// Remove current cookie (if any)
			HttpServletResponse response = (HttpServletResponse) Executions
					.getCurrent().getNativeResponse();
			Cookie cookie = new Cookie(COOKIE_NAME, null);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
	
	public void generateMessage() throws NamingException, CreateException, InternalErrorException
	{
		generateMessage (getCurrentPolicy());
	}
	
	protected void generateMessage(PolicyStatus ps) throws NamingException, CreateException, InternalErrorException
	{
		Box box = (Box) Path.getComponent("//index/xacmltesting"); //$NON-NLS-1$
		Label label = (Label) Path.getComponent("//index/xacmltestinglabel"); //$NON-NLS-1$
		
		if (ps != null && ps.isTesting() && ps.isEnabled())
		{
			box.setVisible(true);
			label.setValue(String.format("Testing XACML Policy Set %s: %s", //$NON-NLS-1$
					ps.getPolicyId(), ps.getPolicyVersion()));
		}
		else
			box.setVisible(false);
	}
}
