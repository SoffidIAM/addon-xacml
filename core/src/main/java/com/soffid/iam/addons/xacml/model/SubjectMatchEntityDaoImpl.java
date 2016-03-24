//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.SubjectMatch;

/**
 * DAO SubjectMatchEntity implementation
 */
public class SubjectMatchEntityDaoImpl extends SubjectMatchEntityDaoBase
{

	@Override
	protected SubjectMatchEntity handleCreate(SubjectMatch vo, TargetEntity tar) throws Exception {
		SubjectMatchEntity se = subjectMatchToEntity(vo);
		se.setTarget(tar);
		super.create (se);
		return se;
	}

	@Override
	protected void handleUpdate(SubjectMatch vo) throws Exception {
		SubjectMatchEntity se = subjectMatchToEntity(vo);
		super.update (se);
	}
	

	@Override
	protected void handleRemove(SubjectMatch subjectMatch) throws Exception {
		SubjectMatchEntity ame = subjectMatchToEntity(subjectMatch);
		super.remove (ame);
	}
}
