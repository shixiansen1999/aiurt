package com.aiurt.modules.weaver.service;

import com.aiurt.modules.weaver.service.entity.WeaverSsoRestultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

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

    /**
     * 获取ssotoken
     * @return
     */
    public String ssoTokenByIdentifier(String identifier) throws JsonProcessingException;
}
