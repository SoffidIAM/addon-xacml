package com.soffid.iam.addons.xacml.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.core.model.context.ResultType;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.sunxacml.Obligation;
import org.jboss.security.xacml.sunxacml.ctx.Attribute;
import org.jboss.security.xacml.sunxacml.ctx.RequestCtx;
import org.jboss.security.xacml.sunxacml.ctx.ResponseCtx;
import org.jboss.security.xacml.sunxacml.ctx.Result;
import org.jboss.security.xacml.sunxacml.ctx.Subject;

import com.soffid.iam.addons.xacml.XacmlServiceLocator;
import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.PDPPolicySetReference;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.service.pool.AbstractPool;
import com.soffid.iam.addons.xacml.service.pool.PDPPool;
import com.soffid.iam.addons.xacml.service.pool.SoffidPDP;
import com.soffid.iam.service.impl.tenant.Action;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.exception.InternalErrorException;

public class MultiPDPHandler {
	boolean debug = true;
	Log log = LogFactory.getLog(getClass());
	
	public void reset ()
	{
		map.clear();
	}
	
	Map<PDPConfiguration, AbstractPool<SoffidPDP>> map =
			new HashMap<PDPConfiguration, AbstractPool<SoffidPDP>>();
	private PolicySetService policySetService ;

	public MultiPDPHandler(PolicySetService svc) {
		this.policySetService = svc;
	}
	
	protected JBossPDP getJBossPDP(PDPConfiguration config) throws Exception
	{
		AbstractPool<SoffidPDP> ref = map.get(config);
		if (ref == null)
		{
			ref = new PDPPool(config, policySetService); 
			map.put(config, ref);
		}
		JBossPDP jbossPDP = ref.getConnection();
		return jbossPDP;
	}

	protected void returnConnection(PDPConfiguration config) throws Exception
	{
		AbstractPool<SoffidPDP> ref = map.get(config);
		if (ref != null)
		{
			ref.returnConnection();
		}
	}

	public ResponseContext handle(PDPConfiguration config,
			RequestContext requestContext) throws Exception {
		JBossPDP jbosspdp = getJBossPDP(config);
		try {
			ResponseContext result = jbosspdp.evaluate(requestContext);
			if (config.isDebug()) {
				ResponseCtx resp = result.get(XACMLConstants.RESPONSE_CTX);
				debugRequest (config, requestContext, resp);
			}
			return result;
		} finally {
			returnConnection(config);
		}
	}

	private void debugRequest(PDPConfiguration config, RequestContext requestContext, ResponseCtx resp) throws InternalErrorException, IOException {
		PDPPolicySetReference policy = config.getPolicies().iterator().next();
		PolicySetService svc = XacmlServiceLocator.instance().getPolicySetService();
		PolicySetCriteria criteria = new PolicySetCriteria();
		criteria.setPolicySetId(policy.getPolicySet());
		criteria.setVersion(policy.getPolicySetVersion());
		File f = File.createTempFile("policySet", ".xml");
		try {
			RequestCtx ctx = requestContext.get(XACMLConstants.REQUEST_CTX);
			StringBuffer sb = new StringBuffer();
			sb.append("subject:\n");
			for ( Subject s: (Collection<Subject>) ctx.getSubjectsAsList()) {
				Collection<Attribute> values = s.getAttributesAsList();
				for (Attribute value: values) {
					sb.append(" "+value.getId()+"="+value.getValue()+"\n");					
				}
			}
			sb.append("resource:\n");
			for ( Attribute value: (Collection<Attribute>) ctx.getResourceAsList()) {
				sb.append(" "+value.getId()+"="+value.getValue()+"\n");					
			}
			sb.append("action:\n");
			for ( Attribute value: (Collection<Attribute>) ctx.getActionAsList()) {
				sb.append(" "+value.getId()+"="+value.getValue()+"\n");					
			}
			sb.append("environment:\n");
			for ( Attribute value: (Collection<Attribute>) ctx.getEnvironmentAttributesAsList()) {
				sb.append(" "+value.getId()+"="+value.getValue()+"\n");					
			}
			StringBuffer sb2 = new StringBuffer();
			for (PolicySet set: svc.findPolicySetByCriteria(criteria )) {
				debug (sb2, f, set, requestContext, "");
				log.info("Analyzing request:\n"+sb.toString());
				
				if (resp != null && resp.getResults() != null) {
					for (Result rr: (Collection<Result>)resp.getResults()) {
						if (rr.getObligations() != null) {
							for (Obligation obl: (Set<Obligation>) rr.getObligations()) {
								sb2.append("Obligation: "+obl.getId().toString()+"\n");
								for ( Attribute att: (List<Attribute>) obl.getAssignments()) {
									sb2.append("            - "+att.getId()+": "+att.getValue().toString()+"\n");
								}
							}
						}
					}
				}

				log.info("Evaluation tree\n"+sb2.toString());
			}
		} finally {
			f.delete();
		}
	}

	private void debug(StringBuffer sb, File f, PolicySet set, RequestContext requestContext, String prefix) throws IOException, InternalErrorException {
		int r = generatePolicySetPDP (f, set, requestContext, false);
		if (r == XACMLConstants.DECISION_NOT_APPLICABLE) {
			sb.append(prefix+"PolicySet "+set.getPolicySetId()+" "+set.getVersion()+": NOT APPLICABLE\n");
		} else {
			sb.append(prefix+"PolicySet "+set.getPolicySetId()+" "+set.getVersion()+": MATCHES\n");
		}
		for ( PolicySet child: policySetService.findChildrenPolicySet(set.getId())) {
			debug (sb, f, child, requestContext, prefix + "> ");
		}
		for ( Policy child: policySetService.findPolicyChildrenPolicySet(set.getId())) {
			debug (sb, f, child, requestContext, prefix + "> ");
		}
		r = generatePolicySetPDP (f, set, requestContext, true);
		if (r == XACMLConstants.DECISION_DENY)
			sb.append(prefix+"Decision: DENY\n");
		if (r == XACMLConstants.DECISION_INDETERMINATE)
			sb.append(prefix+"Decision: INDETERMINATE\n");
		if (r == XACMLConstants.DECISION_NOT_APPLICABLE)
			sb.append(prefix+"Decision: NOT APPLICABLE\n");
		if (r == XACMLConstants.DECISION_PERMIT)
			sb.append(prefix+"Decision: PERMIT\n");
	}

	private void debug(StringBuffer sb, File f, Policy pol, RequestContext requestContext, String prefix) throws IOException, InternalErrorException {

		int r = generatePolicyPDP (f, pol, requestContext, false, new Integer(-1));
		if (r == XACMLConstants.DECISION_NOT_APPLICABLE) {
			sb.append(prefix+"Policy "+pol.getPolicyId()+" "+pol.getVersion()+": NOT APPLICABLE\n");
		} else {
			sb.append(prefix+"Policy "+pol.getPolicyId()+" "+pol.getVersion()+": MATCHES\n");
			List<Rule> rules = new LinkedList<Rule> (pol.getRule());
			for ( int i = 0; i < rules.size() ; i++ ) {
				Rule rule = rules.get(i);
				r = generatePolicyPDP (f, pol, requestContext, false, new Integer(i));
				if (r == XACMLConstants.DECISION_DENY)
					sb.append(prefix+"- Rule "+rule.getDescription()+": DENY\n");
				if (r == XACMLConstants.DECISION_INDETERMINATE)
					sb.append(prefix+"- Rule "+rule.getDescription()+": INDETERMINATE\n");
				if (r == XACMLConstants.DECISION_NOT_APPLICABLE)
					sb.append(prefix+"- Rule "+rule.getDescription()+": NOT APPLICABLE\n");
				if (r == XACMLConstants.DECISION_PERMIT)
					sb.append(prefix+"- Rule "+rule.getDescription()+": PERMIT\n");
			}
			r = generatePolicyPDP (f, pol, requestContext, true, new Integer(-1));
			if (r == XACMLConstants.DECISION_DENY)
				sb.append(prefix+"Decision: DENY\n");
			if (r == XACMLConstants.DECISION_INDETERMINATE)
				sb.append(prefix+"Decision: INDETERMINATE\n");
			if (r == XACMLConstants.DECISION_NOT_APPLICABLE)
				sb.append(prefix+"Decision: NOT APPLICABLE\n");
			if (r == XACMLConstants.DECISION_PERMIT)
				sb.append(prefix+"Decision: PERMIT\n");
		}
	}


	private int generatePolicySetPDP(File f, PolicySet set, RequestContext requestContext, boolean childPolicies) throws InternalErrorException, IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>\n")
			.append("<ns:jbosspdp xmlns:ns='urn:jboss:xacml:2.0'>\n")
			.append("<ns:Policies>\n")
			.append("<ns:PolicySet>\n")
			.append("<ns:Location>\n")
			.append(f.toURI().toString())
			.append("</ns:Location>\n")
			.append("</ns:PolicySet>\n")
			.append("</ns:Policies>\n")
			.append("<ns:Locators>\n")
			.append("<ns:Locator Name='org.jboss.security.xacml.locators.JBossPolicySetLocator'/>\n")
			.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidAttributeFinderModule'/>\n")
			.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidPolicyLocator'/>\n")
			.append("</ns:Locators>\n")
			.append("</ns:jbosspdp>\n");
		String result = sb.toString();
		FileOutputStream out = new FileOutputStream(f);
		policySetService.exportXACMLPolicySet(set.getPolicySetId(), set.getVersion(), out, childPolicies);
		out.close ();
		SoffidPDP pdp = new SoffidPDP(new ByteArrayInputStream(result.getBytes("UTF-8")));
		ResponseContext r = pdp.evaluate(requestContext);
		return r.getDecision();
	}
	
	
	private int generatePolicyPDP(File f, Policy pol, RequestContext requestContext, boolean allRules, Integer ruleNumber) throws InternalErrorException, IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>\n")
			.append("<ns:jbosspdp xmlns:ns='urn:jboss:xacml:2.0'>\n")
			.append("<ns:Policies>\n")
			.append("<ns:PolicySet>\n")
			.append("<ns:Location>\n")
			.append(f.toURI().toString())
			.append("</ns:Location>\n")
			.append("</ns:PolicySet>\n")
			.append("</ns:Policies>\n")
			.append("<ns:Locators>\n")
			.append("<ns:Locator Name='org.jboss.security.xacml.locators.JBossPolicySetLocator'/>\n")
			.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidAttributeFinderModule'/>\n")
			.append("<ns:Locator Name='com.soffid.iam.addons.xacml.service.SoffidPolicyLocator'/>\n")
			.append("</ns:Locators>\n")
			.append("</ns:jbosspdp>\n");
		String result = sb.toString();
		FileOutputStream out = new FileOutputStream(f);
		policySetService.exportXACMLPolicy(pol.getPolicyId(), pol.getVersion(), out, true, allRules, ruleNumber);
		out.close ();
		SoffidPDP pdp = new SoffidPDP(new ByteArrayInputStream(result.getBytes("UTF-8")));
		ResponseContext r = pdp.evaluate(requestContext);
		return r.getDecision();
	}
	

}
