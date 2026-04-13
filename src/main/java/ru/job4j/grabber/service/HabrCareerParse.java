package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final int PAGES = 5;
    private static final String JAVA = "java";
    private static final String JAVASCRIPT = "javascript";

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private Post detail(String link) {
        Post post = new Post();
        post.setLink(link);
        try {
            var document = Jsoup.connect(link).get();
            var titleElement = document.select(".page-title__title").first();
            var descriptionElement = document.select(".vacancy-description__text .style-ugc").first();
            var timeElement = document.select("time").first();
            if (titleElement != null) {
                post.setTitle(titleElement.text());
            }
            if (descriptionElement != null) {
                post.setDescription(descriptionElement.text());
            }
            if (timeElement != null) {
                String datetime = timeElement.attr("datetime");
                post.setTime(parseTime(datetime));
            }
        } catch (IOException e) {
            LOG.error("When load vacancy details: " + link, e);
        }
        return post;
    }

    private long parseTime(String datetime) {
        if (datetime.contains("T") && (datetime.contains("+") || datetime.endsWith("Z"))) {
            return OffsetDateTime.parse(datetime).toInstant().toEpochMilli();
        }
        return dateTimeParser.parse(datetime)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    @Override
    public List<Post> fetch(String link) {
        List<Post> result = new ArrayList<>();
        for (int page = 1; page <= PAGES; page++) {
            String pageLink = link.replaceFirst("page=\\d+", "page=" + page);
            try {
                var document = Jsoup.connect(pageLink).get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    if (titleElement == null) {
                        return;
                    }
                    String title = titleElement.text();
                    String lowerTitle = title.toLowerCase(Locale.ROOT);
                    if (!lowerTitle.contains(JAVA) || lowerTitle.contains(JAVASCRIPT)) {
                        return;
                    }
                    var linkElement = titleElement.select("a").first();
                    if (linkElement == null) {
                        return;
                    }
                    String vacancyLink = SOURCE_LINK + linkElement.attr("href");
                    result.add(detail(vacancyLink));
                });
            } catch (IOException e) {
                LOG.error("When load page: " + pageLink, e);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Post> list = new HabrCareerParse(new HabrCareerDateTimeParser())
                .fetch("https://career.habr.com/vacancies?page=1&q=Java%20developer&type=all");
        System.out.println(list);
    }
}
