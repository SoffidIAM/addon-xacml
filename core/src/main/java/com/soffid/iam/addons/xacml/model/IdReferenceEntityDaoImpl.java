//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;


import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;

/**
 * DAO IdReferenceEntity implementation
 */
public class IdReferenceEntityDaoImpl extends IdReferenceEntityDaoBase
{

	
	@Override
	public void policySetIdReferenceToEntity (com.soffid.iam.addons.xacml.common.PolicySetIdReference source, com.soffid.iam.addons.xacml.model.PolicySetIdReferenceEntity target, boolean copyIfNull) {
		// Missing attribute idReferenceTypeValue on entity
		if(copyIfNull || source.getIdReferenceTypeValue() != null)
			target.setIdReferenceTypeValue(source.getIdReferenceTypeValue());
		// Missing attribute version on entity
		if(copyIfNull || source.getVersion() != null)
				target.setVersion(source.getVersion());
		// Missing attribute earliestVersion on entity
		if(copyIfNull || source.getEarliestVersion() != null)
			target.setEarliestVersion(source.getEarliestVersion());
		// Missing attribute latestVersion on entity
		if(copyIfNull || source.getLatestVersion() != null)
			target.setLatestVersion(source.getLatestVersion());
		if (copyIfNull || source.getOrder() != null)
		{
			target.setOrder(source.getOrder());
		}
	}
	
	@Override
	public void policyIdReferenceToEntity (com.soffid.iam.addons.xacml.common.PolicyIdReference source, com.soffid.iam.addons.xacml.model.PolicyIdReferenceEntity target, boolean copyIfNull) {
		// Missing attribute idReferenceTypeValue on entity
		if(copyIfNull || source.getIdReferenceTypeValue() != null)
			target.setIdReferenceTypeValue(source.getIdReferenceTypeValue());
		// Missing attribute version on entity
		if(copyIfNull || source.getVersion() != null)
				target.setVersion(source.getVersion());
		// Missing attribute earliestVersion on entity
		if(copyIfNull || source.getEarliestVersion() != null)
			target.setEarliestVersion(source.getEarliestVersion());
		// Missing attribute latestVersion on entity
		if(copyIfNull || source.getLatestVersion() != null)
			target.setLatestVersion(source.getLatestVersion());
		if (copyIfNull || source.getOrder() != null)
		{
			target.setOrder(source.getOrder());
		}
	}

	public PolicySetIdReferenceEntity policySetIdReferenceToEntity(
			PolicySetIdReference instance) {
		PolicySetIdReferenceEntity entity;
		if (instance.getId() == null) 
			entity = newPolicySetIdReferenceEntity();
		else
			entity = (PolicySetIdReferenceEntity) load (instance.getId());
		policySetIdReferenceToEntity(instance, entity, true);
		return entity;
	}

	public PolicyIdReferenceEntity policyIdReferenceToEntity(
			PolicyIdReference instance) {
		PolicyIdReferenceEntity entity;
		if (instance.getId() == null) 
			entity = newPolicyIdReferenceEntity();
		else
			entity = (PolicyIdReferenceEntity) load (instance.getId());
		policyIdReferenceToEntity(instance, entity, true);
		return entity;
	}
	
}
