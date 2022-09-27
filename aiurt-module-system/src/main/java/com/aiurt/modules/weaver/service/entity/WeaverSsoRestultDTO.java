package com.aiurt.modules.weaver.service.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 * @date 2022-09-26
 */
@Data
public class WeaverSsoRestultDTO implements Serializable {

    private String token;

    private String appid;

    private String userid;
}
