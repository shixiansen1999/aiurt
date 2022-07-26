package com.aiurt.modules.train.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;

import java.util.List;

/**
 * @Description: 培训签到记录
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdTrainTaskSignMapper extends BaseMapper<BdTrainTaskSign> {

	public boolean deleteByMainId(@Param("mainId") String mainId);

	public List<BdTrainTaskSign> selectByMainId(@Param("mainId") String mainId);
	//根据培训任务id查询培训人员
    int getByTaskId(String trainTaskId);

    //根据培训任务id查询已签到的培训人员
	int getSignByTaskId(String trainTaskId);

	BdTrainTaskSign getSign(@Param("trainTaskId") String trainTaskId, @Param("userId") String userId);
}
