CREATE TABLE trn_stock_weekly
(
  code integer NOT NULL,
  regist_date date NOT NULL,
  opening_price integer NOT NULL,
  high_price integer NOT NULL,
  low_price integer NOT NULL,
  finish_price integer NOT NULL,
  turnover integer,
  CONSTRAINT stock_weekly_pkey PRIMARY KEY (code , regist_date ),
  CONSTRAINT instrument_fkey FOREIGN KEY (code)
      REFERENCES mst_instrument (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
