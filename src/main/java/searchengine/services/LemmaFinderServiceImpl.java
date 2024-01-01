package searchengine.services;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import searchengine.models.*;
import searchengine.textSpliter.LemmaFinder;

import java.io.IOException;
import java.util.*;

@Service
public class LemmaFinderServiceImpl implements LemmaFinderService{
    @Autowired
    LemmaRepository lemmaRepository;
    @Autowired
    IndexRepository indexRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    SiteRepository siteRepository;
    public synchronized void findLemmas(Page page, Document document) {
        try {
            String string = document.body().text().toLowerCase();
            Map<String, Integer> map = LemmaFinder.getInstance().collectLemmas(string);
            Site site = siteRepository.findByUrl(page.getPath());

            List<Lemma> lemmaListToSave = new ArrayList<>();
            List<Index> indexListToSave = new ArrayList<>();


            for (Map.Entry<String, Integer> entry : map.entrySet()) {

                Lemma lemmaToSave = lemmaRepository.findLemmaByNameAndSite(entry.getKey(), site.getId());
                if (lemmaToSave != null) {
                    lemmaToSave.setFrequency(lemmaToSave.getFrequency() + entry.getValue());
                } else {
                    lemmaToSave = new Lemma();
                    lemmaToSave.setLemma(entry.getKey());
                    lemmaToSave.setFrequency(entry.getValue());
                    lemmaToSave.setSiteId(site);
                    lemmaListToSave.add(lemmaToSave);
                }
                Index index = new Index();
                index.setPageId(page);
                index.setLemmaId(lemmaToSave);
                index.setRank(entry.getValue());
                indexListToSave.add(index);
            }


            lemmaRepository.saveAll(lemmaListToSave);
            indexRepository.saveAll(indexListToSave);
        } catch (IOException | IncorrectResultSizeDataAccessException exception) {
            exception.printStackTrace();
        }
    }
}
