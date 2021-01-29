package top.scraft.picmanserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.scraft.picmanserver.dao.PictureLibraryDao;
import top.scraft.picmanserver.data.SacUserPrincipal;
import top.scraft.picmanserver.dao.User;
import top.scraft.picmanserver.dao.UserDao;

import javax.annotation.Resource;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;
    @Resource
    private PictureLibraryDao pictureLibraryDao;

    @Override
    public void createUserIfNotExists(SacUserPrincipal userPrincipal) {
        if (!userDao.existsById(userPrincipal.getSaid())) {
            log.info("create user {}, username={}", userPrincipal.getSaid(), userPrincipal.getUsername());
            User user = new User();
            user.setSaid(userPrincipal.getSaid());
            user.setMaxLibCount(2);
            user.setDefaultPictureCountPerLib(10);
            user.setMaxPictureFileSize(2 * 1024 * 1024);
            userDao.save(user);
        }
    }

    @Override
    public boolean canCreateLib(Long said) {
        if (said == null || said < 1) {
            return false;
        }
        User user = userDao.findById(said).orElseThrow(() -> new IllegalArgumentException("User not found: " + said));
        return pictureLibraryDao.countByUsers_SaidAndDeletedFalse(user.getSaid()) < user.getMaxLibCount();
    }

}
