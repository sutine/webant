select count(1) from link;
select count(1) from link where status = 'init';
select count(1) from link where status = 'pending';
select count(1) from link where status = 'success';
select count(1) from link where status = 'fail';

update link set status = 'init' where status = 'pending'