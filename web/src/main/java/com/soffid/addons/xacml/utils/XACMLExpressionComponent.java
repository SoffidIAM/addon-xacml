package com.soffid.addons.xacml.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;

import es.caib.zkib.binder.BindContext;
import es.caib.zkib.binder.SingletonBinder;
import es.caib.zkib.component.Form;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datasource.DataSource;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.events.XPathEvent;
import es.caib.zkib.events.XPathSubscriber;

public class XACMLExpressionComponent extends Form implements BindContext, XPathSubscriber, IdSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String compos;
	
	private SingletonBinder binder1 = new SingletonBinder(this);
	private SingletonBinder binder2 = new SingletonBinder(this);
	private SingletonBinder binder3 = new SingletonBinder(this);
	private SingletonBinder binder4 = new SingletonBinder(this);
	private SingletonBinder binder5 = new SingletonBinder(this);
	private SingletonBinder binder6 = new SingletonBinder(this);
	private SingletonBinder binder7 = new SingletonBinder(this);
	private SingletonBinder binder8 = new SingletonBinder(this);
	private SingletonBinder binder9 = new SingletonBinder(this);
	private SingletonBinder binder10 = new SingletonBinder(this);
	private SingletonBinder binder11 = new SingletonBinder(this);
	
	//Segons l'expressió pot tenir més o menys subExpressions i té o no qualifiers la presentarem de diferents formes
	
	//Presentació d'expression de tipus resource, subject, action o environment
	static final String NO_EXPR_SUBPANE = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='lab' sclass=\"label_pop\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
				"getPage(\"expressionPanel\").getFellow(\"esquema\"), self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"<label id='argDesignator' style='margin-left:7px;'/>" +
			//"<label id='argAttribute' style='margin-left:5px;'/>" +
			"</h:span>" +
			"</div>";
	//Presentació d'expressions de tipus attribute, value o selector
	static final String NO_EXPR_NO_VALUE_SUBPANE = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='lab' sclass=\"label_pop\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\"), self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"<label id='arg2' style='margin-left:5px;'/> " +
			"</h:span>" +
			"</div>";
	//Presentació d'expressió amb 1 argument i que no té qualificadors
	static final String ONE_ARG_SUBPANE = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\"), self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</h:span>" + 
			"<div class='subExpression1'>" +
			"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</div>";
	//Presentació d'expressió amb 1 argument i listbox de possibles qualificadors
	static final String ONE_ARG_SUBPANE_WITHQUALIFIER = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()" +
			".getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label value=\"${c:l('xacml_expressionPanel.Qualifier')}\"/>" +
			"<listbox mold='select' id='quali' style='margin-left:5px; font-size: 10px;' width='155px'>" +
			"</listbox>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='argTipus' style='margin-left:25px;'/>" +
			"<div class='subExpression1'>" +
			"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</h:span>" +
			"</div>";
	//Presentació d'expressió amb 1 argument i qualificador fix tipus String
	static final String ONE_ARG_SUBPANE_STRING = "<div>" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div>" +
			"<label value='(string)'/>" +
			"</div>" +
			"<div class='subExpression1'>" +
			"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</div>";
	//Presentació d'expressió amb 2 arguments i listbox de possibles qualificadors
	static final String TWO_ARG_SUBPANE = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()" +
			".getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label value=\"${c:l('xacml_expressionPanel.Qualifier')}\"/>" +
			"<listbox mold='select' id='quali' style='margin-left:5px; font-size: 10px' width='155px'>" +
			"</listbox>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='argTipus' style='margin-left:25px;'/>" +
			"<div class='subExpression1'>" +
						"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' " +
							"use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
						"<box width='99%' dataPath='/subexpression[2]' mold='vertical' visible='true' " +
							"use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</h:span>" +
			"</div>";
	//Presentació d'expressió amb 2 arguments i que no té qualificadors
	static final String TWO_ARG_SUBPANE_SENSE = "<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div class='subExpression1'>" +
			"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<box width='99%' dataPath='/subexpression[2]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</h:span>";
	//Presentació d'expressió amb 3 arguments i que no té qualificadors
	static final String THREE_ARG_SUBPANE_SENSE = "<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div class='subExpression1'>" +
			"<box width='99%' dataPath='/subexpression[1]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<box width='99%' dataPath='/subexpression[2]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<box width='99%' dataPath='/subexpression[3]' mold='vertical' visible='true' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"</div>" +
			"</h:span>";
	//Presentació d'expressió amb 2 o més arguments sense qualificador
	static final String TWO_ARG_SUBPANE_PLUS_SENSE = "<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()." +
			"getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div class='subExpression1'>" +
			"<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[1]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[2]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<imageclic id='addSub' src='~./img/list-add.gif'>" +
			"<attribute name='onClick'>" +
			"createNewSubExpression(self);" +
			"</attribute>" +
			"</imageclic>" +
			"<label value=\"${c:l('xacml_policySet.AddNew')}\" style='margin-left:5px;'/>" +
			"</h:span>" +
			"</div>" +
			"</h:span>";
	//Presentació d'expressió amb 2 o més arguments i qualificador
	static final String TWO_ARG_SUBPANE_PLUS = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()" +
			".getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label value=\"${c:l('xacml_expressionPanel.Qualifier')}\"/>" +
			"<listbox mold='select' id='quali' style='margin-left:5px; font-size: 10px' width='155px'>" +
			"</listbox>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='argTipus' style='margin-left:25px;'/>" +
			"<div class='subExpression1'>" +
			"<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[1]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[2]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<imageclic id='addSub' src='~./img/list-add.gif'>" +
			"<attribute name='onClick'>createNewSubExpression(self);" +
			"</attribute>" +
			"</imageclic>" +
			"<label value=\"${c:l('xacml_policySet.AddNew')}\" style='margin-left:5px;'/>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"</div>";
	//Presentació de funció que pot tenir cap o varis arguments
	static final String NO_ARG_SUBPANE_PLUS = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<div>" +
			"<label id='label2' sclass=\"label_pop2\">" +
			"<attribute name=\"onClick\">Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop()" +
			".getPage(\"expressionPanel\").getFellow(\"esquema\") , self.parent.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</div>" +
			"<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label value=\"${c:l('xacml_expressionPanel.Qualifier')}\"/>" +
			"<listbox mold='select' id='quali' style='margin-left:5px; font-size: 10px' width='155px'>" +
			"</listbox>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='argTipus' style='margin-left:25px;'/>" +
			"<div class='subExpression1'>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<imageclic id='addSub' src='~./img/list-add.gif'>" +
			"<attribute name='onClick'>createNewSubExpression(self);" +
			"</attribute>" +
			"</imageclic>" +
			"<label value=\"${c:l('xacml_policySet.AddNew')}\" style='margin-left:5px;'/>" +
			"</h:span>" +
			"</div>" +
			"</h:span>" +
			"</div>";
	//Presentació pels arguments (Subexpressions) que encara són "void"
	static final String PANEL_EXP_EMPTY = "<div>" +
			"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
			"<label id='arg3' sclass=\"label_pop\">" +
			"<attribute name=\"onClick\">" +
			"Events.postEvent(\"onInicia\", Executions.getCurrent().getDesktop().getPage(\"expressionPanel\")." +
			"getFellow(\"esquema\") , self.parent.parent.parent);" +
			"</attribute>" +
			"</label>" +
			"</h:span>" +
			"</div>";
	
	//Array per seleccionar de quin tipus és l'expressió i segons això el tipus de presentació que ha de tenir
	static final String paneType [][] = {
			{"attributeSelector", NO_EXPR_NO_VALUE_SUBPANE},
			{"attributeValue", NO_EXPR_NO_VALUE_SUBPANE},
			{"variable", NO_EXPR_NO_VALUE_SUBPANE},
			{"subject", NO_EXPR_SUBPANE},
			{"action", NO_EXPR_SUBPANE},
			{"environment", NO_EXPR_SUBPANE},
			{"resource", NO_EXPR_SUBPANE},
			{"name", NO_EXPR_NO_VALUE_SUBPANE},
			{"function", ""}
			};
	
	//Array per seleccionar el tipus de funció i segons això el tipus de presentació que ha de tenir
	static final String functionType[][] = {
			{"ROUND", ONE_ARG_SUBPANE, "U"},
			{"FLOOR", ONE_ARG_SUBPANE, "U"},
			{"ABS", ONE_ARG_SUBPANE_WITHQUALIFIER, "UQ"},
			{"STRING_NORMALIZE_SPACE", ONE_ARG_SUBPANE_STRING, "S"},
			{"STRING_NORMALIZE_TO_LOWER_CASE", ONE_ARG_SUBPANE_STRING, "S"},
			{"DOUBLE_TO_INTEGER", ONE_ARG_SUBPANE, "U"},
			{"INTEGER_TO_DOUBLE", ONE_ARG_SUBPANE, "U"},
			{"NOT", ONE_ARG_SUBPANE, "U"},
			{"ONE_AND_ONLY", ONE_ARG_SUBPANE_WITHQUALIFIER, "UQ"},
			{"BAG_SIZE", ONE_ARG_SUBPANE_WITHQUALIFIER, "UQ"},
			{"XPATH_NODE_COUNT", ONE_ARG_SUBPANE, "U"},
			{"EQUAL", TWO_ARG_SUBPANE, "TQ"}, // --> Han de tenir un listbox amb els altres qualifiers
			{"GREATER_THAN", TWO_ARG_SUBPANE, "TQ"},
			{"GREATER_THAN_OR_EQUAL", TWO_ARG_SUBPANE, "TQ"},
			{"LESS_THAN", TWO_ARG_SUBPANE, "TQ"},
			{"LESS_THAN_OR_EQUAL", TWO_ARG_SUBPANE, "TQ"},
			{"MATCH", TWO_ARG_SUBPANE, "TQ"},
			{"REGEXP_MATCH", TWO_ARG_SUBPANE, "TQ"},
			{"SUBTRACT", TWO_ARG_SUBPANE, "TQ"},
			{"MULTIPLY", TWO_ARG_SUBPANE, "TQ"},
			{"DIVIDE", TWO_ARG_SUBPANE, "TQ"},
			{"MOD", TWO_ARG_SUBPANE, "TQ"}, // --> Té qualifier però sempre el mateix
			{"ADD_DAYTIMEDURATION", TWO_ARG_SUBPANE, "TQ"},
			{"ADD_YEARMONTHDURATION", TWO_ARG_SUBPANE, "TQ"},
			{"SUBTRACT_DAYTIMEDURATION", TWO_ARG_SUBPANE, "TQ"},
			{"SUBTRACT_YEARMONTHDURATION", TWO_ARG_SUBPANE, "TQ"},
			{"IS_IN", TWO_ARG_SUBPANE, "TQ"},
			{"INTERSECTION", TWO_ARG_SUBPANE, "TQ"},
			{"AT_LEAST_ONE_MEMBER_OF", TWO_ARG_SUBPANE, "TQ"},
			{"UNION", TWO_ARG_SUBPANE, "TQ"},
			{"SUBSET", TWO_ARG_SUBPANE, "TQ"},
			{"SET_EQUALS", TWO_ARG_SUBPANE, "TQ"},
			{"MAP", TWO_ARG_SUBPANE_SENSE, "T"}, // --> Dos arguments però sense qualifier
			{"XPATH_NODE_EQUAL", TWO_ARG_SUBPANE_SENSE, "T"},
			{"XPATH_NODE_MATCH", TWO_ARG_SUBPANE_SENSE, "T"},
			{"TIME_IN_RANGE", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ANY_OF", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ALL_OF", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ANY_OF_ANY", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ALL_OF_ANY", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ANY_OF_ALL", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ALL_OF_ALL", THREE_ARG_SUBPANE_SENSE, "3"},
			{"ADD", TWO_ARG_SUBPANE_PLUS, "TQP"},
			{"N_OF", TWO_ARG_SUBPANE_PLUS_SENSE, "TP"},
			{"STRING_CONCATENATE", TWO_ARG_SUBPANE_PLUS_SENSE, "TP"},
			{"URL_STRING_CONCATENATE", TWO_ARG_SUBPANE_PLUS_SENSE, "TP"},
			{"AND", TWO_ARG_SUBPANE_PLUS_SENSE, "TP"},
			{"OR", TWO_ARG_SUBPANE_PLUS_SENSE, "TP"},
			{"BAG", NO_ARG_SUBPANE_PLUS, "Z"}
	};
	
	
	public XACMLExpressionComponent(){
		super();
		compos = new String();
	}
	
	public void onCreate () 
	{		
		binder1.setDataPath(getDataPath()+"/@expressionType");
		binder2.setDataPath(getDataPath()+"/@attributeDesignator");
		binder3.setDataPath(getDataPath()+"/@attributeSelector");
		binder4.setDataPath(getDataPath()+"/@attributeValue");
		binder5.setDataPath(getDataPath()+"/@name");
		binder6.setDataPath(getDataPath()+"/@order");
		binder7.setDataPath(getDataPath()+"/@subexpression"); //Hi havia expression
		binder8.setDataPath(getDataPath()+"/@id");
		binder9.setDataPath(getDataPath()+"/@variableId");
		binder10.setDataPath(getDataPath()+"/@dataTypeAttributeValue");
		binder11.setDataPath(getDataPath()+"/@dataTypeAttributeDesignator");
		
		drawExpression ();
		String parentClass = null;
		Component c = getParent ();
		while (c != null)
		{
			if (c instanceof XACMLExpressionComponent)
			{
				parentClass = ((XACMLExpressionComponent) c).getSclass();
				break;
			}
			c = c.getParent();
		}
		if ("subExpression3".equals(parentClass))
			setSclass("subExpression2");
		else
			setSclass("subExpression3");
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
		binder8.setPage(page);
		binder9.setPage(page);
		binder10.setPage(page);
		binder11.setPage(page);
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
		binder8.setParent(parent);
		binder9.setParent(parent);
		binder10.setParent(parent);
		binder11.setParent(parent);
		
	}

	
	public Object clone() {
		XACMLExpressionComponent clone = (XACMLExpressionComponent) super.clone();
		clone.binder1 = new SingletonBinder (clone);
		clone.binder1.setDataPath(binder1.getDataPath());
		clone.binder2 = new SingletonBinder (clone);
		clone.binder2.setDataPath(binder2.getDataPath());
		clone.binder3 = new SingletonBinder (clone);
		clone.binder3.setDataPath(binder3.getDataPath());
		clone.binder4 = new SingletonBinder (clone);
		clone.binder4.setDataPath(binder4.getDataPath());
		clone.binder5 = new SingletonBinder (clone);
		clone.binder5.setDataPath(binder5.getDataPath());
		clone.binder6 = new SingletonBinder (clone);
		clone.binder6.setDataPath(binder6.getDataPath());
		clone.binder7 = new SingletonBinder (clone);
		clone.binder7.setDataPath(binder7.getDataPath());
		clone.binder8 = new SingletonBinder (clone);
		clone.binder8.setDataPath(binder8.getDataPath());
		clone.binder9 = new SingletonBinder (clone);
		clone.binder9.setDataPath(binder9.getDataPath());
		clone.binder10 = new SingletonBinder (clone);
		clone.binder10.setDataPath(binder10.getDataPath());
		clone.binder11 = new SingletonBinder (clone);
		clone.binder11.setDataPath(binder11.getDataPath());
		return clone;
	}
	
	
	public void onUpdate(XPathEvent event) {
		drawExpression();
	}
	
	boolean disableRecursive = false;
	//Segons de quin tipus és cada expressió triam la seva presentació i assignam els valors que s'han de presentar
	@SuppressWarnings("unchecked")
	public synchronized void drawExpression () 
	{
		if (getPage() == null)
			return;	
		
		if (disableRecursive)
			return;
		
		disableRecursive = true;
		
		String type = new String();
		String attribute = new String();
		String designator = new String();
		String selector = new String();
		List<Expression> subExpression; //Subexpressions
		Collection<Expression> subExpressionCol = (Collection<Expression>) binder7.getValue();
		
		if(subExpressionCol != null && !subExpressionCol.isEmpty() && subExpressionCol.size()>1)
		{
			subExpression = new ArrayList<Expression>(subExpressionCol);
			Collections.sort(subExpression, new Comparator<Expression>(){
				public int compare(Expression o1, Expression o2) {
					if (o1 == null && o2 == null)
						return 0;
					if (o1 == null)
						return -1;
					if (o2 == null)
						return +1;
					if(o1.getOrder() != null && o2.getOrder() != null)
						return o1.getOrder().compareTo(o2.getOrder());
					else
						return 0;
				}	
			});
		}
		String var = new String();
		
		type = (String) binder1.getValue(); //Tipus de l'expressió
		attribute = (String) binder4.getValue();//AttributeValue
		designator = (String) binder2.getValue();//AttributeDesignator
		selector = (String) binder3.getValue();//AttributeSelector
		FunctionEnumeration name = (FunctionEnumeration) binder5.getValue();//Nom de la funció
		var = (String) binder9.getValue();
		
		String result = "";
		String typeString = null;
		String subType = new String();
		String arg2 = new String();
		String arg3 = new String();
		String tipus = new String();	
		String r = new String();
		
		//Si l'expressió no té subexpressions però ha de tenir un mínim d'arguments 
		//els inicialitzem a void
		if(type == null || type.isEmpty())// && (subExpression == null || subExpression.isEmpty()))
		{
			//L'expressió encara està buida. És una subexpressió!!
			arg3 = "void";
			result = result + PANEL_EXP_EMPTY;
		}
		else
		{
			//Si l'expressió és de tipus funció, ens quedam només amb això (llevam el subtipus)
			if(type != null && type.startsWith("function"))
			{
				subType = type.substring(9);
				type = type.substring(0, 8);
			}
			
			//Si el nom de la funció té el qualificador davant el llevam
			r = retalla(designator);
			
			//Miran de quin tipus és l'expressió per a triar la presentació corresponent
			for(int i=3; i<7; i++)
			{
				if (paneType[i][0].equals(type))
				{
					typeString = paneType[i][0];
					result = result + paneType[i][1];
				}
			}
		
			if(paneType[0][0].equals(type))
			{
				typeString = "Att selector";
				arg2 = selector;
				result = result + paneType[0][1];
			}
			
			if(paneType[1][0].equals(type))
			{
				typeString = "Att value";
				arg2 = attribute;
				result = result + paneType[1][1];
			}
			
			if(paneType[2][0].equals(type))
			{
				typeString = "Variable";
				arg2 = var;
				result = result + paneType[2][1];
			}
			
			if(paneType[7][0].equals(type))
			{
				String sensesub = new String();
				if(name != null && name.toString().contains("_"))
					sensesub = name.toString().replace('_', ' ');
				else if(name != null)
					sensesub = name.toString();
				else
					sensesub = "";
				typeString = "Function name";
				arg2 = sensesub;
				result = result + paneType[7][1];
			}
		
			if(paneType[8][0].equals(type))
			{
				if(name == null)
					return;
				
				String n = retallaName(name, designator);
				if(designator != null && !designator.isEmpty())
				{
					tipus = designator.toLowerCase();
				}
		
				for(int i=0; i<49; i++)
				{			
					if(functionType[i][0].equals(n))
					{
						// Add subexpressionso
						if ( functionType[i][2].equals("S"))
							minSubexpressions ( subExpressionCol, 1 );
						if ( functionType[i][2].startsWith("T"))
							minSubexpressions ( subExpressionCol, 2 );
						if ( functionType[i][2].startsWith("3"))
							minSubexpressions ( subExpressionCol, 3 );
						typeString = n.replace('_', ' ');
						result = result + functionType[i][1];
						if(functionType[i][2].equals("S"))
						{
							typeString = typeString.substring(7);
						}
						if(functionType[i][2].equals("Z") || functionType[i][2].equals("TQP") || functionType[i][2].equals("TP") )
						{
							String a = "<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
									"<imageclic id='addSub' src='~./img/list-add.gif'>" +
									"<attribute name='onClick'>createNewSubExpression(self);" +
									"</attribute>" +
									"</imageclic>" +
									"<label value=\"${c:l('xacml_policySet.AddNew')}\" style='margin-left:5px;'/>" +
									"</h:span>";
							String b = "<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[1]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
									"<box width='99%' mold='vertical' visible='true' dataPath='/subexpression[2]' use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
									"<h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
									"<imageclic id='addSub' src='~./img/list-add.gif'>" +
									"<attribute name='onClick'>createNewSubExpression(self);" +
									"</attribute>" +
									"</imageclic>" +
									"<label value=\"${c:l('xacml_policySet.AddNew')}\" style='margin-left:5px;'/>" +
									"</h:span>";
							
							
							if(functionType[i][2].equals("Z") && subExpressionCol != null && !subExpressionCol.isEmpty())
							{
								int ind = result.indexOf(a);
								String nouResult = result.substring(0, ind);
								for(int e = 0; e < subExpressionCol.size(); e ++)
								{
									nouResult = nouResult + "<div><h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
											"<imageclic style='float:left;' src='~./img/list-remove.gif'>" +
											"<attribute name='onClick'>removeSubExpression(self);" +
											"</attribute>" +
											"</imageclic>" +
											"<box width='90%' mold='vertical' visible='true' dataPath='/subexpression[" + (e+1) + "]' " +
											"use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
											"</h:span></div>";
								}
								nouResult = nouResult +  result.substring(ind);
								result = nouResult;
							}
							if((functionType[i][2].equals("TP") || functionType[i][2].equals("TQP")) 
									&& subExpressionCol != null && !subExpressionCol.isEmpty() && subExpressionCol.size()>2)
							{
								int ind = result.indexOf(b);
								if (ind > 0)
								{
									int ind2 = result.indexOf(a);
									String nouResult = result.substring(0, ind);
									for(int e = 0; e < subExpressionCol.size(); e ++)
									{
										nouResult = nouResult + "<div><h:span xmlns:h=\"http://www.w3.org/1999/xhtml\">" +
											"<imageclic style='float:left;' src='~./img/list-remove.gif'>" +
											"<attribute name='onClick'>removeSubExpression(self);" +
											"</attribute>" +
											"</imageclic>" +
											"<box width='90%' mold='vertical' visible='true' dataPath='/subexpression[" + (e+1) + "]' " +
											"use='com.soffid.addons.xacml.utils.XACMLExpressionComponent'/>" +
											"</h:span></div>";
									}
									nouResult = nouResult +  result.substring(ind2);
									result = nouResult;
								}
							}
						}
					}
				}
			}
		}			
		if (result.equals(""))
		{
			result="<label value=\"NO era de cap tipus\"/>";
		}
		
		//Insertam l'expressió construida dins el zul
		if(compos.isEmpty() || !compos.equals(result))
		{
			while (!getChildren().isEmpty())
			{
				((Component)getChildren().get(0)).setParent(null);
			}
			compos=result;
			Executions.createComponentsDirectly(result, "zul", this, Collections.EMPTY_MAP);
		}
		
		//Assignam els valors corresponents als elements que s'han creat a cada presentació
		Label l = (Label) getFellowIfAny("lab");
		Label l2 = (Label) getFellowIfAny("label2");
		Label argument2 = (Label) getFellowIfAny("arg2");
		Label argument3 = (Label) getFellowIfAny("arg3");
		Label argDes = (Label) getFellowIfAny("argDesignator");
		Label argAtt = (Label) getFellowIfAny("argAttribute");
		Label argTipus = (Label) getFellowIfAny("argTipus");
		
		if (l != null && typeString != null)
			l.setValue(typeString + ": " );
		
		if (l2 != null && typeString != null)
			l2.setValue(typeString);
		
		if (argument2 != null && arg2 != null)
			argument2.setValue(arg2);
		
		if (argument3 != null && arg3 != null)
			argument3.setValue(arg3);
		
		if (argDes != null && r != null)
			argDes.setValue(r);

		if (argAtt != null && attribute != null)
		{
			if(!attribute.isEmpty())
				argAtt.setValue("(" + attribute + ")");
			else
				argAtt.setValue("");
		}
		
		if (argTipus != null && tipus != null)
		{
			if(!tipus.isEmpty())
				argTipus.setValue("(" + tipus + ")");
			else
				argTipus.setValue("");
		}
		
		final Listbox quali = (Listbox) getFellowIfAny("quali");
		if(quali != null)
		{
			creaQualifierListbox(quali, retallaName(getName(), getDesignator()));
			setElementList(quali, designator);
			quali.addEventListener(Events.ON_SELECT, new org.zkoss.zk.ui.event.EventListener() {
				public void onEvent(Event event) throws Exception {
					Listitem sel = quali.getSelectedItem();
					String n = retallaName(getName(), getDesignator());
					if (sel != null) {
						String value = (String) sel.getValue();
						String name = composaName(n, value);
						setName(FunctionEnumeration.fromString(name));
						setDesignator(value);
					}
				}
			});
		}
		
		disableRecursive = false;
	}
	
	
	private void minSubexpressions(Collection<Expression> subExpressionCol, int i) {
		if (subExpressionCol == null)
			return;
		while (subExpressionCol.size() < i)
		{
			com.soffid.iam.addons.xacml.common.Expression exp = new com.soffid.iam.addons.xacml.common.Expression();
			String usuari = Executions.getCurrent().getUserPrincipal().getName();
			exp.setAttributeValue(usuari); //Passar el codi d'usuari
			exp.setDataTypeAttributeValue(DataType.STRING_TYPE);
			exp.setExpressionType("subject");
			exp.setAttributeDesignator("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
			exp.setDataTypeAttributeDesignator(DataType.STRING_TYPE);
			exp.setName(com.soffid.iam.addons.xacml.common.FunctionEnumeration.fromString(java.lang.String.valueOf("STRING_EQUAL")));  //Function tipus true
			exp.setOrder( subExpressionCol.size() + 1);
			
			subExpressionCol.add(exp);
		}
	}

	//Geters i seters per recuperar i assignar valors als binders des del zuls
	public void setOrder(int order)
	{
		binder6.setValue(order);
	}
	
	public Integer getOrder()
	{
		return (Integer) binder6.getValue();
	}
	
	public Long getIden()
	{
		return (Long) binder8.getValue();
	}
	
	public void setName(FunctionEnumeration name)
	{
		binder5.setValue(name);
	}
	
	public FunctionEnumeration getName()
	{
		return (FunctionEnumeration) binder5.getValue();
	}
	
	public void setType (String type)
	{
		binder1.setValue(type);
	}
	
	public String getType()
	{
		return (String) binder1.getValue();
	}
	
	public void setSelector(String sel)
	{
		binder3.setValue(sel);
	}

	public String getSelector()
	{
		return (String) binder3.getValue();
	}
	
	public void setAttributeValue(String val)
	{
		binder4.setValue(val);
	}
	
	public String getAttributeValue()
	{
		return (String) binder4.getValue();
	}
	
	public void setDesignator(String des)
	{
		binder2.setValue(des);
	}
	
	public String getDesignator()
	{
		return (String) binder2.getValue();
	}
	
	public void setVariable(String var)
	{
		binder9.setValue(var);
	}
	
	public String getVariable()
	{
		return (String) binder9.getValue();
	}
	
	public void setSubexpression(Collection<Expression> exp)
	{ 
		binder7.setValue(exp);
	}
	
	public void setDataTypeAttributeValue(DataType d)
	{
		binder10.setValue(d);
	}
	 
	public DataType getDataTypeAttributeValue()
	{
		return (DataType) binder10.getValue();
	}
	
	public void setDataTypeAttributeDesignator(DataType d)
	{
		binder11.setValue(d);
	}
	 
	public DataType getDataTypeAttributeDesignator()
	{
		return (DataType) binder11.getValue();
	}
	
	public Collection<Expression> getSubexpression()
	{
		Collection<Expression> list = (Collection<Expression>) binder7.getValue();
		return list;
	}
	
	//Pels tipus d'expressions subject, action, environment i resource el camp attributeDesignator 
	//té el format urn:com:soffid:... 
	//Aquesta funció retalla la capçalera
	private String retalla(String tot){
		String retallat = new String();
		if(tot != null && tot != ""){
			int last = tot.lastIndexOf(":");
			if (last >= 0)
				retallat = tot.substring(last+1);
			else
				retallat = tot;
		}else
			retallat = tot;
		return retallat;
	}

	//Funció que donada una llista i un valor, cerca aquest valor dins la llista i selecciona l'item que el conté.
	public void setElementList(Listbox list, String valorCercat)
	{
		for(int i=0; i<list.getItemCount(); i++)
		{
			Listitem item = list.getItemAtIndex(i);
			if(item.getValue().equals(valorCercat))
			{
				list.setSelectedIndex(i);
			}
		}
	}
	
	public void setElementListDataType(Listbox list, DataType valorCercat)
	{
		for(int i=1; i<list.getItemCount(); i++)
		{
			Listitem item = list.getItemAtIndex(i);
			DataType it = (DataType) item.getValue();
			if(it.equals(valorCercat))
			{
				list.setSelectedIndex(i);
			}
		}
	}
	
	//Lleva del nom de la funció la part corresponent al qualificador, si en té
	//Per exemple: Function_name: INTEGER_ABS, ens quedam amb la funció ABS
	private String retallaName(FunctionEnumeration name, String designator){
		String n = new String();
		if(name.toString().startsWith("TYPE_"))
		{
			n = name.toString().substring(5);
		}	
		else
		{
			n = name.toString();
			if(designator != null && !designator.isEmpty())
			{
				int i = designator.length();
				if(i < n.length())
					n = n.substring(i+1);
			}
		}
		return n;
	}
	
	//Per guardar el nom de la funció a BBDD aquest a de coincidir amb algun dels valors de dins l'enumeration.
	//Aquesta funció fa la correspondència entre funcions i noms a BBDD
	public String composaName(String name, String qualifier){
		String function_name = new String();
		if(name != null && qualifier != null && !qualifier.isEmpty())
		{	
			if(name.equals("ONE_AND_ONLY") || name.equals("BAG_SIZE") || name.equals("IS_IN") ||
					name.equals("BAG") || name.equals("INTERSECTION") || name.equals("AT_LEAST_ONE_MEMBER_OF") ||
					name.equals("UNION") || name.equals("SUBSET") || name.equals("SET_EQUALS"))
			{
				function_name = "TYPE_" + name;
			}
			else
			{
				function_name = qualifier + "_" + name;
			}
			
		}
		else if(name != null)
		{
			function_name = name;
		}
		return function_name;
	}
	
	//Funció que crea la llista de qualificadors per a cada funció que en té.
	public void creaQualifierListbox(Listbox listacrear, String value){
		
		int i = listacrear.getItemCount();
		
		if(i>=1)
		{
			for(int j=0;j<i;j++)
			{
				listacrear.removeItemAt(0);
			}
		}
		
		
		if(value.equals("MOD"))
		{
			if(listacrear.getItemCount()<1 )
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Integer"),"INTEGER");
			}
			else if (listacrear.getItemCount()==1 && listacrear.getItemAtIndex(0).getValue().equals(""))
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Integer"),"INTEGER");
		}
		else if(value.equals("ADD_DAYTIMEDURATION") || value.equals("SUBTRACT_DAYTIMEDURATION"))
		{
			if(listacrear.getItemCount()<1)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTime"),"DATETIME");
			}
			else if (listacrear.getItemCount()==1 && listacrear.getItemAtIndex(0).getValue().equals(""))
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTime"),"DATETIME");
		}
		else if(value.equals("ADD_YEARMONTHDURATION") || value.equals("SUBTRACT_YEARMONTHDURATION"))
		{
			if(listacrear.getItemCount()<2)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Date"),"DATE");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTime"),"DATETIME");
			}
		}
		else if(value.equals("MATCH"))
		{	
			if(listacrear.getItemCount()<2)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.X500Name"),"X500NAME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.rfc822Name"),"RFC822NAME");
			}
		}
		else if(value.equals("ADD") || value.equals("SUBTRACT") || value.equals("MULTIPLY") || value.equals("DIVIDE") 
				|| value.equals("ABS"))
		{
			if(listacrear.getItemCount()<2)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Integer"),"INTEGER");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Double"),"DOUBLE");
			}
		}
		else if(value.equals("REGEXP_MATCH"))
		{
			if(listacrear.getItemCount()<6)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.String"),"STRING");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.AnyURI"),"ANYURI");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.IpAddress"),"IPADDRESS");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DnsName"),"DNSNAME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.X500Name"),"X500NAME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.rfc822Name"),"RFC822NAME");
			}
		}
		else if(value.equals("GREATER_THAN") || value.equals("GREATER_THAN_OR_EQUAL") || value.equals("LESS_THAN") 
				|| value.equals("LESS_THAN_OR_EQUAL"))
		{
			if(listacrear.getItemCount()<6)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Integer"),"INTEGER");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Double"),"DOUBLE");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.String"),"STRING");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Time"),"TIME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Date"),"DATE");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTime"),"DATETIME");
			}
		}
		else if(value.equals("BAG_SIZE") || value.equals("BAG") || value.equals("ONE_AND_ONLY") || 
				value.equals("IS_IN") || value.equals("INTERSECTION") || value.equals("AT_LEAST_ONE_MEMBER_OF") || 
				value.equals("UNION") || value.equals("SUBSET") || value.equals("SET_EQUALS") || value.equals("EQUAL"))
		{
			if(listacrear.getItemCount()<14)
			{
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_policySet.Select"),"");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Integer"),"INTEGER");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Double"),"DOUBLE");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.String"),"STRING");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Time"),"TIME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Date"),"DATE");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTime"),"DATETIME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.X500Name"),"X500NAME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.rfc822Name"),"RFC822NAME");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.AnyURI"),"ANYURI");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.DateTimeDuration"),"DAYTIMEDURATION");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.YearMonthDuration"),"YEARMONTHDURATION");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Boolean"),"BOOLEAN");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.HexBinary"),"HEXBINARY");
				listacrear.appendItem(org.zkoss.util.resource.Labels.getLabel("xacml_expressionPanel.Base64Binary"),"BASE64BINARY");
			}
		}
	}
	
	
	public void markAsModified ()
	{
		BindContext ctx = XPathUtils.getComponentContext(this);
		DataSource ds = ctx.getDataSource();
		String path = ctx.getXPath();
		do
		{
			Object obj = ds.getJXPathContext().getValue(path);
			if (obj != null && obj instanceof DataNode)
			{
				((DataNode)obj).update();
				break;
			}
			else
			{
				int last = path.lastIndexOf('/');
				if (last < 0)
					break;
				path = path.substring(0,  last);
			}
		} while (true);
		
	}
	
}
