package com.soffid.iam.addons.xacml.service;

import com.soffid.iam.addons.xacml.sync.web.XACMLExternalPEP;
import com.soffid.iam.addons.xacml.sync.web.XACMLPasswordVault;
import com.soffid.iam.addons.xacml.sync.web.XACMLPolicyServlet;
import com.soffid.iam.api.Tenant;

import es.caib.seycon.ng.sync.SeyconApplication;
import es.caib.seycon.ng.sync.jetty.JettyServer;

public class XACMLBootServiceImpl extends XACMLBootServiceBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		JettyServer jetty = SeyconApplication.getJetty();
		jetty.bindAdministrationServlet("/XACML/PolicyData.xml", null, XACMLPolicyServlet.class);
		jetty.bindAdministrationServlet("/XACML/pep", null, XACMLExternalPEP.class);
		jetty.bindAdministrationServlet("/XACML/vault", null, XACMLPasswordVault.class);
	}

	@Override
	protected void handleTenantBoot(Tenant arg0) throws Exception {
		
	}
	

}
