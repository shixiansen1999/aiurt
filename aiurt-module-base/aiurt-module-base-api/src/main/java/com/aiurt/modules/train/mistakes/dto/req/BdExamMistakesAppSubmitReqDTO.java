package com.aiurt.modules.train.mistakes.dto.req;

import com.aiurt.modules.train.exam.dto.BdExamRecordDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 错题集-app端填写错题后提交的请求DTO
 *
 * @author 华宜威
 * @date 2023-08-28 11:59:31
 */
@Data
public class BdExamMistakesAppSubmitReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**错题集id*/
    @ApiModelProperty(value = "错题集id", required = true)
    private String id;

    /**开始答错题集的时间*/
    @ApiModelProperty(value = "开始答错题集的时间", required = true)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**提交答错题集的时间*/
    @ApiModelProperty(value = "提交答错题集的时间", required = true)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**选项内容*/
    @ApiModelProperty(value = "选项内容")
    private List<BdExamRecordDTO> bdExamRecordDTOList;
}
