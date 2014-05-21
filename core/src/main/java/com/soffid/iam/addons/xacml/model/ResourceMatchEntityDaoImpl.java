//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.ResourceMatch;

/**
 * DAO ResourceMatchEntity implementation
 */
public class ResourceMatchEntityDaoImpl extends ResourceMatchEntityDaoBase
{

	@Override
	protected ResourceMatchEntity handleCreate(ResourceMatch vo, TargetEntity tar) throws Exception {
		ResourceMatchEntity re = resourceMatchToEntity(vo);
		re.setTarget(tar);
		super.create (re);
		return re;
	}

	@Override
	protected void handleUpdate(ResourceMatch vo) throws Exception {
		ResourceMatchEntity re = resourceMatchToEntity(vo);
		super.update (re);
	}

	@Override
	protected void handleRemove(ResourceMatch resourceMatch) throws Exception {
		ResourceMatchEntity rme = resourceMatchToEntity(resourceMatch);
		super.remove (rme);
	}
	
}
