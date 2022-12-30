package com.aiurt.modules.sysfile.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: TypeNameVO
 * @author: Mr.zhao
 * @date: 2021/11/23 16:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UploadVO implements Serializable {

    private String userId;

    private Integer uploadTag;
}
