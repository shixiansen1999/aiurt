package com.aiurt.modules.train.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;

import java.util.List;

/**
 * @Description: 培训任务人员
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface BdTrainTaskUserMapper extends BaseMapper<BdTrainTaskUser> {

    /**
     * 根据任务id获取签到状态
     * @param taskId
     * @return
     */
    List<BdTrainTaskUser> taskUserList(String taskId);

    /**
     * 获取学生培训任务
     * @param pageList
     * @param id
     * @param taskName
     * @param taskId
     * @return
     */
    List<BdTrainTaskUser> getUserTasks(@Param("pageList") Page<BdTrainTaskUser> pageList, @Param("id")String id, @Param("taskName")String taskName,@Param("taskId") String taskId);

    /**
     * 修改签到状态
     * @param id
     * @param trainTaskId
     * @return
     */
    void updateSignState(@Param("id") String id, @Param("trainTaskId") String trainTaskId);

    /**
     * 修改反馈状态
     * @param id
     * @param trainTaskId
     * @return
     */
    void updateFeedState(@Param("id") String id, @Param("trainTaskId") String trainTaskId);
    /**
     * 根据任务id获取培训人员
     * @param taskId
     * @return
     * */
    List<BdTrainTaskUser> getUserListById(@Param("taskId") String taskId);

    /**
     * web-获取学生培训任务
     * @param pageList
     * @param uid
     * @param trainTaskUser
     * @return
     */
    List<BdTrainTaskUser> getUserTasksWeb(@Param("pageList")Page<BdTrainTaskUser> pageList, @Param("uid")String uid, @Param("condition")BdTrainTaskUser trainTaskUser);

    /**
     * 删除
     * @param mainId
     * @return
     */
    void deleteByMainId(@Param("mainId") String mainId);

    /**
     * 查询
     */
    void getUserListById();
}
