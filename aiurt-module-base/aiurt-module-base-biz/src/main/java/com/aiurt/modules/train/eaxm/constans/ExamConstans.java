package com.aiurt.modules.train.eaxm.constans;
/**
 * Administrator
 * 2022/06/01
 * 常用常量
 * @author LKJ
 */
public class ExamConstans {
    //计划任务状态 0: 未发布 ；1：已发布；2：培训中；3：待考试；4：考试中；5：待复核；6;待评估；7：已完成
    /**
     * 0: 未发布
     * */
    public static final Integer UNPUBLISHED = 0;

    /**
     * 1：已发布
     * */
    public static final Integer PUBLISHED = 1;

    /**
     * 2：培训中
     * */
    public static final Integer IN_TRAINING = 2;

    /**
     * 3：待考试
     * */
    public static final Integer PENDING_EXAM = 3;

    /**
     * 4：考试中
     * */
    public static final Integer IN_EXAM = 4;

    /**
     * 5：待复核
     * */
    public static final Integer PENDING_REVIEW = 5;

    /**
     * 6;待评估
     * */
    public static final Integer TO_BE_ASSESSED= 6;

    /**
     * 7：已完成
     * */
    public static final Integer COMPLETED = 7;

    //考试计划状态 0未开始，1进行中，2待复核，3已结束，4待发布
    /**
     * 0：未开始
     * */
    public static final String RECORD_NOT_STARTED = "0";

    /**
     * 1：进行中
     * */
    public static final String RECORD_PROCESSING = "1";

    /**
     * 2：待复核
     * */
    public static final String RECORD_PENDING_REVIEW = "2";

    /**
     * 3：已结束
     * */
    public static final String RECORD_OVER = "3";

    /**
     * 4:待发布
     * */
    public static final String RECORD_PUBLISHED = "4";


    //考试状态 0：未考试，1：考试中，2：已考试
    /**
     * 0：未考试
     * */
    public static final String NOT_TESTED = "0";

    /**
     * 1：考试中
     * */
    public static final String IN_THE_EXAM= "1";

    /**
     * 2：已考试
     * */
    public static final String EXAMED = "2";

    //考试任务状态 0：未开始，1：进行中，2：待复核，3：已结束
    /**
     * 0：未开始
     * */
    public static final Integer NOT_STARTED = 0;

    /**
     * 1：进行中
     * */
    public static final Integer PROCESSING = 1;

    /**
     * 2：待复核
     * */
    public static final Integer PENDING_REVIEW_2 = 2;

    /**
     * 3：已结束
     * */
    public static final Integer OVER = 3;


}
