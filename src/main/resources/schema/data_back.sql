insert into employee values ('123','123','name1',false,'2023-01-01');
insert into employee values ('456','456','name2',false,'2023-01-02');

insert into vacation_category values("a",2);
insert into vacation_request(vacation_category_key, employee_id, result, vacation_quantity, vacation_start_time, vacation_end_time, vacation_related_start_time, vacation_related_end_time, vacation_request_time, reason) values("a","123","a",1,"2023-01-01","2023-01-10","09:00:00","09:00:00","2023-01-01 18:00:00","a");