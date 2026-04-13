package ru.job4j.grabber;

import org.apache.log4j.Logger;
import ru.job4j.grabber.service.*;
import ru.job4j.grabber.stores.JdbcStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        var config = new Config();
        config.load("app.properties");
        try {
            Class.forName(config.get("db.driver-class-name"));
        } catch (ClassNotFoundException e) {
            log.error("Driver class not found", e);
            return;
        }
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password"));
             SchedulerManager scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store
            );
            new Web(store).start(Integer.parseInt(config.get("server.port")));
            new CountDownLatch(1).await();
        } catch (SQLException e) {
            log.error("When create a connection", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Main thread interrupted", e);
        }
    }
}
