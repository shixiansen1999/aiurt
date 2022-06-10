package com.aiurt.boot.modules.manage.model;

import lombok.Data;

import java.util.List;

@Data
public class SituationUserModel {
    private String title;
    private String value;
    private String key;
    private List<StationModel> children;
    private boolean disabled;
}
