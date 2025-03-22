create table budget_type (
    budget_type_id int primary key auto_increment,
    name varchar(255) not null
);

create table budget (
    budget_id int primary key auto_increment,
    description varchar(255),
    budget_type_id int,
    amount decimal(15,2),
    customer_id int unsigned not null,
    foreign key (customer_id) references customer(customer_id),
    foreign key (budget_type_id) references budget_type(budget_type_id)
);

create table expense (
    expense_id int auto_increment,
    customer_id int unsigned not null,
    budget_id int references budget(budget_id),
    amount decimal(15,2),
    primary key (expense_id),
    foreign key (customer_id) references customer(customer_id)
);

create table alert_settings (
    alert_id int primary key auto_increment,
    rate decimal(5,2)
);

alter table trigger_lead add column expense_id int not null;
ALTER TABLE `trigger_lead`
ADD CONSTRAINT `fk_expense`
FOREIGN KEY (`expense_id`) REFERENCES `expense`(`expense_id`);

alter table trigger_ticket add column expense_id int not null;
ALTER TABLE `trigger_ticket`
ADD CONSTRAINT `fk_expense`
FOREIGN KEY (`expense_id`) REFERENCES `expense`(`expense_id`);

select * from budget;

insert into expense (customer_id, budget_id, amount)
values (44,3,7500);