-- Table: trn_fx_hour

-- DROP TABLE trn_fx_hour;

CREATE TABLE trn_fx_hour
(
  currency_pair char(6) NOT NULL,
  regist_date timestamp with time zone NOT NULL,
  opening_price numeric NOT NULL,
  high_price numeric NOT NULL,
  low_price numeric NOT NULL,
  finish_price numeric NOT NULL,
  av_opening_price numeric,
  av_finish_price numeric,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20)
--  ,CONSTRAINT japan_stock_pkey PRIMARY KEY (currency_pair, regist_date),
);
--update trn_fx_tick set processed_flag=1, updated_by='system', updated_date=now() where currency_pair='usdjpy' and regist_date>='2009-05-01 00:00:00' and regist_date<'2009-05-02 00:00:00';
-- USD/JPY
CREATE TABLE child_fx_hour_usdjpy (
    CONSTRAINT pk_fx_hour_usdjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_usdjpy ON child_fx_hour_usdjpy (regist_date);

-- EUR/JPY
CREATE TABLE child_fx_hour_eurjpy (
    CONSTRAINT pk_fx_hour_eurjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_eurjpy ON child_fx_hour_eurjpy (regist_date);

-- AUD/JPY
CREATE TABLE child_fx_hour_audjpy (
    CONSTRAINT pk_fx_hour_audjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_audjpy ON child_fx_hour_audjpy (regist_date);

-- GBP/JPY
CREATE TABLE child_fx_hour_gbpjpy (
    CONSTRAINT pk_fx_hour_gbpjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_gbpjpy ON child_fx_hour_gbpjpy (regist_date);

-- CHF/JPY
CREATE TABLE child_fx_hour_chfjpy (
    CONSTRAINT pk_fx_hour_chfjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_chfjpy ON child_fx_hour_chfjpy (regist_date);

-- EUR/USD
CREATE TABLE child_fx_hour_eurusd (
    CONSTRAINT pk_fx_hour_eurusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_eurusd ON child_fx_hour_eurusd (regist_date);

-- GBP/USD
CREATE TABLE child_fx_hour_gbpusd (
    CONSTRAINT pk_fx_hour_gbpusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_gbpusd ON child_fx_hour_gbpusd (regist_date);

-- AUD/USD
CREATE TABLE child_fx_hour_audusd (
    CONSTRAINT pk_fx_hour_audusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_audusd ON child_fx_hour_audusd (regist_date);

-- USD/CHF
CREATE TABLE child_fx_hour_usdchf (
    CONSTRAINT pk_fx_hour_usdchf PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_hour_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_hour);
CREATE INDEX idx_fx_hour_usdchf ON child_fx_hour_usdchf (regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_hour() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_hour_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_hour_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_hour_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_hour_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_hour_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_hour_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_hour_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_hour_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_hour_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_hour BEFORE INSERT ON trn_fx_hour
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_hour();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_hour_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_hour;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_hour_master_cleanup AFTER INSERT ON trn_fx_hour
FOR EACH ROW EXECUTE PROCEDURE fn_fx_hour_master_cleanup();
