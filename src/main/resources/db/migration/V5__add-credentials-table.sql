create table payment.credentials (
  id varchar(255),
  recipient varchar(255),
  gateway_id varchar(255),
  token varchar(255),
  gateway varchar(255)
);

insert into payment.credentials (id, recipient, gateway_id, token, gateway) values ('0', 'testuser', '237164', 'test_F3RDFIcOsobb3qtsjEzK5tPuHR6b7P0Qigmwf0A_Lv4', 'yookassa');
