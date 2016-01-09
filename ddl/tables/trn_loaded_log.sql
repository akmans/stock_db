-- Table: trn_loaded_log

-- DROP TABLE trn_loaded_log;

CREATE TABLE trn_loaded_log
(
  code integer NOT NULL,
  CONSTRAINT loaded_pkey PRIMARY KEY (code ),
  CONSTRAINT instrument_fkey FOREIGN KEY (code)
      REFERENCES mst_instrument (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
