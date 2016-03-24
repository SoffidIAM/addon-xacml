//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.ActionMatch;

/**
 * DAO ActionMatchEntity implementation
 */
public class ActionMatchEntityDaoImpl extends ActionMatchEntityDaoBase
{

	@Override
	protected ActionMatchEntity handleCreate(ActionMatch vo, TargetEntity tar) throws Exception {
		ActionMatchEntity ae = actionMatchToEntity(vo);
		ae.setTarget(tar);
		super.create (ae);
		return ae;
	}

	@Override
	protected void handleUpdate(ActionMatch vo) throws Exception {
		ActionMatchEntity ame = actionMatchToEntity(vo);
		super.update (ame);
	}
	

	@Override
	protected void handleRemove(ActionMatch actionMatch) throws Exception {
		ActionMatchEntity ame = actionMatchToEntity(actionMatch);
		super.remove (ame);
	}
	
}
