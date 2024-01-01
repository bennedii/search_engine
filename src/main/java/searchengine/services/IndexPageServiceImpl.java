package searchengine.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.ForkJoinThreads.Fork;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.models.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;


@Service
public class IndexPageServiceImpl implements IndexPageService{

    @Autowired
    PageRepository pageRepository;
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;
    @Autowired
    LemmaFinderService lemmaFinderService;

    @Override
    public HashMap<String, Object> indexPage(String url, SitesList sitesList) {

        HashMap<String, Object> answer = new HashMap<>();
        if (!isLink(url)){
            answer.put("result", false);
            answer.put("error", "Вы пытаетесь проиндексировать не сайт!");
            return answer;
        }
        boolean found = false;
        for (Site site : sitesList.getSites()){
            if (url.startsWith(site.getUrl())){
                found = true;
            }
        }
        if (!found){
            answer.put("result",false);
            answer.put("error", "Данная страница находится за пределами сайтов, \n" +
                    "указанных в конфигурационном файле");
            return answer;
        }
        try {
            Optional<Page> page = pageRepository.findByUrl(url);
            if (page.isPresent()){
                pageRepository.delete(page.get());
            }
            Connection.Response response = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com").ignoreHttpErrors(true).execute();
            Page pageToSave = new Page();
            searchengine.models.Site site = siteRepository.findByUrl(url);
            pageToSave.setSite(site);
            pageToSave.setCode(response.statusCode());
            pageToSave.setPath(url);
            pageToSave.setContent(response.body());
            pageRepository.save(pageToSave);
            lemmaFinderService.findLemmas(pageToSave, response.parse());
            answer.put("result", true);
        }catch (IOException ioException){
        }
        return answer;
    }
    private boolean isLink(String url){
        return url.matches("https://[A-Za-z0-9а-яА-Я\\-_./]+") || url.matches("http://[A-Za-z0-9а-яА-Я\\-_./]+");
    }
}
