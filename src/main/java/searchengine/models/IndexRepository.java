package searchengine.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    @Query(value = "SELECT * FROM search_index where lemma_id =?1", nativeQuery = true)
    List<Index> getIndexesByLemmaId(int LemmaId);

    @Query(value = "SELECT i.id FROM search_index i JOIN page p on p.id = i.page_id join lemma l on i.lemma_id = l.id WHERE l.lemma IN :lemmas AND p.path IN :pages", nativeQuery = true)
    List<Integer> findByPageTableAndLemmaTable(@Param("lemmas") List<String> lemmasNameList,
                                             @Param("pages") List<String> pageListPaths);

    @Query(value = "SELECT page_id " +
            "FROM search_engine.search_index " +
            "where id in :ids " +
            "group by page_id " +
            "order by Sum(lemma_rank) /  Max(lemma_rank) desc " +
            "limit :offset , :limitt ; ", nativeQuery = true)
    List<Integer> findPagesByIndicies(@Param(value = "ids") List<Integer> ids, @Param(value = "offset") int offset, @Param("limitt") int limit);

    @Query(value = "SELECT Sum(lemma_rank) /  Max(lemma_rank) " +
            "FROM search_engine.search_index " +
            "where id in :ids " +
            "group by page_id " +
            "order by Sum(lemma_rank) /  Max(lemma_rank) desc " +
            "limit :offset , :limitt ; ", nativeQuery = true)
    List<Long> findRelevancePagesByIndicies(@Param(value = "ids") List<Integer> ids, @Param(value = "offset") int offset, @Param("limitt") int limit);
}
