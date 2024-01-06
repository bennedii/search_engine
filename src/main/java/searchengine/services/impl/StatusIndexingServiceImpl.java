package searchengine.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.controllers.ApiController;
import searchengine.models.PageRepository;
import searchengine.models.SITE_INDEX;
import searchengine.models.SiteRepository;
import searchengine.services.serv.SiteIndexingService;
import searchengine.services.serv.StatusIndexingService;

import java.util.HashMap;
@Service
public class StatusIndexingServiceImpl implements StatusIndexingService {
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
    @Override
    public HashMap<String, Object> stopIndexing() {
        HashMap<String,Object> answer = new HashMap<>();
        Iterable<searchengine.models.Site> sites = siteRepository.findAll();
        for (searchengine.models.Site site : sites){
            if (site.getStatus() == SITE_INDEX.INDEXING){
                ApiController.indexingIsGone = false;
                answer.put("result", true);
                return answer;
            }
        }
        answer.put("result",false);
        answer.put("error", "Индексация не запущена");
        return answer;
    }
}
