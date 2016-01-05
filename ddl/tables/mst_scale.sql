-- Table: mst_scale

-- DROP TABLE mst_scale;

CREATE TABLE mst_scale
(
  code integer NOT NULL,
  name character varying(200),
  CONSTRAINT scale_pkey PRIMARY KEY (code )
);
