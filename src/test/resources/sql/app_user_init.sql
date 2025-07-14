
insert into availability(id, type, from_unavailable, to_unavailable)
values (3, 'UNAVAILABLE', '23:00:00', '23:00:00'),
       (4, 'TEMPORARILY_UNAVAILABLE', '14:00:00', '17:00:00');

insert into accounts(id, name, email, password, avatar_url, availability_id)
values (3, 'Name3', 'email3@email.com', 'password3', 'url3', 3),
       (4, 'Name4', 'email4@email.com', 'password4', 'url4', 4);

insert into accounts_roles(users_id, roles_id)
values (3, 2),
       (4, 2)
