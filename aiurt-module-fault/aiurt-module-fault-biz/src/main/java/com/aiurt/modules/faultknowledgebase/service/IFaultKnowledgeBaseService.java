package com.aiurt.modules.faultknowledgebase.service;

import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface IFaultKnowledgeBaseService extends IService<FaultKnowledgeBase> {
    /**
     * 故障知识库查询
     * @param page
     * @param faultKnowledgeBase
     * @return IPage<faultKnowledgeBase>
     */
    IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase);


    /**
     * 故障知识库查询，不分页
     * @param faultKnowledgeBase
     * @return
     */
    List<FaultKnowledgeBase> queryAll(FaultKnowledgeBase faultKnowledgeBase);


    /**
     * 故障选择查询
     * @param page
     * @param faultDTO
     * @return List<Fault>
     * */
    IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO);

    /**
     *  审批
     *
     * @param approvedRemark
     * @param approvedResult
     * @param id
     * @return
     */
    Result<String> approval(String approvedRemark, Integer approvedResult, String id);

    /**
     * 删除
     * @param id
     */
    Result<String> delete(String id);

    Result<String> deleteBatch(List<String> ids);


    /**
     * 故障知识库模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXls(HttpServletResponse response) throws IOException;


    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
