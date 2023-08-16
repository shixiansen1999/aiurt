package com.aiurt.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.common.util.RestUtil;
import com.aiurt.config.thirdapp.ThirdAppConfig;
import com.aiurt.modules.system.entity.SysRole;
import com.aiurt.modules.system.entity.SysThirdAccount;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.entity.SysUserRole;
import com.aiurt.modules.system.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static cn.hutool.crypto.SecureUtil.sha1;

/**
 * @author fgw
 */
@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    public static final String WX_HOST = "qyapi.weixin.qq.com";

    public static final String WX_HOST_CONFIG_VALUE = "wx_host";

    public static final String WX_PRIVATE_CLOUD = "wx_private_cloud";

    /***
     * 非私有
     */
    public static final String WX_PRIVATE_CLOUD_VALUE = "0";

    @Autowired
    private ISysParamAPI sysParamApi;

    @Autowired
    ThirdAppConfig thirdAppConfig;

    /**
     * 企业微信网页授权登录
     *
     * @param code
     * @return
     */
    @Override
    public Result<JSONObject> webAuthorizationLogin(HttpServletRequest req, String code) {
        Result<com.alibaba.fastjson.JSONObject> result = new Result<com.alibaba.fastjson.JSONObject>();
        ThirdAppWechatEnterpriseServiceImpl enterpriseService = SpringContextUtils.getBean(ThirdAppWechatEnterpriseServiceImpl.class);
        String accessToken = enterpriseService.getAccessToken();
        log.info("请求参数：code->{}, accessToken->{}", code, accessToken);

        // 获取企业微信域名
        SysParamModel sysParamModel = sysParamApi.selectByCode(WX_HOST_CONFIG_VALUE);
        String wechatHost = Objects.isNull(sysParamModel) ? WX_HOST : StrUtil.isBlank(sysParamModel.getValue()) ? WX_HOST : sysParamModel.getValue();
        // 该接口用于根据code获取成员信息，适用于自建应用与代开发应用
        String url = "https://"+wechatHost+"/cgi-bin/user/getuserinfo?access_token="+accessToken+"&code="+code;
        JSONObject userinfoResult  =  RestUtil.get(url);
        // 请请求结果{"errcode": 0,"errmsg": "ok","userid":"USERID","user_ticket": "USER_TICKET"}
        log.info("请求url->{},请求结果：{}", url, JSONObject.toJSONString(userinfoResult));
        if (Objects.isNull(userinfoResult)) {
            throw new AiurtBootException("获取企业微信用户信息失败！请检查应用的配置信息是否正确！");
        }

        String userId = (String)userinfoResult.getOrDefault("UserId", "");

        // 公有云部署需要user_ticket 获取用户信息
        SysParamModel wxPrivateCloudConfig = sysParamApi.selectByCode(WX_PRIVATE_CLOUD);
        String privateCloudValue = Objects.isNull(wxPrivateCloudConfig)?WX_PRIVATE_CLOUD_VALUE:StrUtil.isBlank(wxPrivateCloudConfig.getValue())?WX_PRIVATE_CLOUD_VALUE:wxPrivateCloudConfig.getValue();

        String phone = "";

        // 公有云
        if (StrUtil.equalsIgnoreCase(privateCloudValue, WX_PRIVATE_CLOUD_VALUE)) {
            // 用户票据
            String userTicket = (String)userinfoResult.getOrDefault("user_ticket", "");

            if (StrUtil.isBlank(userTicket)) {
                throw new  AiurtBootException("获取企业微信用户票据失败！请检查应用的配置信息是否正确！");
            }

            // 获取访问用户敏感信息接口
            String userDetailUrl ="https://"+wechatHost+"/cgi-bin/auth/getuserdetail?access_token="+accessToken;
            // 请求参数
            JSONObject params = new JSONObject();
            params.put("user_ticket", userTicket);
            JSONObject userDetailResult = RestUtil.post(userDetailUrl, params);
            log.info("公有云获取用户手机号，请求url->{},请求结果：{}", userDetailUrl, JSONObject.toJSONString(userDetailResult));
            phone = userDetailResult.getString("mobile");
        } else {

            //
            String userUrl ="https://"+wechatHost+"/cgi-bin/user/get?access_token="+accessToken+"&userid="+userId;
            JSONObject userJson = RestUtil.get(userUrl);
            log.info("公有云获取用户手机号，请求url->{},请求结果：{}", userUrl, JSONObject.toJSONString(userJson));
            phone = userJson.getString("mobile");
        }

        // 获取用户手机号
        if (StrUtil.isBlank(phone)) {
            throw new  AiurtBootException("获取企业微信用户手机号失败！请检查应用的配置信息是否正确！");
        }

        ISysUserService bean = SpringContextUtils.getBean(ISysUserService.class);
        SysUser sysUser = bean.getUserByPhone(phone);
        if (ObjectUtil.isEmpty(sysUser)){
            return result.error500("该用户手机号:"+phone+"没注册, 请联系管理员添加账号！");
        }
        //第一次登录生成第三方登录信息数据
        /*
         * 判断是否同步过的逻辑：
         * 1. 查询 sys_third_account（第三方账号表）是否有数据，如果有代表已同步
         */
        String wechat = ThirdAppConfig.WECHAT_ENTERPRISE.toLowerCase();
        ISysThirdAccountService sysThirdAccountService = SpringContextUtils.getBean(ISysThirdAccountService.class);
        SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneByThirdUserId(userId, wechat);

        if (sysThirdAccount == null) {
            sysThirdAccount = new SysThirdAccount();
            sysThirdAccount.setSysUserId(sysUser.getId());
            sysThirdAccount.setStatus(1);
            sysThirdAccount.setDelFlag(0);
            sysThirdAccount.setThirdType(wechat);
            sysThirdAccount.setThirdUserId(userId);
            sysThirdAccountService.saveOrUpdate(sysThirdAccount);
        }
        String username = sysUser.getUsername();
        String password = sysUser.getPassword();
        // 生成token
        String token = JwtUtil.sign(username, password);
        // 设置token缓存有效时间
        putReids(sysUser, token, JwtUtil.EXPIRE_TIME * 2 / 1000);
        // 获取用户部门信息
        JSONObject obj = new JSONObject();
        List<String> roleList = new ArrayList<String>();
        ISysUserRoleService sysUserRoleService = SpringContextUtils.getBean(ISysUserRoleService.class);
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, sysUser.getId()));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                ISysRoleService sysRoleService =SpringContextUtils.getBean(ISysRoleService.class);
                final SysRole role = sysRoleService.getById(sysUserRole.getRoleId());
                roleList.add(role.getRoleCode());
            }
            obj.put("roleList", roleList);
        }
        obj.put("token", token);
        obj.put("userInfo", sysUser);
        result.setResult(obj);
        result.success("登录成功");
        result.getResult().put("role", "1");
        ISysBaseAPI sysBaseApi =SpringContextUtils.getBean(ISysBaseAPI.class);
        req.getSession().setAttribute("username", req.getParameter("username"));
        return result;
    }

    /**
     * put 用户key到reids
     * @param sysUser
     * @param token
     * @param expireTime
     */
    private void putReids(SysUser sysUser, String token, long expireTime){
        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
        redisUtil.set(org.jeecg.common.constant.CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(org.jeecg.common.constant.CommonConstant.PREFIX_USER_TOKEN + token, expireTime);
        //redisUtil.set(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + sysUser.getId(), sysUser.getDepartmentIds());
        //redisUtil.set(CommonConstant.PREFIX_USER_SYSTEM_CODES + sysUser.getId(), sysUser.getSystemCodes());

    }

    /**
     * 生成签名
     *
     * @param url
     * @return
     */
    @Override
    public Result<JSONObject> autograph(String url) {
        RedisUtil redisUtil =SpringContextUtils.getBean(RedisUtil.class);
        ThirdAppWechatEnterpriseServiceImpl enterpriseService = SpringContextUtils.getBean(ThirdAppWechatEnterpriseServiceImpl.class);
        String accessToken = enterpriseService.getAccessToken();
        String ticket =(String)redisUtil.get("ticket");
        if (ObjectUtil.isEmpty(ticket)){
            SysParamModel sysParamModel = sysParamApi.selectByCode(WX_HOST_CONFIG_VALUE);
            String wechatHost = Objects.isNull(sysParamModel) ? WX_HOST : StrUtil.isBlank(sysParamModel.getValue()) ? WX_HOST : sysParamModel.getValue();
            JSONObject resultJson = RestUtil.get("https://"+wechatHost+"/cgi-bin/get_jsapi_ticket?access_token="+accessToken);
            log.info("请求结果:->{}", JSONObject.toJSONString(resultJson));
            String ticket1 = resultJson.getString("ticket");
            Integer expiresIn = resultJson.getInteger("expires_in");
            Long time = (System.currentTimeMillis() / 1000);
            String noncestr = "akltasdaWWWWW";
            String string1 ="jsapi_ticket="+ticket1+"&noncestr="+noncestr+"&timestamp="+time+"&url="+url;
            log.info("st->{}", string1);
            String signature = sha1(string1);
            JSONObject obj = new JSONObject();
            obj.put("appId", thirdAppConfig.getWechatEnterprise().getClientId());
            obj.put("timestamp",time);
            obj.put("nonceStr",noncestr);
            obj.put("signature",signature);
            redisUtil.set("ticket",ticket1);
            redisUtil.expire("ticket",expiresIn);
            Result<JSONObject> result = new Result<JSONObject>();
            result.setResult(obj);
            result.success("操作成功");
            return result;
        }else {
            Long time = (System.currentTimeMillis() / 1000);
            String noncestr = "akltasdaWWWWW";
            String string1 ="jsapi_ticket="+ticket+"&noncestr="+noncestr+"&timestamp="+time+"&url="+url;
            System.out.println(string1);
            String signature = sha1(string1);
            JSONObject obj = new JSONObject();
            obj.put("appId",thirdAppConfig.getWechatEnterprise().getClientId());
            obj.put("timestamp",time);
            obj.put("nonceStr",noncestr);
            obj.put("signature",signature);
            Result<JSONObject> result = new Result<JSONObject>();
            result.setResult(obj);
            result.success("操作成功");
            return result;
        }
    }
}
