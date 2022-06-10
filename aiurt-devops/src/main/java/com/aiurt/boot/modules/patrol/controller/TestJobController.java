package com.aiurt.boot.modules.patrol.controller;


import com.aiurt.boot.modules.patrol.task.TestJob;
import com.aiurt.common.util.QuartzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 调度任务demo
 *
 * @description: TestController
 * @author: Mr.zhao
 * @date: 2021/12/17 17:23
 */
@RestController
@RequestMapping("/testJob")
public class TestJobController {

	@Autowired
	private QuartzUtils quartzUtils;


	@GetMapping("/addJob")
	public void addJob(){

		Map map = new HashMap<>();
		map.put("data","data测试数据");
		quartzUtils.addJob("jab1","jabGroup1",
				"tri1","triGroup1",
				TestJob.class,"1/1 * * * * ?",map
				);
	}

	@GetMapping("/stopJob")
	public void stopJob(){
		quartzUtils.removeJob("jab1","jabGroup1",
				"tri1","triGroup1");
	}
}
