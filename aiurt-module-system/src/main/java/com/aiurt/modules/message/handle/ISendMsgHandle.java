package com.aiurt.modules.message.handle;

/**
 * @Description: 发送信息接口
 * @author: jeecg-boot
 */
public interface ISendMsgHandle {

    /**
     * 发送信息
     * @param esReceiver 发送人
     * @param esTitle 标题
     * @param esContent 内容
     */
	void sendMsg(String esReceiver, String esTitle, String esContent);
}
