package top.scraft.picmanserver.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class User {

    @Id
    private Long said;

    @Column(nullable = false)
    private int maxLibCount;

    @Column(nullable = false)
    private int maxPictureFileSize;

    @Column(nullable = false)
    private int defaultPictureCountPerLib;

    @ManyToMany
    @JoinTable(name = "user_piclibs_map",
            joinColumns = @JoinColumn(name = "user", referencedColumnName = "said"),
            inverseJoinColumns = @JoinColumn(name = "lid", referencedColumnName = "lid"))
    private Set<PictureLibrary> libs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return said.equals(user.said);
    }

    @Override
    public int hashCode() {
        return Objects.hash(said);
    }

}
