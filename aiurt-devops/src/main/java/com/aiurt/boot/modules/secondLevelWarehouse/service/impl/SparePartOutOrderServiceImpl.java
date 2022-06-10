package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.FaultTypeEnum;
import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.common.enums.ProductiveTypeEnum;
import com.aiurt.boot.common.result.FaultResult;
import com.aiurt.boot.common.result.FaultSparePartResult;
import com.aiurt.boot.common.result.SparePartResult;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.mapper.DeviceMapper;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartOutOrderMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartScrapMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartOutOrderService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Resource
    private ISparePartStockService iSparePartStockService;
    @Resource
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Resource
    @Lazy
    private ISparePartOutOrderService iSparePartOutOrderService;
    @Resource
    private IFaultService iFaultService;
    @Resource
    private SparePartScrapMapper sparePartScrapMapper;
    @Resource
    private DeviceChangeSparePartMapper deviceChangeSparePartMapper;
    @Resource
    private ISysBaseAPI iSysBaseAPI;
    @Resource
    private ISubsystemService subsystemService;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ISparePartStockService sparePartStockService;
    @Resource
    private ISysUserService sysUserService;

    @Override
    public IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery) {
        IPage<SparePartOutVO> pageList = sparePartOutOrderMapper.queryPageList(page, sparePartLendQuery);
        pageList.getRecords().forEach(e -> {
            if (e.getType() != null) {
                e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
            }
            if (StringUtils.isNotBlank(e.getSystem())) {
                e.setSystem(subsystemService.getOne(new QueryWrapper<Subsystem>().eq(Subsystem.SYSTEM_CODE,e.getSystem()),false).getSystemName());
            }
        });
        return pageList;
    }

    /**
     * 备件出库表-添加
     * @param result
     * @param sparePartOutOrder
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addOutOrder(Result<?> result, SparePartOutOrder sparePartOutOrder, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();
        if (sparePartOutOrder.getNum() == null) {
            return result.error500("出库数量不能为空");
        }
        //判断库存够不够
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, orgId)
                .eq(SparePartStock.MATERIAL_CODE, sparePartOutOrder.getMaterialCode()), false);
        if (ObjectUtil.isNotEmpty(one)) {
            if (one.getNum() < sparePartOutOrder.getNum()) {
                return result.error500("备件：" + sparePartOutOrder.getMaterialCode() + " 库存不足");
            }
        } else {
            return result.error500("备件：" + sparePartOutOrder.getMaterialCode() + " 备件库存中没有该物资");
        }
        //新增出库信息
        sparePartOutOrder.setCreateBy(userId);
        sparePartOutOrder.setOrgId(orgId);
        sparePartOutOrder.setRemarks(sparePartOutOrder.getRemarks());
        sparePartOutOrder.setUpdateBy(userId);
        sparePartOutOrder.setOutTime(sparePartOutOrder.getOutTime());
        iSparePartOutOrderService.save(sparePartOutOrder);
        //库存减少
        one.setNum(one.getNum() - sparePartOutOrder.getNum());
        one.setUpdateBy(userId);
        iSparePartStockService.updateById(one);
        return result.success("添加成功");
    }

    /**
     * 备件出库信息导出
     * @param sparePartLendQuery
     * @return
     */
    @Override
    public List<SparePartOutExcel> exportXls(SparePartLendQuery sparePartLendQuery) {
        List<SparePartOutExcel> list = sparePartOutOrderMapper.exportXls(sparePartLendQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            if (list.get(i).getType() != null) {
                list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
            }
            if (StringUtils.isNotBlank(list.get(i).getSystem())){
                list.get(i).setSystem(subsystemService.getOne(new QueryWrapper<Subsystem>().eq("system_code",list.get(i).getSystem()),false).getSystemName());
            }
        }
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addByFault(Result<?> result, Long id) {
        //备件报损暂不调用备件出库
        DeviceChangeSparePart faultChangeSparePart = deviceChangeSparePartMapper.selectById(id);
        if (ObjectUtil.isEmpty(faultChangeSparePart)) {
            return result.error500("备件--->接收故障更换备件表id为空");
        }
        if (faultChangeSparePart.getOldSparePartNum() == null) {
            return result.error500("出库数量不能为空");
        }
        //根据编号查看详情
        FaultResult faultDetail = iFaultService.getFaultDetail(faultChangeSparePart.getCode());

        //新备件到备件出库需要插入的数据
        SparePartOutOrder outOrder = new SparePartOutOrder();
        outOrder.setMaterialCode(faultChangeSparePart.getOldSparePartCode());
        outOrder.setNum(faultChangeSparePart.getOldSparePartNum());
        SparePartStock sparePartStock = sparePartStockService.getOne(new QueryWrapper<SparePartStock>().eq("material_code", faultChangeSparePart.getOldSparePartCode()), false);
        outOrder.setOrgId(sparePartStock.getOrgId());
        outOrder.setCreateBy(faultChangeSparePart.getCreateBy());
        outOrder.setOutTime(new Date());
        outOrder.setUpdateBy(faultChangeSparePart.getUpdateBy());
        outOrder.setFaultChangeSparePartId(faultChangeSparePart.getId());
        //新增出库数据
        sparePartOutOrderMapper.insert(outOrder);

        //备件库存数据
        //判断库存够不够
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq(SparePartStock.ORG_ID, outOrder.getOrgId())
                .eq(SparePartStock.MATERIAL_CODE, outOrder.getMaterialCode()), false);
        if (ObjectUtil.isNotEmpty(one)) {
            if (one.getNum() < outOrder.getNum()) {
                return result.error500("备件：" + outOrder.getMaterialCode() + " 库存不足");
            }
        } else {
            return result.error500("备件：" + outOrder.getMaterialCode() + " 备件库存中没有该物资");
        }
        //库存减少
        one.setNum(one.getNum() - outOrder.getNum());
        //更新库存数据
        iSparePartStockService.updateById(one);
        faultChangeSparePart.setNewOrgId(outOrder.getOrgId());
        faultChangeSparePart.setNewSparePartNum(outOrder.getNum());
        faultChangeSparePart.setNewSparePartCode(outOrder.getMaterialCode());
        deviceChangeSparePartMapper.updateById(faultChangeSparePart);

        //旧备件到备件报损需要插入的数据
        SparePartScrap scrap = new SparePartScrap();
        scrap.setMaterialCode(faultChangeSparePart.getOldSparePartCode());
        scrap.setNum(faultChangeSparePart.getOldSparePartNum());
//        scrap.setwa(faultChangeSparePart.get());
        scrap.setReason(faultDetail.getRemark());
        scrap.setLineCode(faultDetail.getLineCode());
        scrap.setStationCode(faultDetail.getStationCode());
        scrap.setCreateBy(faultDetail.getCreateBy());
        scrap.setUpdateBy(faultDetail.getUpdateBy());
        //现在还不知道要不要关联报修单
//            scrap.setRelateFaultCode(spareByRepair.getFaultCode());

        //新增备件报损数据
        sparePartScrapMapper.insert(scrap);
        return result.success("成功");

    }

    /**
     * 履历-查询故障更换备件信息
     * @param id
     * @return
     */
    @Override
    public Result<List<SparePartResult>> selectFaultChangePart(Long id) {
        List<SparePartResult> sparePartResults = sparePartOutOrderMapper.selectByFaultChangeSparePartId(id);
        for (int i = 0; i < sparePartResults.size(); i++) {
            sparePartResults.get(i).setSerialNumber(i+1);
            if (sparePartResults.get(i).getOldSparePartType()!=null) {
                sparePartResults.get(i).setOldSparePartTypeDesc(ProductiveTypeEnum.findMessage(sparePartResults.get(i).getOldSparePartType()));
            }
            if (sparePartResults.get(i).getNewSparePartType()!=null) {
                sparePartResults.get(i).setNewSparePartTypeDesc(ProductiveTypeEnum.findMessage(sparePartResults.get(i).getNewSparePartType()));
            }
        }
        return Result.ok(sparePartResults);
    }

    /**
     * 履历-查询故障信息
     * @param id
     * @return
     */
    @Override
    public Result<List<FaultSparePartResult>> selectFaultDetail(Long id) {
        List<FaultSparePartResult> faultDetail = sparePartOutOrderMapper.getFaultDetail(id);
        for (int i = 0; i < faultDetail.size(); i++) {
            faultDetail.get(i).setSerialNumber(i+1);
            faultDetail.get(i).setFaultTypeDesc(FaultTypeEnum.findMessage(faultDetail.get(i).getFaultType()));
            if (StringUtils.isNotBlank(faultDetail.get(i).getDevice())) {
                List<String> name = new ArrayList<String>();
                String[] split = faultDetail.get(i).getDevice().split(",");
                List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<Device>()
                        .in(Device::getCode, split)
                        .eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .select(Device::getName));
                for (Device device : devices) {
                    name.add(device.getName());
                }
                String join = StringUtils.join(name, ",");
                faultDetail.get(i).setDeviceName(join);
            }
        }
        return Result.ok(faultDetail);
    }
}
