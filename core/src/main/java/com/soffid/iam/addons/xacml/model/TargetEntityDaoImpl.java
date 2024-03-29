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
			vo.setSubjectMatch(new HashSet<SubjectMatch>());
		}
		if (vo.getResourceMatch() == null)
		{
			vo.setResourceMatch(new HashSet<ResourceMatch>());
		}
		if (vo.getActionMatch() == null)
		{
			vo.setActionMatch(new HashSet<ActionMatch>());
		}
		if (vo.getEnvironmentMatch() == null)
		{
			vo.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
		}
		for (SubjectMatch subject : vo.getSubjectMatch())
		{
			SubjectMatchEntity sm = getSubjectMatchEntityDao().create(subject, te);
			sm.setTarget(te);
			getSubjectMatchEntityDao().update(sm);
			te.getSubjectMatch().add(sm);
		}
		for (ResourceMatch resource : vo.getResourceMatch())
		{
			ResourceMatchEntity rm = getResourceMatchEntityDao().create(resource, te);
			rm.setTarget(te);
			getResourceMatchEntityDao().update(rm);
			te.getResourceMatch().add(rm);
		}
		for (ActionMatch action : vo.getActionMatch())
		{
			ActionMatchEntity am = getActionMatchEntityDao().create(action, te);
			am.setTarget(te);
			getActionMatchEntityDao().update(am);
			te.getActionMatch().add(am);
		}
		for (EnvironmentMatch environment : vo.getEnvironmentMatch())
		{
			EnvironmentMatchEntity em = getEnvironmentMatchEntityDao().create(environment, te);
			em.setTarget(te);
			getEnvironmentMatchEntityDao().update(em);
			te.getEnvironmentMatch().add(em);
		}
		return te;
	}

	@Override
	protected void handleUpdate(Target vo) throws Exception {
		TargetEntity te = targetToEntity(vo);
		super.update (te);
		for (Iterator<SubjectMatchEntity> it = te.getSubjectMatch().iterator(); it.hasNext();)
		{
			SubjectMatchEntity subjectE = it.next();
			boolean found = false;
			for (SubjectMatch subject : vo.getSubjectMatch())
			{
				if (subjectE.getId().equals (subject.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getSubjectMatchEntityDao().remove(subjectE);
				it.remove();
			}
		}
		for (SubjectMatch subject : vo.getSubjectMatch())
		{
			if (subject.getId() == null  || getSubjectMatchEntityDao().load(subject.getId()) == null){
				SubjectMatchEntity sm = getSubjectMatchEntityDao().create(subject, te);
				sm.setTarget(te);
				getSubjectMatchEntityDao().create(sm);
				subject.setId(sm.getId());
				te.getSubjectMatch().add(sm);
			}else
				getSubjectMatchEntityDao().update(subject);
		}
		
		for (Iterator<ResourceMatchEntity> it = te.getResourceMatch().iterator();
				it.hasNext();)
		{
			ResourceMatchEntity resourceE = it.next();
			boolean found = false;
			for (ResourceMatch resource : vo.getResourceMatch())
			{
				if (resourceE.getId().equals (resource.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getResourceMatchEntityDao().remove(resourceE);
				it.remove();
			}
				
		}
		for (ResourceMatch resource : vo.getResourceMatch())
		{
			if (resource.getId() == null  || getResourceMatchEntityDao().load(resource.getId()) == null){
				ResourceMatchEntity rm = getResourceMatchEntityDao().create(resource, te);
				rm.setTarget(te);
				getResourceMatchEntityDao().update(rm);
				resource.setId(rm.getId());
				te.getResourceMatch().add(rm);
			}else
				getResourceMatchEntityDao().update(resource);
		}
		
		for (Iterator<ActionMatchEntity> it = te.getActionMatch().iterator(); it.hasNext();)
		{
			ActionMatchEntity actionE = it.next();
			boolean found = false;
			for (ActionMatch action : vo.getActionMatch())
			{
				if (actionE.getId().equals (action.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getActionMatchEntityDao().remove(actionE);
				it.remove();
			}
		}
		for (ActionMatch action : vo.getActionMatch())
		{
			if (action.getId() == null || getActionMatchEntityDao().load(action.getId()) == null){
				ActionMatchEntity ae = getActionMatchEntityDao().create(action, te);
				ae.setTarget(te);
				getActionMatchEntityDao().update(ae);
				action.setId(ae.getId());
				te.getActionMatch().add(ae);
			}else
				getActionMatchEntityDao().update(action);
		}
		
		for (Iterator<EnvironmentMatchEntity> it = te.getEnvironmentMatch().iterator();
				it.hasNext();)
		{
			EnvironmentMatchEntity environmentE = it.next();
			boolean found = false;
			for (EnvironmentMatch environment : vo.getEnvironmentMatch())
			{
				if (environmentE.getId().equals (environment.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getEnvironmentMatchEntityDao().remove(environmentE);
				it.remove();
			}
		}
		for (EnvironmentMatch environment : vo.getEnvironmentMatch())
		{
			if (environment.getId() == null || getEnvironmentMatchEntityDao().load(environment.getId()) == null){
				EnvironmentMatchEntity ee = getEnvironmentMatchEntityDao().create(environment, te);
				ee.setTarget(te);
				getEnvironmentMatchEntityDao().update(ee);
				environment.setId(ee.getId());
				te.getEnvironmentMatch().add(ee);
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