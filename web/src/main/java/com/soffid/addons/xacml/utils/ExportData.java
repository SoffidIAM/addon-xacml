package com.soffid.addons.xacml.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.naming.InitialContext;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Treeitem;

import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;

import es.caib.zkib.component.DataTree;
import es.caib.zkib.datamodel.xml.XmlDataNode;
import es.caib.zkib.zkiblaf.ImageClic;

public class ExportData extends ImageClic implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File xmltemp;

	public ExportData ()
	{
		super();
	}
	
	public ExportData (String src)
	{
		super(src);
	}
	
	
	public void afterCompose() {
		addEventListener (Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				ExportData target = (ExportData) event.getTarget();
				target.export();
			}
		});
	}

	private void export() throws Exception{
		Path p = new Path("/esquema/lista/treebox");
		DataTree t = (DataTree) p.getComponent();
		Treeitem ti = t.getSelectedItem();
		XmlDataNode selected = (XmlDataNode) ti.getValue();
		PolicySet polset = new PolicySet();
		Policy pol = new Policy();
		if(selected.getInstance() instanceof PolicySet)
			polset = (PolicySet) selected.getInstance();
		else
			pol = (Policy) selected.getInstance();
		
		PolicySetService pss = (PolicySetService) new InitialContext().lookup(PolicySetServiceHome.JNDI_NAME);
	 
		try {
			xmltemp = File.createTempFile("xmltemp", ".xml"); //Crea un fichero temporal tipo xml
			OutputStream out = new FileOutputStream(xmltemp.getPath());
			if(polset.getPolicySetId() != null)
				pss.exportXACMLPolcySet(polset.getPolicySetId(), polset.getVersion(), out);
			else
				pss.exportXACMLPolicy(pol.getPolicyId(), pol.getVersion(), out);
			out.close();
			Filedownload.save(new FileInputStream(xmltemp), "text/xml", 
					"policy-"+((PolicySet)polset).getPolicySetId()+".xml");
			xmltemp.deleteOnExit();
		} catch (Exception e) {
			throw new Exception("Error generating xml file: " + e.getMessage());
		} 
		
	}

}
