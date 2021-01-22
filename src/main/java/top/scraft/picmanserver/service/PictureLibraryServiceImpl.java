package top.scraft.picmanserver.service;

import org.springframework.stereotype.Service;
import top.scraft.picmanserver.dao.*;
import top.scraft.picmanserver.data.PictureLibraryFullException;
import top.scraft.picmanserver.data.UpdatePictureRequest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;

@Service
public class PictureLibraryServiceImpl implements PictureLibraryService {

    @Resource
    private UserDao userDao;
    @Resource
    private PictureLibraryDao pictureLibraryDao;
    @Resource
    private PictureDao pictureDao;

    @Override
    public boolean exists(long lid) {
        return pictureLibraryDao.existsByLidAndDeletedFalse(lid);
    }

    @Override
    public boolean access(long lid, long said) {
        return pictureLibraryDao.existsByLidAndUsers_SaidAndDeletedFalse(lid, said);
    }

    @Override
    @Transactional
    public PictureLibrary create(String name, Long owner) {
        User user = userDao.findById(owner).orElseThrow(() -> new IllegalArgumentException("User not found: " + owner));

        PictureLibrary library = PictureLibrary.create(name, owner, user.getDefaultPictureCountPerLib());
        library.markUpdate();
        library = pictureLibraryDao.save(library);

        if (user.getLibs() == null) {
            user.setLibs(new HashSet<>());
        }
        user.getLibs().add(library);
        userDao.save(user);

        return library;
    }

    @Override
    public void delete(long lid) {
        pictureLibraryDao.findByLidAndDeletedFalse(lid).ifPresent(l -> {
            l.setDeleted(true);
            l.markUpdate();
            pictureLibraryDao.save(l);
        });
    }

    @Override
    @Transactional
    public Picture addOrUpdatePicture(long lid, String pid, UpdatePictureRequest request, Long operator) throws PictureLibraryFullException {
        PictureLibrary library = pictureLibraryDao.findByLidAndDeletedFalse(lid).orElseThrow(() -> new IllegalArgumentException("Library not found: " + lid));
        boolean libraryContains = pictureDao.existsByPidAndLibraries_Lid(pid, lid);
        // 检查图库是否满
        if ((!libraryContains) && pictureDao.countByLibraries_Lid(lid) >= library.getMaxPictureCount()) {
            throw new PictureLibraryFullException();
        }
        // 更新图片
        Optional<Picture> pictureOptional = pictureDao.findById(pid);
        Picture p;
        if (pictureOptional.isEmpty()) {
            // 不存在 创建新图片
            p = new Picture();
            p.setPid(pid);
            p.setCreateTime(System.currentTimeMillis() / 1000);
            p.setCreator(operator);
            p.setDescription(request.getDescription());
            p.setTags(request.getTags());
            p.setLastModify(System.currentTimeMillis() / 1000);
            p = pictureDao.save(p);
        } else {
            // 图片已存在
            p = pictureOptional.get();
            if (p.getCreator().equals(operator)) {
                p.setDescription(request.getDescription());
                p.setTags(request.getTags());
                p.setLastModify(System.currentTimeMillis() / 1000);
                p = pictureDao.save(p);
            }
            // 非创建者直接引用保存
        }
        library.getPictures().add(p);
        library.markUpdate();
        pictureLibraryDao.save(library);
        return p;
    }

}
