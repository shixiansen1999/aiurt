package com.aiurt.boot.modules.appMessage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessagePageParam;
import com.aiurt.boot.modules.appMessage.vo.MessageStatusVO;
import com.aiurt.boot.modules.appMessage.vo.MessageUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date:   2021-10-29
 * @Version: V1.0
 */
public interface MessageMapper extends BaseMapper<Message> {

	/**
	 * 分页查询消息
	 *
	 * @param page  页面
	 * @param param 参数
	 * @return {@code IPage<MessageStatusVO>}
	 */
	IPage<MessageStatusVO> selectMessagePage(Page<MessageStatusVO> page, @Param("param") MessagePageParam param);

	/**
	 * 查询消息列表
	 *
	 * @param param 参数
	 * @return {@code List<Message>}
	 */
	List<Message> selectMessageList(@Param("param") MessagePageParam param);

	/**
	 * 查询未读消息数
	 *
	 * @param param 参数
	 * @return {@code Integer}
	 */
	Integer selectMessageUnReadCount(@Param("param")MessagePageParam param);

	/**
	 * 查询已读消息数
	 *
	 * @param param 参数
	 * @return {@code Integer}
	 */
	Integer selectMessageReadCount(@Param("param")MessagePageParam param);

	/**
	 * 查询用户信息信息
	 *
	 * @param page    页面
	 * @param message 消息
	 * @return {@code IPage<MessageUserVO>}
	 */
	IPage<MessageUserVO> selectUserMessagePage(Page<MessageUserVO> page,@Param("param") MessageUserVO message);



}
