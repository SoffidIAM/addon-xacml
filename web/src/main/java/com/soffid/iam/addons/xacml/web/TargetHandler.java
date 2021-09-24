package com.soffid.iam.addons.xacml.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.beanutils.PropertyUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.web.component.CustomField3;
import com.soffid.iam.web.component.InputField3;
import com.soffid.iam.web.component.ObjectAttributesDiv;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.BindContext;
import es.caib.zkib.component.DataDiv;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.component.Div;
import es.caib.zkib.component.Form2;
import es.caib.zkib.component.Wizard;
import es.caib.zkib.datamodel.DataNodeCollection;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.zkiblaf.Missatgebox;

public class TargetHandler extends Div {
	private DataTable currentTable;
	private Object currentBean;

	public void createNew(Event ev) throws Exception {
		Component button = ev.getTarget();
		currentTable = (DataTable) button.getParent().getPreviousSibling();
		BindContext ctx = XPathUtils.getComponentContext(button);
		
		currentTable.addNew();
		
		Window add = (Window) getFellow("add");
		if (add.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", add));
		add.doHighlighted();
		
		Form2 f = (Form2) add.getFellow("form");
		f.setDataPath("../"+currentTable.getId()+":/");
		
		configureAttributeDesignator(add);
		
		Wizard w = (Wizard) add.getFellow("wizard");
		w.setSelected(0);
		Radiogroup rg = (Radiogroup) w.getFellow("radiogroup");
		rg.setSelectedIndex(0);
		enableRadioElements(rg);
	}
	
	public void changeAttributeType (Event event) {
		Radio r = (Radio) event.getTarget();
		Radiogroup rg = r.getRadiogroup();
		enableRadioElements (rg); 
		if (rg.getSelectedIndex() == 1) {
			XPathUtils.setValue( currentTable, "attributeDesignator", null);
		} else {
			XPathUtils.setValue( currentTable, "attributeSelector", null);
		}
	}

	private void enableRadioElements(Radiogroup rg) {
		for (int i = 0; i < rg.getItemCount(); i++) {
			Radio r = rg.getItemAtIndex(i);
			for (Component c = r.getNextSibling(); c != null; c = c.getNextSibling())
				c.setVisible(rg.getSelectedIndex() == i);
		}
	}
	
	public void wizardRollback(Event event) {
		Window add = (Window) getFellow("add");
		add.setVisible(false);
		
		currentTable.delete();
	}
	
	public void wizardBack(Event event) {
		Wizard w = (Wizard) event.getTarget().getFellow("wizard");
		w.previous();
	}

	public void wizardNext(Event event) {
		Window add = (Window) getFellow("add");
		Wizard w = (Wizard) add.getFellow("wizard");
		if (validateAttributes((Component) w.getChildren().get(w.getSelected())))
			w.next();
	}

	private boolean validateAttributes(Component form) {
		if (form == null || !form.isVisible()) return true;
		if (form instanceof ObjectAttributesDiv) {
			return ((ObjectAttributesDiv) form).validate();
		}
		if (form instanceof InputField3) {
			InputField3 inputField = (InputField3)form;
			if (inputField.isReadonly() || inputField.isDisabled())
				return true;
			else
				return inputField.attributeValidateAll();
		}
		boolean ok = true;
		for (Component child = form.getFirstChild(); child != null; child = child.getNextSibling())
			if (! validateAttributes(child))
				ok = false;
		return ok;
	}
	
	public void onSetAttributeDesignator(Event event) {
		CustomField3 cf = (CustomField3) event.getTarget();
		CustomField3 dt = (CustomField3) cf.getNextSibling();
		if (cf.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-time"))
			dt.setValue(DataType.TIME_TYPE);
		else if (cf.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-date"))
			dt.setValue(DataType.DATE_TYPE);
		else if (cf.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-dateTime"))
			dt.setValue(DataType.DATE_TIME_TYPE);
		else
			dt.setValue(DataType.STRING_TYPE);
	}
	
	public void wizardApply(Event event) {
		
		Window add = (Window) getFellow("add");
		Wizard w = (Wizard) add.getFellow("wizard");
		if (validateAttributes((Component) w.getChildren().get(w.getSelected()))) {
			Component form = w.getFellow("form");
			XPathUtils.setValue(form, "@dataTypeAttributeValue",
					XPathUtils.eval(form, "@dataTypeDesignator"));
//			currentTable.commit();
			add.setVisible(false);			
		}
		
	}

	public void edit(Event ev) throws Exception {
		currentTable = (DataTable) ev.getTarget();
		Object current = XPathUtils.eval(currentTable, "instance");
		currentBean = current.getClass().newInstance();
		PropertyUtils.copyProperties(currentBean, current);
		Window modify = (Window) getFellow("modify");
		if (modify.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", modify));
		
		Form2 f = (Form2) modify.getFellow("form");
		f.setDataPath("../"+currentTable.getId()+":/");

		configureAttributeDesignator(modify);

		Radiogroup rg = (Radiogroup) modify.getFellow("radiogroup");
		rg.setSelectedIndex( XPathUtils.eval( currentTable, "attributeDesignator") == null ? 1: 0 );
		enableRadioElements(rg);
		
		modify.doHighlighted();
	}

	public void modifyApply(Event event) {
		
		Window modify = (Window) getFellow("modify");
		if (validateAttributes(modify)) {
			Component form = modify.getFellow("form");
			XPathUtils.setValue(form, "@dataTypeAttributeValue",
					XPathUtils.eval(form, "@dataTypeDesignator"));
			modify.setVisible(false);		
		}
		
	}

	public void modifyBack(Event event) throws Exception {
		Object current = XPathUtils.eval(currentTable, "instance");
		PropertyUtils.copyProperties(current, currentBean);
		DataNodeCollection coll = (DataNodeCollection) currentTable.getDataSource().getJXPathContext().getValue(currentTable.getXPath());
		coll.refresh();
		Window modify = (Window) getFellow("modify");
		modify.setVisible(false);		
	}
	
	public void multiSelect(Event ev) {
		DataTable dt = (DataTable) ev.getTarget();
		Button remove = (Button) dt.getNextSibling().getLastChild();
		remove.setVisible(dt.getSelectedIndexes().length > 0);
	}
	
	public void delete(final Event ev) {
		
		Missatgebox.confirmaOK_CANCEL(Labels.getLabel("common.delete"), 
				(event) -> {
					if (event.getName().equals("onOK")) {
						DataTable dt = (DataTable) ev.getTarget().getParent().getPreviousSibling();
						dt.delete();
//						dt.commit();
						ev.getTarget().setVisible(false);
					}
				});

	}
	
	void configureAttributeDesignator(Window add) throws NamingException, CreateException, InternalErrorException, IOException {
		CustomField3 ad = (CustomField3) add.getFellow("attributeDesignator");
		if (currentTable.getId().equals("gridSubjects"))
			configureSubjectDesignator(ad);
		if (currentTable.getId().equals("gridResources"))
			configureResourceDesignator(ad);
		if (currentTable.getId().equals("gridActions"))
			configureActionDesignator(ad);
		if (currentTable.getId().equals("gridEnvironments"))
			configureEnvironmentDesignator(ad);
	}
	
	void configureResourceDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		ExpressionHelper.configureResourceDesignator(field);
	}

	void configureSubjectDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		ExpressionHelper.configureSubjectDesignator(field);
	}

	void configureActionDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		ExpressionHelper.configureActionDesignator(field);
	}

	void configureEnvironmentDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		ExpressionHelper.configureEnvironmentDesignator(field);
	}
}
