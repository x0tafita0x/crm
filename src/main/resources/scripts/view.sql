create or replace view v_employe as
    select u.*
    from user_roles ur
    join users u on u.id=ur.user_id
    join roles r on r.id=ur.role_id
    where r.name='ROLE_EMPLOYEE';