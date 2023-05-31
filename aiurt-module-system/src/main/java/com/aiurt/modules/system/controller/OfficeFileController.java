package com.aiurt.modules.system.controller;


import com.aiurt.modules.system.service.IOfficeFileService;
import com.aiurt.modules.system.service.impl.OfficeFileServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fgw
 */
@Slf4j
@RestController
@RequestMapping("/sys/common")
@Api(tags = "office文件")
public class OfficeFileController {

    @Autowired
    private IOfficeFileService officeFile;

    /**
     * 在线编辑回调
     *
     * @param request  表示传入请求的 HttpServletRequest 对象
     * @param response 表示要发送的 HttpServletResponse 对象
     * @throws Exception 如果在处理请求过程中发生错误
     */
    @RequestMapping(value = "/callback/**", method = {RequestMethod.GET, RequestMethod.POST})
    public void callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        officeFile.callback(request, response);
    }

    /**
     * 获取系统文件的键。
     *
     * @param id 文件Id
     * @return 包含结果的 Result 对象，结果类型为 String
     */
    @RequestMapping("/getSysFileKey")
    @ApiOperation("获取在线编辑key")
    public Result<String> getSysFileKey(@ApiParam(name = "id", value = "文件Id") @RequestParam(value = "id", required = false) String id) {
        String documentKey = officeFile.getSysFileKey(id);
        return Result.OK(documentKey);
    }
}
