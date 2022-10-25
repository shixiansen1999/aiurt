package com.aiurt.modules.weaver.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.aiurt.modules.weaver.service.IWeaverSSOService;
import com.aiurt.modules.weaver.service.entity.WeaverSsoRestultDTO;
import com.alibaba.fastjson.JSONObject;
import liquibase.pro.packaged.J;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fgw
 * @date 2022-09-26
 */
@Slf4j
@Service
public class WeaverSSOServiceImpl implements IWeaverSSOService {

    private static final String REGIST_URL = "/api/ec/dev/auth/regist";
    private static final String APPLY_TOKEN_URL = "/api/ec/dev/auth/applytoken";

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private String appId = "aiurt";

    @Value("${weaver.ip:192.168.1.31:8888}")
    private String weaverIp;

    @Autowired
    private RestTemplate restTemplate;



    /**
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    private void regist() {
        //获取当前系统RSA加密的公钥
        RSA rsa = new RSA();
        String publicKey = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();

        // 客户端RSA私钥
        redisTemplate.opsForValue().set("WEAVER:LOCAL_PRIVATE_KEY", privateKey);
        // 客户端RSA公钥
        redisTemplate.opsForValue().set("WEAVER:LOCAL_PUBLIC_KEY", publicKey);

        String url = String.format("http://%s%s", weaverIp, REGIST_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.add("appid", appId);
        headers.add("cpk", publicKey);
        HttpEntity httpEntity = new HttpEntity(headers);
        String s = restTemplate.postForObject(url, httpEntity, String.class);
        log.info("请求密钥结果:{}",s);
        JSONObject result = JSONObject.parseObject(s);
        int code = result.getIntValue("code");
        if (code != 0) {
            log.info("请求失败:{}",s);
            return;
        }
        //ECOLOGY返回的系统公钥
        String spk = result.getString("spk");
        //ECOLOGY返回的系统密钥
        String secrit = result.getString("secrit");
        // 缓存
        redisTemplate.opsForValue().set("WEAVER:SERVER_PUBLIC_KEY", spk);
        redisTemplate.opsForValue().set("WEAVER:SERVER_SECRET", secrit);
    }

    @Override
    public WeaverSsoRestultDTO getToken() {
        String tokenResult = redisTemplate.opsForValue().get("weaver：token");
        if (StrUtil.isNotBlank(tokenResult)) {
            return JSONObject.parseObject(tokenResult, WeaverSsoRestultDTO.class);
        }
        // 从系统缓存或者数据库中获取ECOLOGY系统公钥和Secret信息
        String secret = redisTemplate.opsForValue().get("WEAVER:SERVER_SECRET");
        String spk = redisTemplate.opsForValue().get("WEAVER:SERVER_PUBLIC_KEY");

        // 如果为空,说明还未进行注册,调用注册接口进行注册认证与数据更新
        if (StrUtil.isBlank(secret) || StrUtil.isBlank(spk)) {
            regist();
            // 重新获取最新ECOLOGY系统公钥和Secret信息
            secret = redisTemplate.opsForValue().get("WEAVER:SERVER_SECRET");
            spk = redisTemplate.opsForValue().get("WEAVER:SERVER_PUBLIC_KEY");
        }

        // 公钥加密,所以RSA对象私钥为null
        RSA rsa = new RSA(null,spk);
        //对秘钥进行加密传输，防止篡改数据
        String encryptSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);

        String url = String.format("http://%s%s", weaverIp, APPLY_TOKEN_URL);
        HttpHeaders headers = new HttpHeaders();
        headers.add("appid", appId);
        headers.add("secret", encryptSecret);
        long time = 8*24*60*60L;
        headers.add("time", String.valueOf(time));
        HttpEntity httpEntity = new HttpEntity(headers);
        String s = restTemplate.postForObject(url, httpEntity, String.class);
        log.info("请求token结果: {}", s);

        JSONObject result = JSONObject.parseObject(s);

        String token = result.getString("token");

        redisTemplate.opsForValue().set("SERVER_TOKEN", token, time, TimeUnit.SECONDS);
        //ECOLOGY返回的token

        WeaverSsoRestultDTO weaverSsoRestultDTO = new WeaverSsoRestultDTO();
        weaverSsoRestultDTO.setToken(token);
        String encryptUserid = rsa.encryptBase64("1", CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        weaverSsoRestultDTO.setUserid(encryptUserid);
        weaverSsoRestultDTO.setAppid(appId);

        redisTemplate.opsForValue().set("weaver：token", JSONObject.toJSONString(weaverSsoRestultDTO), time, TimeUnit.SECONDS);

        return weaverSsoRestultDTO;

    }

    @Override
    public String ssoToken() {
        String url = "/ssologin/getToken";
        url = String.format("http://%s%s", weaverIp, url);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        JSONObject req = new JSONObject();
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap();
        paramMap.add("appid", "aiurtplatform");
        paramMap.add("loginid", "sysadmin");
        HttpEntity httpEntity = new HttpEntity(paramMap, headers);
        String result = restTemplate.postForObject(url,httpEntity, String.class);
        log.info("请求的token:{}", result);
        return result;
    }
}
