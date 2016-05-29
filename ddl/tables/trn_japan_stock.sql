-- Table: trn_japan_stock

-- DROP TABLE trn_japan_stock;

CREATE TABLE trn_japan_stock
(
  code integer NOT NULL,
  regist_date date NOT NULL,
  opening_price integer NOT NULL,
  high_price integer NOT NULL,
  low_price integer NOT NULL,
  finish_price integer NOT NULL,
  turnover bigint,
  trading_value bigint,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
--  CONSTRAINT japan_stock_pkey PRIMARY KEY (code , regist_date),
  CONSTRAINT instrument_fkey FOREIGN KEY (code)
      REFERENCES mst_instrument (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
--insert into child_jstock_10000_100000 (code, regist_date, opening_price, high_price, low_price, finish_price, turnover, trading_value, created_date, updated_date, created_by, updated_by)
--select code, regist_date, opening_price, high_price, low_price, finish_price, turnover, null, now(), now(), 'system', 'system'
--from trn_stock_data
--where code >= 10000 and code < 100000;

--insert into trn_japan_stock (created_by, created_date, updated_by, updated_date, finish_price, high_price, low_price, opening_price, trading_value, turnover, code, regist_date)
--values ('system', now(), 'system', now(), 1, 2, 3, 4, 5, 6, 1301, now());

-- 1000 - 1499
CREATE TABLE child_jstock_1000_1500 (
    CONSTRAINT pk_jstock_1000_1500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_1000_1500 CHECK (code >= 1000 AND code < 1500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_1000_1500 ON child_jstock_1000_1500 (code);

-- 1500 - 1999
CREATE TABLE child_jstock_1500_2000 (
    CONSTRAINT pk_jstock_1500_2000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_1500_2000 CHECK (code >= 1500 AND code < 2000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_1500_2000 ON child_jstock_1500_2000 (code);

-- 2000 - 2499
CREATE TABLE child_jstock_2000_2500 (
    CONSTRAINT pk_jstock_2000_2500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_2000_2500 CHECK (code >= 2000 AND code < 2500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_2000_2500 ON child_jstock_2000_2500 (code);

-- 2500 - 2999
CREATE TABLE child_jstock_2500_3000 (
    CONSTRAINT pk_jstock_2500_3000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_2500_3000 CHECK (code >= 2500 AND code < 3000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_2500_3000 ON child_jstock_2500_3000 (code);

-- 3000 - 3499
CREATE TABLE child_jstock_3000_3500 (
    CONSTRAINT pk_jstock_3000_3500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_3000_3500 CHECK (code >= 3000 AND code < 3500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_3000_3500 ON child_jstock_3000_3500 (code);

-- 3500 - 3999
CREATE TABLE child_jstock_3500_4000 (
    CONSTRAINT pk_jstock_3500_4000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_3500_4000 CHECK (code >= 3500 AND code < 4000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_3500_4000 ON child_jstock_3500_4000 (code);

-- 4000 - 4499
CREATE TABLE child_jstock_4000_4500 (
    CONSTRAINT pk_jstock_4000_4500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_4000_4500 CHECK (code >= 4000 AND code < 4500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_4000_4500 ON child_jstock_4000_4500 (code);

-- 4500 - 4999
CREATE TABLE child_jstock_4500_5000 (
    CONSTRAINT pk_jstock_4500_5000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_4500_5000 CHECK (code >= 4500 AND code < 5000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_4500_5000 ON child_jstock_4500_5000 (code);

-- 5000 - 5499
CREATE TABLE child_jstock_5000_5500 (
    CONSTRAINT pk_jstock_5000_5500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_5000_5500 CHECK (code >= 5000 AND code < 5500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_5000_5500 ON child_jstock_5000_5500 (code);

-- 5500 - 5999
CREATE TABLE child_jstock_5500_6000 (
    CONSTRAINT pk_jstock_5500_6000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_5500_6000 CHECK (code >= 5500 AND code < 6000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_5500_6000 ON child_jstock_5500_6000 (code);

-- 6000 - 6499
CREATE TABLE child_jstock_6000_6500 (
    CONSTRAINT pk_jstock_6000_6500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_6000_6500 CHECK (code >= 6000 AND code < 6500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_6000_6500 ON child_jstock_6000_6500 (code);

-- 6500 - 6999
CREATE TABLE child_jstock_6500_7000 (
    CONSTRAINT pk_jstock_6500_7000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_6500_7000 CHECK (code >= 6500 AND code < 7000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_6500_7000 ON child_jstock_6500_7000 (code);

-- 7000 - 7499
CREATE TABLE child_jstock_7000_7500 (
    CONSTRAINT pk_jstock_7000_7500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_7000_7500 CHECK (code >= 7000 AND code < 7500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_7000_7500 ON child_jstock_7000_7500 (code);

-- 7500 - 7999
CREATE TABLE child_jstock_7500_8000 (
    CONSTRAINT pk_jstock_7500_8000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_7500_8000 CHECK (code >= 7500 AND code < 8000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_7500_8000 ON child_jstock_7500_8000 (code);

-- 8000 - 8499
CREATE TABLE child_jstock_8000_8500 (
    CONSTRAINT pk_jstock_8000_8500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_8000_8500 CHECK (code >= 8000 AND code < 8500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_8000_8500 ON child_jstock_8000_8500 (code);

-- 8500 - 8999
CREATE TABLE child_jstock_8500_9000 (
    CONSTRAINT pk_jstock_8500_9000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_8500_9000 CHECK (code >= 8500 AND code < 9000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_8500_9000 ON child_jstock_8500_9000 (code);

-- 9000 - 9499
CREATE TABLE child_jstock_9000_9500 (
    CONSTRAINT pk_jstock_9000_9500 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_9000_9500 CHECK (code >= 9000 AND code < 9500)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_9000_9500 ON child_jstock_9000_9500 (code);

-- 9500 - 9999
CREATE TABLE child_jstock_9500_10000 (
    CONSTRAINT pk_jstock_9500_10000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_9500_10000 CHECK (code >= 9500 AND code < 10000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_9500_10000 ON child_jstock_9500_10000 (code);

-- 10000 - 100000
CREATE TABLE child_jstock_10000_100000 (
    CONSTRAINT pk_jstock_10000_100000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_10000_100000 CHECK (code >= 10000 AND code < 100000)
) INHERITS (trn_japan_stock);
CREATE INDEX idx_jstock_10000_100000 ON child_jstock_10000_100000 (code);

-- View v_japan_stock
--CREATE OR REPLACE VIEW v_japan_stock AS SELECT * FROM trn_japan_stock;

-- Insert FUNCTION
/*CREATE OR REPLACE FUNCTION fn_insert_jstock() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
    IF ( NEW.code >= 1000 AND NEW.code < 1500 ) THEN
        INSERT INTO child_jstock_1000_1500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 1500 AND NEW.code < 2000 ) THEN
        INSERT INTO child_jstock_1500_2000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 2000 AND NEW.code < 2500 ) THEN
        INSERT INTO child_jstock_2000_2500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 2500 AND NEW.code < 3000 ) THEN
        INSERT INTO child_jstock_2500_3000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 3000 AND NEW.code < 3500 ) THEN
        INSERT INTO child_jstock_3000_3500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 3500 AND NEW.code < 4000 ) THEN
        INSERT INTO child_jstock_3500_4000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 4000 AND NEW.code < 4500 ) THEN
        INSERT INTO child_jstock_4000_4500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 4500 AND NEW.code < 5000 ) THEN
        INSERT INTO child_jstock_4500_5000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 5000 AND NEW.code < 5500 ) THEN
        INSERT INTO child_jstock_5000_5500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 5500 AND NEW.code < 6000 ) THEN
        INSERT INTO child_jstock_5500_6000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 6000 AND NEW.code < 6500 ) THEN
        INSERT INTO child_jstock_6000_6500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 6500 AND NEW.code < 7000 ) THEN
        INSERT INTO child_jstock_6500_7000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 7000 AND NEW.code < 7500 ) THEN
        INSERT INTO child_jstock_7000_7500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 7500 AND NEW.code < 8000 ) THEN
        INSERT INTO child_jstock_7500_8000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 8000 AND NEW.code < 8500 ) THEN
        INSERT INTO child_jstock_8000_8500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 8500 AND NEW.code < 9000 ) THEN
        INSERT INTO child_jstock_8500_9000 VALUES (NEW.*);
    ELSIF ( NEW.code >= 9000 AND NEW.code < 9500 ) THEN
        INSERT INTO child_jstock_9000_9500 VALUES (NEW.*);
    ELSIF ( NEW.code >= 9500 AND NEW.code < 10000 ) THEN
        INSERT INTO child_jstock_9500_10000 VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Code out of range';
    END IF;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views we return NEW
END;
$$
LANGUAGE plpgsql;
*/
CREATE OR REPLACE FUNCTION fn_insert_jstock() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.code >= 1000 AND NEW.code < 1500 ) THEN
        INSERT INTO child_jstock_1000_1500 VALUES (NEW.*);
    WHEN ( NEW.code >= 1500 AND NEW.code < 2000 ) THEN
        INSERT INTO child_jstock_1500_2000 VALUES (NEW.*);
    WHEN ( NEW.code >= 2000 AND NEW.code < 2500 ) THEN
        INSERT INTO child_jstock_2000_2500 VALUES (NEW.*);
    WHEN ( NEW.code >= 2500 AND NEW.code < 3000 ) THEN
        INSERT INTO child_jstock_2500_3000 VALUES (NEW.*);
    WHEN ( NEW.code >= 3000 AND NEW.code < 3500 ) THEN
        INSERT INTO child_jstock_3000_3500 VALUES (NEW.*);
    WHEN ( NEW.code >= 3500 AND NEW.code < 4000 ) THEN
        INSERT INTO child_jstock_3500_4000 VALUES (NEW.*);
    WHEN ( NEW.code >= 4000 AND NEW.code < 4500 ) THEN
        INSERT INTO child_jstock_4000_4500 VALUES (NEW.*);
    WHEN ( NEW.code >= 4500 AND NEW.code < 5000 ) THEN
        INSERT INTO child_jstock_4500_5000 VALUES (NEW.*);
    WHEN ( NEW.code >= 5000 AND NEW.code < 5500 ) THEN
        INSERT INTO child_jstock_5000_5500 VALUES (NEW.*);
    WHEN ( NEW.code >= 5500 AND NEW.code < 6000 ) THEN
        INSERT INTO child_jstock_5500_6000 VALUES (NEW.*);
    WHEN ( NEW.code >= 6000 AND NEW.code < 6500 ) THEN
        INSERT INTO child_jstock_6000_6500 VALUES (NEW.*);
    WHEN ( NEW.code >= 6500 AND NEW.code < 7000 ) THEN
        INSERT INTO child_jstock_6500_7000 VALUES (NEW.*);
    WHEN ( NEW.code >= 7000 AND NEW.code < 7500 ) THEN
        INSERT INTO child_jstock_7000_7500 VALUES (NEW.*);
    WHEN ( NEW.code >= 7500 AND NEW.code < 8000 ) THEN
        INSERT INTO child_jstock_7500_8000 VALUES (NEW.*);
    WHEN ( NEW.code >= 8000 AND NEW.code < 8500 ) THEN
        INSERT INTO child_jstock_8000_8500 VALUES (NEW.*);
    WHEN ( NEW.code >= 8500 AND NEW.code < 9000 ) THEN
        INSERT INTO child_jstock_8500_9000 VALUES (NEW.*);
    WHEN ( NEW.code >= 9000 AND NEW.code < 9500 ) THEN
        INSERT INTO child_jstock_9000_9500 VALUES (NEW.*);
    WHEN ( NEW.code >= 9500 AND NEW.code < 10000 ) THEN
        INSERT INTO child_jstock_9500_10000 VALUES (NEW.*);
    WHEN ( NEW.code >= 10000 AND NEW.code < 100000 ) THEN
        INSERT INTO child_jstock_10000_100000 VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
--CREATE TRIGGER tr_insert_jstock INSTEAD OF INSERT ON v_japan_stock
--FOR EACH ROW EXECUTE PROCEDURE fn_insert_jstock();

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_jstock BEFORE INSERT ON trn_japan_stock
FOR EACH ROW EXECUTE PROCEDURE fn_insert_jstock();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_jstock_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_japan_stock;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_jstock_master_cleanup AFTER INSERT ON trn_japan_stock
FOR EACH ROW EXECUTE PROCEDURE fn_jstock_master_cleanup();

/*
CREATE RULE rule_jstock_1000_1500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 1000 AND code < 1500 )
DO INSTEAD
  INSERT INTO child_jstock_1000_1500 VALUES (NEW.*);

CREATE RULE rule_jstock_1500_2000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 1500 AND code < 2000 )
DO INSTEAD
  INSERT INTO child_jstock_1500_2000 VALUES (NEW.*);

CREATE RULE rule_jstock_2000_2500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 2000 AND code < 2500 )
DO INSTEAD
  INSERT INTO child_jstock_2000_2500 VALUES (NEW.*);

CREATE RULE rule_jstock_2500_3000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 2500 AND code < 3000 )
DO INSTEAD
  INSERT INTO child_jstock_2500_3000 VALUES (NEW.*);

CREATE RULE rule_jstock_3000_3500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 3000 AND code < 3500 )
DO INSTEAD
  INSERT INTO child_jstock_3000_3500 VALUES (NEW.*);

CREATE RULE rule_jstock_3500_4000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 3500 AND code < 4000 )
DO INSTEAD
  INSERT INTO child_jstock_3500_4000 VALUES (NEW.*);

CREATE RULE rule_jstock_4000_4500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 4000 AND code < 4500 )
DO INSTEAD
  INSERT INTO child_jstock_4000_4500 VALUES (NEW.*);

CREATE RULE rule_jstock_4500_5000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 4500 AND code < 5000 )
DO INSTEAD
  INSERT INTO child_jstock_4500_5000 VALUES (NEW.*);

CREATE RULE rule_jstock_5000_5500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 5000 AND code < 5500 )
DO INSTEAD
  INSERT INTO child_jstock_5000_5500 VALUES (NEW.*);

CREATE RULE rule_jstock_5500_6000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 5500 AND code < 6000 )
DO INSTEAD
  INSERT INTO child_jstock_5500_6000 VALUES (NEW.*);

CREATE RULE rule_jstock_6000_6500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 6000 AND code < 6500 )
DO INSTEAD
  INSERT INTO child_jstock_6000_6500 VALUES (NEW.*);

CREATE RULE rule_jstock_6500_7000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 6500 AND code < 7000 )
DO INSTEAD
  INSERT INTO child_jstock_6500_7000 VALUES (NEW.*);

CREATE RULE rule_jstock_7000_7500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 7000 AND code < 7500 )
DO INSTEAD
  INSERT INTO child_jstock_7000_7500 VALUES (NEW.*);

CREATE RULE rule_jstock_7500_8000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 7500 AND code < 8000 )
DO INSTEAD
  INSERT INTO child_jstock_7500_8000 VALUES (NEW.*);

CREATE RULE rule_jstock_8000_8500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 8000 AND code < 8500 )
DO INSTEAD
  INSERT INTO child_jstock_8000_8500 VALUES (NEW.*);

CREATE RULE rule_jstock_8500_9000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 8500 AND code < 9000 )
DO INSTEAD
  INSERT INTO child_jstock_8500_9000 VALUES (NEW.*);

CREATE RULE rule_jstock_9000_9500 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 9000 AND code < 9500 )
DO INSTEAD
  INSERT INTO child_jstock_9000_9500 VALUES (NEW.*);

CREATE RULE rule_jstock_9500_10000 AS
ON INSERT TO trn_japan_stock WHERE
  ( code >= 9500 AND code < 10000 )
DO INSTEAD
  INSERT INTO child_jstock_9500_10000 VALUES (NEW.*);
*/