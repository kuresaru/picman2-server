package top.scraft.picmanserver.service;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import top.scraft.picmanserver.data.PictureDetails;

import java.io.File;
import java.io.IOException;

public interface PictureService {

    @Nullable
    @Deprecated
    PictureDetails getDetails(String pid);

    @Nullable
    PictureDetails getDetails(String pid, long lid);

    boolean isInLibrary(String pid, long lid);

    /**
     * 上传图片并设置图片存在
     *
     * @param pid 图片id
     * @param file 上传文件
     * @throws IOException
     */
    void uploadPicture(String pid, MultipartFile file) throws IOException;

    File getPictureFile(String pid);

    File getThumbFile(String pid);

}
