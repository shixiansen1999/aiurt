package com.aiurt.boot.task.controller;

import com.aiurt.boot.pool.PatrolTaskMissingDetection;
import com.aiurt.boot.pool.TaskPool;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PatrolPoolController {
    @Autowired
    private TaskPool pool;
    @Autowired
    private PatrolTaskMissingDetection taskMissingDetection;

    /**
     * 定时生成巡检任务触发接口(供测试用)
     */
    @RequestMapping(value = "/taskpool", method = RequestMethod.POST)
    public Result<?> taskPool() {
        pool.execute();
        return Result.ok();
    }

    /**
     * 漏检任务检测触发接口(供测试用)
     *
     * @return
     */
    @RequestMapping(value = "/taskmiss", method = RequestMethod.POST)
    public Result<?> taskMissDetection() {
        taskMissingDetection.execute();
        return Result.ok();
    }
}
