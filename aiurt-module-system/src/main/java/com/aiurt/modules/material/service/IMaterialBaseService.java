package com.aiurt.modules.material.service;

import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import org.jeecg.common.api.vo.Result;
/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IMaterialBaseService extends IService<MaterialBase> {
    /**
     * 物资新增--获取一个物资编码
     * @param finalstr 专业编码+子系统编码+物资类型编码（字符串）
     * @return
     */
    String getNewBaseCode(String finalstr);

    /**
     * 当不是分页时，可以调用此方法进行翻译
     * @param materialBase 未翻译实体
     * @return
     */
    MaterialBase translate(MaterialBase materialBase);

    /**
     * 导入
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcelMaterial(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 编码分级处理
     * @param baseTypeCodeCc
     * @return
     */
    String getCodeByCc(String baseTypeCodeCc);
}
