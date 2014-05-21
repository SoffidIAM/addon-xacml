//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.soffid.iam.addons.xacml.common.Rule;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.Target;

import es.caib.seycon.ng.exception.InternalErrorException;


/**
 * DAO TargetEntity implementation
 */
public class TargetEntityDaoImpl extends TargetEntityDaoBase
{
	@Override
	public void toTarget(TargetEntity source, Target target){
		super.toTarget(source, target);
		// Missing attribute subjectMatch on entity
		// Missing attribute actionMatch on entity
		// Missing attribute resourceMatch on entity
		// Missing attribute environmentMatch on entity
		
		if(source.getSubjectMatch() != null)
			target.setSubjectMatch(getSubjectMatchEntityDao().toSubjectMatchList(source.getSubjectMatch()));
		
		if(source.getResourceMatch() != null)
			target.setResourceMatch(getResourceMatchEntityDao().toResourceMatchList(source.getResourceMatch()));
		
		if(source.getActionMatch() != null)
			target.setActionMatch(getActionMatchEntityDao().toActionMatchList(source.getActionMatch()));
		
		if(source.getEnvironmentMatch() != null)
			target.setEnvironmentMatch(getEnvironmentMatchEntityDao().toEnvironmentMatchList(source.getEnvironmentMatch()));
		
	}
	
	@Override
	public void targetToEntity(Target source, TargetEntity target, boolean copyIfNull){
		super.targetToEntity(source, target, copyIfNull);
		// Missing attribute policy on entity
		// Missing attribute rule on entity
		// Missing attribute policySet on entity
	}

	@Override
	public void remove(TargetEntity entity) {
		for(SubjectMatchEntity subjects: entity.getSubjectMatch())
			getSubjectMatchEntityDao().remove(subjects);
		for(ResourceMatchEntity resources: entity.getResourceMatch())
			getResourceMatchEntityDao().remove(resources);
		for(ActionMatchEntity actions: entity.getActionMatch())
			getActionMatchEntityDao().remove(actions);
		for(EnvironmentMatchEntity environemnts: entity.getEnvironmentMatch())
			getEnvironmentMatchEntityDao().remove(environemnts);
		super.remove(entity);
	}
	

	@Override
	protected TargetEntity handleCreate(Target vo) throws Exception {
		TargetEntity te = targetToEntity(vo);
		super.create (te);
		if (vo.getSubjectMatch() == null)
		{
			SubjectMatch sm;
			sm = new SubjectMatch();
			vo.setSubjectMatch(new HashSet<SubjectMatch>());
			vo.getSubjectMatch().add(sm);
		}
		if (vo.getResourceMatch() == null)
		{
			ResourceMatch rm;
			rm = new ResourceMatch();
			vo.setResourceMatch(new HashSet<ResourceMatch>());
			vo.getResourceMatch().add(rm);
		}
		if (vo.getActionMatch() == null)
		{
			ActionMatch am;
			am = new ActionMatch();
			vo.setActionMatch(new HashSet<ActionMatch>());
			vo.getActionMatch().add(am);
		}
		if (vo.getEnvironmentMatch() == null)
		{
			EnvironmentMatch em;
			em = new EnvironmentMatch();
			vo.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
			vo.getEnvironmentMatch().add(em);
		}
		for (SubjectMatch subject : vo.getSubjectMatch())
		{
			SubjectMatchEntity sm = getSubjectMatchEntityDao().create(subject, te);
			sm.setTarget(te);
			getSubjectMatchEntityDao().update(sm);
		}
		for (ResourceMatch resource : vo.getResourceMatch())
		{
			ResourceMatchEntity rm = getResourceMatchEntityDao().create(resource, te);
			rm.setTarget(te);
			getResourceMatchEntityDao().update(rm);
		}
		for (ActionMatch action : vo.getActionMatch())
		{
			ActionMatchEntity am = getActionMatchEntityDao().create(action, te);
			am.setTarget(te);
			getActionMatchEntityDao().update(am);
		}
		for (EnvironmentMatch environment : vo.getEnvironmentMatch())
		{
			EnvironmentMatchEntity em = getEnvironmentMatchEntityDao().create(environment, te);
			em.setTarget(te);
			getEnvironmentMatchEntityDao().update(em);
		}
		return te;
	}

	@Override
	protected void handleUpdate(Target vo) throws Exception {
		TargetEntity te = targetToEntity(vo);
		super.update (te);
		for (SubjectMatchEntity subjectE: te.getSubjectMatch())
		{
			boolean found = false;
			for (SubjectMatch subject : vo.getSubjectMatch())
			{
				if (subjectE.getId().equals (subject.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getSubjectMatchEntityDao().remove(subjectE);
		}
		for (SubjectMatch subject : vo.getSubjectMatch())
		{
			if (subject.getId() == null){
				SubjectMatchEntity sm = getSubjectMatchEntityDao().create(subject, te);
				sm.setTarget(te);
				getSubjectMatchEntityDao().update(sm);
			}else
				getSubjectMatchEntityDao().update(subject);
		}
		
		for (ResourceMatchEntity resourceE: te.getResourceMatch())
		{
			boolean found = false;
			for (ResourceMatch resource : vo.getResourceMatch())
			{
				if (resourceE.getId().equals (resource.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getResourceMatchEntityDao().remove(resourceE);
		}
		for (ResourceMatch resource : vo.getResourceMatch())
		{
			if (resource.getId() == null){
				ResourceMatchEntity rm = getResourceMatchEntityDao().create(resource, te);
				rm.setTarget(te);
				getResourceMatchEntityDao().update(rm);
			}else
				getResourceMatchEntityDao().update(resource);
		}
		
		for (ActionMatchEntity actionE: te.getActionMatch())
		{
			boolean found = false;
			for (ActionMatch action : vo.getActionMatch())
			{
				if (actionE.getId().equals (action.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getActionMatchEntityDao().remove(actionE);
		}
		for (ActionMatch action : vo.getActionMatch())
		{
			if (action.getId() == null){
				ActionMatchEntity ae = getActionMatchEntityDao().create(action, te);
				ae.setTarget(te);
				getActionMatchEntityDao().update(ae);
			}else
				getActionMatchEntityDao().update(action);
		}
		
		for (EnvironmentMatchEntity environmentE: te.getEnvironmentMatch())
		{
			boolean found = false;
			for (EnvironmentMatch environment : vo.getEnvironmentMatch())
			{
				if (environmentE.getId().equals (environment.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getEnvironmentMatchEntityDao().remove(environmentE);
		}
		for (EnvironmentMatch environment : vo.getEnvironmentMatch())
		{
			if (environment.getId() == null){
				EnvironmentMatchEntity ee = getEnvironmentMatchEntityDao().create(environment, te);
				ee.setTarget(te);
				getEnvironmentMatchEntityDao().update(ee);
			}else
				getEnvironmentMatchEntityDao().update(environment);
		}
		
		
	}

	@Override
	protected void handleRemove(Target vo) throws Exception {
		TargetEntity te = targetToEntity(vo);
		for (SubjectMatchEntity subjectE: te.getSubjectMatch())
		{
			getSubjectMatchEntityDao().remove(subjectE);
		}
		for (ResourceMatchEntity resourceE: te.getResourceMatch())
		{
			getResourceMatchEntityDao().remove(resourceE);
		}
		for (ActionMatchEntity actionE: te.getActionMatch())
		{
			getActionMatchEntityDao().remove(actionE);
		}
		for (EnvironmentMatchEntity environmentE: te.getEnvironmentMatch())
		{
			getEnvironmentMatchEntityDao().remove(environmentE);
		}
		super.remove (te);
	}

}