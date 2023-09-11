package com.soffid.iam.addon.xacml.test;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.xacml.service.PolicySetService;
import com.soffid.iam.addons.xacml.service.XACMLBootService;
import com.soffid.iam.api.Tenant;
import com.soffid.iam.utils.Security;
import com.soffid.test.AbstractHibernateTest;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.servei.ApplicationBootService;
import es.caib.seycon.ng.servei.ConfiguracioService;
import es.caib.seycon.ng.servei.UsuariService;

public class XacmlTest extends AbstractHibernateTest {

	private ConfiguracioService configService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ServiceLocator.instance().init("testBeanRefFactory.xml", "beanRefFactory");

//			Security.onSyncServer();
		Security.nestedLogin("master", "admin", Security.ALL_PERMISSIONS);

		configService = (ConfiguracioService) context.getBean(ConfiguracioService.SERVICE_NAME);

		UsuariService usuariSvc = (UsuariService) context.getBean(UsuariService.SERVICE_NAME);

		XACMLBootService bootSvc = (XACMLBootService) context.getBean(XACMLBootService.SERVICE_NAME);
		ApplicationBootService applicationBoot = (ApplicationBootService) context
				.getBean(ApplicationBootService.SERVICE_NAME);
		applicationBoot.consoleBoot();
		bootSvc.consoleBoot();
		bootSvc.tenantBoot(new Tenant("master", "Master", true));
	}

	public void test() throws InternalErrorException {
		URL r = getClass().getClassLoader().getResource("javax/xml/bind/Messages.class");
		System.out.println(r);
		PolicySetService svc = (PolicySetService) ServiceLocator.instance().getService(PolicySetService.SERVICE_NAME);
		svc.importXACMLPolicySet(getClass().getResourceAsStream("/test-policy.xml"));
	}
}
