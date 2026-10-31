# StudyFlow

StudyFlow - учебное клиент-серверное приложение для управления задачами, написанное полностью на чистой Java без Spring, Maven, Gradle и сторонних библиотек.

Проект создан для практики Java Core, Collections, Generics, Stream API, Files/NIO, Exceptions, HttpServer, HttpClient и базовых принципов построения многослойных приложений.

## Возможности

StudyFlow позволяет:

- создавать задачи;
- получать список задач;
- получать задачу по ID;
- изменять задачи;
- удалять задачи;
- фильтровать задачи по статусу и приоритету;
- искать задачи по названию и описанию;
- сортировать задачи;
- получать статистику;
- сохранять данные между запусками приложения.

## Используемые технологии

Проект использует только стандартную библиотеку Java.

Основные технологии и темы:

- Java 17+;
- OOP;
- Collections;
- Generics;
- Stream API;
- Lambda Expressions;
- Method References;
- Optional;
- Custom Exceptions;
- Files / NIO;
- HttpServer;
- HttpClient;
- LocalDate;
- Records;
- Functional Interfaces;
- Comparator;
- собственные unit- и integration-тесты.

Сторонние зависимости отсутствуют.

## Архитектура

```text
StudyFlow Client
       |
   HttpClient
       |
       | HTTP
       v
HttpServer
       |
       +-------------------+
       |                   |
  TaskHandler      StatisticsHandler
       |                   |
  TaskService      StatisticsService
       |                   |
       +---------+---------+
                 |
        Repository<Task, Long>
                 |
          TaskRepository
                 |
       InMemoryRepository
                 |
       HashMap<Long, Task>

TaskPersistenceService
       |
TaskFileStorage
       |
data/tasks.txt
```

## Основные пакеты

```text
studyflow
├── client
├── dto
├── exception
├── http
├── model
├── repository
├── service
├── storage
├── test
└── util
```

## REST API

### Получить все задачи

```http
GET /tasks
```

### Получить задачу

```http
GET /tasks/{id}
```

### Создать задачу

```http
POST /tasks
Content-Type: application/x-www-form-urlencoded
```

Параметры:

```text
title
description
priority
deadline
```

Пример:

```text
title=Изучить HttpClient
description=Написать клиент
priority=HIGH
deadline=2026-12-01
```

### Изменить задачу

```http
PUT /tasks/{id}
```

Можно передать только изменяемые поля:

```text
title
description
status
priority
deadline
```

### Удалить задачу

```http
DELETE /tasks/{id}
```

### Фильтрация

```http
GET /tasks?status=TODO
GET /tasks?priority=HIGH
GET /tasks?search=java
GET /tasks?sort=deadline
```

Параметры можно комбинировать:

```http
GET /tasks?status=TODO&priority=HIGH&search=java&sort=deadline
```

### Статистика

```http
GET /statistics
```

Возвращает:

- общее количество задач;
- количество задач каждого статуса;
- количество задач каждого приоритета;
- количество просроченных задач;
- процент выполненных задач;
- ближайшую активную задачу.

## HTTP-коды

| Код | Значение |
|---|---|
| 200 | Успешный запрос |
| 201 | Ресурс создан |
| 204 | Ресурс успешно удалён |
| 400 | Некорректный запрос |
| 404 | Ресурс не найден |
| 405 | HTTP-метод не поддерживается |
| 500 | Внутренняя ошибка сервера |

## Хранение данных

Данные сохраняются в:

```text
data/tasks.txt
```

При запуске приложения задачи автоматически загружаются из файла.

После создания, изменения или удаления задачи состояние сохраняется автоматически.

Директория `data` исключена из Git.

## Запуск сервера

Запустите серверный `main`-класс проекта.

По умолчанию сервер доступен по адресу:

```text
http://localhost:8080
```

## Запуск клиента

После запуска сервера отдельно запустите:

```text
ClientMain
```

Консольный клиент предоставляет меню для работы с API через `HttpClient`.

## Git

В проекте используется Conventional Commits.

Примеры:

```text
feat: добавить HTTP API для получения задач
fix: восстанавливать следующий идентификатор после загрузки
refactor: обобщить репозиторий с помощью generics
test: добавить интеграционные тесты HTTP API
docs: добавить документацию проекта
```

## Что демонстрирует проект

StudyFlow показывает практическое применение основных возможностей Java без использования фреймворков.

В частности, проект демонстрирует разделение приложения на слои, использование generic-репозиториев, работу с коллекциями и Stream API, клиент-серверное взаимодействие через HTTP, файловое хранение данных.
