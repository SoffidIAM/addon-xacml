package com.soffid.iam.addons.xacml.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;

import com.soffid.iam.addons.xacml.common.PDPConfiguration;
import com.soffid.iam.addons.xacml.common.PDPPolicySetReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.service.pool.AbstractPool;
import com.soffid.iam.addons.xacml.service.pool.PDPPool;
import com.soffid.iam.addons.xacml.service.pool.SoffidPDP;

import es.caib.seycon.ng.exception.InternalErrorException;

public class MultiPDPHandler {

	public void reset ()
	{
		map.clear();
	}
	
	Map<PDPConfiguration, AbstractPool<SoffidPDP>> map =
			new HashMap<PDPConfiguration, AbstractPool<SoffidPDP>>();
	private PolicySetService policySetService;

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

	public ResponseContext handle(PDPConfiguration config,
			RequestContext requestContext) throws Exception {
		JBossPDP jbosspdp = getJBossPDP(config);
		return jbosspdp.evaluate(requestContext);
	}

}
