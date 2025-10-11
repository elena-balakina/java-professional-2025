-- Клиент 1
WITH a AS (
INSERT INTO address (id, street)
VALUES (nextval('address_SEQ'), 'Невский проспект, 10')
    RETURNING id
    ), c AS (
INSERT INTO client (id, name, address_id)
SELECT nextval('client_SEQ'), 'Иван Петров', a.id FROM a
    RETURNING id
    )
INSERT INTO phone (id, number, client_id)
SELECT nextval('phone_SEQ'), '+7 921 000-00-01', c.id FROM c;

-- Клиент 2
WITH a AS (
INSERT INTO address (id, street)
VALUES (nextval('address_SEQ'), 'ул. Ленина, 5')
    RETURNING id
    ), c AS (
INSERT INTO client (id, name, address_id)
SELECT nextval('client_SEQ'), 'Анна Смирнова', a.id FROM a
    RETURNING id
    )
INSERT INTO phone (id, number, client_id)
SELECT nextval('phone_SEQ'), '+7 903 111-22-33', c.id FROM c;

-- Клиент 3
WITH a AS (
INSERT INTO address (id, street)
VALUES (nextval('address_SEQ'), 'Baker St. 221B')
    RETURNING id
    ), c AS (
INSERT INTO client (id, name, address_id)
SELECT nextval('client_SEQ'), 'John Watson', a.id FROM a
    RETURNING id
    )
INSERT INTO phone (id, number, client_id)
SELECT nextval('phone_SEQ'), '+44 20 7946 0958', c.id FROM c;
