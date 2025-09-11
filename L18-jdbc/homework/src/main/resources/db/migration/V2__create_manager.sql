create table if not exists manager (
    no     bigserial primary key,
    label  varchar(50) not null,
    param1 varchar(100)
    );