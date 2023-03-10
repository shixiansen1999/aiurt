package com.aiurt.modules.system.mapper;

import java.util.List;

import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.modules.system.dto.SysAnnouncementSendDTO;
import com.aiurt.modules.system.dto.SysAnnouncementTypeCountDTO;
import com.aiurt.modules.system.dto.SysMessageInfoDTO;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
     * 查找所有未办理的流程
     * @param userName
     * @return
     */
    List<SysTodoList> queryTodoList(@Param("userName")String userName);

    /**
     * 查询当前登录人未读的公告详情
     * @param page
     * @param userId
     * @param keyWord
     * @param busType
     * @param msgCategory
     * @return
     */
    IPage<SysMessageInfoDTO> queryAnnouncementInfo(@Param("page") Page<SysMessageInfoDTO> page, @Param("userId")String userId, @Param("keyWord")String keyWord, @Param("busType")String busType, @Param("msgCategory")String msgCategory);


    /**
     * 查询当前登录人未读的公告详情不分页
     * @param userId
     * @param keyWord
     * @param busType
     * @param msgCategory
     * @return
     */
    List<SysMessageInfoDTO> queryAllAnnouncement(@Param("userId")String userId, @Param("keyWord")String keyWord, @Param("busType")String busType, @Param("msgCategory")String msgCategory);

    /**
     * 查询流程消息的详情
     * @param page
     * @param userName
     * @param todoType
     * @param keyWord
     * @param busType
     * @return
     */
    IPage<SysMessageInfoDTO> queryTodoListInfo(@Param("page") Page<SysMessageInfoDTO> page,@Param("userName")String userName,@Param("todoType")String todoType,@Param("keyWord")String keyWord,@Param("busType")String busType);

    /**
     * 查询流程消息的详情不分页
     * @param userName
     * @param keyWord
     * @param busType
     * @param todoType
     * @return
     */
    List<SysMessageInfoDTO> queryAllTodoList(@Param("userName")String userName,@Param("todoType")String todoType,@Param("keyWord")String keyWord,@Param("busType")String busType);

    /**
     * 消息统计
     * @param userId
     * @return
     */
    List<SysAnnouncementTypeCountDTO> queryTypeCount(@Param("userId") String userId);

    /**
     *
     * @param userId
     * @return
     */
    List<SysAnnouncementTypeCountDTO> queryBNullTypeCount(@Param("userId") String userId);

    /**
     * 查询最近的一条
     * @param userId
     * @param busTypeList
     * @return
     */
    SysAnnouncementSend queryLast(@Param("userId") String userId, @Param("list") List<String> busTypeList, @Param("msgCategory") String msgCategory);
}
