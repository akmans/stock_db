-- Table: mst_instrument

-- DROP TABLE mst_instrument;

CREATE TABLE mst_instrument
(
  code integer NOT NULL,
  name character varying(200),
  sector33_code integer,
  sector17_code integer,
  scale_code integer,
  market_code integer,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT instrument_pkey PRIMARY KEY (code ),
  CONSTRAINT market_fkey FOREIGN KEY (market_code)
      REFERENCES mst_market (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT scale_fkey FOREIGN KEY (scale_code)
      REFERENCES mst_scale (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sector17_fkey FOREIGN KEY (sector17_code)
      REFERENCES mst_sector17 (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sector33_fkey FOREIGN KEY (sector33_code)
      REFERENCES mst_sector33 (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
