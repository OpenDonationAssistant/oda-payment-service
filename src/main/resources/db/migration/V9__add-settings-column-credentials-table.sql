alter table credentials add settings jsonb not null default '{}'::jsonb;
