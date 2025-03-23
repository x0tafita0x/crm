create table budget (
    budget_id int primary key auto_increment,
    description varchar(255),
    amount decimal(15,2),
    customer_id int unsigned not null,
    created_at date not null,
    foreign key (customer_id) references customer(customer_id)
);

create table expense (
    expense_id int auto_increment,
    customer_id int unsigned not null,
    budget_id int,
    description varchar(255),
    amount decimal(15,2),
    created_at date,
    primary key (expense_id),
    foreign key (customer_id) references customer(customer_id),
    foreign key (budget_id) references budget(budget_id)
);

create table alert_settings (
    alert_id int primary key auto_increment,
    rate decimal(5,2)
);