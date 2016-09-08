-- Table: trn_fx_month

-- DROP TABLE trn_fx_month;

CREATE TABLE trn_fx_month
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
CREATE TABLE child_fx_month_usdjpy (
    CONSTRAINT pk_fx_month_usdjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_usdjpy ON child_fx_month_usdjpy (regist_date);

-- EUR/JPY
CREATE TABLE child_fx_month_eurjpy (
    CONSTRAINT pk_fx_month_eurjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_eurjpy ON child_fx_month_eurjpy (regist_date);

-- AUD/JPY
CREATE TABLE child_fx_month_audjpy (
    CONSTRAINT pk_fx_month_audjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_audjpy ON child_fx_month_audjpy (regist_date);

-- GBP/JPY
CREATE TABLE child_fx_month_gbpjpy (
    CONSTRAINT pk_fx_month_gbpjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_gbpjpy ON child_fx_month_gbpjpy (regist_date);

-- CHF/JPY
CREATE TABLE child_fx_month_chfjpy (
    CONSTRAINT pk_fx_month_chfjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_chfjpy ON child_fx_month_chfjpy (regist_date);

-- EUR/USD
CREATE TABLE child_fx_month_eurusd (
    CONSTRAINT pk_fx_month_eurusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_eurusd ON child_fx_month_eurusd (regist_date);

-- GBP/USD
CREATE TABLE child_fx_month_gbpusd (
    CONSTRAINT pk_fx_month_gbpusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_gbpusd ON child_fx_month_gbpusd (regist_date);

-- AUD/USD
CREATE TABLE child_fx_month_audusd (
    CONSTRAINT pk_fx_month_audusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_audusd ON child_fx_month_audusd (regist_date);

-- USD/CHF
CREATE TABLE child_fx_month_usdchf (
    CONSTRAINT pk_fx_month_usdchf PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_month_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_month);
CREATE INDEX idx_fx_month_usdchf ON child_fx_month_usdchf (regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_month() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_month_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_month_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_month_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_month_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_month_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_month_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_month_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_month_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_month_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_month BEFORE INSERT ON trn_fx_month
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_month();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_month_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_month;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_month_master_cleanup AFTER INSERT ON trn_fx_month
FOR EACH ROW EXECUTE PROCEDURE fn_fx_month_master_cleanup();
