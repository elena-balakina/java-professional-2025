create sequence address_SEQ start with 1 increment by 1;
create sequence phone_SEQ start with 1 increment by 1;

create table address
(
    id     bigint not null primary key,
    street varchar(255) not null
);

alter table client add column address_id bigint;
alter table client
    add constraint fk_client_address
        foreign key (address_id) references address(id);

create table phone
(
    id        bigint not null primary key,
    number    varchar(50) not null,
    client_id bigint not null
);

alter table phone
    add constraint fk_phone_client
        foreign key (client_id) references client(id);
