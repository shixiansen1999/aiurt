package com.aiurt.boot.modules.patrol.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.NumberGenerate;
import com.swsc.copsms.modules.patrol.mapper.NumberGenerateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务编号生成工具类
 *
 * @description: NumberGenerateUtils
 * @author: Mr.zhao
 * @date: 2021/9/17 13:44
 */
@Component
@RequiredArgsConstructor
public class NumberGenerateUtils {

	private final NumberGenerateMapper numberGenerateMapper;


	/**
	 * 获取任务编号
	 *
	 * @param before 前四位
	 * @return {@link String}
	 */
	public String getCodeNo(String before) throws RuntimeException {

		//当前时间的4位
		String time = new SimpleDateFormat("yyMM").format(new Date());

		//所组成编号
		String no = before + PatrolConstant.NO_SPL + time + PatrolConstant.NO_SPL;

		NumberGenerate one = numberGenerateMapper.selectOne(new QueryWrapper<NumberGenerate>()
				.eq(NumberGenerate.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(NumberGenerate.NAME, before)
				.eq(NumberGenerate.CODE_TIME, time)
		);
		//获取编号
		Integer code = null;
		if (one == null) {
			NumberGenerate numberGenerate = new NumberGenerate().setCode(1).setDelFlag(0).setCodeTime(time).setName(before);
			int insert = numberGenerateMapper.insert(numberGenerate);
			if (insert < 1) {
				throw new RuntimeException("插入记录错误,请重新调用");
			}
			code = 1;
		} else {
			code = one.getCode() + 1;
			one.setCode(code);
			int update = numberGenerateMapper.updateById(one);
			if (update < 1) {
				throw new RuntimeException("更新记录错误,请重新调用");
			}
		}
		//格式化编号
		if (code < 10) {
			//个位数拼两个0
			no = no + "00" + code;
		} else if (code < 100) {
			//十位数拼一个0
			no = no + "0" + code;
		} else {
			no = no + code;
		}
		return no;
	}

}
