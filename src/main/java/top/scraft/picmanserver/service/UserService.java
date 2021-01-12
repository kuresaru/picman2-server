package top.scraft.picmanserver.service;

import top.scraft.picmanserver.data.SacUserPrincipal;

public interface UserService {

    /**
     * 检查用户是否存在,否则新建
     * 用于登录时检查
     *
     * @param userPrincipal 用户信息
     */
    void createUserIfNotExists(SacUserPrincipal userPrincipal);

    /**
     * 检查是否可以创建图库(数量是否限制)
     *
     * @return 可创建
     */
    boolean canCreateLib(Long said);

}
