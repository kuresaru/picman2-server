package top.scraft.picmanserver.rest.result;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    public static final String SUCCESS = "成功";
    public static final String FORBIDDEN = "无权访问";

    public static final String ERROR_INVALID_PID = "无效的PID";
    public static final String ERROR_LIBRARY_LIMIT = "图库数量已达上限";
    public static final String ERROR_ALREADY_EXISTS = "已存在";
    public static final String ERROR_CHECKSUM_ERROR = "文件校验不通过";
    public static final String ERROR_IO = "服务器文件读写失败";

    private int code = 0;
    private String message = "";
    private T data;

    /**
     * 200 数据
     *
     * @param data
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> ok(R data) {
        return ResponseEntity.ok(new Result<>(200, SUCCESS, data));
    }

    /**
     * 200
     *
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> ok() {
        return ok(null);
    }

    /**
     * 400 错误信息
     *
     * @param message
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> badRequest(String message) {
        return status(HttpStatus.BAD_REQUEST, message, null);
    }

    /**
     * 403 错误信息
     *
     * @param message
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> forbidden(String message) {
        return status(HttpStatus.FORBIDDEN, message, null);
    }

    /**
     * 403
     *
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> forbidden() {
        return status(HttpStatus.FORBIDDEN, FORBIDDEN, null);
    }

    /**
     * 404
     *
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> notFound() {
        return ResponseEntity.notFound().build();
    }

    public static <R> ResponseEntity<Result<R>> ioError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_IO, null);
    }

    /**
     * 自定义
     *
     * @param status
     * @param message
     * @param data
     * @param <R>
     * @return
     */
    public static <R> ResponseEntity<Result<R>> status(HttpStatus status, String message, R data) {
        return ResponseEntity.status(status).body(new Result<>(status.value(), message, data));
    }

}
