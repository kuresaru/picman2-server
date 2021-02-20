package top.scraft.picmanserver.rest;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import top.scraft.picmanserver.data.Result;
import top.scraft.picmanserver.data.SacUserPrincipal;
import top.scraft.picmanserver.data.ServerInfo;
import top.scraft.picmanserver.data.UserDetail;
import top.scraft.picmanserver.log.ApiLog;

@RestController
@RequestMapping("/api")
@Slf4j
public class Api {

    @ApiLog
    @GetMapping("/")
    @ApiOperation("取服务器信息")
    public ResponseEntity<Result<ServerInfo>>
    getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setApiVersion(2);
        serverInfo.setApiSubVersion(0);
        return Result.ok(serverInfo);
    }

    @ApiLog
    @GetMapping("/my")
    @ApiOperation("取已登录用户信息")
    public ResponseEntity<Result<UserDetail>>
    getUserInfo(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal) {
        UserDetail userDetail = new UserDetail();
        userDetail.setSacUserPrincipal(principal);
        userDetail.setAdmin(true); // TODO
        return Result.ok(userDetail);
    }

}
