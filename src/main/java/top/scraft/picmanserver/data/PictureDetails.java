package top.scraft.picmanserver.data;

import lombok.Data;

import java.util.Set;

@Data
public class PictureDetails {

    private String description;
    private Set<String> tags;
    private long fileSize;
    private int width;
    private int height;
    private long createTime;
    private long lastModify;
    private boolean valid;

}
