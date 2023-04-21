CREATE TABLE transactions
(
    id                  SERIAL PRIMARY KEY,
    type_id             NUMERIC(2)               NOT NULL,
    sales_person_name   VARCHAR(50)              NOT NULL,
    product_description VARCHAR(50)              NOT NULL,
    value               NUMERIC(8, 2)            NOT NULL,
    date                TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_types_transactions_01 FOREIGN KEY (type_id) REFERENCES transaction_types (id);

CREATE INDEX xf_transaction_types_transactions_01 ON transactions (type_id);