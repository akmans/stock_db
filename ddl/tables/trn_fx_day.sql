-- Table: trn_fx_day

-- DROP TABLE trn_fx_day;

CREATE TABLE trn_fx_day
(
  currency_pair char(6) NOT NULL,
  regist_date timestamp with time zone NOT NULL,
  opening_price numeric NOT NULL,
  high_price numeric NOT NULL,
  low_price numeric NOT NULL,
  finish_price numeric NOT NULL,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20)
--  ,CONSTRAINT japan_stock_pkey PRIMARY KEY (currency_pair, regist_date),
);

-- USD/JPY
CREATE TABLE child_fx_day_usdjpy (
    CONSTRAINT pk_fx_day_usdjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_usdjpy ON child_fx_day_usdjpy (regist_date);

-- EUR/JPY
CREATE TABLE child_fx_day_eurjpy (
    CONSTRAINT pk_fx_day_eurjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_eurjpy ON child_fx_day_eurjpy (regist_date);

-- AUD/JPY
CREATE TABLE child_fx_day_audjpy (
    CONSTRAINT pk_fx_day_audjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_audjpy ON child_fx_day_audjpy (regist_date);

-- GBP/JPY
CREATE TABLE child_fx_day_gbpjpy (
    CONSTRAINT pk_fx_day_gbpjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_gbpjpy ON child_fx_day_gbpjpy (regist_date);

-- CHF/JPY
CREATE TABLE child_fx_day_chfjpy (
    CONSTRAINT pk_fx_day_chfjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_chfjpy ON child_fx_day_chfjpy (regist_date);

-- EUR/USD
CREATE TABLE child_fx_day_eurusd (
    CONSTRAINT pk_fx_day_eurusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_eurusd ON child_fx_day_eurusd (regist_date);

-- GBP/USD
CREATE TABLE child_fx_day_gbpusd (
    CONSTRAINT pk_fx_day_gbpusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_gbpusd ON child_fx_day_gbpusd (regist_date);

-- AUD/USD
CREATE TABLE child_fx_day_audusd (
    CONSTRAINT pk_fx_day_audusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_audusd ON child_fx_day_audusd (regist_date);

-- USD/CHF
CREATE TABLE child_fx_day_usdchf (
    CONSTRAINT pk_fx_day_usdchf PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_day_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_day);
CREATE INDEX idx_fx_day_usdchf ON child_fx_day_usdchf (regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_day() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_day_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_day_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_day_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_day_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_day_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_day_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_day_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_day_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_day_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_day BEFORE INSERT ON trn_fx_day
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_day();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_day_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_day;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_day_master_cleanup AFTER INSERT ON trn_fx_day
FOR EACH ROW EXECUTE PROCEDURE fn_fx_day_master_cleanup();
