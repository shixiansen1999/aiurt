package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.CodeGenerateUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.SparePartLendMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockNumMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.service.impl.MaterialStockOutInRecordServiceImpl;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartLendServiceImpl extends ServiceImpl<SparePartLendMapper, SparePartLend> implements ISparePartLendService {
    @Autowired
    private SparePartLendMapper sparePartLendMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private ISparePartStockService iSparePartStockService;
    @Autowired
    private ISparePartOutOrderService sparePartOutOrderService;
    @Autowired
    private ISparePartInOrderService sparePartInOrderService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private MaterialStockOutInRecordServiceImpl materialStockOutInRecordService;
    /**
     * 查询列表
     * @param page
     * @param sparePartLend
     * @return
     */
    @Override
    public List<SparePartLend> selectList(Page page, SparePartLend sparePartLend){
        //权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departModels = sysBaseApi.getDepartByUserId(user.getId());
        if(!user.getRoleCodes().contains("admin")&&departModels.size()==0){
            return CollUtil.newArrayList();
        }
        if(!user.getRoleCodes().contains("admin")&&departModels.size()!=0){
            List<String> orgCodes = departModels.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            List<SparePartStockInfo> stockInfoList = sparePartStockInfoMapper.selectList(new LambdaQueryWrapper<SparePartStockInfo>().in(SparePartStockInfo::getOrgCode, orgCodes));
            if(ObjectUtil.isEmpty(stockInfoList)){
                return CollUtil.newArrayList();
            }
            List<String> wareHouses = stockInfoList.stream().map(SparePartStockInfo::getWarehouseCode).collect(Collectors.toList());
            sparePartLend.setWareHouses(wareHouses);
        }
        return sparePartLendMapper.readAll(page,sparePartLend);
    }
    /**
     * 查询列表
     * @param sparePartLend
     * @return
     */
    @Override
    public List<SparePartLend> selectListById( SparePartLend sparePartLend){
        return sparePartLendMapper.readAll(sparePartLend);
    }
    /**
     * 添加
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询借入仓库
        SparePartStockInfo sparePartStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getOrganizationId,user.getOrgId()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(null!=sparePartStockInfo){
            sparePartLend.setBackWarehouseCode(sparePartStockInfo.getWarehouseCode());
        }

        //2.借出仓库库可使用数量做减法
        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,sparePartLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,sparePartLend.getLendWarehouseCode()));
        lendStock.setAvailableNum(lendStock.getAvailableNum()-sparePartLend.getBorrowNum());
        sparePartStockMapper.updateById(lendStock);

        //查询借出仓库
        SparePartStockInfo lendStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getWarehouseCode,sparePartLend.getLendWarehouseCode()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(null!=lendStockInfo){
            sparePartLend.setLendPerson(user.getId());
            sparePartLend.setCreateOrgCode(user.getOrgCode());
            sparePartLend.setEntryOrgCode(sysDepartService.getById(sparePartStockInfo.getOrganizationId()).getOrgCode());
            sparePartLend.setExitOrgCode(sysDepartService.getById(lendStockInfo.getOrganizationId()).getOrgCode());
            sparePartLendMapper.insert(sparePartLend);

            try {
                //根据仓库编号获取仓库组织机构code
                String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartLend.getLendWarehouseCode());
                String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

                //发送通知
                MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件借出-确认" + DateUtil.today(), null);

                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartLend.getId());
                map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND.getType());
                map.put("materialCode",sparePartLend.getMaterialCode());
                String materialName= sysBaseApi.getMaterialNameByCode(sparePartLend.getMaterialCode());
                map.put("name",materialName);
                map.put("lendNum",sparePartLend.getBorrowNum());
                String warehouseName= sysBaseApi.getWarehouseNameByCode(sparePartLend.getLendWarehouseCode());
                map.put("warehouseName",warehouseName);

                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                //发送待办
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
                todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
                todoDTO.setTitle("备件借出-确认" + DateUtil.today());
                todoDTO.setMsgAbstract("备件借出申请");
                todoDTO.setPublishingContent("备件借出申请，请确认");
                todoDTO.setCurrentUserName(userName);
                todoDTO.setBusinessKey(sparePartLend.getId());
                todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_LEND.getType());
                todoDTO.setCurrentUserName(userName);
                todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_LEND.getType());
                todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                todoDTO.setTemplateCode(CommonConstant.SPAREPARTLEND_SERVICE_NOTICE);

                isTodoBaseAPI.createTodoTask(todoDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.OK("添加成功！");
        }else{
            return Result.error("当前所在班组没有备件仓库！");
        }
    }
    /**
     * 借出确认
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> lendConfirm(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        SparePartLend partLend = getById(sparePartLend.getId());
        //1.更新借出数量、更改状态为“已借”
        sparePartLend.setOutTime(date);
        updateById(sparePartLend);
        //2.借出仓库库存数做减法
        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getLendWarehouseCode()));
        lendStock.setNum(lendStock.getNum()-sparePartLend.getLendNum());
        sparePartStockMapper.updateById(lendStock);
        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, partLend.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, partLend.getLendWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
        if (ObjectUtil.isNotNull(stockNum)) {
            Integer newNum = stockNum.getNewNum();
            //如果全新数量小于借出数量，则从已使用数量中扣除
            if (newNum < sparePartLend.getLendNum()) {
                stockNum.setNewNum(0);
                stockNum.setUsedNum(stockNum.getUsedNum() - (sparePartLend.getLendNum() - newNum));
            } else {
                stockNum.setNewNum(newNum - sparePartLend.getLendNum());
            }
            sparePartStockNumMapper.updateById(stockNum);
        }

        //3.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getLendWarehouseCode());
        sparePartOutOrder.setNum(sparePartLend.getLendNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getId());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getLendPerson());
        sparePartOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2 );
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        sparePartOutOrder.setOutType(MaterialRequisitionConstant.BORROW_OUT);
        sparePartOutOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3CK", 5));
        //计算库存结余
        sparePartOutOrder.setBalance(lendStock.getNum());
        sparePartOutOrderService.save(sparePartOutOrder);

        //同步出库记录到出入库记录表
        MaterialStockOutInRecord record = new MaterialStockOutInRecord();
        BeanUtils.copyProperties(sparePartOutOrder, record);
        record.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3);
        record.setIsOutIn(2);
        //带负号表示出库
        record.setNum(-record.getNum());
        record.setOutInType(sparePartOutOrder.getOutType());
        materialStockOutInRecordService.save(record);


        //4.借入仓库库存数做加法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper2.eq(SparePartStockNum::getMaterialCode, partLend.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, partLend.getBackWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum2 = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper2);

        if(null!=backStock){
            backStock.setNum(backStock.getNum()+sparePartLend.getLendNum());
            backStock.setAvailableNum(backStock.getAvailableNum()+sparePartLend.getLendNum());
            sparePartStockMapper.updateById(backStock);
        }else{
            //插入库存
            SparePartStock stock = new SparePartStock();
            stock.setMaterialCode(partLend.getMaterialCode());
            stock.setNum(sparePartLend.getLendNum());
            stock.setAvailableNum(sparePartLend.getLendNum());
            stock.setWarehouseCode(partLend.getBackWarehouseCode());
            stock.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getLendPerson())).getOrgId());
            stock.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getLendPerson())).getOrgCode());
            sparePartStockMapper.insert(stock);
        }
        if (null!=stockNum2) {
            stockNum2.setNewNum(stockNum2.getNewNum() + sparePartLend.getLendNum());
            sparePartStockNumMapper.updateById(stockNum2);
        }else {
            SparePartStockNum partStockNum = new SparePartStockNum();
            partStockNum.setMaterialCode(partLend.getMaterialCode());
            partStockNum.setWarehouseCode(partLend.getBackWarehouseCode());
            // 新增已使用数量
            partStockNum.setNewNum(sparePartLend.getLendNum());
            sparePartStockNumMapper.insert(partStockNum);
        }
        //5.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartInOrder.setNum(sparePartLend.getLendNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getLendPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
        sparePartInOrder.setConfirmId(user.getId());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrder.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getLendPerson())).getOrgCode());
        sparePartInOrder.setNewNum(sparePartLend.getLendNum());
        sparePartInOrder.setOrderCode(CodeGenerateUtils.generateSingleCode("3RK", 5));
        sparePartInOrder.setInType(MaterialRequisitionConstant.BORROW_IN);
        //计算库存结余
        int num = null != backStock ? backStock.getNum() : 0;
        sparePartInOrder.setBalance(num + sparePartInOrder.getNum());
        sparePartInOrderService.save(sparePartInOrder);

        //同步入库记录到出入库记录表
        MaterialStockOutInRecord record2 = new MaterialStockOutInRecord();
        BeanUtils.copyProperties(sparePartInOrder, record2);
        record2.setConfirmUserId(user.getId());
        record2.setMaterialRequisitionType(MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3);
        record2.setIsOutIn(1);
        record2.setOutInType(sparePartInOrder.getInType());
        materialStockOutInRecordService.save(record2);

        try {
            LoginUser userById = sysBaseApi.getUserById(partLend.getLendPerson());

            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件借出成功" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, partLend.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND.getType());
            map.put("materialCode",partLend.getMaterialCode());
            String materialName= sysBaseApi.getMaterialNameByCode(partLend.getMaterialCode());
            map.put("name",materialName);
            map.put("lendNum",sparePartLend.getLendNum());
            String warehouseName= sysBaseApi.getWarehouseNameByCode(partLend.getLendWarehouseCode());
            map.put("warehouseName",warehouseName);

            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            messageDTO.setTemplateCode(CommonConstant.SPAREPARTLEND_SERVICE_NOTICE);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("备件借出申请确认");
            messageDTO.setPublishingContent("备件借出申请通过");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
            sysBaseApi.sendTemplateMessage(messageDTO);
            // 更新待办
            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_LEND.getType(), partLend.getId(), user.getUsername(), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.OK("操作成功！");
    }
    /**
     * 归还确认
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> backConfirm(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        SparePartLend partLend = getById(sparePartLend.getId());
        //1.更新归还数量、更改状态为“已完结”
        updateById(sparePartLend);
        //2.借出仓库库存数做加法
        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getLendWarehouseCode()));
        lendStock.setNum(lendStock.getNum()+sparePartLend.getBackNum());
        lendStock.setAvailableNum(lendStock.getAvailableNum()+sparePartLend.getBackNum());
        sparePartStockMapper.updateById(lendStock);

        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, partLend.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, partLend.getLendWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);

        stockNum.setNewNum(stockNum.getNewNum() + sparePartLend.getBackNum());
        sparePartStockNumMapper.updateById(stockNum);
        //3.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getLendWarehouseCode());
        sparePartInOrder.setNum(sparePartLend.getBackNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getBackPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
        sparePartInOrder.setConfirmId(user.getId());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrder.setSysOrgCode(user.getOrgCode());
        sparePartInOrder.setNewNum(sparePartLend.getBackNum());
        sparePartInOrderService.save(sparePartInOrder);
        //3.借入仓库库存数做减法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        backStock.setNum(backStock.getNum()-sparePartLend.getBackNum());
        sparePartStockMapper.updateById(backStock);
        //先获取该备件的数量记录,更新全新数量
        LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        numLambdaQueryWrapper2.eq(SparePartStockNum::getMaterialCode, partLend.getMaterialCode())
                .eq(SparePartStockNum::getWarehouseCode, partLend.getBackWarehouseCode())
                .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
        SparePartStockNum stockNumBack = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper2);

        if (ObjectUtil.isNotNull(stockNumBack)) {
            Integer newNum = stockNumBack.getNewNum();
            //如果全新数量小于归还数量，则从已使用数量中扣除
            if (newNum < sparePartLend.getBackNum()) {
                stockNumBack.setNewNum(0);
                stockNumBack.setUsedNum(stockNumBack.getUsedNum() - (sparePartLend.getBackNum() - newNum));
            } else {
                stockNumBack.setNewNum(newNum - sparePartLend.getBackNum());
            }
            sparePartStockNumMapper.updateById(stockNumBack);
        }

        //4.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartOutOrder.setNum(sparePartLend.getBackNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getId());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getBackPerson());
        sparePartOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2);
        sparePartOutOrder.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId,partLend.getBackPerson())).getOrgCode());
        sparePartOutOrderService.save(sparePartOutOrder);
        try {
            LoginUser userById = sysBaseApi.getUserById(partLend.getBackPerson());
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件归还成功" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, partLend.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_RETURN.getType());
            map.put("materialCode",partLend.getMaterialCode());
            String materialName= sysBaseApi.getMaterialNameByCode(partLend.getMaterialCode());
            map.put("name",materialName);
            map.put("backNum",sparePartLend.getBackNum());
            String warehouseName= sysBaseApi.getWarehouseNameByCode(partLend.getLendWarehouseCode());
            map.put("warehouseName",warehouseName);

            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            messageDTO.setTemplateCode(CommonConstant.SPAREPARTBACK_SERVICE_NOTICE);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("备件归还申请确认");
            messageDTO.setPublishingContent("备件归还申请通过");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
            sysBaseApi.sendTemplateMessage(messageDTO);
            // 更新待办
            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_LEND_RETURN.getType(), partLend.getId(), user.getUsername(), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.OK("操作成功！");
    }

    @Override
    public Result<?> check() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询借入仓库
        SparePartStockInfo sparePartStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getOrganizationId,user.getOrgId()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(sparePartStockInfo==null){
            throw new AiurtBootException("当前所在班组没有备件仓库");
        }
        return Result.ok("成功");
    }
}
