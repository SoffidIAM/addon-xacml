package com.soffid.iam.addons.xacml.service.pool;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.Indenter;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BooleanAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateAttribute;
import org.jboss.security.xacml.sunxacml.attr.DateTimeAttribute;
import org.jboss.security.xacml.sunxacml.attr.IntegerAttribute;
import org.jboss.security.xacml.sunxacml.cond.Evaluatable;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.sunxacml.cond.Function;
import org.jboss.security.xacml.sunxacml.cond.FunctionBase;
import org.jboss.security.xacml.sunxacml.ctx.Status;

public class DateToIntegerFunction implements Function {

	@Override
	public URI getIdentifier() {
		return URI.create("urn:com.soffid:xacml:2.0:function:day-to-integer");
	}

	@Override
	public URI getType() {
		return URI.create(IntegerAttribute.identifier);
	}

	@Override
	public EvaluationResult evaluate(List inputs, EvaluationCtx context) {
        Iterator it = inputs.iterator();
        while (it.hasNext()) {
            Evaluatable eval = (Evaluatable)(it.next());

            // Evaluate the argument
            EvaluationResult result = eval.evaluate(context);
            if (result.indeterminate())
                return result;

            AttributeValue value = result.getAttributeValue();
            if (value != null) {
            	if (value instanceof DateAttribute || 
            		value instanceof DateTimeAttribute) {
            		Date d = ((Date) value.getValue());
            		return new EvaluationResult(new IntegerAttribute(d.getTime()));
            	}
            }
        }
        return new EvaluationResult( new Status( Arrays.asList( Status.STATUS_MISSING_ATTRIBUTE) ));
	}

	@Override
	public URI getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean returnsBag() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkInputs(List inputs) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkInputsNoBag(List inputs) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode(OutputStream output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode(OutputStream output, Indenter indenter) {
		// TODO Auto-generated method stub
		
	}

}
