package com.soffid.iam.addons.xacml.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.jboss.security.xacml.factories.PolicyFactory;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.interfaces.XACMLPolicy;
import org.jboss.security.xacml.sunxacml.PolicyMetaData;
import org.jboss.security.xacml.sunxacml.VersionConstraints;
import org.jboss.security.xacml.sunxacml.ctx.Status;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinder;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderModule;
import org.jboss.security.xacml.sunxacml.finder.PolicyFinderResult;

import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyCriteria;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.service.PolicySetService;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.exception.InternalErrorException;

public class SoffidPolicyFinderModule extends PolicyFinderModule {


	public SoffidPolicyFinderModule ()
	{
	}

	@Override
	public void init(PolicyFinder finder) {
	}

	@Override
	public PolicyFinderResult findPolicy(URI idReference, int type,
			VersionConstraints constraints, PolicyMetaData parentMetaData) 
	{

		PolicySetService policySetService = (PolicySetService) ServiceLocator.instance().getService(PolicySetService.SERVICE_NAME);

		try 
		{
			if (type == XACMLPolicy.POLICY)
			{
				PolicyCriteria pc = new PolicyCriteria();
				pc.setPolicyId(idReference.toString());
				for (Policy policy: policySetService.findPolicyByCriteria(pc))
				{
					if (constraints.meetsConstraint(policy.getVersion()))
					{
						File f = new File (new File(System.getProperty("jboss.server.temp.dir")), "xacml-policy-"+policy.getId()+".xml");
						OutputStream out = new FileOutputStream(f);
						policySetService.exportXACMLPolicy(policy.getPolicyId(), policy.getVersion(), out);
						out.close ();

			            XACMLPolicy xacmlPolicy = PolicyFactory.createPolicy(new FileInputStream(f));
			            
			            org.jboss.security.xacml.sunxacml.Policy jbossPolicy = (org.jboss.security.xacml.sunxacml.Policy) xacmlPolicy.get(XACMLConstants.UNDERLYING_POLICY);
			            return new PolicyFinderResult(jbossPolicy);
					}
				}
			}
			else
			{
				PolicySetCriteria pc = new PolicySetCriteria();
				pc.setPolicySetId(idReference.toString());
				for (PolicySet policySet: policySetService.findPolicySetByCriteria(pc))
				{
					if (constraints.meetsConstraint(policySet.getVersion()))
					{
						File f = new File (new File(System.getProperty("jboss.server.temp.dir")), "xacml-policy-"+policySet.getId()+".xml");
						OutputStream out = new FileOutputStream(f);
						
						policySetService.exportXACMLPolcySet(policySet.getPolicySetId(), policySet.getVersion(), out);
						
						out.close ();
			            XACMLPolicy xacmlPolicySet = PolicyFactory.createPolicySet(new FileInputStream(f));
			            
			            org.jboss.security.xacml.sunxacml.PolicySet jbossPolicySet = (org.jboss.security.xacml.sunxacml.PolicySet) xacmlPolicySet.get(XACMLConstants.UNDERLYING_POLICY);
			            return new PolicyFinderResult(jbossPolicySet);
						
					}
				}
			}
			return new PolicyFinderResult();
		} catch (Exception e) {
			Status status = new Status(Collections.singletonList(Status.STATUS_PROCESSING_ERROR), e.toString());
			return new PolicyFinderResult(status);
		}
	}
}
