package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.enums.MaterialApplyStatusEnum;
import com.aiurt.common.util.PageLimitUtil;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.entity.dto.AddApplyDTO;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.sparepart.entity.dto.StockOutDTO;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMapper;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.jeewx.api.core.util.DateUtils.datetimeFormat;


/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartApplyServiceImpl extends ServiceImpl<SparePartApplyMapper, SparePartApply> implements ISparePartApplyService {

    @Resource
    private SparePartApplyMapper sparePartApplyMapper;
    @Resource @Lazy
    private ISparePartApplyService iSparePartApplyService;
    @Resource
    private ISparePartApplyMaterialService iSparePartApplyMaterialService;
    @Resource
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    /*    @Resource
      private IStockLevel2InfoService iStockLevel2InfoService;*/
    @Resource
    private ISparePartStockInfoService iSparePartStockInfoService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private IMaterialBaseService iMaterialBaseService;
    /* @Resource
     private IStockLevel2Service iStockLevel2Service;*/
    @Resource
    private ISparePartStockService iSparePartStockService;
    @Resource
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Override
    public PageLimitUtil<SparePartApply> queryPageList(SparePartApply sparePartApply, Integer pageNo, Integer pageSize,
                                                       String startTime, String endTime,HttpServletRequest req) throws ParseException {
        QueryWrapper<SparePartApply> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
/*            Date startDate = datetimeFormat.parse(startTime);
            Date endData = datetimeFormat.parse(endTime);
            queryWrapper.between(startTime != null && endTime != null, "create_time", startDate, endData);*/
        }
        queryWrapper.eq(sparePartApply.getCode()!=null,"code",sparePartApply.getCode());

        List<SparePartApply> applyList = sparePartApplyMapper.selectList(queryWrapper);
        applyList.forEach(e->{
            SparePartStockInfo sparePartStockInfo = iSparePartStockInfoService.getOne(new QueryWrapper<SparePartStockInfo>()
                    .eq("warehouse_code", e.getWarehouseCode()), false);

            if(ObjectUtil.isNotEmpty(sparePartStockInfo)){
                e.setWarehouseDepartment(sparePartStockInfo.getOrganizationId()+"");
            }

            List<SparePartApplyMaterial> applyMaterials = sparePartApplyMaterialMapper.selectList(
                    new QueryWrapper<SparePartApplyMaterial>().eq("apply_code", e.getCode()));
            if(CollUtil.isNotEmpty(applyMaterials)){
                /*Integer typeByMaterialCode =
                        iMaterialBaseService.getTypeByMaterialCode(applyMaterials.get(0).getMaterialCode());
                if(typeByMaterialCode!=null){
                    e.setMaterialType(typeByMaterialCode);
                    e.setMaterialTypeName(MaterialTypeEnum.getNameByCode(typeByMaterialCode));
                }*/

            }
            int applyNum =
                    applyMaterials.stream().mapToInt(SparePartApplyMaterial::getApplyNum).sum();
            e.setApplyAllNum(applyNum);
        });
        //备件类型筛选
        if(sparePartApply.getMaterialType()!=null){
            applyList = applyList.stream().filter(e -> e.getMaterialType().equals(sparePartApply.getMaterialType())).collect(Collectors.toList());
        }
        PageLimitUtil<SparePartApply> pageLimitUtil = new PageLimitUtil<>(pageNo, pageSize, true, applyList);
        return pageLimitUtil;

    }

    @Override
    public List<StockApplyExcel> exportXls(List<Integer> ids) {
        List<StockApplyExcel> excelList = sparePartApplyMapper.selectExportXls(ids);
        AtomicReference<Integer> flag= new AtomicReference<>(1);
        excelList.forEach(e->{
            e.setSerialNumber(flag.getAndSet(flag.get() + 1));
            List<SparePartApplyMaterial> applyMaterials = sparePartApplyMaterialMapper.selectList(
                    new QueryWrapper<SparePartApplyMaterial>().eq("apply_code", e.getCode()));
            int applyNum =
                    applyMaterials.stream().mapToInt(SparePartApplyMaterial::getApplyNum).sum();
            e.setApplyAllNum(applyNum);

            //去所属的备件申领单详情里面找备件类型
            if(CollUtil.isNotEmpty(applyMaterials)){
              /*  Integer typeByMaterialCode =
                        iMaterialBaseService.getTypeByMaterialCode(applyMaterials.get(0).getMaterialCode());
                if(typeByMaterialCode!=null){
                    e.setMaterialTypeName(MaterialTypeEnum.getNameByCode(typeByMaterialCode));
                }*/
            }
            e.setStatusName(MaterialApplyStatusEnum.getNameByCode(e.getStatus()));

            String format = datetimeFormat.format(e.getApplyTime());
            e.setApplyTimeString(format);
        });
        return excelList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean stockOutConfirm(StockOutDTO stockOutDTOList) {
        //改表spare_part_apply,备件申领表的状态改为已审核
        SparePartApply apply = sparePartApplyMapper.selectById(stockOutDTOList.getId());
        apply.setId(stockOutDTOList.getId());
        apply.setRemarks(stockOutDTOList.getRemarks());
//        apply.setUpdateBy(stockOutDTOList.getOperatorId());
        apply.setStatus(MaterialApplyStatusEnum.CHECKED.getCode());


        List<SparePartApplyMaterial> applyMaterialList = new ArrayList<>();
        List<?> stockLevel2List = new ArrayList<>();
        //List<StockLevel2> stockLevel2List = new ArrayList<>();
        List<SparePartStock> sparePartStockList = new ArrayList<>();
        if(CollUtil.isNotEmpty(stockOutDTOList.getMaterialVOList())){
            stockOutDTOList.getMaterialVOList().forEach(e->{
                //备件申领物资表spare_part_apply_material设定实际出库量
                SparePartApplyMaterial applyMaterial = new SparePartApplyMaterial();
                applyMaterial.setId(e.getId());
                applyMaterial.setActualNum(e.getMaterialNum());
//                applyMaterial.setUpdateBy(stockOutDTOList.getOperatorId());
                applyMaterialList.add(applyMaterial);


                //二级库库存，出库(这里找apply.getOutWarehouseCode())
                /*StockLevel2 outStock = iStockLevel2Service.getOne(
                        new QueryWrapper<StockLevel2>().eq("material_code", e.getMaterialCode())
                        .eq("warehouse_code", apply.getOutWarehouseCode()), false);
                if (ObjectUtil.isNotEmpty(outStock)){
                    outStock.setNum(outStock.getNum()-applyMaterial.getActualNum());

                    stockLevel2List.add(outStock);
                }*/

                //备件,入库 (apply.getWarehouseCode())
                SparePartInOrder sparePartInOrder = new SparePartInOrder();
                sparePartInOrder.setMaterialCode(e.getMaterialCode());
                sparePartInOrder.setNum(e.getMaterialNum());
                sparePartInOrder.setWarehouseCode(apply.getWarehouseCode());
                sparePartInOrderMapper.insert(sparePartInOrder);

                //备件库存(这里找warehouseCode)
                //先找该仓库下有没有该物资，没有添加，有则修改
                SparePartStock applyStock = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                        .eq("warehouse_code", apply.getWarehouseCode())
                        .eq("material_code", e.getMaterialCode()), false);
                if(ObjectUtil.isEmpty(applyStock)){
                    SparePartStock partStock = new SparePartStock();
                    partStock.setMaterialCode(e.getMaterialCode());
                    partStock.setNum(applyMaterial.getActualNum());
                    partStock.setWarehouseCode(apply.getWarehouseCode());
//                    partStock.setCreateBy(stockOutDTOList.getOperatorId());
//                    partStock.setUpdateBy(stockOutDTOList.getOperatorId());
                    iSparePartStockService.save(partStock);
                }else{
                    applyStock.setNum(applyStock.getNum()+applyMaterial.getActualNum());
//                    applyStock.setUpdateBy(stockOutDTOList.getOperatorId());
                    sparePartStockList.add(applyStock);
                }

            });
        }
        if(ObjectUtil.isNotEmpty(apply)){
            iSparePartApplyService.updateById(apply);
        }
        if(CollUtil.isNotEmpty(applyMaterialList)){
            iSparePartApplyMaterialService.updateBatchById(applyMaterialList);
        }
        if(CollUtil.isNotEmpty(stockLevel2List)){
            //iStockLevel2Service.updateBatchById(stockLevel2List);
        }
        if(CollUtil.isNotEmpty(sparePartStockList)){
            iSparePartStockService.updateBatchById(sparePartStockList);
        }
        return true;
    }

    @Override
    public void addApply(AddApplyDTO addApplyDTO) {
        //模拟申领单号
        String code = simulateApplyCode();

        if(CollUtil.isNotEmpty(addApplyDTO.getMaterialVOList())){
            SparePartApply sparePartApply = new SparePartApply();
            //TODO
            //申领单号先这样模拟一下，后续有了具体规则再改
            sparePartApply.setCode(code);
            addApplyDTO.getMaterialVOList().forEach(e->{
                SparePartApplyMaterial sparePartApplyMaterial = new SparePartApplyMaterial();
                sparePartApplyMaterial.setApplyCode(sparePartApply.getCode());
                sparePartApplyMaterial.setMaterialCode(e.getMaterialCode());
                sparePartApplyMaterial.setApplyNum(e.getMaterialNum());
//                sparePartApplyMaterial.setCreateBy(addApplyDTO.getOperatorId());
//                sparePartApplyMaterial.setUpdateBy(addApplyDTO.getOperatorId());
                //插入备件申领的物资表
                sparePartApplyMaterialMapper.insert(sparePartApplyMaterial);
            });
            BeanUtils.copyProperties(addApplyDTO,sparePartApply);
            //操作人-->创建人
//            sparePartApply.setCreateBy(addApplyDTO.getOperatorId());
//            sparePartApply.setUpdateBy(addApplyDTO.getOperatorId());

            //插入备件申领单
            sparePartApplyMapper.insert(sparePartApply);
        }
    }

    private String simulateApplyCode() {
        String applyCode="SL101.";
        String y = String.valueOf(LocalDateTime.now().getYear());
        String year = y.substring(y.length() - 2);
        int monthValue = LocalDateTime.now().getMonthValue();
        String month= monthValue +".";
        if(monthValue<10){
            month="0"+ monthValue+".";
        }
        //Integer integer = sparePartApplyMapper.selectCount(null);
        Integer integer = 0;
        return applyCode+year+month+(integer+1);
    }

}
