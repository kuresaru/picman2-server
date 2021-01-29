package top.scraft.picmanserver.data;

import lombok.Data;

@Data
public class SacUserPrincipal {

    public static transient final long SAID_RGW = -1;

    private long said;
    private String username;
    private String nickname;

}
