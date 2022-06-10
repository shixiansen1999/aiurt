package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.MaterialApplyCommitEnum;
import com.aiurt.boot.common.enums.MaterialApplyStatusEnum;
import com.aiurt.boot.common.enums.WorkLogCheckStatusEnum;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApply;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.*;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialApplyVO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartApplyVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartApplyMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartApplyMaterialMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartInOrderMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartApplyMaterialService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartApplyService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 备件申领
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartApplyServiceImpl extends ServiceImpl<SparePartApplyMapper, SparePartApply> implements ISparePartApplyService {

    @Resource
    private SparePartApplyMapper sparePartApplyMapper;
    @Resource
    @Lazy
    private ISparePartApplyService iSparePartApplyService;
    @Resource
    private ISparePartApplyMaterialService iSparePartApplyMaterialService;
    @Resource
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    @Resource
    private IStockLevel2Service iStockLevel2Service;
    @Resource
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Resource
    private ISysBaseAPI iSysBaseAPI;
    @Resource
    private StockLevel2Mapper stockLevel2Mapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private NumberGenerateUtils numberGenerateUtils;

    /**
     * 备件申领-分页列表查询
     *
     * @param page
     * @param sparePartQuery
     * @return
     */
    @Override
    public IPage<SparePartApplyVO> queryPageList(Page<SparePartApplyVO> page, SparePartQuery sparePartQuery) {
        IPage<SparePartApplyVO> pageList = sparePartApplyMapper.queryPageList(page, sparePartQuery);
        return getSparePartApply(pageList);

    }

    /**
     * 二级库出库列表-分页列表查询
     *
     * @param page
     * @param sparePartQuery
     * @return
     */
    @Override
    public IPage<SparePartApplyVO> queryPageListLevel2(Page<SparePartApplyVO> page, SparePartQuery sparePartQuery) {
        IPage<SparePartApplyVO> pageList = sparePartApplyMapper.queryPageListLevel2(page, sparePartQuery);
        return getSparePartApply(pageList);
    }

    /**
     * 计算申领数量以及实际出库数量
     *
     * @param pageList
     * @return
     */
    private IPage<SparePartApplyVO> getSparePartApply(IPage<SparePartApplyVO> pageList) {
        List<SparePartApplyVO> records = pageList.getRecords();
        for (SparePartApplyVO record : records) {
            Integer actualNum = sparePartApplyMaterialMapper.selectActualNum(record.getCode());
            record.setNum(actualNum);
            Integer applyNum = sparePartApplyMaterialMapper.selectApplyNum(record.getCode());
            record.setApplyNum(applyNum);
        }
        return pageList;
    }

    /**
     * 导出excel
     *
     * @param sparePartQuery
     * @return
     */
    @Override
    public List<StockApplyExcel> exportXls(SparePartQuery sparePartQuery) {
        List<StockApplyExcel> excelList = sparePartApplyMapper.selectExportXls(sparePartQuery);
        for (int i = 0; i < excelList.size(); i++) {
            excelList.get(i).setSerialNumber(i + 1);
            excelList.get(i).setStatusName(WorkLogCheckStatusEnum.findMessage(excelList.get(i).getStatus()));
            Integer applyNum = sparePartApplyMaterialMapper.selectApplyNum(excelList.get(i).getCode());
            excelList.get(i).setApplyNum(applyNum);
        }
        return excelList;
    }

    /**
     *  二级库出库列表导出
     * @param selections 选中行的ids
     * @return
     */
    @Override
    public List<StockOutExcel> exportStock2Xls(List<Integer> selections) {
        List<StockOutExcel> excelList = sparePartApplyMapper.selectStock2ExportXls(selections);
        AtomicReference<Integer> flag = new AtomicReference<>(1);
        excelList.forEach(e -> {
            //序号
            e.setSerialNumber(flag.getAndSet(flag.get() + 1));
            //计算实际出库数量
            Integer actualNum = sparePartApplyMaterialMapper.selectActualNum(e.getCode());
            e.setNum(actualNum);

        });
        return excelList;
    }


    /**
     * 出库确认
     *
     * @param stockOutDTOList
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void stockOutConfirm(StockOutDTO stockOutDTOList, HttpServletRequest req) {
        if (CollUtil.isEmpty(stockOutDTOList.getMaterialVOList())) {
            throw new SwscException("出库列表不能为空");
        }
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        //改表spare_part_apply,备件申领表的状态改为已审核
        SparePartApply apply = sparePartApplyMapper.selectById(stockOutDTOList.getId());
        if (apply.getStatus().equals(MaterialApplyStatusEnum.CHECKED.getCode())) {
            throw new SwscException("已经确认出库的单据无法再次出库确认");
        }
        apply.setRemarks(stockOutDTOList.getRemarks());
        apply.setId(stockOutDTOList.getId());
        apply.setUpdateBy(userId);
        apply.setStatus(MaterialApplyStatusEnum.CHECKED.getCode());
        //出库时间
        apply.setStockOutTime(new Date());

        List<SparePartApplyMaterial> applyMaterialList = new ArrayList<>();
        List<StockLevel2> stockLevel2List = new ArrayList<>();
        if (CollUtil.isNotEmpty(stockOutDTOList.getMaterialVOList())) {
            List<StockOutDetailDTO> materialVOList = stockOutDTOList.getMaterialVOList();
            for (StockOutDetailDTO dto : materialVOList) {
                //备件申领物资表spare_part_apply_material设定实际出库量
                SparePartApplyMaterial applyMaterial = new SparePartApplyMaterial();
                applyMaterial.setId(dto.getId());
                applyMaterial.setActualNum(dto.getMaterialNum());
                applyMaterial.setRemarks(dto.getRemarks());
                applyMaterial.setUpdateBy(userId);
                applyMaterialList.add(applyMaterial);

                //二级库库存，出库(这里找apply.getOutWarehouseCode())
                StockLevel2 outStock = iStockLevel2Service.getOne(
                        new QueryWrapper<StockLevel2>().eq(StockLevel2.MATERIAL_CODE, dto.getMaterialCode())
                                .eq(StockLevel2.WAREHOUSE_CODE, apply.getOutWarehouseCode()), false);
                if (ObjectUtil.isNotEmpty(outStock)) {
                    if (outStock.getNum() < applyMaterial.getActualNum()) {
                        throw new SwscException("仓库编号: " + outStock.getWarehouseCode() + " 库存不足，无法确认出库");
                    }
                    outStock.setNum(outStock.getNum() - applyMaterial.getActualNum());
                    outStock.setUpdateBy(userId);
                    outStock.setCreateBy(userId);
                    stockLevel2Mapper.updateById(outStock);
                    stockLevel2List.add(outStock);
                }

                //备件,入库 (apply.getWarehouseCode())
                SparePartInOrder sparePartInOrder = new SparePartInOrder();
                sparePartInOrder.setMaterialCode(dto.getMaterialCode());
                sparePartInOrder.setNum(dto.getMaterialNum());
                sparePartInOrder.setOrgId(apply.getDepartId());
                sparePartInOrder.setCreateBy(userId);
                sparePartInOrder.setConfirmStatus(0);
                sparePartInOrderMapper.insert(sparePartInOrder);
            }
        }
        //修改备件申领状态
        if (ObjectUtil.isNotEmpty(apply)) {
            iSparePartApplyService.updateById(apply);
        }
        //修改备件申领物资详情
        if (CollUtil.isNotEmpty(applyMaterialList)) {
            iSparePartApplyMaterialService.updateBatchById(applyMaterialList);
        }
        //修改二级库库存
        if (CollUtil.isNotEmpty(stockLevel2List)) {
            iStockLevel2Service.updateBatchById(stockLevel2List);
        }
    }


    /**
     * 添加申领单
     *
     * @param addApplyDTO
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addApply(AddApplyDTO addApplyDTO, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        String orgId = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false).getOrgId();

        String before = "SL"+addApplyDTO.getOutWarehouseCode();
        //模拟申领单号
        String code = numberGenerateUtils.getCodeNo(before);


        if (CollUtil.isNotEmpty(addApplyDTO.getMaterialVOList())) {
            SparePartApply sparePartApply = new SparePartApply();
            BeanUtils.copyProperties(addApplyDTO, sparePartApply);

            //申领单号先这样模拟一下，后续有了具体规则再改
            sparePartApply.setCode(code);
            sparePartApply.setCommitStatus(MaterialApplyCommitEnum.UNCOMMITTED.getCode());
            addApplyDTO.getMaterialVOList().forEach(e -> {
                //二级库库存中查询是否存在该物资
                StockLevel2 one = iStockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                        .eq(StockLevel2.WAREHOUSE_CODE, sparePartApply.getOutWarehouseCode())
                        .eq(StockLevel2.MATERIAL_CODE, e.getMaterialCode()), false);
                if (ObjectUtil.isEmpty(one)) {
                    throw new SwscException("二级库出库仓库：" + sparePartApply.getOutWarehouseCode() + "下不存在物资:" + e.getMaterialCode());
                }
                //判断申领数量是否大于库存数量
                if (e.getMaterialNum() > one.getNum()) {
                    throw new SwscException("申领数量不能大于库存数量!");
                }
                SparePartApplyMaterial sparePartApplyMaterial = new SparePartApplyMaterial();
                sparePartApplyMaterial.setApplyCode(sparePartApply.getCode());
                sparePartApplyMaterial.setMaterialCode(e.getMaterialCode());
                sparePartApplyMaterial.setApplyNum(e.getMaterialNum());
                //插入备件申领的物资表
                sparePartApplyMaterialMapper.insert(sparePartApplyMaterial);
            });
            //申领时间
            sparePartApply.setApplyTime(new Date());
            //操作人-->创建人

            sparePartApply.setUpdateBy(userId);
            sparePartApply.setDepartId(orgId);

            //插入备件申领单
            int insert = sparePartApplyMapper.insert(sparePartApply);
            if (insert > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提交申领单
     *
     * @param ids
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> submitFormByIds(String ids, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        String[] split = ids.split(",");
        List<String> strings = Arrays.asList(split);
        for (String string : strings) {
            SparePartApply one = this.getOne(new QueryWrapper<SparePartApply>().eq(SparePartApply.ID, string), false);
            if (one.getCommitStatus() == MaterialApplyCommitEnum.COMMITTED.getCode()) {
                throw new SwscException("单号为" + one.getCode() + "的申领单已经提交，不能重复提交！");
            } else {
                one.setCommitStatus(MaterialApplyCommitEnum.COMMITTED.getCode());
                one.setUpdateBy(userId);
                sparePartApplyMapper.updateById(one);
            }
        }
        return Result.ok("提交成功！");
    }

    /**
     * 编辑申领单
     *
     * @param editApplyDTO
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editApply(EditApplyDTO editApplyDTO, HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);

        List<MaterialApplyVO> materialVOList = editApplyDTO.getMaterialVOList();
        if (CollUtil.isNotEmpty(materialVOList)) {
            //筛选出有id的list
            List<MaterialApplyVO> haveIdList = materialVOList.stream().filter(e -> e.getId() != null).collect(Collectors.toList());
            List<SparePartApplyMaterial> applyMaterialListEdit = new ArrayList<>();
            haveIdList.forEach(h -> {
                SparePartApplyMaterial sparePartApplyMaterial = new SparePartApplyMaterial();
                sparePartApplyMaterial.setId(h.getId());
                sparePartApplyMaterial.setApplyNum(h.getMaterialNum());
                sparePartApplyMaterial.setUpdateBy(userId);
                applyMaterialListEdit.add(sparePartApplyMaterial);
            });
            //批量修改备件申领物资详情表
            iSparePartApplyMaterialService.updateBatchById(applyMaterialListEdit);
            //筛选出没有id的list
            //没有id的进行新增
            List<MaterialApplyVO> noIdList = materialVOList.stream().filter(e -> e.getId() == null).collect(Collectors.toList());
            List<SparePartApplyMaterial> applyMaterialListAdd = new ArrayList<>();
            noIdList.forEach(n -> {
                SparePartApplyMaterial sparePartApplyMaterial = new SparePartApplyMaterial();
                sparePartApplyMaterial.setApplyCode(editApplyDTO.getCode());
                sparePartApplyMaterial.setMaterialCode(n.getMaterialCode());
                sparePartApplyMaterial.setApplyNum(n.getMaterialNum());
                sparePartApplyMaterial.setCreateBy(userId);
                sparePartApplyMaterial.setUpdateBy(userId);
                applyMaterialListAdd.add(sparePartApplyMaterial);
            });
            //批量插入备件申领物资详情表
            sparePartApplyMaterialMapper.insertBatchList(applyMaterialListAdd);
        }

    }


    /**
     * 生成申领单号
     * @return
     */
    private String simulateApplyCode() {
        String applyCode = "SL101.";
        String y = String.valueOf(LocalDateTime.now().getYear());
        String year = y.substring(y.length() - 2);
        int monthValue = LocalDateTime.now().getMonthValue();
        String month = monthValue + ".";
        if (monthValue < CommonConstant.MONTH_VALUE) {
            month = "0" + monthValue + ".";
        }
        Integer integer = sparePartApplyMapper.selectCount(null);
        return applyCode + year + month + (integer + 1);
    }

}
