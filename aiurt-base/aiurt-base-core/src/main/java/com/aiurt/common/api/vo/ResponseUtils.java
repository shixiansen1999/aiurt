package com.aiurt.common.api.vo;

/**
 * @author: jeecg-boot
 */
public final class ResponseUtils {

    public static <T> ResponseBean<T> writeSuccessResult() {
        ResponseBean<T> responseBean = new ResponseBean<>();
        responseBean.setCode(I18nErrorEnum.CODE0.getCode());
        responseBean.setMsg("success");
        return responseBean;
    }

    public static <T> ResponseBean<T> writeSuccessResult(T result) {
        ResponseBean<T> responseBean = new ResponseBean<>();
        responseBean.setCode(I18nErrorEnum.CODE0.getCode());
        responseBean.setMsg("success");
        responseBean.setData(result);
        return responseBean;
    }

    public static <T> ResponseBean<T> writeErrorResult(int code, String message) {
        ResponseBean<T> responseBean = new ResponseBean<>();
        responseBean.setCode(code);
        responseBean.setMsg(message);
        return responseBean;
    }

    public static <T> ResponseBean<T> writeErrorResult(int code) {
        ResponseBean<T> responseBean = new ResponseBean<>();
        responseBean.setCode(code);
        responseBean.setMsg("");
        return responseBean;
    }

    public static <T> ResponseBean<T> writeErrorResult(String msg) {
        ResponseBean<T> responseBean = new ResponseBean<>();
        responseBean.setCode(I18nErrorEnum.ERROR400.getCode());
        responseBean.setMsg(msg);
        return responseBean;
    }

}
