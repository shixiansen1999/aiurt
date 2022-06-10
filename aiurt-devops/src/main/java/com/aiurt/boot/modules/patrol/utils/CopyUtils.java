package com.aiurt.boot.modules.patrol.utils;

import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import com.swsc.copsms.modules.patrol.entity.PatrolPoolContent;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 复制类
 *
 * @description: CopyUtils
 * @author: Mr.zhao
 * @date: 2021/9/16 21:11
 */
public class CopyUtils {


	/**
	 * 复制巡逻池内容
	 *
	 * @param list 列表
	 * @param id   id
	 * @return {@link List}<{@link PatrolPoolContent}>
	 */
	public static List<PatrolPoolContent> copyPatrolPoolContent(List<PatrolContent> list, Long id){
		List<PatrolPoolContent> voList = new ArrayList<>();
		for (PatrolContent t : list) {
			PatrolPoolContent content = new PatrolPoolContent();
			BeanUtils.copyProperties(t,content);
			content.setPatrolPoolId(id);
			voList.add(content);
		}
		return voList;
	}

}
