package com.aiurt.modules.common.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class DepartUserTreeDTO implements Serializable {

    private String majorId;
    private String mark;
    private String username;
    private List<String> values;

    /**
     * 可选择机构
     */
    private Boolean isSelectOrg = true;
}
