package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.result.FaultCodesResult;
import com.swsc.copsms.common.result.FaultKnowledgeBaseResult;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.fault.dto.FaultKnowledgeBaseDTO;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBase;
import com.swsc.copsms.modules.fault.entity.KnowledgeBaseEnclosure;
import com.swsc.copsms.modules.fault.mapper.FaultKnowledgeBaseMapper;
import com.swsc.copsms.modules.fault.mapper.FaultMapper;
import com.swsc.copsms.modules.fault.mapper.KnowledgeBaseEnclosureMapper;
import com.swsc.copsms.modules.fault.param.FaultKnowledgeBaseParam;
import com.swsc.copsms.modules.fault.service.IFaultKnowledgeBaseService;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseServiceImpl extends ServiceImpl<FaultKnowledgeBaseMapper, FaultKnowledgeBase> implements IFaultKnowledgeBaseService {

    @Resource
    private FaultKnowledgeBaseMapper baseMapper;

    @Resource
    private KnowledgeBaseEnclosureMapper enclosureMapper;

    @Resource
    private FaultMapper faultMapper;

    @Resource
    private SysUserMapper userMapper;

    /**
     * 查询故障知识库
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    @Override
    public IPage<FaultKnowledgeBaseResult> pageList(IPage<FaultKnowledgeBaseResult> page, Wrapper<FaultKnowledgeBaseResult> queryWrapper, FaultKnowledgeBaseParam param) {
        IPage<FaultKnowledgeBaseResult> faultKnowledgeBaseIPage = baseMapper.queryFaultKnowledgeBase(page, queryWrapper, param);
        List<FaultKnowledgeBaseResult> records = faultKnowledgeBaseIPage.getRecords();
        for (FaultKnowledgeBaseResult record : records) {
            if (record.getFaultType() == 0) {
                record.setFaultTypeDesc("设备故障");
            }else if (record.getFaultType() == 1){
                record.setFaultTypeDesc("外界妨害");
            }else {
                record.setFaultTypeDesc("其他");
            }
        }
        return faultKnowledgeBaseIPage;
    }

    /**
     * 添加故障知识库
     * @param dto
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void add(FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
        FaultKnowledgeBase base = new FaultKnowledgeBase();

        if (dto.getTypeId() == null) {
            throw new SwscException("类型id不能为空");
        }
        base.setTypeId(dto.getTypeId());

        if (dto.getFaultType() == null) {
            throw new SwscException("故障类型不能为空");
        }
        base.setFaultType(dto.getFaultType());

        if (dto.getSystemCode() == null || "".equals(dto.getSystemCode())) {
            throw new SwscException("系统名称不能为空");
        }
        base.setSystemCode(dto.getSystemCode());

        if (dto.getFaultPhenomenon() == null || "".equals(dto.getFaultPhenomenon())) {
            throw new SwscException("故障现象不能为空");
        }
        base.setFaultPhenomenon(dto.getFaultPhenomenon());

        if (dto.getFaultReason() == null || "".equals(dto.getFaultReason())) {
            throw new SwscException("故障原因不能为空");
        }
        base.setFaultReason(dto.getFaultReason());

        if (dto.getSolution() == null || "".equals(dto.getSolution())) {
            throw new SwscException("故障措施不能为空");
        }
        base.setSolution(dto.getSolution());

        base.setDelFlag(0);

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
        base.setCreateBy(id);
        if (dto.getTypeId()!=null) {
            base.setUpdateBy(dto.getUpdateBy());
        }
        base.setCreateTime(new Date());
        base.setUpdateTime(new Date());
        baseMapper.insert(base);

        if (dto.getUrlList()!= null) {
            //存储附件
            KnowledgeBaseEnclosure enclosure = new KnowledgeBaseEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setCreateBy(base.getCreateBy());
                if (base.getUpdateBy()!= null) {
                    enclosure.setUpdateBy(base.getUpdateBy());
                }
                enclosure.setKnowledgeBaseId(base.getId());
                enclosure.setDelFlag(0);
                enclosure.setUrl(s);
                enclosure.setCreateTime(new Date());
                enclosure.setUpdateTime(new Date());
                enclosureMapper.insert(enclosure);
            }
        }

    }

    /**
     * 查询关联故障列表
     * @param id
     * @return
     */
    @Override
    public Result<?> getAssociateFault(Integer id) {
        String s = baseMapper.selectCodeById(id);
        String[] split = s.split(",");
        List<FaultCodesResult> objects = new ArrayList<>();
        for (String s1 : split) {
            FaultCodesResult result = faultMapper.selectCodeDetail(s1);
            objects.add(result);
        }
        return Result.ok(objects);
    }

    /**
     * 更改关联故障
     * @param id
     * @param faultCodes
     */
    @Override
    public Result associateFaultEdit(Integer id,String faultCodes) {
        baseMapper.updateAssociateFault(id,faultCodes);
        return Result.ok();
    }

    /**
     * 根据id假删除
     * @param id
     */
    @Override
    public Result deleteById(Integer id) {
        baseMapper.deleteOne(id);
        return Result.ok();
    }

}
