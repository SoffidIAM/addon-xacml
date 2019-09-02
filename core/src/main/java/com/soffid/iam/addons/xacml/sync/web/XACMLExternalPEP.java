package com.soffid.iam.addons.xacml.sync.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
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

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.PolicyStatus;

public class XACMLExternalPEP extends HttpServlet{
	Log log = LogFactory.getLog(getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
    public void init() throws ServletException {
        super.init();
        
    }

    
    public XACMLExternalPEP() {
        super();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	try {
			String token = req.getParameter("token");
			String method = req.getParameter("method");
			String resource = req.getParameter("resource");
			service (req, resp, token, method, resource, null);
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
			String method = ob.getString("method");
			String resource = ob.getString("resource");
			JSONObject params = ob.optJSONObject("params");
			Map<String,String> ctx = new HashMap<String, String>();
			if (params != null)
			{
				for (Iterator<String> it = params.keys(); it.hasNext();)
				{
					String key = it.next();
					String value = params.optString(key);
					ctx.put(key, value);
				}
			}
			service (req, resp, token, method, resource, ctx);
		} catch (Exception e) {
			log.warn(e);
			throw new ServletException(e);
		}
    	
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp, String token, String method, String resource, Map<String, String> ctx) throws Exception {
		PepConfiguration cfg;
		JSONObject result = new JSONObject();
		try {
			cfg = new PolicyManager().getCurrentPolicy();
			if (cfg.getExternalPolicy().isEnabled())
			{
				Map<String, Object> data;
				try {
					data = new TokenVerifier().verify( token );
				} catch (JWTVerificationException e) {
					throw new Exception(e.getMessage());
				}
				PolicyStatus ps = cfg.getExternalPolicy();
				Set<Result> r = new XACMLEngine().testResource(ps, req.getRemoteAddr(), data, resource, method, ctx);
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
							result.put("cause", "");
							result.put("message", rr.getStatus().getMessage());
							if (rr.getStatus() != null && rr.getStatus().getMessage() != null)
								result.put("message", "Cannot take decision: "+ rr.getStatus().getMessage());
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
				result.put("cause", "External policy is disabled");
			}
		} catch (Exception e) {
			log.warn("Error validating token", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.put("status", "error");
			result.put("cause", e.toString());
		}
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.print(result.toString());
    }
}
