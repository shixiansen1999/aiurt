package com.aiurt.boot.modules.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.constant.CacheConstant;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.system.util.JwtUtil;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.*;
import com.aiurt.boot.common.util.encryption.AesEncryptUtil;
import com.aiurt.boot.common.util.encryption.EncryptedString;
import com.aiurt.boot.modules.shiro.vo.DefContants;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysRole;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.entity.SysUserRole;
import com.aiurt.boot.modules.system.model.SysLoginModel;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysLogService;
import com.aiurt.boot.modules.system.service.ISysRoleService;
import com.aiurt.boot.modules.system.service.ISysUserRoleService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static cn.hutool.crypto.SecureUtil.sha1;


/**
 * @Author swsc
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags = "用户登录")
@Slf4j
public class LoginController {
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private ISysLogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysRoleService sysRoleService;

    private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

    @ApiOperation("登录接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<JSONObject> login(HttpServletRequest req, @RequestBody SysLoginModel sysLoginModel) {
        Result<JSONObject> result = new Result<JSONObject>();

        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();
        //update-begin--Author:swsc  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
        //前端密码加密，后端进行密码解密
        try {
            username = AesEncryptUtil.desEncrypt(sysLoginModel.getUsername().replaceAll("%2B", "\\+")).trim();//密码解密
            password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
        } catch (Exception e) {
            e.printStackTrace();
        }
        //update-begin--Author:swsc  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

        //update-begin-author:taoyan date:20190828 for:校验验证码
       /* String checkCode = (String) redisUtil.get(sysLoginModel.getCheckKey());
        if (checkCode == null) {
            result.error500("验证码失效,请刷新重新输入!");
            return result;
        }
        if (!checkCode.equalsIgnoreCase(sysLoginModel.getCaptcha())) {
            result.error500("验证码错误!");
            return result;
        }*/
        //update-end-author:taoyan date:20190828 for:校验验证码

        //1. 校验用户是否有效
        SysUser sysUser = null;
        /*List<SysUser> userList = sysUserService.findUserByAccount(username);
        if (userList != null && userList.size() > 0) {
            sysUser = userList.get(0);
            if(userList.size() > 1){
                sysUser.setShowChange(1);
            }
            //sysUser.setAccounts(userList);
        }*/
        sysUser = sysUserService.getUserByName(username);
        // sysUser = sysUserService.getUserByName("admin");
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (false) {
            result.error500("用户名或密码错误");
            return result;
        }

        //用户登录信息
        userInfo(sysUser, result);
        result.getResult().put("role", "1");
        sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

        req.getSession().setAttribute("username", req.getParameter("username"));
        return result;
    }

    private void dealAccounts(SysUser sysUser, Result<JSONObject> result) {
        List<SysUser> accounts = sysUserService.list(new QueryWrapper<SysUser>()
                //.eq("account", sysUser.getAccount())
                .select("username", "id"));
        if (accounts.size() > 1) {
            SysUser userInfo = (SysUser) result.getResult().get("userInfo");
            for (SysUser user : accounts) {
                List<SysDepart> departList = sysDepartService.queryUserDeparts(user.getId());
                if (departList.size() > 0) {
                    SysDepart depart = departList.get(0);
                    user.setOrgName(depart.getDepartName());
                }
            }
        }
    }

    /**
     * 切换多账号的token
     *
     * @param sysLoginModel
     * @return
     */
    @PostMapping(value = "/changeToken")
    public Result<JSONObject> changeToken(@RequestBody SysLoginModel sysLoginModel, HttpServletRequest request) {
        Result<JSONObject> result = new Result<>();
        Object token = redisUtil.get(CommonConstant.PREFIX_USER_TOKEN + sysLoginModel.getToken());
        if (ObjectUtil.isEmpty(token)) {
            result.error500("登录用户不一致！请重新登录");
            return result;
        }
        String oldUsername = JwtUtil.getUserNameByToken(request);
        SysUser oldSysUser = sysUserService.getUserByName(oldUsername);
        String newUsername = sysLoginModel.getUsername();
        //SysUser newSysUser = sysUserService.getUserByAccountAndOrgcode(oldSysUser.getAccount(), newUsername);
        //查询当前用户所在部门名称，用于导航栏的部门显示
	/*	List<SysDepart> sysDeparts = sysDepartService.queryUserDeparts(newSysUser.getId());
		if (sysDeparts.size() > 0){
			newSysUser.setOrgName(sysDeparts.get(0).getDepartName());
		}*/
        //newSysUser.setShowChange(0);
//        if (!oldUsername.equals(newSysUser.getUsername())) {
        //清空用户登录Token缓存
        redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
        //清空用户登录Shiro权限缓存
        redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + oldSysUser.getId());
        //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<newUsername>
        redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, oldUsername));
        log.info(" 用户名:  " + oldSysUser.getRealname() + ",退出成功！ ");
        //dealAccounts(newSysUser, result);
        //userInfo(newSysUser, result);
//        }
        result.isSuccess();
        return result;
    }


    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout")
    public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        //用户退出逻辑
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token)) {
            return Result.error("退出登录失败！");
        }
        String username = JwtUtil.getUsername(token);
        LoginUser sysUser = sysBaseAPI.getUserByName(username);
        if (sysUser != null) {
            sysBaseAPI.addLog("用户名: " + sysUser.getRealname() + ",退出成功！", CommonConstant.LOG_TYPE_1, null);
            log.info(" 用户名:  " + sysUser.getRealname() + ",退出成功！ ");
            //清空用户登录Token缓存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //调用shiro的logout
            SecurityUtils.getSubject().logout();

            return Result.ok("退出登录成功！");
        } else {
            return Result.error("Token无效!");
        }
    }

    /**
     * 获取访问量
     *
     * @return
     */
    @GetMapping("loginfo")
    public Result<JSONObject> loginfo() {
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject obj = new JSONObject();
        //update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
        // 获取一天的开始和结束时间
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dayStart = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date dayEnd = calendar.getTime();
        // 获取系统访问记录
        Long totalVisitCount = logService.findTotalVisitCount();
        obj.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = logService.findTodayVisitCount(dayStart, dayEnd);
        obj.put("todayVisitCount", todayVisitCount);
        Long todayIp = logService.findTodayIp(dayStart, dayEnd);
        //update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
        obj.put("todayIp", todayIp);
        result.setResult(obj);
        result.success("登录成功");
        return result;
    }

    /**
     * 获取访问量
     *
     * @return
     */
    @GetMapping("visitInfo")
    public Result<List<Map<String, Object>>> visitInfo() {
        Result<List<Map<String, Object>>> result = new Result<List<Map<String, Object>>>();
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String, Object>> list = logService.findVisitCount(dayStart, dayEnd);
        result.setResult(oConvertUtils.toLowerCasePageList(list));
        return result;
    }


    /**
     * 登陆成功选择用户当前部门
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
    public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = user.getUsername();
        if (oConvertUtils.isEmpty(username)) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            username = sysUser.getUsername();
        }
        String orgCode = user.getOrgCode();
        this.sysUserService.updateUserDepart(username, orgCode);
        SysUser sysUser = sysUserService.getUserByName(username);
        JSONObject obj = new JSONObject();
        obj.put("userInfo", sysUser);
        result.setResult(obj);
        return result;
    }

    /**
     * 短信登录接口
     *
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "/sms")
    public Result<String> sms(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String mobile = jsonObject.get("mobile").toString();
        String smsmode = jsonObject.get("smsmode").toString();
        log.info(mobile);
        Object object = redisUtil.get(mobile);
        if (object != null) {
            result.setMessage("验证码10分钟内，仍然有效！");
            result.setSuccess(false);
            return result;
        }

        //随机数
        String captcha = RandomUtil.randomNumbers(6);
        JSONObject obj = new JSONObject();
        obj.put("code", captcha);
        try {
            boolean b = false;
            //注册模板
            if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
                SysUser sysUser = sysUserService.getUserByPhone(mobile);
                if (sysUser != null) {
                    result.error500(" 手机号已经注册，请直接登录！");
                    sysBaseAPI.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
                    return result;
                }
                b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
            } else {
                //登录模式，校验用户有效性
               /* SysUser sysUser = sysUserService.getUserByPhone(mobile);
                result = sysUserService.checkUserIsEffective(sysUser);
                if (!result.isSuccess()) {
                    return result;
                }*/

                /**
                 * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
                 */
                if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
                    //登录模板
                    b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
                } else if (CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
                    //忘记密码模板
                    b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
                }
            }

            if (b == false) {
                result.setMessage("短信验证码发送失败,请稍后重试");
                result.setSuccess(false);
                return result;
            }
            //验证码10分钟内有效
            redisUtil.set(mobile, captcha, 60 * 10);
            //update-begin--Author:swsc  Date:20190812 for：issues#391
            //result.setResult(captcha);
            //update-end--Author:swsc  Date:20190812 for：issues#391
            result.setSuccess(true);

        } catch (ClientException e) {
            e.printStackTrace();
            result.error500(" 短信接口未配置，请联系管理员！");
            return result;
        }
        return result;
    }


    /**
     * 手机号登录接口
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("手机号登录接口")
    @PostMapping("/phoneLogin")
    public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("mobile");

        //校验用户有效性
        /* SysUser sysUser = sysUserService.getUserByPhone(phone);*/
       /* result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }*/
       /* String smscode = jsonObject.getString("captcha");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            return result;
        }*/
        SysUser sysUser = sysUserService.registerUserOrSelectUser(phone);
        //用户信息
        userInfo(sysUser, result);
        result.getResult().put("role", "0");
        //添加日志
        sysBaseAPI.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

        return result;
    }


    /**
     * 用户信息
     *
     * @param sysUser
     * @param result
     * @return
     */
    private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
        String syspassword = sysUser.getPassword();
        String username = sysUser.getUsername();
        String id = sysUser.getId();
        // 生成token
        String token = JwtUtil.sign(username, syspassword);
        // 设置token缓存有效时间
        putReids(sysUser, token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        // 获取用户部门信息
        JSONObject obj = new JSONObject();
       /* List<SysDepart> departs = sysDepartService.queryDepartsByUsername(sysUser.getAccount());
        if (departs != null && departs.size() > 0) {
            for (SysDepart depart : departs) {
                if (depart.getOrgCode().equals(sysUser.getOrgCode())) {
                    sysUser.setOrgName(depart.getDepartName());
                }
            }
            if (departs.size() > 1) {
                sysUser.setDepartList(departs);
                sysUser.setMultiDepart(2);
            }
        }*/
        obj.put("token", token);
        obj.put("userInfo", sysUser);
        result.setResult(obj);
        result.success("登录成功");
        return result;
    }

    /**
     * 获取加密字符串
     *
     * @return
     */
    @GetMapping(value = "/getEncryptedString")
    public Result<Map<String, String>> getEncryptedString() {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", EncryptedString.key);
        map.put("iv", EncryptedString.iv);
        result.setResult(map);
        return result;
    }

    /**
     * 获取校验码
     */
    @ApiOperation("获取验证码")
    @GetMapping(value = "/getCheckCode")
    public Result<Map<String, String>> getCheckCode() {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            String code = RandomUtil.randomString(BASE_CHECK_CODES, 4);
            String key = MD5Util.MD5Encode(code + System.currentTimeMillis(), "utf-8");
            redisUtil.set(key, code, 60);
            map.put("key", key);
            map.put("code", code);
            result.setResult(map);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    /**
     *网页授权登录
     * @return
     */
    @ApiOperation("企业微信网页授权登录")
    @RequestMapping(value = "/webAuthorizationLogin", method = RequestMethod.GET)
    private Result<JSONObject> webAuthorizationLogin(HttpServletRequest req,
                                                     @RequestParam(name = "code") String code){
        Result<JSONObject> result = new Result<JSONObject>();
      Map response = RestUtil.get( "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww19d88c8272303c7b&corpsecret=dlcGybmI3DaooKDYv7g3cKKBcVmtd5Ljb82TgHBq6Jk");
           String accessToken = (String) response.get("access_token");
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="+accessToken+"&code="+code;
      Map response1  =  RestUtil.get(url);
           String userId = (String)response1.get("UserId");
           String url1 ="https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+accessToken+"&userid="+userId;
      Map response2 = RestUtil.get(url1);
           String phone = (String)response2.get("mobile");
        ISysUserService bean = SpringContextUtils.getBean(ISysUserService.class);
        SysUser sysUser = bean.getUserByPhone(phone);
           if (ObjectUtil.isEmpty(sysUser)){
              return result.error500("请注册好用户信息");
           }
           String username = sysUser.getUsername();
           String password = sysUser.getPassword();
        System.out.println(username+":"+password);
        // 生成token
        String token = JwtUtil.sign(username, password);
        // 设置token缓存有效时间
        putReids(sysUser, token, JwtUtil.EXPIRE_TIME * 2 / 1000);
        // 获取用户部门信息
        JSONObject obj = new JSONObject();
        List<String> roleList = new ArrayList<String>();
        ISysUserRoleService sysUserRoleService =SpringContextUtils.getBean(ISysUserRoleService.class);
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
        ISysBaseAPI sysBaseAPI =SpringContextUtils.getBean(ISysBaseAPI.class);
        sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
        req.getSession().setAttribute("username", req.getParameter("username"));
        return result;
    }
    @ApiOperation("生成签名")
    @GetMapping(value = "/autograph")
    public Result<JSONObject> autograph(@RequestParam(name = "url") String url) {
        RedisUtil redisUtil =SpringContextUtils.getBean(RedisUtil.class);
        Map response = RestUtil.get( "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww19d88c8272303c7b&corpsecret=dlcGybmI3DaooKDYv7g3cKKBcVmtd5Ljb82TgHBq6Jk");
        String accessToken = (String) response.get("access_token");
        String ticket =(String)redisUtil.get("ticket");
        if (ObjectUtil.isEmpty(ticket)){
            Map response1 = RestUtil.get("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+accessToken);
            String ticket1 = (String)response1.get("ticket");
            Integer expiresIn = (Integer) response1.get("expires_in");
            Long time = (System.currentTimeMillis() / 1000);
            String noncestr = "akltasdaWWWWW";
            String string1 ="jsapi_ticket="+ticket1+"&noncestr="+noncestr+"&timestamp="+time+"&url="+url;
            System.out.println(string1);
            String signature = sha1(string1);
            JSONObject obj = new JSONObject();
            obj.put("appId","wwfc07c9f3a1c075aa");
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
            obj.put("appId","ww19d88c8272303c7b");
            obj.put("timestamp",time);
            obj.put("nonceStr",noncestr);
            obj.put("signature",signature);
            Result<JSONObject> result = new Result<JSONObject>();
            result.setResult(obj);
            result.success("操作成功");
            return result;
        }
    }
    /**
     *网页授权登录
     * @return
     */
    @ApiOperation("根据token查询用户信息")
    @RequestMapping(value = "/queryAccordingToken", method = RequestMethod.GET)
    private Result<JSONObject> queryAccordingToken(HttpServletRequest req,
                                                     @RequestParam(name = "token") String token){
        String username = JwtUtil.getUsername(token);
        ISysUserService sysUserService =SpringContextUtils.getBean(ISysUserService.class);
        SysUser sysUser = sysUserService.getUserByName(username);
        if (sysUser != null) {
            JSONObject obj = new JSONObject();
            //用户登录信息
            obj.put("userInfo", sysUser);
            obj.put("token", token);
            Result<JSONObject> result =new Result<>();
            result.setResult(obj);
            result.setSuccess(true);
            result.setCode(200);
            return result;
        } else {
            return Result.error("Token无效!");
        }
    }
    /**
     * app登录
     *
     * @param sysLoginModel
     * @return
     * @throws Exception
     */
    @ApiOperation("app-登录")
    @RequestMapping(value = "/mLogin", method = RequestMethod.POST)
    public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();

        //1. 校验用户是否有效
        SysUser sysUser = sysUserService.getUserByName(username);
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (false) {
            result.error500("用户名或密码错误");
            return result;
        }

        String orgCode = sysUser.getOrgCode();
        if (oConvertUtils.isEmpty(orgCode)) {
            //如果当前用户无选择部门 查看部门关联信息
            List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
            if (departs == null || departs.size() == 0) {
                result.error500("用户暂未归属部门,不可登录!");
                return result;
            }
            orgCode = departs.get(0).getOrgCode();
            sysUser.setOrgCode(orgCode);
            this.sysUserService.updateUserDepart(username, orgCode);
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);

        List<String> roleList = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, sysUser.getId()));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                final SysRole role = sysRoleService.getById(sysUserRole.getRoleId());
                roleList.add(role.getRoleCode());
            }
            obj.put("roleList", roleList);
        }

        // 生成token
        String token = JwtUtil.sign(username, syspassword);
        // 设置超时时间
        putReids(sysUser, token, JwtUtil.EXPIRE_TIME * 24 / 1000);
        //token 信息
        obj.put("token", token);
        result.setResult(obj);
        result.setSuccess(true);
        result.setCode(200);
        sysBaseAPI.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return result;
    }

    /**
     * put 用户key到reids
     * @param sysUser
     * @param token
     * @param expireTime
     */
    private void putReids(SysUser sysUser, String token, long expireTime){
        RedisUtil redisUtil =SpringContextUtils.getBean(RedisUtil.class);
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, expireTime);
        redisUtil.set(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + sysUser.getId(), sysUser.getDepartmentIds());
        redisUtil.set(CommonConstant.PREFIX_USER_SYSTEM_CODES + sysUser.getId(), sysUser.getSystemCodes());

    }
}
