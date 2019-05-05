package com.soffid.iam.addons.xacml.sync.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.xacml.sunxacml.ctx.Result;
import org.json.JSONObject;

import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.ServiceLocator;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Password;
import com.soffid.iam.service.AccountService;
import com.soffid.iam.sync.service.SecretStoreService;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.util.Base64;

public class XACMLPasswordVault extends HttpServlet{
	Log log = LogFactory.getLog(getClass());
	final static SecretStoreService secretStore = ServiceLocator.instance().getSecretStoreService();
	final static AccountService accountService = ServiceLocator.instance().getAccountService();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
    public void init() throws ServletException {
        super.init();
        
    }

    
    public XACMLPasswordVault() {
        super();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	try {
			String token = req.getParameter("token");
			String account = req.getParameter("account");
			String system = req.getParameter("system");
			service (req, resp, token, account, system);
		} catch (Exception e) {
			log.warn(e);
			throw new ServletException(e);
		}
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	try {
			BufferedReader reader = req.getReader();
			int read;
			StringBuffer sb = new StringBuffer();
			while ( (read = reader.read()) >= 0)
				sb.append( (char) read);
			JSONObject ob = new JSONObject(sb.toString());
			String token = ob.getString("token");
			String account = ob.getString("account");
			String system = ob.getString("system");
			service (req, resp, token, account, system);
		} catch (Exception e) {
			log.warn(e);
			throw new ServletException(e);
		}
    	
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp, String token, String account, String system) throws Exception {
		PepConfiguration cfg;
		JSONObject result = new JSONObject();
		try {
			cfg = new PolicyManager().getCurrentPolicy();
			if (cfg.getVaultPolicy().isEnabled())
			{
				Map<String, Object> data = new TokenVerifier().verify( token );
				PolicyStatus ps = cfg.getVaultPolicy();
				Set<Result> r = new XACMLEngine().testAccount(ps, req.getRemoteAddr(), data, account, system);
				if (r == null)
				{
					result.put("status", "denied");
					result.put("cause", "No policy applies to this resource and method");
				}
				else
				{
					for (Result rr: r)
					{
						int decision = rr.getDecision() ;
						if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
						{
							result.put("status", "deny");
							result.put("cause", "Cannot take decision");
							if (rr.getStatus() != null && rr.getStatus().getMessage() != null)
								result.put("message", "Cannot take decision: "+ rr.getStatus().getMessage());
						}
						else if (decision == Result.DECISION_PERMIT)
						{
							result.put("status", "accept");
							result.put("message", rr.getStatus().getMessage());
							result.put("password", getPassword(account,system));
						}
						else
						{
							result.put("status", "deny");
							result.put("cause", "Access denied");
							if (rr.getStatus() != null && rr.getStatus().getMessage() != null)
								result.put("message", "Cannot take decision: "+ rr.getStatus().getMessage());
						}
					}
				}
			}
			else
			{
				result.put("status", "denied");
				result.put("cause", "Vault policy is disabled");
			}
		} catch (Exception e) {
			log.warn(e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.put("status", "error");
			result.put("cause", e.toString());
		}
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.print(result.toString());
    }


	private String getPassword(String account, String system) throws InternalErrorException, UnsupportedEncodingException {
		Account acc = accountService.findAccount(account, system);
		if (acc == null)
			return null;
		Password password = secretStore.getPassword(acc.getId());
		return es.caib.seycon.util.Base64.encodeBytes(password.getPassword().getBytes("UTF-8"), Base64.DONT_BREAK_LINES);
	}

}
