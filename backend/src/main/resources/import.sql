INSERT INTO tab_user(user_name, user_password) VALUES ('jw', 'password');
INSERT INTO tab_user(user_name, user_password) VALUES ('foo', 'password');
INSERT INTO tab_user_role_relationship(user_id, user_role) VALUES ('1', 'USER');
INSERT INTO tab_user_role_relationship(user_id, user_role) VALUES ('2', 'USER');
INSERT INTO tab_tweet(tweet_message, tweet_post_time, tweet_author) VALUES ('Today is a good day!', '01.01.2019 12:12:12', '1');