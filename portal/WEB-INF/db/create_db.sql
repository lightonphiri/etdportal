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
drop table if exists Properties;


--
-- Table structure for table `Creator`
--

CREATE TABLE `Creator` (
  `creator_etd` varchar(255) NOT NULL DEFAULT '',
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`,`creator_etd`)
);

--
-- Table structure for table `Description`
--

CREATE TABLE `Description` (
  `description_etd` blob,
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`)
);

--
-- Table structure for table `Format`
--

CREATE TABLE `Format` (
  `format_etd` varchar(255) NOT NULL DEFAULT '',
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`,`format_etd`)
);

--
-- Table structure for table `Properties`
--

CREATE TABLE `Properties` (
  `name` varchar(255) NOT NULL DEFAULT '',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
);

--
-- Table structure for table `RecordAffiliation`
--

CREATE TABLE `RecordAffiliation` (
  `affiliation_etd` varchar(255) DEFAULT NULL,
  `identifier_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`identifier_etd`)
);

--
-- Table structure for table `RecordDate`
--

DROP TABLE IF EXISTS `RecordDate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RecordDate` (
  `date_etd` date NOT NULL DEFAULT '0000-00-00',
  `identifier_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`identifier_etd`,`date_etd`)
);

--
-- Table structure for table `RecordLastHarvestDate`
--

CREATE TABLE `RecordLastHarvestDate` (
  `harvest_etd` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `identifier_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`identifier_etd`)
);

--
-- Table structure for table `RecordTitle`
--

CREATE TABLE `RecordTitle` (
  `title_etd` varchar(255) DEFAULT NULL,
  `title_etd_browse` varchar(255) DEFAULT NULL,
  `identifier_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`identifier_etd`)
);

--
-- Table structure for table `RecordXML`
--

CREATE TABLE `RecordXML` (
  `description_etd` blob,
  `identifier_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`identifier_etd`)
);

--
-- Table structure for table `Subject`
--

CREATE TABLE `Subject` (
  `subject_etd` varchar(255) NOT NULL DEFAULT '',
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  `title_etd` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`,`title_etd`,`subject_etd`),
  KEY `subject_title_etd` (`title_etd`)
);

--
-- Table structure for table `Title`
--

CREATE TABLE `Title` (
  `title_etd` varchar(255) DEFAULT NULL,
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`)
);

--
-- Table structure for table `Type`
--

CREATE TABLE `Type` (
  `type_etd` varchar(255) NOT NULL DEFAULT '',
  `oai_identifier` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oai_identifier`,`type_etd`)
);
