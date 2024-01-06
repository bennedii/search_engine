package searchengine.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ForkJoinThreads.Fork;

import searchengine.controllers.ApiController;
import searchengine.models.*;
import searchengine.services.serv.LemmaFinderService;
import searchengine.services.serv.SiteIndexingService;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@Service
public class SiteIndexingServiceImpl implements SiteIndexingService {
    @Autowired
    LemmaFinderService lemmaFinderService;
    @Override
    public void siteIndexing(SiteRepository siteRepository, PageRepository pageRepository, Site site) {
        site.setStatus(SITE_INDEX.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Fork fork = new Fork(pageRepository,siteRepository,lemmaFinderService,site,site.getUrl());
        forkJoinPool.invoke(fork);
        if (site.getStatus() != SITE_INDEX.FAILED){
            site.setStatus(SITE_INDEX.INDEXED);
        }
        siteRepository.save(site);
        stopIndex(siteRepository);
    }

    private void stopIndex(SiteRepository siteRepository){
        Iterable<searchengine.models.Site> sites = siteRepository.findAll();
        for (searchengine.models.Site site : sites){
            if (site.getStatus() == SITE_INDEX.INDEXING){
                return;
            }
        }
        ApiController.indexingIsGone = false;
    }

}
