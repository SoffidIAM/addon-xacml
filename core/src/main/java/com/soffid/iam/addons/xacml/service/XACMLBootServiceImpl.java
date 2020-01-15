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
	private ApplicationContext applicationContext;
	
	@Override
	protected void handleConsoleBoot() throws Exception {
		updateFromVersion1();
	}

	private void updateFromVersion1() throws IOException, Exception 
	{
		DataSource ds = (DataSource) applicationContext.getBean("dataSource"); //$NON-NLS-1$
		final Connection conn = ds.getConnection();
		
		try
		{
			Long tenantId = getTenantService().getMasterTenant().getId();
			
	    	Database db = new Database();
	    	XmlReader reader = new XmlReader();
	    	PathMatchingResourcePatternResolver rpr = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
	    	parseResources(rpr, db, reader, "plugin-ddl.xml");

	    	for (ForeignKey fk: db.foreignKeys)
	    	{
	    		if (fk.foreignTable.equals("SC_TENANT"))
	    		{
	    			log.info("Assigning tenant on table "+fk.tableName);
					executeSentence(conn, "UPDATE "+fk.tableName+" SET "+fk.columns.get(0)+"=? WHERE "+fk.columns.get(0)+" IS NULL",
							new Object[] {tenantId});
	    			
					executeSentence(conn, "UPDATE "+fk.tableName+" SET "+fk.columns.get(0)+"=? WHERE "+fk.columns.get(0)+" = 0",
							new Object[] {tenantId});
	    		}
	    	}
		}
		finally
		{
			conn.close();
		}
	}

	private void parseResources(ResourcePatternResolver rpr, Database db,
			XmlReader reader, String path) throws IOException, Exception {
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
    	while (resources.hasMoreElements())
    	{
    		reader.parse(db, resources.nextElement().openStream());
    	}
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
	

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}


	private void executeSentence(Connection conn, String sql, Object... objects)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		try {
			parseParameters(stmt, objects);
			stmt.execute();
		} finally {
			stmt.close();
		}
	}

	private List<Object[]> executeQuery(Connection conn, String sql,
			Object... objects) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		try {
			parseParameters(stmt, objects);
			ResultSet rset = stmt.executeQuery();
			try {
				List<Object[]> result = new LinkedList<Object[]>();
				int cols = rset.getMetaData().getColumnCount();
				while (rset.next()) {
					Object[] row = new Object[cols];
					for (int i = 0; i < cols; i++) {
						row[i] = rset.getObject(i + 1);
					}
					result.add(row);
				}
				return result;
			} finally {
				rset.close();
			}
		} finally {
			stmt.close();
		}
	}


	protected void parseParameters(PreparedStatement stmt, Object... objects)
			throws SQLException {
		int id = 1;
		for (Object p : objects) {
			if (p == null)
				stmt.setString(id++, null);
			else if (p instanceof String)
				stmt.setString(id++, (String) p);
			else if (p instanceof Integer)
				stmt.setInt(id++, ((Integer) p).intValue());
			else if (p instanceof Long)
				stmt.setLong(id++, ((Long) p).longValue());
			else if (p instanceof Date)
				stmt.setDate(id++, (Date) p);
			else if (p instanceof java.util.Date)
				stmt.setDate(id++, new Date(((java.util.Date) p).getTime()));
			else
				stmt.setObject(id++, p);
		}
	}

}
