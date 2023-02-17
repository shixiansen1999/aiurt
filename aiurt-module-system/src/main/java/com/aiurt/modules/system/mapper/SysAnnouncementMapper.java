package com.aiurt.modules.system.mapper;

import java.util.List;

import com.aiurt.modules.system.dto.SysAnnouncementSendDTO;
import com.aiurt.modules.system.dto.SysMessageInfoDTO;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysAnnouncementMapper extends BaseMapper<SysAnnouncement> {

    /**
     * 通过消息类型和用户id获取系统通告
     * @param page
     * @param userId 用户id
     * @param msgCategory 消息类型
     * @return
     */
	List<SysAnnouncement> querySysCementListByUserId(Page<SysAnnouncement> page, @Param("userId")String userId,@Param("msgCategory")List<String> msgCategory);

    /**
     * 查询当前登录人未读的公告
     * @param userId
     * @return
     */
    List<SysAnnouncementSendDTO> queryAnnouncement(@Param("userId")String userId);

    /**
     * 查询当前登录人未读公告类型为null的数据
     * @param userId
     * @return
     */
    List<SysAnnouncementSendDTO> queryAnnouncementByNull(@Param("userId")String userId);

    /**
     * 查询当前登录人未读的公告详情
     * @param userId
     * @return
     */
    List<SysMessageInfoDTO> queryAnnouncementInfo(@Param("userId")String userId,@Param("keyWord")String keyWord,@Param("busType")String busType);

    /**
     * 查询流程消息的详情
     * @param userName
     * @param todoType
     * @param keyWord
     * @return
     */
    List<SysMessageInfoDTO> queryTodoListInfo(@Param("userName")String userName,@Param("todoType")String todoType,@Param("keyWord")String keyWord);
}
