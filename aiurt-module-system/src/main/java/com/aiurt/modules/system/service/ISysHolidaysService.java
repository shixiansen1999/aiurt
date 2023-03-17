package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.SysHolidays;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: sys_holidays
 * @Author: aiurt
 * @Date:   2023-03-16
 * @Version: V1.0
 */
public interface ISysHolidaysService extends IService<SysHolidays> {

    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<SysHolidays> sysHolidaysClass);
}
