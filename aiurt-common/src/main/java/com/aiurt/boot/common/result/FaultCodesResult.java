package com.aiurt.boot.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class FaultCodesResult implements Serializable {

    private String code;

    private String faultPhenomenon;

}
