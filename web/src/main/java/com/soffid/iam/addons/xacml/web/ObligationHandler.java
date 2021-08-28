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
import com.soffid.iam.addons.xacml.common.EffectTypeEnumeration;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.addons.xacml.common.Obligation;
import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.VariableDefinition;
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

public class ObligationHandler extends Div {
	private DataTable currentTable;
	private boolean newObject;
	String dataPath;
	private byte[] currentBean;
	
	public void createNew(Event ev) throws Exception {
		Component button = ev.getTarget();
		currentTable = (DataTable) button.getParent().getPreviousSibling();
		BindContext ctx = XPathUtils.getComponentContext(button);

		currentTable.addNew();
		
		XPathUtils.setValue(currentTable, "fulfillOn", EffectTypeEnumeration.PERMIT);

		newObject = true;
		Window var = (Window) getFellow("var");
		if (var.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", var));
		var.getFellow("back").setVisible(true);
		var.doHighlighted();
	}
	
	public void wizardBack(Event event) throws Exception {
		Window add = (Window) getFellow("var");
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
		Obligation current = (Obligation) XPathUtils.eval(currentTable, "instance");
		
		this.newObject = false;
		currentBean = serialize(current);
		Window modify = (Window) getFellow("var");
		if (modify.getChildren().isEmpty())
			Events.sendEvent(new Event("onOpen", modify));
		
		modify.getFellow("back").setVisible(false);
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
	
	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
}
