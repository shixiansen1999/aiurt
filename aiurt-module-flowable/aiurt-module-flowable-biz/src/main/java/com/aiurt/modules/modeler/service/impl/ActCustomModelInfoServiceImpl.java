package com.aiurt.modules.modeler.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.enums.ModelFormStatusEnum;
import com.aiurt.modules.modeler.mapper.ActCustomModelInfoMapper;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;


/**
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class ActCustomModelInfoServiceImpl extends ServiceImpl<ActCustomModelInfoMapper, ActCustomModelInfo> implements IActCustomModelInfoService {

    /**
     * 内置的流程变量 startUserName
     */
    private static final String  STAR_USER_NAME = "startUserName";

    /**
     * 内置的流程变量 operationType
     */
    private static final String OPERATION_TYPE = "operationType";

    @Lazy
    @Autowired
    private IFlowableBpmnService flowableBpmnService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private IActCustomVariableService customVariableService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 添加模板
     * @param actCustomModelInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActCustomModelInfo add(ActCustomModelInfo actCustomModelInfo) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }
        Model model = flowableBpmnService.createInitBpmn(actCustomModelInfo, user);
        actCustomModelInfo.setModelId(model.getId());
        actCustomModelInfo.setStatus(ModelFormStatusEnum.CG.getStatus());
        saveOrUpdate(actCustomModelInfo);

        // 初始化变量
        if (Objects.nonNull(model)) {
            ActCustomVariable actCustomVariable = new ActCustomVariable();
            actCustomVariable.setVariableName(OPERATION_TYPE);
            actCustomVariable.setShowName("审批类型");
            actCustomVariable.setVariableType(1);
            actCustomVariable.setModelId(model.getId());
            actCustomVariable.setType("1");
            customVariableService.save(actCustomVariable);

            actCustomVariable = new ActCustomVariable();
            actCustomVariable.setVariableName(STAR_USER_NAME);
            actCustomVariable.setShowName("流程启动用户");
            actCustomVariable.setVariableType(1);
            actCustomVariable.setModelId(model.getId());
            actCustomVariable.setType("1");
            customVariableService.save(actCustomVariable);
        }

        return actCustomModelInfo;
    }

    /**
     * 删除
     * @param idList
     */
    @Override
    public void deleteById(List<String> idList) {
        if (CollectionUtils.isNotEmpty(idList)) {
            String id = idList.get(0);
            ActCustomModelInfo modelInfo = this.getById(id);
            String modelKey = modelInfo.getModelKey();

            // 同时删除流程定义信息
            if (modelInfo.getStatus().equals(ModelFormStatusEnum.CG.getStatus())) {
                this.removeById(id);
                String modelId = modelInfo.getModelId();
               // repositoryService.getBpmnModel(modelId);
                modelService.deleteModel(modelId);
            } else {
                throw new AiurtBootException("模型不是草稿状态，请勿删除！");
            }
        }
    }

    /**
     * 根据modelId获取流程流程模板信息
     * @param modelId
     * @return
     */
    @Override
    public ActCustomModelInfo queryByModelId(String modelId) {
        LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomModelInfo::getModelId, modelId).last("limit 1");
        ActCustomModelInfo actCustomModelInfo = baseMapper.selectOne(wrapper);
        Model model = modelService.getModel(modelId);
        byte[] bpmnXml = modelService.getBpmnXML(model);
        String streamStr = null;
        try {
            streamStr = new String(bpmnXml, "UTF-8");
        } catch (UnsupportedEncodingException e) {
           log.error(e.getMessage());
        }
        return actCustomModelInfo;
    }

    /**
     * 流程信息编辑
     *
     * @param actCustomModelInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(ActCustomModelInfo actCustomModelInfo) {
        ActCustomModelInfo one = this.getById(actCustomModelInfo.getId());
        if (Objects.isNull(one) || StrUtil.isBlank(one.getModelId())) {
            throw new AiurtBootException("流程模板不存在，无法修改。");
        }

        Model model = modelService.getModel(one.getModelId());
        if (Objects.isNull(model)) {
            throw new AiurtBootException("流程模板不存在，无法修改。");
        }



        if (!StrUtil.equals(one.getModelKey(), actCustomModelInfo.getModelKey())) {
            throw new AiurtBootException("流程标识不能修改！");
        }
        this.updateById(actCustomModelInfo);

        model.setName(one.getName());
        modelService.saveModel(model);
    }


    /**
     * 根据modelKey查询模板信息
     *
     * @param modelKey
     * @return
     */
    @Override
    public ActCustomModelInfo queryByModelKey(String modelKey) {
        LambdaQueryWrapper<ActCustomModelInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomModelInfo::getModelKey, modelKey).last("limit 1");
        ActCustomModelInfo actCustomModelInfo = baseMapper.selectOne(wrapper);
        return actCustomModelInfo;
    }
}
