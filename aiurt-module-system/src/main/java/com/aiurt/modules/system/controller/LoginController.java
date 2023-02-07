package com.aiurt.modules.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.aiurt.common.api.dto.LogDTO;
import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.*;
import com.aiurt.common.util.encryption.EncryptedString;
import com.aiurt.modules.system.entity.*;
import com.aiurt.modules.system.model.SysLoginModel;
import com.aiurt.modules.system.service.*;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.aiurt.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import com.aiurt.modules.system.util.RandImageUtil;
import com.aiurt.modules.weaver.service.IWeaverSsoService;
import com.aiurt.modules.weaver.service.entity.WeaverSsoRestultDTO;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static cn.hutool.crypto.SecureUtil.sha1;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	private ISysLogService logService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
    private ISysDepartService sysDepartService;
	@Autowired
	private ISysTenantService sysTenantService;
	@Autowired
    private ISysDictService sysDictService;
	@Resource
	private BaseCommonService baseCommonService;

	@Autowired
	private ICsUserMajorService csUserMajorService;

	@Autowired
	private ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;

	@Autowired
	private IWeaverSsoService weaverSsoService;

	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();

        String captcha = sysLoginModel.getCaptcha();
        if(captcha==null){
            result.error500("验证码无效");
            return result;
        }
        String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+sysLoginModel.getCheckKey(), "utf-8");
		Object checkCode = redisUtil.get(realKey);
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if(checkCode==null || !checkCode.toString().equals(lowerCaseCaptcha)) {
            log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("验证码错误");
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校验验证码

		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		//用户登录信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		 redisUtil.del(realKey);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("用户名: " + sysUser.getRealname() + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}

	@ApiOperation("登录接口无验证码")
	@RequestMapping(value = "/loginWithoutCaptcha", method = RequestMethod.POST)
	public Result<JSONObject> loginWithoutCaptcha(@RequestBody SysLoginModel sysLoginModel){
		Result result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();

		//update-end-author:taoyan date:20190828 for:校验验证码

		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		//用户登录信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("用户名: " + sysUser.getRealname() + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
		//update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}


	/**
	 * 【vue3专用】获取用户信息
	 */
	@GetMapping("/user/getUserInfo")
	@ApiOperation("获取用户信息")
	public Result<JSONObject> getUserInfo(HttpServletRequest request){
		Result<JSONObject> result = new Result<JSONObject>();
		String  username = JwtUtil.getUserNameByToken(request);
		if(oConvertUtils.isNotEmpty(username)) {
			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			JSONObject obj=new JSONObject();
			obj.put("userInfo",sysUser);
			obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
			result.setResult(obj);
			result.success("");
		}
		return result;

	}

	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	@ApiOperation("登出")
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return Result.error("退出登录失败！");
	    }
	    String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseApi.getUserByName(username);
	    if(sysUser!=null) {
			//update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			// 计算在线时长
			Date date = JwtUtil.getExpDateByToken(token);
			int onlineMin = 1;
			if (Objects.nonNull(date)) {
				Date now = new Date();
				date = DateUtil.offset(date, DateField.MILLISECOND, (int) JwtUtil.EXPIRE_TIME*(-1));
				onlineMin = (int) (DateUtil.between(date, now, DateUnit.MINUTE));
				onlineMin =onlineMin==0?onlineMin:onlineMin;
			}
			LogDTO logDTO = new LogDTO();
			logDTO.setLogContent(sysUser.getRealname()+",退出成功！");
			logDTO.setIp(IpUtils.getIpAddr(request));
			logDTO.setLogType(CommonConstant.LOG_TYPE_1);
			logDTO.setLoginUser(sysUser);
			logDTO.setOnlineTime(onlineMin);
			logDTO.setCreateTime(new Date());
			logDTO.setUsername(sysUser.getRealname());
			logDTO.setUserid(sysUser.getUsername());
			baseCommonService.addLog(logDTO);
			//update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
	    	log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
	    	//清空用户登录Token缓存
	    	redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
	    	//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
	    	return Result.ok("退出登录成功！");
	    }else {
	    	return Result.error("Token无效!");
	    }
	}

	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	@ApiOperation("获取访问量")
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
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String,Object>>> visitInfo() {
		Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}


	/**
	 * 登陆成功选择用户当前部门
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode= user.getOrgCode();
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
	@ApiOperation("短信登录接口")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 登录模式: "2"  注册模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
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
				if(sysUser!=null) {
					result.error500(" 手机号已经注册，请直接登录！");
					baseCommonService.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//登录模式，校验用户有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					String message = result.getMessage();
					String e = "该用户不存在，请注册";
					if(e.equals(message)){
						result.error500("该用户不存在或未绑定手机号");
					}
					return result;
				}

				/**
				 * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登录模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
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
			redisUtil.set(mobile, captcha, 600);
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
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		baseCommonService.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

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
		// 获取用户部门信息
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		obj.put("multi_depart", 0);
		String orgId = sysUser.getOrgId();
		if (StringUtils.isNotBlank(orgId)) {
			SysDepart depart = sysDepartService.getById(orgId);
			depart = Optional.ofNullable(depart).orElse(new SysDepart());
			sysUser.setOrgCode(depart.getOrgCode());
			sysUser.setOrgName(depart.getDepartName());
		}
		//获取用户角色名称
		String userId = sysUser.getId();
		List<String> roleNamesById = sysBaseApi.getRoleNamesById(userId);
		if(CollUtil.isNotEmpty(roleNamesById)){
			obj.put("roleNames",roleNamesById);
		}

		String tenantIds = sysUser.getRelTenantIds();
		if (oConvertUtils.isNotEmpty(tenantIds)) {
			List<Integer> tenantIdList = new ArrayList<>();
			for(String id: tenantIds.split(SymbolConstant.COMMA)){
				tenantIdList.add(Integer.valueOf(id));
			}
			// 该方法仅查询有效的租户，如果返回0个就说明所有的租户均无效。
			List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
			if (tenantList.size() == 0) {
				result.error500("与该用户关联的租户均已被冻结，无法登录！");
				return result;
			} else {
				obj.put("tenantList", tenantList);
			}
		}

		// fugaowei 获取用户专业
		LambdaQueryWrapper<CsUserMajor> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CsUserMajor::getUserId, sysUser.getId());
		List<CsUserMajor> csUserMajorList = csUserMajorService.getBaseMapper().selectList(wrapper);
		if (CollectionUtil.isEmpty(csUserMajorList)) {
			csUserMajorList = Collections.emptyList();
		}
		obj.put("majorInfo", csUserMajorList);

		// update-end--Author:sunjianlei Date:20210802 for：获取用户租户信息
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 获取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap(5);
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 * @param response
	 * @param key
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(HttpServletResponse response,@PathVariable("key") String key){
		Result<String> res = new Result<String>();
		try {
			//生成验证码
			final String baseCheckCodes = "1234567890";
			String code = RandomUtil.randomString(baseCheckCodes,4);

			//存到redis中
			String lowerCaseCode = code.toLowerCase();
			String realKey = Md5Util.md5Encode(lowerCaseCode+key, "utf-8");
            log.info("获取验证码，Redis checkCode = {}，key = {}", code, key);
			redisUtil.set(realKey, lowerCaseCode, 300);
			log.info("获取过期时间");
			long expire = redisUtil.getExpire(realKey);
			//返回前端
			res.setMessage(Convert.toStr(expire));
			//返回前端
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setResult(base64);
		} catch (Exception e) {
			res.error500("获取验证码出错"+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * app登录
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	@ApiOperation("app登录")
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();

		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		String orgId = sysUser.getOrgId();
		if (StrUtil.isNotBlank(orgId)) {
			SysDepart sysDepart = sysDepartService.getById(orgId);
			if (Objects.nonNull(sysDepart)) {
				sysUser.setOrgName(sysDepart.getDepartName());
			}
		}
		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);

		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 图形验证码
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return Result.error("验证码无效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("验证码错误");
		}
		return Result.ok();
	}
	/**
	 * 登录二维码
	 */
	@ApiOperation(value = "登录二维码", notes = "登录二维码")
	@GetMapping("/getLoginQrcode")
	public Result<?>  getLoginQrcode() {
		String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE+IdWorker.getIdStr();
		//定义二维码参数
		Map params = new HashMap(5);
		params.put("qrcodeId", qrcodeId);
		//存放二维码唯一标识30秒有效
		redisUtil.set(CommonConstant.LOGIN_QRCODE + qrcodeId, qrcodeId, 30);
		return Result.OK(params);
	}
	/**
	 * 扫码二维码
	 */
	@ApiOperation(value = "扫码登录二维码", notes = "扫码登录二维码")
	@PostMapping("/scanLoginQrcode")
	public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
		Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isNotEmpty(check)) {
			//存放token给前台读取
			redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN+qrcodeId, token, 60);
		} else {
			return Result.error("二维码已过期,请刷新后重试");
		}
		return Result.OK("扫码成功");
	}


	/**
	 * 获取用户扫码后保存的token
	 */
	@ApiOperation(value = "获取用户扫码后保存的token", notes = "获取用户扫码后保存的token")
	@GetMapping("/getQrcodeToken")
	public Result getQrcodeToken(@RequestParam String qrcodeId) {
		Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
		Map result = new HashMap(5);
		Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
			//二维码过期通知前台刷新
			result.put("token", "-2");
			return Result.OK(result);
		}
		if (oConvertUtils.isNotEmpty(token)) {
			result.put("success", true);
			result.put("token", token);
		} else {
			result.put("token", "-1");
		}
		return Result.OK(result);
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
		ThirdAppWechatEnterpriseServiceImpl enterpriseService = SpringContextUtils.getBean(ThirdAppWechatEnterpriseServiceImpl.class);
		String accessToken = enterpriseService.getAccessToken();
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
		ISysBaseAPI sysBaseApi =SpringContextUtils.getBean(ISysBaseAPI.class);
		req.getSession().setAttribute("username", req.getParameter("username"));
		return result;
	}
	@ApiOperation("生成签名")
	@GetMapping(value = "/autograph")
	public Result<JSONObject> autograph(@RequestParam(name = "url") String url) {
		RedisUtil redisUtil =SpringContextUtils.getBean(RedisUtil.class);
		ThirdAppWechatEnterpriseServiceImpl enterpriseService = SpringContextUtils.getBean(ThirdAppWechatEnterpriseServiceImpl.class);
		String accessToken = enterpriseService.getAccessToken();
		String ticket =(String)redisUtil.get("ticket");
		if (ObjectUtil.isEmpty(ticket)){
			Map response1 = RestUtil.get("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+accessToken);
			log.info("请求结果:->{}", JSONObject.toJSONString(response1));
			String ticket1 = (String)response1.get("ticket");
			Integer expiresIn = (Integer) response1.get("expires_in");
			Long time = (System.currentTimeMillis() / 1000);
			String noncestr = "akltasdaWWWWW";
			String string1 ="jsapi_ticket="+ticket1+"&noncestr="+noncestr+"&timestamp="+time+"&url="+url;
			log.info("st->{}", string1);
			String signature = sha1(string1);
			JSONObject obj = new JSONObject();
			obj.put("appId","ww19d88c8272303c7b");
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
	 *根据token查询用户信息
	 * @return
	 */
	@ApiOperation("根据token查询用户信息")
	@RequestMapping(value = "/queryAccordingToken", method = RequestMethod.GET)
	private Result<?> queryAccordingToken(HttpServletRequest req,
										  @RequestParam(name = "token") String token){
		String username =JwtUtil.getUsername(token);
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


	public static void main(String[] args) {
		//获取当前系统RSA加密的公钥
		RSA rsa = new RSA();
		String publicKey = rsa.getPublicKeyBase64();
		String privateKey = rsa.getPrivateKeyBase64();


		RSA rsa1 = new RSA(null,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtp6TEWlM6HZQk3wcAODWcsQdIXKL+JBwaUKeu7JR3+PPhAmwvKMXgB3pj+UpK50ycyPISgj5WMRCMquOXa8KjQmNSmm3hG99sSIVnyTXpx/opGlzDQih4utg0MYE08a575Hi3wvrbbGHHgHNFUPL8WqyqSJlj95QVwp1aqFP9FEWg5Sh4Ps1zX58i5XFH/TLFYiI4OeAALKSpbfcBaAsNN7noKsL4iS4gVVnd6tqlt3ubUuzYQ7Q0uQBfNa5GtA2PbirA56ue12Lqh1y5HhnLp+aH9+/ga7HWuhWFtSyXtrK2SD3WGXhUIrXFlpgqj0cPBik4HT8S0yJ7wdy/Oa7EQIDAQAB");
		//对秘钥进行加密传输，防止篡改数据
		String encryptSecret = rsa1.encryptBase64("cadfb4a6-404b-4a8b-9552-55bc56141875", CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);

		String encryptUserid = rsa1.encryptBase64("1",CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
		System.out.println(encryptUserid);
	}

	@GetMapping("/getWeaverToken")
	@ApiOperation("获取泛微token信息")
	public Result<WeaverSsoRestultDTO> getWeaverToken() {
		WeaverSsoRestultDTO serviceToken = weaverSsoService.getToken();
		return Result.OK(serviceToken);
	}

	@GetMapping("/getSsoToken")
	@ApiOperation("获取泛微token信息")
	public Result<String> getSsoToken() {
		String ssoToken = weaverSsoService.ssoToken();
		return Result.OK(ssoToken);
	}

}
