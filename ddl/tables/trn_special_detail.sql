-- Table: trn_special_detail

-- DROP TABLE trn_special_detail;

CREATE TABLE trn_special_detail
(
  code serial NOT NULL,
  name character varying(100) NOT NULL,
  detail character varying(300),
  amount bigint NOT NULL,
  item_code integer NOT NULL,
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT trn_special_detail_pkey PRIMARY KEY (code ),
  CONSTRAINT special_item_fkey FOREIGN KEY (item_code)
      REFERENCES trn_special_item (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);