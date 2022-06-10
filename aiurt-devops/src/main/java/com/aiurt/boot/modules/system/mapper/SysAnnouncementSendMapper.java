package com.aiurt.boot.modules.system.mapper;

import java.util.List;

import com.aiurt.boot.modules.system.model.AnnouncementSendModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.system.entity.SysAnnouncementSend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

/**
 * @Description: 用户通告阅读标记表
 * @Author: swsc
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Mapper
@Repository
public interface SysAnnouncementSendMapper extends BaseMapper<SysAnnouncementSend> {

	public List<String> queryByUserId(@Param("userId") String userId);

	/**
	 * @功能：获取我的消息
	 * @param announcementSendModel
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public List<AnnouncementSendModel> getMyAnnouncementSendList(Page<AnnouncementSendModel> page, @Param("announcementSendModel") AnnouncementSendModel announcementSendModel);

}
