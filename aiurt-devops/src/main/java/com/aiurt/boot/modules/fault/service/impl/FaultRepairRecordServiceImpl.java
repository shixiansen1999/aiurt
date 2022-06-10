package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.result.FaultRepairRecordResult;
import com.swsc.copsms.common.result.SpareResult;
import com.swsc.copsms.modules.fault.dto.FaultRepairRecordDTO;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.entity.FaultChangeSparePart;
import com.swsc.copsms.modules.fault.entity.FaultRepairRecord;
import com.swsc.copsms.modules.fault.entity.RepairRecordEnclosure;
import com.swsc.copsms.modules.fault.mapper.FaultChangeSparePartMapper;
import com.swsc.copsms.modules.fault.mapper.FaultMapper;
import com.swsc.copsms.modules.fault.mapper.FaultRepairRecordMapper;
import com.swsc.copsms.modules.fault.mapper.RepairRecordEnclosureMapper;
import com.swsc.copsms.modules.fault.service.IFaultRepairRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultRepairRecordServiceImpl extends ServiceImpl<FaultRepairRecordMapper, FaultRepairRecord> implements IFaultRepairRecordService {


    @Resource
    private FaultMapper faultMapper;

    @Resource
    private FaultRepairRecordMapper faultRepairRecordMapper;

    @Resource
    private RepairRecordEnclosureMapper repairRecordEnclosureMapper;

    @Resource
    private FaultChangeSparePartMapper partMapper;


    /**
     * 指派
     * @param code
     * @param appointUserId
     * @param workType
     * @param planOrderCode
     * @param planOrderImg
     * @return
     */
    @Override
    public Result assign(String code, String appointUserId, String workType, String planOrderCode, String planOrderImg) {
        //根据code查询故障表数据
        Fault fault = faultMapper.selectByCode(code);
        FaultRepairRecord faultRepairRecord = new FaultRepairRecord();
        faultRepairRecord.setFaultCode(code);
        faultRepairRecord.setAppointUserId(appointUserId);
        faultRepairRecord.setWorkType(workType);
        if (planOrderCode != null) {
            faultRepairRecord.setPlanOrderCode(planOrderCode);
        }
        if (planOrderImg != null) {
            faultRepairRecord.setPlanOrderImg(planOrderImg);
        }
        faultRepairRecord.setFaultPhenomenon(fault.getFaultPhenomenon());
        faultRepairRecord.setCreateTime(new Date());
        faultRepairRecord.setUpdateTime(new Date());
        faultRepairRecordMapper.insert(faultRepairRecord);
        //更改指派状态为1 已指派
        faultMapper.assign(code);
        return Result.ok();
    }

    /**
     * 重新指派
     * @param code
     * @param appointUserId
     * @param workType
     * @param planOrderCode
     * @param planOrderImg
     * @return
     */
    @Override
    public Result assignAgain(String code, String appointUserId, String workType, String planOrderCode, String planOrderImg) {
        //根据code查询故障表数据
        Fault fault = faultMapper.selectByCode(code);
        FaultRepairRecord faultRepairRecord = new FaultRepairRecord();
        faultRepairRecord.setFaultCode(code);
        faultRepairRecord.setAppointUserId(appointUserId);
        faultRepairRecord.setWorkType(workType);
        if (planOrderCode != null) {
            faultRepairRecord.setPlanOrderCode(planOrderCode);
        }
        if (planOrderImg != null) {
            faultRepairRecord.setPlanOrderImg(planOrderImg);
        }
        faultRepairRecord.setFaultPhenomenon(fault.getFaultPhenomenon());
        faultRepairRecord.setCreateTime(new Date());
        faultRepairRecord.setUpdateTime(new Date());
        faultRepairRecordMapper.insert(faultRepairRecord);
        //更改指派状态为1 已指派
        faultMapper.assignAgain(code);
        return Result.ok();
    }

    /**
     * 根据code查询故障维修记录
     * @param code
     * @return
     */
    @Override
    public List<FaultRepairRecordResult> getRepairRecord(String code) {
        List<FaultRepairRecordResult> query = faultRepairRecordMapper.queryDetail(code);
        for (FaultRepairRecordResult result : query) {
            List<String> query1 = repairRecordEnclosureMapper.queryDetail(result.getId());
            result.setUrlList(query1);
        }
        return query;
    }

    /**
     * app添加故障维修记录
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result addRecord(FaultRepairRecordDTO dto) {

        //更改故障维修记录表
        FaultRepairRecord record = new FaultRepairRecord();
        if (dto.getParticipateIds()==null || "".equals(dto.getParticipateIds())) {
            throw new SwscException("参与人不能为空");
        }
        record.setParticipateIds(dto.getParticipateIds());

        if (dto.getOutsourcingIds() != null) {
            record.setOutsourcingIds(dto.getOutsourcingIds());
        }

        if (dto.getOverTime() == null) {
            throw new SwscException("请选择维修结束时间");
        }
        record.setOverTime(dto.getOverTime());

        if (dto.getFaultAnalysis() == null || "".equals(dto.getFaultAnalysis())) {
            throw new SwscException("故障分析不能为空");
        }
        record.setFaultAnalysis(dto.getFaultAnalysis());

        if (dto.getMaintenanceMeasures() == null || "".equals(dto.getMaintenanceMeasures())) {
            throw new SwscException("维修过程不能为空");
        }
        record.setMaintenanceMeasures(dto.getMaintenanceMeasures());

        if (dto.getSolveStatus() == null) {
            throw new SwscException("请选择是否解决");
        }
        record.setSolveStatus(dto.getSolveStatus());

        record.setUpdateTime(new Date());
        record.setId(dto.getId());
        faultRepairRecordMapper.updateById(record);

        //插入附件表
        if (dto.getUrlList() != null) {
            RepairRecordEnclosure enclosure = new RepairRecordEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setRepairRecordId(record.getId());
                if (record.getCreateBy()!=null) {
                    enclosure.setUpdateBy(record.getUpdateBy());
                }
                enclosure.setType(0);
                enclosure.setUpdateBy(dto.getCreateBy());
                enclosure.setDelFlag(0);
                enclosure.setCreateTime(new Date());
                enclosure.setUpdateTime(new Date());
                enclosure.setUrl(s);
                repairRecordEnclosureMapper.insert(enclosure);
            }
        }

        //插入故障更换备件表
        if (dto.getNewSparePartCode() != null) {
            FaultChangeSparePart part = new FaultChangeSparePart();
            part.setFaultCode(dto.getFaultCode());
            part.setRepairRecordId(record.getId());
            part.setNewSparePartCode(dto.getNewSparePartCode());
            part.setNewSparePartNum(dto.getNewSparePartNum());
            part.setOldSparePartCode(dto.getOldSparePartCode());
            part.setOldSparePartNum(dto.getOldSparePartNum());
            part.setDelFlag(0);
            part.setCreateBy(dto.getCreateBy());
            if (dto.getCreateBy()!=null) {
                part.setUpdateBy(dto.getUpdateBy());
            }
            part.setCreateTime(new Date());
            part.setUpdateTime(new Date());
            partMapper.insert(part);
        }

        //插入签名
        if (dto.getSignature()!= null) {
            RepairRecordEnclosure enclosure = new RepairRecordEnclosure();
                enclosure.setRepairRecordId(record.getId());
                enclosure.setUpdateBy(dto.getUpdateBy());
                enclosure.setType(1);
                enclosure.setUpdateBy(dto.getCreateBy());
                enclosure.setDelFlag(0);
                enclosure.setCreateTime(new Date());
                enclosure.setUpdateTime(new Date());
                enclosure.setUrl(dto.getSignature());
                repairRecordEnclosureMapper.insert(enclosure);
        }
        return Result.ok("新增成功");
    }

    /**
     * 根据故障编号查询更换备件
     * @param code
     * @return
     */
    @Override
    public List<SpareResult> changeSpare(String code) {
        List<SpareResult> results = partMapper.querySpare(code);
        return results;
    }

}
