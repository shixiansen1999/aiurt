package com.aiurt.boot.modules.apphome.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.aiurt.boot.modules.apphome.param.HomeListParam;
import com.aiurt.boot.modules.apphome.vo.AppHomeVO;
import com.aiurt.boot.modules.apphome.vo.HomeVO;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: AppHomeService
 * @author: Mr.zhao
 * @date: 2021/10/1 13:08
 */
public interface AppHomeService {

	Result<HomeVO> getHomeList(HttpServletRequest req, HomeListParam param);

	IPage<UserTask> getHomeTaskList(HttpServletRequest req, HomeListParam param, Pageable pageable);

	AppHomeVO getHomeCount(HttpServletRequest req, HomeListParam param, AppHomeVO vo);
}
