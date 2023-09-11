package com.soffid.iam.addons.xacml.service.pool;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.security.xacml.sunxacml.ParsingException;
import org.jboss.security.xacml.sunxacml.UnknownIdentifierException;
import org.jboss.security.xacml.sunxacml.cond.BaseFunctionFactory;
import org.jboss.security.xacml.sunxacml.cond.Function;
import org.jboss.security.xacml.sunxacml.cond.FunctionFactory;
import org.jboss.security.xacml.sunxacml.cond.FunctionFactoryProxy;
import org.jboss.security.xacml.sunxacml.cond.FunctionProxy;
import org.jboss.security.xacml.sunxacml.cond.FunctionTypeException;

public class SoffidFunctionFactory extends BaseFunctionFactory {
	private FunctionFactory parent;
	private static Set<Function> supported = Set.of(
			new IntegerDayOfWeekFunction(),
			new IntegerDayOfMonthFunction(),
			new IntegerMonthFunction(),
			new IntegerYearFunction(),
			new DateToIntegerFunction() // urn:com.soffid:xacml:2.0:function:date-to-integer"
			);
	private static Map abstractFunctions = new HashMap<>();

	public SoffidFunctionFactory(FunctionFactory targetFactory) {
		super(targetFactory, supported, abstractFunctions);
		parent = targetFactory;
	}

	public static FunctionFactory getTargetFactory(FunctionFactoryProxy defaultInstance) {
		return new SoffidFunctionFactory(defaultInstance.getTargetFactory());
	}

	public static FunctionFactory getConditionFactory(FunctionFactoryProxy defaultInstance) {
		return new SoffidFunctionFactory(defaultInstance.getConditionFactory());
	}

	public static FunctionFactory getGeneralFactory(FunctionFactoryProxy defaultInstance) {
		return new SoffidFunctionFactory(defaultInstance.getGeneralFactory());
	}

    public Function createFunction(String identity)
            throws UnknownIdentifierException, FunctionTypeException
    {
    	try {
    		return super.createFunction(identity);
    	} catch (UnknownIdentifierException e) {
    		return parent.createFunction(identity);
    	}
    }
}
