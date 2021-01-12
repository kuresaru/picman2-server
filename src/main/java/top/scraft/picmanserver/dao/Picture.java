package top.scraft.picmanserver.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(indexes = {
        @Index(columnList = "pid"),
        @Index(columnList = "description"),
        @Index(columnList = "valid")
})
public class Picture {

    @Id
    @Column(length = 37) // 32位md5+点+最多4位扩展名
    private String pid;

    @Column(nullable = false)
    private String description;

    @JoinTable(name = "picture_tag_map", joinColumns = @JoinColumn
            (name = "picture", referencedColumnName = "pid"))
    @ElementCollection
    private Set<String> tags;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private long createTime;

    @Column(nullable = false)
    private long lastModify;

    @Column(nullable = false)
    private Long creator;

    @Column(nullable = false)
    private boolean valid;

    @ManyToMany(mappedBy = "pictures")
    private Set<PictureLibrary> libraries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return pid.equals(picture.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

}
