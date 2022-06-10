package com.aiurt.boot.modules.worklog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.result.WorkLogResult;
import com.swsc.copsms.common.system.api.ISysBaseAPI;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.system.vo.LoginUser;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.mapper.FaultMapper;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskMapper;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import com.swsc.copsms.modules.worklog.dto.WorkLogDTO;
import com.swsc.copsms.modules.worklog.entity.WorkLog;
import com.swsc.copsms.modules.worklog.entity.workLogEnclosure;
import com.swsc.copsms.modules.worklog.mapper.WorkLogMapper;
import com.swsc.copsms.modules.worklog.mapper.workLogEnclosureMapper;
import com.swsc.copsms.modules.worklog.param.WorkLogParam;
import com.swsc.copsms.modules.worklog.service.IWorkLogService;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class WorkLogServiceImpl extends ServiceImpl<WorkLogMapper, WorkLog> implements IWorkLogService {

    @Resource
    private WorkLogMapper depotMapper;

    @Resource
    private workLogEnclosureMapper enclosureMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private FaultMapper faultMapper;

    @Resource
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(WorkLogDTO dto, HttpServletRequest req) {
        WorkLog depot = new WorkLog();
        if (dto.getPatrolCode()==null || "".equals(dto.getPatrolCode())) {
            throw new SwscException("巡检编号不能为空");
        }
        depot.setPatrolCode(dto.getPatrolCode());

        if (dto.getRepairCode() == null || "".equals(dto.getRepairCode())) {
            throw new SwscException("检修编号不能为空");
        }
        depot.setRepairCode(dto.getRepairCode());

        if (dto.getFaultCode() == null || "".equals(dto.getFaultCode())) {
            throw new SwscException("故障编号不能为空");
        }
        depot.setFaultCode(dto.getRepairCode());

        depot.setStatus(1);
        depot.setConfirmStatus(0);
        depot.setCheckStatus(0);

        // 解密获得username，用于和数据库进行对比
        String token = TokenUtils.getTokenByRequest(req);

        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }
        // 查询用户信息
        SysUser name = userMapper.getUserByName(username);
        if (name==null){
            throw new AuthenticationException("用户不存在!");
        }
        String id = name.getId();
        depot.setSubmitId(id);

        if (dto.getSubmitTime() == null){
            throw new SwscException("提交时间不能为空");
        }
        depot.setSubmitTime(dto.getSubmitTime());

        if (dto.getWorkContent() == null || "".equals(dto.getWorkContent())) {
            throw new SwscException("工作内容不能为空");
        }
        depot.setWorkContent(dto.getWorkContent());

        if (dto.getContent() != null) {
            depot.setContent(dto.getContent());
        }

        if (dto.getSucceedId() != null) {
            depot.setSucceedId(dto.getSucceedId());
        }

        if (dto.getApproverId() != null) {
            depot.setApproverId(dto.getApproverId());
        }

        if (dto.getApproverId()!= null) {
            depot.setApprovalTime(new Date());
        }else {
            depot.setApprovalTime(null);
        }

        if (dto.getLogTime() == null) {
            throw new SwscException("日期不能为空");
        }
        depot.setLogTime(dto.getLogTime());

        depot.setSubmitTime(new Date());

        depot.setDelFlag(0);

        depot.setCreateBy(id);
        if (dto.getUpdateBy()!=null){
            depot.setUpdateBy(dto.getUpdateBy());
        }
        depot.setCreateTime(new Date());
        depot.setUpdateTime(new Date());
        depotMapper.insert(depot);
        if (dto.getUrlList()!=null) {
            workLogEnclosure enclosure = new workLogEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setCreateBy(depot.getCreateBy());
                if (depot.getUpdateBy()!= null ) {
                    enclosure.setUpdateBy(depot.getUpdateBy());
                }
                enclosure.setParentId(depot.getId());
                enclosure.setUrl(s);
                enclosure.setDelFlag(0);
                enclosure.setCreateTime(new Date());
                enclosure.setUpdateTime(new Date());
                enclosureMapper.insert(enclosure);
            }
        }
        return Result.ok("新增成功");
    }

    @Override
    public IPage<WorkLogResult> pageList(IPage<WorkLogResult> page, Wrapper<WorkLogResult> queryWrapper, WorkLogParam param) {
        IPage<WorkLogResult> result = depotMapper.queryWorkLog(page, queryWrapper, param);
        List<WorkLogResult> records = result.getRecords();
        for (WorkLogResult record : records) {
            SysUser userById = sysUserMapper.getUserById(record.getSucceedId());
            record.setSucceedName(userById.getRealname());
            SysUser userById1 = sysUserMapper.getUserById(record.getSubmitId());
            record.setSubmitName(userById1.getRealname());
            Fault fault = faultMapper.selectByCode(record.getFaultCode());
            record.setFaultDesc(fault.getFaultPhenomenon());
            if (record.getStatus() == 0) {
                record.setStatusDesc("未提交");
            }else {
                record.setStatusDesc("已提交");
            }
            if (record.getConfirmStatus() == 0) {
                record.setConfirmStatusDesc("未确认");
            }else {
                record.setConfirmStatusDesc("已确认");
            }
            if (record.getCheckStatus() == 0) {
                record.setCheckStatusDesc("未审核");
            }
        }
        return result;
    }

    /**
     * 根据id假删除
     * @param id
     */
    @Override
    public Result deleteById(Integer id) {
        depotMapper.deleteOne(id);
        return Result.ok();
    }

    /**
     * 查询日志详情
     * @param id
     * @return
     */
    @Override
    public WorkLogResult getDetailById(Integer id) {
        WorkLogResult workLog = depotMapper.selectById(id);
        //附件列表
        List<String> query = enclosureMapper.query(id);
        workLog.setUrlList(query);
        return workLog;
    }

    /**
     * 通过id确认
     * @param id
     * @return
     */
    @Override
    public Result confirm(Integer id) {
        depotMapper.confirm(id);
        return Result.ok();
    }

    /**
     * 批量确认
     * @param ids
     * @return
     */
    @Override
    public Result<?> checkByIds(String ids) {
        String[] split = ids.split(",");
        for (String s : split) {
            depotMapper.check(s);
        }
        return Result.ok();
    }
}
