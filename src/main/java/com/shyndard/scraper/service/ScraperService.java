package com.shyndard.scraper.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.shyndard.scraper.domain.Page;
import com.shyndard.scraper.infra.persistent.PageDao;
import com.shyndard.scraper.infra.rabbitmq.MessageSender;
import com.shyndard.scraper.infra.scraper.WebScraper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ScraperService {

    private final WebScraper scraper;
    private final MessageSender messageSender;
    private final PageDao pageDao;

    private final Map<String, Integer> failedHost = new HashMap<>();

    public void process(final String url) {
        if (!shouldBeProcess(url)) {
            return;
        }
        final var page = scraper.getContent(url);
        processHost(page);
        // Save page
        pageDao.create(page);
        // Send urls
        if (page.getUrls() != null) {
            page.getUrls()
                    .stream()
                    .filter(newUrl -> shouldBeProcess(newUrl))
                    .forEach(newUrl -> messageSender.sendMessage(newUrl));
        }
    }

    private String getHost(final String url) {
        var host = url.split("/")[2];
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        return host;
    }

    private void processHost(final Page page) {
        final var host = getHost(page.getUrl());
        if (isPagNotUsable(page)) {
            failedHost.put(host, failedHost.getOrDefault(host, 0) + 1);
        }
    }

    private boolean isPagNotUsable(final Page page) {
        return page == null || page.getStatusCode() == null
                || (page.getStatusCode() < 200 && page.getStatusCode() >= 300);
    }

    private boolean shouldBeProcess(final String url) {
        final var host = getHost(url);
        if (failedHost.getOrDefault(host, 0) > 10) {
            return false;
        }
        if (pageDao.exist(url)) {
            return false;
        }
        if (!(host.endsWith(".ca") || host.endsWith(".com") || host.endsWith(".fr") || host.endsWith(".net"))) {
            return false;
        }
        final var params = url.split("\\.");
        if (params.length <= 2) {
            return true;
        }
        final var extension = params[params.length - 1];
        if ("html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension)
                || "php".equalsIgnoreCase(extension)) {
            return true;
        }
        return false;
    }

}
