package com.aiurt.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fgw
 */
public interface ILoginService {

    /**
     * 企业微信网页授权登录
     * @param code
     * @return
     */
    Result<JSONObject> webAuthorizationLogin(HttpServletRequest req, String code);


    /**
     * 生成签名
     * @param url
     * @return
     */
    Result<JSONObject> autograph(String url);
}
