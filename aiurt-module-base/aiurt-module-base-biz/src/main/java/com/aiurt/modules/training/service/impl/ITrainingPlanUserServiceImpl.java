package com.aiurt.modules.training.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.mapper.TrainingPlanUserMapper;
import com.aiurt.modules.training.param.PlanUserParam;
import com.aiurt.modules.training.service.ITrainingPlanUserService;
import com.aiurt.modules.training.vo.PlanUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: ITrainingPlanUserServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/28 17:52
 */
@Service
public class ITrainingPlanUserServiceImpl extends ServiceImpl<TrainingPlanUserMapper, TrainingPlanUser> implements ITrainingPlanUserService {

    @Autowired
    private TrainingPlanUserMapper trainingPlanUserMapper;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Override
    public IPage<TrainingPlanUser> listByPlanId(Page<TrainingPlanUser> page, Long planId) {

        return this.baseMapper.listByPlanId(page, planId);
    }

    @Override
    public IPage<PlanUserVO> listPlan(Page<PlanUserVO> page, PlanUserParam param) {
        if (ObjectUtil.isEmpty(param.getUserId())) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            param.setUserId(loginUser.getId());
        }
        return this.baseMapper.listPlan(page, param);
    }

    @Override
    public ModelAndView exportListPlan(PlanUserParam param) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        if (ObjectUtil.isEmpty(param.getUserId())) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            param.setUserId(loginUser.getId());
        }
        List<PlanUserVO> list = trainingPlanUserMapper.exportListPlan(param);
        //查询培训类型字典项
        List<DictModel> trainingTypes = sysBaseApi.getDictItems("training_type");

        Map<String, String> itemMap = trainingTypes.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                item.setTrainingTypeString(itemMap.get(item.getTrainingType().toString()));
                item.setStartDateString(DateUtil.format(item.getStartDate(), "yyyy-MM-dd"));
                item.setEndDateString(DateUtil.format(item.getEndDate(), "yyyy-MM-dd"));
            });
        }
        mv.addObject(NormalExcelConstants.FILE_NAME, "个人管理列表");
        mv.addObject(NormalExcelConstants.CLASS, PlanUserVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("个人管理列表", "导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }


}
