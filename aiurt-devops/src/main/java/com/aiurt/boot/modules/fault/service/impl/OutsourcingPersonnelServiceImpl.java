package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.fault.entity.OutsourcingPersonnel;
import com.swsc.copsms.modules.fault.mapper.OutsourcingPersonnelMapper;
import com.swsc.copsms.modules.fault.param.OutsourcingPersonnelParam;
import com.swsc.copsms.modules.fault.service.IOutsourcingPersonnelService;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.Patrol;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
@Service
public class OutsourcingPersonnelServiceImpl extends ServiceImpl<OutsourcingPersonnelMapper, OutsourcingPersonnel> implements IOutsourcingPersonnelService {

    @Resource
    private OutsourcingPersonnelMapper personnelMapper;

    @Resource
    private SysUserMapper userMapper;

    /**
     * 新增委外人员
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(OutsourcingPersonnel personnel, HttpServletRequest req) {
        OutsourcingPersonnel outsourcingPersonnel = new OutsourcingPersonnel();
        if (personnel.getName() == null || "".equals(personnel.getName())) {
            throw new SwscException("人员名称不能为空");
        }
        outsourcingPersonnel.setName(personnel.getName());

        if (personnel.getCompany() == null || "".equals(personnel.getCompany())) {
            throw new SwscException("所属单位不能为空");
        }
        outsourcingPersonnel.setCompany(personnel.getCompany());

        if (personnel.getPosition() == null || "".equals(personnel.getPosition())) {
            throw new SwscException("职位名称不能为空");
        }
        outsourcingPersonnel.setPosition(personnel.getPosition());

        if (personnel.getSystemCode() == null || "".equals(personnel.getSystemCode())) {
            throw new SwscException("所属专业系统不能为空");
        }
        outsourcingPersonnel.setSystemCode(personnel.getSystemCode());

        outsourcingPersonnel.setConnectionWay(personnel.getConnectionWay());
        outsourcingPersonnel.setDelFlag(0);
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
        outsourcingPersonnel.setCreateBy(id);
        if (personnel.getUpdateBy()!= null) {
            outsourcingPersonnel.setUpdateBy(personnel.getUpdateBy());
        }
        outsourcingPersonnel.setCreateTime(new Date());
        outsourcingPersonnel.setUpdateTime(new Date());
        personnelMapper.insert(outsourcingPersonnel);
        return Result.ok("新增成功");
    }

    /**
     * 查询委外人员
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    @Override
    public IPage<OutsourcingPersonnel> pageList(IPage<OutsourcingPersonnel> page, Wrapper<OutsourcingPersonnel> queryWrapper, OutsourcingPersonnelParam param) {
        IPage<OutsourcingPersonnel> iPage = personnelMapper.queryOutsourcingPersonnel(page, queryWrapper, param);
        return iPage;
    }

    /**
     * 根据id假删除
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        personnelMapper.deleteOne(id);
    }

    @Override
    public List<OutsourcingPersonnel> queryAll() {
        return personnelMapper.selectAll();
    }
}
