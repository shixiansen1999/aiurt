package com.aiurt.common.api.dto.message;

import com.aiurt.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

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

}
