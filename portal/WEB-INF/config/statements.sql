create table	Title
(title_etd varchar(255),
 oai_identifier varchar(255),
 primary key (oai_identifier)
 	
);

create table Creator
(creator_etd varchar(255),
 oai_identifier varchar(255),
 constraint author_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier,creator_etd)
 );

create table Subject
(subject_etd varchar(255),
 oai_identifier varchar(255),
 title_etd varchar(255),
 constraint subject_title_etd foreign key (title_etd) references Title(title_etd),
 constraint subject_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier,title_etd,subject_etd)
 );
 
create table Description
(description_etd BLOB,
 oai_identifier varchar(255),
 constraint description_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier)
 );
  
 
create table RecordDate
(date_etd date,
 oai_identifier varchar(255),
 constraint publisher_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier,date_etd)
 );

create table Type
(type_etd varchar(255),
 oai_identifier varchar(255),
 constraint type_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier,type_etd)
);

create table Format
(format_etd varchar(255),
 oai_identifier varchar(255),
 constraint format_oai_identifier foreign key (oai_identifier) references Title(oai_identifier),
 primary key (oai_identifier,format_etd)
);