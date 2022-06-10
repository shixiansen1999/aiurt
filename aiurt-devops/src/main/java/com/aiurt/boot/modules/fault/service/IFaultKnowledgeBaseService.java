package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.result.FaultCodesResult;
import com.aiurt.boot.common.result.FaultKnowledgeBaseResult;
import com.aiurt.boot.modules.fault.dto.FaultKnowledgeBaseDTO;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBase;
import com.aiurt.boot.modules.fault.param.FaultKnowledgeBaseParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultKnowledgeBaseService extends IService<FaultKnowledgeBase> {

    /**
     * 查询故障知识库
     * @param page
     * @param param
     * @return
     */
    IPage<FaultKnowledgeBaseResult> pageList(IPage<FaultKnowledgeBaseResult> page,FaultKnowledgeBaseParam param);

    /**
     * 添加故障知识库
     * @param dto
     */
    public Long add(FaultKnowledgeBaseDTO dto, HttpServletRequest req);

    /**
     * 根据id获取关联故障
     * @param id
     * @return
     */
    Result<List<FaultCodesResult>>  getAssociateFault(Long id);

    /**
     * 更改关联故障
     * @param id
     * @param faultCodes
     * @return
     */
    Result associateFaultEdit(Integer id ,String faultCodes);

    /**
     * 根据id修改
     * @param dto
     * @param req
     */
    void updateByKnowledgeId(FaultKnowledgeBaseDTO dto, HttpServletRequest req);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    FaultKnowledgeBaseResult queryDetail(Long id);

    FaultKnowledgeBaseResult getResultById(String id);
}
