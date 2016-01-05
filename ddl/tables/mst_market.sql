-- Table: mst_market

-- DROP TABLE mst_market;

CREATE TABLE mst_market
(
  code integer NOT NULL,
  name character varying(200),
  CONSTRAINT market_pkey PRIMARY KEY (code )
);
