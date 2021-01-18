package com.soffid.iam.addons.xacml.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zkoss.io.URLReader;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;

import com.ibm.icu.util.BytesTrie.Iterator;
import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.FunctionEnumeration;
import com.soffid.iam.api.DataType;
import com.soffid.iam.service.ejb.AdditionalDataService;
import com.soffid.iam.web.component.CustomField3;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkdb.yaml.Yaml2Json;
import es.caib.zkib.component.DataModel;
import es.caib.zkib.datamodel.DataNodeCollection;

public class ExpressionHelper {
	public static JSONArray data = null;
	
	static JSONObject findFunction(String fn) throws JSONException, IOException {
		parse();
		for (int i = 0; i < data.length(); i++) {
			JSONObject o = data.getJSONObject(i);
			JSONArray f = o.getJSONArray("functions");
			for (int j = 0; j < f.length(); j++) {
				String name = f.getJSONObject(j).optString("name");
				if (fn.endsWith(name))
				{
					return f.getJSONObject(j);
				}
			}
		}
		return null;
	}
	
	public static JSONArray parse() throws JSONException, IOException {
		if (data == null || true)
		{
			Reader r = new InputStreamReader(ExpressionHelper.class.getResourceAsStream("functions.yaml"), "UTF-8");
			StringBuffer sb = new StringBuffer();
			for (int i = r.read(); i >= 0; i = r.read())
				sb.append((char) i);
			data = new JSONArray(new Yaml2Json().transform(sb.toString()));
		}
		return data;
	}
	
	public static int getMinChildren(Expression e) throws JSONException, IOException {
		int minChildren = 0; // Default
		if (e == null || e.getExpressionType() == null)
			return 0;
		if (e.getExpressionType().startsWith("function")) {
			String fn = e.getName().getValue();
			JSONObject o = findFunction(fn);
			if (o != null)
				minChildren = o.optInt("min");
		}
		return minChildren;
	}
	
	public static int getMaxChildren(Expression e) throws JSONException, IOException {
		int maxChildren = 0; // Default
		if (e == null || e.getExpressionType() == null)
			return 0;
		parse();
		if (e.getExpressionType().startsWith("function")) {
			String fn = e.getName().getValue();
			JSONObject o = findFunction(fn);
			if (o != null)
				maxChildren = o.optInt("max");
		}
		return maxChildren;
	}
	
	public static List<String[]> getSubjects() throws InternalErrorException, NamingException, CreateException {
		List<String[]> l = new LinkedList<>();
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.User"), "urn:com:soffid:xacml:subject:user"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.SubjectId"), "urn:oasis:names:tc:xacml:1.0:subject:subject-id"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.Roles"), "urn:oasis:names:tc:xacml:2.0:subject:role"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.Groups"), "urn:com:soffid:xacml:subject:group"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.PrimaryGroup"), "urn:com:soffid:xacml:subject:primaryGroup"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.SubjectIdQualifier"), "urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.IPaddress"), "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.OperatingSystem"), "urn:com:soffid:host:os"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.DhcpParams"), "urn:com:soffid:host:dhcp"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.HostName"), "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.OpenIdIssuer"), "urn:com:soffid:oidc:iss"});
		l.add ( new String [] { Labels.getLabel ("xacml_policySet.OpenIdAudience"), "urn:com:soffid:oidc:aud"});
		AdditionalDataService ejb = EJBLocator.getAdditionalDataService();
		for ( DataType tipusDada: ejb.findDataTypes2(com.soffid.iam.api.MetadataScope.USER))
		{
			if ( tipusDada.getType() != es.caib.seycon.ng.comu.TypeEnumeration.SEPARATOR)
				l.add (new String [] {new com.soffid.iam.web.WebDataType( tipusDada ).getLabel(), 
					"urn:com:soffid:xacml:subject:user:att:"+tipusDada.getName()});
		}
		return l;
	}
	
	public static String getShortDescription(Expression e) throws JSONException, IOException {
		String description =  "??";
		
		if (e == null || e.getExpressionType() == null)
			return "";
		parse();
		FunctionEnumeration function = e.getName();
		if (e.getExpressionType().startsWith("function")) {
			return getFunctionDescription(function);
		}

		if (e.getExpressionType().equals("name")) {
			return Labels.getLabel("xacml_expressionPanel.FunctionName")+" "+getFunctionDescription(function);
		}

		if (e.getExpressionType().equals("resource")) {
			if (e.getAttributeDesignator().equals("urn:oasis:names:tc:xacml:1.0:resource:resource-location"))
				return Labels.getLabel("xacml_policySet.Url");
			if (e.getAttributeDesignator().equals("com:soffid:iam:xacml:1.0:resource:soffid-object"))
				return Labels.getLabel("xacml_policySet.SoffidObject");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:subject:account"))
				return Labels.getLabel("xacml_policySet.AccountName");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:subject:system"))
				return Labels.getLabel("xacml_policySet.SystemName");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:resource:loginName"))
				return Labels.getLabel("xacml_policySet.LoginName");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:resource:vault"))
				return Labels.getLabel("com.soffid.iam.api.Account.vaultFolder");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:resource:access-level"))
				return Labels.getLabel("xacml_policySet.accessLevel");
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:resource:server-url"))
				return Labels.getLabel("xacml_policySet.targetURL");
			description = Labels.getLabel("xacml_expressionPanel.Resource")+" "+e.getAttributeDesignator();
		}
		if (e.getExpressionType().equals("subject")) {
			try {
				for (String[] l: getSubjects()) {
					if (l[1].equals(e.getAttributeDesignator()))
						return l[0];
				}
			} catch (InternalErrorException | NamingException | CreateException e1) {
			}
			description = Labels.getLabel("xacml_expressionPanel.Subject")+" "+e.getAttributeDesignator();			
		}
		if (e.getExpressionType().equals("action")) {

			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:action:method"))
					return Labels.getLabel("xacml_policySet.Method");
			description = Labels.getLabel("xacml_expressionPanel.Action")+" "+e.getAttributeDesignator();
		}
		if (e.getExpressionType().equals("environment")) {
			if (e.getAttributeDesignator().equals("urn:com:soffid:xacml:environment:country"))
				return Labels.getLabel("xacml_policySet.Country");
			if (e.getAttributeDesignator().equals("urn:oasis:names:tc:xacml:1.0:environment:current-time"))
				return Labels.getLabel("xacml_policySet.CurrentTime");
			if (e.getAttributeDesignator().equals("urn:oasis:names:tc:xacml:1.0:environment:current-date"))
				return Labels.getLabel("xacml_policySet.CurrentDate");
			if (e.getAttributeDesignator().equals("urn:oasis:names:tc:xacml:1.0:environment:current-dateTime"))
				return Labels.getLabel("xacml_policySet.CurrentDateTime");
			description = Labels.getLabel("xacml_expressionPanel.Environment")+" "+e.getAttributeDesignator();
		}
		if (e.getExpressionType().equals("attributeSelector")) {
			description = Labels.getLabel("xacml_expressionPanel.Attribute")+" "+e.getAttributeSelector();
		}
		if (e.getExpressionType().equals("attributeValue")) {
			description = "\""+e.getAttributeValue()+"\"";
		} 
		if (e.getExpressionType().equals("variable")) {
			description = e.getVariableId();
		}
		return description;
	}

	public static String getFunctionDescription(FunctionEnumeration function) throws IOException {
		String description;
		JSONObject f = ExpressionHelper.findFunction(function.getValue());
		if (f == null) 
			description = function.getValue();
		else {
			description =  f.optString("name");
			String label = Labels.getLabel("xacml_expressionPanel."+description);
			if (label == null)
				return description;
			else
				return label;
		}
		return description;
	}

	public static String getLongDescription(Expression e) throws JSONException, IOException {
		if (e == null || e.getExpressionType() == null)
			return "";
		parse();
		if (e.getExpressionType().startsWith("function") && 
				e.getSubexpression() != null && 
				!e.getSubexpression().isEmpty() ) 
		{
			JSONObject o = findFunction(e.getName().getValue());
			String label = getFunctionDescription(e.getName());
			if (o != null) {
				String operator = o.optString("operator");
				if (operator == null || operator.trim().isEmpty())
				{
					String s = null;
					for ( java.util.Iterator<Expression> it = e.getSubexpression().iterator(); it.hasNext();) {
						if (s == null) s = label+"(";
						else s = s +", ";
						s = s + getLongDescription(it.next());
					}
					s = s + ")";
					return s;
				} else {
					String s = null;
					for ( java.util.Iterator<Expression> it = e.getSubexpression().iterator(); it.hasNext();) {
						if (s == null) s = "( ";
						else s = s + " " + operator + " ";
						s = s + getLongDescription(it.next());
					}
					s = s + " )";
					return s;
				}
			}
			return label;
		} else {
			return getShortDescription(e);
		}
	}
	
	public static List<String> getCategories() throws JSONException, IOException {
		List<String> c = new LinkedList<>();
		parse();
		
		for ( int i = 0; i < data.length(); i++) {
			String name = data.getJSONObject(i).optString("name");
			c.add(name);
		}
		return c;
	}

	public static List<String> getCategoryFunctions(String category) throws JSONException, IOException {
		List<String> c = new LinkedList<>();
		parse();
		
		for ( int i = 0; i < data.length(); i++) {
			String name = data.getJSONObject(i).optString("name");
			if (name.equals(category)) {
				JSONArray f = data.getJSONObject(i).optJSONArray("functions");
				for (int j = 0; j < f.length(); j++)
					c.add(f.getJSONObject(j).optString("name"));
			}
		}
		return c;
	}
	
	public static String getFunctionCategory(String fn) throws JSONException, IOException {
		parse();
		
		for (int i = 0; i < data.length(); i++) {
			JSONObject o = data.getJSONObject(i);
			JSONArray f = o.getJSONArray("functions");
			for (int j = 0; j < f.length(); j++) {
				String name = f.getJSONObject(j).optString("name");
				if (fn.equals(name))
				{
					return o.optString("name");
				}
			}
		}
		return null;
	}

	public static void configureResourceDesignator(CustomField3 field) throws UnsupportedEncodingException {
		field.setValuesPath(null);
		field.setKeysPath(null);
		List<String> values = new LinkedList<>();
		values.add( URLEncoder.encode("urn:oasis:names:tc:xacml:1.0:resource:resource-location", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.Url") );
		values.add( URLEncoder.encode("com:soffid:iam:xacml:1.0:resource:soffid-object", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.SoffidObject") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:subject:account", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.AccountName") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:subject:system", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.SystemName") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:resource:loginName", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.LoginName") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:resource:vault", "UTF-8")+ ":" + Labels.getLabel("com.soffid.iam.api.Account.vaultFolder") );
		values.add( URLEncoder.encode("urn:com:soffid:xacml:resource:access-level", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.accessLevel") );
		field.setListOfValues(values.toArray(new String[values.size()]));
		field.updateMetadata();
	}

	public static void configureSubjectDesignator(CustomField3 field) {
		field.setValuesPath("/model:/metadata/name");
		field.setKeysPath("/model:/metadata/value");
		field.setListOfValues((String[])null);
		field.updateMetadata();
	}

	public static void configureActionDesignator(CustomField3 field) throws UnsupportedEncodingException {
		field.setValuesPath(null);
		field.setKeysPath(null);
		List<String> values = new LinkedList<>();
		values.add( URLEncoder.encode("urn:com:soffid:xacml:action:method", "UTF-8")+ ":" + Labels.getLabel("xacml_policySet.Method") );
		field.setListOfValues(values.toArray(new String[values.size()]));
		field.updateMetadata();
	}

	public static void configureEnvironmentDesignator(CustomField3 field) throws UnsupportedEncodingException {
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
	
}
