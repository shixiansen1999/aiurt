package com.aiurt.modules.train.mistakes.constant;
/**
 * 错题集相关常量
 *
 * @author 华宜威
 * @date 2023-08-25 11:28:03
 */
public interface BdExamMistakesConstant {

    /**错题集状态，1未开答，2待审核，3已驳回，4已通过*/
    Integer EXAM_MISTAKES_STATE_NOT_ANSWER = 1;
    Integer EXAM_MISTAKES_STATE_PENDING_REVIEW = 2;
    Integer EXAM_MISTAKES_STATE_REJECTED = 3;
    Integer EXAM_MISTAKES_STATE_PASSED = 4;
}
