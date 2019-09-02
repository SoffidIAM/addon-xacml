package com.soffid.iam.addons.xacml.sync.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.xacml.sunxacml.ctx.Result;
import org.json.JSONArray;
import org.json.JSONObject;

import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.PolicyStatus;
import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.xacml.XacmlServiceLocator;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Password;
import com.soffid.iam.service.AccountService;
import com.soffid.iam.sync.service.SecretStoreService;
import com.soffid.iam.utils.ConfigurationCache;

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
			String folder = req.getParameter("folder");
			if (folder != null)
				service (req, resp, token, folder, null);
			else
				service (req, resp, token, account, system, null);
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
			String account = ob.optString("account");
			String system = ob.optString("system");
			String folder = ob.optString("folder");
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
			if (folder == null)
				service (req, resp, token, account, system, ctx);
			else
				service (req, resp, token, folder, ctx);
				
		} catch (Exception e) {
			log.warn(e);
			throw new ServletException(e);
		}
    	
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp, String token, String account, String system, Map<String, String> ctx) throws Exception {
		PepConfiguration cfg;
		JSONObject result = new JSONObject();
		result.put("status", "");
		result.put("cause", "");
		result.put("password", "");
		try {
			cfg = new PolicyManager().getCurrentPolicy();
			if (cfg.getVaultPolicy().isEnabled())
			{
				Map<String, Object> data = new TokenVerifier().verify( token );
				PolicyStatus ps = cfg.getVaultPolicy();
				Set<Result> r = new XACMLEngine().testAccount(ps, req.getRemoteAddr(), data, account, system, ctx);
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


	private String getPassword(String account, String system) throws InternalErrorException, Exception {
		Account acc = accountService.findAccount(account, system);
		if (acc == null)
			return null;
		Password password = secretStore.getPassword(acc.getId());
		byte[] bytes = password.getPassword().getBytes("UTF-8");
		String encryptKey = ConfigurationCache.getProperty("xacml.password.encryptionKey");
		if (encryptKey != null)
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			final byte[] digestOfPassword = md.digest(encryptKey
	                .getBytes("utf-8"));
	        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
	                    for (int j = 0,  k = 16; j < 8;)
            {
                keyBytes[k++] = keyBytes[j++];
            }

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            final byte[] cipherText = cipher.doFinal(bytes);
            final String encodedCipherText = Base64.encodeBytes(cipherText, Base64.DONT_BREAK_LINES);

            return encodedCipherText;    			
		}
		return es.caib.seycon.util.Base64.encodeBytes(bytes, Base64.DONT_BREAK_LINES);
	}

    protected void service(HttpServletRequest req, HttpServletResponse resp, String token, String folder, Map<String, String> ctx) throws Exception {
		PepConfiguration cfg;
		JSONObject result = new JSONObject();
		result.put("status", "");
		result.put("cause", "");
		result.put("password", "");
		try {
			cfg = new PolicyManager().getCurrentPolicy();
			if (cfg.getVaultPolicy().isEnabled())
			{
				Map<String, Object> data = new TokenVerifier().verify( token );
				PolicyStatus ps = cfg.getVaultPolicy();

				Collection<Account> accounts = XacmlServiceLocator.instance().getPolicySetService().findFolderAccounts(folder);
			
				if (accounts == null)
				{
					result.put("status", "denied");
					result.put("cause", "Unable to find folder "+folder);
				}
				else
				{
					
					JSONArray array = new JSONArray();
					result.put("status", "success");
					result.put("data", array);
					
					for  (Account account: accounts) 
					{
						JSONObject accountResult = new JSONObject();
						array.put(accountResult);
						accountResult.put("account", account.getName());
						accountResult.put("system", account.getSystem());
						Set<Result> r = new XACMLEngine().testAccount(ps, req.getRemoteAddr(), data, account.getName(), account.getSystem(), ctx);
						if (r == null)
						{
							accountResult.put("status", "denied");
							accountResult.put("cause", "No policy applies to this resource and method");
						}
						else
						{
							for (Result rr: r)
							{
								int decision = rr.getDecision() ;
								if (decision == Result.DECISION_INDETERMINATE || decision == Result.DECISION_NOT_APPLICABLE)
								{
									accountResult.put("status", "deny");
									accountResult.put("cause", "Cannot take decision");
									if (rr.getStatus() != null && rr.getStatus().getMessage() != null)
										accountResult.put("message", "Cannot take decision: "+ rr.getStatus().getMessage());
								}
								else if (decision == Result.DECISION_PERMIT)
								{
									accountResult.put("status", "accept");
									accountResult.put("message", rr.getStatus().getMessage());
									accountResult.put("password", getPassword(account.getName(),account.getSystem()));
								}
								else
								{
									accountResult.put("status", "deny");
									accountResult.put("cause", "Access denied");
									if (rr.getStatus() != null && rr.getStatus().getMessage() != null)
										result.put("message", "Cannot take decision: "+ rr.getStatus().getMessage());
								}
							}
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


}
