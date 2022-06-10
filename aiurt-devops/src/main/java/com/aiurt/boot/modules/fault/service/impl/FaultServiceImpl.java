package com.aiurt.boot.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.*;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.*;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.mapper.DeviceMapper;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.fault.constant.FaultCommonConstant;
import com.aiurt.boot.modules.fault.dto.FaultDTO;
import com.aiurt.boot.modules.fault.entity.Fault;
import com.aiurt.boot.modules.fault.entity.FaultEnclosure;
import com.aiurt.boot.modules.fault.entity.OperationProcess;
import com.aiurt.boot.modules.fault.mapper.FaultEnclosureMapper;
import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.boot.modules.fault.mapper.OperationProcessMapper;
import com.aiurt.boot.modules.fault.param.FaultCountParam;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.param.FaultParam;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.manage.entity.CommonFault;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.mapper.CommonFaultMapper;
import com.aiurt.boot.modules.manage.service.ICommonFaultService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskReportService;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.service.IRepairTaskService;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.boot.modules.system.util.TimeUtil;
import com.aiurt.boot.modules.worklog.mapper.WorkLogMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {


	@Resource
	private FaultMapper faultMapper;

	@Resource
	private FaultEnclosureMapper faultEnclosureMapper;

	@Resource
	private NumberGenerateUtils numberGenerateUtils;

	@Resource
	private DeviceMapper deviceMapper;

	@Resource
	private OperationProcessMapper processMapper;

	@Resource
	private IPatrolTaskReportService patrolTaskReportService;

	@Resource
	private FaultRepairRecordMapper faultRepairRecordMapper;

	@Resource
	private ISysUserService sysUserService;

	@Resource
	private ICommonFaultService commonFaultService;

	@Resource
	private CommonFaultMapper commonFaultMapper;

	@Resource
	private FaultRepairRecordMapper repairRecordMapper;

	@Resource
	private WorkLogMapper workLogMapper;

	@Resource
	private IDeviceService deviceService;

	@Resource
	private IStationService stationService;

	@Resource
	private RoleAdditionalUtils roleAdditionalUtils;

	@Resource
	private IRepairTaskService repairTaskService;

	private final long nh = 60 * 60 * 1000;

	/**
	 * 查询故障列表
	 *
	 * @param page
	 * @param param
	 * @return
	 */
	@Override
	public IPage<FaultResult> pageList(IPage<FaultResult> page, FaultParam param, HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = user.getId();
		param.setUserId(userId);
		if (param.getStatus() != null && param.getStatus() == FaultStatusEnum.WUXIAOZHI.getCode()) {
			param.setStatus(null);
			param.setHangState("1");
		}
		if (StringUtils.isNotBlank(param.getStationCode())) {
			Station one = stationService.getOne(new QueryWrapper<Station>().eq(Station.ID, param.getStationCode()), false);
			param.setStationCode(one.getStationCode());
		}

		//权限
		List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(userId);
		if (CollectionUtils.isNotEmpty(departIdsByUserId)) {
			param.setDepartList(departIdsByUserId);
			List<Station> list = stationService.lambdaQuery().eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).in(Station::getTeamId, departIdsByUserId).select(Station::getStationCode).list();
			if (CollectionUtils.isNotEmpty(list)){
				param.setStationCodes(list.stream().map(Station::getStationCode).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
			}
		}
		List<String> listSystemCodesByUserId = roleAdditionalUtils.getListSystemCodesByUserId(userId);
		if (CollectionUtils.isNotEmpty(listSystemCodesByUserId)) {
			param.setSystemCodes(listSystemCodesByUserId);
		}

		IPage<FaultResult> faultResults = faultMapper.queryFault(page, param);

		List<SysUser> sysUser = sysUserService.lambdaQuery().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0).list();
		Map<String, String> nameMap = null;
		if (CollectionUtils.isNotEmpty(sysUser)) {
			nameMap = sysUser.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealname));
		}

		for (FaultResult record : faultResults.getRecords()) {
			if (StringUtils.isNotBlank(record.getCode())) {
				//获取故障维修记录
				FaultRepairRecordResult repairRecordResult = faultRepairRecordMapper.queryLastDetail(record.getCode());
				if (ObjectUtil.isNotEmpty(repairRecordResult)) {
					record.setOverTime(repairRecordResult.getOverTime());
					if (nameMap != null && StringUtils.isNotBlank(nameMap.get(repairRecordResult.getAppointUserId()))) {
						record.setRepairUserName(nameMap.get(repairRecordResult.getAppointUserId()));
					}
					record.setFaultAnalysis(repairRecordResult.getFaultAnalysis());
					record.setMaintenanceMeasures(repairRecordResult.getMaintenanceMeasures());
				}
				//设备id转化为设备名
				if (StringUtils.isNotBlank(record.getDevicesIds())) {
					record.setDevice(getDeviceName(record.getDevicesIds()));
				}
				//原因描述
				record.setStatusDesc(FaultStatusEnum.findMessage(record.getStatus()));
				//故障级别描述
				record.setFaultLevelDesc(FaultLevelEnum.findMessage(record.getFaultLevel()));
				record.setTimeCost(TimeUtil.dateDiff(record.getOccurrenceTime(),record.getOverTime()));
			}
		}
		return faultResults;
	}

	/**
	 * 导出故障列表
	 *
	 * @param param
	 * @return
	 */
	@Override
	public List<FaultResult> exportXls(FaultParam param) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = user.getId();
		if (param.getStatus() != null && param.getStatus() == FaultStatusEnum.WUXIAOZHI.getCode()) {
			param.setStatus(null);
			param.setHangState("1");
		}
		if (StringUtils.isNotBlank(param.getStationCode())) {
			Station one = stationService.getOne(new QueryWrapper<Station>().eq(Station.ID, param.getStationCode()), false);
			param.setStationCode(one.getStationCode());
		}
		List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(userId);
		if (CollectionUtils.isNotEmpty(departIdsByUserId)) {
			param.setDepartList(departIdsByUserId);
		}
		List<String> listSystemCodesByUserId = roleAdditionalUtils.getListSystemCodesByUserId(userId);
		if (CollectionUtils.isNotEmpty(listSystemCodesByUserId)) {
			param.setSystemCodes(listSystemCodesByUserId);
		}
		List<FaultResult> faultResults = faultMapper.exportXls(param);
		for (FaultResult record : faultResults) {
			if (StringUtils.isNotBlank(record.getCode())) {
				//获取故障维修记录
				FaultRepairRecordResult repairRecordResult = faultRepairRecordMapper.queryLastDetail(record.getCode());
				if (ObjectUtil.isNotEmpty(repairRecordResult)) {
					record.setOverTime(repairRecordResult.getOverTime());
					SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, repairRecordResult.getAppointUserId()).select(SysUser::getRealname), false);
					record.setRepairUserName(sysUser!=null ? sysUser.getRealname(): null);
					record.setFaultAnalysis(repairRecordResult.getFaultAnalysis());
					record.setMaintenanceMeasures(repairRecordResult.getMaintenanceMeasures());
				}
				//设备id转化为设备名
				if (StringUtils.isNotBlank(record.getDevicesIds())) {
					record.setDevice(getDeviceName(record.getDevicesIds()));
				}
				//原因描述
				record.setStatusDesc(FaultStatusEnum.findMessage(record.getStatus()));
				//故障级别描述
				record.setFaultLevelDesc(FaultLevelEnum.findMessage(record.getFaultLevel()));
			}
		}
		return faultResults;
	}

	/**
	 * 故障登记
	 *
	 * @param dto
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result<?> add(FaultDTO dto, HttpServletRequest req) {

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		if (dto.getRepairTaskId() != null) {
			RepairTask task = repairTaskService.getById(dto.getRepairTaskId());
			if (StringUtils.isNotBlank(task.getFaultCode())) {
				return Result.error("此检修单已上报故障,请勿重复上报");
			}
		}

		Fault fault = new Fault();
		//生成故障编号
		String before = "G" + dto.getLineCode() + dto.getStationCode();
		String codeNo = numberGenerateUtils.getCodeNo(before);
		fault.setCode(codeNo);
		fault.setLineCode(dto.getLineCode());
		fault.setStationCode(dto.getStationCode());
		if (StringUtils.isNotBlank(dto.getDevicesIds())) {
			fault.setDevicesIds(dto.getDevicesIds());
		}
		if (RepairWayEnum.BX.getMessage().equals(dto.getRepairWay()) && StringUtils.isBlank(dto.getRepairCode())) {
			throw new SwscException("请输入报修编号");
		} else if (RepairWayEnum.BX.getMessage().equals(dto.getRepairWay()) && StringUtils.isNotBlank(dto.getRepairCode())) {
			char[] chars = dto.getRepairCode().toCharArray();
			if (chars.length != CommonConstant.REPAIR_CODE_SIZE) {
				throw new SwscException("报修编号长度为10");
			}
		}
		fault.setRepairWay(dto.getRepairWay());
		fault.setFaultPhenomenon(dto.getFaultPhenomenon());
		fault.setFaultType(dto.getFaultType());
		fault.setFaultLevel(dto.getFaultLevel());
		fault.setRepairCode(dto.getRepairCode());
		if (StringUtils.isNotBlank(dto.getLocation())) {
			fault.setLocation(dto.getLocation());
		}
		if (StringUtils.isNotBlank(dto.getScope())) {
			fault.setScope(dto.getScope());
		}
		fault.setOccurrenceTime(dto.getOccurrenceTime());
		fault.setStatus(0);
		fault.setSystemCode(dto.getSystemCode());
		fault.setDelFlag(0);
		fault.setHangState(0);
		fault.setAssignStatus(0);
		fault.setDetailLocation(dto.getDetailLocation());

		Station station = stationService.lambdaQuery()
				.eq(StringUtils.isNotBlank(dto.getStationCode()), Station::getId, dto.getStationCode())
				.select(Station::getTeamId).last("limit 1").one();
		if (station == null || station.getTeamId() == null){
			throw new SwscException("未查询到站点所对应的班组数据,请核对后重新提交");
		}
		//添加机构id
		fault.setOrgId(station.getTeamId());
		fault.setCreateBy(user.getId());
		faultMapper.insert(fault);
		//插入附件表
		if (CollUtil.isNotEmpty(dto.urlList)) {
			FaultEnclosure faultEnclosure = new FaultEnclosure();
			List<String> urlList = dto.urlList;
			for (String s : urlList) {
				faultEnclosure.setCreateBy(fault.getCreateBy());
				faultEnclosure.setCode(fault.getCode());
				faultEnclosure.setUrl(s);
				faultEnclosure.setDelFlag(0);
				faultEnclosureMapper.insert(faultEnclosure);
			}
		}
		//记录常见故障数量
		if (dto.getCommonFaultId() != null) {
			CommonFault commonFault = commonFaultService.getOne(new QueryWrapper<CommonFault>().eq(CommonFault.ID, dto.getCommonFaultId()), false);
			commonFault.setNum(commonFault.getNum() + 1);
			commonFaultMapper.updateById(commonFault);
		}
		//记录运转流程
		OperationProcess process = new OperationProcess();
		process.setFaultCode(fault.getCode());
		process.setProcessCode(0);//新增故障
		process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
		process.setProcessPerson(user.getId());
		process.setProcessTime(new Date());
		processMapper.insert(process);

		// 回调巡检接口
		if (dto.getTaskId() != null && dto.getPoolContentId() != null) {
			patrolTaskReportService.callback(dto.getTaskId(), dto.getPoolContentId(), codeNo);
		}
		// 回调检修接口
		if (dto.getRepairTaskId() != null) {
			repairTaskService.callback(dto.getRepairTaskId(), codeNo);
		}
		return Result.ok("新增成功");
	}

	/**
	 * 根据code查询故障信息
	 *
	 * @param code
	 * @return
	 */
	@Override
	public FaultResult getFaultDetail(String code) {
		FaultResult fault = faultMapper.selectDetailByCode(code);
		SysUser userById = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, fault.getCreateBy()), false);
		fault.setCreateByName(userById.getRealname());
		//设备id转化为设备名
		if (StringUtils.isNotBlank(fault.getDevicesIds())) {
			fault.setDevice(getDeviceName(fault.getDevicesIds()));
		}
		//故障级别描述
		fault.setFaultLevelDesc(FaultLevelEnum.findMessage(fault.getFaultLevel()));
		//故障类型描述
		fault.setFaultTypeDesc(FaultTypeEnum.findMessage(fault.getFaultType()));
		//故障附件列表
		List<String> query = faultEnclosureMapper.query(code);
		fault.setUrlList(query);
		return fault;
	}

	/**
	 * 挂起
	 *
	 * @param id
	 * @return
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result hangById(Integer id, String remark) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		faultMapper.hangById(id, remark);
		//记录运转流程
		Fault fault = this.getOne(new QueryWrapper<Fault>().eq(Fault.ID, id), false);
		OperationProcess process = new OperationProcess();
		process.setFaultCode(fault.getCode());
		process.setProcessCode(3);
		process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
		process.setProcessPerson(user.getId());
		process.setProcessTime(new Date());
		processMapper.insert(process);
		return Result.ok();
	}

	/**
	 * 取消挂起
	 *
	 * @param id
	 * @return
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result cancelHang(Integer id) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		faultMapper.cancelById(id);
		//记录运转流程
		Fault fault = this.getOne(new QueryWrapper<Fault>().eq(Fault.ID, id), false);
		OperationProcess process = new OperationProcess();
		process.setFaultCode(fault.getCode());
		process.setProcessCode(4);
		process.setProcessLink(ProcessLinkEnum.findMessage(process.getProcessCode()));
		process.setProcessPerson(user.getId());
		process.setProcessTime(new Date());
		processMapper.insert(process);
		return Result.ok();
	}

	/**
	 * 报表统计故障数量
	 *
	 * @return
	 */
	@Override
	public Result<FaultNumResult> getFaultNum(String startTime, String endTime) {
		FaultNumResult result = new FaultNumResult();
		//故障总量
		Integer integer = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).
				between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), Fault::getCreateTime, startTime, endTime));
		result.setFaultNum(integer);
		//挂起数量
		Integer integer1 = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getHangState, CommonConstant.HANG_STATE).eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).
				between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), Fault::getCreateTime, startTime, endTime));
		result.setHangNum(integer1);
		//自检数量
		Integer integer2 = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).eq(Fault::getRepairWay, RepairWayEnum.ZJ.getMessage()).
				between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), Fault::getCreateTime, startTime, endTime));
		result.setCheckNum(integer2);
		//报修数量
		Integer integer3 = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).eq(Fault::getRepairWay, RepairWayEnum.BX.getMessage()).
				between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), Fault::getCreateTime, startTime, endTime));
		result.setRepairNum(integer3);
		return Result.ok(result);
	}

	/**
	 * 报表统计超时故障数量
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public Result<TimeOutFaultNum> getTimeOutFaultNum(String startTime, String endTime) {
		TimeOutFaultNum num = new TimeOutFaultNum();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -2);
		//三级故障数量
		Integer integer = faultMapper.selectTimeOutCount(startTime, endTime, c.getTime());
		num.setThirdLevelFaultNum(integer);
		//二级故障数量
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Integer integer1 = faultMapper.selectTimeOutCount(startTime, endTime, calendar.getTime());
		num.setSecondLevelFaultNum(integer1 - integer);
		//一级故障数量
		Calendar instance = Calendar.getInstance();
		Integer integer2 = faultMapper.selectTimeOutCount(startTime, endTime, instance.getTime());
		num.setFirstLevelFaultNum(integer2 - integer1);
		//超时故障总数量
		num.setTimeOutFaultNum(integer2);
		return Result.ok(num);
	}

	/**
	 * pc首页报表统计超时挂起数量
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public Result<TimeOutFaultNum> getTimeOutHangNum(String startTime, String endTime) {
		Calendar instance = Calendar.getInstance();
		Integer num = faultMapper.selectTimeOutHangNum(startTime, endTime, instance.getTime());
		TimeOutFaultNum timeOutFaultNum = new TimeOutFaultNum();
		timeOutFaultNum.setTimeOutHangNum(num);
		return Result.ok(timeOutFaultNum);
	}

	/**
	 * 查询故障数量
	 *
	 * @param param
	 * @return
	 */
	@Override
	public Result<List<FaultCountResult>> getFaultCount(FaultCountParam param) {
		List<FaultCountResult> results = faultMapper.selectFaultCount(param);
		//上月同期开始时间
		LocalDateTime lastMonthDayStart = param.getDayStart().plusMonths(-1);
		//上月同期结束时间
		LocalDateTime lastMonthDayEnd = param.getDayEnd().plusMonths(-1).withHour(23).withMinute(59).withSecond(59);
		//去年同期开始时间
		LocalDateTime lastYearDayStart = param.getDayStart().plusYears(-1);
		//去年同期结束时间
		LocalDateTime lastYearDayEnd = param.getDayEnd().plusYears(-1).withHour(23).withMinute(59).withSecond(59);

		FaultCountParam tmpParam = new FaultCountParam();
		BeanUtils.copyProperties(param,tmpParam);

		for (FaultCountResult result : results) {
			param.setSystemCode(result.getSystemCode());
			//获取该系统自检数量
			Integer num = faultMapper.selectSelfCheckNum(param);
			result.setSelfCheckNum(num);
			//计算该系统报修数量
			result.setRepairNum(result.getSumNum() - num);

			//上月开始时间
			param.setDayStart(lastMonthDayStart);
			param.setDayEnd(lastMonthDayEnd);
			//上月同期数量
			Integer lastMonthNum = faultMapper.selectLastMonthNum(param);
			result.setThanLastMonthNum(lastMonthNum);
			//计算上月同期比例
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(2);
			String format = numberFormat.format((float) (result.getSumNum() - result.getThanLastMonthNum()) / (float) result.getThanLastMonthNum() * 100);
			if (result.getThanLastMonthNum() == 0 || "0".equals(format)) {
				result.setThanLastMonth("0.00" + "%");
			} else {
				result.setThanLastMonth(format + "%");
			}
			//去年同期时间
			param.setDayStart(lastYearDayStart);
			param.setDayEnd(lastYearDayEnd);
			//去年同期数量
			Integer lastYearNum = faultMapper.selectLastMonthNum(param);
			result.setThanLastYearNum(lastYearNum);
			//计算去年同期比例
			String format1 = numberFormat.format((float) (result.getSumNum() - result.getThanLastYearNum()) / (float) result.getThanLastYearNum() * 100);
			if (result.getThanLastYearNum() == 0 || "0".equals(format1)) {
				result.setThanLastYear("0.00" + "%");
			} else {
				result.setThanLastYear(format1 + "%");
			}
			result.setDeviceFaultNum(0);
			result.setLineFaultNum(0);
			result.setExternalNum(0);
			result.setOtherNum(0);
			result.setPowerSupplyFaultNum(0);
			param.setDayEnd(lastYearDayEnd.plusYears(+1));
			param.setDayStart(lastYearDayStart.plusYears(+1));
		}

		param = tmpParam;

		//计算合计数量
		FaultCountResult faultCountResult = new FaultCountResult();
		faultCountResult.setSystemName("总计");
		//当前月总量
		Integer count = faultMapper.selectCount(new LambdaQueryWrapper<Fault>()
				.eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0)
				.between(Fault::getCreateTime, param.getDayStart(), param.getDayEnd())

				.eq(StringUtils.isNotBlank(param.getStationCode()),Fault::getStationCode,param.getStationCode())
				.eq(StringUtils.isNotBlank(param.getLineCode()),Fault::getLineCode,param.getLineCode())
				.eq(StringUtils.isNotBlank(param.getSystemCode()),Fault::getSystemCode,param.getSystemCode())
		);
		faultCountResult.setSumNum(count);
		//当前月自检总量
		Integer selfCheckNum = faultMapper.selectCount(new LambdaQueryWrapper<Fault>()
				.eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).between(Fault::getCreateTime, param.getDayStart(), param.getDayEnd()).eq(Fault::getRepairWay, "自检")
				.eq(StringUtils.isNotBlank(param.getStationCode()),Fault::getStationCode,param.getStationCode())
				.eq(StringUtils.isNotBlank(param.getLineCode()),Fault::getLineCode,param.getLineCode())
				.eq(StringUtils.isNotBlank(param.getSystemCode()),Fault::getSystemCode,param.getSystemCode())
		);
		faultCountResult.setSelfCheckNum(selfCheckNum);
		//当前月报修总量
		faultCountResult.setRepairNum(count - selfCheckNum);
		//上月同期总量
		Integer thanLastMonthNum = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).between(Fault::getCreateTime, lastMonthDayStart, lastMonthDayEnd)
				.eq(StringUtils.isNotBlank(param.getStationCode()),Fault::getStationCode,param.getStationCode())
				.eq(StringUtils.isNotBlank(param.getLineCode()),Fault::getLineCode,param.getLineCode())
				.eq(StringUtils.isNotBlank(param.getSystemCode()),Fault::getSystemCode,param.getSystemCode())
		);
		faultCountResult.setThanLastMonthNum(thanLastMonthNum);
		//上月同期比例
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		String format = numberFormat.format((float) (count - thanLastMonthNum) / (float) thanLastMonthNum * 100);
		if (thanLastMonthNum == FaultCommonConstant.DEFAULT_VALUE || "0".equals(format)) {
			faultCountResult.setThanLastMonth("0.00" + "%");
		} else {
			faultCountResult.setThanLastMonth(format + "%");
		}

		//去年同期数量
		Integer thanLastYearNum = faultMapper.selectCount(new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0).between(Fault::getCreateTime, lastYearDayStart, lastYearDayEnd)
				.eq(StringUtils.isNotBlank(param.getStationCode()),Fault::getStationCode,param.getStationCode())
				.eq(StringUtils.isNotBlank(param.getLineCode()),Fault::getLineCode,param.getLineCode())
				.eq(StringUtils.isNotBlank(param.getSystemCode()),Fault::getSystemCode,param.getSystemCode())
		);
		faultCountResult.setThanLastYearNum(thanLastYearNum);
		String format1 = numberFormat.format((float) (count - thanLastYearNum) / (float) thanLastYearNum * 100);
		if (thanLastYearNum == FaultCommonConstant.DEFAULT_VALUE || "0".equals(format1)) {
			faultCountResult.setThanLastYear("0.00" + "%");
		} else {
			faultCountResult.setThanLastYear(format1 + "%");
		}
		faultCountResult.setDeviceFaultNum(0);
		faultCountResult.setLineFaultNum(0);
		faultCountResult.setExternalNum(0);
		faultCountResult.setOtherNum(0);
		faultCountResult.setPowerSupplyFaultNum(0);
		results.add(faultCountResult);
		return Result.ok(results);
	}

	/**
	 * 各系统检修/自检对比
	 *
	 * @param param
	 * @return
	 */
	@Override
	public Result<List<FaultCountResult>> getContrast(FaultCountParam param) {
		List<FaultCountResult> faultCountResults = faultMapper.selectContrast(param);
		for (FaultCountResult result : faultCountResults) {
			LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0)
					.between(Fault::getCreateTime, param.getDayStart(), param.getDayEnd())
					.eq(Fault::getRepairWay, RepairWayEnum.ZJ.getMessage())
					.eq(StringUtils.isNotBlank(result.getSystemCode()),Fault::getSystemCode,result.getSystemCode())
					.eq(StringUtils.isNotBlank(param.getLineCode()), Fault::getLineCode, param.getLineCode())
					.eq(StringUtils.isNotBlank(param.getStationCode()),Fault::getStationCode, param.getSystemCode());
			//自检数量
			Integer selfCheckNum = faultMapper.selectCount(queryWrapper);
			result.setSelfCheckNum(selfCheckNum);
			result.setRepairNum(result.getSumNum() - selfCheckNum);
		}
		return Result.ok(faultCountResults);
	}

	/**
	 * 各系统故障数比较
	 *
	 * @param param
	 * @return
	 */
	@Override
	public Result<List<FaultCountResult>> getPercentage(FaultCountParam param) {
		LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<Fault>().eq(Fault::getDelFlag, CommonConstant.DEL_FLAG_0)
				.ge(param.getDayStart()!=null,Fault::getCreateTime,param.getDayStart())
				.le(param.getDayEnd()!=null,Fault::getCreateTime,param.getDayEnd())
				.eq(StringUtils.isNotBlank(param.getSystemCode()),Fault::getSystemCode,param.getSystemCode())
				.eq(StringUtils.isNotBlank(param.getLineCode()), Fault::getLineCode, param.getLineCode())
				.eq(StringUtils.isNotBlank(param.getStationCode()), Fault::getStationCode, param.getStationCode());
		//故障总量
		Integer sumNum = faultMapper.selectCount(queryWrapper);
		List<FaultCountResult> faultCountResults = faultMapper.selectContrast(param);
		for (FaultCountResult result : faultCountResults) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(2);
			String format = numberFormat.format((float) result.getSumNum() / (float) sumNum * 100);
			if ("0".equals(format)) {
				result.setSystemPercentage("0.00" + "%");
			}
			result.setSystemPercentage(format + "%");
		}
		return Result.ok(faultCountResults);
	}

	/**
	 * 单一系统检修/自检各月份故障分析
	 *
	 * @param param
	 * @return
	 */
	@Override
	public Result<List<FaultMonthResult>> getFaultNumByMonth(FaultCountParam param) {
		List<FaultMonthResult> results = faultMapper.selectFaultNumByMonth(param);
		for (FaultMonthResult result : results) {
			LocalDate monthStart = LocalDate.of(LocalDate.now().getYear(), result.getThisMonth(), 1);
			LocalDate monthEnd = monthStart.plusMonths(1).plusDays(-1);
			//计算自检数量
			Integer num = faultMapper.selectThisMonthNum(param, monthStart, monthEnd);
			result.setSelfCheckNum(num);
			result.setRepairNum(result.getSumNum() - num);
		}
		return Result.ok(results);
	}

	/**
	 * 设备故障总数同比分析
	 *
	 * @param param
	 * @return
	 */
	@Override
	public Result<List<FaultMonthResult>> getFaultByMonth(FaultCountParam param) {
		List<FaultMonthResult> faultMonthResults = faultMapper.selectFaultByMonth(param);
		for (FaultMonthResult result : faultMonthResults) {
			LocalDate yearStart = LocalDate.of(LocalDate.now().getYear() - 1, result.getThisMonth(), 1);
			LocalDate yearEnd = yearStart.plusMonths(1).plusDays(-1);
			//计算去年同期数量
			Integer num = faultMapper.selectThisMonth(param, yearStart, yearEnd);
			result.setLastYearNum(num);
		}
		return Result.ok(faultMonthResults);
	}

	/**
	 * 首页一级故障
	 *
	 * @param dayStart
	 * @param dayEnd
	 * @return
	 */
	@Override
	public Result<List<FaultLevelResult>> getFirstLevelFault(String dayStart, String dayEnd) {
		Result<List<FaultLevelResult>> result = new Result<>();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -2);
		long dateTime = (new Date()).getTime();
		List<FaultLevelResult> faultLevelResults = faultMapper.selectFirstLevelFault(dayStart, dayEnd, c.getTime());
		for (FaultLevelResult faultLevelResult : faultLevelResults) {
			long time = faultLevelResult.getCreateTime().getTime();
			faultLevelResult.setDuration((dateTime - time) / nh + 1);
		}
		result.setResult(faultLevelResults);
		return result;
	}

	/**
	 * 首页二级故障
	 *
	 * @param dayStart
	 * @param dayEnd
	 * @return
	 */
	@Override
	public Result<List<FaultLevelResult>> getSecondLevelFault(String dayStart, String dayEnd) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -2);
		long dateTime = (new Date()).getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		List<FaultLevelResult> faultLevelResults = faultMapper.selectLevelFault(dayStart, dayEnd, c.getTime(), calendar.getTime());
		for (FaultLevelResult faultLevelResult : faultLevelResults) {
			long time = faultLevelResult.getCreateTime().getTime();
			faultLevelResult.setDuration((dateTime - time) / nh + 1);
		}
		return Result.ok(faultLevelResults);
	}

	/**
	 * 首页三级故障
	 *
	 * @param dayStart
	 * @param dayEnd
	 * @return
	 */
	@Override
	public Result<List<FaultLevelResult>> getThirdLevelFault(String dayStart, String dayEnd) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		long dateTime = (new Date()).getTime();
		List<FaultLevelResult> faultLevelResults = faultMapper.selectLevelFault(dayStart, dayEnd, c.getTime(), new Date());
		for (FaultLevelResult faultLevelResult : faultLevelResults) {
			long time = faultLevelResult.getCreateTime().getTime();
			faultLevelResult.setDuration((dateTime - time) / nh + 1);
		}
		return Result.ok(faultLevelResults);
	}

	/**
	 * 维修人员故障信息
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public List<UserAndAmountVO> getFaultPersonDetail(StatisticsVO vo) {
		final String teamId = vo.getTeamId();
		List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq(SysUser.ORG_ID, teamId));

		List<String> userNameList = sysUserList.stream().map(SysUser::getRealname).collect(Collectors.toList());
		List<UserAndAmountVO> list = repairRecordMapper.selectFaultNum(vo.getStartTime(), vo.getEndTime(), vo.getUserName(), userNameList);
		return list;
	}

	/**
	 * 根据设备编号查询故障信息
	 *
	 * @param code
	 * @param param
	 * @return
	 */
	@Override
	public IPage<FaultDeviceResult> getFaultDeviceDetail(IPage<FaultDeviceResult> page, String code, FaultDeviceParam param) {
		Device device = deviceService.getOne(new QueryWrapper<Device>().eq(Device.CODE, code), false);
		//分页查询设备对应故障
		IPage<FaultDeviceResult> iPage = faultMapper.selectFaultDeviceDetail(page, device.getId(), param);
		List<FaultDeviceResult> results = iPage.getRecords();
		for (FaultDeviceResult result : results) {
			result.setFaultLevelDesc(FaultLevelEnum.findMessage(result.getFaultLevel()));
			FaultRepairRecordResult repairRecordResult = repairRecordMapper.queryLastDetail(result.getCode());
			if (ObjectUtil.isNotEmpty(repairRecordResult)) {
				String createBy = repairRecordResult.getCreateBy();
				String realname = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, createBy), false).getRealname();
				if (StringUtils.isNotBlank(realname)) {
					result.setSolveBy(realname);
				}
			}
		}
		return iPage;
	}

	/**
	 * 维修时长
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public Map<String, Long> getFaultDuration(StatisticsVO vo) {
		final String teamId = vo.getTeamId();
		List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq(SysUser.ORG_ID, teamId));
		List<String> userIdList = sysUserList.stream().map(SysUser::getId).collect(Collectors.toList());
		long mh = 24 * 60 * 60 * 1000;
		Map<String, Long> map = new HashMap<>();
		Long allTime = 0L;
		//获取对应维修时长集合
		for (String s : userIdList) {
			SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, s), false);
			List<FaultPersonResult> faultDate = repairRecordMapper.getFaultDate(vo.getStartTime(), vo.getEndTime(), user.getRealname());
			if (CollUtil.isEmpty(faultDate)) {
				map.put(user.getRealname(), 0L);
			} else {
				for (FaultPersonResult result : faultDate) {
					long time = result.getStartDate().getTime();
					long time1 = result.getEndDate().getTime();
					result.setDuration((time1 - time) / mh);
					allTime += result.getDuration();
				}
				map.put(user.getRealname(), allTime);
			}
		}
		return map;
	}

	/**
	 * 配合施工总人次
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public Map<String, Integer> getAssortNum(StatisticsVO vo) {
		final String teamId = vo.getTeamId();
		List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq(SysUser.ORG_ID, teamId));
		List<String> userIdList = sysUserList.stream().map(SysUser::getId).collect(Collectors.toList());
		HashMap<String, Integer> map = new HashMap<>();
		Integer allNum = 0;
		//获取对应配合施工人数集合
		for (String s : userIdList) {
			SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, s), false);
			List<AssortNumResult> assortNum = workLogMapper.getAssortNum(vo.getStartTime(), vo.getEndTime(), user.getRealname());
			if (CollUtil.isEmpty(assortNum)) {
				map.put(user.getRealname(), 0);
			} else {
				for (AssortNumResult result : assortNum) {
					if (ObjectUtil.isNotEmpty(result)) {
						Integer assortNum1 = result.getAssortNum();
						allNum += assortNum1;
					}
				}
				map.put(user.getRealname(), allNum);
			}
		}
		return map;
	}



	@Override
	public List<StatisticsFaultWayVO> getFaultCountGroupByWay(Date startTime, Date endTime, String lineCode) {
		return faultMapper.getFaultCountGroupByWay(startTime,endTime,lineCode);

	}

	@Override
	public List<StatisticsFaultStatusVO> getFaultCountGroupByStatus(Date startTime, Date endTime, String lineCode) {
		return faultMapper.getFaultCountGroupByStatus(startTime,endTime,lineCode);
	}

	@Override
	public List<StatisticsFaultLevelVO> getFaultGroupByLevel(Date startTime, Date endTime, String lineCode) {
		return faultMapper.getFaultGroupByLevel(startTime,endTime,lineCode);
	}

	@Override
	public List<StatisticsFaultMonthVO> getFaultCountGroupByMonth(Date startTime, Date endTime, String lineCode) {
		return faultMapper.getFaultCountGroupByMonth(startTime,endTime,lineCode);
	}

	@Override
	public List<StatisticsFaultSystemVO> getFaultCountGroupBySystem(Date startTime, Date endTime, String lineCode) {
		return faultMapper.getFaultCountGroupBySystem(startTime,endTime,lineCode);
	}

	@Override
	public StatisticsFaultCountVO  getFaultCountAndDetails(String lineCode) {
		Date now = new Date();
        StatisticsFaultCountVO statisticsFaultCountVO=new StatisticsFaultCountVO();
        Date startTime = DateUtil.beginOfYear(now);
        Date endTime = DateUtil.endOfYear(now);
        int faultCount = faultMapper.getFaultCount(startTime, endTime, lineCode);
        statisticsFaultCountVO.setFaultTotal(faultCount);
        //未完成
        Integer unCompleteNum = faultMapper.countUnCompleteNumByLineCode(startTime, endTime, lineCode);
        statisticsFaultCountVO.setUnCompleteCount(unCompleteNum);
        //本周新增
        Date weekDayStartTime = DateUtil.beginOfWeek(now);
        Date weekDayEndTime = DateUtil.endOfWeek(now);
        int faultweekCount = faultMapper.getFaultCount(weekDayStartTime, weekDayEndTime, lineCode);
        statisticsFaultCountVO.setFaultWeekCount(faultweekCount);
        //本周完成
        int faultCompleteCount = faultMapper.getFaultCompleteCount(weekDayStartTime, weekDayEndTime, lineCode);
        statisticsFaultCountVO.setFaultWeekCompleteCount(faultCompleteCount);
        List<FaultSystemVO> faultSystemVOList=faultMapper.selectFaultSystemVO(weekDayStartTime, weekDayEndTime, lineCode);
        statisticsFaultCountVO.setStatisticsFaultSystemVOList(faultSystemVOList);
        return statisticsFaultCountVO;
	}

	/**
	 * 根据设备ids获取设备名
	 *
	 * @param ids
	 * @return
	 */
	private String getDeviceName(String ids) {
		List<String> name = new ArrayList<String>();
		List<String> string = Arrays.asList(ids.split(","));
		List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<Device>()
				.in(Device::getCode, string)
				.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(Device::getName));
		for (Device device : devices) {
			name.add(device.getName());
		}
		String str = StringUtils.join(name, ",");
		return str;
	}

}


