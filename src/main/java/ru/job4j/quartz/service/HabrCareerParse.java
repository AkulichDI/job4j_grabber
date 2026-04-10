package ru.job4j.quartz.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.quartz.model.Post;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse{

    private static final Logger log = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static String PREFIX = "/vacancies?page=";
    private static String SUFFIX = "&q=Java%20developer&type=all";






    @Override
    public List<Post> fetch() {

        var result = new ArrayList<Post>();

        try {
            int pageNumber = 1;
            String fullLink = "%s%s%d$s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            var connection = Jsoup.connect(fullLink);
            var document = connection.get();
            var rows = document.select(".vacancy-card__inner");

            rows.forEach( row -> {

                var titleElement = row.select(".vacancy-card__title").first();
                var linkElement = titleElement.child(0);
                var dateElement =  row.select(".vacancy-card__date").first();
                String vacancyName = titleElement.text();
                var vacancyDate = Timestamp.from(
                        OffsetDateTime.parse(dateElement.child(0).attr("datetime")).toInstant()
                );
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));


                var post = new Post();

                System.out.println(vacancyName + ": " + link + " Дата: " +  vacancyDate) ;

                post.setTitle(vacancyName);
                post.setLink(link);
                post.setTime(vacancyDate);
                result.add(post);
            });

        }catch (IOException e ){
            log.error("When load page", e );
        }


        return result;
    }

    public static void main(String[] args) {


       List<Post> list =  new HabrCareerParse().fetch();



    }

}
