package top.scraft.picmanserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureDao extends JpaRepository<Picture, String> {

    boolean existsByPidAndLibraries_Lid(String pid, Long lid);

    long countByLibraries_Lid(Long lid);

    List<Picture> findByLibraries_Lid(Long lid);

}
