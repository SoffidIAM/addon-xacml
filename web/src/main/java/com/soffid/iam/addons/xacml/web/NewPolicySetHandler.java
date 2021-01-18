package com.soffid.iam.addons.xacml.web;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.soffid.iam.addons.xacml.common.PolicyCombiningAlgorithm;
import com.soffid.iam.web.component.CustomField3;

import es.caib.zkib.component.Div;
import es.caib.zkib.zkiblaf.Missatgebox;

public class NewPolicySetHandler extends Window {
	private String type;
	private EventListener eventListener;
	
	public NewPolicySetHandler () {
		
	}

	public void cleanWindow(){
		setVisible(false);
	}
	
	CustomField3 getCustomField3 (String name) {
		return (CustomField3) getFellow(name);
	}
	
	void selectVersion(Event ev){
		Radio r = (Radio) ev.getTarget();
		Radiogroup rg = r.getRadiogroup();
		int i = rg.getSelectedIndex();
		if(i==0){
			getCustomField3("psID_version").setDisabled(true);
			getCustomField3("psID_earliest").setValue("");
			getCustomField3("psID_latest").setValue("");
			getCustomField3("psID_earliest").setDisabled(false);
			getCustomField3("psID_latest").setDisabled(false);
		}else if(i==1){
			getCustomField3("psID_version").setValue("");
			getCustomField3("psID_version").setDisabled(false);
			getCustomField3("psID_earliest").setDisabled(true);
			getCustomField3("psID_latest").setDisabled(true);
		}else{
			getCustomField3("psID_version").setValue("");
			getCustomField3("psID_earliest").setValue("");
			getCustomField3("psID_latest").setValue("");
			getCustomField3("psID_earliest").setDisabled(true);
			getCustomField3("psID_latest").setDisabled(true);
			getCustomField3("psID_version").setDisabled(true);
		}
	}
	
	public void apply(Event event) throws Exception{
		if(getFellow("v_ps").isVisible())
		{
			if ( getCustomField3("ps_id").attributeValidateAll() &&
					getCustomField3("ps_desc").attributeValidateAll() &&
				getCustomField3("ps_vers").attributeValidateAll()) {
				String policySetId = (String)getCustomField3("ps_id").getValue();
				String descript = (String)getCustomField3("ps_desc").getValue();
				String versio = (String)getCustomField3("ps_vers").getValue();
				String policy = (String) getCustomField3("lbPolicyCombiningAlgorithm").getValue();
				Object[] dades = {policySetId, descript, versio, PolicyCombiningAlgorithm.fromString(policy), ""};
				eventListener.onEvent(new Event("onApply", this, dades));
				cleanWindow();
			}
		} 
		else if(getFellow("v_po").isVisible())
		{
			if (getCustomField3("po_id").attributeValidateAll() &&
					getCustomField3("po_desc").attributeValidateAll() &&
					getCustomField3("po_vers").attributeValidateAll())
			{
				Component vbox_po = getFellow("v_po");
				String policyId = (String)getCustomField3("po_id").getValue();
				String descript = (String)getCustomField3("po_desc").getValue();
				String versio = (String)getCustomField3("po_vers").getValue();
				String policy = (String) getCustomField3("rule").getValue();
				Object[] dades = {policyId, descript, versio, PolicyCombiningAlgorithm.fromString(policy), ""};
				eventListener.onEvent(new Event("onApply", this, dades));
				cleanWindow();
			}
		}
		else
		{
			if (getCustomField3("psID_reference_type").validate() &&
				getCustomField3("psID_earliest").validate() &&
				getCustomField3("psID_latest").validate() &&
				getCustomField3("psID_version").validate()) {
				Component vbox_psID = getFellow("v_psID");
				String idReferenceType = (String)getCustomField3("psID_reference_type").getValue();
				String early = (String)getCustomField3("psID_earliest").getValue();
				String late = (String)getCustomField3("psID_latest").getValue();
				String version = (String)getCustomField3("psID_version").getValue();
				Object[] dades = {idReferenceType, early, late, version, ""};
				eventListener.onEvent(new Event("onApply", this, dades));
				cleanWindow();
				
			}
		}
	}

	public void open(String type, EventListener eventListener) {
		Component vbox_ps = getFellow("v_ps");
		Component vbox_po = getFellow("v_po");
		Component vbox_psID = getFellow("v_psID");
		vbox_ps.setVisible(false);
		vbox_po.setVisible(false);
		vbox_psID.setVisible(false);
		if(type.equals("policySet")){
			getCustomField3("lbPolicyCombiningAlgorithm").setValue(PolicyCombiningAlgorithm.PERMIT_OVERRIDES.getValue());
			vbox_ps.setVisible(true);		
			getCustomField3("ps_id").setValue("");
			getCustomField3("ps_desc").setValue("");
			getCustomField3("ps_vers").setValue("");
		}else if(type.equals("policy")){
			getCustomField3("rule").setValue(PolicyCombiningAlgorithm.PERMIT_OVERRIDES.getValue());
			getCustomField3("po_id").setValue("");
			getCustomField3("po_desc").setValue("");
			getCustomField3("po_vers").setValue("");
			vbox_po.setVisible(true);	
		}else if(type.equals("policySetIdRef")){
			vbox_psID.setVisible(true);	
			getCustomField3("psID_reference_type").setValue("");
			getCustomField3("psID_earliest").setValue("");
			getCustomField3("psID_latest").setValue("");
			getCustomField3("psID_version").setValue("");
			getCustomField3("psID_earliest").setDisabled(true);
			getCustomField3("psID_latest").setDisabled(true);
		}else if(type.equals("policyIdRef")){
			vbox_psID.setVisible(true);
			getCustomField3("psID_reference_type").setValue("");
			getCustomField3("psID_earliest").setValue("");
			getCustomField3("psID_latest").setValue("");
			getCustomField3("psID_version").setValue("");
			getCustomField3("psID_earliest").setDisabled(true);
			getCustomField3("psID_latest").setDisabled(true);	
		}
		this.type = type;
		this.eventListener = eventListener;

		doHighlighted();
	}
}
