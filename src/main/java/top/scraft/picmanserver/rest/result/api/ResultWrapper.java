package top.scraft.picmanserver.rest.result.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultWrapper<T> {

    public static final String SUCCESS = "成功";
    public static final String ERROR_FORBIDDEN = "无权访问";
    public static final String ERROR_INVALID_PID = "无效的PID";

    private int code = 0;
    private String message = "";
    private T data;

    public ResultWrapper(T data) {
        this(200, SUCCESS, data);
    }

}
