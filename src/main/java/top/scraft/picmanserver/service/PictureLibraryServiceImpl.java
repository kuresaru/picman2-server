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
        Optional<Picture> pictureOptional = pictureDao.findById(pid);
        PictureLibrary library = pictureLibraryDao.findByLidAndDeletedFalse(lid).orElseThrow(() -> new IllegalArgumentException("Library not found: " + lid));
        if (pictureOptional.isEmpty() && (!(library.getPictures().size() < library.getMaxPictureCount()))) {
            throw new PictureLibraryFullException();
        }
        Picture picture = pictureOptional.orElseGet(() -> {
            Picture p = new Picture();
            p.setPid(pid);
            p.setCreateTime(System.currentTimeMillis() / 1000);
            p.setCreator(operator);
            return p;
        });
        picture.setDescription(request.getDescription());
        picture.setTags(request.getTags());
        picture.setLastModify(System.currentTimeMillis() / 1000);
        picture = pictureDao.save(picture);
        library.getPictures().add(picture);
        library.markUpdate();
        pictureLibraryDao.save(library);
        return picture;
    }

}
