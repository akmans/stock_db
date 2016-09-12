-- Table: trn_fx_6hour

-- DROP TABLE trn_fx_6hour;

CREATE TABLE trn_fx_6hour
(
  currency_pair char(6) NOT NULL,
  regist_date timestamp with time zone NOT NULL,
  opening_price numeric NOT NULL,
  high_price numeric NOT NULL,
  low_price numeric NOT NULL,
  finish_price numeric NOT NULL,
--  av_opening_price numeric NOT NULL,
--  av_finish_price numeric NOT NULL,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20)
--  ,CONSTRAINT japan_stock_pkey PRIMARY KEY (currency_pair, regist_date),
);

-- USD/JPY
CREATE TABLE child_fx_6hour_usdjpy (
    CONSTRAINT pk_fx_6hour_usdjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_usdjpy ON child_fx_6hour_usdjpy (regist_date);

-- EUR/JPY
CREATE TABLE child_fx_6hour_eurjpy (
    CONSTRAINT pk_fx_6hour_eurjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_eurjpy ON child_fx_6hour_eurjpy (regist_date);

-- AUD/JPY
CREATE TABLE child_fx_6hour_audjpy (
    CONSTRAINT pk_fx_6hour_audjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_audjpy ON child_fx_6hour_audjpy (regist_date);

-- GBP/JPY
CREATE TABLE child_fx_6hour_gbpjpy (
    CONSTRAINT pk_fx_6hour_gbpjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_gbpjpy ON child_fx_6hour_gbpjpy (regist_date);

-- CHF/JPY
CREATE TABLE child_fx_6hour_chfjpy (
    CONSTRAINT pk_fx_6hour_chfjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_chfjpy ON child_fx_6hour_chfjpy (regist_date);

-- EUR/USD
CREATE TABLE child_fx_6hour_eurusd (
    CONSTRAINT pk_fx_6hour_eurusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_eurusd ON child_fx_6hour_eurusd (regist_date);

-- GBP/USD
CREATE TABLE child_fx_6hour_gbpusd (
    CONSTRAINT pk_fx_6hour_gbpusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_gbpusd ON child_fx_6hour_gbpusd (regist_date);

-- AUD/USD
CREATE TABLE child_fx_6hour_audusd (
    CONSTRAINT pk_fx_6hour_audusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_audusd ON child_fx_6hour_audusd (regist_date);

-- USD/CHF
CREATE TABLE child_fx_6hour_usdchf (
    CONSTRAINT pk_fx_6hour_usdchf PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_6hour_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_6hour);
CREATE INDEX idx_fx_6hour_usdchf ON child_fx_6hour_usdchf (regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_6hour() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_6hour_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_6hour_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_6hour_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_6hour_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_6hour_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_6hour_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_6hour_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_6hour_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_6hour_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_6hour BEFORE INSERT ON trn_fx_6hour
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_6hour();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_6hour_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_6hour;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_6hour_master_cleanup AFTER INSERT ON trn_fx_6hour
FOR EACH ROW EXECUTE PROCEDURE fn_fx_6hour_master_cleanup();
