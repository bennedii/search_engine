package searchengine.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "VARCHAR(255)",nullable = false)
    private String lemma;
    @Column(columnDefinition = "INT", nullable = false)
    private int frequency;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id",nullable = false)
    private Site site;

    @OneToMany(mappedBy = "lemmaId", orphanRemoval = true)
    private List<Index> indices;


    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Site getSiteId() {
        return site;
    }

    public void setSiteId(Site siteId) {
        this.site = siteId;
    }


}
