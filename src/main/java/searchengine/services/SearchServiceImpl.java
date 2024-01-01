package searchengine.services;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchData;
import searchengine.models.*;
import searchengine.textSpliter.LemmaFinder;

import java.io.IOException;
import java.util.*;


@Service
public class SearchServiceImpl implements SearchService {
    @Override
    public HashMap<String, Object> search(LemmaRepository lemmaRepository, IndexRepository indexRepository, SiteRepository siteRepository, PageRepository pageRepository, String query, String site, int offset, int limit) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<String> Lemmalist = new ArrayList<>(LemmaFinder.getInstance().collectLemmas(query).keySet());
            List<String> lemmas;
            List<String> pagesPaths;
            if (site == null) {
                lemmas = lemmaRepository.getLemmasWithNoSite(Lemmalist);
                pagesPaths = pageRepository.findPathsByLemmaList(lemmas);
            } else {
                int siteId = siteRepository.findByUrl(site).getId();
                lemmas = lemmaRepository.getLemmasWithSite(Lemmalist, siteId);
                pagesPaths = pageRepository.findPathsByLemmaListWithSite(lemmas, siteId);
            }

            for (String s : lemmas) {
                pagesPaths = pageRepository.findPages(pagesPaths, s);
            }
            if (pagesPaths.isEmpty()) {
                result.put("result", false);
                result.put("error", "Не найдено ни одного сайта");
                return result;
            }
            List<Integer> indices = indexRepository.findByPageTableAndLemmaTable(lemmas, pagesPaths);
            List<Integer> pagesIds = indexRepository.findPagesByIndicies(indices, offset, limit);
            List<Long> relevance = indexRepository.findRelevancePagesByIndicies(indices, offset, limit);
            List<SearchData> searchData = getSearchData(siteRepository,pageRepository, pagesIds, relevance, LemmaFinder.getInstance().getLemmaSet(query));
            result = getFinalInfo(searchData);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<SearchData> getSearchData(SiteRepository siteRepository,PageRepository pageRepository, List<Integer> pageIds, List<Long> relevance, Set<String> query) throws IOException {
            List<SearchData> searchDataList = new ArrayList<>();
            for (int i = 0; i < pageIds.size(); i++){
                Page page = pageRepository.findPageById(pageIds.get(i));
                Site site = siteRepository.findByUrl(page.getPath());
                String content = LemmaFinder.getInstance().getFormatedText(page.getContent(), query);
                String snippet = getMostRelevantedPart(content);
                SearchData searchData = new SearchData(
                        site.getUrl(),site.getName(), page.getPath().replace(site.getUrl(),""), Jsoup.connect(page.getPath()).get().title()
                        ,snippet, relevance.get(i));
                searchDataList.add(searchData);
            }

        return searchDataList;
    }

    private String getMostRelevantedPart(String text){
        String[] sentences = text.split("(?<=[.!?])\\s");

        int maxTagCount = 0;
        int maxIndex = 0;

        for (int i = 0; i < sentences.length; i++) {
            int tagCount = countTagsInSentence(sentences[i]);
            if (tagCount > maxTagCount) {
                maxTagCount = tagCount;
                maxIndex = i;
            }
        }

        String sentence = sentences[maxIndex];
        StringBuilder result = new StringBuilder();

        int index = sentence.indexOf("<b>");

        result.append("...").append(sentence.substring(
                Math.max(0, index - 150), Math.min(sentence.length(), index + 150)
        )).append("...");

        return result.toString();

    }
    private int countTagsInSentence(String sentence) {
        int startIndex = sentence.indexOf("<b>");
        int tagCount = 0;

        while (startIndex != -1) {
            int endIndex = sentence.indexOf("</b>", startIndex);
            if (endIndex == -1) {
                break;
            }

            tagCount++;
            startIndex = sentence.indexOf("<b>", endIndex + 1);
        }

        return tagCount;
    }

    private HashMap<String, Object> getFinalInfo(List<SearchData> searchData) {
        HashMap<String, Object> finalResult = new HashMap<>();
        List<HashMap<String, Object>> data = new ArrayList<>();

        for (SearchData dataItem : searchData) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("site", dataItem.getSite());
            item.put("siteName", dataItem.getSiteName());
            item.put("uri", dataItem.getUri());
            item.put("title", dataItem.getTitle());
            item.put("snippet", dataItem.getSnippet());
            item.put("relevance", dataItem.getRelevance());
            data.add(item);
        }

        finalResult.put("result", true);
        finalResult.put("count", searchData.size());
        finalResult.put("data", data);

        return finalResult;
    }


}
