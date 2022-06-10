package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.common.enums.ScrapStatusEnum;
import com.aiurt.boot.common.enums.SpareScrapStatusEnums;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.ReportRepairResult;
import com.aiurt.boot.common.result.ReportWasteResult;
import com.aiurt.boot.common.result.ScrapReportResult;
import com.aiurt.boot.common.result.SpareConsumeNum;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportRepairDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportWasteDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartScrapMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartScrapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {

    @Resource
    private SparePartScrapMapper sparePartScrapMapper;

    /**
     * 分页查询
     * @param page
     * @param sparePartScrapQuery
     * @return
     */
    @Override
    public IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery) {
        IPage<SparePartScrapVO> list = sparePartScrapMapper.queryPageList(page, sparePartScrapQuery);
        list.getRecords().forEach(e->{
            if(e.getType()!=null){
                e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
            }
            if (e.getStatus()!=null) {
                e.setStatusDesc(ScrapStatusEnum.findMessage(e.getStatus()));
            }
        });
        return list;
    }

    /**
     * 备件报损信息导出
     * @param sparePartScrapQuery
     * @return
     */
    @Override
    public List<SparePartScrapExcel> exportXls(SparePartScrapQuery sparePartScrapQuery) {
        List<SparePartScrapExcel> list = sparePartScrapMapper.exportXls(sparePartScrapQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            if(list.get(i).getType()!=null){
                list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
            }
            if(list.get(i).getStatus()!=null){
                list.get(i).setStatusDesc(SpareScrapStatusEnums.getNameByCode(list.get(i).getStatus()));
            }
        }
        return list;
    }



    /**
     * 报修
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result reportRepair(ReportRepairDTO dto) {
        SparePartScrap sparePartScrap = sparePartScrapMapper.selectById(dto.getId());
        sparePartScrap.setKeepPerson(dto.getKeepPerson());
        sparePartScrap.setScrapReason(dto.getScrapReason());
        if (dto.getRepairTime()==null) {
            throw new SwscException("送修时间不能为空");
        }
        sparePartScrap.setRepairTime(dto.getRepairTime());
        sparePartScrap.setScrapDepart(dto.getScrapDepart());
        //修改状态为报修
        sparePartScrap.setStatus(1);
        sparePartScrapMapper.updateById(sparePartScrap);
        return Result.ok();
    }

    /**
     * 报废
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result reportWaste(ReportWasteDTO dto) {
        SparePartScrap sparePartScrap = sparePartScrapMapper.selectById(dto.getId());
        sparePartScrap.setScrapReason(dto.getScrapReason());
        sparePartScrap.setUseLife(dto.getUseLife());
        sparePartScrap.setServiceLife(dto.getServiceLife());
        sparePartScrap.setRepairTime(new Date());
        sparePartScrap.setBuyTime(dto.getBuyTime());
        sparePartScrapMapper.updateById(sparePartScrap);
        //修改状态为报废
        sparePartScrap.setStatus(2);
        sparePartScrapMapper.updateById(sparePartScrap);
        return Result.ok();
    }

    /**
     * 根据id查询报损详情
     * @param id
     * @return
     */
    @Override
    public Result<ScrapReportResult> getDetailById(String id) {
        Result<ScrapReportResult> result = new Result<>();
        ScrapReportResult scrapReportResult = sparePartScrapMapper.selectDetailById(id);
        scrapReportResult.setMaterialTypeDesc(MaterialTypeEnum.getNameByCode(scrapReportResult.getMaterialType()));
        result.setResult(scrapReportResult);
        return result;
    }

    /**
     * 获取送修详情
     * @param id
     * @return
     */
    @Override
    public Result<ReportRepairResult> getRepairDetailById(String id) {
        Result<ReportRepairResult> result = new Result<>();
        ReportRepairResult reportRepairResult = sparePartScrapMapper.selectRepairDetailById(id);
        result.setResult(reportRepairResult);
        return result;
    }

    /**
     * 获取报废详情
     * @param id
     * @return
     */
    @Override
    public Result<ReportWasteResult> getWasteDetailById(String id) {
        Result<ReportWasteResult> result = new Result<>();
        ReportWasteResult reportWasteResult = sparePartScrapMapper.selectWasteDetailById(id);
        result.setResult(reportWasteResult);
        return result;
    }

    /**
     * @Description: 获取消耗数
     * @Return:
     */
    @Override
    public Integer getConsumeNum(Map map) {
        Date startTime = DateUtils.getYearStartTime(DateUtils.getDate());
        Date endTime = DateUtils.getYearEndTime(DateUtils.getDate());
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        return this.baseMapper.getCountConsumeNum(map);
    }

    @Override
    public List<SpareConsumeNum> getSpareConsumeNumByTime(Map map) {
        Date startTime = DateUtils.getYearStartTime(DateUtils.getDate());
        Date endTime = DateUtils.getYearEndTime(DateUtils.getDate());
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        return this.baseMapper.selectSpareConsumeNumByTime(map);
    }
}
