package com.aiurt.boot.task.controller;

import com.aiurt.boot.pool.TaskPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时生成巡检任务触发接口(供测试用)
 */
@RestController
public class PatrolPoolController {
    @Autowired
    private TaskPool pool;

    @RequestMapping(value = "/taskpool", method = RequestMethod.POST)
    public void taskPool() {
        pool.execute();
    }
}
