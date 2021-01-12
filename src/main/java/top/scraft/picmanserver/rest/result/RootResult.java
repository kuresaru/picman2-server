/*
 * Copyright (c) 2020
 * User: Twilight Ape (暮光小猿wzt)
 * File: RootResult.java
 * Date: 2020/12/17
 */

package top.scraft.picmanserver.rest.result;

import lombok.Data;

@Data
@Deprecated
public class RootResult {

    public static final String SUCCESS = "成功";
    public static final String ERROR_FORBIDDEN = "无权访问";
    public static final String ERROR_INVALID_PID = "无效的PID";

    private int code = 0;
    private String message = "";

    public RootResult status(int code, String msg) {
        this.code = code;
        this.message = msg;
        return this;
    }

    public RootResult forbidden() {
        return status(403, ERROR_FORBIDDEN);
    }

    public RootResult forbidden(String msg) {
        return status(403, msg);
    }

    public RootResult ok() {
        return status(200, SUCCESS);
    }

}
