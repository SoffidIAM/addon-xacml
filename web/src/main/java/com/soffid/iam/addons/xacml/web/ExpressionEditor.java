package com.soffid.iam.addons.xacml.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.bouncycastle.util.Arrays;
import org.jboss.security.xacml.sunxacml.attr.Base64BinaryAttribute;
import org.jboss.security.xacml.sunxacml.attr.BooleanAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateTimeAttribute;
import org.jboss.security.xacml.sunxacml.attr.DayTimeDurationAttribute;
import org.jboss.security.xacml.sunxacml.attr.DoubleAttribute;
import org.jboss.security.xacml.sunxacml.attr.HexBinaryAttribute;
import org.jboss.security.xacml.sunxacml.attr.IntegerAttribute;
import org.jboss.security.xacml.sunxacml.attr.RFC822NameAttribute;
import org.jboss.security.xacml.sunxacml.attr.TimeAttribute;
import org.jboss.security.xacml.sunxacml.attr.X500NameAttribute;
import org.jboss.security.xacml.sunxacml.attr.YearMonthDurationAttribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;

import com.soffid.iam.addons.xacml.common.DataType;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.web.component.CustomField3;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.SingletonBinder;
import es.caib.zkib.component.DataTree2;
import es.caib.zkib.component.Div;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.events.XPathEvent;
import es.caib.zkib.events.XPathSubscriber;

public class ExpressionEditor extends Div implements XPathSubscriber, AfterCompose { 
	SingletonBinder binder = new SingletonBinder(this);
	private Expression activeExpression;
	private CustomField3 attributeValue;
	private String activePath;
	private CustomField3 attributeValueDataType;
	private CustomField3 expressionType;
	private CustomField3 functionFamily;
	private CustomField3 function;
	private CustomField3 attributeDesignator;
	private CustomField3 attributeSelector;
	private CustomField3 functionDataType;
	private CustomField3 variable;
	private boolean duringUpdate = false;
	private Expression parentExpression = null;
	private Expression rootExpression;
	
	public void setDataPath(String path) {
		binder.setDataPath(path);
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		binder.setPage(newpage);
	}

	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		binder.setPage(null);
	}

	@Override
	public void onUpdate(XPathEvent event) {
		try {
			if (!duringUpdate)
				updateTree();
		} catch (JSONException | IOException e) {
			throw new UiException(e);
		}
	}

	@Override
	public Object clone() {
		ExpressionEditor o = (ExpressionEditor) super.clone();
		o.binder = new SingletonBinder(o);
		return o;
	}

	@Override
	public void afterCompose() {
		try {
			expressionType = (CustomField3) getFellow("expressionType");
			List<String> values = new LinkedList<>();
			values.add("attributeValue: "+Labels.getLabel("xacml_expressionPanel.Value") );
			values.add("resource: "+Labels.getLabel("xacml_expressionPanel.Resource") );
			values.add("subject: "+Labels.getLabel("xacml_expressionPanel.Subject") );
			values.add("action: "+Labels.getLabel("xacml_expressionPanel.Action") );
			values.add("environment: "+Labels.getLabel("xacml_expressionPanel.Environment") );
			values.add("attributeSelector: "+Labels.getLabel("xacml_expressionPanel.Selector") );
			values.add("variable: "+Labels.getLabel("xacml_expressionPanel.Variable") );
			values.add("function: "+Labels.getLabel("xacml_expressionPanel.Function") );
			values.add("name: "+Labels.getLabel("xacml_expressionPanel.FunctionName") );
			expressionType.setListOfValues(values.toArray(new String[values.size()]));
			expressionType.updateMetadata();
		
			functionFamily = (CustomField3) getFellow("functionFamily");
			values = new LinkedList<>();
			for ( String s: ExpressionHelper.getCategories() ) {
				String label = Labels.getLabel("xacml_expressioPanel."+s);
				if (label == null)
					values.add(s);
				else
					values.add(s+":"+label);
			}
			functionFamily.setListOfValues(values.toArray(new String[values.size()]));
			functionFamily.updateMetadata();
			
			function = (CustomField3) getFellow("function");
			attributeDesignator = (CustomField3) getFellow("attributeDesignator");
			attributeSelector = (CustomField3) getFellow("attributeSelector");
			attributeValue = (CustomField3) getFellow("attributeValue");
			attributeValueDataType = (CustomField3) getFellow("attributeValueDataType");
			functionDataType = (CustomField3) getFellow("functionDataType");
			variable = (CustomField3) getFellow("variable");
			updateTree();
		} catch (Exception e) {
			throw new UiException(e);
		}
	}

	private void updateTree() throws JSONException, IOException {
		DataTree2 dt = (DataTree2) getFellow("dt");
		JSONObject data = new JSONObject();
		JSONArray children = new JSONArray();
		data.put("children", children);
		if (binder.isValid()) {
			Expression e = (Expression) binder.getValue();
			children.put (render(e));
			activeExpression = e;
			parentExpression  = null;
			rootExpression = e;
			dt.setData(data);
			dt.setSelectedIndex(new int[] {0});
		} else {
			dt.setData(data);
			rootExpression = null;
			activeExpression = null;
			parentExpression = null;
		}
		updateForm();
	}

	
	private JSONObject render(Expression e) throws JSONException, IOException {
		String description = ExpressionHelper.getShortDescription(e);
		int minChildren = ExpressionHelper.getMinChildren(e);
		int maxChildren = ExpressionHelper.getMaxChildren(e);
		
		JSONObject o = new JSONObject();
		o.put("type", "expression");
		o.put("value", description);
		if (maxChildren == 0) {
			o.put("leaf", true);
			o.put("children", new JSONArray());
		} else {
			if (e.getSubexpression() == null)
				e.setSubexpression( new LinkedList<>() );
			JSONArray children = new JSONArray();
			o.put("children", children);
			Iterator<Expression> it = e.getSubexpression().iterator();
			for (int i = 0; i < minChildren || i < e.getSubexpression().size(); i++) {
				Expression s;
				if ( i >= e.getSubexpression().size()) {
					s = newExpression(e);
				} else {
					s = it.next();
				}
				children.put(render(s));
			}
			if (e.getSubexpression().size() < maxChildren || maxChildren == -1) {
				o.put("tail", "<button class=\"small-button\" onclick=\"zkDatatree2.sendClientAction(this, 'onAddExpression')\">"+
						Labels.getLabel("xacml_policySet.AddNew")+
						"</button>");
			}
		}
		return o;
	}

	public void onSelect(Event event) throws JSONException, IOException {
		DataTree2 dt = (DataTree2) event.getTarget();
		int selected[] = dt.getSelectedItem();
		
		Expression e = rootExpression;
		Expression p = null;
		Collection<Expression> l = new LinkedList<Expression>();
		l.add(e);
		
		String path = "";
		for (int i = 1; i < selected.length; i++) {
			path = path + "/subexpression["+selected[i]+"]";
		}
		activePath = path;
		
		e = null;
		for (int i = 0; i < selected.length; i++) {
			if (l == null)
				return;
			Iterator<Expression> iterator = l.iterator();
			for (int j = 0; j < selected[i]; j++) {
				if (iterator.hasNext()) iterator.next();
			}
			if (iterator.hasNext()) {
				p = e;
				e = iterator.next();
				l = e.getSubexpression();
			} else {
				return;
			}
		}
		
		parentExpression = p;
		activeExpression = e;
		
		updateForm();
	}
	
	public void addExpression(Event event) throws JSONException, IOException {
		DataTree2 dt = (DataTree2) getFellow("dt");

		Expression e = newExpression(activeExpression);
		refreshActiveTree();
		int size = activeExpression.getSubexpression().size();
		int[] item = dt.getSelectedItem();
		int[] newItem = Arrays.copyOf(item, item.length+1);
		newItem[item.length] = size - 1;
		dt.setSelectedIndex(newItem);
		
		parentExpression = activeExpression;
		activeExpression = e;
		
		updateForm();
	}
	
	public void removeExpression (Event event) throws JSONException, IOException {
		DataTree2 dt = (DataTree2) getFellow("dt");

		int[] item = dt.getSelectedItem();
		
		parentExpression.getSubexpression().remove(activeExpression);
		int[] newItem = Arrays.copyOf(item, item.length-1);
		
		dt.setSelectedIndex(newItem);
		JSONObject data = render(parentExpression);
		dt.updateCurrentBranch(data);

		if ( item [item.length-1] >= parentExpression.getSubexpression().size()) {
			item [item.length-1] --;
		}
		
		Iterator<Expression> it = parentExpression.getSubexpression().iterator();
		for (int i = 0; i <= item[item.length-1]; i++) {
			activeExpression = it.next();
		}
		dt.setSelectedIndex(item);
		
		updateForm();
	}

	public void updateValue(Event event) throws JSONException, IOException {
		activeExpression.setAttributeValue((String) attributeValue.getValue());
		refreshActiveTree();
		validateValue();
	}
	
	private void validateValue() {
		try {
			attributeValue.setWarning(null, "");
			String v = (String) attributeValue.getValue();
			if (v != null) {
				if (activeExpression.getDataTypeAttributeValue() == DataType.HEX_BINARY_TYPE)
					HexBinaryAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.BASE_64BINARY_TYPE)
					Base64BinaryAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.BOOLEAN_TYPE)
					BooleanAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.DATE_TIME_TYPE)
					DateTimeAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.DATE_TYPE)
					DateAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.DAY_TIME_DURATION_TYPE)
					DayTimeDurationAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.DOUBLE_TYPE)
					DoubleAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.INTEGER_TYPE)
					IntegerAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.RFC_822NAME_TYPE)
					RFC822NameAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.TIME_TYPE)
					TimeAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.X_500NAME_TYPE)
					X500NameAttribute.getInstance(v);
				if (activeExpression.getDataTypeAttributeValue() == DataType.YEAR_MONTH_DURATION_TYPE)
					YearMonthDurationAttribute.getInstance(v);
			}
		} catch (Exception e) {
			String msg = "Expected ";
			if (activeExpression.getDataTypeAttributeValue() == DataType.HEX_BINARY_TYPE)
				msg += "hexadecimal data";
			if (activeExpression.getDataTypeAttributeValue() == DataType.BASE_64BINARY_TYPE)
				msg += "base 64 data";
			if (activeExpression.getDataTypeAttributeValue() == DataType.BOOLEAN_TYPE)
				msg += "true or false";
			if (activeExpression.getDataTypeAttributeValue() == DataType.DATE_TIME_TYPE)
				msg += "yyyy-mm-ddThh:mm:ss[Z] format.\nFor instance, 1970-06-26T19:20:00Z";
			if (activeExpression.getDataTypeAttributeValue() == DataType.DATE_TYPE)
				msg += "yyyy-mm-dd format.\nFor instance, 1970-06-26";
			if (activeExpression.getDataTypeAttributeValue() == DataType.DAY_TIME_DURATION_TYPE)
				msg += "P[00D][00H][00M][00S] format.\nFor instance: P1H30M";
			if (activeExpression.getDataTypeAttributeValue() == DataType.DOUBLE_TYPE)
				msg += "decimal number";
			if (activeExpression.getDataTypeAttributeValue() == DataType.INTEGER_TYPE)
				msg += "integer number";
			if (activeExpression.getDataTypeAttributeValue() == DataType.RFC_822NAME_TYPE)
				msg += "RFC822 name";
			if (activeExpression.getDataTypeAttributeValue() == DataType.TIME_TYPE)
				msg += "hh:mm:ss[Z] format.\nFor instance 19:20:00Z";
			if (activeExpression.getDataTypeAttributeValue() == DataType.X_500NAME_TYPE)
				msg += "X500 name";
			if (activeExpression.getDataTypeAttributeValue() == DataType.YEAR_MONTH_DURATION_TYPE)
				msg += "P[00Y][00M] format.\nFor instance, P1Y6M";
			attributeValue.setWarning(null, msg);
		}
	}

	public void updateVariable(Event event) throws JSONException, IOException {
		activeExpression.setVariableId((String) variable.getValue());
		refreshActiveTree();
	}

	public void updateExpressionType(Event event) throws JSONException, IOException {
		String type = (String) expressionType.getValue();
		activeExpression.setExpressionType(type);
		updateForm();
	}
	
	public void updateFunctionType(Event event) throws JSONException, IOException {
		String category = (String) functionFamily.getValue();
		activeExpression.setExpressionType("function_"+category);
		updateFunctionsList(category);
	}

	public void updateForm() throws JSONException, IOException {
		attributeValueDataType.setVisible(false);
		attributeValue.setVisible(false);
		attributeDesignator.setVisible(false);
		attributeSelector.setVisible(false);
		functionFamily.setVisible(false);
		variable.setVisible(false);
		function.setVisible(false);
		functionDataType.setVisible(false);
		Component removeIcon = getFellow("removeIcon");
		
		if (activeExpression != null) {
			expressionType.setValue(activeExpression.getExpressionType());
			expressionType.setVisible(true);
			if ("attributeValue".equals(activeExpression.getExpressionType())) {
				attributeValue.setValue(activeExpression.getAttributeValue());
				attributeValue.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeValue());
				attributeValueDataType.setVisible(true);
			}
			if ("attributeSelector".equals(activeExpression.getExpressionType())) {
				attributeSelector.setValue(activeExpression.getAttributeSelector());
				attributeSelector.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeValue());
				attributeValueDataType.setVisible(true);
			}
			if ("resource".equals(activeExpression.getExpressionType())) {
				attributeDesignator.setValue(activeExpression.getAttributeDesignator());
				attributeDesignator.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeDesignator());
				attributeValueDataType.setVisible(true);
				ExpressionHelper.configureResourceDesignator(attributeDesignator);
			}
			if ("subject".equals(activeExpression.getExpressionType())) {
				attributeDesignator.setValue(activeExpression.getAttributeDesignator());
				attributeDesignator.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeDesignator());
				attributeValueDataType.setVisible(true);
				ExpressionHelper.configureSubjectDesignator(attributeDesignator);
			}
			if ("action".equals(activeExpression.getExpressionType())) {
				attributeDesignator.setValue(activeExpression.getAttributeDesignator());
				attributeDesignator.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeDesignator());
				attributeValueDataType.setVisible(true);
				ExpressionHelper.configureActionDesignator(attributeDesignator);
			}
			if ("environment".equals(activeExpression.getExpressionType())) {
				attributeDesignator.setValue(activeExpression.getAttributeDesignator());
				attributeDesignator.setVisible(true);
				attributeValueDataType.setValue(activeExpression.getDataTypeAttributeDesignator());
				attributeValueDataType.setVisible(true);
				ExpressionHelper.configureEnvironmentDesignator(attributeDesignator);
			}
			if ("variable".equals(activeExpression.getExpressionType())) {
				variable.setValue(activeExpression.getAttributeSelector());
				variable.setVisible(true);
			}
			if (activeExpression.getExpressionType() != null &&
					activeExpression.getExpressionType().startsWith("function")) {
				expressionType.setValue("function");
				functionFamily.setVisible(true);
				functionDataType.setValue(activeExpression.getAttributeDesignator());
				if (activeExpression.getName() != null) {
					JSONObject f = ExpressionHelper.findFunction(activeExpression.getName().getValue());
					String name = f.getString("name");
					String category = ExpressionHelper.getFunctionCategory(name);
					functionFamily.setValue(category);
					function.setVisible(true);
					updateFunctionsList(category);
					function.setValue(name);
					updateQualifiers(name, f);
				}
				else if (activeExpression.getExpressionType().startsWith("function_")) {
					String category = activeExpression.getExpressionType().substring(9);
					functionFamily.setValue(category);
					function.setVisible(true);
					updateFunctionsList(category);
					function.setValue("");
				} else {
					functionFamily.setValue("");
					function.setVisible(false);
				}
			}
			if ("name".equals( activeExpression.getExpressionType())) {
				functionFamily.setVisible(true);
				functionDataType.setValue(activeExpression.getAttributeDesignator());
				
				if (activeExpression.getName() != null) {
					JSONObject f = ExpressionHelper.findFunction(activeExpression.getName().getValue());
					String category = ExpressionHelper.getFunctionCategory(f.optString("name"));
					functionFamily.setValue(category);
					function.setVisible(true);
					updateFunctionsList(category);
				} else {
					functionFamily.setValue("");
					function.setVisible(false);
				}
				String fn;
				if ( activeExpression.getName() == null) {
					fn = "EQUAL";
				} else {
					fn = activeExpression.getName().getValue();
				}
				JSONObject f = ExpressionHelper.findFunction(fn);
				if (f != null) {
					String name = f.optString("name");
					function.setValue(name);
					updateQualifiers(name, f);
				}
			}
			
			if (parentExpression == null)
				removeIcon.setVisible(false);
			else
			{
				int min = ExpressionHelper.getMinChildren(parentExpression);
				if (parentExpression.getSubexpression().size() > min) 
					removeIcon.setVisible(true);
				else
					removeIcon.setVisible(false);
			}
		} else {
			removeIcon.setVisible(false);
			expressionType.setVisible(false);
		}
	}

	private void updateFunctionsList(String category) throws JSONException, IOException {
		if (category == null || category.trim().isEmpty())
			function.setVisible(false);
		else {
			LinkedList<String> values = new LinkedList<>();
			for ( String s: ExpressionHelper.getCategoryFunctions(category) ) {
				String label = Labels.getLabel("xacml_expressionPanel."+s);
				if (label == null)
					values.add(s);
				else
					values.add(s+":"+label);
			}
			function.setListOfValues(values.toArray(new String[values.size()]));
			function.setVisible(true);
			function.updateMetadata();
		}
	}

	
	void refreshActiveTree() throws JSONException, IOException {
		JSONObject data = render(activeExpression);
		
		DataTree2 dt = (DataTree2) getFellow("dt");
		dt.updateCurrentBranch(data);
		
		duringUpdate = true;
		try {
			binder.setValue(new Expression(rootExpression));
		} finally {
			duringUpdate  = false;
		}
	}
	
	public void updateFunction(Event ev) throws JSONException, IOException {
		DataTree2 dt = (DataTree2) getFellow("dt");

		String fn = (String) function.getValue();
		
		JSONObject f = ExpressionHelper.findFunction(fn);
		if (f != null) {
			updateQualifiers(fn, f);
			int min = f.optInt("min");
			int max = f.optInt("max");
			Collection<Expression> children = activeExpression.getSubexpression();
			if (children == null) 
				activeExpression.setSubexpression(children = new LinkedList<>());
			while ( children.size() < min ) {
				Expression child = newExpression(activeExpression);
			}
			
			if (max >= 0) {
				Iterator<Expression> iterator = children.iterator();
				for (int i = 0; i < max && iterator.hasNext(); i++) iterator.next();
				while (iterator.hasNext()) {iterator.next(); iterator.remove(); }
			}
			
		}
		
		refreshActiveTree();
	}

	public DataType dataTypeFromText(String s) {
		if (s == null)
			return null;
		List<String> l = DataType.literals();
		List<String> n = DataType.names();
		for (int i = 0; i < l.size(); i++) {
			if (n.get(i).equalsIgnoreCase(s) ||
					n.get(i).equalsIgnoreCase(s+"_TYPE"))
			{
				return DataType.fromString(l.get(i));
			}
		}
		return null;
	}
	
	public void updateQualifiers(String fn, JSONObject f) {
		if (f.has("qualifiers")) {
			JSONArray validQualifiers = f.getJSONArray("qualifiers");

			if (validQualifiers.isEmpty()) {
				functionDataType.setVisible(false);
				activeExpression.setName(FunctionEnumeration.fromString(fn));
			} else {
				String q, qt = "STRING";
				String cq = (String) functionDataType.getValue();
				DataType cqt = dataTypeFromText(cq);
				if (cqt != null && isValidType (cqt.toString(), validQualifiers))
					q = cqt.toString();
				else
					q = validQualifiers.getString(0);
				LinkedList<String> values = new LinkedList<>();
				List<String> names = DataType.names();
				List<String> literals = DataType.literals();
				for (int i = 0; i < names.size(); i++) {
					String name = names.get(i);
					String literal = literals.get(i);
					if (isValidType(literal, validQualifiers)) {
						String s = name+":"+ Labels.getLabel("com.soffid.iam.addons.xacml.common.DataType."+name);
						values.add(s);
						if (q.equals(literal))
							qt = name;
					}
				}
				functionDataType.setListOfValues(values.toArray(new String[values.size()]));
				functionDataType.setValue(qt);
				functionDataType.setVisible(true);
				functionDataType.updateMetadata();
				if (qt.endsWith("_TYPE"))
					qt = qt.substring(0, qt.length()-5);
				qt = qt.replaceAll("_", "");
				if (FunctionEnumeration.literals().contains("TYPE_"+fn))
					activeExpression.setName(FunctionEnumeration.fromString("TYPE_"+fn));
				else
					activeExpression.setName(FunctionEnumeration.fromString(qt+"_"+fn));
				activeExpression.setAttributeDesignator(qt);
			}
		} else {
			functionDataType.setVisible(false);
			activeExpression.setName(FunctionEnumeration.fromString(fn));
		}
	}

	public void updateFunctionQualifier(Event ev) throws JSONException, IOException {
		DataTree2 dt = (DataTree2) getFellow("dt");

		String fn = (String) function.getValue();
		String qt = (String) functionDataType.getValue();
		if (qt.endsWith("_TYPE"))
			qt = qt.substring(0, qt.length()-5);
		qt = qt.replaceAll("_", "");
		activeExpression.setAttributeDesignator(qt);
		if (FunctionEnumeration.literals().contains("TYPE_"+fn))
			activeExpression.setName(FunctionEnumeration.fromString("TYPE_"+fn));
		else
			activeExpression.setName(FunctionEnumeration.fromString(qt+"_"+fn));
		refreshActiveTree();
	}

	public void updateDataType(Event ev) throws JSONException, IOException {
		DataType dt = (DataType) this.attributeValueDataType.getValue();
		activeExpression.setDataTypeAttributeDesignator(dt);
		activeExpression.setDataTypeAttributeValue(dt);
		refreshActiveTree();
		validateValue();
	}

	private boolean isValidType(String cqt, JSONArray validQualifiers) {
		for (int i = 0; i < validQualifiers.length(); i++)
			if (cqt != null && cqt.equals(validQualifiers.optString(i)))
				return true;
		return false;
	}

	private Expression newExpression(Expression parent) {
		Expression expression = new Expression();
		expression.setOrder(0);
		expression.setExpressionType("attributeValue");
		expression.setDataTypeAttributeValue(DataType.BOOLEAN_TYPE);
		expression.setDataTypeAttributeDesignator(DataType.STRING_TYPE);
		expression.setAttributeValue("true");
		expression.setName(FunctionEnumeration.STRING_EQUAL);
		if (parent != null) {
			int i = 0;
			for (Expression child: parent.getSubexpression()) {
				if (child.getOrder().intValue() >= i) i = child.getOrder().intValue() + 1;
			}
			expression.setOrder(i);
			parent.getSubexpression().add(expression);
		}
		return expression;
	}
	
	public void updateAttributeDesignator(Event event) throws JSONException, IOException {
		if (attributeDesignator.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-time"))
			attributeValueDataType.setValue(DataType.TIME_TYPE);
		else if (attributeDesignator.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-date"))
			attributeValueDataType.setValue(DataType.DATE_TYPE);
		else if (attributeDesignator.getValue().equals("urn:oasis:names:tc:xacml:1.0:environment:current-dateTime"))
			attributeValueDataType.setValue(DataType.DATE_TIME_TYPE);
		else
			attributeValueDataType.setValue(DataType.STRING_TYPE);
		activeExpression.setAttributeDesignator((String) attributeDesignator.getValue());
		activeExpression.setDataTypeAttributeValue((DataType) attributeValueDataType.getValue());
		activeExpression.setDataTypeAttributeDesignator((DataType) attributeValueDataType.getValue());
		refreshActiveTree();
	}

	public void updateAttributeSelector(Event event) throws JSONException, IOException {
		activeExpression.setAttributeSelector((String) attributeSelector.getValue());
		activeExpression.setDataTypeAttributeValue((DataType) attributeValueDataType.getValue());
		activeExpression.setDataTypeAttributeDesignator((DataType) attributeValueDataType.getValue());
		refreshActiveTree();
	}
}
