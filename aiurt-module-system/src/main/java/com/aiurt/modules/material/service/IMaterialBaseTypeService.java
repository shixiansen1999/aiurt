package com.aiurt.modules.material.service;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IMaterialBaseTypeService extends IService<MaterialBaseType> {
    /**
     * 物资分类树处理
     * @param materialBaseTypeList
     * @param id
     * @return
     */
    List<MaterialBaseType> treeList(List<MaterialBaseType> materialBaseTypeList, String id);

    /**
     * 物资分类编码分级处理
     * @param materialBaseType
     * @return
     */
    String getCcStr(MaterialBaseType materialBaseType);

    /**
     * 导入
     * @param file
     * @param params
     * @param id
     * @return
     */
    Result importExcelMaterial(MultipartFile file, ImportParams params, String id) throws Exception;
}
