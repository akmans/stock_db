-- Table: trn_japan_stock_log

-- DROP TABLE trn_japan_stock_log;

CREATE TABLE trn_japan_stock_log
(
  job_id character varying(50) NOT NULL,
  process_date date NOT NULL,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT japan_stock_log_pkey PRIMARY KEY (job_id, process_date)
);
