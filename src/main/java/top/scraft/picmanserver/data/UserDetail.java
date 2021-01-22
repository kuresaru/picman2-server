package top.scraft.picmanserver.data;

import lombok.Data;
import top.scraft.picmanserver.data.SacUserPrincipal;

@Data
public class UserDetail {

    private boolean admin;
    private SacUserPrincipal sacUserPrincipal;

}
