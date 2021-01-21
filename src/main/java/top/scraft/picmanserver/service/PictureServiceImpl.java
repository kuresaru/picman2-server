package top.scraft.picmanserver.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.scraft.picmanserver.utils.GifDecoder;
import top.scraft.picmanserver.dao.Picture;
import top.scraft.picmanserver.dao.PictureDao;
import top.scraft.picmanserver.data.PictureDetails;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PictureServiceImpl implements PictureService {

    @Value("${picman.storage.base-path}")
    private String storageBasePath;

    @Resource
    private PictureDao pictureDao;

    @Override
    @Nullable
    public PictureDetails getDetails(String pid) {
        Optional<Picture> pictureOptional = pictureDao.findById(pid);
        if (pictureOptional.isEmpty()) {
            return null;
        }
        Picture picture = pictureOptional.get();
        PictureDetails result = new PictureDetails();
        result.setDescription(picture.getDescription());
        result.setTags(picture.getTags());
        result.setFileSize(picture.getFileSize());
        result.setWidth(picture.getWidth());
        result.setHeight(picture.getHeight());
        result.setCreateTime(picture.getCreateTime());
        result.setLastModify(picture.getLastModify());
        result.setValid(picture.isValid());
        return result;
    }

    @Override
    public boolean isInLibrary(String pid, long lid) {
        return pictureDao.existsByPidAndLibraries_Lid(pid, lid);
    }

    @Override
    public void uploadPicture(String pid, MultipartFile file) throws IOException, IllegalArgumentException {
        Picture picture = pictureDao.findById(pid).orElseThrow(() -> new IllegalArgumentException("Picture not found: " + pid));
        // get meta
        BufferedImage image;
        if (pid.endsWith("gif")) {
            GifDecoder.GifImage gifImage = GifDecoder.read(file.getBytes());
            image = gifImage.getFrame(0);
        } else {
            image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        }
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            picture.setFileSize(file.getSize());
            picture.setWidth(image.getWidth());
            picture.setHeight(image.getHeight());
            picture.setValid(true);
        } else {
            throw new IllegalArgumentException("图片文件无效");
        }
        // save picture
        Path path = getPictureStorePath(pid);
        file.transferTo(path);
        // generate thumb (200*200px)
        int size = Math.min(image.getWidth(), image.getHeight());
        double scale = 200.0 / size;
        Thumbnails.of(image).sourceRegion(Positions.CENTER, size, size).scale(scale).toFile(getThumbFile(pid));
        // save meta
        pictureDao.save(picture);
    }

    @Override
    public File getPictureFile(String pid) {
        return getPictureStorePath(pid).toFile();
    }

    @Override
    public File getThumbFile(String pid) {
        File dir = new File(storageBasePath, "thumb");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("创建目录失败 {}", dir.getAbsoluteFile());
            }
        }
        return new File(dir, pid);
    }

    private Path getPictureStorePath(String pid) {
        File dir = new File(storageBasePath, "picture");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("创建目录失败 {}", dir.getAbsoluteFile());
            }
        }
        return Path.of(dir.getAbsolutePath(), pid);
    }

}
