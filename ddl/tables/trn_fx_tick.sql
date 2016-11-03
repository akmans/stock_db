-- Table: trn_fx_tick

-- DROP TABLE trn_fx_tick;

CREATE TABLE trn_fx_tick
(
  code bigserial NOT NULL,
  currency_pair char(6) NOT NULL,
  regist_date timestamp without time zone NOT NULL,
  bid_price numeric(10, 6) NOT NULL,
  ask_price numeric(10, 6) NOT NULL,
  mid_price numeric(10, 6),
  processed_flag integer default 0,
  created_date timestamp with time zone default current_timestamp,
  updated_date timestamp with time zone default current_timestamp,
  created_by character varying(20) default 'default',
  updated_by character varying(20) default 'default'
--  ,CONSTRAINT trn_fx_tick_pkey PRIMARY KEY (code)
);
--CREATE INDEX idx_fx_tick ON trn_fx_tick (currency_pair, regist_date);

-- USD/JPY
CREATE TABLE child_fx_tick_usdjpy (
    CONSTRAINT pk_fx_tick_usdjpy PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_usdjpy CHECK (currency_pair = 'usdjpy')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_usdjpy ON child_fx_tick_usdjpy (currency_pair, regist_date);

-- EUR/JPY
CREATE TABLE child_fx_tick_eurjpy (
    CONSTRAINT pk_fx_tick_eurjpy PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_eurjpy CHECK (currency_pair = 'eurjpy')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_eurjpy ON child_fx_tick_eurjpy (currency_pair, regist_date);

-- AUD/JPY
CREATE TABLE child_fx_tick_audjpy (
    CONSTRAINT pk_fx_tick_audjpy PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_audjpy CHECK (currency_pair = 'audjpy')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_audjpy ON child_fx_tick_audjpy (currency_pair, regist_date);

-- GBP/JPY
CREATE TABLE child_fx_tick_gbpjpy (
    CONSTRAINT pk_fx_tick_gbpjpy PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_gbpjpy CHECK (currency_pair = 'gbpjpy')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_gbpjpy ON child_fx_tick_gbpjpy (currency_pair, regist_date);

-- CHF/JPY
CREATE TABLE child_fx_tick_chfjpy (
    CONSTRAINT pk_fx_tick_chfjpy PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_chfjpy CHECK (currency_pair = 'chfjpy')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_chfjpy ON child_fx_tick_chfjpy (currency_pair, regist_date);

-- EUR/USD
CREATE TABLE child_fx_tick_eurusd (
    CONSTRAINT pk_fx_tick_eurusd PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_eurusd CHECK (currency_pair = 'eurusd')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_eurusd ON child_fx_tick_eurusd (currency_pair, regist_date);

-- GBP/USD
CREATE TABLE child_fx_tick_gbpusd (
    CONSTRAINT pk_fx_tick_gbpusd PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_gbpusd CHECK (currency_pair = 'gbpusd')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_gbpusd ON child_fx_tick_gbpusd (currency_pair, regist_date);

-- AUD/USD
CREATE TABLE child_fx_tick_audusd (
    CONSTRAINT pk_fx_tick_audusd PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_audusd CHECK (currency_pair = 'audusd')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_audusd ON child_fx_tick_audusd (currency_pair, regist_date);

-- USD/CHF
CREATE TABLE child_fx_tick_usdchf (
    CONSTRAINT pk_fx_tick_usdchf PRIMARY KEY (code),
    CONSTRAINT ck_fx_tick_usdchf CHECK (currency_pair = 'usdchf')
) INHERITS (trn_fx_tick);
CREATE INDEX idx_fx_tick_usdchf ON child_fx_tick_usdchf (currency_pair, regist_date);

CREATE OR REPLACE FUNCTION fn_insert_fx_tick() RETURNS TRIGGER AS $$
BEGIN
--	SET NOCOUNT ON;
  CASE
    WHEN ( NEW.currency_pair = 'usdjpy' ) THEN
        INSERT INTO child_fx_tick_usdjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurjpy' ) THEN
        INSERT INTO child_fx_tick_eurjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audjpy' ) THEN
        INSERT INTO child_fx_tick_audjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpjpy' ) THEN
        INSERT INTO child_fx_tick_gbpjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'chfjpy' ) THEN
        INSERT INTO child_fx_tick_chfjpy VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'eurusd' ) THEN
        INSERT INTO child_fx_tick_eurusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'gbpusd' ) THEN
        INSERT INTO child_fx_tick_gbpusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'audusd' ) THEN
        INSERT INTO child_fx_tick_audusd VALUES (NEW.*);
    WHEN ( NEW.currency_pair = 'usdchf' ) THEN
        INSERT INTO child_fx_tick_usdchf VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Instrument code out of range';
    END CASE;
    RETURN NEW; -- RETURN NEW in this case, typically you'd return NULL from this trigger, but for views or handling hibernate error("0 row affected" ) we return NEW. 
END;
$$
LANGUAGE plpgsql;

-- create "INSTEAD OF" trigger
CREATE TRIGGER tr_insert_fx_tick BEFORE INSERT ON trn_fx_tick
FOR EACH ROW EXECUTE PROCEDURE fn_insert_fx_tick();

-- Cleanup master function
CREATE OR REPLACE FUNCTION fn_fx_tick_master_cleanup() RETURNS trigger AS $$
BEGIN
  DELETE FROM only trn_fx_tick;
  RETURN NULL;
END;
$$
LANGUAGE plpgsql;

-- Create after "INSERT ON" trigger to handle clean up.
CREATE TRIGGER tr_fx_tick_master_cleanup AFTER INSERT ON trn_fx_tick
FOR EACH ROW EXECUTE PROCEDURE fn_fx_tick_master_cleanup();
