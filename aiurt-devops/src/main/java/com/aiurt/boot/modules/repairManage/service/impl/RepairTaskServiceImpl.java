package com.aiurt.boot.modules.repairManage.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.constant.RepairContant;
import com.swsc.copsms.common.system.vo.LoginUser;
import com.swsc.copsms.common.util.MyBeanUtils;
import com.swsc.copsms.modules.repairManage.entity.RepairPool;
import com.swsc.copsms.modules.repairManage.entity.RepairTask;
import com.swsc.copsms.modules.repairManage.mapper.RepairPoolMapper;
import com.swsc.copsms.modules.repairManage.mapper.RepairTaskMapper;
import com.swsc.copsms.modules.repairManage.service.IRepairTaskService;
import com.swsc.copsms.modules.repairManage.vo.ReTaskDetailVO;
import com.swsc.copsms.modules.repairManage.vo.RepairPoolListVO;
import com.swsc.copsms.modules.system.entity.SysDepart;
import com.swsc.copsms.modules.system.mapper.SysDepartMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService {

    @Resource
    private RepairPoolMapper repairPoolMapper;

    @Resource
    private SysDepartMapper departMapper;

    @Autowired
    private RepairPoolServiceImpl repairPoolServiceImpl;

    @Override
    public Result confirmById(String id, Integer confirmStatus, String errorContent) {
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null){
            return Result.error("非法参数");
        }
        if (confirmStatus==1){
            repairTask.setStatus(RepairContant.REPAIRSTATUS[2]);
        }else {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[3]);
            repairTask.setErrorContent(errorContent);
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        repairTask.setConfirmUserName(Optional.ofNullable(user).map(LoginUser::getRealname).orElse("test"));
        repairTask.setConfirmTime(new Date());
        this.baseMapper.updateById(repairTask);
        return Result.ok();
    }

    @Override
    public Result checkById(String id, Integer receiptStatus, String errorContent) {
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null){
            return Result.error("非法参数");
        }
        if (receiptStatus==1){
            repairTask.setStatus(RepairContant.REPAIRSTATUS[4]);
        }else {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[5]);
            repairTask.setErrorContent(errorContent);
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        repairTask.setReceiptUserName(Optional.ofNullable(user).map(LoginUser::getRealname).orElse("test"));
        repairTask.setReceiptTime(new Date());
        this.baseMapper.updateById(repairTask);
        return Result.ok();
    }

    @Override
    public Result<ReTaskDetailVO> queryDetailById(String id) {
        Result<ReTaskDetailVO> result = new Result<>();
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null){
            return result.error500("非法参数");
        }
        String repairPoolIds = repairTask.getRepairPoolIds();
        //查询检修计划池内容
        ArrayList<RepairPoolListVO> list = new ArrayList<>();
        String[] split = repairPoolIds.split(",");
        for (String repariPoolId : split) {
            RepairPoolListVO repairPoolListVO = repairPoolMapper.selectTypeAndContentById(repariPoolId);
            list.add(repairPoolListVO);
        }

        RepairPool repairPool = repairPoolMapper.selectById(split[0]);
        //查询班组信息
        SysDepart sysDepart = departMapper.selectById(repairPool.getOrganizationId());
        ReTaskDetailVO vo = this.generateReTask(repairPool, repairTask, sysDepart.getDepartName(),list);
        result.setResult(vo);
        return result;
    }

    @Override
    public Result<ReTaskDetailVO> getDetailByUser(LoginUser user, String startTime, String endTime) {
        Result<ReTaskDetailVO> result = new Result<>();
//        if (user==null){
//            return result.error500("未授权");
//        }
        user = new LoginUser();
        user.setOrgId("050bfc214f204544a18a6bac83eb2424");
        user.setOrgName("test");
        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
        wrapper.eq("organization_id",user.getOrgId())
                .ge("start_time",startTime.concat(" 00:00:00")).le("end_time",endTime.concat(" 23:59:59")).eq("del_flag",0);
        List<RepairPool> repairPoolList = repairPoolMapper.selectList(wrapper);
        if(repairPoolList.size()==0){
            return result.success("本周无检修任务");
        }
        RepairPool repairPool = repairPoolList.get(0);
        List list = MyBeanUtils.copyList(repairPoolList, RepairPoolListVO.class);
        //未检修
        if (repairPool.getStatus() == 0){
            ReTaskDetailVO vo = new ReTaskDetailVO();
            vo.setWeeks(repairPool.getWeeks());
            vo.setTeamName(user.getOrgName());
            vo.setStatus(repairPool.getStatus());
            vo.setStartTime(repairPool.getStartTime());
            vo.setEndTime(repairPool.getEndTime());
            vo.setRepairPoolList(list);
            result.setResult(vo);
            return result;
        }
        List<Long> collect = repairPoolList.stream().map(RepairPool::getId).collect(Collectors.toList());
        String repairPoolIds = StringUtils.join(collect, ",");
        QueryWrapper<RepairTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",0).eq("repair_pool_ids",repairPoolIds);
        RepairTask repairTask = this.baseMapper.selectOne(queryWrapper);
        if (repairTask == null){
            return result.error500("异常数据");
        }
        ReTaskDetailVO vo = this.generateReTask(repairPool, repairTask, user.getOrgName(), list);
        result.setResult(vo);
        return result;
    }

    @Override
    public Result receiveByUser(LoginUser user,  String ids) {
//        if (user==null){
//            return Result.error("未授权");
//        }
        user = new LoginUser();
        user.setOrgId("050bfc214f204544a18a6bac83eb2424");
        user.setId("123");
        user.setRealname("test");
        user.setOrgName("test");
        return repairPoolServiceImpl.assigned(ids,user.getId(),user.getRealname());
    }

    /**
     * 生成检修单任务
     * @param repairPool
     * @param repairTask
     * @param departName
     * @param list
     * @return
     */
    private ReTaskDetailVO generateReTask(RepairPool repairPool, RepairTask repairTask, String departName, List<RepairPoolListVO> list){
        ReTaskDetailVO vo = new ReTaskDetailVO();
        vo.setId(repairTask.getId());
        vo.setStartTime(repairPool.getStartTime());
        vo.setEndTime(repairPool.getEndTime());
        vo.setWeeks(repairPool.getWeeks());
        vo.setStaffNames(repairTask.getStaffNames());
        vo.setSubmitTime(Optional.ofNullable(repairTask.getSubmitTime()).orElse(null));
        vo.setStatus(repairPool.getStatus());

        vo.setTeamName(departName);
        vo.setPosition(Optional.ofNullable(repairTask.getPosition()).orElse(null));
        vo.setRepairPoolList(list);

        vo.setContent(Optional.ofNullable(repairTask.getContent()).orElse(null));
        vo.setProcessContent(Optional.ofNullable(repairTask.getProcessContent()).orElse(null));
        vo.setUrl(Lists.newArrayList());
        vo.setSumitUserName(Optional.ofNullable(repairTask.getSumitUserName()).orElse(null));
        vo.setConfirmUserName(Optional.ofNullable(repairTask.getConfirmUserName()).orElse(null));
        vo.setConfirmTime(Optional.ofNullable(repairTask.getConfirmTime()).orElse(null));
        vo.setReceiptUserName(Optional.ofNullable(repairTask.getReceiptUserName()).orElse(null));
        vo.setReceiptTime(Optional.ofNullable(repairTask.getReceiptTime()).orElse(null));
        return vo;
    }
}
