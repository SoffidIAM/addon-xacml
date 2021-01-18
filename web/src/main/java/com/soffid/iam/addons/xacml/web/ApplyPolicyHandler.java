package com.soffid.iam.addons.xacml.web;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;

import com.soffid.addons.xacml.pep.PepConfiguration;
import com.soffid.addons.xacml.pep.PolicyManager;
import com.soffid.addons.xacml.pep.WebPolicyManager;
import com.soffid.iam.web.component.CustomField3;
import com.soffid.iam.web.component.FrameHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataModel;
import es.caib.zkib.datamodel.DataNode;

public class ApplyPolicyHandler extends FrameHandler implements AfterCompose{
	
	private CustomField3 wc;
	private CustomField3 wp;
	private CustomField3 wv;
	private CustomField3 rc;
	private CustomField3 rp;
	private CustomField3 rv;
	private CustomField3 ac;
	private CustomField3 ap;
	private CustomField3 av;
	private CustomField3 vc;
	private CustomField3 vp;
	private CustomField3 vv;
	private CustomField3 ec;
	private CustomField3 ep;
	private CustomField3 ev;
	private DataModel model;
	private CustomField3 wd;
	private CustomField3 rd;
	private CustomField3 ad;
	private CustomField3 ed;
	private CustomField3 vd;

	public ApplyPolicyHandler() throws InternalErrorException {
		super();
	}

	public void applyPolicy (Event event) throws InternalErrorException, NamingException, CreateException
	{
		PepConfiguration config = (PepConfiguration) ((DataNode) model.getJXPathContext().getValue("/config[1]")).getInstance();
		config.setTesting(false);
		new PolicyManager().apply(config);
	}
	
	public void onCheckWeb ()
	{
		wp.setReadonly(! Boolean.TRUE.equals( wc.getValue())); 
		wv.setReadonly(! Boolean.TRUE.equals( wc.getValue()));
		wd.setReadonly(! Boolean.TRUE.equals( wc.getValue()));
	}
	
	public void onCheckRole ()
	{
		rp.setReadonly(! Boolean.TRUE.equals( rc.getValue())); 
		rv.setReadonly(! Boolean.TRUE.equals( rc.getValue()));
		rd.setReadonly(! Boolean.TRUE.equals( rc.getValue()));
	}
	
	public void onCheckAuth ()
	{
		ap.setReadonly(! Boolean.TRUE.equals( ac.getValue())); 
		av.setReadonly(! Boolean.TRUE.equals( ac.getValue()));
		ad.setReadonly(! Boolean.TRUE.equals( ac.getValue()));
	}
	
	public void onCheckExternal ()
	{
		ep.setReadonly(! Boolean.TRUE.equals( ec.getValue())); 
		ev.setReadonly(! Boolean.TRUE.equals( ec.getValue()));
		ed.setReadonly(! Boolean.TRUE.equals( ec.getValue()));
	}
	
	public void onCheckVault ()
	{
		vp.setReadonly(! Boolean.TRUE.equals( vc.getValue())); 
		vv.setReadonly(! Boolean.TRUE.equals( vc.getValue()));
		vd.setReadonly(! Boolean.TRUE.equals( vc.getValue()));
	}
	
	public void applyTest (Event event) throws InternalErrorException, NamingException, CreateException
	{
		PepConfiguration config = (PepConfiguration) ((DataNode) model.getJXPathContext().getValue("/config[1]")).getInstance();
		config.setTesting(true);
		new WebPolicyManager().apply(config);
		es.caib.zkib.zkiblaf.Missatgebox.avis("A cookie has been sent to your browser to enforce XACML rules for your browser.\nRemove it or restart console to enable default behaviour.\nMind only one user can test XACML policies at a time");
	}

	@Override
	public void afterCompose() {
		super.afterCompose();
		
		model=getModel();
		
		wc = (CustomField3) getFellow("wc");
		wp = (CustomField3) getFellow("wp");
		wv = (CustomField3) getFellow("wv");
		wd = (CustomField3) getFellow("wd");

		rc = (CustomField3) getFellow("rc");
		rp = (CustomField3) getFellow("rp");
		rv = (CustomField3) getFellow("rv");
		rd = (CustomField3) getFellow("rd");

		ac = (CustomField3) getFellow("ac");
		ap = (CustomField3) getFellow("ap");
		av = (CustomField3) getFellow("av");
		ad = (CustomField3) getFellow("ad");

		ec = (CustomField3) getFellow("ec");
		ep = (CustomField3) getFellow("ep");
		ev = (CustomField3) getFellow("ev");
		ed = (CustomField3) getFellow("ed");

		vc = (CustomField3) getFellow("vc");
		vp = (CustomField3) getFellow("vp");
		vv = (CustomField3) getFellow("vv");
		vd = (CustomField3) getFellow("vd");
		
	    CustomField3 el = (CustomField3) getFellow("el");
	    CustomField3 vl = (CustomField3) getFellow("vl");
		try {
			for ( com.soffid.iam.api.Server server: com.soffid.iam.EJBLocator.getDispatcherService().findAllServers())
			{
				if (server.getType() == es.caib.seycon.ng.comu.ServerType.MASTERSERVER)
				{
					el.setLabel(el.getLabel()+"( "+server.getUrl()+"/XACML/pep )");
					vl.setLabel(vl.getLabel()+"( "+server.getUrl()+"/XACML/vault )");
					break;						
				}
			}
		} catch (InternalErrorException | NamingException | CreateException e) {
		}

		onCheckAuth();
		onCheckExternal();
		onCheckRole();
		onCheckVault();
		onCheckWeb();
	}

}
