-- Table: trn_fx_week

-- DROP TABLE trn_fx_week;

CREATE TABLE trn_fx_week
(
  currency_pair char(6) NOT NULL,
  regist_date timestamp without time zone NOT NULL,
  opening_price numeric(10, 6) NOT NULL,
  high_price numeric(10, 6) NOT NULL,
  low_price numeric(10, 6) NOT NULL,
  finish_price numeric(10, 6) NOT NULL,
  av_opening_price numeric(10, 6),
  av_finish_price numeric(10, 6),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20)
--  ,CONSTRAINT japan_stock_pkey PRIMARY KEY (currency_pair, regist_date),
);

-- USD/JPY
CREATE TABLE child_fx_week_usdjpy (
    CONSTRAINT pk_fx_week_usdjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_usdjpy ON child_fx_week_usdjpy (regist_date);

-- EUR/JPY
CREATE TABLE child_fx_week_eurjpy (
    CONSTRAINT pk_fx_week_eurjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_eurjpy ON child_fx_week_eurjpy (regist_date);

-- AUD/JPY
CREATE TABLE child_fx_week_audjpy (
    CONSTRAINT pk_fx_week_audjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_audjpy ON child_fx_week_audjpy (regist_date);

-- GBP/JPY
CREATE TABLE child_fx_week_gbpjpy (
    CONSTRAINT pk_fx_week_gbpjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_gbpjpy ON child_fx_week_gbpjpy (regist_date);

-- CHF/JPY
CREATE TABLE child_fx_week_chfjpy (
    CONSTRAINT pk_fx_week_chfjpy PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_chfjpy ON child_fx_week_chfjpy (regist_date);

-- EUR/USD
CREATE TABLE child_fx_week_eurusd (
    CONSTRAINT pk_fx_week_eurusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_eurusd ON child_fx_week_eurusd (regist_date);

-- GBP/USD
CREATE TABLE child_fx_week_gbpusd (
    CONSTRAINT pk_fx_week_gbpusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_gbpusd ON child_fx_week_gbpusd (regist_date);

-- AUD/USD
CREATE TABLE child_fx_week_audusd (
    CONSTRAINT pk_fx_week_audusd PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_audusd ON child_fx_week_audusd (regist_date);

-- USD/CHF
CREATE TABLE child_fx_week_usdchf (
    CONSTRAINT pk_fx_week_usdchf PRIMARY KEY (currency_pair , regist_date),
    CONSTRAINT ck_fx_week_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_week);
CREATE INDEX idx_fx_week_usdchf ON child_fx_week_usdchf (regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_week() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_week_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_week_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_week_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_week_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_week_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_week_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_week_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_week_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_week_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_week BEFORE INSERT ON trn_fx_week
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_week();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_week_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_week;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_week_master_cleanup AFTER INSERT ON trn_fx_week
FOR EACH ROW EXECUTE PROCEDURE fn_fx_week_master_cleanup();
