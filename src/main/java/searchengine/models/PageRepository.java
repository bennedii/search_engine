package searchengine.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    @Query("Select page from Page page where page.path =?1")
    Optional<Page> findByUrl(String url);

    @Query(value = "Select * from Page where id = :pageId",nativeQuery = true)
    Page findPageById(@Param("pageId") int pageId);

    @Query(value = "SELECT p.path FROM page p JOIN `search_index` i ON p.id = i.page_id Join lemma l on l.id = i.lemma_id WHERE l.lemma IN :lemmas",
            nativeQuery = true)
    List<String> findPathsByLemmaList(@Param("lemmas") List<String> lemmas);
    @Query(value = "SELECT p.path FROM page p JOIN `search_index` i ON p.id = i.page_id Join lemma l on l.id = i.lemma_id WHERE l.lemma IN :lemmas and l.site_id = :siteId",
            nativeQuery = true)
    List<String> findPathsByLemmaListWithSite(@Param("lemmas") List<String> lemmas, @Param("siteId") int id);

    @Query(value ="SELECT p.path FROM page p JOIN `search_index` i ON p.id = i.page_id Join lemma l on l.id = i.lemma_id WHERE p.path in :pages and l.lemma = :lem",
            nativeQuery = true)
    List<String> findPages(@Param("pages") List<String> pages, @Param("lem") String lemma);

}
