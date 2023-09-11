package com.soffid.iam.addons.xacml.service.pool;

import org.jboss.security.xacml.sunxacml.cond.FunctionFactory;
import org.jboss.security.xacml.sunxacml.cond.FunctionFactoryProxy;

public class SoffidFunctionFactoryProxy implements FunctionFactoryProxy {

	private FunctionFactoryProxy defaultInstance;

	public SoffidFunctionFactoryProxy(FunctionFactoryProxy instance) {
		defaultInstance = instance;
	}

	public FunctionFactory getTargetFactory() {
		return SoffidFunctionFactory.getTargetFactory(defaultInstance);
	}

	public FunctionFactory getConditionFactory() {
		return SoffidFunctionFactory.getConditionFactory(defaultInstance);
	}

	public FunctionFactory getGeneralFactory() {
		return SoffidFunctionFactory.getGeneralFactory(defaultInstance);
	}

}
