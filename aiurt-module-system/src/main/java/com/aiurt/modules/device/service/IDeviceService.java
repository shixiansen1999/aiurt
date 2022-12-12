package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceService extends IService<Device> {

    /**
     * 查询详情
     * @param deviceId
     * @return
     */
    Result<Device> queryDetailById(String deviceId);

    /**
     * 翻译
     * @param device
     * @return
     */
    Device translate(Device device);

    /**
     * 编码分级
     * @param deviceTypeCodeCc
     * @return
     */
    String getCodeByCc(String deviceTypeCodeCc);

    /**
     * 查找设备
     * @param stationCode
     * @param positionCodeCc
     * @param temporary
     * @param majorCode
     * @param systemCode
     * @param deviceTypeCode
     * @param code
     * @param name
     * @param status
     * @return
     */
    QueryWrapper<Device> getQueryWrapper(String stationCode,String positionCodeCc, String temporary, String majorCode, String systemCode, String deviceTypeCode, String code, String name, String status);

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);

    /**
     * 设备管理-设备主数据-添加
     * @param device
     * @return
     */
    Result<Device> add(Device device);

    /**
     * 设备主数据导出
     * @param device
     * @param request
     * @param response
     */
    void exportXls(Device device, HttpServletRequest request, HttpServletResponse response);

    /**
     * 设备主数据模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXl(HttpServletResponse response) throws IOException;
}
