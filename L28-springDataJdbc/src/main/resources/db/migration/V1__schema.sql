create sequence if not exists client_SEQ start with 1 increment by 1;
create sequence if not exists address_SEQ start with 1 increment by 1;
create sequence if not exists phone_SEQ start with 1 increment by 1;

create table if not exists address
(
    id      bigint primary key default nextval('address_SEQ'),
    street  varchar(255)
);

create table if not exists client
(
    id         bigint primary key default nextval('client_SEQ'),
    name       varchar(50) not null,
    address_id bigint references address(id) on delete set null
);

create table if not exists phone
(
    id        bigint primary key default nextval('phone_SEQ'),
    number    varchar(50),
    client_id bigint not null references client(id) on delete cascade
);
