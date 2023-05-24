package com.aiurt.boot.wificonnect.controller;


import com.aiurt.boot.wificonnect.entity.WifiConnect;
import com.aiurt.boot.wificonnect.service.IWifiConnectService;


import com.aiurt.common.aspect.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

/**
 * @Description: wifi_connect
 * @Author: jeecg-boot
 * @Date:   2023-05-24
 * @Version: V1.0
 */
@Api(tags="wifi连接记录")
@RestController
@RequestMapping("/wifiConnect")
@Slf4j
public class WifiConnectController {
	@Autowired
	private IWifiConnectService wifiConnectService;

	/**
	 *   添加
	 *
	 * @param wifiConnect
	 * @return
	 */
	@AutoLog(value = "wifi连接记录-添加")
	@ApiOperation(value="wifi连接记录-添加", notes="wifi连接记录-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody @Valid WifiConnect wifiConnect) {
		wifiConnectService.saveOne(wifiConnect);
		return Result.OK("添加成功！");
	}
	

}
