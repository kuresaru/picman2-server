package top.scraft.picmanserver.dao;

import lombok.Data;
import top.scraft.picmanserver.data.PictureLibraryDetails;
import top.scraft.picmanserver.data.SacUserPrincipal;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class PictureLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lid;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long owner;

    @Column(nullable = false)
    private long lastUpdate;

    @Column(nullable = false)
    private int maxPictureCount;

    @ManyToMany(mappedBy = "libs")
    private Set<User> users;

    @ManyToMany
    @JoinTable(name = "piclib_pictures_map",
            joinColumns = @JoinColumn(name = "lid", referencedColumnName = "lid"),
            inverseJoinColumns = @JoinColumn(name = "pid", referencedColumnName = "pid"))
    private Set<Picture> pictures;

    @Column(nullable = false)
    private boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureLibrary library = (PictureLibrary) o;
        return lid.equals(library.lid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lid);
    }

    public void markUpdate() {
        lastUpdate = System.currentTimeMillis() / 1000;
    }

    public PictureLibraryDetails details(PictureDao pictureDao, SacUserPrincipal principal) {
        PictureLibraryDetails details = new PictureLibraryDetails();
        details.setLid(getLid());
        details.setName(getName());
        details.setPicCount((int) pictureDao.countByLibraries_Lid(getLid()));
        details.setLastUpdate(getLastUpdate());
        details.setReadonly(!getOwner().equals(principal.getSaid())); // 暂时设置只有创建者能修改内容
        return details;
    }

    public static PictureLibrary create(String name, Long owner, int maxPictureCount) {
        PictureLibrary library = new PictureLibrary();
        library.setName(name);
        library.setOwner(owner);
        library.setMaxPictureCount(maxPictureCount);
        return library;
    }

}
