create table if not exists `logs`
(
    id              binary(16)   unique not null default (UUID_TO_BIN(UUID(), true)),
    event           varchar(50) not null,
    entity_id       binary(16)  not null,
    entity_type     varchar(255) not null,
    description     VARCHAR(255),
    event_date   datetime(6) not null,
    event_created_date  datetime(6) not null default current_timestamp(6),
    primary key (id)
);
