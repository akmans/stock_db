-- Table: mst_sector17

-- DROP TABLE mst_sector17;

CREATE TABLE mst_sector17
(
  code integer NOT NULL,
  name character varying(200),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
 CONSTRAINT sector17_pkey PRIMARY KEY (code )
);
