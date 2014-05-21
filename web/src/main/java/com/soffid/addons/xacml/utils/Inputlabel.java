package com.soffid.addons.xacml.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Label;

import com.soffid.iam.addons.xacml.common.MatchIdEnumeration;

import es.caib.zkib.binder.SingletonBinder;
import es.caib.zkib.component.DataLabel;
import es.caib.zkib.events.XPathEvent;
import es.caib.zkib.events.XPathSubscriber;

public class Inputlabel extends Label implements XPathSubscriber{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SingletonBinder binder1 = new SingletonBinder(this);
	private SingletonBinder binder2 = new SingletonBinder(this);
	private SingletonBinder binder3 = new SingletonBinder(this);
	private SingletonBinder binder4 = new SingletonBinder(this);
	private SingletonBinder binder5 = new SingletonBinder(this);
	private SingletonBinder binder6 = new SingletonBinder(this);
	private SingletonBinder binder7 = new SingletonBinder(this);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zul.Label#getValue()
	 */
	private void calculateValue() {
		String subjectType = (String) binder1.getValue();
		String resourceType = (String) binder5.getValue();
		String actionType = (String) binder6.getValue();
		String environmentType = (String) binder7.getValue();
		String selector = (String) binder2.getValue();
		if (selector == null) selector = "?";
		
		MatchIdEnumeration action = (MatchIdEnumeration) binder3.getValue();
		String actionValue = "";
		if(action != null){
			actionValue = convert(action.getValue());
		}
		
		//String actionValue = action == null ? "" : action.getValue();
		String value = (String) binder4.getValue();
		if (value == null) value = "";
		
		if ((subjectType == null || subjectType.equals("")) && 
				(actionType == null || actionType.equals("")) && 
				(environmentType == null || environmentType.equals("")) && 
				(resourceType == null || resourceType.equals("")))
			setValue ("(" + selector + ") " + actionValue + " '" + value + "'");
		else
		{
			String type = "";
			if(subjectType != null && subjectType != ""){
				int last = subjectType.lastIndexOf(":");
				if (last >= 0)
					type = subjectType.substring(last+1);
			}else if (resourceType != null && resourceType != ""){
				int last = resourceType.lastIndexOf(":");
				if (last >= 0)
					type = resourceType.substring(last+1);
			}else if (actionType != null && actionType != ""){
				int last = actionType.lastIndexOf(":");
				if (last >= 0)
					type = actionType.substring(last+1);
			}else if (environmentType != null && actionType != ""){
				int last = environmentType.lastIndexOf(":");
				if (last >= 0)
					type = environmentType.substring(last+1);
			}
			setValue ("(" + type +") " + actionValue + " '" + value + "'");
		}
	}

	public void onUpdate(XPathEvent event) {
		calculateValue();
	}
	
	private String convert(String action){
		String actionValue = "";	
		if(action.equals("TYPE_EQUAL")){
			actionValue = "=";
		}else if(action.equals("TYPE_GREATER_THAN")){
			actionValue = ">";
		}else if(action.equals("TYPE_GREATER_THAN_OR_EQUAL")){
			actionValue = ">=";
		}else if(action.equals("TYPE_LESS_THAN")){
			actionValue = "<";
		}else if(action.equals("TYPE_LESS_THAN_OR_EQUAL")){
			actionValue = "<=";
		}else if(action.equals("TYPE_MATCH")){
			actionValue = "~=";
		}
		
		return actionValue;
		
	}

	public Inputlabel(){
		super();
		binder1.setDataPath("@subjectAttributeDesignator");
		binder2.setDataPath("@attributeSelector");
		binder3.setDataPath("@matchId");
		binder4.setDataPath("@attributeValue");
		binder5.setDataPath("@resourceAttributeDesignator");
		binder6.setDataPath("@actionAttributeDesignator");
		binder7.setDataPath("@environmentAttributeDesignator");
	}
	
	public void setPage(Page page) {
		super.setPage(page);
		binder1.setPage(page);
		binder2.setPage(page);
		binder3.setPage(page);
		binder4.setPage(page);
		binder5.setPage(page);
		binder6.setPage(page);
		binder7.setPage(page);
	}

	public void setParent(Component parent) {
		super.setParent(parent);
		binder1.setParent(parent);
		binder2.setParent(parent);
		binder3.setParent(parent);
		binder4.setParent(parent);
		binder5.setParent(parent);
		binder6.setParent(parent);
		binder7.setParent(parent);
	}

	public Object clone() {
		Inputlabel clone = (Inputlabel) super.clone();
		clone.binder1 = new SingletonBinder(clone);
		clone.binder1.setDataPath(binder1.getDataPath());
		clone.binder2 = new SingletonBinder(clone);
		clone.binder2.setDataPath(binder2.getDataPath());
		clone.binder3 = new SingletonBinder(clone);
		clone.binder3.setDataPath(binder3.getDataPath());
		clone.binder4 = new SingletonBinder(clone);
		clone.binder4.setDataPath(binder4.getDataPath());
		clone.binder5 = new SingletonBinder(clone);
		clone.binder5.setDataPath(binder5.getDataPath());
		clone.binder6 = new SingletonBinder(clone);
		clone.binder6.setDataPath(binder6.getDataPath());
		clone.binder7 = new SingletonBinder(clone);
		clone.binder7.setDataPath(binder7.getDataPath());
		return clone;
	}
	
}
