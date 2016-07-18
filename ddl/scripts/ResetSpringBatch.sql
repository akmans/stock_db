
delete from trn_japan_stock_weekly where regist_date = '2016-07-10';
delete from trn_japan_stock where regist_date = '2016-07-15';

delete from trn_japan_stock_log where job_id = 'generateJapanStockWeeklyJob';

select count(*) from trn_japan_stock_weekly;

--1 delete batch_step_execution_context
delete from batch_step_execution_context
where step_execution_id in 
(select step_execution_id from batch_step_execution
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 3009));

--2 delete batch_step_execution
delete from batch_step_execution
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 3009);

--3 delete batch_job_execution_params
delete from batch_job_execution_params
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 3009);

--4 delete batch_job_execution_context
delete from batch_job_execution_context
 where job_execution_id in
 (select job_execution_id from batch_job_execution
  where job_instance_id >= 3009);

--5 delete batch_job_execution
delete from batch_job_execution
where job_instance_id >= 3009;

--6 delete batch_job_instance
delete from batch_job_instance
where job_instance_id >= 3009;
