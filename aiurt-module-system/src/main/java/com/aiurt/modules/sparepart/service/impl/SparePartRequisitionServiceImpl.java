package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.mapper.MaterialRequisitionMapper;
import com.aiurt.modules.material.service.IMaterialRequisitionDetailService;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.entity.dto.SparePartRequisitionDetailDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionListReqDTO;
import com.aiurt.modules.sparepart.entity.dto.resp.SparePartRequisitionListRespDTO;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.sparepart.service.SparePartRequisitionService;
import com.aiurt.modules.stock.dto.StockLevel2RequisitionDetailDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.aiurt.modules.stock.mapper.StockLevel2InfoMapper;
import com.aiurt.modules.stock.mapper.StockLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockOutOrderLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockOutboundMaterialsMapper;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.stock.service.StockLevel2RequisitionService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private MaterialRequisitionMapper materialRequisitionMapper;
    @Autowired
    private IMaterialRequisitionDetailService materialRequisitionDetailService;
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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO,Integer applyType)  {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
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
            submitRequisition(applyType, materialRequisition, requisitionDetailList,sparePartRequisitionAddReqDTO);
        }
    }

    /**
     * 提交
     * */
    private void submitRequisition(Integer applyType, MaterialRequisition materialRequisition, List<MaterialRequisitionDetail> requisitionDetailList, SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (MaterialRequisitionConstant.APPLY_TYPE_NORMAL.equals(applyType)) {
            //三级库申领
            //1.修改状态为“待确认”
            materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_TO_BE_CONFIRMED);
            addStockOutOrderLevel2(materialRequisition, requisitionDetailList);
        }else {
            //维修申领
            if (MaterialRequisitionConstant.COMMIT_STATUS_SUBMITTED.equals(materialRequisition.getCommitStatus())) {
                //判断是否需要三级库申领，二级库申领
                List<StockLevel2RequisitionDetailDTO> level2RequisitionDetailDTOS = new ArrayList<StockLevel2RequisitionDetailDTO>();
                List<MaterialRequisitionDetail> materialRequisitionDetails = new ArrayList<MaterialRequisitionDetail>();

                for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetailList) {
                    //如果申请数量大于可使用数量，则证明需要向上一级库申领，
                    int i = materialRequisitionDetail.getApplyNum() - materialRequisitionDetail.getAvailableNum();
                    //维修申领产生三级库申领，三级库申领产生二级库申领
                    if (i > 0 && MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                        //二级库可使用数量
                        int availableNum = stockLevel2Mapper.getAvailableNum(materialRequisitionDetail.getMaterialsCode(), sparePartRequisitionAddReqDTO.getLeve2WarehouseCode());
                        MaterialRequisitionDetail requisitionDetailDTO = new MaterialRequisitionDetail();
                        BeanUtils.copyProperties(materialRequisitionDetail, requisitionDetailDTO);
                        requisitionDetailDTO.setApplyNum(i);
                        requisitionDetailDTO.setAvailableNum(availableNum);
                        materialRequisitionDetails.add(requisitionDetailDTO);

                        int applyNumber = i - availableNum;
                        if (applyNumber > 0) {
                            //如果二级库申领数量大于二级库库存，则需要向一级库申领，只要有一条要向一级库申领则所有出入库单需要等这个申领审核完成才能生成且确认
                            StockLevel2RequisitionDetailDTO stockLevel2RequisitionDetailDTO = new StockLevel2RequisitionDetailDTO();
                            BeanUtils.copyProperties(materialRequisitionDetail, stockLevel2RequisitionDetailDTO);
                            level2RequisitionDetailDTOS.add(stockLevel2RequisitionDetailDTO);
                        }
                    } else if (i > 0 && MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                        //三级库申领产生二级库申领
                        //如果二级库申领数量大于二级库库存，则需要向一级库申领，只要有一条要向一级库申领则所有出入库单需要等这个申领审核完成才能生成且确认
                        StockLevel2RequisitionDetailDTO stockLevel2RequisitionDetailDTO = new StockLevel2RequisitionDetailDTO();
                        BeanUtils.copyProperties(materialRequisitionDetail, stockLevel2RequisitionDetailDTO);
                        level2RequisitionDetailDTOS.add(stockLevel2RequisitionDetailDTO);
                    }
                }

                //二级库申领
                if (CollUtil.isNotEmpty(level2RequisitionDetailDTOS)) {
                    StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO = new StockLevel2RequisitionAddReqDTO();
                    BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, stockLevel2RequisitionAddReqDTO);
                    stockLevel2RequisitionAddReqDTO.setMaterialRequisitionPid(materialRequisition.getId());
                    //保管仓库为二级库
                    stockLevel2RequisitionAddReqDTO.setCustodialWarehouseCode(sparePartRequisitionAddReqDTO.getLeve2WarehouseCode());
                    stockLevel2RequisitionService.submit(stockLevel2RequisitionAddReqDTO);
                    return;
                }


                if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(sparePartRequisitionAddReqDTO.getMaterialRequisitionType())) {
                    //三级库申领
                    addLevel3Requisition(materialRequisitionDetails, sparePartRequisitionAddReqDTO, materialRequisition);

                    //生成三级库出库
                    addSparePartOutOrder(requisitionDetailList, loginUser, materialRequisition);
                } else {
                    //二级库出库
                    String outOrderCode = addStockOutOrderLevel2(materialRequisition, requisitionDetailList);
                    //三级库入库
                    addSparePartInOrder(requisitionDetailList, materialRequisition, outOrderCode);
                }

                //维修申领单变更为已完成,
                materialRequisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
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
    private void addLevel3Requisition(List<MaterialRequisitionDetail> materialRequisitionDetails, SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO, MaterialRequisition materialRequisition) {
        if (CollUtil.isNotEmpty(materialRequisitionDetails)) {
            MaterialRequisition requisition = new MaterialRequisition();
            BeanUtils.copyProperties(sparePartRequisitionAddReqDTO, requisition);
            requisition.setApplyWarehouseCode(sparePartRequisitionAddReqDTO.getLeve2WarehouseCode());
            requisition.setCustodialWarehouseCode(sparePartRequisitionAddReqDTO.getApplyWarehouseCode());
            requisition.setLeve2WarehouseCode(null);
            requisition.setStatus(MaterialRequisitionConstant.STATUS_COMPLETED);
            requisition.setMaterialRequisitionPid(materialRequisition.getId());
            // 先保存领料单
            materialRequisitionService.save(requisition);
            // 再保存物资清单
            for (MaterialRequisitionDetail materialRequisitionDetail : materialRequisitionDetails) {
                materialRequisitionDetail.setActualNum(materialRequisitionDetail.getApplyNum());
            }
            materialRequisitionDetailService.saveBatch(materialRequisitionDetails);

            //二级库出库
            String outOrderCode = addStockOutOrderLevel2(requisition, materialRequisitionDetails);
            //三级库入库
            addSparePartInOrder(materialRequisitionDetails, requisition, outOrderCode);
        }
    }


    /**
     * 二级库出库
     * */
    private String addStockOutOrderLevel2( MaterialRequisition materialRequisition, List<MaterialRequisitionDetail> requisitionDetailList){
        //三级库向二级库申领
        StockLevel2Info stockLevel2Info = stockLevel2InfoMapper.selectOne(new LambdaQueryWrapper<StockLevel2Info>().eq(StockLevel2Info::getDelFlag, CommonConstant.DEL_FLAG_0).eq(StockLevel2Info::getWarehouseCode, materialRequisition.getApplyWarehouseCode()));
        SysDepart sysDepart = iSysDepartService.getById(stockLevel2Info.getOrganizationId());

        materialRequisitionService.updateById(materialRequisition);
        //2.插入二级库出库表
        StockOutOrderLevel2 stockOutOrderLevel = new StockOutOrderLevel2();
        //生成出库单号
        String code = getStockOutCode();
        //EJCK+日期+自增3位
        stockOutOrderLevel.setOrderCode(code);
        //出库仓库为申领仓库
        stockOutOrderLevel.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
        //出库人为出库确认人，保管人为备件申领人
        stockOutOrderLevel.setCustodialId(materialRequisition.getApplyUserId());
        stockOutOrderLevel.setCustodialWarehouseCode(materialRequisition.getCustodialWarehouseCode());
        stockOutOrderLevel.setOrgCode(null != sysDepart ? sysDepart.getOrgCode() : null);
        stockOutOrderLevel.setApplyCode(materialRequisition.getCode());
        stockOutOrderLevel.setMaterialRequisitionId(materialRequisition.getId());
        stockOutOrderLevel.setOutType(MaterialRequisitionConstant.RETURN_LIBRARY);

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
        //3.插入出库物资
        requisitionDetailList.forEach(applyMaterial -> {
            StockOutboundMaterials stockOutboundMaterials = new StockOutboundMaterials();
            stockOutboundMaterials.setOutOrderCode(code);
            stockOutboundMaterials.setMaterialCode(applyMaterial.getMaterialsCode());
            stockOutboundMaterials.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
            //stockOutboundMaterials.setInventory();
            //计算库存结余
            if (equals) {
                stockOutboundMaterials.setActualOutput(stockOutboundMaterials.getApplyOutput());
                StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("material_code",stockOutOrderLevel.getWarehouseCode()).eq("warehouse_code",applyMaterial.getMaterialsCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
                stockLevel2.setNum(stockLevel2.getNum() - (null!=applyMaterial.getActualNum()?applyMaterial.getActualNum():1));
                stockLevel2.setAvailableNum(stockLevel2.getAvailableNum() - (null!=applyMaterial.getActualNum()?applyMaterial.getActualNum():1));
                //stockOutboundMaterials.setBalance(stockLevel2.getNum());

            }
            stockOutboundMaterials.setApplyOutput(applyMaterial.getApplyNum());
            stockOutboundMaterialsMapper.insert(stockOutboundMaterials);

            //同步出库记录到出入库记录表
        });

        return stockOutOrderLevel.getOrderCode();
    }

    /**
     * 生成三级库出库
     * */
    private void addSparePartOutOrder(List<MaterialRequisitionDetail> requisitionDetailList, LoginUser loginUser, MaterialRequisition materialRequisition) {
        for (MaterialRequisitionDetail materialRequisitionDetail : requisitionDetailList) {
            SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
            //sparePartOutOrder.setOrderCode();
            sparePartOutOrder.setMaterialCode(materialRequisitionDetail.getMaterialsCode());
            sparePartOutOrder.setWarehouseCode(materialRequisition.getApplyWarehouseCode());
            sparePartOutOrder.setSysOrgCode(loginUser.getOrgCode());
            sparePartOutOrder.setNum(materialRequisitionDetail.getApplyNum());
            sparePartOutOrder.setApplyOutTime(new Date());
            sparePartOutOrder.setApplyUserId(loginUser.getUsername());
            sparePartOutOrder.setConfirmTime(new Date());
            sparePartOutOrder.setConfirmUserId(loginUser.getId());
            sparePartOutOrder.setMaterialRequisitionId(materialRequisition.getId());
            sparePartOutOrder.setStatus(2);
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
                    .eq(SparePartStock::getMaterialCode, sparePartOutOrder.getWarehouseCode())
                    .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0));
            sparePartStock.setNum(sparePartStock.getNum()- sparePartOutOrder.getNum());

            //计算库存结余
            sparePartOutOrder.setBalance(sparePartStock.getNum());
            sparePartOutOrderMapper.insert(sparePartOutOrder);

            //更新库存数量
            sparePartStockMapper.updateById(sparePartStock);

            //同步出库记录到出入库记录表
        }
    }

    /**
     * 生成三级库入库
     *
     * @param materialRequisitionDetails
     * @param requisition
     * @param outOrderCode
     * */
    private void addSparePartInOrder(List<MaterialRequisitionDetail> materialRequisitionDetails, MaterialRequisition requisition, String outOrderCode) {
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

            sparePartInOrder.setOrgId(null!=sysDepart?sysDepart.getId():null);
            sparePartInOrder.setSysOrgCode(null!=sysDepart?sysDepart.getOrgCode():null);
            sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_1);
            sparePartInOrder.setOutOrderCode(outOrderCode);
            sparePartInOrder.setMaterialRequisitionId(requisition.getId());
            //全新数量等于入库数量
            sparePartInOrder.setNewNum(sparePartInOrder.getNum());
            updateSparePartStockNum(sparePartInOrder.getMaterialCode(), sparePartInOrder.getWarehouseCode(), sparePartInOrder.getNum());

            SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                    .eq(SparePartStock::getMaterialCode, materialRequisitionDetail.getMaterialsCode())
                    .eq(SparePartStock::getMaterialCode, sparePartInOrder.getWarehouseCode())
                    .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0));
            sparePartStock.setNum(sparePartStock.getNum()+sparePartInOrder.getNum());

            //入库结余
            sparePartInOrder.setBalance(sparePartStock.getNum());
            sparePartInOrderMapper.insert(sparePartInOrder);
            //更新库存数量
            sparePartStockMapper.updateById(sparePartStock);

            //同步入库记录到出入库记录表
        }
    }


    private void lendMaterial(SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO,LoginUser loginUser,SparePartStockInfo stockInfo) {
        List<SparePartRequisitionDetailDTO> requisitionDetailDTOList = sparePartRequisitionAddReqDTO.getSparePartRequisitionDetailDTOS();
        for (SparePartRequisitionDetailDTO sparePartRequisitionDetailDTO : requisitionDetailDTOList) {
            //1.生成借出单（已借出）
            SparePartLend sparePartLend = new SparePartLend();
            sparePartLend.setMaterialCode(sparePartRequisitionDetailDTO.getMaterialsCode());
            sparePartLend.setLendWarehouseCode(sparePartRequisitionAddReqDTO.getApplyWarehouseCode());
            sparePartLend.setBackWarehouseCode(sparePartRequisitionAddReqDTO.getCustodialWarehouseCode());

            sparePartLend.setEntryOrgCode(loginUser.getOrgCode());
            //查询借出仓库
            SparePartStockInfo lendStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getWarehouseCode,sparePartLend.getLendWarehouseCode()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
            sparePartLend.setExitOrgCode(sysDepartService.getById(lendStockInfo.getOrganizationId()).getOrgCode());
            sparePartLend.setOutTime(new Date());
            sparePartLend.setLendPerson(loginUser.getUsername());
            sparePartLend.setLendNum(sparePartRequisitionDetailDTO.getApplyNum());
            sparePartLend.setBorrowNum(sparePartRequisitionDetailDTO.getApplyNum());
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
            lendOutOrder.setMaterialCode(sparePartLend.getMaterialCode());
            lendOutOrder.setWarehouseCode(sparePartLend.getLendWarehouseCode());
            lendOutOrder.setSysOrgCode(loginUser.getOrgCode());
            lendOutOrder.setNum(sparePartLend.getLendNum());
            lendOutOrder.setConfirmTime(new Date());
            lendOutOrder.setConfirmUserId(loginUser.getUsername());
            lendOutOrder.setApplyOutTime(new Date());
            lendOutOrder.setApplyUserId(sparePartLend.getLendPerson());
            lendOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2);
            //计算库存结余
            lendOutOrder.setBalance(lendStock.getNum());

            List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                    .eq(SparePartOutOrder::getStatus,2)
                    .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(SparePartOutOrder::getMaterialCode,lendOutOrder.getMaterialCode())
                    .eq(SparePartOutOrder::getWarehouseCode,lendOutOrder.getWarehouseCode()));
            if(!orderList.isEmpty()){
                lendOutOrder.setUnused(orderList.get(0).getUnused());
            }
            sparePartOutOrderMapper.insert(lendOutOrder);

            //5.因为直接借入然后出库，所以借入仓库库存数做不变,可使用数量不变
            //但有可能之前不存在该物资的库存记录，需要新增备件库存数量记录表，所有数量为0
            updateSparePartStockNum(sparePartLend.getMaterialCode(), stockInfo.getWarehouseCode(), sparePartLend.getLendNum());
            SparePartStock  borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                    .eq(SparePartStock::getMaterialCode, lendOutOrder.getMaterialCode())
                    .eq(SparePartStock::getWarehouseCode, stockInfo.getWarehouseCode()));
            int num = null != borrowingStock ? borrowingStock.getNum() : 0;
            //6.借入仓库生成入库记录
            SparePartInOrder sparePartInOrder = new SparePartInOrder();
            sparePartInOrder.setMaterialCode(lendOutOrder.getMaterialCode());
            sparePartInOrder.setWarehouseCode(stockInfo.getWarehouseCode());
            sparePartInOrder.setNum(lendOutOrder.getNum());
            sparePartInOrder.setNewNum(lendOutOrder.getNum());
            sparePartInOrder.setOrgId(loginUser.getOrgId());
            sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
            sparePartInOrder.setConfirmId(loginUser.getUsername());
            sparePartInOrder.setConfirmTime(new Date());
            sparePartInOrder.setSysOrgCode(loginUser.getOrgCode());
            //计算库存结余
            sparePartInOrder.setBalance(num + lendOutOrder.getNum());
            sparePartInOrderMapper.insert(sparePartInOrder);
            
            

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
    public void edit(List<SparePartRequisitionAddReqDTO> sparePartRequisitionAddReqDTOs,Integer applyType){
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        for (SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO : sparePartRequisitionAddReqDTOs) {
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
                submitRequisition(applyType, materialRequisition, requisitionDetailList,sparePartRequisitionAddReqDTO);
            }
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
        //申领单类型查询
        Integer materialRequisitionType = sparePartRequisitionListReqDTO.getMaterialRequisitionType();
        queryWrapper.lambda().eq(ObjectUtil.isNotNull(materialRequisitionType), MaterialRequisition::getMaterialRequisitionType, materialRequisitionType);

        queryWrapper.lambda().eq( MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0);
        // 排序
        queryWrapper.lambda().orderByDesc(MaterialRequisition::getPlanApplyTime).orderByDesc(MaterialRequisition::getId);

        materialRequisitionService.page(page, queryWrapper);

        // 将查询结果转化成响应DTO返回
        Page<SparePartRequisitionListRespDTO> pageList = new Page<>();
        BeanUtils.copyProperties(page, pageList);
        //todo 查询仓库名称
        List<SparePartRequisitionListRespDTO> respRecords = page.getRecords().stream().map(record -> {
            SparePartRequisitionListRespDTO respDTO = new SparePartRequisitionListRespDTO();
            BeanUtils.copyProperties(record, respDTO);
            return respDTO;
        }).collect(Collectors.toList());
        pageList.setRecords(respRecords);
        return pageList;
    }

    /**
     * 生成出库单号
     * @param
     * @return
     */
    public String getStockOutCode() {
        LambdaQueryWrapper<StockOutOrderLevel2> queryWrapper = new LambdaQueryWrapper<>();
        String str = "EJCK";
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        str += date.format(new Date());
        queryWrapper.eq(StockOutOrderLevel2::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.likeRight(StockOutOrderLevel2::getOrderCode,str);
        queryWrapper.orderByDesc(StockOutOrderLevel2::getCreateTime);
        queryWrapper.last("limit 1");
        StockOutOrderLevel2 orderLevel2 = stockOutOrderLevel2Mapper.selectOne(queryWrapper);
        String format = "";
        if(orderLevel2 != null){
            String code = orderLevel2.getOrderCode();
            String numstr = code.substring(code.length()-3);
            format = String.format("%03d", Long.parseLong(numstr) + 1);
        }else{
            format = "001";
        }
        String code = str + format;
        return code;
    }
}
