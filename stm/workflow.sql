-----------------------flow---------------------------------

create table eb_flow_description (id bigint primary key,flow_name varchar(100),initial_state  varchar(100),entry_action varchar(100),exit_action varchar(100),retrieval_strategy varchar(100),skip_entry_exit_for_auto_states boolean,is_default boolean,security_strategy varchar(100));

CREATE SEQUENCE eb_flow_description_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE eb_flow_description_id_seq OWNED BY eb_flow_description.id;


insert into eb_flow_description values (1,'product-flow','new','productEntryAction','productExitAction','productRetrievalStrategy',true,true,null);


---------------------------States---------------------------------
create table eb_state_description (id bigint primary key,state_name varchar(100),initial_state  boolean,final_state boolean,entry_action varchar(100),exit_action varchar(100),state_type varchar(100),flow_id bigint,component_name varchar(100),meta_alt_name varchar(100));


CREATE SEQUENCE eb_state_description_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE eb_state_description_id_seq OWNED BY eb_state_description.id;


insert into eb_state_description values (1,'new',true,false,'newProductEntryAction',null,'manual-state',1,null,'New');

insert into eb_state_description values (2,'qualified',false,false,'qualifiedProductEntryAction',null,'manual-state',1,null,'Qualified');

insert into eb_state_description values (3,'ready_to_publish',false,false,'readyToPublishProductEntryAction',null,'manual-state',1,null,'Ready To Publish');

insert into eb_state_description values (4,'published',false,true,'publishProductDataEntryAction',null,'manual-state',1,null,'Published');

insert into eb_state_description values (5,'qualificationCheck',false,false,null,null,'script',1,'productQualification','Qualification Check');

insert into eb_state_description values (6,'autoApprovalCheck',false,false,null,null,'script',1,'productApproval','Auto Approval Check');


-----------------------transition---------------------------------
create table eb_transition_description (id bigint primary key,event_name varchar(100),new_state_id varchar(100),new_flow_id varchar(100), state_id bigint,transition_action varchar(100),retrieval_transition boolean,meta_alt_name varchar(100),meta_is_combinable varchar(100),meta_is_visible varchar(100),acls varchar(250),is_invokable_from_stm boolean);

CREATE SEQUENCE eb_transition_description_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE eb_transition_description_id_seq OWNED BY eb_transition_description.id;


insert into eb_transition_description values (1,'createProduct','qualificationCheck',null,1,'defaultVariantCreation',true,'Create Product','false','true',null,null);

insert into eb_transition_description values (2,'addProductToCategory','qualificationCheck',null,1,'productCategoryMappingExecution',true,'Add Product To Category','true','true',null,null);

insert into eb_transition_description values (3,'updateProduct','qualificationCheck',null,1,'defaultVariantUpdation',true,'Update Product','false','true',null,null);

insert into eb_transition_description values (4,'checkProductQualificationOnVariantUpdate','qualificationCheck',null,1,null,true,'Create Product','false','false',null,null);


insert into eb_transition_description values (5,'approveProduct','autoApprovalCheck',null,2,null,false,'Approve Product','true','true',null,null);

insert into eb_transition_description values (6,'addProductToCategory','qualificationCheck',null,2,'productCategoryMappingExecution',true,'Add Product To Category','true','true',null,null);

insert into eb_transition_description values (7,'updateProduct','qualificationCheck',null,2,'defaultVariantUpdation',true,'Update Product','false','true',null,null);

insert into eb_transition_description values (8,'checkProductQualificationOnVariantUpdate','qualificationCheck',null,2,null,false,'Create Product','false','false',null,null);


insert into eb_transition_description values (9,'publishProduct','published',null,3,null,false,'Approve Product','true','true',null,null);

insert into eb_transition_description values (10,'addProductToCategory','qualificationCheck',null,4,'productCategoryMappingExecution',true,'Add Product To Category','true','true',null,null);

insert into eb_transition_description values (11,'updateProduct','qualificationCheck',null,4,'defaultVariantUpdation',true,'Update Product','false','true',null,null);

insert into eb_transition_description values (12,'checkProductQualificationOnVariantUpdate','qualificationCheck',null,4,null,true,'Create Product','false','false',null,null);


insert into eb_transition_description values (13,'qualifyProduct','qualified',null,5,null,false,'Qualify Product','false','false',null,null);

insert into eb_transition_description values (14,'disQualifyProduct','new',null,5,null,false,'Qualify Product','false','false',null,null);

insert into eb_transition_description values (15,'readyProductToPublish','ready_to_publish',null,6,null,false,'Qualify Product','false','false',null,null);

insert into eb_transition_description values (16,'publishProduct','published',null,6,null,false,'Qualify Product','false','false',null,null);



-------------------------state attributes-----------------------------
create table eb_state_attributes (id bigint primary key,state_id bigint,key varchar(100),value varchar(100));

CREATE SEQUENCE eb_state_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE eb_state_attributes_id_seq OWNED BY eb_state_attributes.id;
