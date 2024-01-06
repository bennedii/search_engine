package searchengine.services.serv;

import org.springframework.stereotype.Service;
import searchengine.models.*;

import java.util.Optional;


@Service
public interface SiteIndexingService {

    void siteIndexing(SiteRepository siteRepository, PageRepository pageRepository,Site site);
}
