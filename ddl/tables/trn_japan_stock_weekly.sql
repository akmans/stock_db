CREATE TABLE trn_japan_stock_weekly
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
  CONSTRAINT instrument_fkey FOREIGN KEY (code)
      REFERENCES mst_instrument (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- 1000 - 1999
CREATE TABLE child_jstock_w_1000_1999 (
    CONSTRAINT pk_jstock_w_1000_1999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_1000_1999 CHECK (code >= 1000 AND code < 2000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_1000_1999 ON child_jstock_w_1000_1999 (code);

-- 2000 - 2999
CREATE TABLE child_jstock_w_2000_2999 (
    CONSTRAINT pk_jstock_w_2000_2999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_2000_2999 CHECK (code >= 2000 AND code < 3000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_2000_2999 ON child_jstock_w_2000_2999 (code);

-- 3000 - 3999
CREATE TABLE child_jstock_w_3000_3999 (
    CONSTRAINT pk_jstock_w_3000_3999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_3000_3999 CHECK (code >= 3000 AND code < 4000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_3000_3999 ON child_jstock_w_3000_3999 (code);

-- 4000 - 4999
CREATE TABLE child_jstock_w_4000_4999 (
    CONSTRAINT pk_jstock_w_4000_4999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_4000_4999 CHECK (code >= 4000 AND code < 5000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_4000_4999 ON child_jstock_w_4000_4999 (code);

-- 5000 - 5999
CREATE TABLE child_jstock_w_5000_5999 (
    CONSTRAINT pk_jstock_w_5000_5999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_5000_5999 CHECK (code >= 5000 AND code < 6000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_5000_5999 ON child_jstock_w_5000_5999 (code);

-- 6000 - 6999
CREATE TABLE child_jstock_w_6000_6999 (
    CONSTRAINT pk_jstock_w_6000_6999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_6000_6999 CHECK (code >= 6000 AND code < 7000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_6000_6999 ON child_jstock_w_6000_6999 (code);

-- 7000 - 7999
CREATE TABLE child_jstock_w_7000_7999 (
    CONSTRAINT pk_jstock_w_7000_7999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_7000_7999 CHECK (code >= 7000 AND code < 8000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_7000_7999 ON child_jstock_w_7000_7999 (code);

-- 8000 - 8999
CREATE TABLE child_jstock_w_8000_8999 (
    CONSTRAINT pk_jstock_w_8000_8999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_8000_8999 CHECK (code >= 8000 AND code < 9000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_8000_8999 ON child_jstock_w_8000_8999 (code);

-- 9000 - 9999
CREATE TABLE child_jstock_w_9000_9999 (
    CONSTRAINT pk_jstock_w_9000_9999 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_9000_9999 CHECK (code >= 9000 AND code < 10000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_9000_9999 ON child_jstock_w_9000_9999 (code);

-- 10000 - 100000
CREATE TABLE child_jstock_w_10000_100000 (
    CONSTRAINT pk_jstock_w_10000_100000 PRIMARY KEY (code , regist_date),
    CONSTRAINT ck_jstock_w_10000_100000 CHECK (code >= 10000 AND code < 100000)
) INHERITS (trn_japan_stock_weekly);
CREATE INDEX idx_jstock_w_10000_100000 ON child_jstock_w_10000_100000 (code);

CREATE OR REPLACE FUNCTION fn_insert_jstock_weekly() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.code >= 1000 AND NEW.code < 2000 ) THEN
        INSERT INTO child_jstock_w_1000_1999 VALUES (NEW.*);
    WHEN ( NEW.code >= 2000 AND NEW.code < 3000 ) THEN
        INSERT INTO child_jstock_w_2000_2999 VALUES (NEW.*);
    WHEN ( NEW.code >= 3000 AND NEW.code < 4000 ) THEN
        INSERT INTO child_jstock_w_3000_3999 VALUES (NEW.*);
    WHEN ( NEW.code >= 4000 AND NEW.code < 5000 ) THEN
        INSERT INTO child_jstock_w_4000_4999 VALUES (NEW.*);
    WHEN ( NEW.code >= 5000 AND NEW.code < 6000 ) THEN
        INSERT INTO child_jstock_w_5000_5999 VALUES (NEW.*);
    WHEN ( NEW.code >= 6000 AND NEW.code < 7000 ) THEN
        INSERT INTO child_jstock_w_6000_6999 VALUES (NEW.*);
    WHEN ( NEW.code >= 7000 AND NEW.code < 8000 ) THEN
        INSERT INTO child_jstock_w_7000_7999 VALUES (NEW.*);
    WHEN ( NEW.code >= 8000 AND NEW.code < 9000 ) THEN
        INSERT INTO child_jstock_w_8000_8999 VALUES (NEW.*);
    WHEN ( NEW.code >= 9000 AND NEW.code < 10000 ) THEN
        INSERT INTO child_jstock_w_9000_9999 VALUES (NEW.*);
    WHEN ( NEW.code >= 10000 AND NEW.code < 100000 ) THEN
        INSERT INTO child_jstock_w_10000_100000 VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_jstock_weekly BEFORE INSERT ON trn_japan_stock_weekly
FOR EACH ROW EXECUTE PROCEDURE fn_insert_jstock_weekly();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_jstock_weekly_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_japan_stock_weekly;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_jstock_weekly_master_cleanup AFTER INSERT ON trn_japan_stock_weekly
FOR EACH ROW EXECUTE PROCEDURE fn_jstock_weekly_master_cleanup();
