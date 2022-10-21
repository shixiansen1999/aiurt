package com.aiurt.modules.train.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: bd_question
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdQuestionService extends IService<BdQuestion> {

    /**
     * 查询习题信息
     * @param condition
     * @param pageList
     * @return
     */
    Page<BdQuestion> queryPageList(Page<BdQuestion> pageList,BdQuestion condition);

    /**
     * 添加习题
     * @param bdQuestion
     */
    void addBdQuestion(BdQuestion bdQuestion);


    /**
     *
     * 修改习题
     * @param bdQuestion
     */
    void updateBdQuestion(BdQuestion bdQuestion);


    /**
     * 查询
     * @param id
     * @return
     */
    BdQuestion bdQuestion  (@Param("id") String id);

    /**
     *
     * 查看学习资料
     * @param id
     * @return
     */
    List<BdQuestion> getLearningMaterials(@Param("id") String id);
}
