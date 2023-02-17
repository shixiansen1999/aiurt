package com.aiurt.modules.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.oConvertUtils;
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
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveAnnouncement(SysAnnouncement sysAnnouncement) {
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
		LambdaQueryWrapper<SysTodoList> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.ne(SysTodoList::getTodoType,1)
				.eq(SysTodoList::getActualUserName,username);
		List<SysTodoList> sysTodoLists = sysTodoListMapper.selectList(lambdaQueryWrapper);
		//业务消息处理
		Map<String, List<SysAnnouncementSendDTO>> collect = sysAnnouncementSendDTOS.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getBusType() != null).collect(Collectors.groupingBy(SysAnnouncementSendDTO::getBusType));
		for (Map.Entry<String, List<SysAnnouncementSendDTO>> entry : collect.entrySet()) {
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			// 通过key拿名称
			String key = entry.getKey();
			String module = SysAnnmentTypeEnum.getByType(key).getModule();
			sysMessageTypeDTO.setTitle(module);
			// 统计长度
			List<SysAnnouncementSendDTO> value = entry.getValue();
			int size = value.size();
			sysMessageTypeDTO.setCount(size);
			//对list进行比较，相同名称，则数量相加
			for (SysMessageTypeDTO messageTypeDTO : list) {
				if(messageTypeDTO.getTitle().equals(module)){
					sysMessageTypeDTO.setTitle(messageTypeDTO.getTitle());
					sysMessageTypeDTO.setCount(messageTypeDTO.getCount()+size);
				}
			}
			//获取时间，在对list进行排序
			Collections.sort(value,((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			if(CollUtil.isNotEmpty(value)){
				SysAnnouncementSendDTO lastSysAnnouncementSendDTO = value.get(value.size() - 1);
				sysMessageTypeDTO.setIntervalTime(lastSysAnnouncementSendDTO.getCreateTime());
			}
			sysMessageTypeDTO.setMessageFlag("1");
			list.add(sysMessageTypeDTO);
		}
		//bus_type为空的数据
		Map<String, List<SysAnnouncementSendDTO>> collect2 = sysAnnouncementSendList.stream().filter(sysAnnouncementSendDTO -> sysAnnouncementSendDTO.getMsgCategory() != null).collect(Collectors.groupingBy(SysAnnouncementSendDTO::getMsgCategory));
		for (Map.Entry<String, List<SysAnnouncementSendDTO>> entry : collect2.entrySet()) {
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			// 通过key拿名称
			String key = entry.getKey();
			if("1".equals(key)){
				sysMessageTypeDTO.setTitle("系统公告");
			}
			if("2".equals(key)){
				sysMessageTypeDTO.setTitle("系统消息");
			}
			if("3".equals(key)){
				sysMessageTypeDTO.setTitle("特情消息");
			}
			// 统计长度
			List<SysAnnouncementSendDTO> value = entry.getValue();
			int size = value.size();
			sysMessageTypeDTO.setCount(size);
			//获取时间，在对list进行排序
			Collections.sort(value,((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			if(CollUtil.isNotEmpty(value)) {
				SysAnnouncementSendDTO lastSysAnnouncementSendDTO = value.get(value.size() - 1);
				sysMessageTypeDTO.setIntervalTime(lastSysAnnouncementSendDTO.getCreateTime());
			}
			sysMessageTypeDTO.setMessageFlag("0");
			list.add(sysMessageTypeDTO);
		}

		//流程消息处理
		Map<String, List<SysTodoList>> collect1 = sysTodoLists.stream().filter(sysTodoList -> sysTodoList.getTaskType() !=null).collect(Collectors.groupingBy(SysTodoList::getTaskType));
		for (Map.Entry<String, List<SysTodoList>> entry : collect1.entrySet()) {
			SysMessageTypeDTO sysMessageTypeDTO = new SysMessageTypeDTO();
			// 通过key拿名称
			String key = entry.getKey();
			String module = TodoTaskTypeEnum.getByType(key).getModule();
			sysMessageTypeDTO.setTitle(module);
			// 统计长度
			List<SysTodoList> value = entry.getValue();
			int size = value.size();
			sysMessageTypeDTO.setCount(size);
			//对list进行比较，相同名称，则数量相加
			for (SysMessageTypeDTO messageTypeDTO : list) {
				if(messageTypeDTO.getTitle().equals(module)){
					sysMessageTypeDTO.setTitle(messageTypeDTO.getTitle());
					sysMessageTypeDTO.setCount(messageTypeDTO.getCount()+size);
				}
			}
			//获取时间，在对list进行排序
			Collections.sort(value,((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
			if(CollUtil.isNotEmpty(value)) {
				SysTodoList sysTodoList = value.get(value.size() - 1);
				sysMessageTypeDTO.setIntervalTime(sysTodoList.getCreateTime());
			}
			sysMessageTypeDTO.setMessageFlag("2");
			list.add(sysMessageTypeDTO);
		}



		return list;
	}

	@Override
	public List<SysMessageInfoDTO> queryMessageInfo(String messageFlag, String todoType,String keyWord,String busType) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();
		String username = loginUser.getUsername();
		if(("1").equals(messageFlag)){
			List<SysMessageInfoDTO> businessList = sysAnnouncementMapper.queryAnnouncementInfo(userId, keyWord,busType);
			return businessList;
		} else if(("2").equals(messageFlag)){
			List<SysMessageInfoDTO> flowList = sysAnnouncementMapper.queryTodoListInfo(username, todoType, keyWord);
		return flowList;
		}
		return null;
	}

}
