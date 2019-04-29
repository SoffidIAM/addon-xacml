package com.soffid.iam.addons.xacml.sync.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetCriteria;
import com.soffid.iam.addons.xacml.generate.XACMLGenerator;
import com.soffid.iam.addons.xacml.service.PolicySetService;

import es.caib.seycon.ng.ServiceLocator;

public class XACMLPolicyServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
    public void init() throws ServletException {
        super.init();
        
    }

    
    public XACMLPolicyServlet() {
        super();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
        	PolicySetService svc = (PolicySetService)ServiceLocator.instance().getService(PolicySetService.SERVICE_NAME);
        	String id = (String) req.getParameter("id");
        	String name = (String) req.getParameter("name");
        	String version = (String) req.getParameter("version");
    		PolicySetCriteria criteria = new PolicySetCriteria();
    		criteria.setPolicySetId(name);
    		criteria.setVersion(version);
			List<PolicySet> policySets = svc.findPolicySetByCriteria(criteria );
			if (policySets.isEmpty())
        		throw new ServletException("Cannot find selected policy set. Set right name and version");
			if (policySets.size() > 1)	
        		throw new ServletException("Too many policy sets found. Set right name and version");
			for (PolicySet ps: policySets)
    		{
				resp.setContentType("text/xml");
				XACMLGenerator xmlFile = new XACMLGenerator(svc);
				
				xmlFile.generate (resp.getOutputStream(), ps);
    		}
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
    }
   
}
