create table payment.payment (
  id varchar(255),
  gateway_id varchar(255),
  nickname varchar(255),
  message text,
  recipient_id varchar(255),
  amount varchar(255),
  confirmation varchar(255),
  attachments text,
  authorization_timestamp timestamp with time zone,
  status varchar(255)
);
