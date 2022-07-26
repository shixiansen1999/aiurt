package com.aiurt.modules.train.task.service.impl;

import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.aiurt.modules.train.task.mapper.BdTrainPlanSubMapper;
import com.aiurt.modules.train.task.service.IBdTrainPlanSubService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 年子计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Service
public class BdTrainPlanSubServiceImpl extends ServiceImpl<BdTrainPlanSubMapper, BdTrainPlanSub> implements IBdTrainPlanSubService {
    @Autowired
    private BdTrainPlanSubMapper bdTrainPlanSubMapper;

    /**
     * 根据年计划删除子计划
     *
     * @param id
     */
    @Override
    public void deleteByPlanId(String id) {
        getBaseMapper().deleteByPlanId(id);
    }

    @Override
    public List<BdTrainPlanSub> getByPlanId(String id) {
        return baseMapper.getByPlanId(id);
    }

    @Override
    public Page<BdTrainPlanSub> filterPlanSub(Page<BdTrainPlanSub> pageList, BdTrainPlanSub bdTrainPlanSub) {
      /*  LoginUser loginUser = TokenUtils.getLoginUser();
        Integer teamId = loginUser.getTeamId();*/
        List<BdTrainPlanSub> list = bdTrainPlanSubMapper.getList(pageList, bdTrainPlanSub,null);
        for (BdTrainPlanSub trainPlanSub :list) {
            trainPlanSub.setState(0);
        }
        return pageList.setRecords(list);
    }
}
