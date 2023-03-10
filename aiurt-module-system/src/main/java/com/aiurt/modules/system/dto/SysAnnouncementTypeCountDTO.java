package com.aiurt.modules.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class SysAnnouncementTypeCountDTO implements Serializable {
    private static final long serialVersionUID = -3916795189737349013L;

    private String busType;

    private String titile;


    private Integer  count;

    private Integer unreadCount;
}
