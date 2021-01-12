package top.scraft.picmanserver.data;

import lombok.Data;

@Data
public class PictureLibraryContentDetails {

    private String pid;
    private long lastModify;
    private boolean valid;

}
