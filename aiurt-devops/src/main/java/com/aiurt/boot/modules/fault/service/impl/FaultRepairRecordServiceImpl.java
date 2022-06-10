package com.aiurt.boot.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.FaultLevelEnum;
import com.aiurt.boot.common.enums.ProcessLinkEnum;
import com.aiurt.boot.common.enums.RepairWayEnum;
import com.aiurt.boot.common.enums.SolveStatusEnum;
import com.aiurt.boot.common.result.FaultRepairRecordResult;
import com.aiurt.boot.common.result.SpareResult;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.appMessage.constant.MessageConstant;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.param.UserTaskAddParam;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.fault.dto.FaultRepairDTO;
import com.aiurt.boot.modules.fault.dto.FaultRepairRecordDTO;
import com.aiurt.boot.modules.fault.dto.SparePartDTO;
import com.aiurt.boot.modules.fault.entity.*;
import com.aiurt.boot.modules.fault.mapper.*;
import com.aiurt.boot.modules.fault.param.AssignParam;
import com.aiurt.boot.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.boot.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.fault.service.IOutsourcingPersonnelService;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartScrapService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.mapper.SysUserMapper;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
    private DeviceChangeSparePartMapper partMapper;

    @Resource
    private IDeviceChangeSparePartService partService;

    @Resource
    private ISparePartScrapService sparePartScrapService;

    @Resource
    private IMaterialBaseService iMaterialBaseService;

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private OperationProcessMapper processMapper;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private IFaultService faultService;

    @Resource
    private IOutsourcingPersonnelService personnelService;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private IStationService stationService;

    @Resource
    private IMessageService messageService;

    @Resource
    private ISparePartStockService partStockService;


    /**
     * 指派
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assign(AssignParam param) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据code查询故障表数据
        Fault fault = faultService.getOne(new QueryWrapper<Fault>().eq(Fault.CODE,param.getCode()),false);
        FaultRepairRecord faultRepairRecord = new FaultRepairRecord();
        faultRepairRecord.setFaultCode(param.getCode());
        faultRepairRecord.setAppointUserId(param.getAppointUserId());
        faultRepairRecord.setWorkType(param.getWorkType());
        faultRepairRecord.setStatus(0);
        faultRepairRecord.setSolveStatus(2);
        if (StringUtils.isNotBlank(param.getPlanOrderCode())) {
            faultRepairRecord.setPlanOrderCode(param.getPlanOrderCode());
        }
        if (StringUtils.isNotBlank(param.getPlanOrderImg())) {
            faultRepairRecord.setPlanOrderImg(param.getPlanOrderImg());
        }
        faultRepairRecord.setFaultPhenomenon(fault.getFaultPhenomenon());
        faultRepairRecordMapper.insert(faultRepairRecord);

        //发送待办消息
        UserTaskAddParam addParam = new UserTaskAddParam();
        List<String> list = new ArrayList<>();
        list.add(param.getAppointUserId());
        addParam.setUserIds(list);
        addParam.setType(UserTaskConstant.USER_TASK_TYPE_3);
        addParam.setRecordCode(param.getCode());
        if (StringUtils.isNotBlank(fault.getStationCode())) {
            Station one = stationService.getOne(new QueryWrapper<Station>().eq(Station.STATION_CODE, fault.getStationCode()), false);
            addParam.setContent(one.getStationName());
        }
        addParam.setTitle("故障");
        addParam.setWorkCode(param.getCode());
        addParam.setProductionTime(fault.getCreateTime());
        if (fault.getFaultLevel()== FaultLevelEnum.PTGZ.getCode() && RepairWayEnum.ZJ.getMessage().equals(fault.getRepairWay())){
            addParam.setLevel(3);
        }else if (fault.getFaultLevel()==FaultLevelEnum.PTGZ.getCode() && RepairWayEnum.BX.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(4);
        }else if (fault.getFaultLevel()==FaultLevelEnum.ZDGZ.getCode() && RepairWayEnum.ZJ.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(5);
        }else if (fault.getFaultLevel()==FaultLevelEnum.ZDGZ.getCode() && RepairWayEnum.BX.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(6);
        }
        userTaskService.add(addParam);

        //发送app消息
        Message message = new Message();
        message.setTitle("消息通知").setContent("您有一条新的故障维修任务!").setType(MessageConstant.MESSAGE_TYPE_0).setCode(addParam.getRecordCode());
        messageService.addMessage(MessageAddParam.builder().message(message).userIds(addParam.getUserIds()).build());

        //给予登记人发送app消息
        if (fault.getStatus().equals(0)) {
            Message toRankMessage = new Message();
            toRankMessage.setTitle("消息通知").setContent("您提报的故障已被指派,故障编号:" + fault.getCode()).setType(MessageConstant.MESSAGE_TYPE_0).setCode(addParam.getRecordCode());
            messageService.addMessage(MessageAddParam.builder().message(toRankMessage).userIds(Arrays.asList(fault.getCreateBy())).build());
        }

        //更改指派状态为 1-已指派未填写
        faultMapper.assignByCode(param.getCode());
        OperationProcess process = new OperationProcess();
        process.setFaultCode(fault.getCode());
        process.setProcessCode(1);
        process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
        process.setProcessPerson(user.getId());
        process.setProcessTime(new Date());
        processMapper.insert(process);
        return Result.ok();
    }

    /**
     * 重新指派
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assignAgain(AssignParam param) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据code查询故障表数据
        Fault fault = faultService.getOne(new QueryWrapper<Fault>().eq(Fault.CODE,param.getCode()),false);
        faultRepairRecordMapper.update(new FaultRepairRecord().setDelFlag(1),new LambdaQueryWrapper<FaultRepairRecord>()
                .eq(FaultRepairRecord::getFaultCode,param.getCode()));

        FaultRepairRecord faultRepairRecord = new FaultRepairRecord();
        faultRepairRecord.setFaultCode(param.getCode());
        faultRepairRecord.setAppointUserId(param.getAppointUserId());
        faultRepairRecord.setWorkType(param.getWorkType());
        faultRepairRecord.setStatus(0);
        faultRepairRecord.setSolveStatus(2);
        if (StringUtils.isNotBlank(param.getPlanOrderCode())) {
            faultRepairRecord.setPlanOrderCode(param.getPlanOrderCode());
        }
        if (StringUtils.isNotBlank(param.getPlanOrderImg())) {
            faultRepairRecord.setPlanOrderImg(param.getPlanOrderImg());
        }
        faultRepairRecord.setFaultPhenomenon(fault.getFaultPhenomenon());
        faultRepairRecordMapper.insert(faultRepairRecord);

        //删除原指记录
        List<String> list = new ArrayList<>();
        list.add(param.getAppointUserId());
        userTaskService.removeUserTaskWork(param.getCode(),UserTaskConstant.USER_TASK_TYPE_3);

        //删除原来指派待办&重新发送待办消息
        UserTaskAddParam addParam = new UserTaskAddParam();
        addParam.setUserIds(list);
        addParam.setType(UserTaskConstant.USER_TASK_TYPE_3);
        addParam.setRecordCode(param.getCode());
        if (StringUtils.isNotBlank(fault.getStationCode())) {
            Station one = stationService.getOne(new QueryWrapper<Station>().eq(Station.STATION_CODE, fault.getStationCode()), false);
            addParam.setContent(one.getStationName());
        }
        addParam.setTitle("故障");
        addParam.setWorkCode(param.getCode());
        addParam.setProductionTime(fault.getCreateTime());
        if (fault.getFaultLevel()== FaultLevelEnum.PTGZ.getCode() && RepairWayEnum.ZJ.getMessage().equals(fault.getRepairWay())){
            addParam.setLevel(3);
        }else if (fault.getFaultLevel()==FaultLevelEnum.PTGZ.getCode() && RepairWayEnum.BX.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(4);
        }else if (fault.getFaultLevel()==FaultLevelEnum.ZDGZ.getCode() && RepairWayEnum.ZJ.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(5);
        }else if (fault.getFaultLevel()==FaultLevelEnum.ZDGZ.getCode() && RepairWayEnum.BX.getMessage().equals(fault.getRepairWay())) {
            addParam.setLevel(6);
        }
        userTaskService.add(addParam);


        //发送app消息
        Message message = new Message();
        message.setTitle("消息通知").setContent("您有一条新的故障维修任务!").setType(MessageConstant.MESSAGE_TYPE_0).setCode(addParam.getRecordCode());
        messageService.addMessage(MessageAddParam.builder().message(message).userIds(addParam.getUserIds()).build());

        //更改指派状态为2
        faultMapper.assignAgain(param.getCode());
        OperationProcess process = new OperationProcess();
        process.setFaultCode(fault.getCode());
        process.setProcessCode(2);
        process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
        process.setProcessPerson(user.getId());
        process.setProcessTime(new Date());
        processMapper.insert(process);
        return Result.ok();
    }

    /**
     * 根据code查询故障维修记录
     * @param code
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FaultRepairRecordResult> getRepairRecord(String code) {
        List<FaultRepairRecordResult> query = faultRepairRecordMapper.queryDetail(code);
        for (FaultRepairRecordResult result : query) {
            //普通附件
            List<String> query1 = repairRecordEnclosureMapper.queryDetail(result.getId(),0);
            //签名
            List<String> query2 = repairRecordEnclosureMapper.queryDetail(result.getId(),1);
            SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, result.getAppointUserId()), false);
            if (ObjectUtil.isNotEmpty(sysUser)) {
                result.setAppointUserName(sysUser.getRealname());
            }
            if (StringUtils.isNotBlank(result.getParticipateIds())) {
                List<String> name = new ArrayList<String>();
                List<String> ids = Arrays.asList(result.getParticipateIds().split(","));
                List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getId,ids).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .select(SysUser::getRealname));
                for (SysUser user : userList) {
                    name.add(user.getRealname());
                }
                String str = StringUtils.join(name, ",");
                result.setParticipateNames(str);
            }

            if (StringUtils.isNotBlank(result.getOutsourcingIds())) {
                List<String> ids = Arrays.asList(result.getOutsourcingIds().split(","));
                List<OutsourcingPersonnel> list = personnelService.list(new LambdaQueryWrapper<OutsourcingPersonnel>()
                        .in(OutsourcingPersonnel::getId, ids).eq(OutsourcingPersonnel::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .select(OutsourcingPersonnel::getName));
                List<String> stringList = list.stream().map(OutsourcingPersonnel::getName).collect(Collectors.toList());
                String str = StringUtils.join(stringList, ",");
                result.setOutsourcingNames(str);
            }
            if (result.getSolveStatus()!=null) {
                result.setSolveStatusDesc(SolveStatusEnum.findMessage(result.getSolveStatus()));
            }
            Fault fault = faultService.getOne(new QueryWrapper<Fault>().eq(Fault.CODE,code),false);
            if (fault!=null){
                result.setStationCode(fault.getStationCode());
                result.setLineCode(fault.getLineCode());
                result.setSystemCode(fault.getSystemCode());
            }
            if (StringUtils.isNotBlank(fault.getDevicesIds())) {
                List<String> codes = Arrays.asList(fault.getDevicesIds().split(","));
                List<Device> list = deviceService.list(new LambdaQueryWrapper<Device>().in(Device::getCode, codes).eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0).select(Device::getName));
                List<String> collect = list.stream().map(Device::getName).collect(Collectors.toList());
                result.setDeviceList(collect);
            }
            if (CollUtil.isNotEmpty(query1)) {
                result.setUrlList(query1);
            }
            if (CollUtil.isNotEmpty(query2)) {
                result.setSignature(query2);
            }
            List<DeviceChangeSparePart> list = partService.list(new QueryWrapper<DeviceChangeSparePart>()
                    .eq(DeviceChangeSparePart.CODE, result.getFaultCode())
                    .eq(DeviceChangeSparePart.TYPE, 2)
                    .eq(DeviceChangeSparePart.DEL_FLAG,CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(list)) {
                result.setList(list);
            }
        }
        return query;
    }

    /**
     * app添加故障维修记录
     * @param dto
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result addRecord(FaultRepairRecordDTO dto,HttpServletRequest req) {
        //更改故障维修记录表
        FaultRepairRecord record = new FaultRepairRecord();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();

        record.setId(dto.getId());
        record.setParticipateIds(dto.getParticipateIds());
        if (StringUtils.isNotBlank(dto.getOutsourcingIds())) {
            record.setOutsourcingIds(dto.getOutsourcingIds());
        }
        record.setOverTime(dto.getOverTime());
        record.setFaultAnalysis(dto.getFaultAnalysis());
        record.setMaintenanceMeasures(dto.getMaintenanceMeasures());
        record.setSolveStatus(dto.getSolveStatus());
        record.setFaultTime(dto.getCreateTime());
        record.setOverTime(dto.getOverTime());
        record.setCreateBy(userId);
        record.setStatus(1);
        record.setRemark(dto.getRemark());

        faultRepairRecordMapper.updateById(record);
        //修改问题解决状态
        if (dto.getSolveStatus() == SolveStatusEnum.YJJ.getCode()) {
            faultMapper.updateStatus(dto.getFaultCode());
            userTaskService.complete(userId,dto.getFaultCode(), UserTaskConstant.USER_TASK_TYPE_3);
        }

        //删除附件
        repairRecordEnclosureMapper.deleteByName(dto.getId());
        //重新插入附件表
        if (CollUtil.isNotEmpty(dto.getUrlList())) {
            RepairRecordEnclosure enclosure = new RepairRecordEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
                enclosure.setRepairRecordId(dto.getId());
                if (record.getCreateBy()!=null) {
                    enclosure.setUpdateBy(record.getUpdateBy());
                }
                enclosure.setType(0);
                enclosure.setUpdateBy(dto.getCreateBy());
                enclosure.setDelFlag(0);
                enclosure.setUrl(s);
                repairRecordEnclosureMapper.insert(enclosure);
            }
        }
        //删除更换备件记录
        partService.lambdaUpdate().eq(DeviceChangeSparePart::getRepairRecordId,dto.getId()).remove();
        //插入故障更换备件表
        if (CollUtil.isNotEmpty(dto.getList())) {
            List<SparePartDTO> list = dto.getList();

            List<DeviceChangeSparePart> partList = new ArrayList<>();
            List<SparePartScrap> sparePartList = new ArrayList<>();
            List<SparePartStock> updateStockList = new ArrayList<>();

            //库存
            Map<String, SparePartStock> stockMap = null;
            List<SparePartStock> partStockList = partStockService.lambdaQuery().eq(SparePartStock::getOrgId, user.getOrgId()).eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0).list();
            if (partStockList!=null){
                 stockMap = partStockList.stream().collect(Collectors.toMap(SparePartStock::getMaterialCode, p -> p));
            }


            for (SparePartDTO sparePartDTO : list) {
                DeviceChangeSparePart part = new DeviceChangeSparePart();
                part.setCode(dto.getFaultCode());
                part.setType(2);
                part.setRepairRecordId(record.getId());
                part.setDeviceId(sparePartDTO.getDeviceId());
                part.setOldSparePartCode(sparePartDTO.getOldSparePartCode());
                part.setOldSparePartNum(sparePartDTO.getOldSparePartNum());
                part.setNewSparePartCode(sparePartDTO.getOldSparePartCode());
                part.setNewSparePartNum(sparePartDTO.getOldSparePartNum());
                part.setDelFlag(0);
                part.setCreateBy(userId);
                partList.add(part);
                //调用备件出库接口  暂不调用
//                iSparePartOutOrderService.addByFault(result,part.getId());
                //插入备件报损
                SparePartScrap sparePartScrap = new SparePartScrap();
                sparePartScrap.setMaterialCode(sparePartDTO.getOldSparePartCode());
                sparePartScrap.setNum(sparePartDTO.getOldSparePartNum());
                sparePartScrap.setStatus(0);
                sparePartScrap.setScrapTime(new Date());
                sparePartScrap.setReason("故障更换备件报损");
                sparePartScrap.setDelFlag(0);
                sparePartList.add(sparePartScrap);

                //处理班组数量
                if (sparePartDTO.getOldSparePartNum()!=null) {
                    if (stockMap != null && stockMap.get(sparePartDTO.getOldSparePartCode()) != null) {
                        SparePartStock stock = stockMap.get(sparePartDTO.getOldSparePartCode());
                        stock.setNum(stock.getNum() != null ? stock.getNum() - sparePartDTO.getOldSparePartNum() : -sparePartDTO.getOldSparePartNum());
                        updateStockList.add(stock);
                    } else {
                        SparePartStock stock = new SparePartStock();
                        stock.setOrgId(user.getOrgId()).setMaterialCode(sparePartDTO.getOldSparePartCode()).setDelFlag(0);
                        stock.setNum(-sparePartDTO.getOldSparePartNum());
                        updateStockList.add(stock);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(partList)) {
                partService.saveBatch(partList);
            }
            if (CollectionUtils.isNotEmpty(sparePartList)) {
                sparePartScrapService.saveBatch(sparePartList);
            }
            if (CollectionUtils.isNotEmpty(updateStockList)){
                partStockService.saveOrUpdateBatch(updateStockList);
            }
        }

        //插入签名
        if (StringUtils.isNotBlank(dto.getSignature())) {
            RepairRecordEnclosure enclosure = new RepairRecordEnclosure();
                enclosure.setRepairRecordId(dto.getId());
                enclosure.setType(1);
                enclosure.setUpdateBy(userId);
                enclosure.setDelFlag(0);
                enclosure.setUrl(dto.getSignature());
                repairRecordEnclosureMapper.insert(enclosure);
        }
        //插入流转记录表
        OperationProcess process = new OperationProcess();
        process.setFaultCode(dto.getFaultCode());
        process.setProcessCode(5);
        process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
        process.setProcessPerson(user.getId());
        process.setProcessTime(new Date());
        processMapper.insert(process);
        return Result.ok();
    }

    /**
     * 根据故障编号查询更换备件
     * @param code
     * @return
     */
    @Override
    public List<SpareResult> changeSpare(String code) {
        List<SpareResult> results = partMapper.querySpare(code);
        for (SpareResult result : results) {
            //查询新备件
            MaterialBase one = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, result.getNewSparePartCode()),false);
            if(ObjectUtil.isNotEmpty(one)){
                result.setNewSparePartName(one.getName());
            }
            //查询老备件
            MaterialBase base = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq(MaterialBase.CODE, result.getOldSparePartCode()),false);
            if(ObjectUtil.isNotEmpty(one)){
                result.setNewSparePartName(one.getName());
            }
            if (base!=null) {
                result.setOldSparePartName(base.getName());
            }
        }
        return results;
    }

    /**
     * 查询最后一条维修记录
     * @param code
     * @param req
     * @return
     */
    @Override
    public FaultRepairRecordResult getDetail(String code,HttpServletRequest req) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        FaultRepairRecordResult record = faultRepairRecordMapper.selectLastRecord(userId, code);
        if (record!=null){
            //附件列表
            List<String> query = repairRecordEnclosureMapper.queryDetail(record.getId(),0);
            //电子签名
            List<String> query1= repairRecordEnclosureMapper.queryDetail(record.getId(),1);
            if (CollUtil.isNotEmpty(query)) {
                record.setUrlList(query);
            }
            if (CollUtil.isNotEmpty(query1)) {
                record.setSignature(query1);
            }
            //参与人
            if (StringUtils.isNotBlank(record.getParticipateIds())) {
                List<String> name = new ArrayList<String>();
                List<String> ids = Arrays.asList(record.getParticipateIds().split(","));
                List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getId,ids).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .select(SysUser::getRealname));
                for (SysUser user : userList) {
                    name.add(user.getRealname());
                }
                String str = StringUtils.join(name, ",");
                record.setParticipateNames(str);
            }
            //委外人员
            if (StringUtils.isNotBlank(record.getOutsourcingIds())) {
                List<String> ids = Arrays.asList(record.getOutsourcingIds().split(","));
                List<OutsourcingPersonnel> list = personnelService.list(new LambdaQueryWrapper<OutsourcingPersonnel>()
                        .in(OutsourcingPersonnel::getId, ids).eq(OutsourcingPersonnel::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .select(OutsourcingPersonnel::getName));
                List<String> stringList = list.stream().map(OutsourcingPersonnel::getName).collect(Collectors.toList());
                String str = StringUtils.join(stringList, ",");
                record.setOutsourcingNames(str);
            }
            if (record.getSolveStatus()!=null) {
                record.setSolveStatusDesc(SolveStatusEnum.findMessage(record.getSolveStatus()));
            }
        }
        return record;
    }

    /**
     * 编辑故障维修记录
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editRecord(FaultRepairDTO dto) {
        //查询当前指派人最后一条维修记录
        FaultRepairRecord record = faultRepairRecordMapper.selectDetailById(dto.getId());
        record.setParticipateIds(dto.getParticipateIds());
        record.setOutsourcingIds(dto.getOutsourcingIds());
        record.setOverTime(dto.getOverTime());
        record.setFaultAnalysis(dto.getFaultAnalysis());
        record.setMaintenanceMeasures(dto.getMaintenanceMeasures());
        //如果当前指派人最后一条维修记录为已解决 可以修改解决状态
        record.setSolveStatus(dto.getSolveStatus());
        faultRepairRecordMapper.updateById(record);
        //删除附件列表
        repairRecordEnclosureMapper.deleteByName(record.getId());
        //重新插入附件列表
        List<String> urlList = dto.getUrlList();
        if (CollUtil.isNotEmpty(urlList)){
            for (String s : urlList) {
                RepairRecordEnclosure enclosure = new RepairRecordEnclosure();
                enclosure.setRepairRecordId(record.getId());
                enclosure.setUrl(s);
                enclosure.setType(0);
                enclosure.setDelFlag(0);
                enclosure.setCreateBy(record.getCreateBy());
                repairRecordEnclosureMapper.insert(enclosure);
            }
        }
        return Result.ok();
    }
}
