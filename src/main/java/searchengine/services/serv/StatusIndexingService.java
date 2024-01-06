package searchengine.services.serv;

import searchengine.config.SitesList;

import java.util.HashMap;

public interface StatusIndexingService {
    HashMap<String, Object> startIndexing(SitesList sitesList);
    HashMap<String, Object> stopIndexing();
}
