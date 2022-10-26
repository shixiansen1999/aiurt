package com.aiurt.modules.weaver.service;

import com.aiurt.modules.weaver.service.entity.WeaverSsoRestultDTO;

/**
 * @author fgw
 */
public interface IWeaverSsoService {

    /**
     * 获取token， header
     * @return
     */
    public WeaverSsoRestultDTO getToken();

    /**
     * 获取ssotoken
     * @return
     */
    public String ssoToken();
}
