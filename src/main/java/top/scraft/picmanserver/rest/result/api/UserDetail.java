package top.scraft.picmanserver.rest.result.api;

import lombok.Data;
import top.scraft.picmanserver.data.SacUserPrincipal;

@Data
public class UserDetail {

    private boolean admin;
    private SacUserPrincipal sacUserPrincipal;

}
