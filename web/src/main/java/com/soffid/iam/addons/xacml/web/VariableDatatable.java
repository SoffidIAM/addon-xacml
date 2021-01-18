package com.soffid.iam.addons.xacml.web;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.soffid.iam.addons.xacml.common.VariableDefinition;

import es.caib.zkib.component.DataTable;
import es.caib.zkib.datamodel.DataNode;

public class VariableDatatable extends DataTable {

	@Override
	protected JSONObject getClientValue(Object element) throws JSONException {
		JSONObject o = new JSONObject();
		VariableDefinition def = (VariableDefinition) ((DataNode)element).getInstance();
		o.put("variableId", def.getVariableId());
		try {
			o.put("expression", ExpressionHelper.getLongDescription(def.getExpression()));
		} catch (IOException e) {
			o.put("expression", "");
		}
		return o;
	}

}
