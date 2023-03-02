package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.message.entity.SysMessageTemplate;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.system.dto.SysAnnouncementPageDTO;
import com.aiurt.modules.system.dto.SysAnnouncementSendDTO;
import com.aiurt.modules.system.dto.SysMessageInfoDTO;
import com.aiurt.modules.system.dto.SysMessageTypeDTO;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.system.mapper.SysAnnouncementMapper;
import com.aiurt.modules.system.mapper.SysAnnouncementSendMapper;
import com.aiurt.modules.system.service.ISysAnnouncementService;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.mapper.SysTodoListMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.min;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
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
	private SysParamMapper sysParamMapper;

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
		//获取当前登录人待办消息(业务消息)
		List<SysAnnouncementSendDTO> sysAnnouncementSendDTOS = sysAnnouncementMapper.queryAnnouncement(userId);
		//获取当前登录人待办消息(业务消息)消息类型为null
		List<SysAnnouncementSendDTO> sysAnnouncementSendList = sysAnnouncementMapper.queryAnnouncementByNull(userId);

		//获取当前登录人待办消息(流程消息)
		List<SysTodoList> sysTodoLists = sysAnnouncementMapper.queryTodoList(username);
		//头像集合
        List<String> pictureCode = new ArrayList<>();
		pictureCode.add(SysParamCodeConstant.FAULT);
		pictureCode.add(SysParamCodeConstant.INSPECTION);
		pictureCode.add(SysParamCodeConstant.PATROL);
		pictureCode.add(SysParamCodeConstant.EMERGENCY);
		pictureCode.add(SysParamCodeConstant.TRAIN);
		pictureCode.add(SysParamCodeConstant.WEEK_PLAN);
		pictureCode.add(SysParamCodeConstant.SITUATION);
		pictureCode.add(SysParamCodeConstant.WORKLOG);

		//业务消息处理
		SysAnnmentTypeEnum[] typeValues = SysAnnmentTypeEnum.values();
		//遍历消息类型枚举
		for (SysAnnmentTypeEnum value : typeValues) {
			String s = value.getType();
			String type = StrUtil.splitTrim(s, "_").get(0);
			String module = value.getModule();
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			List<SysAnnouncementSendDTO> typeList = new ArrayList<>();
			List<SysAnnouncementSendDTO> bpmList = new ArrayList<>();
			List<SysAnnouncementSendDTO> readFlagList = new ArrayList<>();
			List<SysAnnouncementSendDTO> readFlagBpmList = new ArrayList<>();
			//所有数据
			typeList = sysAnnouncementSendDTOS.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getBusType() != null && sysAnnouncementSendDTO.getBusType().contains(type)).collect(Collectors.toList());
			bpmList = sysAnnouncementSendDTOS.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getProcessCode() != null && sysAnnouncementSendDTO.getProcessCode().contains(type)).collect(Collectors.toList());
			typeList.addAll(bpmList);
			//所有未读的消息
			readFlagList = sysAnnouncementSendDTOS.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getBusType() != null && sysAnnouncementSendDTO.getReadFlag().equals("0") && sysAnnouncementSendDTO.getBusType().contains(type)).collect(Collectors.toList());
			readFlagBpmList = sysAnnouncementSendDTOS.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getProcessCode() != null && sysAnnouncementSendDTO.getReadFlag().equals("0") && sysAnnouncementSendDTO.getProcessCode().contains(type)).collect(Collectors.toList());
			readFlagList.addAll(readFlagBpmList);
			//设置消息类型
			sysMessageTypeDTO.setBusType(type);
			//设置类型名称
			sysMessageTypeDTO.setTitle(module);
			//给不同类型赋值不同图片
			for (String pCode : pictureCode) {
				if(pCode.equals(type)){
					SysParamModel sysParamModel = sysParamAPI.selectByCode(pCode);
					sysMessageTypeDTO.setValue(sysParamModel.getValue());
				}

			}
			// 统计长度
			int size = typeList.size();
			int messageSize = readFlagList.size();
			sysMessageTypeDTO.setCount(messageSize);
			//给内容赋值
			if(size != 0) {
				//根据时间排序，最近的在上面
				typeList = typeList.stream().sorted(Comparator.comparing(SysAnnouncementSendDTO::getCreateTime).reversed()).collect(Collectors.toList());
			}
			if(CollUtil.isNotEmpty(typeList)) {
				String msgContent = typeList.get(0).getMsgContent();
				sysMessageTypeDTO.setTitleContent(msgContent);
			}
			//获取时间，在对list进行排序
			if(size != 0) {
				Collections.sort(typeList, ((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			}
			if(CollUtil.isNotEmpty(typeList)){
				SysAnnouncementSendDTO lastSysAnnouncementSendDTO = typeList.get(0);
				sysMessageTypeDTO.setIntervalTime(lastSysAnnouncementSendDTO.getCreateTime());
			}
			sysMessageTypeDTO.setMessageFlag("1");
			if(!("bpm").equals(sysMessageTypeDTO.getBusType()) && messageSize != 0){
				list.add(sysMessageTypeDTO);
			}
		}

		//bus_type为空的数据
		Map<String, List<SysAnnouncementSendDTO>> collect2 = sysAnnouncementSendList.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getMsgCategory() != null).collect(Collectors.groupingBy(SysAnnouncementSendDTO::getMsgCategory));
		for (Map.Entry<String, List<SysAnnouncementSendDTO>> entry : collect2.entrySet()) {
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			// 通过key拿名称
			String key = entry.getKey();
			if("1".equals(key)){
				sysMessageTypeDTO.setTitle("系统公告");
				sysMessageTypeDTO.setBusType(null);
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SYS_ANNOUNCEMENT);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("1");
			}
			if("2".equals(key)){
				sysMessageTypeDTO.setTitle("系统消息");
				//设置消息类型
				sysMessageTypeDTO.setBusType(null);
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SYS_MESSAGE);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("2");
			}
			if("3".equals(key)){
				sysMessageTypeDTO.setTitle("特情消息");
				//设置消息类型
				sysMessageTypeDTO.setBusType("situation");
				SysParamModel sysParamModel = sysParamAPI.selectByCode(SysParamCodeConstant.SITUATION);
				sysMessageTypeDTO.setValue(sysParamModel.getValue());
				sysMessageTypeDTO.setMsgCategory("3");
			}
			// 统计长度
			List<SysAnnouncementSendDTO> value = entry.getValue();
			int messageSize = 0;
			if(CollUtil.isNotEmpty(value)){
				List<SysAnnouncementSendDTO> messageList = value.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getMsgCategory() != null && sysAnnouncementSendDTO.getReadFlag().equals("0")).collect(Collectors.toList());
				messageSize = messageList.size();
			}
			sysMessageTypeDTO.setCount(messageSize);
			//给内容赋值
			value=value.stream().sorted(Comparator.comparing(SysAnnouncementSendDTO::getCreateTime).reversed()).collect(Collectors.toList());
			String msgContent = value.get(0).getMsgContent();
			sysMessageTypeDTO.setTitleContent(msgContent);
			//获取时间，在对list进行排序
			Collections.sort(value,((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			if(CollUtil.isNotEmpty(value)) {
				SysAnnouncementSendDTO lastSysAnnouncementSendDTO = value.get(0);
				sysMessageTypeDTO.setIntervalTime(lastSysAnnouncementSendDTO.getCreateTime());
			}
			sysMessageTypeDTO.setMessageFlag("1");
			list.add(sysMessageTypeDTO);
		}

		//流程消息处理
		TodoTaskTypeEnum[] values = TodoTaskTypeEnum.values();
		//遍历消息类型枚举
		for (TodoTaskTypeEnum value : values) {
			String type = value.getType();
			String module = value.getModule();
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			List<SysTodoList> typeList= sysTodoLists.stream().filter(sysTodoList -> sysTodoList.getTaskType() != null && sysTodoList.getTaskType().contains(type)).collect(Collectors.toList());
			List<SysTodoList> bpmnList = sysTodoLists.stream().filter(sysTodoList -> sysTodoList.getProcessCode() != null && sysTodoList.getProcessCode().contains(type)).collect(Collectors.toList());
			typeList.addAll(bpmnList);
			//所有未处理的流程消息
			List<SysTodoList> readFlagList= sysTodoLists.stream().filter(sysTodoList -> sysTodoList.getTaskType() != null && sysTodoList.getTodoType().equals("0") && sysTodoList.getTaskType().contains(type)).collect(Collectors.toList());
			List<SysTodoList> readFlagBpmList = sysTodoLists.stream().filter(sysTodoList -> sysTodoList.getProcessCode() != null && sysTodoList.getTodoType().equals("0") && sysTodoList.getProcessCode().contains(type)).collect(Collectors.toList());
			readFlagList.addAll(readFlagBpmList);
			//设置流程名称
			sysMessageTypeDTO.setTitle(module);
			//设置不同类型流程图片
			sysMessageTypeDTO = setPicture(sysMessageTypeDTO, type);
			//设置流程类型
			sysMessageTypeDTO.setBusType(type);
			// 统计长度
			int size = typeList.size();
			int messageSize = readFlagList.size();
			sysMessageTypeDTO.setCount(messageSize);
			//给内容赋值
			if(size != 0){
				typeList=typeList.stream().sorted(Comparator.comparing(SysTodoList::getCreateTime).reversed()).collect(Collectors.toList());
				String msgContent = typeList.get(0).getTaskName();
				sysMessageTypeDTO.setTitleContent(msgContent);
			}
			//获取时间，在对list进行排序
			if(size !=0){
				Collections.sort(typeList,((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			}
			if(CollUtil.isNotEmpty(typeList)) {
				SysTodoList sysTodoList = typeList.get(0);
				Date s = sysTodoList.getUpdateTime();
				String updateTime = DateUtil.formatDateTime(s);
				if(StrUtil.isNotBlank(updateTime)){
					sysMessageTypeDTO.setIntervalTime(s);
				}else{
					sysMessageTypeDTO.setIntervalTime(sysTodoList.getCreateTime());
				}
			}
			sysMessageTypeDTO.setMessageFlag("2");
			if(!("bpmn").equals(sysMessageTypeDTO.getBusType()) && messageSize != 0){
				list.add(sysMessageTypeDTO);
			}
		}

		//list去重，数量相加
		Map<String, SysMessageTypeDTO> productMap = new HashMap<String, SysMessageTypeDTO>(32);
		for (SysMessageTypeDTO product : list)
		{
			if (productMap.containsKey(product.getTitle()))
			{
				product.setCount(productMap.get(product.getTitle()).getCount());
			}
			productMap.put(product.getTitle(), product);
		}
		list.clear();// 清空栈内存
		for (Map.Entry<String, SysMessageTypeDTO> entry : productMap.entrySet()) {
			list.add(entry.getValue());
		}
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
		return sysMessageTypeDTO;
	}

	@Override
	public IPage<SysMessageInfoDTO> queryMessageInfo(Page<SysMessageInfoDTO> page,String messageFlag, String todoType, String keyWord, String busType,String msgCategory) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		String username = loginUser.getUsername();
		//业务数据
		if ("1".equals(messageFlag)) {
			IPage<SysMessageInfoDTO> businessList = sysAnnouncementMapper.queryAnnouncementInfo(page, userId, keyWord, busType, msgCategory);
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
				}
				//系统公告和系统消息，特情取消去办理
				if(StrUtil.isNotEmpty(record.getMsgCategory()) && StrUtil.isEmpty(record.getTaskType()) || SysAnnmentTypeEnum.SITUATION.getType().equals(record.getTaskType())){
					record.setDeal(false);
				}else{
					record.setDeal(true);
				}
				//设置类型
				String s = record.getTaskType();
				String type = StrUtil.splitTrim(s, "_").get(0);
				record.setTaskType(type);
			}
			return businessList;
		}
		//流程业务
		else if ("2".equals(messageFlag)) {
			//流程数据
			IPage<SysMessageInfoDTO> flowList = sysAnnouncementMapper.queryTodoListInfo(page, username, todoType, keyWord, busType);
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
				}
				//系统公告和系统消息，特情取消去办理
				if(StrUtil.isNotEmpty(record.getMsgCategory()) && StrUtil.isEmpty(record.getTaskType()) || SysAnnmentTypeEnum.SITUATION.getType().equals(record.getTaskType())){
					record.setDeal(false);
				}else{
					record.setDeal(true);
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
			List<SysMessageInfoDTO> sysMessageInfoDTOS = sysAnnouncementMapper.queryAllAnnouncement(userId, keyWord, busType, msgCategory);
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
			//流程数据
			List<SysMessageInfoDTO> sysMessageInfoDTOS = sysAnnouncementMapper.queryAllTodoList(username, todoType, keyWord, busType);
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
