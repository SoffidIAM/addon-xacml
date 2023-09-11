package com.soffid.iam.addons.xacml.service.pool;

import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamReader;

import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.sunxacml.cond.FunctionFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class SoffidPDP extends JBossPDP {
	long created; 
	
	static {
		FunctionFactory.setDefaultFactory(new SoffidFunctionFactoryProxy( FunctionFactory.getInstance() ));
	}
	
	public SoffidPDP() {
		created = System.currentTimeMillis();
	}

	public SoffidPDP(InputStream configFile) {
		super(configFile);
		created = System.currentTimeMillis();
	}

	public SoffidPDP(InputSource configFile) {
		super(configFile);
		created = System.currentTimeMillis();
	}

	public SoffidPDP(Node configFile) {
		super(configFile);
		created = System.currentTimeMillis();
	}

	public SoffidPDP(XMLStreamReader configFile) {
		super(configFile);
		created = System.currentTimeMillis();
	}

	public SoffidPDP(URL configFileURL) {
		super(configFileURL);
		created = System.currentTimeMillis();
	}

	public long getCreated() {
		return created;
	}

}
