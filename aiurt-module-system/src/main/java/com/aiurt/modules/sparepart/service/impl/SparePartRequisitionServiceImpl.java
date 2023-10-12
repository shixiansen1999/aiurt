package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.CodeGenerateUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.fault.dto.SparePartStockDTO;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.mapper.MaterialRequisitionDetailMapper;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.entity.dto.SparePartRequisitionDetailDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionListReqDTO;
import com.aiurt.modules.sparepart.entity.dto.resp.SparePartRequisitionListRespDTO;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.sparepart.service.*;
import com.aiurt.modules.stock.dto.StockLevel2RequisitionDetailDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.mapper.StockLevel2InfoMapper;
import com.aiurt.modules.stock.mapper.StockLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockOutboundMaterialsMapper;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import com.aiurt.modules.stock.service.impl.MaterialStockOutInRecordServiceImpl;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 三级库申领的service的实现类，因为用到的实体类是领料单，因此不继承ServiceImpl
 *
 * @author 华宜威
 * @date 2023-09-21 09:52:12
 */
@Service
public class SparePartRequisitionServiceImpl implements SparePartRequisitionService {

    @Autowired
    private IMaterialRequisitionService materialRequisitionService;
    @Autowired
    private MaterialStockOutInRecordServiceImpl materialStockOutInRecordService;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;
    @Autowired
    private MaterialRequisitionDetailMapper materialRequisitionDetailMapper;
    @Autowired
    private StockLevel2RequisitionService stockLevel2RequisitionService;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private StockOutOrderLevel2Mapper stockOutOrderLevel2Mapper;
    @Autowired
    private StockOutboundMaterialsMapper stockOutboundMaterialsMapper;
    @Autowired
    private StockLevel2InfoMapper stockLevel2InfoMapper;
    @Autowired
    private ISysDepartService iSysDepartService;
    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Autowired
    private SparePartLendMapper sparePartLendMapper;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private StockLevel2Mapper stockLevel2Mapper;
    @Autowired
    private IStockLevel2Service stockLevel2Service;
    @Autowired
    private ISparePartReturnOrderService iSparePartReturnOrderService;
    @Autowired
    private IDeviceChangeSparePartService sparePartService;
    @Autowired
    private ISparePartScrapService sparePartScrapService;
    @Autowired
    @Lazy
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private IDeviceAssemblyService deviceAssemblyService;
    @Autowired
    private ISparePartStockService sparePartStockService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO)  {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, materialRequisition);
        // 保管仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId,loginUser.getOrgId());
        wrapper.eq(SparePartStockInfo::getDelFlag,CommonConstant.DEL_FLAG_0);
        if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
            SparePartStockInfo info = sparePartStockInfoService.getOne(wrapper);
            if(null!=info && null!=info.getWarehouseCode()){
                materialRequisition.setCustodialWarehouseCode(info.getWarehouseCode());
            }
        }
        materialRequisition.setApplyTime(new Date());
        materialRequisition.setIsUsed(MaterialRequisitionConstant.UNUSED);
        String name = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                DateUtil.format(materialRequisition.getApplyTime(), "yyyyMMdd") + "-" + "领料单";
        materialRequisition.setName(name);
        materialRequisitionService.save(materialRequisition);
        // 再保存物资清单
        List<SparePartRequisitionDetailDTO> requisitionDetailDTOList = sparePartRequisitionAddReqDTO.getSparePartRequisitionDetailDTOS();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);
        //提交
        if (MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED.equals(materialRequisition.getCommitStatus())) {
            submitRequisition(materialRequisition, requisitionDetailList,sparePartRequisitionAddReqDTO);
        }
    }

    /**
     * 提交
     * */
    private void submitRequisition(MaterialRequisition materialRequisition, List<MaterialRequisitionDetail> requisitionDetailList, SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (MaterialRequisitionConstant.APPLY_TYPE_NORMAL.equals(sparePartRequisitionAddReqDTO.getApplyType())) {
            //普通三级库申领
            //1.修改状态为“待确认”
            materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_TO_BE_CONFIRMED);
            addStockOutOrderLevel2(materialRequisition, requisitionDetailList,true);
        }else {
            //维修申领和特殊三级库申领
            if (MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED.equals(materialRequisition.getCommitStatus())) {
                //判断是否需要三级库申领，二级库申领
                List<StockLevel2RequisitionDetailDTO> level2RequisitionDetailDTOS = new ArrayList<StockLevel2RequisitionDetailDTO>();
                List<MaterialRequisitionDetail> materialRequisitionDetails = new ArrayList<MaterialRequisitionDetail>();

                for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetailList) {
                    //如果申请数量大于可使用数量，则证明需要向上一级库申领，
                    int i = materialRequisitionDetail.getApplyNum() - materialRequisitionDetail.getAvailableNum();

                    //维修申领产生三级库申领，三级库申领产生二级库申领
                    if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                        //三级库更新可使用数量
                        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                                .eq(SparePartStock::getMaterialCode, materialRequisitionDetail.getMaterialsCode())
                                .eq(SparePartStock::getWarehouseCode, materialRequisition.getApplyWarehouseCode())
                                .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0));
                        sparePartStock.setAvailableNum(i < 0 ? -i : 0);
                        sparePartStockMapper.updateById(sparePartStock);

                        if (i > 0) {
                            //二级库可使用数量
                            StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("warehouse_code", sparePartRequisitionAddReqDTO.getLeve2WarehouseCode()).eq("material_code", materialRequisitionDetail.getMaterialsCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
                            int availableNum = ObjectUtil.isNotNull(stockLevel2) ? stockLevel2.getAvailableNum() : 0;
                            MaterialRequisitionDetail requisitionDetailDTO = new MaterialRequisitionDetail();
                            BeanUtils.copyProperties(materialRequisitionDetail, requisitionDetailDTO, "id");
                            requisitionDetailDTO.setApplyNum(i);
                            requisitionDetailDTO.setAvailableNum(availableNum);
                            materialRequisitionDetails.add(requisitionDetailDTO);

                            int applyNumber = i - availableNum;

                            //二级库更新可使用数量
                            if (ObjectUtil.isNotNull(stockLevel2)) {
                                stockLevel2.setAvailableNum(applyNumber < 0 ? -applyNumber : 0);
                                stockLevel2Service.updateById(stockLevel2);
                            }

                            if (applyNumber > 0) {
                                //如果二级库申领数量大于二级库库存，则需要向一级库申领，只要有一条要向一级库申领则所有出入库单需要等这个申领审核完成才能生成且确认
                                StockLevel2RequisitionDetailDTO stockLevel2RequisitionDetailDTO = new StockLevel2RequisitionDetailDTO();
                                BeanUtils.copyProperties(materialRequisitionDetail, stockLevel2RequisitionDetailDTO, "id");
                                stockLevel2RequisitionDetailDTO.setApplyNum(applyNumber);
                                level2RequisitionDetailDTOS.add(stockLevel2RequisitionDetailDTO);
                            }
                        }

                    } else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                        //二级库更新可使用数量
                        StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("warehouse_code", sparePartRequisitionAddReqDTO.getApplyWarehouseCode()).eq("material_code", materialRequisitionDetail.getMaterialsCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
                        stockLevel2.setAvailableNum(i < 0 ? -i : 0);
                        stockLevel2Service.updateById(stockLevel2);

                        if (i > 0) {
                            //三级库申领产生二级库申领
                            //如果二级库申领数量大于二级库库存，则需要向一级库申领，只要有一条要向一级库申领则所有出入库单需要等这个申领审核完成才能生成且确认
                            StockLevel2RequisitionDetailDTO stockLevel2RequisitionDetailDTO = new StockLevel2RequisitionDetailDTO();
                            BeanUtils.copyProperties(materialRequisitionDetail, stockLevel2RequisitionDetailDTO, "id");
                            stockLevel2RequisitionDetailDTO.setApplyNum(i);
                            level2RequisitionDetailDTOS.add(stockLevel2RequisitionDetailDTO);
                        }
                    }
                }

                //二级库申领
                if (CollUtil.isNotEmpty(level2RequisitionDetailDTOS)) {
                    StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO = new StockLevel2RequisitionAddReqDTO();
                    BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, stockLevel2RequisitionAddReqDTO, "id");
                    stockLevel2RequisitionAddReqDTO.setMaterialRequisitionPid(materialRequisition.getId());
                    //保管仓库为二级库
                    if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                        stockLevel2RequisitionAddReqDTO.setCustodialWarehouseCode(sparePartRequisitionAddReqDTO.getLeve2WarehouseCode());
                    }else {
                        stockLevel2RequisitionAddReqDTO.setCustodialWarehouseCode(sparePartRequisitionAddReqDTO.getApplyWarehouseCode());
                    }
                    String code = CodeGenerateUtils.generateSingleCode("EJKSL", 5);
                    stockLevel2RequisitionAddReqDTO.setCode(code);
                    stockLevel2RequisitionAddReqDTO.setIsUsed(MaterialRequisitionConstant.UNUSED);
                    stockLevel2RequisitionAddReqDTO.setStockLevel2RequisitionDetailDTOList(level2RequisitionDetailDTOS);
                    String name = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                            DateUtil.format(materialRequisition.getApplyTime(), "yyyyMMdd") + "-" + "领料单";
                    stockLevel2RequisitionAddReqDTO.setName(name);
                    stockLevel2RequisitionAddReqDTO.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2);
                    stockLevel2RequisitionService.submit(stockLevel2RequisitionAddReqDTO);
                    // 更改消耗物资申领单状态为待确认
                    materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_TO_BE_CONFIRMED);
                    materialRequisitionService.updateById(materialRequisition);
                    return;
                }

                //维修申领单变更为已完成,
                materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);

                if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                    //三级库申领
                    addLevel3Requisition(materialRequisitionDetails, sparePartRequisitionAddReqDTO, materialRequisition,false);

                    //生成三级库出库
                    addSparePartOutOrder(requisitionDetailList, loginUser, materialRequisition,false);
                } else {
                    requisitionDetailList.forEach(s -> s.setActualNum(s.getApplyNum()));
                    //二级库出库
                    String outOrderCode = addStockOutOrderLevel2(materialRequisition, requisitionDetailList,false);
                    //三级库入库
                    addSparePartInOrder(requisitionDetailList, materialRequisition, outOrderCode,loginUser);
                }


                materialRequisitionService.updateById(materialRequisition);
                // 再保存物资清单
                for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetailList) {
                    materialRequisitionDetail.setActualNum(materialRequisitionDetail.getApplyNum());
                }
                materialRequisitionDetailService.updateBatchById(requisitionDetailList);
            }
        }
    }

    /**
     * 三级库申领
     * */
    public void addLevel3Requisition(List<MaterialRequisitionDetail> materialRequisitionDetails, SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO, MaterialRequisition materialRequisition,Boolean flag) {
        if (CollUtil.isNotEmpty(materialRequisitionDetails)) {
            MaterialRequisition requisition = new MaterialRequisition();
            BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, requisition, "id");
            requisition.setApplyWarehouseCode(sparePartRequisitionAddReqDTO.getLeve2WarehouseCode());
            requisition.setCustodialWarehouseCode(sparePartRequisitionAddReqDTO.getApplyWarehouseCode());
            requisition.setLeve2WarehouseCode(null);
            requisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
            requisition.setMaterialRequisitionPid(materialRequisition.getId());
            String code = CodeGenerateUtils.generateSingleCode("WXSL", 5);
            requisition.setCode(code);
            requisition.setIsUsed(MaterialRequisitionConstant.UNUSED);
            requisition.setApplyTime(new Date());
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String name = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                    DateUtil.format(requisition.getApplyTime(), "yyyyMMdd") + "-" + "领料单";
            requisition.setName(name);
            requisition.setId(null);
            materialRequisitionService.save(requisition);
            // 再保存物资清单
            for (MaterialRequisitionDetail materialRequisitionDetail : materialRequisitionDetails) {
                materialRequisitionDetail.setActualNum(materialRequisitionDetail.getApplyNum());
                materialRequisitionDetail.setMaterialRequisitionId(requisition.getId());
                materialRequisitionDetail.setId(null);
            }
            materialRequisitionDetailService.saveBatch(materialRequisitionDetails);

            //二级库出库
            String outOrderCode = addStockOutOrderLevel2(requisition, materialRequisitionDetails,flag);
            //三级库入库
            addSparePartInOrder(materialRequisitionDetails, requisition, outOrderCode,loginUser);
        }
    }


    /**
     * 二级库出库
     * @param materialRequisition
     * @param requisitionDetailList
     * @param flag  库存信息是否需要更新可使用数量 false不需要，true需要,当没有入库的时候不需要再更新
     * */
    public String addStockOutOrderLevel2(MaterialRequisition materialRequisition, List<MaterialRequisitionDetail> requisitionDetailList,Boolean flag){
        //三级库向二级库申领
        StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new LambdaQueryWrapper<StockLevel2Info>().eq(StockLevel2Info::getDelFlag, CommonConstant.DEL_FLAG_0).eq(StockLevel2Info::getWarehouseCode, materialRequisition.getApplyWarehouseCode()));
        SysDepart sysDepart = iSysDepartService.getById(stockLevel2Info.getOrganizationId());

        materialRequisitionService.updateById(materialRequisition);
        //2.插入二级库出库表
        StockOutOrderLevel2 stockOutOrderLevel = new StockOutOrderLevel2();
        //生成出库单号
        String code = CodeGenerateUtils.generateSingleCode("EJCK", 5);
        //EJCK+日期+自增3位
        stockOutOrderLevel.setOrderCode(code);
        //出库仓库为申领仓库
        stockOutOrderLevel.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
        //出库人为出库确认人，保管人为备件申领人(保管人实际存的是username)
        LoginUser keepingUser = sysBaseApi.getUserById(materialRequisition.getApplyUserId());
        stockOutOrderLevel.setCustodialId(keepingUser != null ? keepingUser.getUsername(): materialRequisition.getApplyUserId());
        stockOutOrderLevel.setCustodialWarehouseCode(materialRequisition.getCustodialWarehouseCode());
        stockOutOrderLevel.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
        stockOutOrderLevel.setOrgCode(null != sysDepart ? sysDepart.getOrgCode() : null);
        stockOutOrderLevel.setApplyCode(materialRequisition.getCode());
        stockOutOrderLevel.setMaterialRequisitionId(materialRequisition.getId());
        stockOutOrderLevel.setOutType(MaterialRequisitionConstant.NORMAL_OUT);

        //判断是已完成还是待完成
        boolean equals = MaterialRequisitionConstant.STATUS_COMPLETED.equals(materialRequisition.getStatus());
        if (equals) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date parse = null;
            try {
                parse = sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            stockOutOrderLevel.setOutTime(parse);
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            stockOutOrderLevel.setUserId(user.getId());
        }
        stockOutOrderLevel2Mapper.insert(stockOutOrderLevel);
        List<StockOutboundMaterials> stockOutboundMaterialsList = new ArrayList<>();
        //3.插入出库物资
        requisitionDetailList.forEach(applyMaterial -> {
            StockOutboundMaterials stockOutboundMaterials = new StockOutboundMaterials();
            stockOutboundMaterials.setOutOrderCode(code);
            stockOutboundMaterials.setMaterialCode(applyMaterial.getMaterialsCode());
            stockOutboundMaterials.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
            stockOutboundMaterials.setInventory(applyMaterial.getAvailableNum());
            stockOutboundMaterials.setRemark(applyMaterial.getRemarks());

            StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("warehouse_code",stockOutOrderLevel.getWarehouseCode()).eq("material_code",applyMaterial.getMaterialsCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
            if (flag) {
                //由于特殊申领，这时候二级库的可使用数量已经提前扣掉，但是这里的申领数量还是扣满额的，所以当availableNum小于0的时候就当全出，剩余可使用数量为0
                int availableNum = stockLevel2.getAvailableNum() - applyMaterial.getApplyNum();
                stockLevel2.setAvailableNum(Math.max(availableNum, 0));
            }
            if (equals) {
                //更新二级库库存信息
                stockOutboundMaterials.setActualOutput(applyMaterial.getApplyNum());
                stockLevel2.setNum(stockLevel2.getNum() - applyMaterial.getActualNum());
                int i = applyMaterial.getApplyNum() - applyMaterial.getActualNum();
                if (i > 0) {
                    stockLevel2.setAvailableNum(stockLevel2.getAvailableNum() + i);
                }
            }
            stockLevel2Service.updateById(stockLevel2);
            //计算库存结余
            stockOutboundMaterials.setBalance(stockLevel2.getNum());
            stockOutboundMaterials.setApplyOutput(applyMaterial.getApplyNum());
            stockOutboundMaterialsMapper.insert(stockOutboundMaterials);
            stockOutboundMaterialsList.add(stockOutboundMaterials);
        });
        //同步出库记录到出入库记录表, 只有已完成的申领单才生成出入库记录，因为不是已完成的话，会在二级库出库确认时生成出入库记录
        if (equals){
            materialStockOutInRecordService.addOutRecordOfLevel2(stockOutOrderLevel, stockOutboundMaterialsList);
        }

        return stockOutOrderLevel.getOrderCode();
    }

    /**
     * 生成三级库出库
     * @param flag  库存信息是否需要更新可使用数量 false不需要，true需要
     * */
    public void addSparePartOutOrder(List<MaterialRequisitionDetail> requisitionDetailList, LoginUser loginUser, MaterialRequisition materialRequisition,Boolean flag) {
        for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetailList) {
            SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
            sparePartOutOrder.setMaterialCode(materialRequisitionDetail.getMaterialsCode());
            sparePartOutOrder.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
            sparePartOutOrder.setSysOrgCode(loginUser.getOrgCode());
            sparePartOutOrder.setNum(materialRequisitionDetail.getApplyNum());
            sparePartOutOrder.setApplyOutTime(new Date());
            sparePartOutOrder.setApplyUserId(loginUser.getId());
            sparePartOutOrder.setConfirmTime(new Date());
            sparePartOutOrder.setConfirmUserId(loginUser.getId());
            sparePartOutOrder.setMaterialRequisitionId(materialRequisition.getId());
            String code = CodeGenerateUtils.generateSingleCode("3CK", 5);
            sparePartOutOrder.setOrderCode(code);
            sparePartOutOrder.setStatus(2);
            sparePartOutOrder.setOutType(MaterialRequisitionConstant.NORMAL_OUT);
            List<SparePartOutOrder> outOrders = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                    .eq(SparePartOutOrder::getStatus,2)
                    .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(SparePartOutOrder::getMaterialCode,sparePartOutOrder.getMaterialCode())
                    .eq(SparePartOutOrder::getWarehouseCode,sparePartOutOrder.getWarehouseCode()));
            if(!outOrders.isEmpty()){
                sparePartOutOrder.setUnused(outOrders.get(0).getUnused());
            }
            //出库更新备件库存数量记录表
            updateSparePartStockNum(sparePartOutOrder.getMaterialCode(), sparePartOutOrder.getWarehouseCode(), sparePartOutOrder.getNum());


            SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                    .eq(SparePartStock::getMaterialCode, materialRequisitionDetail.getMaterialsCode())
                    .eq(SparePartStock::getWarehouseCode, sparePartOutOrder.getWarehouseCode())
                    .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0));


            if (ObjectUtil.isNotNull(sparePartStock)) {
                sparePartStock.setNum(sparePartStock.getNum() - sparePartOutOrder.getNum());

                //计算库存结余
                sparePartOutOrder.setBalance(sparePartStock.getNum());
                if (flag) {
                    sparePartStock.setAvailableNum(sparePartStock.getAvailableNum() - sparePartOutOrder.getNum());
                }
                sparePartOutOrderMapper.insert(sparePartOutOrder);

                //更新库存数量
                sparePartStockMapper.updateById(sparePartStock);
            } else {
                //计算库存结余
                sparePartOutOrder.setBalance(0);
                sparePartOutOrderMapper.insert(sparePartOutOrder);
            }


            //同步出库记录到出入库记录表
            MaterialStockOutInRecord record = new MaterialStockOutInRecord();
            BeanUtils.copyProperties(sparePartOutOrder, record);
            record.setMaterialRequisitionType(materialRequisition.getMaterialRequisitionType());
            record.setIsOutIn(2);
            //带负号表示出库
            record.setNum(-record.getNum());
            record.setOutInType(sparePartOutOrder.getOutType());
            materialStockOutInRecordService.save(record);
        }
    }

    /**
     * 生成三级库入库
     *
     * @param materialRequisitionDetails
     * @param requisition
     * @param outOrderCode
     * */
    public void addSparePartInOrder(List<MaterialRequisitionDetail> materialRequisitionDetails, MaterialRequisition requisition, String outOrderCode,LoginUser loginUser) {
        for (MaterialRequisitionDetail materialRequisitionDetail : materialRequisitionDetails) {
            SparePartInOrder sparePartInOrder = new SparePartInOrder();
            sparePartInOrder.setMaterialCode(materialRequisitionDetail.getMaterialsCode());
            sparePartInOrder.setWarehouseCode(requisition.getCustodialWarehouseCode());
            sparePartInOrder.setNum(null!=materialRequisitionDetail.getActualNum()?materialRequisitionDetail.getActualNum():1);
            SparePartStockInfo sparePartStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getDelFlag,CommonConstant.DEL_FLAG_0).eq(SparePartStockInfo::getWarehouseCode,requisition.getCustodialWarehouseCode()));
            SysDepart sysDepart = new SysDepart();
            if (ObjectUtils.isNotEmpty(sparePartStockInfo)){
                sysDepart = iSysDepartService.getById(sparePartStockInfo.getOrganizationId());
            }
            sparePartInOrder.setConfirmId(loginUser.getId());
            sparePartInOrder.setConfirmTime(new Date());
            sparePartInOrder.setOrgId(null!=sysDepart?sysDepart.getId():null);
            sparePartInOrder.setSysOrgCode(null!=sysDepart?sysDepart.getOrgCode():null);
            sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_1);
            sparePartInOrder.setOutOrderCode(outOrderCode);
            sparePartInOrder.setMaterialRequisitionId(requisition.getId());
            sparePartInOrder.setInType(MaterialRequisitionConstant.NORMAL_IN);
            //全新数量等于入库数量
            sparePartInOrder.setNewNum(sparePartInOrder.getNum());
            updateSparePartStockNum(sparePartInOrder.getMaterialCode(), sparePartInOrder.getWarehouseCode(), sparePartInOrder.getNum());

            SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                    .eq(SparePartStock::getMaterialCode, materialRequisitionDetail.getMaterialsCode())
                    .eq(SparePartStock::getWarehouseCode, sparePartInOrder.getWarehouseCode())
                    .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0));

            if(ObjectUtil.isNull(sparePartStock)){
                SparePartStock stock = new SparePartStock();
                stock.setMaterialCode(sparePartInOrder.getMaterialCode());
                stock.setNum(sparePartInOrder.getNum());
                stock.setAvailableNum(sparePartInOrder.getNum());
                stock.setWarehouseCode(sparePartInOrder.getWarehouseCode());
                //存仓库组织机构的关联班组
                String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartInOrder.getWarehouseCode());
                SysDepartModel departByOrgCode = sysBaseApi.getDepartByOrgCode(orgCode);
                stock.setOrgId(departByOrgCode.getId());
                stock.setSysOrgCode(departByOrgCode.getOrgCode());
                sparePartStockMapper.insert(stock);
                //入库结余
                sparePartInOrder.setBalance(stock.getNum());
            }else {
                sparePartStock.setNum(sparePartStock.getNum()+sparePartInOrder.getNum());
                sparePartStock.setAvailableNum(sparePartStock.getAvailableNum()+sparePartInOrder.getNum());
                //入库结余
                sparePartInOrder.setBalance(sparePartStock.getNum());
                //更新库存数量
                sparePartStockMapper.updateById(sparePartStock);
            }

            String code = CodeGenerateUtils.generateSingleCode("3RK", 5);
            sparePartInOrder.setOrderCode(code);
            sparePartInOrderMapper.insert(sparePartInOrder);

            //同步入库记录到出入库记录表
            MaterialStockOutInRecord record = new MaterialStockOutInRecord();
            BeanUtils.copyProperties(sparePartInOrder, record);
            record.setConfirmUserId(loginUser.getId());
            record.setMaterialRequisitionType(requisition.getMaterialRequisitionType());
            record.setIsOutIn(1);
            record.setOutInType(sparePartInOrder.getInType());
            materialStockOutInRecordService.save(record);
        }
    }


    private void updateSparePartStockNum(String materialCode,String warehouseCode,Integer num) {
        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, materialCode)
                .eq(SparePartStockNum::getWarehouseCode, warehouseCode)
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
        if (ObjectUtil.isNotNull(stockNum)) {
            Integer newNum = stockNum.getNewNum();
            //如果全新数量小于新组件数量，则从已使用数量中扣除
            if (newNum < num) {
                stockNum.setNewNum(0);
                stockNum.setUsedNum(stockNum.getUsedNum() - (num - newNum));
            } else {
                stockNum.setNewNum(newNum - num);
            }
            sparePartStockNumMapper.updateById(stockNum);
        } else {
            SparePartStockNum partStockNum = new SparePartStockNum();
            partStockNum.setMaterialCode(materialCode);
            partStockNum.setWarehouseCode(warehouseCode);
            // 新增全新数量
            partStockNum.setNewNum(0);
            // 新增已使用数量
            partStockNum.setUsedNum(0);
            // 新增待报损数量
            partStockNum.setScrapNum(0);
            // 新增委外送修数量
            partStockNum.setOutsourceRepairNum(0);
            sparePartStockNumMapper.insert(partStockNum);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO){
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 将DTO转化成实体类，并更新领料单
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, materialRequisition);
        // 保管仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId,loginUser.getOrgId());
        wrapper.eq(SparePartStockInfo::getDelFlag,CommonConstant.DEL_FLAG_0);
        SparePartStockInfo info = sparePartStockInfoService.getOne(wrapper);
        if(null!=info && null!=info.getWarehouseCode()){
            materialRequisition.setCustodialWarehouseCode(info.getWarehouseCode());
        }
        materialRequisition.setApplyTime(new Date());
        materialRequisitionService.updateById(materialRequisition);
        // 物资清单，删除原来的，再新增
        LambdaQueryWrapper<MaterialRequisitionDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialRequisitionDetail::getMaterialRequisitionId, materialRequisition.getId());
        materialRequisitionDetailService.remove(queryWrapper);
        // 再保存物资清单
        List<SparePartRequisitionDetailDTO> requisitionDetailDTOList = sparePartRequisitionAddReqDTO.getSparePartRequisitionDetailDTOS();
        List<MaterialRequisitionDetail> requisitionDetailList = requisitionDetailDTOList.stream().map(detailDTO -> {
            MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
            BeanUtils.copyProperties(detailDTO, requisitionDetail);
            requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
            return requisitionDetail;
        }).collect(Collectors.toList());
        materialRequisitionDetailService.saveBatch(requisitionDetailList);

        //提交
        if (MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED.equals(materialRequisition.getCommitStatus())) {
            submitRequisition(materialRequisition, requisitionDetailList,sparePartRequisitionAddReqDTO);
        }
    }


    @Override
    public Page<SparePartRequisitionListRespDTO> pageList(SparePartRequisitionListReqDTO sparePartRequisitionListReqDTO) {
        // 将请求DTO转化成实体类进行查询
        int pageNo = sparePartRequisitionListReqDTO.getPageNo();
        int pageSize = sparePartRequisitionListReqDTO.getPageSize();
        Page<MaterialRequisition> page = new Page<>(pageNo, pageSize);
        MaterialRequisition materialRequisition = new MaterialRequisition();
        BeanUtils.copyProperties(sparePartRequisitionListReqDTO, materialRequisition);
        QueryWrapper<MaterialRequisition> queryWrapper = QueryGenerator.initQueryWrapper(materialRequisition, null);
        // 时间范围的搜索, DateUtil.beginOfDay不能放到queryWrapper.le()里面，因为在里面DateUtil.beginOfDay总是执行，可能会报空指针
        Date searchBeginTime = sparePartRequisitionListReqDTO.getSearchBeginTime();
        Date searchEndTime = sparePartRequisitionListReqDTO.getSearchEndTime();
        DateTime beginTime = searchBeginTime != null ? DateUtil.beginOfDay(searchBeginTime) : null;
        DateTime endTime = searchEndTime != null ? DateUtil.endOfDay(searchEndTime) : null;
        queryWrapper.lambda().ge(searchBeginTime != null, MaterialRequisition::getApplyTime, beginTime);
        queryWrapper.lambda().le(searchEndTime != null, MaterialRequisition::getApplyTime, endTime);

        String search = sparePartRequisitionListReqDTO.getSearch();
        if (StrUtil.isNotBlank(search)) {
            //申领单编号查询
            queryWrapper.lambda().like( MaterialRequisition::getCode, sparePartRequisitionListReqDTO.getSearch());
            //申领人查询
            queryWrapper.or().apply("(apply_user_id in (select id from sys_user where (realname like concat('%', {0}, '%'))))", sparePartRequisitionListReqDTO.getSearch());
        }
        String materialRequisitionTypes = sparePartRequisitionListReqDTO.getMaterialRequisitionTypes();
        if (StrUtil.isNotBlank(materialRequisitionTypes)) {
            queryWrapper.in("material_requisition_type", StrUtil.split(materialRequisitionTypes, ','));
        }

        //申领单类型查询
        String materialRequisitionType = sparePartRequisitionListReqDTO.getMaterialRequisitionType();
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(materialRequisitionType), MaterialRequisition::getMaterialRequisitionType, materialRequisitionType);
        //申领仓库查询
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(sparePartRequisitionListReqDTO.getApplyWarehouseCode()), MaterialRequisition::getApplyWarehouseCode, sparePartRequisitionListReqDTO.getApplyWarehouseCode());
        //保管仓库查询
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(sparePartRequisitionListReqDTO.getCustodialWarehouseCode()), MaterialRequisition::getCustodialWarehouseCode, sparePartRequisitionListReqDTO.getCustodialWarehouseCode());
        //申领状态查询
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(sparePartRequisitionListReqDTO.getStatus()), MaterialRequisition::getStatus, sparePartRequisitionListReqDTO.getStatus());

        queryWrapper.lambda().eq( MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0);
        // 排序
        queryWrapper.lambda().orderByDesc(MaterialRequisition::getPlanApplyTime).orderByDesc(MaterialRequisition::getId);

        materialRequisitionService.page(page, queryWrapper);

        // 将查询结果转化成响应DTO返回
        Page<SparePartRequisitionListRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);
        // 二级库仓库
        Map<String, String> level2Map = sysBaseApi.queryTableDictItemsByCode("stock_level2_info", "warehouse_name", "warehouse_code")
                .stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 三级库仓库
        Map<String, String> level3Map = sysBaseApi.queryTableDictItemsByCode("spare_part_stock_info", "warehouse_name", "warehouse_code")
                .stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        //todo 查询仓库名称
        List<SparePartRequisitionListRespDTO> respRecords = page.getRecords().stream().map(record -> {
            SparePartRequisitionListRespDTO respDTO = new SparePartRequisitionListRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            Integer requisitionType = record.getMaterialRequisitionType();
            //翻译仓库名称
            if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(requisitionType)) {
                //维修申领
                respDTO.setApplyWarehouseName(level3Map.get(record.getApplyWarehouseCode()));
                respDTO.setLeve2WarehouseName(level2Map.get(record.getLeve2WarehouseCode()));
            } else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(requisitionType)) {
                //三级库领用
                respDTO.setApplyWarehouseName(level2Map.get(record.getApplyWarehouseCode()));
                respDTO.setCustodialWarehouseName(level3Map.get(record.getCustodialWarehouseCode()));
            }
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(respRecords);
        return pageList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submit(String id) {
        MaterialRequisition materialRequisition = materialRequisitionService.getById(id);
        QueryWrapper<MaterialRequisitionDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MaterialRequisitionDetail::getMaterialRequisitionId, id).eq(MaterialRequisitionDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<MaterialRequisitionDetail> requisitionDetailList = materialRequisitionDetailService.getBaseMapper().selectList(wrapper);
        SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO = new SparePartRequisitionAddReqDTO();
        BeanUtils.copyProperties(materialRequisition, sparePartRequisitionAddReqDTO, "id");
        materialRequisition.setCommitStatus(MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED);
        requisitionDetailList.forEach(detail -> {
            //查询实时可用量
            if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(materialRequisition.getMaterialRequisitionType())) {
                //三级库领用
                StockLevel2 stockLevel2 = stockLevel2Service.getOne(new LambdaQueryWrapper<StockLevel2>()
                        .eq(StockLevel2::getWarehouseCode, materialRequisition.getApplyWarehouseCode())
                        .eq(StockLevel2::getMaterialCode, detail.getMaterialsCode())
                        .eq(StockLevel2::getDelFlag, CommonConstant.DEL_FLAG_0), false);
                detail.setAvailableNum(ObjectUtil.isNotNull(stockLevel2) ? stockLevel2.getAvailableNum() : 0);
                //只有三级库普通领用的时候需要再次检查可以使用数量
                if ((MaterialRequisitionConstant.APPLY_TYPE_NORMAL.equals(materialRequisition.getApplyType())) && detail.getAvailableNum() < detail.getApplyNum()) {
                    throw new AiurtBootException("可使用数量已变更，不足申领数量，请修改申领数量");
                }
            }
        });
        submitRequisition(materialRequisition, requisitionDetailList,sparePartRequisitionAddReqDTO);
    }



    /**故障备件更换*/
    public void addSpareChange(List<SparePartStockDTO> dtoList, String faultCode,String faultRepairRecordId) {

        // 获取当前登录人所属机构， 根据所属机构擦查询管理三级管理仓库
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 查询仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        //一个班组管理一个仓库，用selectOne,防止有人多配，只取一条
        wrapper.eq(SparePartStockInfo::getOrganizationId, loginUser.getOrgId()).last("limit 1");
        SparePartStockInfo stockInfo = sparePartStockInfoService.getOne(wrapper);

        //先判断故障维修单是否已经关联了领料单，即事前申领了，
        List<MaterialRequisitionDetail> materialRequisitions = materialRequisitionDetailMapper.getList(faultRepairRecordId);
        if (CollUtil.isNotEmpty(materialRequisitions)) {
            //更新申领单为已使用
            materialRequisitionDetailMapper.updateIsUsed(faultRepairRecordId,MaterialRequisitionConstant.IS_USED);
            for (MaterialRequisitionDetail materialRequisition : materialRequisitions) {
                int newSparePartNum = dtoList.stream().filter(s -> s.getWarehouseCode().equals(materialRequisition.getWarehouseCode()) && s.getNewSparePartCode().equals(materialRequisition.getMaterialsCode()))
                        .mapToInt(SparePartStockDTO::getNewSparePartNum).sum();
                //剩余数量
                int remainingQuantity = materialRequisition.getApplyNum() - newSparePartNum;
                if (remainingQuantity > 0) {
                    //退库处理
                    SparePartReturnOrder sparePartReturnOrder = new SparePartReturnOrder();
                    sparePartReturnOrder.setWarehouseCode(stockInfo.getWarehouseCode());
                    sparePartReturnOrder.setMaterialCode(materialRequisition.getMaterialsCode());
                    sparePartReturnOrder.setNum(remainingQuantity);
                    iSparePartReturnOrderService.add(sparePartReturnOrder);
                }
            }
            for (SparePartStockDTO sparePartStockDTO : dtoList) {
                //处理故障备件更换记录
                sparePartStockDTO.setCode(faultCode);
                sparePartStockDTO.setRepairRecordId(faultRepairRecordId);
                dealDeviceChange(loginUser, sparePartStockDTO, stockInfo);
            }

            //更新故障更换记录
        } else {
            //事后申领
            //总维修申领
            MaterialRequisition materialRequisition = new MaterialRequisition();
            materialRequisition.setCode(CodeGenerateUtils.generateSingleCode("WXSL", 5));
            materialRequisition.setApplyWarehouseCode(stockInfo.getWarehouseCode());
            materialRequisition.setApplyUserId(loginUser.getId());
            materialRequisition.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR);
            materialRequisition.setApplyType(MaterialRequisitionConstant.APPLY_TYPE_SPECIAL);
            materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
            materialRequisition.setCommitStatus(MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED);
            materialRequisition.setIsUsed(MaterialRequisitionConstant.IS_USED);
            // 添加其他信息
            Date applyTime = new Date();
            materialRequisition.setApplyTime(applyTime);
            String name = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                    DateUtil.format(applyTime, "yyyyMMdd") + "-" + "领料单";
            materialRequisition.setName(name);
            materialRequisition.setFaultRepairRecordId(faultRepairRecordId);
            materialRequisitionService.save(materialRequisition);

            List<MaterialRequisitionDetail> requisitionDetailList = new ArrayList<>();
            //先根据物资区分
            Map<String, List<SparePartStockDTO>> map = dtoList.stream().collect(Collectors.groupingBy(SparePartStockDTO::getMaterialCode));
            for (Map.Entry<String, List<SparePartStockDTO>> entry : map.entrySet()) {
                String materialCode = entry.getKey();
                List<SparePartStockDTO> sparePartStockDTOList = entry.getValue();
                //构建申领物资
                int sum = sparePartStockDTOList.stream().mapToInt(SparePartStockDTO::getApplyNum).sum();
                MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
                requisitionDetail.setMaterialRequisitionId(materialRequisition.getId());
                requisitionDetail.setApplyNum(sum);
                requisitionDetail.setActualNum(sum);
                requisitionDetail.setMaterialsCode(materialCode);
                requisitionDetail.setMaterialsName(sparePartStockDTOList.get(0).getName());
                requisitionDetailList.add(requisitionDetail);

                //处理故障备件更换记录,当存在原备件的时候才有备件更换记录
                SparePartStockDTO stockDTO = sparePartStockDTOList.get(0);
                if (StrUtil.isNotBlank(stockDTO.getOldMaterialCode())) {
                    SparePartStockDTO sparePartStockDTO = new SparePartStockDTO();
                    BeanUtils.copyProperties(stockDTO, sparePartStockDTO);
                    sparePartStockDTO.setNewSparePartNum(sum);
                    sparePartStockDTO.setWarehouseCode(stockInfo.getWarehouseCode());
                    sparePartStockDTO.setCode(faultCode);
                    sparePartStockDTO.setRepairRecordId(faultRepairRecordId);
                    dealDeviceChange(loginUser, sparePartStockDTO, stockInfo);
                }

                int newSparePartNum = sparePartStockDTOList.stream().mapToInt(SparePartStockDTO::getNewSparePartNum).sum();
                //剩余数量
                int remainingQuantity = sum - newSparePartNum;
                if (remainingQuantity > 0) {
                    //退库处理
                    SparePartReturnOrder sparePartReturnOrder = new SparePartReturnOrder();
                    sparePartReturnOrder.setWarehouseCode(stockInfo.getWarehouseCode());
                    sparePartReturnOrder.setMaterialCode(materialCode);
                    sparePartReturnOrder.setNum(remainingQuantity);
                    iSparePartReturnOrderService.add(sparePartReturnOrder);
                }

            }
            materialRequisitionDetailService.saveBatch(requisitionDetailList);



            //借入集合
            List<SparePartStockDTO> lend = dtoList.stream().filter(s -> MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(s.getMaterialRequisitionType())).filter(s -> !s.getWarehouseCode().equals(stockInfo.getWarehouseCode())).collect(Collectors.toList());

            //借入处理
            if (CollUtil.isNotEmpty(lend)) {
                lendMaterial(lend, loginUser, stockInfo,materialRequisition);
            }

            //根据仓库区分，三级库申领集合
            List<SparePartStockDTO> level3RequisitionList = new ArrayList<>(dtoList.stream().filter(s -> MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2.equals(s.getMaterialRequisitionType())).collect(Collectors.toList()));

            //三级库申领处理
            if (CollUtil.isNotEmpty(level3RequisitionList)) {
                Map<String, List<SparePartStockDTO>> level3Map = level3RequisitionList.stream().collect(Collectors.groupingBy(SparePartStockDTO::getWarehouseCode));
                for (Map.Entry<String, List<SparePartStockDTO>> entry : level3Map.entrySet()) {
                    String warehouseCode = entry.getKey();
                    List<SparePartStockDTO> sparePartStockDTOS = entry.getValue();
                    MaterialRequisition level3MaterialRequisition = new MaterialRequisition();
                    level3MaterialRequisition.setCode(CodeGenerateUtils.generateSingleCode("WXSL", 5));
                    level3MaterialRequisition.setApplyWarehouseCode(warehouseCode);
                    level3MaterialRequisition.setCustodialWarehouseCode(stockInfo.getWarehouseCode());
                    level3MaterialRequisition.setApplyUserId(loginUser.getId());
                    level3MaterialRequisition.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3);
                    level3MaterialRequisition.setApplyType(MaterialRequisitionConstant.APPLY_TYPE_SPECIAL);
                    level3MaterialRequisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
                    level3MaterialRequisition.setCommitStatus(MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED);
                    level3MaterialRequisition.setIsUsed(MaterialRequisitionConstant.IS_USED);
                    // 添加其他信息
                    level3MaterialRequisition.setApplyTime(applyTime);
                    level3MaterialRequisition.setName(name);
                    level3MaterialRequisition.setFaultRepairRecordId(faultRepairRecordId);
                    String name2 = loginUser.getOrgName() + "-" + loginUser.getRealname() + "-" +
                            DateUtil.format(level3MaterialRequisition.getApplyTime(), "yyyyMMdd") + "-" + "领料单";
                    level3MaterialRequisition.setName(name2);
                    materialRequisitionService.save(level3MaterialRequisition);

                    // 再保存物资清单
                    List<MaterialRequisitionDetail> requisitionDetails = sparePartStockDTOS.stream().map(detailDTO -> {
                        MaterialRequisitionDetail requisitionDetail = new MaterialRequisitionDetail();
                        requisitionDetail.setMaterialRequisitionId(level3MaterialRequisition.getId());
                        requisitionDetail.setApplyNum(detailDTO.getApplyNum());
                        requisitionDetail.setActualNum(detailDTO.getApplyNum());
                        requisitionDetail.setAvailableNum(detailDTO.getAvailableNum());
                        requisitionDetail.setMaterialsCode(detailDTO.getMaterialCode());
                        requisitionDetail.setMaterialsName(detailDTO.getName());
                        return requisitionDetail;
                    }).collect(Collectors.toList());
                    materialRequisitionDetailService.saveBatch(requisitionDetails);

                    //二级库出库
                    String outOrderCode = addStockOutOrderLevel2(level3MaterialRequisition, requisitionDetails,true);
                    //三级库入库
                    addSparePartInOrder(requisitionDetails, level3MaterialRequisition, outOrderCode,loginUser);
                }
            }

            //生成三级库出库
            addSparePartOutOrder(requisitionDetailList, loginUser, materialRequisition,true);
        }

    }


    private void lendMaterial(List<SparePartStockDTO> lend, LoginUser loginUser, SparePartStockInfo stockInfo, MaterialRequisition materialRequisition) {
        for (SparePartStockDTO sparePartStockDTO : lend) {
            //1.生成借出单（已借出）
            SparePartLend sparePartLend = new SparePartLend();
            sparePartLend.setMaterialCode(sparePartStockDTO.getMaterialCode());
            sparePartLend.setLendWarehouseCode(sparePartStockDTO.getWarehouseCode());
            sparePartLend.setBackWarehouseCode(stockInfo.getWarehouseCode());

            sparePartLend.setEntryOrgCode(loginUser.getOrgCode());
            //查询借出仓库
            SparePartStockInfo lendStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getWarehouseCode,sparePartLend.getLendWarehouseCode()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
            sparePartLend.setExitOrgCode(sysDepartService.getById(lendStockInfo.getOrganizationId()).getOrgCode());
            sparePartLend.setOutTime(new Date());
            sparePartLend.setLendPerson(loginUser.getId());
            sparePartLend.setLendNum(sparePartStockDTO.getNewSparePartNum());
            sparePartLend.setBorrowNum(sparePartStockDTO.getNewSparePartNum());
            sparePartLend.setStatus(2);
            sparePartLend.setCreateOrgCode(loginUser.getOrgCode());
            sparePartLendMapper.insert(sparePartLend);

            //2.借出仓库库存数做减法,更新可使用数量
            SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode, sparePartLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode, sparePartLend.getLendWarehouseCode()));
            lendStock.setNum(lendStock.getNum() - sparePartLend.getLendNum());
            lendStock.setAvailableNum(lendStock.getAvailableNum() - sparePartLend.getLendNum());
            //出库更新备件库存数量记录表
            updateSparePartStockNum(sparePartLend.getMaterialCode(), sparePartLend.getLendWarehouseCode(), sparePartLend.getLendNum());

            sparePartStockMapper.updateById(lendStock);

            //4.借出仓库生成出库单
            SparePartOutOrder lendOutOrder = new SparePartOutOrder();
            lendOutOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3CK", 5));
            lendOutOrder.setMaterialCode(sparePartLend.getMaterialCode());
            lendOutOrder.setWarehouseCode(sparePartLend.getLendWarehouseCode());
            lendOutOrder.setSysOrgCode(loginUser.getOrgCode());
            lendOutOrder.setNum(sparePartLend.getLendNum());
            lendOutOrder.setConfirmTime(new Date());
            lendOutOrder.setConfirmUserId(loginUser.getId());
            lendOutOrder.setApplyOutTime(new Date());
            lendOutOrder.setApplyUserId(sparePartLend.getLendPerson());
            lendOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2);
            //计算库存结余
            lendOutOrder.setBalance(lendStock.getNum());
            lendOutOrder.setMaterialRequisitionId(materialRequisition.getId());
            lendOutOrder.setOutType(MaterialRequisitionConstant.BORROW_OUT);

            List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                    .eq(SparePartOutOrder::getStatus,2)
                    .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(SparePartOutOrder::getMaterialCode,lendOutOrder.getMaterialCode())
                    .eq(SparePartOutOrder::getWarehouseCode,lendOutOrder.getWarehouseCode()));
            if(!orderList.isEmpty()){
                lendOutOrder.setUnused(orderList.get(0).getUnused());
            }
            sparePartOutOrderMapper.insert(lendOutOrder);
            //同步出库记录到出入库记录表
            MaterialStockOutInRecord record = new MaterialStockOutInRecord();
            BeanUtils.copyProperties(lendOutOrder, record);
            record.setMaterialRequisitionType(materialRequisition.getMaterialRequisitionType());
            record.setIsOutIn(2);
            record.setOrderId(lendOutOrder.getId());
            //带负号表示出库
            record.setNum(-record.getNum());
            record.setOutInType(lendOutOrder.getOutType());
            materialStockOutInRecordService.save(record);


            //5.因为直接借入然后出库，所以借入仓库库存数0,可使用数量0
            //但有可能之前不存在该物资的库存记录，需要新增备件库存数量记录表，所有数量为0
            updateSparePartStockNum(sparePartLend.getMaterialCode(), stockInfo.getWarehouseCode(), sparePartLend.getLendNum());
            SparePartStock  borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                    .eq(SparePartStock::getMaterialCode, lendOutOrder.getMaterialCode())
                    .eq(SparePartStock::getWarehouseCode, stockInfo.getWarehouseCode()));
            int num = 0;
            if(ObjectUtil.isNull(borrowingStock)){
                SparePartStock stock = new SparePartStock();
                stock.setMaterialCode(lendOutOrder.getMaterialCode());
                stock.setNum(lendOutOrder.getNum());
                stock.setAvailableNum(lendOutOrder.getNum());
                stock.setWarehouseCode(stockInfo.getWarehouseCode());
                //存仓库组织机构的关联班组
                String orgCode = sysBaseApi.getDepartByWarehouseCode(stockInfo.getWarehouseCode());
                SysDepartModel departByOrgCode = sysBaseApi.getDepartByOrgCode(orgCode);
                stock.setOrgId(departByOrgCode.getId());
                stock.setSysOrgCode(departByOrgCode.getOrgCode());
                sparePartStockMapper.insert(stock);
                num = num + lendOutOrder.getNum();
            }else {
                borrowingStock.setNum(borrowingStock.getNum()+lendOutOrder.getNum());
                borrowingStock.setAvailableNum(borrowingStock.getAvailableNum()+lendOutOrder.getNum());
                //更新库存数量
                sparePartStockMapper.updateById(borrowingStock);
                num = num + borrowingStock.getNum();
            }


            //6.借入仓库生成入库记录
            SparePartInOrder sparePartInOrder = new SparePartInOrder();
            sparePartInOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3RK", 5));
            sparePartInOrder.setMaterialCode(lendOutOrder.getMaterialCode());
            sparePartInOrder.setWarehouseCode(stockInfo.getWarehouseCode());
            sparePartInOrder.setNum(lendOutOrder.getNum());
            sparePartInOrder.setNewNum(lendOutOrder.getNum());
            sparePartInOrder.setOrgId(loginUser.getOrgId());
            sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
            sparePartInOrder.setConfirmId(loginUser.getId());
            sparePartInOrder.setConfirmTime(new Date());
            sparePartInOrder.setSysOrgCode(loginUser.getOrgCode());
            //计算库存结余
            sparePartInOrder.setBalance(num);
            sparePartInOrder.setMaterialRequisitionId(materialRequisition.getId());
            sparePartInOrder.setInType(MaterialRequisitionConstant.BORROW_IN);
            sparePartInOrderMapper.insert(sparePartInOrder);

            //同步入库记录到出入库记录表
            MaterialStockOutInRecord record2 = new MaterialStockOutInRecord();
            BeanUtils.copyProperties(sparePartInOrder, record2);
            record2.setConfirmUserId(loginUser.getId());
            record2.setMaterialRequisitionType(materialRequisition.getMaterialRequisitionType());
            record2.setOrderId(sparePartInOrder.getId());
            record2.setIsOutIn(1);
            record2.setStatus(2);
            record2.setOutInType(sparePartInOrder.getInType());
            materialStockOutInRecordService.save(record2);
        }

    }

    private void dealDeviceChange(LoginUser loginUser,SparePartStockDTO sparePartStockDTO,SparePartStockInfo stockInfo) {
        DeviceChangeSparePart sparePart = new DeviceChangeSparePart();
        sparePart.setCode(sparePartStockDTO.getCode());
        //原组件数量默认1
        sparePart.setOldSparePartNum(1);
        sparePart.setNewOrgCode(loginUser.getOrgCode());
        sparePart.setOldSparePartCode(sparePartStockDTO.getOldSparePartCode());
        sparePart.setMaterialBaseCode(sparePartStockDTO.getMaterialCode());
        sparePart.setConsumables(sparePartStockDTO.getConsumablesType());
        sparePart.setWarehouseCode(sparePartStockDTO.getWarehouseCode());
        sparePart.setRepairRecordId(sparePartStockDTO.getRepairRecordId());

        if ("0".equals(sparePartStockDTO.getConsumablesType())) {
            SparePartScrap scrap = new SparePartScrap();
            scrap.setStatus(1);
            scrap.setSysOrgCode(loginUser.getOrgCode());
            scrap.setMaterialCode(sparePartStockDTO.getOldMaterialCode());
            scrap.setWarehouseCode(stockInfo.getWarehouseCode());
            scrap.setNum(1);
            scrap.setFaultCode(sparePartStockDTO.getCode());
            scrap.setScrapTime(new Date());
            scrap.setCreateBy(loginUser.getUsername());
            sparePartScrapService.save(scrap);
            try {
                String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(loginUser.getOrgCode()), Collections.singletonList(RoleConstant.FOREMAN));
                //发送通知
                MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userName, "备件报废申请-确认" + DateUtil.today(), null);
                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, scrap.getId());
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND.getType());
                map.put("materialCode",scrap.getMaterialCode());
                String materialName= sysBaseApi.getMaterialNameByCode(scrap.getMaterialCode());
                map.put("name",materialName);
                map.put("num",scrap.getNum());
                LoginUser userByName = sysBaseApi.getUserByName(scrap.getCreateBy());
                map.put("realName",userByName.getRealname());
                map.put("scrapTime", DateUtil.format(scrap.getScrapTime(),"yyyy-MM-dd HH:mm:ss"));
                messageDTO.setData(map);
                //发送待办
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
                todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
                todoDTO.setTitle("备件报废申请-确认" + DateUtil.today());
                todoDTO.setMsgAbstract("备件报废申请");
                todoDTO.setPublishingContent("备件报废申请，请确认");
                todoDTO.setCurrentUserName(userName);
                todoDTO.setBusinessKey(scrap.getId());
                todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
                todoDTO.setCurrentUserName(userName);
                todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
                todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                todoDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);
                isTodoBaseAPI.createTodoTask(todoDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sparePart.setScrapId(scrap.getId());
            QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper();
            queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
            List<DeviceAssembly> deviceAssemblyList = deviceAssemblyService.list(queryWrapper);
            Set<String> assemblyCodeSet = deviceAssemblyList.stream().map(DeviceAssembly::getCode).collect(Collectors.toSet());
            List<String> strings = new ArrayList<>();
            Integer newSparePartNum = sparePartStockDTO.getNewSparePartNum();
            Integer num = 1;
            for (Integer i = 0; i < newSparePartNum; i++) {
                String format = "";
                do {
                    String number = String.format("%04d", num);
                    format = sparePartStockDTO.getMaterialCode() + number;
                    num = num + 1;
                } while (assemblyCodeSet.contains(format));
                strings.add(format);
            }
            String codes = strings.stream().collect(Collectors.joining(","));
            sparePart.setDeviceCode(sparePartStockDTO.getDeviceCode());
            sparePart.setNewSparePartSplitCode(codes);
            sparePart.setNewSparePartCode(codes);
        }
        sparePart.setNewSparePartNum(sparePartStockDTO.getNewSparePartNum());
        sparePart.setNewOrgCode(loginUser.getOrgCode());

        sparePart.setLendOrderId(sparePartStockDTO.getLendOrderId());
        sparePart.setLendOutOrderId(sparePartStockDTO.getLendOutOrderId());
        sparePart.setIntOrderId(sparePartStockDTO.getIntOrderId());
        sparePartService.getBaseMapper().insert(sparePart);
    }

    @Override
    public List<SparePartStockInfo> getCustodialStock() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departModels = sysBaseApi.getDepartByUserId(user.getId());
        List<String> orgCodes = null;
        if(!user.getRoleCodes().contains("admin")&&departModels.size()!=0){
            orgCodes = departModels.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        }
        //获取有权限的班组库信息
        List<SparePartStockInfo> stockInfoList = sparePartStockInfoMapper.selectList(new LambdaQueryWrapper<SparePartStockInfo>()
                .in(CollUtil.isNotEmpty(orgCodes), SparePartStockInfo::getOrgCode, orgCodes)
                .eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        return stockInfoList;
    }
}
