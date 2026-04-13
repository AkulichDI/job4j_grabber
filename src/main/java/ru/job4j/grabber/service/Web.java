package ru.job4j.grabber.service;

import io.javalin.Javalin;

public class Web {

    private final Store store;

    public Web(Store store) {
        this.store = store;
    }

    public void start(int port) {
        var app = Javalin.create(config -> config.defaultContentType = "text/html; charset=utf-8");
        app.start(port);
        app.get("/", ctx -> {
            var page = new StringBuilder();
            store.getAll().forEach(post -> page.append(post).append(System.lineSeparator()));
            ctx.contentType("text/html; charset=utf-8");
            ctx.result(page.toString());
        });
    }
}
