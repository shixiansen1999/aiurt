package com.aiurt.modules.worklog.task;

import com.aiurt.common.util.TaskStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkLogTask {

    private static final String WORK_TITLE = "工作日志未填写";

//	private final ISysUserService sysUserService;
// todo 待处理
  //  private final UserTaskService userTaskService;

    /**
     * 生成待办任务
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void setUserWorkTask(){

        if (!TaskStatusUtil.getTaskStatus()) {
            return;
        }

        Date date = new Date();
        LocalDate now = LocalDate.now();

        //todo 待处理
//        userTaskService.lambdaUpdate().eq(UserTask::getType, UserTaskConstant.USER_TASK_TYPE_4).lt(UserTask::getCreateTime,now).remove();
        // todo 后期修改
        List<LoginUser> userList = new ArrayList<>();
//		List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>()
//				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
//				.select(SysUser::getId,SysUser::getRealname)
//		);
        //todo 待处理
//        List<UserTask> taskList = new ArrayList<>();
//        userList.forEach(u->{
//                    UserTask userTask = new UserTask();
//                    userTask.setWorkTime(now)
//                            .setRealName(u.getRealname())
//                            .setUserId(u.getId())
//                            .setType(UserTaskConstant.USER_TASK_TYPE_4)
//                            .setLevel(1)
//                            .setStatus(PatrolConstant.DISABLE)
//                            .setTitle(WORK_TITLE)
//                            .setProductionTime(date);
//                    taskList.add(userTask);
//                }
//        );
//        userTaskService.saveBatch(taskList);
    }


}

