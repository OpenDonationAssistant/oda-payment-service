alter table credentials add enabled boolean;
update credentials set enabled = TRUE;

alter table credentials add secret varchar(255);
update credentials set secret = '';
