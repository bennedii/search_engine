package searchengine.services;

import org.jsoup.nodes.Document;
import searchengine.models.*;

public interface LemmaFinderService {
    void findLemmas(Page page, Document document);
}
