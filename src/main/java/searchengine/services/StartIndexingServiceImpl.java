package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.controllers.ApiController;
import searchengine.models.PageRepository;
import searchengine.models.SiteRepository;

import java.util.HashMap;
@Service
public class StartIndexingServiceImpl implements StartIndexingService {
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    SiteIndexingService siteIndexingService;
    @Autowired
    PageRepository pageRepository;


    @Override
    public HashMap<String, Object> startIndexing(SitesList sitesList) {
        HashMap<String, Object> result = new HashMap<>();
        if (ApiController.indexingIsGone){
            result.put("result", false);
            result.put("error","Индексация уже запущена");
            return result;
        }
        ApiController.indexingIsGone = true;
        for (Site site : sitesList.getSites()){
            searchengine.models.Site optionalSite = siteRepository.findOptionalByUrl(site.getUrl());
            if (optionalSite!= null){
                siteRepository.delete(optionalSite);
            }
            searchengine.models.Site newSite = new searchengine.models.Site();
            newSite.setName(site.getName());
            newSite.setUrl(site.getUrl());
            new Thread(()-> {
                siteIndexingService.siteIndexing(siteRepository, pageRepository, newSite);
            }).start();
        }
        result.put("result",true);
        return result;
    }
}
