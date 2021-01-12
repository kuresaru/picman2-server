package top.scraft.picmanserver.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.scraft.picmanserver.utils.Utils;
import top.scraft.picmanserver.data.PictureDetails;
import top.scraft.picmanserver.log.ApiLog;
import top.scraft.picmanserver.rest.result.RootResult;
import top.scraft.picmanserver.rest.result.api.piclib.picture.PictureResultWrapper;
import top.scraft.picmanserver.service.PictureService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/pic")
public class ApiPicture {

    @Resource
    private PictureService pictureService;

    @ApiLog
    @GetMapping("/{pid}/details")
    @ApiOperation("/取图片信息")
    public ResponseEntity<PictureResultWrapper<PictureDetails>>
    getPictureDetails(@PathVariable String pid) {
        PictureResultWrapper<PictureDetails> result = new PictureResultWrapper<>();
        result.setPid(pid);
        if (Utils.isPidInvalid(pid)) {
            result.status(400, RootResult.ERROR_INVALID_PID);
            return ResponseEntity.badRequest().body(result);
        }
        PictureDetails details = pictureService.getDetails(pid);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        result.setData(details);
        result.ok();
        return ResponseEntity.ok(result);
    }

}
