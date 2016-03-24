//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.EnvironmentMatch;


/**
 * DAO EnvironmentMatchEntity implementation
 */
public class EnvironmentMatchEntityDaoImpl extends EnvironmentMatchEntityDaoBase
{

	@Override
	protected EnvironmentMatchEntity handleCreate(EnvironmentMatch vo, TargetEntity tar) throws Exception {
		EnvironmentMatchEntity ee = environmentMatchToEntity(vo);
		ee.setTarget(tar);
		super.create (ee);
		return ee;
	}

	@Override
	protected void handleUpdate(EnvironmentMatch vo) throws Exception {
		EnvironmentMatchEntity ee = environmentMatchToEntity(vo);
		super.update (ee);
	}

	@Override
	protected void handleRemove(EnvironmentMatch environmentMatch)
			throws Exception {
		EnvironmentMatchEntity eme = environmentMatchToEntity(environmentMatch);
		super.remove (eme);
	}
}
