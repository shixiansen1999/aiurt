package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IDeviceTypeService extends IService<DeviceType> {


    /**
     * DeviceType树
     * @param typeList
     * @param id
     * @return
     */
    List<DeviceType> treeList(List<DeviceType> typeList, String id);
    /**
     * 列表
     * @return
     */
    List<DeviceType> selectList();
    /**
     * 添加
     *
     * @param deviceType
     * @return
     */
    Result<?> add(DeviceType deviceType);
    /**
     * 编辑
     *
     * @param deviceType
     * @return
     */
    Result<?> update(DeviceType deviceType);

    /**
     * 拼接cc字段
     * @param deviceType
     * @return
     */
    String getCcStr(DeviceType deviceType);

    /**
     * 导入
     * @param file
     * @param params
     * @return
     */
    Result<?> importExcelMaterial(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 导出
     * @param request
     * @param response
     * @param deviceType
     */
    ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, DeviceType deviceType);
}
