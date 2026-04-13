# job4j_grabber

Приложение собирает Java-вакансии с Habr Career, сохраняет их в PostgreSQL и отдает список через Javalin.

## Возможности

- запуск по расписанию через Quartz;
- парсинг первых 5 страниц вакансий Java;
- сохранение вакансий в PostgreSQL;
- простая веб-страница со списком вакансий.

## Конфигурация

Основные настройки находятся в `src/main/resources/app.properties`:

- `db.url`, `db.username`, `db.password` - подключение к PostgreSQL;
- `rabbit.interval` - период запуска парсинга в секундах;
- `server.port` - порт веб-приложения.

## Запуск

1. Создать базу данных PostgreSQL `grabber`.
2. Настроить `app.properties`.
3. Выполнить `mvn clean verify`.
4. Запустить `ru.job4j.grabber.Main`.
