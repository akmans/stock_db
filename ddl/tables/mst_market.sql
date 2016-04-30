-- Table: mst_market

-- DROP TABLE mst_market;

CREATE TABLE mst_market
(
  code integer NOT NULL,
  name character varying(100),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT mst_market_pkey PRIMARY KEY (code )
);
