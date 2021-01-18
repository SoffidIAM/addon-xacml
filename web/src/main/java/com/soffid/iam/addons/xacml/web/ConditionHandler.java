package com.soffid.iam.addons.xacml.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import org.zkoss.zul.Window;

import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.Condition;
import com.soffid.iam.web.component.CustomField3;
import com.soffid.iam.web.component.InputField3;
import com.soffid.iam.web.component.ObjectAttributesDiv;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.BindContext;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.component.Div;
import es.caib.zkib.component.Form2;
import es.caib.zkib.datamodel.DataNodeCollection;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.events.XPathRerunEvent;
import es.caib.zkib.zkiblaf.Missatgebox;

public class ConditionHandler extends Div {
	private DataTable currentTable;
	private boolean newObject;
	String dataPath;
	private byte[] currentBean;
	
	public void createNew(Event ev) throws Exception {
		Component button = ev.getTarget();
		currentTable = (DataTable) button.getParent().getPreviousSibling();
		BindContext ctx = XPathUtils.getComponentContext(button);

		currentTable.addNew();
		
		
		Expression expression = new Expression();
		expression.setOrder(0);
		expression.setExpressionType("attributeValue");
		expression.setDataTypeAttributeValue(DataType.BOOLEAN_TYPE);
		expression.setAttributeValue("true");
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		
		XPathUtils.setValue(currentTable, "expression", expression);

		newObject = true;
		Window var = (Window) getFellow("var");
		if (var.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", var));
		var.doHighlighted();
		
		Form2 f = (Form2) var.getFellow("form");
		f.setDataPath("../"+currentTable.getId()+":/");
	}
	
	public void wizardBack(Event event) throws Exception {
		Window add = (Window) getFellow("rule");
		if (newObject)
			currentTable.delete();
		else {
			Object r = deserialize(currentBean);
			Object current = XPathUtils.eval(currentTable, "instance");
			PropertyUtils.copyProperties(r, current);
			BindContext ctx = XPathUtils.getComponentContext(this);
			ctx.getDataSource().sendEvent(new XPathRerunEvent(ctx.getDataSource(), XPathUtils.concat(ctx.getXPath(), dataPath )));
			add.setVisible(false);		
		}
		add.setVisible(false);
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
	
	public void wizardApply(Event event) {
		
		Window add = (Window) getFellow("var");
		Component form = add.getFellow("form");
		if (validateAttributes(form)) {
			Expression e = (Expression) XPathUtils.eval(currentTable, "expression");
			XPathUtils.setValue(currentTable, "expression", null);
			XPathUtils.setValue(currentTable, "expression", e);
			add.setVisible(false);			
			currentTable.setSelectedIndex(-1);
		}
		
	}

	byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ObjectOutputStream(out).writeObject(o);
		return out.toByteArray();
	}
	
	Object deserialize(byte data[]) throws IOException, ClassNotFoundException {
		return new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
	}


	public void edit(Event ev) throws Exception {
		currentTable = (DataTable) ev.getTarget();
		Condition current = (Condition) XPathUtils.eval(currentTable, "instance");
		
		this.newObject = false;
		currentBean = serialize(current);
		Window modify = (Window) getFellow("var");
		if (modify.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", modify));
		
		Form2 f = (Form2) modify.getFellow("form");
		f.setDataPath("../"+currentTable.getId()+":/");

		modify.doHighlighted();
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
		field.setValuesPath(null);
		field.setKeysPath(null);
		List<String> values = new LinkedList<>();
		values.add( URLEncoder.encode("urn:oasis:names:tc:xacml:1.0:resource:resource-location", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.Url") );
		values.add( URLEncoder.encode("com:soffid:iam:xacml:1.0:resource:soffid-object", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.SoffidObject") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:subject:account", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.AccountName") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:subject:system", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.SystemName") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:subject:loginName", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.AccountName") );
		field.setListOfValues(values.toArray(new String[values.size()]));
		field.updateMetadata();
	}

	void configureSubjectDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		field.setValuesPath("/model:/metadata/name");
		field.setKeysPath("/model:/metadata/value");
		field.setValues(null);
		field.updateMetadata();
	}

	void configureActionDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		field.setValuesPath(null);
		field.setKeysPath(null);
		List<String> values = new LinkedList<>();
		values.add( URLEncoder.encode("urn:com:soffid:xacml:action:method", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.Method") );
		field.setListOfValues(values.toArray(new String[values.size()]));
		field.updateMetadata();
	}

	void configureEnvironmentDesignator(CustomField3 field) throws NamingException, CreateException, InternalErrorException, IOException {
		field.setValuesPath(null);
		field.setKeysPath(null);
		List<String> values = new LinkedList<>();
		values.add( URLEncoder.encode("urn:com:soffid:xacml:environment:country", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.Country") );
		values.add( URLEncoder.encode("urn:oasis:names:tc:xacml:1.0:environment:current-time", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.CurrentTime") );
		values.add( URLEncoder.encode("urn:oasis:names:tc:xacml:1.0:environment:current-date", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.CurrentDate") );
		values.add( URLEncoder.encode("urn:oasis:names:tc:xacml:1.0:environment:current-dateTime", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.CurrentDateTime") );
		field.setListOfValues(values.toArray(new String[values.size()]));
		field.updateMetadata();
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
}
