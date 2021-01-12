package top.scraft.picmanserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PictureLibraryDao extends JpaRepository<PictureLibrary, Long> {

    boolean existsByLidAndDeletedFalse(Long lid);
    boolean existsByLidAndUsers_SaidAndDeletedFalse(Long lid, Long said);
    Optional<PictureLibrary> findByLidAndDeletedFalse(Long lid);
    List<PictureLibrary> findByUsers_SaidAndDeletedFalse(Long said);
    int countByUsers_SaidAndDeletedFalse(Long said);

}
