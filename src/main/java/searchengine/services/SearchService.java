package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.models.IndexRepository;
import searchengine.models.LemmaRepository;
import searchengine.models.PageRepository;
import searchengine.models.SiteRepository;

import java.util.HashMap;
@Service
public interface SearchService {
    HashMap<String, Object> search(LemmaRepository lemmaRepository, IndexRepository indexRepository, SiteRepository siteRepository, PageRepository pageRepository, String query, String site, int offset, int limit);
}
