package com.soffid.iam.web.wheel;

import java.util.Date;
import java.util.LinkedList;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.zkiblaf.Application;
import es.caib.zkib.zkiblaf.Missatgebox;

public class ActualIrc04Sector {
	PolicySetService service;

	public ActualIrc04Sector() throws NamingException {
		service = (PolicySetService) new InitialContext().lookup(PolicySetServiceHome.JNDI_NAME);
	}
	
	public boolean isDone() throws InternalErrorException, NamingException {
		return ! service.findMasterPolicySet().isEmpty();
	}
	
	public void activate() {
		Missatgebox.confirmaOK_CANCEL(Labels.getLabel("xacml.wizard.explanation"), e -> {
			if (e.getName().equals("onOK")) {
				Application.jumpTo("/addon/xacml/policySet.zul");
			}
		});
	}

}
