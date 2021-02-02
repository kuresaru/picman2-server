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
import top.scraft.picmanserver.dao.Picture;
import top.scraft.picmanserver.dao.PictureDao;
import top.scraft.picmanserver.dao.PictureLibrary;
import top.scraft.picmanserver.dao.PictureLibraryDao;
import top.scraft.picmanserver.data.*;
import top.scraft.picmanserver.log.ApiLog;
import top.scraft.picmanserver.data.Result;
import top.scraft.picmanserver.service.PictureLibraryService;
import top.scraft.picmanserver.service.PictureService;
import top.scraft.picmanserver.service.UserService;
import top.scraft.picmanserver.utils.Utils;

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
    public ResponseEntity<Result<PictureLibraryDetails>>
    createPictureLibrary(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                         @RequestParam String name) {
        if (!userService.canCreateLib(principal.getSaid())) {
            return Result.status(HttpStatus.FORBIDDEN, Result.ERROR_LIBRARY_LIMIT, null);
        }
        PictureLibrary library = pictureLibraryService.create(name, principal.getSaid());
        return Result.ok(library.details(pictureDao, principal));
    }

    @ApiLog
    @ApiOperation("取所有图库信息")
    @GetMapping("/")
    public ResponseEntity<Result<List<PictureLibraryDetails>>>
    getAllPictureLibraryDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal) {
        List<PictureLibraryDetails> detailsList = new ArrayList<>();
        pictureLibraryDao.findByUsers_SaidAndDeletedFalse(principal.getSaid())
                .forEach(library -> detailsList.add(library.details(pictureDao, principal)));
        return Result.ok(detailsList);
    }

    @ApiLog
    @ApiOperation("取图库信息")
    @GetMapping("/{lid}")
    public ResponseEntity<Result<PictureLibraryDetails>>
    getPictureLibraryDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                             @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        PictureLibrary library = pictureLibraryDao.findByLidAndDeletedFalse(lid).orElseThrow();
        return Result.ok(library.details(pictureDao, principal));
    }

    @ApiLog
    @ApiOperation("删除图库")
    @DeleteMapping("/{lid}")
    public ResponseEntity<Result<Object>>
    deletePictureLibrary(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                         @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        pictureLibraryService.delete(lid);
        return Result.ok();
    }

    @ApiLog
    @ApiOperation("搜索图片(测试功能,限内部调用)")
    @GetMapping("/_search")
    public ResponseEntity<Result<List<PictureDetails>>>
    searchPicture(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                  @ApiParam @RequestParam String search) {
        if (principal.getSaid() != SacUserPrincipal.SAID_RGW) {
            return Result.forbidden();
        }
        search = search.trim();
        if (search.length() == 0) {
            return Result.badRequest("关键字不能为空");
        }
        return Result.ok(pictureLibraryService.searchForRgw(search));
    }

    @ApiLog
    @ApiOperation("取图库内容")
    @GetMapping("/{lid}/gallery")
    public ResponseEntity<Result<List<PictureLibraryContentDetails>>>
    getPictureLibraryContentDetails(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                                    @ApiParam @PathVariable long lid) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        List<PictureLibraryContentDetails> contentDetailsList = new ArrayList<>();
        pictureDao.findByLibraries_Lid(lid).forEach(p -> {
            PictureLibraryContentDetails details = new PictureLibraryContentDetails();
            details.setPid(p.getPid());
            details.setLastModify(p.getLastModify());
            details.setValid(p.isValid());
            contentDetailsList.add(details);
        });
        return Result.ok(contentDetailsList);
    }

    @ApiLog
    @ApiOperation("新建或更新图片信息")
    @PutMapping("/{lid}/gallery/{pid}")
    public ResponseEntity<Result<PictureDetails>>
    updatePictureMeta(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                  @ApiParam @PathVariable long lid,
                  @ApiParam @PathVariable String pid,
                  @ApiParam @RequestBody UpdatePictureRequest updatePictureRequest) {
        // 检查图库权限
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return ResponseEntity.notFound().build();
        }
        if (Utils.isPidInvalid(pid)) {
            return Result.badRequest(Result.ERROR_INVALID_PID);
        }
        if (!pictureLibraryService.exists(lid)) {
            return Result.notFound();
        }
        Picture picture;
        try {
            picture = pictureLibraryService.addOrUpdatePicture(lid, pid, updatePictureRequest, principal.getSaid());
        } catch (PictureLibraryFullException e) {
            return Result.forbidden(e.getMessage());
        }
        return Result.ok(pictureService.getDetails(picture.getPid()));
    }

    @ApiLog
    @ApiOperation("上传图片文件")
    @PostMapping("/{lid}/gallery/{pid}/img")
    public ResponseEntity<Result<Object>>
    uploadPictureFile(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                      @ApiParam @PathVariable long lid,
                      @ApiParam @PathVariable String pid,
                      @ApiParam @RequestParam MultipartFile file) {
        if (!pictureLibraryService.access(lid, principal.getSaid())) {
            return Result.notFound();
        }
        PictureDetails details = pictureService.getDetails(pid);
        if (details == null) {
            return Result.notFound();
        }
        if (!pictureService.isInLibrary(pid, lid)) {
            return Result.forbidden();
        }
        if (details.isValid()) {
            return Result.forbidden(Result.ERROR_ALREADY_EXISTS);
        }
        try {
            String md5 = Utils.Bytes2HStrNoSpace(Utils.md5(file.getBytes()));
            if (!pid.substring(0, 32).equalsIgnoreCase(md5)) {
                return Result.forbidden(Result.ERROR_CHECKSUM_ERROR);
            }
            pictureService.uploadPicture(pid, file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.ioError();
        }
        return Result.ok();
    }

    @ApiLog
    @ApiOperation("取图片原图")
    @GetMapping("/{lid}/gallery/{pid}/img")
    public ResponseEntity<FileSystemResource>
    getPictureFile(@ApiIgnore @AuthenticationPrincipal SacUserPrincipal principal,
                   @ApiIgnore WebRequest request,
                   @ApiParam @PathVariable long lid,
                   @ApiParam @PathVariable String pid) {
        if (principal.getSaid() == SacUserPrincipal.SAID_RGW) {
            if (!pictureDao.existsByPidAndValidTrue(pid)) {
                return ResponseEntity.notFound().build();
            }
        } else {
            PictureDetails details = pictureService.getDetails(pid, lid);
            if ((details == null) || (!details.isValid())) {
                return ResponseEntity.notFound().build();
            }
            if (!pictureLibraryService.access(lid, principal.getSaid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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
