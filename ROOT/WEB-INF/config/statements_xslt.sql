create table RecordTitle
(title_etd varchar(255),
 identifier_etd varchar(255),
 primary key (identifier_etd)
 	
);
 
create table RecordDate
(date_etd date,
 identifier_etd varchar(255),
 constraint date_identifier_etd foreign key (identifier_etd) references Title(identifier_etd),
 primary key (identifier_etd,date_etd)
 );

create table RecordXML
(description_etd BLOB,
 identifier_etd varchar(255),
 constraint recordXML_identifier_etd foreign key (identifier_etd) references Title(identifier_etd),
 primary key (identifier_etd)
 );
