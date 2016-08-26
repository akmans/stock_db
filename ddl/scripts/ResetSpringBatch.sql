
delete from trn_japan_stock_weekly where regist_date = '2016-07-10';
delete from trn_japan_stock where regist_date = '2016-07-15';

delete from trn_japan_stock_log where job_id = 'generateJapanStockWeeklyJob' and process_date > '2016-06-30';

select count(*) from trn_japan_stock_weekly;

--1 delete batch_step_execution_context
delete from batch_step_execution_context
where step_execution_id in 
(select step_execution_id from batch_step_execution
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 6626));

--2 delete batch_step_execution
delete from batch_step_execution
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 6626);

--3 delete batch_job_execution_params
delete from batch_job_execution_params
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 6626);

--4 delete batch_job_execution_context
delete from batch_job_execution_context
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 6626);

--5 delete batch_job_execution
delete from batch_job_execution
where job_instance_id >= 6626;

--6 delete batch_job_instance
delete from batch_job_instance
where job_instance_id >= 6626;

-- Patch October Data
select count(*)
from trn_stock_data
where extract(year from regist_date) = 2006;

select count(*)
from trn_japan_stock
where extract(year from regist_date) = 2006
and extract(month from regist_date) = 10;

delete
from trn_japan_stock
where extract(year from regist_date) = 2006
and extract(month from regist_date) = 10;

insert into trn_japan_stock
(code,
regist_date,
opening_price,
high_price,
low_price,
finish_price,
turnover,
trading_value,
created_date,
updated_date,
created_by,
updated_by)
select code,
regist_date,
opening_price,
high_price,
low_price,
finish_price,
turnover,
null,
now(),
now(),
'patch',
'patch'
from trn_stock_data
where extract(year from regist_date) = 2006;