insert into user_roles(role_name)

values  ('ROLE_ADMIN'),
       ( 'ROLE_USER');

insert into availability(id, type, from_unavailable, to_unavailable)
values (1, 'AVAILABLE', '23:00:00', '23:00:00'),
       (2, 'AVAILABLE', '23:00:00', '23:00:00'),
       (3, 'UNAVAILABLE', '23:00:00', '23:00:00'),
       (4, 'TEMPORARY_UNAVAILABLE', '14:00:00', '17:00:00');

insert into accounts(id, name, email, password, avatar_url, availability_id)
values (1, 'Name1', 'email1@email.com', 'password1', 'url1', 1),
       (2, 'Name2', 'email2@email.com', 'password2', 'url2', 2),
       (3, 'Name3', 'email3@email.com', 'password3', 'url3', 3),
       (4, 'Name4', 'email4@email.com', 'password4', 'url4', 4);

insert into accounts_roles(users_id, roles_id)
values (1, 1),
       (1, 2),
       (2, 2),
       (3, 2),
       (4, 2)
