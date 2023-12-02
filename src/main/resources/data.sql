-- insert mock data just for development/test

INSERT INTO todo(title, created_at, updated_at)
VALUES ('Foo', '2023-09-01', '2023-09-02');

INSERT INTO todo(title, created_at, updated_at)
VALUES ('Bar', '2023-09-03', '2023-09-04');

INSERT INTO todo(title, completed, created_at, updated_at)
VALUES ('Baz', TRUE, '2023-09-05', '2023-09-06');

INSERT INTO todo_history
SELECT * FROM CSVREAD('src/main/resources/db/mock-todo-history.csv');
