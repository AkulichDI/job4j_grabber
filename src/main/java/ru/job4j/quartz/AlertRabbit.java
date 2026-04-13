package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    private static Properties readProperties(){

        Properties config = new Properties();

        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")){
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: rabbit.properties");
            }
            config.load(in);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return  config;
    }





    public static void main(String[] args) {

        Properties config = readProperties();
        int interval = Integer.parseInt(config.getProperty("rabbit.interval"));

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData("param", "Hello, Rabbit!")
                    .usingJobData("param2", "42")
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();

            scheduler.scheduleJob(job, trigger);

        }catch (SchedulerException e ){
            e.printStackTrace();
        }





    }


    public static class Rabbit implements Job{
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {

            String value = context.getJobDetail().getJobDataMap().getString("param");
            int param = context.getJobDetail().getJobDataMap().getInt("param2");
            System.out.println("Rabbit runs here with key: " + value + " and param2 :" + param);
        }
    }



}
