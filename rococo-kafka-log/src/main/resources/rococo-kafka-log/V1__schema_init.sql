create table if not exists `logs`
(
    id              binary(16)   unique not null default (UUID_TO_BIN(UUID(), true)),
    user_id         binary(16)   not null,
    event           varchar(50),
    entity_type     varchar(255),
    date_of_event   date  ,
    date_log_added  date,
    primary key (id)
);
