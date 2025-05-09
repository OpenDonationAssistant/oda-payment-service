alter table credentials add enabled boolean;
update credentials set enabled = TRUE;

alter table credentials add secret varchar(255);
update credentials set secret = '';

alter table credentials add gateway_type varchar(255);
update credentials set gateway_type = 'fiat';

alter table payment add cred_id varchar(255);
update payment set cred_id = '';

alter table payment add actions jsonb;
alter table payment add auction jsonb;
