//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.service;
import com.soffid.iam.service.TenantService;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="XACMLBootService",
	 translatedPackage="com.soffid.iam.addons.xacml.service")
@Depends ({com.soffid.iam.addons.xacml.service.PolicySetService.class,
	TenantService.class})
public abstract class XACMLBootService extends es.caib.seycon.ng.servei.ApplicationBootService {

}
