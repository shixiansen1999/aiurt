package com.aiurt.boot.standard.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.standard.dto
 * @className: SysDictDTO
 * @author: life-0
 * @date: 2022/7/4 11:36
 * @description: TODO
 * @version: 1.0
 */
@Data
public class SysDictDTO {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 描述
     */
    private String description;


}
