package top.scraft.picmanserver.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class SacUserPrincipal implements Serializable {

    public static transient final long SAID_RGW = -1;

    private long said;
    private String username;
    private String nickname;

}
