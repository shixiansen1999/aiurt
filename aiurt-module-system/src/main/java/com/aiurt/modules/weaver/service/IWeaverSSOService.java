package com.aiurt.modules.weaver.service;

import com.aiurt.modules.weaver.service.entity.WeaverSsoRestultDTO;

/**
 * @author fgw
 */
public interface IWeaverSSOService {
    public WeaverSsoRestultDTO getToken();

    public String ssoToken();
}
