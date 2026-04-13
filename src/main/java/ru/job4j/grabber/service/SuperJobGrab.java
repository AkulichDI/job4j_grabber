package ru.job4j.grabber.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

public class SuperJobGrab  implements Job {
    private static final String HABR_JAVA_LINK = "https://career.habr.com/vacancies/java_developer?page=1";

    @Override
    public void execute(JobExecutionContext context ) throws JobExecutionException {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        var parse = new HabrCareerParse(new HabrCareerDateTimeParser());
        var posts = parse.fetch(HABR_JAVA_LINK);
        for (Post post : posts) {
            store.save(post);
        }
    }
}
