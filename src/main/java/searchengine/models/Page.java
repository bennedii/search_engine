package searchengine.models;
import javax.persistence.*;
import javax.persistence.Index;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "path_index", columnList = "path")
})
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "INT", nullable = false)
    private int code;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id",nullable = false)
    private Site site;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "pageId")
    private List<searchengine.models.Index> indexList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
