package com.shyndard.scraper.infra.scraper;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.shyndard.scraper.domain.Page;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebScraper {

    public Page getContent(final String url) {
        try {
            final var result = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:131.0) Gecko/20100101 Firefox/131.0")
                    .header("Accept", "*/*")
                    .timeout(5_000)
                    .followRedirects(false)
                    .execute();
            final var document = result.parse();
            return Page.builder()
                    .statusCode(result.statusCode())
                    .urls(getUrls(document))
                    .titles(getTitles(document))
                    .title(document.title())
                    .url(url)
                    .build();
        } catch (final Exception ex) {
            log.error("{}", ex.getMessage());
            return Page.builder().url(url).build();
        }
    }

    private List<String> getUrls(final Document doc) {
        return doc.select("a[href]")
                .stream()
                .map(element -> element.attr("href"))
                .filter(urls -> urls.startsWith("https://"))
                .toList();
    }

    private List<String> getTitles(final Document doc) {
        return doc.select("h1, h2, h3, h4, h5, h6")
                .stream()
                .map(element -> element.text())
                .toList();
    }
}
