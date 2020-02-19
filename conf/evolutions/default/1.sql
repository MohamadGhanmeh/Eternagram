# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table follows (
  follows_id                    varchar(255) not null,
  note                          varchar(255),
  constraint pk_follows primary key (follows_id)
);

create table picture (
  picture_id                    varchar(255) not null,
  location                      varchar(255),
  picture_owner_user_id         bigint,
  constraint pk_picture primary key (picture_id)
);

create table user (
  user_id                       bigint auto_increment not null,
  user_name                     varchar(255),
  user_password                 varchar(255),
  user_email                    varchar(255),
  phone_number                  varchar(255),
  user_dob                      timestamp,
  picture_amount                bigint,
  follower_amount               bigint,
  constraint uq_user_user_name unique (user_name),
  constraint uq_user_user_email unique (user_email),
  constraint pk_user primary key (user_id)
);

create table user_profile (
  profile_id                    bigint auto_increment not null,
  first_name                    varchar(255),
  last_name                     varchar(255),
  user_gender                   integer not null,
  user_bio                      varchar(255),
  user_profile_picture          varchar(255),
  url_facebook                  varchar(255),
  url_linkedin                  varchar(255),
  url_youtube                   varchar(255),
  url_twitch                    varchar(255),
  url_twitter                   varchar(255),
  url_personal                  varchar(255),
  constraint pk_user_profile primary key (profile_id)
);

create index ix_picture_picture_owner_user_id on picture (picture_owner_user_id);
alter table picture add constraint fk_picture_picture_owner_user_id foreign key (picture_owner_user_id) references user (user_id) on delete restrict on update restrict;


# --- !Downs

alter table picture drop constraint if exists fk_picture_picture_owner_user_id;
drop index if exists ix_picture_picture_owner_user_id;

drop table if exists follows;

drop table if exists picture;

drop table if exists user;

drop table if exists user_profile;

