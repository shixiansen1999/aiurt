package com.aiurt.modules.sensorinformation.service;


import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: sensor_information
 * @Author: aiurt
 * @Date:   2023-05-15
 * @Version: V1.0
 */
public interface ISensorInformationService extends IService<SensorInformation> {
    /**
     * 传感器-列表
     * @param page
     * @param sensorInformation
     * @return
     */
    Page<SensorInformation> queryPageList(Page<SensorInformation> page, SensorInformation sensorInformation);
    /**
     * 传感器-添加
     * @param sensorInformation
     */
    void add(SensorInformation sensorInformation);

    /**
     * 传感器-编辑
     * @param sensorInformation
     */
    void edit(SensorInformation sensorInformation);


    /**
     * 传感器-列表(不分页)
     * @param sensorInformation
     * @return
     */
    List<SensorInformation> getList(SensorInformation sensorInformation);

    /**
     * 传感器-导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
