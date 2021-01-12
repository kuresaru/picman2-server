package top.scraft.picmanserver.data;

import lombok.Data;

@Data
public class PictureLibraryDetails {

    private long lid;
    private String name;
    private int picCount;
    private long lastUpdate;
    private boolean readonly;

}
