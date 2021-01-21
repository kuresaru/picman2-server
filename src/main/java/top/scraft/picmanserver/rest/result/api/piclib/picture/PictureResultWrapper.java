package top.scraft.picmanserver.rest.result.api.piclib.picture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.scraft.picmanserver.rest.result.api.piclib.PictureResult;

@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class PictureResultWrapper<T> extends PictureResult {

    private T data;

}
