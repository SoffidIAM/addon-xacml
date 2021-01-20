//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.soffid.iam.addons.xacml.common.ActionMatch;
import com.soffid.iam.addons.xacml.common.EnvironmentMatch;
import com.soffid.iam.addons.xacml.common.Obligation;
import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.ResourceMatch;
import com.soffid.iam.addons.xacml.common.SubjectMatch;
import com.soffid.iam.addons.xacml.common.Target;

import es.caib.seycon.ng.exception.InternalErrorException;

/**
 * DAO PolicySetEntity implementation
 */
public class PolicySetEntityDaoImpl extends PolicySetEntityDaoBase
{
	@Override
	public void toPolicySet(PolicySetEntity source, PolicySet target){
		super.toPolicySet(source, target);
		// Incompatible types source.target and target.target
		Target t;
		target.setTarget(new LinkedList<Target>());
		if (source.getTarget() != null && source.getTarget().size() >= 1)
		{
			t = getTargetEntityDao().toTarget(source.getTarget().iterator().next());
			target.getTarget().add(t);
		}

		// Incompatible types source.policySetIdReference and target.policySetIdReference
		target.setPolicySetIdReference(getPolicySetIdReferenceEntityDao().toPolicySetIdReferenceList(source.getPolicySetIdReference()));
		// Incompatible types source.policyIdReference and target.policyIdReference
		target.setPolicyIdReference(getPolicyIdReferenceEntityDao().toPolicyIdReferenceList(source.getPolicyIdReference()));
		// Incompatible types source.parentPolicySet and target.parentPolicySet
		if(source.getParentPolicySet() != null){
			target.setParentPolicySet(source.getParentPolicySet().getId());
		}else{
			target.setParentPolicySet(null);
		}
		
		target.setObligation(new LinkedList<Obligation>());
		for (ObligationEntity oe: source.getObligation()) {
			target.getObligation().add(getObligationEntityDao().toObligation(oe));
		}
	}
	
	@Override 
	public void policySetToEntity(PolicySet source, PolicySetEntity target, boolean copyIfNull){
		super.policySetToEntity(source, target, copyIfNull);
		// Missing attribute childrenPolicySet on entity
		// Missing attribute obligations on entity
		// Missing attribute policy on entity
		// Missing attribute policySetDefaults on entity
		// Missing attribute combinerParameters on entity
		// Missing attribute policyCombinerParameters on entity
		// Missing attribute policySetCombinerParameters on entity
		
		// Incompatible types source.parentPolicySet and target.parentPolicySet
		if (copyIfNull || source.getParentPolicySet() != null){
			if(source.getParentPolicySet() != null){
				PolicySetEntityDao dao = getPolicySetEntityDao();
				PolicySetEntity parentPolicy = dao.findByPolicySetId(source.getParentPolicySet());
				if(parentPolicy != null){
					target.setParentPolicySet(parentPolicy);
				}else{
					throw new IllegalArgumentException("Parent not found for Policy Set");
				}
			} else {
				target.setParentPolicySet(null);
			}
		}
		
		// Incompatible types source.policySetIdReference and target.policySetIdReference
		if (copyIfNull || source.getPolicySetIdReference() != null)
		{
			Collection<PolicySetIdReference> originalPolicySetIdReference;
			if (source.getPolicySetIdReference() != null)
				originalPolicySetIdReference = new HashSet<PolicySetIdReference>(source.getPolicySetIdReference());
			else
				originalPolicySetIdReference = Collections.EMPTY_SET;
			for (Iterator<PolicySetIdReferenceEntity> it1 = target.getPolicySetIdReference().iterator(); it1.hasNext();)
			{
				PolicySetIdReferenceEntity policySetIdReferenceEntity = it1.next();
				boolean found = false;
				for (Iterator<PolicySetIdReference> it2 = originalPolicySetIdReference.iterator(); it2.hasNext();)
				{
					PolicySetIdReference policySetIdReference = it2.next();
					if (policySetIdReference.getId().equals(policySetIdReferenceEntity.getId()))
					{
						getPolicySetIdReferenceEntityDao().policySetIdReferenceToEntity(policySetIdReference, policySetIdReferenceEntity, true);
						getPolicySetIdReferenceEntityDao().update(policySetIdReferenceEntity);
						found = true;
						it2.remove();
						break;
					}
				}
				if (!found)
				{
					getPolicySetIdReferenceEntityDao().remove(policySetIdReferenceEntity);
					it1.remove();
				}
			}
			
			Collection<PolicySetIdReference> finalPolicySetIdReference = new HashSet<PolicySetIdReference>();//
			for (PolicySetIdReference policySetIdReference: originalPolicySetIdReference)
			{
				PolicySetIdReferenceEntity policySetIdReferenceEntity = getPolicySetIdReferenceEntityDao().policySetIdReferenceToEntity(policySetIdReference);
				policySetIdReferenceEntity.setPolicySet(target);
				getPolicySetIdReferenceEntityDao().create(policySetIdReferenceEntity);
				target.getPolicySetIdReference().add(policySetIdReferenceEntity);
				policySetIdReference.setId(policySetIdReferenceEntity.getId());//Add to permit more than one create without refresh
				finalPolicySetIdReference.add(policySetIdReference);//
			}
			source.setPolicySetIdReference(finalPolicySetIdReference);//
		}
		
		// Incompatible types source.policyIdReference and target.policyIdReference
		if (copyIfNull || source.getPolicyIdReference() != null)
		{
			Collection<PolicyIdReference> originalPolicyIdReference;
			if (source.getPolicyIdReference() != null)
				originalPolicyIdReference = new HashSet<PolicyIdReference>(source.getPolicyIdReference());
			else
				originalPolicyIdReference = Collections.EMPTY_SET;
			for (Iterator<PolicyIdReferenceEntity> it1 = target.getPolicyIdReference().iterator(); it1.hasNext();)
			{
				PolicyIdReferenceEntity policyIdReferenceEntity = it1.next();
				boolean found = false;
				for (Iterator<PolicyIdReference> it2 = originalPolicyIdReference.iterator(); it2.hasNext();)
				{
					PolicyIdReference policyIdReference = it2.next();
					if (policyIdReferenceEntity.getId().equals(policyIdReference.getId()))
					{
						getPolicyIdReferenceEntityDao().idReferenceToEntity(policyIdReference, policyIdReferenceEntity, true);
						getPolicyIdReferenceEntityDao().update(policyIdReferenceEntity);
						found = true;
						it2.remove();
						break;
					}
				}
				if (!found)
				{
					getPolicyIdReferenceEntityDao().remove(policyIdReferenceEntity);
					it1.remove();
				}
			}
			Collection<PolicyIdReference> finalPolicyIdReference = new HashSet<PolicyIdReference>();//
			for (PolicyIdReference policyIdReference: originalPolicyIdReference)
			{
				PolicyIdReferenceEntity policyIdReferenceEntity = getPolicyIdReferenceEntityDao().policyIdReferenceToEntity(policyIdReference);
				policyIdReferenceEntity.setPolicy(target);
				getPolicyIdReferenceEntityDao().create(policyIdReferenceEntity);
				target.getPolicyIdReference().add(policyIdReferenceEntity);
				policyIdReference.setId(policyIdReferenceEntity.getId());//Add to permit more than one create without refresh
				finalPolicyIdReference.add(policyIdReference);//
			}
			source.setPolicyIdReference(finalPolicyIdReference);//
		}
	}

	@Override
	public void remove(PolicySetEntity entity) {
		for (TargetEntity target: entity.getTarget())
		{
			getTargetEntityDao().remove(target);
		}
		super.remove(entity);
	}


	@Override
	protected PolicySetEntity handleCreate(PolicySet vo) throws Exception {
		vo.setId(null);
		String policySetId = vo.getPolicySetId().replace(" ", "");
		vo.setPolicySetId(policySetId);
		PolicySetEntity pse = policySetToEntity(vo);
		super.create (pse);
		if (vo.getTarget() == null)
		{
			Target t;
			t = new Target();
			t.setActionMatch(new HashSet<ActionMatch>());
			t.setEnvironmentMatch(new HashSet<EnvironmentMatch>());
			t.setResourceMatch(new HashSet<ResourceMatch>());
			t.setSubjectMatch(new HashSet<SubjectMatch>());
			vo.setTarget(new HashSet<Target>());
			vo.getTarget().add(t);
		}
		
		for (Target target : vo.getTarget())
		{
			TargetEntity te = getTargetEntityDao().create(target);
			te.setPolicySet(pse);
			getTargetEntityDao().update(te);
			pse.getTarget().add(te);
		}

		if (vo.getObligation() != null)
		{
			for (Obligation o : vo.getObligation())
			{
				ObligationEntity oe = getObligationEntityDao().create(o);
				oe.setPolicySet(pse);
				getObligationEntityDao().update(oe);
				pse.getObligation().add(oe);
			}
		}
		return pse;
	}

	@Override
	protected void handleUpdate(PolicySet vo) throws Exception {
		PolicySetEntity pse = policySetToEntity(vo);
		super.update (pse);
		for (TargetEntity targetE: pse.getTarget())
		{
			boolean found = false;
			for (Target target : vo.getTarget())
			{
				if (targetE.getId().equals (target.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found)
				getTargetEntityDao().remove(targetE);
		}
		for (Target target : vo.getTarget())
		{
			if (target.getId() == null || getTargetEntityDao().load(target.getId()) == null){
				TargetEntity te = getTargetEntityDao().create(target);
				te.setPolicySet(pse);
				getTargetEntityDao().update(te);
			}else
				getTargetEntityDao().update(target);
		}
		
		for (ObligationEntity oe: new LinkedList<ObligationEntity>( pse.getObligation()))
		{
			boolean found = false;
			for (Obligation o : vo.getObligation())
			{
				if (oe.getId().equals (o.getId()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				getObligationEntityDao().remove(oe);
				pse.getObligation().remove(oe);
			}
		}
		for (Obligation o : vo.getObligation())
		{
			if (o.getId() == null || getObligationEntityDao().load(o.getId()) == null){
				ObligationEntity oe = getObligationEntityDao().create(o);
				oe.setPolicySet(pse);
				getObligationEntityDao().update(oe);
			}else {
				getObligationEntityDao().update(o);
			}
		}
	}

	@Override
	protected void handleRemove(PolicySet vo) throws Exception {
		PolicySetEntity pse = policySetToEntity(vo);
		for (TargetEntity targetE: pse.getTarget())
		{
			getTargetEntityDao().remove(targetE);
		}
		for (ObligationEntity oe: pse.getObligation())
			getObligationEntityDao().remove(oe);
		super.remove (pse);
		
	}
	
}
