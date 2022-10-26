package com.aiurt.modules.schedule.mapper;


import com.aiurt.modules.schedule.entity.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * 根据用户姓名和功号查询用户id
     * @param realName
     * @param workNo
     * @return
     */
    LoginUser getUser(@Param("realName") String realName, @Param("workNo") String workNo);

    /**
     * 根据部门名称查询部门id
     * @param departName
     * @return
     */
    SysDepartModel getDepartByName(@Param("departName")String departName);
}
