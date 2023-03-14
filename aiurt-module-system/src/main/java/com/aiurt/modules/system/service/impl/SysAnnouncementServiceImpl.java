package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoTaskEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.util.SysAnnmentEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.message.entity.SysMessageTemplate;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.system.dto.*;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.system.mapper.SysAnnouncementMapper;
import com.aiurt.modules.system.mapper.SysAnnouncementSendMapper;
import com.aiurt.modules.system.service.ISysAnnouncementService;
import com.aiurt.modules.todo.dto.SysTodoCountDTO;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.mapper.SysTodoListMapper;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.min;
import org.flowable.task.api.Task;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.minidao.util.FreemarkerParseFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Service
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, SysAnnouncement> implements ISysAnnouncementService {

	@Resource
	private SysAnnouncementMapper sysAnnouncementMapper;

	@Resource
	private SysAnnouncementSendMapper sysAnnouncementSendMapper;

	@Resource
	private SysTodoListMapper sysTodoListMapper;

	@Resource
	private ISysTodoListService sysTodoListService;

	@Resource
	private ISysParamAPI sysParamAPI;

	@Resource
	private SysBaseApiImpl sysBaseApi;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveAnnouncement(SysAnnouncement sysAnnouncement) {
		SysMessageTemplate templateEntity = sysBaseApi.getTemplateEntity(CommonConstant.ANNOUNCEMENT_SERVICE_NOTICE);
		boolean isMarkdown =CommonConstant.MSG_TEMPLATE_TYPE_MD.equals(templateEntity.getTemplateType());
		HashMap<String, Object> map = new HashMap<>();
		map.put("msgContent", sysAnnouncement.getMsgContent());
		String content = templateEntity.getTemplateContent();
		if(StrUtil.isNotBlank(content)){
			content = FreemarkerParseFactory.parseTemplateContent(content,map);
		}
		sysAnnouncement.setMsgContent(content);

		if(sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
			sysAnnouncementMapper.insert(sysAnnouncement);
		}else {
			// 1.插入通告表记录
			sysAnnouncementMapper.insert(sysAnnouncement);
			// 2.插入用户通告阅读标记表记录
			String userId = sysAnnouncement.getUserIds();
			String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
			String anntId = sysAnnouncement.getId();
			Date refDate = new Date();
			for(int i=0;i<userIds.length;i++) {
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(userIds[i]);
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				announcementSend.setReadTime(refDate);
				sysAnnouncementSendMapper.insert(announcementSend);
			}
		}
	}

	/**
	 * @功能：编辑消息信息
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean upDateAnnouncement(SysAnnouncement sysAnnouncement) {
		// 1.更新系统信息表数据
		sysAnnouncementMapper.updateById(sysAnnouncement);
		String userId = sysAnnouncement.getUserIds();
		if(oConvertUtils.isNotEmpty(userId)&&sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_UESR)) {
			// 2.补充新的通知用户数据
			String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
			String anntId = sysAnnouncement.getId();
			Date refDate = new Date();
			for(int i=0;i<userIds.length;i++) {
				LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<SysAnnouncementSend>();
				queryWrapper.eq(SysAnnouncementSend::getAnntId, anntId);
				queryWrapper.eq(SysAnnouncementSend::getUserId, userIds[i]);
				List<SysAnnouncementSend> announcementSends=sysAnnouncementSendMapper.selectList(queryWrapper);
				if(announcementSends.size()<=0) {
					SysAnnouncementSend announcementSend = new SysAnnouncementSend();
					announcementSend.setAnntId(anntId);
					announcementSend.setUserId(userIds[i]);
					announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
					announcementSend.setReadTime(refDate);
					sysAnnouncementSendMapper.insert(announcementSend);
				}
			}
			// 3. 删除多余通知用户数据
			Collection<String> delUserIds = Arrays.asList(userIds);
			LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<SysAnnouncementSend>();
			queryWrapper.notIn(SysAnnouncementSend::getUserId, delUserIds);
			queryWrapper.eq(SysAnnouncementSend::getAnntId, anntId);
			sysAnnouncementSendMapper.delete(queryWrapper);
		}
		return true;
	}

    /**
     * 流程执行完成保存消息通知
     * @param title 标题
     * @param msgContent 信息内容
     */
	@Override
	public void saveSysAnnouncement(String title, String msgContent) {
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(msgContent);
		announcement.setSender("JEECG BOOT");
		announcement.setPriority(CommonConstant.PRIORITY_L);
		announcement.setMsgType(CommonConstant.MSG_TYPE_ALL);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		sysAnnouncementMapper.insert(announcement);
	}

	@Override
	public Page<SysAnnouncement> querySysCementPageByUserId(Page<SysAnnouncement> page, String userId, List<String> msgCategory) {
		if (page.getSize() == -1) {
			return page.setRecords(sysAnnouncementMapper.querySysCementListByUserId(null, userId, msgCategory));
		} else {
			return page.setRecords(sysAnnouncementMapper.querySysCementListByUserId(page, userId, msgCategory));
		}
	}

	@Override
	public List<SysMessageTypeDTO> queryMessageType() {
		List<SysMessageTypeDTO> list = new ArrayList<>();
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		String username = loginUser.getUsername();
		//获取当前登录人所有业务消息
		List<SysAnnouncementTypeCountDTO> announcementTypeCountDTOList = baseMapper.queryTypeCount(userId);

		//头像集合
        Set<String> pictureCode = new HashSet<>();
		pictureCode.add(SysParamCodeConstant.FAULT);
		pictureCode.add(SysParamCodeConstant.INSPECTION);
		pictureCode.add(SysParamCodeConstant.PATROL);
		pictureCode.add(SysParamCodeConstant.EMERGENCY);
		pictureCode.add(SysParamCodeConstant.TRAIN);
		pictureCode.add(SysParamCodeConstant.WEEK);
		pictureCode.add(SysParamCodeConstant.SITUATION);
		pictureCode.add(SysParamCodeConstant.WORKLOG);
		pictureCode.add(SysParamCodeConstant.SPAREPART);
		pictureCode.add(SysParamCodeConstant.ASSET);

		Map<String, List<SysAnnouncementTypeCountDTO>> busTypeMap = announcementTypeCountDTOList.stream().collect(Collectors.groupingBy(dto -> {
			String busType = dto.getBusType();
			SysAnnmentEnum sysAnnmentEnum = SysAnnmentEnum.getByTypeV2(busType);
			if (Objects.nonNull(sysAnnmentEnum)) {
				return sysAnnmentEnum.getType();
			} else {
				return dto.getBusType();
			}
		}));

		List<SysMessageTypeDTO> messageList = new ArrayList<>();
		busTypeMap.remove(null);
		busTypeMap.forEach((busType, dtoList)->{
			SysAnnouncementTypeCountDTO countDTO = dtoList.get(0);
			int sum = dtoList.stream().mapToInt(SysAnnouncementTypeCountDTO::getUnreadCount).sum();
			List<String> busTypeList = dtoList.stream().map(SysAnnouncementTypeCountDTO::getBusType).collect(Collectors.toList());
			SysAnnmentEnum annmentEnum = SysAnnmentEnum.getByType(busType);


            //查询最近的一条数据
			SysAnnouncementSend sysAnnouncementSend = baseMapper.queryLast(userId, busTypeList, null);

			SysMessageTypeDTO typeDTO = SysMessageTypeDTO.builder()
					.busType(busType)
					.messageFlag("1")
					.count(sum)
					.title(Objects.isNull(annmentEnum)? countDTO.getTitile(): annmentEnum.getModule()).build();

            //设置头像图片
			if (pictureCode.contains(busType)) {
				SysParamModel sysParamModel = sysParamAPI.selectByCode(busType);
				if (Objects.nonNull(sysParamModel)) {
					typeDTO.setValue(sysParamModel.getValue());
				}
			}

			if (Objects.nonNull(sysAnnouncementSend)) {
				typeDTO.setIntervalTime(Objects.isNull(sysAnnouncementSend.getUpdateTime())?sysAnnouncementSend.getCreateTime(): sysAnnouncementSend.getUpdateTime());
				typeDTO.setTitleContent(sysAnnouncementSend.getTitleContent());
			}
			messageList.add(typeDTO);
		});

		//获取当前登录人待办消息(业务消息)消息类型为null
		List<SysAnnouncementTypeCountDTO> sysAnnouncementTypeCountDTOS = baseMapper.queryBNullTypeCount(userId);

		//bus_type为空的数据
		sysAnnouncementTypeCountDTOS.stream().forEach(typeCountDTO->{
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			sysMessageTypeDTO.setCount(typeCountDTO.getUnreadCount());
			sysMessageTypeDTO.setMessageFlag("1");
			String key = typeCountDTO.getBusType();

			if("1".equals(key)){
				sysMessageTypeDTO.setTitle("系统公告");
				sysMessageTypeDTO.setBusType(null);
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SYS_ANNOUNCEMENT);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("1");
			} else  if("2".equals(key)){
				sysMessageTypeDTO.setTitle("系统消息");
				//设置消息类型
				sysMessageTypeDTO.setBusType(null);
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SYS_MESSAGE);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("2");
			} else if ("3".equals(key)){
				sysMessageTypeDTO.setTitle("特情消息");
				//设置消息类型
				sysMessageTypeDTO.setBusType("situation");
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SITUATION);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("3");
			}else {
				// donothing
			}

			SysAnnouncementSend sysAnnouncementSend = baseMapper.queryLast(userId, null, key);

			if (Objects.nonNull(sysAnnouncementSend)) {
				sysMessageTypeDTO.setIntervalTime(Objects.isNull(sysAnnouncementSend.getUpdateTime())?sysAnnouncementSend.getCreateTime(): sysAnnouncementSend.getUpdateTime());
				sysMessageTypeDTO.setTitleContent(sysAnnouncementSend.getTitleContent());
			}
			messageList.add(sysMessageTypeDTO);
		});

		list.addAll(messageList);

		// bpmn类型,processCode 为空历史数据不展示了
		List<SysTodoCountDTO> sysTodoCountDTOS = sysTodoListService.queryBpmn(username);

		Map<String, List<SysTodoCountDTO>> bpmnMap = sysTodoCountDTOS.stream().collect(Collectors.groupingBy(sysTodoCountDTO -> {
			String processCode = sysTodoCountDTO.getProcessCode();
			TodoTaskEnum todoTaskTypeEnum = TodoTaskEnum.getByTypeV2(processCode);
			if (Objects.nonNull(todoTaskTypeEnum)) {
				return todoTaskTypeEnum.getType();
			} else {
				return processCode;
			}
		}));

		List<SysMessageTypeDTO> bpmnList = new ArrayList<>();
		bpmnMap.forEach((type, dtoList)->{
			SysTodoCountDTO sysTodoCountDTO = dtoList.get(0);
			int sum = dtoList.stream().mapToInt(SysTodoCountDTO::getUndoCount).sum();
			TodoTaskEnum typeEnum = TodoTaskEnum.getByType(type);

			SysMessageTypeDTO typeDTO = SysMessageTypeDTO.builder()
					.count(sum)
					.messageFlag("2")
					.title(Objects.isNull(typeEnum)? sysTodoCountDTO.getProcessName():typeEnum.getModule())
					.busType(Objects.isNull(typeEnum)? sysTodoCountDTO.getProcessCode(): typeEnum.getType())
					.build();

			setPicture(typeDTO, type);

			// 查询最近一条的时间
			List<String> stringList = dtoList.stream().map(SysTodoCountDTO::getProcessCode).collect(Collectors.toList());

			SysTodoList sysTodoList = sysTodoListService.queryBpmnLast(stringList, username);
			if (Objects.nonNull(sysTodoList)) {
				typeDTO.setIntervalTime(Objects.isNull(sysTodoList.getUpdateTime())? sysTodoList.getCreateTime():sysTodoList.getUpdateTime());
				typeDTO.setTitleContent(Objects.isNull(sysTodoList.getTaskName())?sysTodoList.getPublishingContent():sysTodoList.getTaskName());
			}
			bpmnList.add(typeDTO);
		});

		list.addAll(bpmnList);

		//list根据创建时间排序
		list = list.stream().sorted(Comparator.comparing(SysMessageTypeDTO::getIntervalTime).reversed()).collect(Collectors.toList());
		//集合设置id
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setId(String.valueOf(i));
		}
		return list;
	}

	public SysMessageTypeDTO setPicture(SysMessageTypeDTO sysMessageTypeDTO,String type){
		//设置消息类型和头像
		if (SysParamCodeConstant.FAULT.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.FAULT_FLOW);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.PATROL.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.PATROL_FLOW);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.INSPECTION.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_FLOW);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.EMERGENCY.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.EMERGENCY_FLOW);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.WEEK_PLAN.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.WEEK_PLAN);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.FIXED_ASSETS.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.FIXED_ASSETS);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.BD_WORK_TITCK.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.BD_WORK_TITCK);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		if (SysParamCodeConstant.SPAREPART.equals(type)) {
			SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SPARE_PART);
			sysMessageTypeDTO.setValue(sysParamModel.getValue());
		}
		return sysMessageTypeDTO;
	}

	@Override
	public IPage<SysMessageInfoDTO> queryMessageInfo(Page<SysMessageInfoDTO> page,String messageFlag, String todoType, String keyWord, String busType,String msgCategory) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		String username = loginUser.getUsername();

		//业务数据
		if ("1".equals(messageFlag)) {
			SysAnnmentEnum annmentEnum = SysAnnmentEnum.getByType(busType);
			List<String> enumList = null;
			if (Objects.nonNull(annmentEnum)) {
				enumList = annmentEnum.getList();
			}

			if (CollUtil.isEmpty(enumList) && StrUtil.isNotBlank(busType)) {
				enumList = Collections.singletonList(busType);
			}
			IPage<SysMessageInfoDTO> businessList = sysAnnouncementMapper.queryAnnouncementInfo(page, userId, keyWord, enumList, msgCategory);
			//设置bpmn类型的数据的返回类型为各自模块类型
			List<SysMessageInfoDTO> records = businessList.getRecords();
			SysAnnmentTypeEnum[] values = SysAnnmentTypeEnum.values();
			for (SysAnnmentTypeEnum value : values) {
				String type = value.getType();
				List<SysMessageInfoDTO> collect = records.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getProcessCode() != null && SysAnnmentTypeEnum.BPM.getType().equals(sysMessageInfoDTO.getTaskType()) && sysMessageInfoDTO.getProcessCode().contains(type)).collect(Collectors.toList());
				for (SysMessageInfoDTO sysMessageInfoDTO : collect) {
					sysMessageInfoDTO.setTaskType(type);
				}
			}
			//接收时间为空，则接收时间等于创建时间
			for (SysMessageInfoDTO record : records) {
				if (ObjectUtil.isEmpty(record.getReceiveTime())) {
					record.setReceiveTime(record.getIntervalTime());
				}else{
					record.setIntervalTime(record.getReceiveTime());
				}
				//系统公告和系统消息，特情取消去办理
				if(StrUtil.isNotEmpty(record.getMsgCategory()) && StrUtil.isEmpty(record.getTaskType()) || SysAnnmentTypeEnum.SITUATION.getType().equals(record.getTaskType())){
					record.setDeal(false);
				}else{
					record.setDeal(true);
				}
				//设置类型
				String s = record.getTaskType();
				if(StrUtil.isEmpty(s)){
					record.setTaskType(null);
				}else{
					record.setOriginalType(s);
					String type = StrUtil.splitTrim(s, "_").get(0);
					if(type.equals("patrol") || type.equals("inspection")){
						record.setTaskType(type);
					}
					if(type.equals("asset")){
						record.setTaskType("fixed");
					}
					if("bpm".equals(s)){
						String processCode = record.getProcessCode();
						String taskType = StrUtil.splitTrim(processCode, "_").get(0);
						record.setTaskType(taskType);
					}
				}
			}
			return businessList;
		}
		//流程业务
		else if ("2".equals(messageFlag)) {
			TodoTaskEnum todoTaskEnum = TodoTaskEnum.getByType(busType);
			List<String> busTypeList = null;
			if (Objects.nonNull(todoTaskEnum)) {
				busTypeList = todoTaskEnum.getList();
			}

			if (CollUtil.isEmpty(busTypeList)) {
				busTypeList = Collections.singletonList(busType);
			}
			//流程数据
			IPage<SysMessageInfoDTO> flowList = sysAnnouncementMapper.queryTodoListInfo(page, username, todoType, keyWord, busTypeList);
			//设置bpmn类型的数据的返回类型为各自模块类型
			List<SysMessageInfoDTO> records = flowList.getRecords();
			TodoTaskTypeEnum[] values = TodoTaskTypeEnum.values();
				for (TodoTaskTypeEnum value : values) {
					String type = value.getType();
					List<SysMessageInfoDTO> collect = records.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getProcessCode() != null && TodoTaskTypeEnum.BPMN.getType().equals(sysMessageInfoDTO.getTaskType()) && sysMessageInfoDTO.getProcessCode().contains(type)).collect(Collectors.toList());
					for (SysMessageInfoDTO sysMessageInfoDTO : collect) {
						sysMessageInfoDTO.setTaskType(type);
					}
				}

			//接收时间为空，则接收时间等于创建时间
			for (SysMessageInfoDTO record : records) {
				if (ObjectUtil.isEmpty(record.getReceiveTime())) {
					record.setReceiveTime(record.getIntervalTime());
				}else{
					record.setIntervalTime(record.getReceiveTime());
				}
				//系统公告和系统消息，特情取消去办理
				if(StrUtil.isNotEmpty(record.getMsgCategory()) && StrUtil.isEmpty(record.getTaskType()) || SysAnnmentTypeEnum.SITUATION.getType().equals(record.getTaskType())){
					record.setDeal(false);
				}else{
					record.setDeal(true);
				}
				//固定资产下发需要给另外的类型
				if(StrUtil.isEmpty(record.getProcessInstanceId()) && record.getTaskType().equals(TodoTaskTypeEnum.FIXED_ASSETS.getType())){
					record.setTaskType("fixed");
				}
			}
			return flowList;
		}
		return null;
	}

	@Override
	public SysAnnouncementPageDTO queryPageNumber(Page<Object> page,String messageFlag, String todoType, String keyWord, String busType, String msgCategory) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		String username = loginUser.getUsername();
		SysAnnouncementPageDTO sysAnnouncementPageDTO = new SysAnnouncementPageDTO();
		//业务数据
		if("1".equals(messageFlag)){
			SysAnnmentEnum annmentEnum = SysAnnmentEnum.getByType(busType);
			List<String> enumList = null;
			if (Objects.nonNull(annmentEnum)) {
				enumList = annmentEnum.getList();
			}

			if (CollUtil.isEmpty(enumList) && StrUtil.isNotBlank(busType)) {
				enumList = Collections.singletonList(busType);
			}
			List<SysMessageInfoDTO> sysMessageInfoDTOS = sysAnnouncementMapper.queryAllAnnouncement(userId, keyWord, enumList, msgCategory);
			//获取未读的条数
			List<SysMessageInfoDTO> collect = sysMessageInfoDTOS.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getReadFlag().equals("0")).collect(Collectors.toList());
			//查找最远的未读消息
			Optional<SysMessageInfoDTO> min = sysMessageInfoDTOS.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getReadFlag().equals("0")).min(Comparator.comparing(SysMessageInfoDTO::getIntervalTime));
			int number = collect.size();
			//计算所在页码，数量
			sysAnnouncementPageDTO = queryPageNumberInfo(min,sysAnnouncementPageDTO,number,page);
			return sysAnnouncementPageDTO;
		}
		//流程业务
		else if ("2".equals(messageFlag)) {
			TodoTaskEnum todoTaskEnum = TodoTaskEnum.getByType(busType);
			List<String> busTypeList = null;
			if (Objects.nonNull(todoTaskEnum)) {
				busTypeList = todoTaskEnum.getList();
			}

			if (CollUtil.isEmpty(busTypeList)) {
				busTypeList = Collections.singletonList(busType);
			}
			//流程数据
			List<SysMessageInfoDTO> sysMessageInfoDTOS = sysAnnouncementMapper.queryAllTodoList(username, todoType, keyWord, busTypeList);
			//获取未办理的条数
			List<SysMessageInfoDTO> collect = sysMessageInfoDTOS.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getTodoType().equals("0")).collect(Collectors.toList());
			int number =collect.size();
			//查找最远的流程消息
			Optional<SysMessageInfoDTO> min = sysMessageInfoDTOS.stream().filter(sysMessageInfoDTO -> sysMessageInfoDTO.getTodoType().equals("0")).min(Comparator.comparing(SysMessageInfoDTO::getIntervalTime));
			sysAnnouncementPageDTO = queryPageNumberInfo(min,sysAnnouncementPageDTO,number,page);
			return sysAnnouncementPageDTO;
		}
		return null;
	}
	public SysAnnouncementPageDTO queryPageNumberInfo(Optional<SysMessageInfoDTO> min,SysAnnouncementPageDTO sysAnnouncementPageDTO,int number,Page<Object> page){
		//计算最远的未处理的流程消息的页码
		int pageNum = 0;
		String seq = null;
		String id = null;
		if (min.isPresent()) {
			SysMessageInfoDTO sysMessageInfoDTO = min.get();
			seq = sysMessageInfoDTO.getSeq();
			//去除末尾加.0的情况
			String s = seq;
			if(s.indexOf(".") > 0){
				//去掉多余的0
				s = s.replaceAll("0+?$", "");
				//如最后一位是.则去掉
				s = s.replaceAll("[.]$", "");
			}
			int num = Integer.parseInt(s);
			//计算所在页码
			long size = page.getSize();
			pageNum = Math.toIntExact(num / size);
			if(num%size !=0){
				pageNum = pageNum+1;
			}
			id = sysMessageInfoDTO.getId();
		}
		sysAnnouncementPageDTO.setId(id);
		sysAnnouncementPageDTO.setPageNumber(pageNum);
		sysAnnouncementPageDTO.setSeq(seq);
		sysAnnouncementPageDTO.setDateNumber(number);
		return sysAnnouncementPageDTO;
	}



}
