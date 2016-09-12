-- Table: trn_fx_tick

-- DROP TABLE trn_fx_tick;

CREATE TABLE trn_fx_tick
(
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
--  ,CONSTRAINT trn_fx_tick_pkey PRIMARY KEY (currency_pair, regist_date)
);
CREATE INDEX idx_fx_tick ON trn_fx_tick (currency_pair, regist_date);
