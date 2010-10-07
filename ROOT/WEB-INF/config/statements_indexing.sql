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

