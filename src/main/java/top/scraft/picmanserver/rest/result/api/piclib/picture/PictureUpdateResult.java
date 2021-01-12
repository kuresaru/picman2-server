package top.scraft.picmanserver.rest.result.api.piclib.picture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.scraft.picmanserver.rest.result.api.piclib.PictureResult;

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureUpdateResult extends PictureResult {

    private boolean needUpload;

}
