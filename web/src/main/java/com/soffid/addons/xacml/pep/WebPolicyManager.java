package com.soffid.addons.xacml.pep;

import java.util.Random;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zul.Box;
import org.zkoss.zul.Label;

import es.caib.seycon.ng.exception.InternalErrorException;

public class WebPolicyManager {
	public final String COOKIE_NAME = "Soffid-XACML-PolicySet-Test"; //$NON-NLS-1$
	private static String random = null;

	private static PepConfiguration testPolicy = null;
	
	public PepConfiguration getCurrentPolicy() throws NamingException,
			CreateException, InternalErrorException {
		return getCurrentPolicy((HttpServletRequest) Executions.getCurrent()
				.getNativeRequest());
	}

	public PepConfiguration getCurrentPolicyRW() throws NamingException,
	CreateException, InternalErrorException {
		PepConfiguration pc = getCurrentPolicy();
		return new PepConfiguration(pc);
	}

	public PepConfiguration getCurrentPolicy(HttpServletRequest request)
			throws NamingException, CreateException, InternalErrorException {
		if (request.getCookies() != null)
		{
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(COOKIE_NAME))
				{
					PepConfiguration ps;
					ps = parseCookie(cookie);
					if (ps != null)
						return ps;
				}
			}
		}
		
		return new PolicyManager().getCurrentPolicy();
	}

	private PepConfiguration parseCookie(Cookie cookie) {
		if (cookie.getValue().equals(random))
			return testPolicy;
		else
			return null;
	}

	public void apply(PepConfiguration pc) throws InternalErrorException, NamingException, CreateException {
		if (pc.isTesting()) {
			new PolicyManager().apply(pc);
			testPolicy = pc;
			StringBuffer b = new StringBuffer ();
			Random r = new Random(System.currentTimeMillis());
			for (int i = 0; i < 50; i++)
			{
				int n = r.nextInt(62);
				if ( n < 10)	
					b.append ((char) (n + '0'));
				else if ( n < 36)
					b.append ((char) (n + 'a'  - 10));
				else 
					b.append ((char) (n + 'A'  - 36));
			}
			random = b.toString();
			HttpServletResponse response = (HttpServletResponse) Executions
					.getCurrent().getNativeResponse();
			Cookie cookie = new Cookie(COOKIE_NAME, random);
			response.addCookie(cookie);
			generateMessage(pc);
		} else {
			new PolicyManager().apply(pc);
			testPolicy = null;
			// Remove current cookie (if any)
			HttpServletResponse response = (HttpServletResponse) Executions
					.getCurrent().getNativeResponse();
			Cookie cookie = new Cookie(COOKIE_NAME, null);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
	
	public void generateMessage() throws NamingException, CreateException, InternalErrorException
	{
		generateMessage (getCurrentPolicy());
	}
	
	protected void generateMessage(PepConfiguration pc) throws NamingException, CreateException, InternalErrorException
	{
		Box box = (Box) Path.getComponent("//index/xacmltesting"); //$NON-NLS-1$
		Label label = (Label) Path.getComponent("//index/xacmltestinglabel"); //$NON-NLS-1$
		
		if (pc != null && pc.isTesting())
		{
			box.setVisible(true);
			StringBuffer b = new StringBuffer("Texting XACML"); //$NON-NLS-1$
			if (pc.getWebPolicy().isEnabled())
				b.append ("Web Policy Set ") //$NON-NLS-1$
					.append (pc.getWebPolicy().getPolicyId())
					.append (": ")
					.append (pc.getWebPolicy().getPolicyVersion())
					.append (" ");
			if (pc.getRolePolicy().isEnabled())
				b.append ("Dynamic role Policy Set ") //$NON-NLS-1$
					.append (pc.getRolePolicy().getPolicyId())
					.append (": ")
					.append (pc.getRolePolicy().getPolicyVersion())
					.append (" ");
			if (pc.getRolePolicy().isEnabled())
				b.append ("Role Centric Policy Set ") //$NON-NLS-1$
					.append (pc.getAuthPolicy().getPolicyId())
					.append (": ")
					.append (pc.getAuthPolicy().getPolicyVersion())
					.append (" ");
/*			if (pc.getExternalPolicy().isEnabled())
				b.append ("Extenal Policy Set ") //$NON-NLS-1$
					.append (pc.getExternalPolicy().getPolicyId())
					.append (": ")
					.append (pc.getExternalPolicy().getPolicyVersion())
					.append (" ");
			if (pc.getVaultPolicy().isEnabled())
				b.append ("Password vault Policy Set ") //$NON-NLS-1$
					.append (pc.getVaultPolicy().getPolicyId())
					.append (": ")
					.append (pc.getVaultPolicy().getPolicyVersion())
					.append (" ");
*/			label.setValue(b.toString());
		}
		else
			box.setVisible(false);
	}
}
