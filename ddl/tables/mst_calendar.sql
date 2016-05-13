-- Table: mst_calendar

-- DROP TABLE mst_calendar;

CREATE TABLE mst_calendar
(
  code serial NOT NULL,
  calendar character varying(2) NOT NULL,
  holiday integer NOT NULL,
  regist_at date NOT NULL,
  description character varying(200),
  created_date timestamp with time zone,
  updated_date timestamp with time zone,
  created_by character varying(20),
  updated_by character varying(20),
  CONSTRAINT mst_calendar_pkey PRIMARY KEY (code )
);
