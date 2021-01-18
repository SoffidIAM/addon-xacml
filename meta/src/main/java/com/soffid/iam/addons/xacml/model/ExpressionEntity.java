//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCX_EXPRES" )
@Depends ({com.soffid.iam.addons.xacml.common.Expression.class,
	com.soffid.iam.addons.xacml.model.VariableDefinitionEntity.class,
	com.soffid.iam.addons.xacml.model.ConditionEntity.class,
	com.soffid.iam.addons.xacml.model.ExpressionEntity.class})
public abstract class ExpressionEntity {

	@Column (name="EXE_VARDEF")
	@Nullable
	public com.soffid.iam.addons.xacml.model.VariableDefinitionEntity variableDefinition;

	@Column (name="EXE_ORDER")
	@Nullable
	public java.lang.Integer order;

	@Column (name="EXE_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="EXE_TEN_ID")
	TenantEntity tenant;


	@Column (name="EXE_CONDIC")
	@Nullable
	public com.soffid.iam.addons.xacml.model.ConditionEntity condicion;

	@Column (name="EXE_NAME")
	@Nullable
	public com.soffid.iam.addons.xacml.common.FunctionEnumeration name;

	@Column (name="EXE_EXPTYP", length=50)
	public java.lang.String expressionType;

	@Column (name="EXE_ATRSEL", length=250)
	@Nullable
	public java.lang.String attributeSelector;

	@Column (name="EXE_EXPRES")
	@Nullable
	public com.soffid.iam.addons.xacml.model.ExpressionEntity expression;

	@ForeignKey (foreignColumn="EXE_EXPRES")
	public java.util.Collection<com.soffid.iam.addons.xacml.model.ExpressionEntity> subexpression;

	@Column (name="EXE_VARIID", length=250)
	@Nullable
	public java.lang.String variableId;

	@Column (name="EXP_DATVAL", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeValue;

	@Column (name="EXP_DATDES", length=250)
	@Nullable
	public com.soffid.iam.addons.xacml.common.DataType dataTypeAttributeDesignator;

	@Column (name="EXP_ATTVAL", length=250)
	@Nullable
	public java.lang.String attributeValue;

	@Column (name="EXP_ATTDES", length=250)
	@Nullable
	public java.lang.String attributeDesignator;

	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ExpressionEntity create(
		com.soffid.iam.addons.xacml.common.Expression expression, 
		com.soffid.iam.addons.xacml.model.ConditionEntity condition)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ExpressionEntity create(
		com.soffid.iam.addons.xacml.common.Expression expression, 
		com.soffid.iam.addons.xacml.model.VariableDefinitionEntity variable)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@DaoOperation
	public void update(
		com.soffid.iam.addons.xacml.common.Expression expression)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoFinder("select e \nfrom com.soffid.iam.addons.xacml.model.ExpressionEntity as e\nwhere e.condicion.id=:condicionId\n")
	public com.soffid.iam.addons.xacml.model.ExpressionEntity findByCondition(
		java.lang.Long condicionId) {
	 return null;
	}
	@DaoFinder("select e \nfrom com.soffid.iam.addons.xacml.model.ExpressionEntity as e\nwhere e.variableDefinition.id=:variableDefinitionId\n")
	public com.soffid.iam.addons.xacml.model.ExpressionEntity findByVariable(
		long variableDefinitionId) {
	 return null;
	}
	@DaoFinder
	public com.soffid.iam.addons.xacml.model.ExpressionEntity findByExpressionId(
		java.lang.Long id) {
	 return null;
	}
	@DaoOperation
	public void remove(
		com.soffid.iam.addons.xacml.common.Expression expression)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@DaoFinder
	public java.util.List<com.soffid.iam.addons.xacml.model.ExpressionEntity> findByExpressionPare(
		com.soffid.iam.addons.xacml.model.ExpressionEntity expression) {
	 return null;
	}
	@DaoOperation
	public com.soffid.iam.addons.xacml.model.ExpressionEntity create(
		com.soffid.iam.addons.xacml.common.Expression expression)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
