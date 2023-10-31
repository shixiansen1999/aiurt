package com.aiurt.modules.material.service;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    /**
     * 根据code获取物资基础数据，包括已删除的
     * @param code
     * @return
     */
    MaterialBase selectByCode(String code);

    /**
     * 获取导入模板
     * @param response 相应
     * @param request 请求
     */
    void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException;
}
