package com.aiurt.modules.fault.dto;

import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.fault.dto
 * @className: UserTimeDTO
 * @author: life-0
 * @date: 2022/10/12 9:08
 * @description: TODO
 * @version: 1.0
 */
@Data
public class UserTimeDTO {

    private String userId;

    private String frrId;

    private Integer duration;

    /**
     * 重写equals方法
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if (o instanceof UserTimeDTO){
            UserTimeDTO userTimeDTO = (UserTimeDTO) o ;
            boolean equals = this.getUserId().equals(userTimeDTO.getUserId());
            boolean equals1 = this.getFrrId().equals(userTimeDTO.getFrrId());
            return (equals && equals1);
        }
        return false;
    }

    @Override
    public int hashCode() {
        String result = userId+frrId;
        return result.hashCode();
    }

}
