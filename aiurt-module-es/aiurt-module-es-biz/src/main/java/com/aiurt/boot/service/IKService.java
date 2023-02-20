package com.aiurt.boot.service;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/1716:20
 */
public interface IKService {
    /**
     * 验证当前是否是最新的head
     * @return
     */
    public String getCurrentNewModified();

    /**
     * 获取分词
     * @return
     */
    public String getDict();
}
