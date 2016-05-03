-- Table: mst_sector33

-- DROP TABLE mst_sector33;

CREATE TABLE mst_sector33
(
  code integer NOT NULL,
  name character varying(200),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT sector33_pkey PRIMARY KEY (code )
);
