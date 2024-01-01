package searchengine.services;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.models.IndexRepository;
import searchengine.models.LemmaRepository;
import searchengine.models.PageRepository;
import searchengine.models.SiteRepository;

public interface StatisticsService {
    StatisticsResponse getStatistics();
}
