package top.scraft.picmanserver.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import top.scraft.picmanserver.utils.Utils;
import top.scraft.picmanserver.dao.Picture;
import top.scraft.picmanserver.dao.PictureDao;
import top.scraft.picmanserver.dao.PictureLibrary;
import top.scraft.picmanserver.dao.PictureLibraryDao;
import top.scraft.picmanserver.data.*;
import top.scraft.picmanserver.log.ApiLog;
import top.scraft.picmanserver.rest.result.RootResult;
import top.scraft.picmanserver.rest.result.api.ResultWrapper;
import top.scraft.picmanserver.rest.result.api.piclib.CreateLibraryResult;
import top.scraft.picmanserver.rest.result.api.piclib.PiclibResultWrapper;
import top.scraft.picmanserver.rest.result.api.piclib.picture.PictureUpdateResult;
import top.scraft.picmanserver.service.PictureLibraryService;
import top.scraft.picmanserver.service.PictureService;
import top.scraft.picmanserver.service.UserService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/lib")
@Slf4j
public class ApiLibrary {

    private final CacheControl cacheControlPublic = CacheControl.empty().cachePublic();

    @Resource
    private UserService userService;
    @Resource
    private PictureLibraryService pictureLibraryService;
    @Resource
    private PictureService pictureService;
    @Resource
    private PictureLibraryDao pictureLibraryDao;
    @Resource
    private PictureDao pictureDao;

    @ApiLog
    @ApiOperation("新建图库")
    @PostMapping("/")
    public ResponseEntity<CreateLibraryResult>
    createPictureLibrary(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                         @RequestParam String name) {
        CreateLibraryResult result = new CreateLibraryResult();
        if (!userService.canCreateLib(principal.getSaid())) {
            result.status(403, "图库数量已达上限");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        PictureLibrary library = pictureLibraryService.create(name, principal.getSaid());
        long lid = library.getLid();
        result.setName(name);
        result.setLid(lid);
        result.ok();
        return ResponseEntity.ok(result);
    }

    @ApiLog
    @ApiOperation("取所有图库信息")
    @GetMapping("/")
    public ResponseEntity<ResultWrapper<List<PictureLibraryDetails>>>
    getAllPictureLibraryDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal) {
        List<PictureLibraryDetails> detailsList = new ArrayList<>();
        pictureLibraryDao.findByUsers_SaidAndDeletedFalse(principal.getSaid()).forEach(library -> {
            PictureLibraryDetails details = new PictureLibraryDetails();
            details.setLid(library.getLid());
            details.setName(library.getName());
            details.setPicCount((int) pictureDao.countByLibraries_Lid(library.getLid()));
            details.setLastUpdate(library.getLastUpdate());
            details.setReadonly(!library.getOwner().equals(principal.getSaid())); // 暂时设置只有创建者能修改内容
            detailsList.add(details);
        });
        return ResponseEntity.ok(new ResultWrapper<>(detailsList));
    }

    @ApiLog
    @ApiOperation("取图库信息")
    @GetMapping("/{lid}")
    public ResponseEntity<PiclibResultWrapper<PictureLibraryDetails>>
    getPictureLibraryDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                             @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        PiclibResultWrapper<PictureLibraryDetails> result = new PiclibResultWrapper<>();
        PictureLibrary library = pictureLibraryDao.findByLidAndDeletedFalse(lid).orElseThrow();
        PictureLibraryDetails details = new PictureLibraryDetails();
        details.setLid(library.getLid());
        details.setName(library.getName());
        details.setPicCount((int) pictureDao.countByLibraries_Lid(library.getLid()));
        details.setLastUpdate(library.getLastUpdate());
        details.setReadonly(!library.getOwner().equals(principal.getSaid())); // 暂时设置只有创建者能修改内容
        result.setData(details);
        result.ok();
        return ResponseEntity.ok(result);
    }

    @ApiLog
    @ApiOperation("删除图库")
    @DeleteMapping("/{lid}")
    public ResponseEntity<ResultWrapper<Object>>
    deletePictureLibrary(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                         @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        pictureLibraryService.delete(lid);
        return ResponseEntity.ok(new ResultWrapper<>(null));
    }

    @ApiLog
    @ApiOperation("取图库内容信息")
    @GetMapping("/{lid}/gallery")
    public ResponseEntity<PiclibResultWrapper<List<PictureLibraryContentDetails>>>
    getPictureLibraryContentDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                                    @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        PiclibResultWrapper<List<PictureLibraryContentDetails>> result = new PiclibResultWrapper<>();
        result.setLid(lid);
        List<PictureLibraryContentDetails> contentDetailsList = new ArrayList<>();
        pictureDao.findByLibraries_Lid(lid).forEach(p -> {
            PictureLibraryContentDetails details = new PictureLibraryContentDetails();
            details.setPid(p.getPid());
            details.setLastModify(p.getLastModify());
            details.setValid(p.isValid());
            contentDetailsList.add(details);
        });
        result.setData(contentDetailsList);
        result.ok();
        return ResponseEntity.ok(result);
    }

    @ApiLog
    @ApiOperation("新建或更新图片信息")
    @PutMapping("/{lid}/gallery/{pid}")
    public ResponseEntity<PictureUpdateResult>
    updatePictureMeta(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                  @ApiParam @PathVariable long lid,
                  @ApiParam @PathVariable String pid,
                  @ApiParam @RequestBody UpdatePictureRequest updatePictureRequest) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        PictureUpdateResult result = new PictureUpdateResult();
        result.setLid(lid);
        result.setPid(pid);
        if (Utils.isPidInvalid(pid)) {
            result.status(400, RootResult.ERROR_INVALID_PID);
            return ResponseEntity.badRequest().body(result);
        }
        if (!pictureLibraryService.exists(lid)) {
            return ResponseEntity.notFound().build();
        }
        Picture picture;
        try {
            picture = pictureLibraryService.addOrUpdatePicture(lid, pid, updatePictureRequest, principal.getSaid());
        } catch (PictureLibraryFullException e) {
            result.status(403, e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
        result.setNeedUpload(!picture.isValid());
        result.ok();
        return ResponseEntity.ok(result);
    }

    @ApiOperation("上传图片文件")
    @PostMapping("/{lid}/gallery/{pid}/img")
    public ResponseEntity<RootResult>
    uploadPictureFile(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                      @ApiParam @PathVariable long lid,
                      @ApiParam @PathVariable String pid,
                      @ApiParam @RequestParam MultipartFile file) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        RootResult result = new RootResult();
        PictureDetails details = pictureService.getDetails(pid);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        if (!pictureService.isInLibrary(pid, lid)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.forbidden());
        }
        if (details.isValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.forbidden("已存在"));
        }
        try {
            String md5 = Utils.Bytes2HStrNoSpace(Utils.md5(file.getBytes()));
            if (!pid.substring(0, 32).equalsIgnoreCase(md5)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.forbidden("文件校验不通过"));
            }
            pictureService.uploadPicture(pid, file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.status(500, "数据存取错误"));
        }
        return ResponseEntity.ok(result.ok());
    }

    @ApiLog
    @ApiOperation("取图片原图")
    @GetMapping("/{lid}/gallery/{pid}/img")
    public ResponseEntity<FileSystemResource>
    getPictureFile(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                   @ApiIgnore WebRequest request,
                   @ApiParam @PathVariable long lid,
                   @ApiParam @PathVariable String pid) {
        PictureDetails details = pictureService.getDetails(pid);
        if ((details == null) || (!details.isValid()) || (!pictureService.isInLibrary(pid, lid))) {
            return ResponseEntity.notFound().build();
        }
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        File file = pictureService.getPictureFile(pid);
        String eTag = "pmpic_" + pid;
        if (request.checkNotModified(eTag)) {
            return null;
        } else {
            return ResponseEntity.ok()
                    .eTag(eTag)
                    .cacheControl(cacheControlPublic)
                    .contentLength(file.length())
                    .contentType(Utils.mediaType(pid))
                    .body(new FileSystemResource(file));
        }
    }

    @ApiLog
    @ApiOperation("取图片缩略图")
    @GetMapping("/{lid}/gallery/{pid}/thumb")
    public ResponseEntity<FileSystemResource>
    getThumbFile(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                   @ApiIgnore WebRequest request,
                   @ApiParam @PathVariable long lid,
                   @ApiParam @PathVariable String pid) {
        PictureDetails details = pictureService.getDetails(pid);
        if ((details == null) || (!details.isValid()) || (!pictureService.isInLibrary(pid, lid))) {
            return ResponseEntity.notFound().build();
        }
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        File file = pictureService.getThumbFile(pid);
        String eTag = "pmthm_" + pid;
        if (request.checkNotModified(eTag)) {
            return null;
        } else {
            return ResponseEntity.ok()
                    .eTag(eTag)
                    .cacheControl(cacheControlPublic)
                    .contentLength(file.length())
                    .contentType(Utils.mediaType(pid))
                    .body(new FileSystemResource(file));
        }
    }

}
