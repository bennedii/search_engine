package searchengine.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site,Integer> {

    @Query("SELECT site FROM Site site WHERE ?1 LIKE CONCAT(url, '%')")
    Site findByUrl(String url);

    @Query("SELECT site FROM Site site WHERE ?1 LIKE CONCAT(url, '%')")
    Site findOptionalByUrl(String url);
}
