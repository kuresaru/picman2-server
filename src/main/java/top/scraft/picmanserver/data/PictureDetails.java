package top.scraft.picmanserver.data;

import lombok.Data;
import top.scraft.picmanserver.dao.Picture;

import java.util.Set;

@Data
public class PictureDetails {

    private String pid;
    private String description;
    private Set<String> tags;
    private long fileSize;
    private int width;
    private int height;
    private long createTime;
    private long lastModify;
    private boolean valid;
    private long accessLibrary;

    public static PictureDetails fromPicture(Picture p) {
        return fromPicture(p, -1);
    }

    public static PictureDetails fromPicture(Picture p, long accessLibrary) {
        PictureDetails d = new PictureDetails();
        d.setPid(p.getPid());
        d.setDescription(p.getDescription());
        d.setTags(p.getTags());
        d.setFileSize(p.getFileSize());
        d.setWidth(p.getWidth());
        d.setHeight(p.getHeight());
        d.setCreateTime(p.getCreateTime());
        d.setLastModify(p.getLastModify());
        d.setValid(p.isValid());
        d.setAccessLibrary(accessLibrary);
        return d;
    }

}
