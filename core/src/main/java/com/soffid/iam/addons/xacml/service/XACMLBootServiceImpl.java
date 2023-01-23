package com.soffid.iam.addons.xacml.service;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.xacml.sync.web.XACMLExternalPEP;
import com.soffid.iam.addons.xacml.sync.web.XACMLPasswordVault;
import com.soffid.iam.addons.xacml.sync.web.XACMLPolicyServlet;
import com.soffid.iam.api.Tenant;
import com.soffid.tools.db.persistence.XmlReader;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.schema.ForeignKey;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.sync.SeyconApplication;
import es.caib.seycon.ng.sync.jetty.JettyServer;

public class XACMLBootServiceImpl extends XACMLBootServiceBase {
	Log log = LogFactory.getLog(getClass());
	
	@Override
	protected void handleConsoleBoot() throws Exception {
	}


	@Override
	protected void handleSyncServerBoot() throws Exception {
		log.info ("Starting XACML services");
		JettyServer jetty = SeyconApplication.getJetty();
		jetty.bindAdministrationServlet("/XACML/PolicyData.xml", null, XACMLPolicyServlet.class);
		jetty.bindAdministrationServlet("/XACML/pep", null, XACMLExternalPEP.class);
		jetty.bindAdministrationServlet("/XACML/vault", null, XACMLPasswordVault.class);
	}

	@Override
	protected void handleTenantBoot(Tenant arg0) throws Exception {
		
	}
	
}
