package top.scraft.picmanserver.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.scraft.picmanserver.rest.result.Result;
import top.scraft.picmanserver.utils.Utils;
import top.scraft.picmanserver.data.PictureDetails;
import top.scraft.picmanserver.log.ApiLog;
import top.scraft.picmanserver.service.PictureService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/pic")
public class ApiPicture {

    @Resource
    private PictureService pictureService;

    @ApiLog
    @GetMapping("/{pid}")
    @ApiOperation("/取图片信息")
    public ResponseEntity<Result<PictureDetails>>
    getPictureDetails(@PathVariable String pid) {
        if (Utils.isPidInvalid(pid)) {
            return Result.forbidden();
        }
        PictureDetails details = pictureService.getDetails(pid);
        if (details == null) {
            return Result.notFound();
        }
        return Result.ok(details);
    }

}
