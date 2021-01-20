//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.xacml.model;

import com.soffid.iam.addons.xacml.common.Obligation;

/**
 * DAO ObligationEntity implementation
 */
public class ObligationEntityDaoImpl extends ObligationEntityDaoBase
{

	@Override
	protected ObligationEntity handleCreate(Obligation obligation) throws Exception {
		ObligationEntity oe = obligationToEntity(obligation);
		create(oe);
		return oe;
	}

	@Override
	protected void handleRemove(Obligation obligation) throws Exception {
		remove(obligation.getId());
	}

	@Override
	protected void handleUpdate(Obligation obligation) throws Exception {
		ObligationEntity oe = obligationToEntity(obligation);
		update(oe);
	}

	@Override
	public void toObligation(ObligationEntity source, Obligation target) {
		super.toObligation(source, target);
	}

	@Override
	public void obligationToEntity(Obligation source, ObligationEntity target, boolean copyIfNull) {
		super.obligationToEntity(source, target, copyIfNull);
	}
}
