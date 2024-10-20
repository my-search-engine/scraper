package com.shyndard.scraper.infra.persistent;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.shyndard.scraper.domain.Page;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class PageDao {

    private final JdbcTemplate jdbcTemplate;

    public void create(final Page page) {
        final String sql = "INSERT INTO page_entity (url, status_code, title) VALUES (lower(?), ?, ?)";
        try {
            jdbcTemplate.update(sql, page.getUrl(), page.getStatusCode(), page.getTitle());
        } catch (final Exception ex) {
            log.error("{}", ex.getMessage());
        }
    }

    // @Cacheable(value = "urls", key = "{#url.toLowerCase()}")
    public boolean exist(final String url) {
        return jdbcTemplate
                .queryForObject("SELECT EXISTS(SELECT FROM page_entity WHERE url = lower(?))", Boolean.class, url);
    }
}
