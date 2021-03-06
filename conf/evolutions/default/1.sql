# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  comment_id                    varchar(255) not null,
  commentator_user_id           bigint,
  commented_picture_picture_id  varchar(255),
  comment_content               varchar(255),
  posting_time                  timestamp,
  constraint pk_comment primary key (comment_id)
);

create table follows (
  follows_id                    varchar(255) not null,
  follower_user_id              bigint,
  followee_user_id              bigint,
  following_note                varchar(255),
  last_profile_view             timestamp,
  constraint pk_follows primary key (follows_id)
);

create table friends (
  friends_id                    varchar(255) not null,
  friend_requester_user_id      bigint,
  friend_receiver_user_id       bigint,
  request_status                integer,
  request_date                  timestamp,
  accepted_date                 timestamp,
  constraint ck_friends_request_status check ( request_status in (0,1,2,3,4)),
  constraint uq_friends_friend_requester_user_id unique (friend_requester_user_id),
  constraint uq_friends_friend_receiver_user_id unique (friend_receiver_user_id),
  constraint pk_friends primary key (friends_id)
);

create table hides (
  hides_id                      varchar(255) not null,
  hider_user_id                 bigint,
  hid_user_id                   bigint,
  constraint pk_hides primary key (hides_id)
);

create table likes (
  likes_id                      varchar(255) not null,
  liker_user_id                 bigint,
  liked_picture_id              varchar(255),
  constraint uq_likes_liker_user_id unique (liker_user_id),
  constraint uq_likes_liked_picture_id unique (liked_picture_id),
  constraint pk_likes primary key (likes_id)
);

create table picture (
  picture_id                    varchar(255) not null,
  upload_time                   timestamp,
  picture_caption               varchar(255),
  picture_owner_user_id         bigint,
  file_extension                varchar(255),
  picture_comments              integer not null,
  picture_likes                 integer not null,
  constraint pk_picture primary key (picture_id)
);

create table tag (
  tag_id                        integer auto_increment not null,
  tag_content                   varchar(255),
  constraint pk_tag primary key (tag_id)
);

create table tags (
  tags_id                       varchar(255) not null,
  tagged_picture_picture_id     varchar(255),
  tag_of_picture_tag_id         integer,
  constraint pk_tags primary key (tags_id)
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
  following_amount              bigint,
  friends_amount                bigint,
  last_upload_date              timestamp,
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
  user_profile_picture_picture_id varchar(255),
  url_facebook                  varchar(255),
  url_linkedin                  varchar(255),
  url_youtube                   varchar(255),
  url_twitch                    varchar(255),
  url_twitter                   varchar(255),
  url_personal                  varchar(255),
  constraint uq_user_profile_user_profile_picture_picture_id unique (user_profile_picture_picture_id),
  constraint pk_user_profile primary key (profile_id)
);

create index ix_comment_commentator_user_id on comment (commentator_user_id);
alter table comment add constraint fk_comment_commentator_user_id foreign key (commentator_user_id) references user (user_id) on delete restrict on update restrict;

create index ix_comment_commented_picture_picture_id on comment (commented_picture_picture_id);
alter table comment add constraint fk_comment_commented_picture_picture_id foreign key (commented_picture_picture_id) references picture (picture_id) on delete restrict on update restrict;

create index ix_follows_follower_user_id on follows (follower_user_id);
alter table follows add constraint fk_follows_follower_user_id foreign key (follower_user_id) references user (user_id) on delete restrict on update restrict;

create index ix_follows_followee_user_id on follows (followee_user_id);
alter table follows add constraint fk_follows_followee_user_id foreign key (followee_user_id) references user (user_id) on delete restrict on update restrict;

alter table friends add constraint fk_friends_friend_requester_user_id foreign key (friend_requester_user_id) references user (user_id) on delete restrict on update restrict;

alter table friends add constraint fk_friends_friend_receiver_user_id foreign key (friend_receiver_user_id) references user (user_id) on delete restrict on update restrict;

create index ix_hides_hider_user_id on hides (hider_user_id);
alter table hides add constraint fk_hides_hider_user_id foreign key (hider_user_id) references user (user_id) on delete restrict on update restrict;

create index ix_hides_hid_user_id on hides (hid_user_id);
alter table hides add constraint fk_hides_hid_user_id foreign key (hid_user_id) references user (user_id) on delete restrict on update restrict;

alter table likes add constraint fk_likes_liker_user_id foreign key (liker_user_id) references user (user_id) on delete restrict on update restrict;

alter table likes add constraint fk_likes_liked_picture_id foreign key (liked_picture_id) references picture (picture_id) on delete restrict on update restrict;

create index ix_picture_picture_owner_user_id on picture (picture_owner_user_id);
alter table picture add constraint fk_picture_picture_owner_user_id foreign key (picture_owner_user_id) references user (user_id) on delete restrict on update restrict;

create index ix_tags_tagged_picture_picture_id on tags (tagged_picture_picture_id);
alter table tags add constraint fk_tags_tagged_picture_picture_id foreign key (tagged_picture_picture_id) references picture (picture_id) on delete restrict on update restrict;

create index ix_tags_tag_of_picture_tag_id on tags (tag_of_picture_tag_id);
alter table tags add constraint fk_tags_tag_of_picture_tag_id foreign key (tag_of_picture_tag_id) references tag (tag_id) on delete restrict on update restrict;

alter table user_profile add constraint fk_user_profile_user_profile_picture_picture_id foreign key (user_profile_picture_picture_id) references picture (picture_id) on delete restrict on update restrict;


# --- !Downs

alter table comment drop constraint if exists fk_comment_commentator_user_id;
drop index if exists ix_comment_commentator_user_id;

alter table comment drop constraint if exists fk_comment_commented_picture_picture_id;
drop index if exists ix_comment_commented_picture_picture_id;

alter table follows drop constraint if exists fk_follows_follower_user_id;
drop index if exists ix_follows_follower_user_id;

alter table follows drop constraint if exists fk_follows_followee_user_id;
drop index if exists ix_follows_followee_user_id;

alter table friends drop constraint if exists fk_friends_friend_requester_user_id;

alter table friends drop constraint if exists fk_friends_friend_receiver_user_id;

alter table hides drop constraint if exists fk_hides_hider_user_id;
drop index if exists ix_hides_hider_user_id;

alter table hides drop constraint if exists fk_hides_hid_user_id;
drop index if exists ix_hides_hid_user_id;

alter table likes drop constraint if exists fk_likes_liker_user_id;

alter table likes drop constraint if exists fk_likes_liked_picture_id;

alter table picture drop constraint if exists fk_picture_picture_owner_user_id;
drop index if exists ix_picture_picture_owner_user_id;

alter table tags drop constraint if exists fk_tags_tagged_picture_picture_id;
drop index if exists ix_tags_tagged_picture_picture_id;

alter table tags drop constraint if exists fk_tags_tag_of_picture_tag_id;
drop index if exists ix_tags_tag_of_picture_tag_id;

alter table user_profile drop constraint if exists fk_user_profile_user_profile_picture_picture_id;

drop table if exists comment;

drop table if exists follows;

drop table if exists friends;

drop table if exists hides;

drop table if exists likes;

drop table if exists picture;

drop table if exists tag;

drop table if exists tags;

drop table if exists user;

drop table if exists user_profile;

