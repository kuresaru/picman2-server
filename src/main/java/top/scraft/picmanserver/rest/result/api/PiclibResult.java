package top.scraft.picmanserver.rest.result.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.scraft.picmanserver.rest.result.RootResult;

@EqualsAndHashCode(callSuper = true)
@Data
public class PiclibResult extends RootResult {

    private long lid;

}
