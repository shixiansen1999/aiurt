package com.aiurt.common.exception;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/7/2011:39
 */
public class AiurtNoDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private List<String> data;

    public AiurtNoDataException(String message) {
        super(message);
    }

    public AiurtNoDataException(String message, List<String> data) {
        super(message);
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
