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

    @Query(nativeQuery = true, value = "select p.* from picture as p left join picture_tag_map as t on p.pid = t.picture " +
            "where valid = true and (p.description like %?1% or t.tags like %?1%)")
    List<Picture> findByValidTrueAndDescriptionOrTagsContaining(String keyword);

}
