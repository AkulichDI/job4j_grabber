package ru.job4j.quartz;

import ru.job4j.quartz.model.Post;
import ru.job4j.quartz.service.Config;
import ru.job4j.quartz.service.SchedulerManager;
import ru.job4j.quartz.service.SuperJobGrab;
import ru.job4j.quartz.stores.MemStore;

public class Main {

    public static void main(String[] args) {





        var config = new Config();

        config.load("application.properties");
        var store = new MemStore();
        var post = new Post();
        post.setTitle("Super Java Job");
        store.save(post);
        var scheduler = new SchedulerManager();
        scheduler.init();
        scheduler.load(
                Integer.parseInt(config.get("rabbit.interval")),
                SuperJobGrab.class,
                store
        );


    }


}
