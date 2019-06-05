INSERT INTO tab_user(user_id, user_name, user_password) VALUES ('1', 'jw', 'password');
INSERT INTO tab_user(user_id, user_name, user_password) VALUES ('2', 'foo', 'password');
INSERT INTO tab_user_role_relationship(user_role_relationship_id, user_id, user_role) VALUES ('1', '1', 'USER');
INSERT INTO tab_user_role_relationship(user_role_relationship_id, user_id, user_role) VALUES ('2', '2', 'USER');
INSERT INTO tab_tweet(tweet_id, tweet_message, tweet_post_time, tweet_author) VALUES ('1', 'Today is a good day!', '01.01.2019 12:12:12', '1');