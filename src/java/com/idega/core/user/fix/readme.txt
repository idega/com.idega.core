


alter table ic_user add user_representative integer
alter table ic_user add primary_group integer

ALTER TABLE "IC_USER" ADD FOREIGN KEY ("USER_REPRESENTATIVE") REFERENCES IC_GROUP ("IC_GROUP_ID");
ALTER TABLE "IC_USER" ADD FOREIGN KEY ("PRIMARY_GROUP") REFERENCES IC_GROUP ("IC_GROUP_ID");
