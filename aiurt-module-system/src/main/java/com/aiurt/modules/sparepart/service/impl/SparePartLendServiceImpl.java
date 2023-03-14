package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.SparePartLendMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
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
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    /**
     * 查询列表
     * @param page
     * @param sparePartLend
     * @return
     */
    @Override
    public List<SparePartLend> selectList(Page page, SparePartLend sparePartLend){
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
        //查询借出仓库
        SparePartStockInfo lendStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getWarehouseCode,sparePartLend.getLendWarehouseCode()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(null!=lendStockInfo){
            sparePartLend.setLendPerson(user.getUsername());
            sparePartLend.setCreateOrgCode(user.getOrgCode());
            sparePartLend.setEntryOrgCode(sysDepartService.getById(sparePartStockInfo.getOrganizationId()).getOrgCode());
            sparePartLend.setExitOrgCode(sysDepartService.getById(lendStockInfo.getOrganizationId()).getOrgCode());
            sparePartLendMapper.insert(sparePartLend);

            try {
                //根据仓库编号获取仓库组织机构code
                String orgCode = sysBaseApi.getDepartByWarehouseCode(sparePartLend.getLendWarehouseCode());
                String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));

                //发送通知
                MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件借出申请" + DateUtil.today(), null);

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
                /*messageDTO.setTemplateCode(CommonConstant.SPAREPARTLEND_SERVICE_NOTICE);
                SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
                messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                messageDTO.setMsgAbstract("备件借出申请");
                messageDTO.setPublishingContent("备件借出申请，请确认");
                messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
                sysBaseApi.sendTemplateMessage(messageDTO);*/
                //发送待办
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
                todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
                todoDTO.setTitle("备件借出申请" + DateUtil.today());
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
        //3.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartOutOrder.setNum(sparePartLend.getLendNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getLendPerson());
        sparePartOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2 );
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        sparePartOutOrderService.save(sparePartOutOrder);
        //4.借入仓库库存数做加法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        if(null!=backStock){
            backStock.setNum(backStock.getNum()+sparePartLend.getLendNum());
            sparePartStockMapper.updateById(backStock);
        }else{
            //插入库存
            SparePartStock stock = new SparePartStock();
            stock.setMaterialCode(partLend.getMaterialCode());
            stock.setNum(sparePartLend.getLendNum());
            stock.setWarehouseCode(partLend.getBackWarehouseCode());
            stock.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getLendPerson())).getOrgId());
            stock.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getLendPerson())).getOrgCode());
            sparePartStockMapper.insert(stock);
        }
        //5.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartInOrder.setNum(sparePartLend.getLendNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getLendPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
        sparePartInOrder.setConfirmId(user.getUsername());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrder.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getLendPerson())).getOrgCode());
        sparePartInOrderService.save(sparePartInOrder);

        try {
            LoginUser userById = sysBaseApi.getUserByName(partLend.getLendPerson());

            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件借出申请" + DateUtil.today(), null);

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
        sparePartStockMapper.updateById(lendStock);
        //3.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getLendWarehouseCode());
        sparePartInOrder.setNum(sparePartLend.getBackNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getBackPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
        sparePartInOrder.setConfirmId(user.getUsername());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrder.setSysOrgCode(user.getOrgCode());
        sparePartInOrderService.save(sparePartInOrder);
        //3.借入仓库库存数做减法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        backStock.setNum(backStock.getNum()-sparePartLend.getBackNum());
        sparePartStockMapper.updateById(backStock);
        //4.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartOutOrder.setNum(sparePartLend.getBackNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getBackPerson());
        sparePartOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2);
        sparePartOutOrder.setSysOrgCode(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getBackPerson())).getOrgCode());
        sparePartOutOrderService.save(sparePartOutOrder);
        try {
            LoginUser userById = sysBaseApi.getUserByName(partLend.getBackPerson());
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userById.getUsername(), "备件归还-确认" + DateUtil.today(), null);

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
