package searchengine.ForkJoinThreads;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.controllers.ApiController;
import searchengine.models.*;
import searchengine.services.LemmaFinderService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveAction;


public class Fork extends RecursiveAction {
    private TreeSet<String> set;
    private PageRepository pageRepository;
    private SiteRepository siteRepository;
    private LemmaFinderService lemmaFinderService;
    private Site site;
    private String url;
    public Fork(PageRepository pageRepository, SiteRepository siteRepository,
                LemmaFinderService lemmaFinderService ,Site site, String url){
        this.set = new TreeSet<>();
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
        this.lemmaFinderService = lemmaFinderService;
        this.site = site;
        this.url = url;
    }
    public Fork(TreeSet<String> set,PageRepository pageRepository,
                SiteRepository siteRepository,LemmaFinderService lemmaFinderService ,Site site, String url){
        this.set = set;
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
        this.lemmaFinderService = lemmaFinderService;
        this.site = site;
        this.url = url;
    }

    @Override
    protected void compute() {
        try{
            if (!ApiController.indexingIsGone){
                site.setStatusTime(LocalDateTime.now());
                site.setLastError("Индексация сайтов была остановлена");
                site.setStatus(SITE_INDEX.FAILED);
                siteRepository.save(site);
                return;
            }
            Thread.sleep(500);
            Connection.Response response = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com").ignoreHttpErrors(true).execute();
            Page page = new Page();
            page.setCode(response.statusCode());
            page.setPath(url);
            page.setSite(site);
            Document doc = response.parse();
            page.setContent(doc.text());
            pageRepository.save(page);

            lemmaFinderService.findLemmas(page, doc);

            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
            List<Fork> tasks = new ArrayList<>();
            if (response.statusCode() < 400){
                Elements elements = doc.select("a[href*=/]*");
                for (Element element : elements){
                    String newUrl = element.attr("abs:href");
                    if (newUrl.equalsIgnoreCase(site.getUrl())){
                        continue;
                    }
                    if (isLink(newUrl) && newUrl.startsWith(site.getUrl()) && set.add(newUrl.replaceAll("/",""))){
                        Fork newFork = new Fork(set, pageRepository,siteRepository,lemmaFinderService,site,newUrl);
                        newFork.fork();
                        tasks.add(newFork);
                    }
                }
            }
            for (Fork task : tasks){
                task.join();
            }
        }catch (IOException exception){
            exception.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isLink(String input) {
        if (input.endsWith(".jpg") || input.endsWith(".png")) return false;
        return true;
    }
}
