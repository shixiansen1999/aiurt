package com.aiurt.boot.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.enums.FaultTypeEnum;
import com.aiurt.boot.common.result.FaultCodesResult;
import com.aiurt.boot.common.result.FaultKnowledgeBaseResult;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.fault.dto.FaultKnowledgeBaseDTO;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBase;
import com.aiurt.boot.modules.fault.entity.KnowledgeBaseEnclosure;
import com.aiurt.boot.modules.fault.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.fault.mapper.KnowledgeBaseEnclosureMapper;
import com.aiurt.boot.modules.fault.param.FaultKnowledgeBaseParam;
import com.aiurt.boot.modules.fault.service.IFaultKnowledgeBaseService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.impl.SubsystemServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private SubsystemServiceImpl subsystemService;
    /**
     * 查询故障知识库
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<FaultKnowledgeBaseResult> pageList(IPage<FaultKnowledgeBaseResult> page,FaultKnowledgeBaseParam param) {
        IPage<FaultKnowledgeBaseResult> iPage = baseMapper.queryFaultKnowledgeBase(page, param);
        List<FaultKnowledgeBaseResult> records = iPage.getRecords();
        for (FaultKnowledgeBaseResult record : records) {
            //查询附件
            List<String> results = enclosureMapper.selectByKnowledgeId(record.getId());
            record.setUrlList(results);
            if (record.getFaultType() != null) {
                record.setFaultTypeDesc(FaultTypeEnum.findMessage(record.getFaultType()));
            }
        }
        return iPage;
    }

    /**
     * 添加故障知识库
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
        FaultKnowledgeBase base = new FaultKnowledgeBase();
        if (dto.getTypeId() != null) {
            base.setTypeId(dto.getTypeId());
        }
        if (dto.getFaultType() != null) {
            base.setFaultType(dto.getFaultType());
        }
        base.setSystemCode(dto.getSystemCode());
        base.setFaultPhenomenon(dto.getFaultPhenomenon());
        base.setFaultReason(dto.getFaultReason());
        base.setSolution(dto.getSolution());
        base.setFaultCodes(dto.getFaultCodes());
        base.setScanNum(0);
        base.setDelFlag(0);
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        base.setCreateBy(userId);
        baseMapper.insert(base);
        //存储附件
        if (CollUtil.isNotEmpty(dto.getUrlList())) {
            KnowledgeBaseEnclosure enclosure = new KnowledgeBaseEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setCreateBy(base.getCreateBy());
                enclosure.setKnowledgeBaseId(base.getId());
                enclosure.setDelFlag(0);
                enclosure.setUrl(s);
                enclosureMapper.insert(enclosure);
            }
        }
        return base.getId();
    }

    /**
     * 根据id获取关联故障
     * @param id
     * @return
     */
    @Override
    public Result<List<FaultCodesResult>> getAssociateFault(Long id) {
        List<FaultCodesResult> objects = new ArrayList<>();
        String s = baseMapper.selectCodeById(id);
        if (StringUtils.isNotBlank(s)) {
            String[] split = s.split(",");
            for (String s1 : split) {
                FaultCodesResult result = faultMapper.selectCodeDetail(s1);
                objects.add(result);
            }
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
     * 根据id修改
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByKnowledgeId(FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        FaultKnowledgeBase base = this.baseMapper.selectById(dto.getId());
        if (StringUtils.isNotBlank(dto.getSystemCode())) {
            base.setSystemCode(dto.getSystemCode());
        }
        if (StringUtils.isNotBlank(dto.getFaultPhenomenon())) {
            base.setFaultPhenomenon(dto.getFaultPhenomenon());
        }
        if (StringUtils.isNotBlank(dto.getFaultReason())) {
            base.setFaultReason(dto.getFaultReason());
        }
        if (StringUtils.isNotBlank(dto.getSolution())) {
            base.setSolution(dto.getSolution());
        }
        if (StringUtils.isNotBlank(dto.getFaultCodes())) {
            base.setFaultCodes(dto.getFaultCodes());
        }
        base.setUpdateBy(userId);
        baseMapper.updateById(base);
        //删除附件列表
        enclosureMapper.deleteByName(dto.getId());
        //重新插入附件列表
        List<String> urlList = dto.getUrlList();
        if (CollUtil.isNotEmpty(urlList)) {
            for (String s : urlList) {
                KnowledgeBaseEnclosure enclosure = new KnowledgeBaseEnclosure();
                enclosure.setKnowledgeBaseId(dto.getId());
                enclosure.setUrl(s);
                enclosure.setDelFlag(0);
                enclosureMapper.insert(enclosure);
            }
        }
    }

    /**
     * 根据id查询故障知识详情
     * @param id
     * @return
     */
    @Override
    public FaultKnowledgeBaseResult queryDetail(Long id) {
        FaultKnowledgeBaseResult result = baseMapper.selectByKnowledgeId(id);
        //app浏览增加一次浏览次数
        Integer scanNum = result.getScanNum();
        baseMapper.updateScanNum(++scanNum,id);
        //查询附加列表
        List<String> results = enclosureMapper.selectByKnowledgeId(id);
        result.setUrlList(results);
        //获取关联故障
        Result<List<FaultCodesResult>> associateFault = getAssociateFault(id);
        List<FaultCodesResult> result1 = associateFault.getResult();
        result.setFaultCodesResults(result1);
        return result;
    }

    @Override
    public FaultKnowledgeBaseResult getResultById(String id) {
        FaultKnowledgeBase faultKnowledgeBase = this.getById(id);
        FaultKnowledgeBaseResult result = new FaultKnowledgeBaseResult();
        if (ObjectUtil.isEmpty(result)){
            return null;
        }
        BeanUtils.copyProperties(faultKnowledgeBase,result);
        if (StringUtils.isNotBlank(result.getSystemCode())){
            Subsystem subsystem = subsystemService.getOne(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getSystemCode, result.getSystemCode()));
            result.setSystemName(subsystem.getSystemName());
        }
        List<String> results = enclosureMapper.selectByKnowledgeId(result.getId());
        result.setUrlList(results);
        if (result.getFaultType() != null) {
            result.setFaultTypeDesc(FaultTypeEnum.findMessage(result.getFaultType()));
        }
        return result;
    }
}
