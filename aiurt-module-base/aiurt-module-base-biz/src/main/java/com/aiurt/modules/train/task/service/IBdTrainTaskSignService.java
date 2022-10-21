package com.aiurt.modules.train.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.task.dto.BdTrainSignDTO;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;

import java.util.List;

/**
 * @Description: 培训签到记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdTrainTaskSignService extends IService<BdTrainTaskSign> {

	/**
	 * 查询
	 * @param mainId
	 * @return
	 */
	public List<BdTrainTaskSign> selectByMainId(String mainId);
	/**
	 * 培训签到记录查询
	 * @param id
	 * @return
	 */
	List<BdTrainSignDTO> getById(String id);


}
