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
  CONSTRAINT japan_stock_pkey PRIMARY KEY (code , regist_date ),
  CONSTRAINT instrument_fkey FOREIGN KEY (code)
      REFERENCES mst_instrument (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
