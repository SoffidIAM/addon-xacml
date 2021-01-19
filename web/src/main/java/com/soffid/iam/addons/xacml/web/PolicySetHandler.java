package com.soffid.iam.addons.xacml.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Treeitem;

import com.soffid.iam.addons.xacml.common.IdReference;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;
import com.soffid.iam.addons.xacml.common.Target;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetService;
import com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome;
import com.soffid.iam.web.component.FrameHandler;
import com.soffid.iam.web.popup.FileUpload2;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTree;
import es.caib.zkib.component.DataTree2;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datamodel.DataNodeCollection;
import es.caib.zkib.datamodel.xml.XmlDataNode;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.jxpath.JXPathContext;

public class PolicySetHandler extends FrameHandler {
	
	public PolicySetHandler() throws InternalErrorException {
		super();
	}


	
	public void addNew()
	{
		int num = 1;
		DataNodeCollection c = (DataNodeCollection) XPathUtils.eval(getModel(), "/world[1]/policySet");
		for (int i = 0; i < c.getSize(); i++) {
			DataNode dataNode = (DataNode) c.getDataModel(i);
			if (dataNode != null && !dataNode.isDeleted()) {
				PolicySet ps = (PolicySet) dataNode.getInstance();
				if (ps != null && ps.getOrder() != null &&
					ps.getOrder().intValue() >= num)
					num = ps.getOrder().intValue() + 1;
			}
		}

		Target t = new Target();
		t.setActionMatch(new LinkedList<>());
		t.setEnvironmentMatch(new LinkedList<>());
		t.setResourceMatch(new LinkedList<>());
		t.setSubjectMatch(new LinkedList<>());
		
		PolicySet child = new PolicySet();
		child.setTarget(new LinkedList<>());
		child.getTarget().add(t);
		child.setOrder(num);
		
		DataTree2 tree = (DataTree2) getListbox();
		tree.setSelectedIndex(new int[0]);
		tree.addNew("/policySet", child);
		showDetails();
	}



	private void createMasterPolicySet(Object[] obj) throws CommitException {
		DataTree2 treebox = (DataTree2) getListbox();
		Long parent = null;
		treebox.setSelectedIndex(new int[0]);

		int num = 1;
		DataNodeCollection c = (DataNodeCollection) getModel().getJXPathContext().getValue("/world[1]/policySet");
		for (int i = 0; i < c.getSize(); i++) {
			PolicySet ps = (PolicySet) ((DataNode) c.getDataModel(i)).getInstance();
			if (ps != null && ps.getOrder() != null &&
				ps.getOrder().intValue() >= num)
				num = ps.getOrder().intValue() + 1;
		}
		
		String path = treebox.addNew("/policySet");
		JXPathContext ctx = getModel().getJXPathContext();

		ctx.setValue(path+"/@policySetId", obj[0].toString().replace(" ", ""));
		ctx.setValue(path+"/@description", obj[1]);
		ctx.setValue(path+"/@version", obj[2]);
		ctx.setValue(path+"/@policyCombiningAlgId", obj[3]);
		ctx.setValue(path+"/@order", num);
		ctx.setValue(path+"/@parentPolicySet", parent);

		getModel().commit();
		showDetails();
	}



	@Override
	public void onChangeForm(Event ev) throws Exception {
		super.onChangeForm(ev);
		if (((DataTree2)getListbox()).getSelectedItem().length > 0) {
			Object o = XPathUtils.eval(getListbox(), "/instance");
			getFellow("v_ps").setVisible(o != null && o instanceof PolicySet);
			getFellow("v_po").setVisible(o != null && o instanceof Policy);
			getFellow("v_reference").setVisible(o != null && o instanceof IdReference);
			if (o != null && o instanceof IdReference) {
				Radiogroup rg = (Radiogroup) getFellow("radio_version");
				if (XPathUtils.eval(getListbox(), "version") != null)
					rg.setSelectedIndex(0);
				else if (XPathUtils.eval(getListbox(), "earliestVersion") != null ||
						XPathUtils.eval(getListbox(), "latestVersion") != null)
					rg.setSelectedIndex(1);
				else
					rg.setSelectedIndex(2);
				updateRadioVersion(rg);
			}
		}
	}		
	
	
/*	void onCreateNewPolicies(Object []obj){
		int i = obj.length;
		
		PolicySet actual = ((DataNode) XPathUtils.getValue(getForm(), ".")).getInstance();
		es.caib.zkib.binder.BindContext bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
				arbre.getSelectedItem());
		
		int number = com.soffid.addons.xacml.utils.Sort.calculaOrdre(bindCtx, actual);
		
		if(obj[i-1].equals("policySet")){
			Long parent = actual.getId();
			treebox.addNew("/policySet");
			bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					treebox.getSelectedItem());
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@policySetId", obj[0].toString().replace(" ", ""));
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@description", obj[1]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@version", obj[2]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@policyCombiningAlgId", obj[3]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@order", number + 1);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@parentPolicySet", parent);
			
		}else if(obj[i-1].equals("policy")){
			treebox.addNew("/policy");
			bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					treebox.getSelectedItem());
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@policyId", obj[0].toString().replace(" ", ""));
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@description", obj[1]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@version", obj[2]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@ruleCombiningAlgId", obj[3]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@order", number + 1); 
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@policySetId", actual.getId());
			
		}else if(obj[i-1].equals("policySetIdRef")){
			Long idrefPolicySet = actual.getId();
			treebox.addNew("/policySetIdRef");
			bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					treebox.getSelectedItem());
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@idReferenceTypeValue", obj[0].toString().replace(" ", ""));
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@earliestVersion", obj[1]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@latestVersion", obj[2]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@version", obj[3]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@order", number + 1);
			
		}else if(obj[i-1].equals("policyIdRef")){
			Long idrefPolicy = actual.getId();
			treebox.addNew("/policyIdRef");
			bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					treebox.getSelectedItem());
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@idReferenceTypeValue", obj[0].toString().replace(" ", ""));
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@earliestVersion", obj[1]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@latestVersion", obj[2]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@version", obj[3]);
			es.caib.zkib.datasource.XPathUtils.setValue(bindCtx, "@order", number + 1);
		}
	}
	
	
	void recalculaOrdre()
	{
		Component arbre = esquema.getFellow("lista").getFellow("treebox");
		Object obj = arbre.getSelectedItem().getValue().getInstance();
		if(obj instanceof PolicySet || obj instanceof Policy || obj instanceof PolicySetIdReference || obj
				instanceof PolicyIdReference)
		{
			Treeitem parent = arbre.getSelectedItem().getParent().getParent();

			Treechildren children = parent.getTreechildren();
			List brothers = children.getChildren();
			int order = 1;
			for (int i = 0; i < brothers.size(); i ++)
			{
				Treeitem item = brothers.get(i);
				if (item != arbre.getSelectedItem())
				{
					if (item.getValue().getInstance().getOrder() != order) 
					{
						item.getValue().getInstance().setOrder(order);
						brothers.get(i).getValue().update();
					}
					order ++;
				}
			}	
		}
	}
	
	
	void createNew(Component grid){
		es.caib.zkib.binder.BindContext ctx = 
				es.caib.zkib.datasource.XPathUtils.getComponentContext(grid);
		es.caib.zkib.datasource.XPathUtils.getValue(ctx,".").newInstance();
		int last = grid.getRows().getChildren().size();
		Component lastPanel = grid.getRows().getChildren().get(last-1);
		lastPanel.getChildren().get(1).getChildren().get(0).getChildren().get(0)
			.setSrc("~./img/fletxa-baix.gif");
		lastPanel.getChildren().get(1).getChildren().get(1).setVisible(true);
	}
	
	
	void createNewRule(Component grid){
		es.caib.zkib.binder.BindContext ctx = 
				es.caib.zkib.datasource.XPathUtils.getComponentContext(grid);
		es.caib.zkib.datasource.XPathUtils.getValue(ctx,".").newInstance();
		
		//CALCUL DE L'ORDRE (Orden els rules només dins cada policy per nombre de rules que hi ha).
		int number = grid.getRows().getChildren().size();
		ctx = es.caib.zkib.datasource.XPathUtils.getComponentContext(grid.getRows().getChildren().get(number-1));
		es.caib.zkib.datasource.XPathUtils.setValue(ctx, "@order", number);
		es.caib.zkib.datasource.XPathUtils.setValue(ctx, "@policyId", po_detall_tipus.getFellow("dada").getValue());
		int last = grid.getRows().getChildren().size();
		Component lastPanel = grid.getRows().getChildren().get(last - 1);
		lastPanel.getChildren().get(1).getChildren().get(0).getChildren().get(0)
			.setSrc("~./img/fletxa-baix.gif");
		lastPanel.getChildren().get(1).getChildren().get(1).setVisible(true);
		String result = es.caib.zkib.datasource.XPathUtils.createPath (
				ctx.getDataSource(),
				ctx.getXPath()+"/target");
	}
	
	
	void removeDataRow(Component r){
		es.caib.zkib.binder.BindContext ctx = es.caib.zkib.datasource.XPathUtils
				.getComponentContext(r.getParent());
		
		Missatgebox.confirmaOK_CANCEL(org.zkoss.util.resource.Labels.getLabel("policySet.ConfirmDelete"),
				org.zkoss.util.resource.Labels.getLabel("policySet.Delete"), new EventListener() {
			public void onEvent(Event evt) {
				if ("onOK".equals(evt.getName())) {
					es.caib.zkib.datasource.XPathUtils.removePath(ctx.getDataSource(),
							ctx.getXPath());
				}
			}
		});
	}
	
	void removeSubExpression(Image rem){
		com.soffid.addons.xacml.utils.XACMLExpressionComponent c = rem.getParent().getChildren().get(1);
		es.caib.zkib.binder.BindContext ctx = es.caib.zkib.datasource.XPathUtils
				.getComponentContext(c);
		Integer ordre = c.getOrder();
		Missatgebox.confirmaOK_CANCEL(org.zkoss.util.resource.Labels.getLabel("policySet.ConfirmDelete"),
				org.zkoss.util.resource.Labels.getLabel("policySet.Delete"), new EventListener() {
				public void onEvent(Event evt) {
					if ("onOK".equals(evt.getName())) {
						es.caib.zkib.datasource.XPathUtils.removePath(ctx.getDataSource(),
								ctx.getXPath());
						Component pare = c.getParent();
						while(!(pare instanceof com.soffid.addons.xacml.utils.XACMLExpressionComponent))
						{
							pare = pare.getParent();
							if (pare == null) return;
						}
						com.soffid.addons.xacml.utils.XACMLExpressionComponent p = pare;
						p.drawExpression();
						p.markAsModified();
						com.soffid.addons.xacml.utils.Sort.modifyOrderSubExpression(p, ordre);
					}
				}
			});
	}
	
	
	void createNewExpression(Component grid){
		
		es.caib.zkib.binder.BindContext ctx = 
				es.caib.zkib.datasource.XPathUtils.getComponentContext(grid);
		es.caib.zkib.datasource.XPathUtils.getValue(ctx,".").newInstance();
		int number = grid.getRows().getChildren().size();
		ctx = es.caib.zkib.datasource.XPathUtils.getComponentContext(grid.getRows().getChildren().get(number-1));
		com.soffid.iam.addons.xacml.common.Expression exp = new com.soffid.iam.addons.xacml.common.Expression();
		
		String usuari = Executions.getCurrent().getUserPrincipal().getName();
		exp.setAttributeValue(usuari); //Passar el codi d'usuari
		exp.setDataTypeAttributeValue(DataType.STRING_TYPE);
		exp.setExpressionType("subject");
		exp.setAttributeDesignator("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		exp.setDataTypeAttributeDesignator(DataType.STRING_TYPE);
		exp.setOrder(1);
		exp.setName(com.soffid.iam.addons.xacml.common.FunctionEnumeration.fromString(java.lang.String.valueOf("STRING_EQUAL")));  //Function tipus true
		es.caib.zkib.datasource.XPathUtils.setValue(ctx,"@expression", exp);
		number = grid.getRows().getChildren().size();
		ctx = es.caib.zkib.datasource.XPathUtils.getComponentContext(grid.getRows().getChildren().get(number-1));
		
		Component lastPanel = grid.getRows().getChildren().get(number-1);
		lastPanel.getChildren().get(1).getChildren().get(0).getChildren().get(0)
			.setSrc("~./img/fletxa-baix.gif");
		lastPanel.getChildren().get(1).getChildren().get(1).setVisible(true);
	}
	
	void createNewExpressionCondition(Component r){
		Component pare = r.getParent().getParent();
		Component grid = pare.getChildren().get(6).getChildren().get(0);
		
		createNewExpression(grid);
	}
	
	
	void selectVersion(Radio r){
		String valor = r.getValue();
		if(valor.equals("range")){
			version.setValue("");
			version.setDisabled(true);
			rangeVersion.setDisabled(false);
			rangeVersion2.setDisabled(false);
		}else if(valor.equals("anyVersion")){
			version.setValue("");
			rangeVersion.setValue("");
			rangeVersion2.setValue("");
			version.setDisabled(true);
			rangeVersion.setDisabled(true);
			rangeVersion2.setDisabled(true);
		}else{
			version.setDisabled(false);
			rangeVersion.setValue("");
			rangeVersion2.setValue("");
			rangeVersion.setDisabled(true);
			rangeVersion2.setDisabled(true);
		}
	}
	
	
	void selectAttributeType(Radio r){
		Radiogroup rg = r.getRadiogroup();
		int i = rg.getSelectedIndex();
		Hbox h = rg.getParent();
		Hbox designator = h.getChildren().get(0).getChildren().get(0).getChildren().get(0);
		Hbox selector = h.getChildren().get(0).getChildren().get(0).getChildren().get(1);
		if(i==0){
			selector.getChildren().get(2).setValue("");
			selector.getChildren().get(2).setVisible(false);
			selector.getChildren().get(4).setVisible(false);
			selector.getChildren().get(5).setVisible(false);
			selector.getChildren().get(6).setVisible(false);
			designator.getChildren().get(2).setVisible(true);
			designator.getChildren().get(4).setVisible(true);
			designator.getChildren().get(5).setVisible(true);
			designator.getChildren().get(6).setVisible(true);
			designator.getChildren().get(2).setSelectedIndex(1);
			selector.setWidths("8%, *");
			designator.setWidths("8%, 22%, 30%, 10%, 15%, *");
		}else{
			designator.getChildren().get(2).setSelectedIndex(0);
			designator.getChildren().get(2).setVisible(false);
			designator.getChildren().get(4).setVisible(false);
			designator.getChildren().get(5).setVisible(false);
			designator.getChildren().get(6).setVisible(false);
			selector.getChildren().get(2).setVisible(true);
			selector.getChildren().get(4).setVisible(true);
			selector.getChildren().get(5).setVisible(true);
			selector.getChildren().get(6).setVisible(true);
			designator.setWidths("8%, *");
			selector.setWidths("8%, 22%, 30%, 10%, 15%, *");
		}
	}
	
	
	void setDataType(Listbox l){
		Hbox h = l.getParent();
		h.getChildren().get(5).setSelectedIndex(1);
		Vbox v = h.getParent();
		v.getChildren().get(5).getChildren().get(6).setSelectedIndex(1);
	}
	
	
	void createNewSubExpression(Component image)
	{
		Component comp = image.getParent();
		while(!(comp instanceof com.soffid.addons.xacml.utils.XACMLExpressionComponent))
			comp = comp.getParent();
		com.soffid.addons.xacml.utils.XACMLExpressionComponent c = comp;
		
		com.soffid.iam.addons.xacml.common.Expression exp = new com.soffid.iam.addons.xacml.common.Expression();
		String usuari = Executions.getCurrent().getUserPrincipal().getName();
		exp.setAttributeValue(usuari); //Passar el codi d'usuari
		exp.setDataTypeAttributeValue(DataType.STRING_TYPE);
		exp.setExpressionType("subject");
		exp.setAttributeDesignator("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		exp.setDataTypeAttributeDesignator(DataType.STRING_TYPE);
		exp.setName(com.soffid.iam.addons.xacml.common.FunctionEnumeration.fromString(java.lang.String.valueOf("STRING_EQUAL")));  //Function tipus true
		int i = c.getSubexpression().size();
		exp.setOrder(i + 1);
		c.getSubexpression().add(exp);
		c.drawExpression();  //???NO funciona
		c.markAsModified();
	}
	
	
	void addNewVersion()
	{
		Component arbre = esquema.getFellow("lista").getFellow("treebox");
		Object obj = arbre.getSelectedItem().getValue().getInstance();
		es.caib.zkib.binder.BindContext bindCtx = es.caib.zkib.datasource.XPathUtils.getComponentContext(
				arbre.getSelectedItem());
		Treeitem parent = arbre.getSelectedItem().getParent().getParent();
		arbre.setSelectedItem(parent);
		
		if (obj instanceof PolicySet)
		{
			es.caib.zkib.binder.BindContext bindCtxNou = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					arbre.getSelectedItem());
			String newPath = com.soffid.addons.xacml.utils.Duplicator.duplicatePolicySet(bindCtx.getDataSource()
					, bindCtx.getXPath(), bindCtxNou.getXPath());
		}
		else
		{ 
			es.caib.zkib.binder.BindContext bindCtxNou = es.caib.zkib.datasource.XPathUtils.getComponentContext(
					arbre.getSelectedItem());
			String newPath = com.soffid.addons.xacml.utils.Duplicator.duplicatePolicy(bindCtx.getDataSource()
					, bindCtx.getXPath(), bindCtxNou.getXPath());
		}
	}
	
	void noEmpty(Textbox tb){
		String value = tb.getValue();
		if(value == null || value.isEmpty()){
			Hbox hb = tb.getParent();
			Radio r = hb.getChildren().get(0);
			if(r.isChecked()){
				 Radiogroup rg = r.getRadiogroup();
				 rg.setSelectedIndex(2);
			}
		}
	}
	
	void onRefrescaArbreIntern(boolean checkCommit)
	{
		String xpath_arbre  = null;
		String xpath_arbre_intern = null;
		
		// Check pending changes
		if (checkCommit && esquema.isCommitPending())
		{
			Missatgebox.avis (org.zkoss.util.resource
					.Labels.getLabel("xacml_policySet"));
			return;
		}
		
		treebox = esquema.getFellow("lista").getFellow("treebox");
		
		if (treebox.getSelectedItem() != null)
		{
			xpath_arbre = treebox.getSelectedItem().getValue().getXPath();
		}
		
		// Refresquem el contingut
		model.getJXPathContext().getValue("/").refresh();
		treebox.setDataPath("/model:/");
		
		// I tornem a obrir les branques als dos arbres
		if (xpath_arbre != null)
		{
			treebox.obreBrancaByXpath(xpath_arbre);
		}
		
		hiddeButtons();
	}
	
	void showCancelButton()
	{
		b_cancel = esquema.getFellow("lista").getFellow("b_cancel");
		b_cancel.setVisible(true);
	}
	
	void hiddeButtons()
	{
		btcommit = esquema.getFellow("lista").getFellow("btcommit");
		b_cancel = esquema.getFellow("lista").getFellow("b_cancel");
		
		btcommit.setVisible(false);
		b_cancel.setVisible(false);
	}


	@Override
	public void showDetails() {
		Tree tx = esquema.getFellow("lista").getFellow("treebox");
		Treeitem selected = tx.getSelectedItem();
		if (selected.getLevel() > 0)
			esquema.showFormulari();
		else 
		{
			esquema.hideFormulari();
		}
		super.showDetails();
	}

	void onChangeDades()
	{
		try {	
			es.caib.zkib.datasource.DataSource ds = form.getDataSource(); 
			es.caib.zkib.jxpath.JXPathContext ctx =  ds.getJXPathContext(); 
			Object registre = ctx.getValue("/");						
			b_inserir_ps = esquema.getFellow("lista").getFellow("b_inserir_ps");
			b_esborrar = esquema.getFellow("lista").getFellow("b_esborrar");
			Component vbox_ps = esquema.getFellow("dades").getFellow("form").getFellow("v_ps");
			Component vbox_po = esquema.getFellow("dades").getFellow("form").getFellow("v_po");
			Component vbox_reference = esquema.getFellow("dades").getFellow("form").getFellow("v_reference");
			Component vbox_arrelps = esquema.getFellow("dades").getFellow("form").getFellow("v_arrelps");
			
			form.getFellow("detall_policySet").getFellow("dada").setDisabled(!registre.isNew());
			form.getFellow("detall_version").setDisabled(!registre.isNew());
			form.getFellow("po_detall_policy").getFellow("dada").setDisabled(!registre.isNew());
			form.getFellow("id_detall").getFellow("dada").setDisabled(!registre.isNew());
			form.getFellow("detall_versionpolicy").getFellow("dada").setDisabled(!registre.isNew());
			
			treebox = esquema.getFellow("lista").getFellow("treebox");
			Object obj = registre.getInstance();
			
			if ((obj instanceof PolicySet) && !arrel) {
				b_inserir_ps.setDisabled(false);
				b_esborrar.setDisabled(false);
				vbox_ps.setVisible(true);
				vbox_po.setVisible(false);
				vbox_reference.setVisible(false);
				vbox_arrelps.setVisible(false);
				showDetall();
			}else if(obj instanceof Policy){
				b_inserir_ps.setDisabled(true);
				b_esborrar.setDisabled(false);
				vbox_ps.setVisible(false);
				vbox_po.setVisible(true);
				vbox_reference.setVisible(false);
				vbox_arrelps.setVisible(false);
				showDetall();
			}else if(obj instanceof IdReference){
				b_inserir_ps.setDisabled(true); 
				b_esborrar.setDisabled(false);
				vbox_ps.setVisible(false);
				vbox_po.setVisible(false);
				vbox_reference.setVisible(true);
				vbox_arrelps.setVisible(false);
				String versio = vbox_reference.getFellow("version").getValue();
				String range = vbox_reference.getFellow("rangeVersion").getValue();
				if(versio != null && !versio.equals("")){
					vbox_reference.getFellow("radio_version").setSelectedIndex(0);
					vbox_reference.getFellow("version").setDisabled(false);
					vbox_reference.getFellow("rangeVersion").setDisabled(true);
					vbox_reference.getFellow("rangeVersion2").setDisabled(true);
				}else if(range != null && !range.equals("")){
					vbox_reference.getFellow("radio_version").setSelectedIndex(1);
					vbox_reference.getFellow("version").setDisabled(true);
					vbox_reference.getFellow("rangeVersion").setDisabled(false);
					vbox_reference.getFellow("rangeVersion2").setDisabled(false);
				} else{
					vbox_reference.getFellow("radio_version").setSelectedIndex(2);
					vbox_reference.getFellow("version").setDisabled(true);
					vbox_reference.getFellow("rangeVersion").setDisabled(true);
					vbox_reference.getFellow("rangeVersion2").setDisabled(true);
				}
				showDetall();
			}else if(arrel){
				if(registre.isNew())
				{
					vbox_arrelps.getFellow("ps_vers").setValue("1");
					Listbox l = vbox_arrelps.getFellow("lbPolicyCombiningAlgorithm2");
					l.setSelectedIndex(0);
				}
				//vbox_ps.setVisible(false);
				vbox_ps.setVisible(true);
				vbox_po.setVisible(false);
				vbox_reference.setVisible(false);
				//vbox_arrelps.setVisible(true);
				vbox_arrelps.setVisible(false);
				arrel=false;
				showDetall();
			}else{
				b_inserir_ps.setDisabled(false); 
				b_esborrar.setDisabled(true);
				vbox_ps.setVisible(false);
				vbox_po.setVisible(false);
				vbox_reference.setVisible(false);
				vbox_arrelps.setVisible(false);
				showDetall();
			}
			
		}catch (Exception e) {
			form.getFellow("detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("po_detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("id_detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("id_new_ps").getFellow("dada").setDisabled(true);
			System.out.println("onChangeDadesError");
//			e.printStackTrace();				
		}catch (es.caib.zkib.jxpath.JXPathNotFoundException e) {
			// No active record
			form.getFellow("detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("po_detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("id_detall_tipus").getFellow("dada").setDisabled(true);
			form.getFellow("id_new_ps").getFellow("dada").setDisabled(true);
			System.out.println("onChangeDadesError");
		}
	}

	*/
	public void testPolicySet (Event e) throws CommitException, InternalErrorException, NamingException
	{
		getModel().commit();
		PolicySet ps = (PolicySet) XPathUtils.eval(getListbox(), "instance");
		PolicySetService svc =
				(PolicySetService) new javax.naming.InitialContext()
				.lookup(com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome.JNDI_NAME);
		svc.test(ps);
	}
	
	public void addPolicySet(Event e) {
		int num = 1;
		DataNodeCollection c = (DataNodeCollection) XPathUtils.eval(getListbox(), "/policySet");
		for (int i = 0; i < c.getSize(); i++) {
			PolicySet ps = (PolicySet) ((DataNode) c.getDataModel(i)).getInstance();
			if (ps != null && ps.getOrder() != null &&
				ps.getOrder().intValue() >= num)
				num = ps.getOrder().intValue() + 1;
		}

		Target t = new Target();
		t.setActionMatch(new LinkedList<>());
		t.setEnvironmentMatch(new LinkedList<>());
		t.setResourceMatch(new LinkedList<>());
		t.setSubjectMatch(new LinkedList<>());
		
		PolicySet child = new PolicySet();
		child.setTarget(new LinkedList<>());
		child.getTarget().add(t);
		child.setParentPolicySet((Long) XPathUtils.eval(getForm(), "@id"));
		child.setOrder(num);
		
		DataTree2 tree = (DataTree2) getListbox();
		tree.addNew("/policySet", child);
	}

	public void addPolicy(Event e) {
		int num = 1;
		DataNodeCollection c = (DataNodeCollection) XPathUtils.eval(getListbox(), "/policy");
		for (int i = 0; i < c.getSize(); i++) {
			PolicySet ps = (PolicySet) ((DataNode) c.getDataModel(i)).getInstance();
			if (ps != null && ps.getOrder() != null &&
				ps.getOrder().intValue() >= num)
				num = ps.getOrder().intValue() + 1;
		}

		Target t = new Target();
		t.setActionMatch(new LinkedList<>());
		t.setEnvironmentMatch(new LinkedList<>());
		t.setResourceMatch(new LinkedList<>());
		t.setSubjectMatch(new LinkedList<>());
		
		Policy child = new Policy();
		child.setTarget(new LinkedList<>());
		child.getTarget().add(t);
		child.setPolicySetId((Long) XPathUtils.eval(getForm(), "@id"));
		child.setOrder(num);
		child.setRule(new LinkedList<>());
		child.setVariableDefinition(new LinkedList<>());
		
		DataTree2 tree = (DataTree2) getListbox();
		tree.addNew("/policy", child);
	}

	public void addPolicyId(Event e) {
		int num = 1;
		DataNodeCollection c = (DataNodeCollection) XPathUtils.eval(getListbox(), "/policyIdRef");
		for (int i = 0; i < c.getSize(); i++) {
			DataNode dataNode = (DataNode) c.getDataModel(i);
			if (dataNode != null && ! dataNode.isDeleted()) {
				PolicySet ps = (PolicySet) dataNode.getInstance();
				if (ps != null && ps.getOrder() != null &&
					ps.getOrder().intValue() >= num)
					num = ps.getOrder().intValue() + 1;
			}
		}

		PolicyIdReference child = new PolicyIdReference();
		
		DataTree2 tree = (DataTree2) getListbox();
		tree.addNew("/policyIdRef", child);
		XPathUtils.setValue(tree, "order", num);
		Radiogroup rg = (Radiogroup) getFellow("radio_version");
		rg.setSelectedIndex(2);
		updateRadioVersion(rg);
	}

	public void addPolicySetId(Event e) {
		int num = 1;
		DataNodeCollection c = (DataNodeCollection) XPathUtils.eval(getListbox(), "/policySetIdRef");
		for (int i = 0; i < c.getSize(); i++) {
			PolicySet ps = (PolicySet) ((DataNode) c.getDataModel(i)).getInstance();
			if (ps != null && ps.getOrder() != null &&
				ps.getOrder().intValue() >= num)
				num = ps.getOrder().intValue() + 1;
		}

		PolicySetIdReference child = new PolicySetIdReference();
		
		DataTree2 tree = (DataTree2) getListbox();
		tree.addNew("/policySetIdRef", child);
		XPathUtils.setValue(tree, "order", num);
		Radiogroup rg = (Radiogroup) getFellow("radio_version");
		rg.setSelectedIndex(2);
		updateRadioVersion(rg);
	}
	
	public void updateRadioVersion(Event ev) {
		Radio r = (Radio) ev.getTarget();
		Radiogroup g = r.getRadiogroup();
		updateRadioVersion(g);
	}



	public void updateRadioVersion(Radiogroup g) {
		getFellow("version").setVisible(g.getSelectedIndex() == 0);
		getFellow("rangeVersion").setVisible(g.getSelectedIndex() == 1);
		getFellow("rangeVersion2").setVisible(g.getSelectedIndex() == 1);
		if (g.getSelectedIndex() != 0) {
			((Textbox)getFellow("version")).setValue(null);
		}
		if (g.getSelectedIndex() != 1) {
			((Textbox)getFellow("rangeVersion")).setValue(null);
			((Textbox)getFellow("rangeVersion2")).setValue(null);
		}
	}
	
	public void importPolicySet(Event event){
		FileUpload2.get((event2) -> {
			org.zkoss.util.media.Media m = ((UploadEvent)event2).getMedia();
			if(m!=null){
				org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
				org.dom4j.Document document;
				java.io.InputStream in;
				if (m.inMemory())
				{
					if (m.isBinary())
						in = new java.io.ByteArrayInputStream(m.getByteData());
					else
						in = new java.io.ByteArrayInputStream(m.getStringData().getBytes());
				}
				else
				{
					if (m.isBinary())
						in = m.getStreamData();
					else
						throw new UiException("The uploaded file seems to be text. It should be uploaded as binary");
				}
				javax.naming.Context context = new javax.naming.InitialContext();
				PolicySetService policySetService = (PolicySetService) context.lookup(com.soffid.iam.addons.xacml.service.ejb.PolicySetServiceHome.JNDI_NAME);
				policySetService.importXACMLPolicySet(in);
				getModel().refresh();
			}
			
		});
	}

	public void exportPolicySet(Event event) throws NamingException {
		Object o = XPathUtils.eval(getForm(), "/instance");
		PolicySet polset = new PolicySet();
		Policy pol = new Policy();
		if (o instanceof PolicySet)
			polset = (PolicySet) o;
		else
			pol = (Policy) o;
		
		PolicySetService pss = (PolicySetService) new InitialContext().lookup(PolicySetServiceHome.JNDI_NAME);
	 
		try {
			File xmltemp = File.createTempFile("xmltemp", ".xml"); //Crea un fichero temporal tipo xml
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
			throw new UiException("Error generating xml file: " + e.getMessage());
		} 
		
	}
}
