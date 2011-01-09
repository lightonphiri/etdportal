drop table if exists Title;
drop table if exists Creator;
drop table if exists Subject;
drop table if exists Description;
drop table if exists RecordDate;
drop table if exists RecordXML;
drop table if exists RecordTitle;
drop table if exists RecordLastHarvestDate;
drop table if exists RecordAffiliation;
drop table if exists Type;
drop table if exists Format;

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

create table RecordTitle
(title_etd varchar(255),
 title_etd_browse varchar(255),
 identifier_etd varchar(255),
 primary key (identifier_etd)
 	
);
 
create table RecordDate
(date_etd date,
 identifier_etd varchar(255),
 constraint date_identifier_etd foreign key (identifier_etd) references RecordTitle(identifier_etd),
 primary key (identifier_etd,date_etd)
 );

create table RecordXML
(description_etd BLOB,
 identifier_etd varchar(255),
 constraint recordXML_identifier_etd foreign key (identifier_etd) references RecordTitle(identifier_etd),
 primary key (identifier_etd)
 );

create table RecordLastHarvestDate
(harvest_etd timestamp,
 identifier_etd varchar(255),
 constraint recordLastHarvestDate_identifier_etd foreign key (identifier_etd) references RecordTitle(identifier_etd),
 primary key (identifier_etd)
	
);

create table RecordAffiliation
( affiliation_etd varchar(255),
 identifier_etd varchar(255),
 constraint recordAffiliation_identifier_etd foreign key (identifier_etd) references RecordTitle(identifier_etd),
 primary key (identifier_etd)
	
);



