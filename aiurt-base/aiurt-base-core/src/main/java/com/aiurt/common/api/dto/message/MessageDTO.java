package com.aiurt.common.api.dto.message;

import com.aiurt.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 普通消息
 * @author: jeecg-boot
 */
@Data
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = -5690444483968058442L;

    /**
     * 发送人(用户登录账户)
     */
    protected String fromUser;

    /**
     * 发送给(用户登录账户)
     */
    protected String toUser;

    /**
     * 发送给所有人
     */
    protected Boolean toAll;

    /**
     * 消息主题
     */
    protected String title;

    /**
     * 消息内容
     */
    protected String content;

    /**
     * 消息类型 1:消息  2:系统消息 3:特情消息
     */
    protected String category;

    /**
     * 特情等级
     */
    protected String level;

    /**
     * 优先级（L低，M中，H高）
     */
    protected String priority;

    /**
     * 指定范围（组织机构）
     */
    protected String orgIds;

    /**
     * 开始时间
     */
    protected java.util.Date startTime;
    /**
     * 结束时间
     */
    protected java.util.Date endTime;
    /**
     * 摘要
     */
    private java.lang.String msgAbstract;
    /**
     * 发布内容
     */
    protected String publishingContent;

    //-----------------------------------------------------------------------
    //update-begin---author:taoyan ---date:20220705  for：支持自定义推送类型，邮件、钉钉、企业微信、系统消息-----------
    /**
     * 模板消息对应的模板编码
     */
    protected String templateCode;
    /**
     * 消息类型：org.jeecg.common.constant.enums.MessageTypeEnum
     *  XT("system",  "系统消息")
     *  YJ("email",  "邮件消息")
     *  DD("dingtalk", "钉钉消息")
     *  QYWX("wechat_enterprise", "企业微信")
     *  WX("wechat","微信")
     *  DX("short_message","短信")
     */
    protected String type;

    /**
     * 是否发送Markdown格式的消息
     */
    protected boolean isMarkdown;

    /**
     * 解析模板内容 对应的数据
     */
    protected Map<String, Object> data;
    //update-end---author:taoyan ---date::20220705  for：支持自定义推送类型，邮件、钉钉、企业微信、系统消息-----------
    //-----------------------------------------------------------------------

    public MessageDTO(){

    }

    /**
     * 构造器1 系统消息
     */
    public MessageDTO(String fromUser,String toUser,String title, String content){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.title = title;
        this.content = content;
        //默认 都是2系统消息
        this.category = CommonConstant.MSG_CATEGORY_2;
    }

    /**
     * 构造器2 支持设置category 1:消息  2:系统消息
     */
    public MessageDTO(String fromUser,String toUser,String title, String content, String category){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.title = title;
        this.content = content;
        this.category = category;
    }


    public boolean isMarkdown() {
        return this.isMarkdown;
    }

    public void setIsMarkdown(boolean isMarkdown) {
        this.isMarkdown = isMarkdown;
    }

}
