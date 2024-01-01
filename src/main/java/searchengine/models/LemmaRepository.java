package searchengine.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query(value = "Select lem.* from Lemma lem where lem.lemma =:lemmaName and lem.site_id = :siteId", nativeQuery = true)
    Lemma findLemmaByNameAndSite(@Param(value = "lemmaName") String name, @Param(value = "siteId") int siteId);

    @Query(value = "select lemma from lemma where lemma in :lemmas order by frequency desc ",nativeQuery = true)
    List<String> getLemmasWithNoSite(@Param(value = "lemmas") List<String> lemmas);

    @Query(value = "select lemma from lemma where lemma in :lemmas AND site_id = :siteId order by frequency desc", nativeQuery = true)
    List<String> getLemmasWithSite(@Param(value = "lemmas") List<String> lemmas,@Param(value = "siteId") int siteId);


}
