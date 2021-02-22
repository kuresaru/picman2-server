package top.scraft.picmanserver.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PicmanDao {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public Map<String, Long> getPictureAccessLibrary(List<String> pids) {
        final String sql = "select p.pid, l.lid from picture as p left join piclib_pictures_map as l on p.pid = l.pid where p.pid in ?1 group by p.pid";
        Map<String, Long> map = new HashMap<>();
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, pids);
        List<Object> rows = (List<Object>) query.getResultList();
        rows.forEach(r -> {
            Object[] columns = (Object[]) r;
            map.put((String) columns[0], ((BigInteger) columns[1]).longValue());
        });
        return map;
    }

}
