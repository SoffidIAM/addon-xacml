package com.soffid.iam.addons.xacml.service;

import com.soffid.iam.addons.xacml.sync.web.XACMLPolicyServlet;

import es.caib.seycon.ng.sync.SeyconApplication;

public class XACMLBootServiceImpl extends XACMLBootServiceBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		SeyconApplication.getJetty().
		bindAdministrationServlet("/XACML/PolicyData.xml", null, XACMLPolicyServlet.class);
	}
	

}
