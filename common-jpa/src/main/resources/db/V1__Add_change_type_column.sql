Hibernate: create table account (id bigint not null, balance decimal(38,2), change_type enum ('Binance','Bybit','Mexc','Test'), name_account varchar(255), public_api_key varchar(255), secret_api_key varchar(255), node_user_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table account_seq (next_val bigint) engine=InnoDB
Hibernate: insert into account_seq values ( 1 )
Hibernate: create table config_trade_seq (next_val bigint) engine=InnoDB
Hibernate: insert into config_trade_seq values ( 1 )
Hibernate: create table config_trade (id bigint not null, amount_order decimal(38,2), depth_glass integer not null, name_pair varchar(255), real_trade bit not null, strategy enum ('PredictionsOfEvents','SlidingProtectiveOrder'), node_user_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table node_change_seq (next_val bigint) engine=InnoDB
Hibernate: insert into node_change_seq values ( 1 )
Hibernate: create table node_order_seq (next_val bigint) engine=InnoDB
Hibernate: insert into node_order_seq values ( 1 )
Hibernate: create table node_user (id bigint not null auto_increment, change_type enum ('Binance','Bybit','Mexc','Test'), chat_id bigint, email varchar(255), first_login_date datetime(6), first_name varchar(255), is_active bit, last_name varchar(255), state enum ('ACCOUNT_ADD_NAME','ACCOUNT_ADD_PUBLIC_API','ACCOUNT_ADD_REGISTER','ACCOUNT_ADD_SECRET_API','ACCOUNT_DELETE','ACCOUNT_INFO','ACCOUNT_LIST','ACCOUNT_SELECT','BASIC_STATE','BOT_BACK','BOT_CANCEL','BOT_CHANGE','BOT_HELP','BOT_MAIN_MENU','BOT_START','INFO_SETTINGS','MAIL_EMAIL','MAIL_WAIT_FOR_EMAIL_STATE','SETTINGS_AMOUNT_ORDER','SETTINGS_DEPTH_GLASS','SETTINGS_NAME_PAIR','SETTINGS_NAME_STRATEGY','SETTINGS_SAVE_AMOUNT_ORDER','SETTINGS_SAVE_DEPTH_GLASS','SETTINGS_SAVE_NAME_PAIR','SETTINGS_SAVE_NAME_STRATEGY','TRADE_MANAGER','TRADE_START','TRADE_STOP'), state_trade enum ('BASIC_STATE','BAY','SEL','START','STOP'), telegram_user_id bigint, trade_start_or_stop bit not null, username varchar(255), account_id bigint, config_trade_id bigint, statistics_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table node_change (id bigint not null, change_type enum ('Binance','Bybit','Mexc','Test') not null, primary key (id)) engine=InnoDB
Hibernate: create table node_order (id bigint not null, average_price decimal(38,2), check_real bit not null, cumulative_amount decimal(38,2), instrument varchar(255), limit_price decimal(38,2), order_id varchar(255), original_amount decimal(38,2), strategy_enams enum ('PredictionsOfEvents','SlidingProtectiveOrder'), timestamp datetime(6), type varchar(255), usd decimal(38,2), user_reference varchar(255), node_user_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table pair (id bigint not null, pair varchar(255), node_change_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table pair_seq (next_val bigint) engine=InnoDB
Hibernate: insert into pair_seq values ( 1 )
Hibernate: create table statistics (id bigint not null, count_deal integer not null, count_deal_bay integer not null, count_deal_sel integer not null, profit decimal(38,2), node_user_id bigint, primary key (id)) engine=InnoDB
Hibernate: create table statistics_seq (next_val bigint) engine=InnoDB
Hibernate: insert into statistics_seq values ( 1 )
Hibernate: alter table config_trade drop index UKm22bpm4dhpeptmbvhcr7n5k24
Hibernate: alter table config_trade add constraint UKm22bpm4dhpeptmbvhcr7n5k24 unique (node_user_id)
    Hibernate: alter table node_user drop index UKo3p0ak48plitvrbjqw6uj8vg3
Hibernate: alter table node_user add constraint UKo3p0ak48plitvrbjqw6uj8vg3 unique (account_id)
    Hibernate: alter table node_user drop index UKr2x9mvf572movo2cvyu0xt1pb
Hibernate: alter table node_user add constraint UKr2x9mvf572movo2cvyu0xt1pb unique (config_trade_id)
    Hibernate: alter table node_user drop index UKgqtkj7rn7fck0ba4ksje9lcai
Hibernate: alter table node_user add constraint UKgqtkj7rn7fck0ba4ksje9lcai unique (statistics_id)
    Hibernate: alter table statistics drop index UKr3u38vcdbf349d1feycycmqsi
Hibernate: alter table statistics add constraint UKr3u38vcdbf349d1feycycmqsi unique (node_user_id)
    Hibernate: alter table account add constraint FKg657ksq8dxr1lg1v340opid8s foreign key (node_user_id) references node_user (id)
    Hibernate: alter table config_trade add constraint FK5qshs043ak0hvcki2nuun1q0p foreign key (node_user_id) references node_user (id)
    Hibernate: alter table node_user add constraint FK4uamdk2rvg3ftbcg2lrnmfnq0 foreign key (account_id) references account (id)
    Hibernate: alter table node_user add constraint FKgym9eqh2mera7ulmfhh2ik5ar foreign key (config_trade_id) references config_trade (id)
    Hibernate: alter table node_user add constraint FKdlu5k9esj33gfxjh6sovfdnaq foreign key (statistics_id) references statistics (id)
    Hibernate: alter table node_order add constraint FKk88u96crus28yfetwxvemel3u foreign key (node_user_id) references node_user (id)
    Hibernate: alter table pair add constraint FKfumwijplil0khb06s87cdfyos foreign key (node_change_id) references node_change (id)
    Hibernate: alter table statistics add constraint FK8b7aybtl6ccmp093bcriv2x8r foreign key (node_user_id) references node_user (id)