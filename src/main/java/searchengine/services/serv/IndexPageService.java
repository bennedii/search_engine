package searchengine.services.serv;
import searchengine.config.SitesList;
import java.util.HashMap;


public interface IndexPageService {
    HashMap<String, Object> indexPage(String url, SitesList sitesList);
}
