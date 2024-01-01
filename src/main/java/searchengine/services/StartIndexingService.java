package searchengine.services;

import searchengine.config.SitesList;

import java.util.HashMap;

public interface StartIndexingService {
    HashMap<String, Object> startIndexing(SitesList sitesList);
}
