package searchengine.controllers;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.ForkJoinThreads.Fork;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.models.*;
import searchengine.services.*;

import java.io.IOException;
import java.util.HashMap;

import java.util.Optional;


@RestController
@RequestMapping("/api")
public class ApiController {
    public static Boolean indexingIsGone = false;
    @Autowired
    private  IndexPageService indexPageService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private StartIndexingService startIndexingService;
    private final SearchService searchService;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SitesList sites;
    public ApiController( SiteRepository siteRepository, SitesList sitesList, SearchService searchService,
                         PageRepository pageRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.sites = sitesList;
        this.searchService = searchService;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public HashMap<String,Object> startIndexing(){
        HashMap<String, Object> answer = new HashMap<>();
        answer = startIndexingService.startIndexing(sites);

        return answer;
    }

    @GetMapping("/stopIndexing")
    public HashMap<String, Object> stopIndexing(){
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

    @PostMapping("/indexPage")
    public HashMap<String, Object> indexPage(@RequestParam String url){
        HashMap<String, Object> answer = new HashMap<>();
        answer = indexPageService.indexPage(url, sites);
        return answer;
    }

    @GetMapping("/search")
    public HashMap<String, Object> search(@RequestParam(required = false) String query, @RequestParam(required = false) String site,
                                          @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        HashMap<String, Object> result = new HashMap<>();
        if (query.isEmpty()){
            result.put("result", false);
            result.put("error", "Задан пустой поисковой запрос");
            return result;
        }
        result = searchService.search(lemmaRepository,indexRepository,siteRepository,pageRepository,query,site,0, 10);
        return result;
    }

}
