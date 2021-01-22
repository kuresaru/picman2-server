package top.scraft.picmanserver.service;

import top.scraft.picmanserver.dao.Picture;
import top.scraft.picmanserver.dao.PictureLibrary;
import top.scraft.picmanserver.data.PictureLibraryFullException;
import top.scraft.picmanserver.data.UpdatePictureRequest;

public interface PictureLibraryService {

    /**
     * 检查图库是否存在
     *
     * @param lid id
     * @return 是否存在
     */
    boolean exists(long lid);

    /**
     * 用户是否可读取
     *
     * @param lid  图库id
     * @param said 用户id
     * @return
     */
    boolean access(long lid, long said);

    /**
     * 新建图库
     *
     * @param name 图库名
     * @param owner 所有者said
     * @return 新图库
     */
    PictureLibrary create(String name, Long owner);

    /**
     * 删除图库(只是标记一下删除,不会从磁盘清除)
     *
     * @param lid
     */
    void delete(long lid);

    /**
     * 更新或新建图片
     *
     * @param request 图片信息
     */
    Picture addOrUpdatePicture(long lid, String pid, UpdatePictureRequest request, Long operator) throws PictureLibraryFullException;

}
