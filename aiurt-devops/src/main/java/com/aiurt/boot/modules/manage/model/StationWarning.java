package com.aiurt.boot.modules.manage.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StationWarning implements Serializable {
    String stationName;
    Integer status;
    Integer openStatus;
}
