package com.aiurt.boot.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.PageVO;

/**
 * 分页获取
 *
 * @description: PageUtils
 * @author: Mr.zhao
 * @date: 2021/9/18 13:29
 */
public class PageUtils {

	/**
	 * 获取分页page
	 *
	 * @param t
	 * @param <T>
	 * @return
	 */
	public static <T,V extends PageVO> IPage<T> getPage(Class<T> clazz,V t) {
		if (t.getPageNo() == null) {
			t.setPageNo(1);
		}
		if (t.getPageSize() == null) {
			t.setPageSize(10);
		}
		return new Page<>(t.getPageNo(), t.getPageSize());
	}

}
