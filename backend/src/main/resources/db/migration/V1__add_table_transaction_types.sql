CREATE TABLE transaction_types
(
    id   NUMERIC(2) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO transaction_types(id, name) VALUES (1, 'Venda produtor');
INSERT INTO transaction_types(id, name) VALUES (2, 'Venda afiliado');
INSERT INTO transaction_types(id, name) VALUES (3, 'Comissão paga');
INSERT INTO transaction_types(id, name) VALUES (4, 'Comissão recebida');