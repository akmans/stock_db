-- Table: mst_scale

-- DROP TABLE mst_scale;

CREATE TABLE mst_scale
(
  code integer NOT NULL,
  name character varying(200),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT scale_pkey PRIMARY KEY (code )
);
