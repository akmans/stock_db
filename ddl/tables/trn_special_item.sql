-- Table: trn_special_item

-- DROP TABLE trn_special_item;

CREATE TABLE trn_special_item
(
  code SERIAL,
  name character varying(100),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT trn_special_item_pkey PRIMARY KEY (code )
);
