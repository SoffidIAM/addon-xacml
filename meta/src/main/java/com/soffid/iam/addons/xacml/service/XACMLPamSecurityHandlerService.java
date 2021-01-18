package com.soffid.iam.addons.xacml.service;

import com.soffid.iam.service.PamSecurityHandlerService;
import com.soffid.iam.service.VaultService;
import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Service;

import es.caib.seycon.ng.model.AccountEntity;

@Service(internal=true)
@Depends({PolicySetService.class, AccountEntity.class, VaultService.class})
public class XACMLPamSecurityHandlerService extends PamSecurityHandlerService {

}
