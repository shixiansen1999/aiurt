package com.aiurt.modules.faultknowledgebase.service;

import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultknowledgebase.dto.FaultKnowledgeBaseBuildDTO;
import com.aiurt.modules.faultknowledgebase.dto.RepairSolRecDTO;
import com.aiurt.modules.faultknowledgebase.dto.SymptomReqDTO;
import com.aiurt.modules.faultknowledgebase.dto.SymptomResDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseMatchDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
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
    IPage<FaultKnowledgeBaseBuildDTO> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase);


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

    /**
     * 故障知识库-通过id查询
     *
     * @param id
     * @return
     */
    FaultKnowledgeBase readOne(String id);

    /**
     * 查找故障现象模板
     * @param symptomReqDTO
     * @return
     */
    Page<SymptomResDTO> querySymptomTemplate(SymptomReqDTO symptomReqDTO);

    /**
     * 维修建议
     * @param knowledgeId 知识库id
     * @return
     */
    RepairSolRecDTO queryRepairSolRecDTO(String knowledgeId);

    /**
     * 故障知识库高级搜索-分页列表查询
     *
     * @param page
     * @param knowledgeBaseReqDTO
     * @return
     */
    IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO);

    /**
     * 同步故障知识库数据到ES
     *
     * @param request
     * @param response
     */
    void synchrodata(HttpServletRequest request, HttpServletResponse response);

    /**
     * 智能助手知识库数据匹配
     *
     * @param page
     * @param knowledgeBaseMatchDTO
     * @return
     */
    IPage<KnowledgeBaseResDTO> knowledgeBaseMatching(Page<KnowledgeBaseResDTO> page, KnowledgeBaseMatchDTO knowledgeBaseMatchDTO);

    /**
     * 智能助手故障现象匹配
     *
     * @param request
     * @param response
     * @param knowledgeBaseMatchDTO
     * @return
     */
    List<String> phenomenonMatching(HttpServletRequest request, HttpServletResponse response, KnowledgeBaseMatchDTO knowledgeBaseMatchDTO);

    /**
     *  标准维修方案要求查询
     * @param faultCauseSolutionIdList
     * @return
     */
    List<FaultSparePart> getStandardRepairRequirements(String[] faultCauseSolutionIdList);
}
