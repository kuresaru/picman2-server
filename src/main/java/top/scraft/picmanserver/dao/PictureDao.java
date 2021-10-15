package top.scraft.picmanserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PictureDao extends JpaRepository<Picture, String> {

    boolean existsByPidAndLibraries_Lid(String pid, Long lid);

    boolean existsByPidAndValidTrue(String pid);

    long countByLibraries_Lid(Long lid);

    Optional<Picture> findByPidAndLibraries_Lid(String pid, Long lid);

    List<Picture> findByLibraries_Lid(Long lid);

    @Query(nativeQuery = true, value = "select distinct p.* from picture as p left join picture_tag_map as t on p.pid = t.picture " +
            "where valid = true and (p.description like %?1% or t.tags like %?1%)")
    List<Picture> findByValidTrueAndDescriptionOrTagsContaining(String keyword);

    @Query(nativeQuery = true, value = "select distinct p.* from picture as p " +
            "left join picture_tag_map as t on p.pid = t.picture " +
            "left join piclib_pictures_map as l on p.pid = l.pid " +
            "where valid = true and (p.description like %?1% or t.tags like %?1%) and l.lid in ?2 " +
            "group by p.pid, t.tags")
    List<Picture> findByValidTrueAndDescriptionOrTagsContainingAndLidIn(String keyword, List<Long> lids);

}
