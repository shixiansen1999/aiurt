package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.common.enums.WorkLogConfirmStatusEnum;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartInVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartInOrderMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartInOrderService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartInOrderServiceImpl extends ServiceImpl<SparePartInOrderMapper, SparePartInOrder> implements ISparePartInOrderService {

    @Resource
    private SparePartInOrderMapper sparePartInOrderMapper;

    @Resource
    private ISubsystemService subsystemService;

    @Resource
    private ISparePartStockService iSparePartStockService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    /**
     * 分页查询
     * @param page
     * @param sparePartInQuery
     * @return
     */
    @Override
    public IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, SparePartInQuery sparePartInQuery) {
        IPage<SparePartInVO> pageList = sparePartInOrderMapper.queryPageList(page, sparePartInQuery);
        pageList.getRecords().forEach(e->{
            if(e.getType()!=null){
                e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
            }
            e.setSystemCode(subsystemService.getOne(new QueryWrapper<Subsystem>().eq(Subsystem.SYSTEM_CODE,e.getSystemCode()),false).getSystemName());
            if (e.getConfirmStatus()!=null) {
                e.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(e.getConfirmStatus()));
            }
        });
        return pageList;
    }

    /**
     * excel导出
     * @param sparePartInQuery
     * @return
     */
    @Override
    public List<SparePartInExcel> exportXls(SparePartInQuery sparePartInQuery) {
        List<SparePartInExcel> list = sparePartInOrderMapper.exportXls(sparePartInQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
            list.get(i).setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(list.get(i).getConfirmStatus()));
        }
        return list;
    }

    /**
     * 批量确认
     * @param ids
     * @param req
     * @return
     */
    @Override
    public Result<?> confirmBatch(String ids, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();
        String[] split = ids.split(",");
        List<SparePartStock> sparePartStockList = new ArrayList<>();
        for (String s : split) {
            SparePartInOrder sparePartInOrder = this.getOne(new QueryWrapper<SparePartInOrder>().eq(SparePartInOrder.ID, s), false);
            if (sparePartInOrder.getConfirmStatus()==1) {
                throw new SwscException("编号为"+sparePartInOrder.getMaterialCode()+"的备件已存在，请重新选择！");
            }
            SparePartStock applyStock = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                        .eq(SparePartStock.ORG_ID, sparePartInOrder.getOrgId())
                        .eq(SparePartStock.MATERIAL_CODE, sparePartInOrder.getMaterialCode()), false);
            //如果备件借入表中已有该备件入库数据则增加数量  如果没有则新增一条该备件的入库记录
                if(ObjectUtil.isNotEmpty(applyStock)){
                    applyStock.setMaterialCode(sparePartInOrder.getMaterialCode());
                    applyStock.setNum(sparePartInOrder.getNum()+applyStock.getNum());
                    applyStock.setOrgId(orgId);
                    applyStock.setOrgId(sparePartInOrder.getOrgId());
                    applyStock.setUpdateBy(userId);
                    iSparePartStockService.updateById(applyStock);
                    sparePartStockList.add(applyStock);
                }else{
                    SparePartStock sparePartStock = new SparePartStock();
                    sparePartStock.setMaterialCode(sparePartInOrder.getMaterialCode());
                    sparePartStock.setNum(sparePartInOrder.getNum());
                    sparePartStock.setOrgId(orgId);
                    sparePartStock.setCreateBy(userId);
                    sparePartStock.setOrgId(sparePartInOrder.getOrgId());
                    iSparePartStockService.save(sparePartStock);
                }
            sparePartInOrderMapper.confirm(s);
        }
        return Result.ok();
    }
}
