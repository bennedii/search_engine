package searchengine.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.SitesList;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.models.*;
import searchengine.services.serv.IndexPageService;
import searchengine.services.serv.SearchService;
import searchengine.services.serv.StatisticsService;
import searchengine.services.serv.StatusIndexingService;

import java.util.HashMap;


@RestController
@RequestMapping("/api")
public class ApiController {
    public static Boolean indexingIsGone = false;
    @Autowired
    private IndexPageService indexPageService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private StatusIndexingService statusIndexingService;
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
        answer = statusIndexingService.startIndexing(sites);

        return answer;
    }

    @GetMapping("/stopIndexing")
    public HashMap<String, Object> stopIndexing(){
        return statusIndexingService.stopIndexing();
    }

    @PostMapping("/indexPage")
    public HashMap<String, Object> indexPage(@RequestParam String url){
        return indexPageService.indexPage(url, sites);
    }

    @GetMapping("/search")
    public HashMap<String, Object> search(@RequestParam(required = false) String query, @RequestParam(required = false) String site,
                                          @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){
        return searchService.search(lemmaRepository,indexRepository,siteRepository,pageRepository,query,site,offset, limit);
    }

}
