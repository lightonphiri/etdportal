SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
SET SQL_MODE='ALLOW_INVALID_DATES';

DROP TABLE IF EXISTS Archive;
DROP TABLE IF EXISTS Counter;
DROP TABLE IF EXISTS Repositories;
DROP TABLE IF EXISTS CountCache;

--
-- Table structure for table `Archive`
--

CREATE TABLE `Archive` (
  `ID` varchar(200) NOT NULL DEFAULT '', 
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MetaType` varchar(16) NOT NULL DEFAULT '', 
  `Source` varchar(2048) DEFAULT NULL,
  `MetaData` mediumblob,
  `Deleted` tinyint(1) NOT NULL DEFAULT '0',
  `About` blob NOT NULL,
  `SetSpec` varchar(64) NOT NULL DEFAULT 'None',
  PRIMARY KEY (`ID`,`MetaType`),
  KEY `IDX_METATYPE` (`MetaType`),
  KEY `SourceID` (`Source`(128),`ID`(128)),
  KEY `SourceDeletedID` (`Source`(128),`Deleted`,`ID`(128)),
  KEY `Source_idx` (`Source`(333)),
  KEY `MetaTypeDate` (`MetaType`,`Date`),
  KEY `DateMetaType` (`Date`,`MetaType`),
  KEY `MetaTypeDateID` (`MetaType`,`Date`,`ID`),
  KEY `MetaTypesetSpecDateID` (`MetaType`,`SetSpec`,`Date`,`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;   

--
-- Table structure for table `Counter`
--

CREATE TABLE `Counter` (
  `setSpec` varchar(64) NOT NULL,
  `count` int(11) DEFAULT NULL,
  PRIMARY KEY (`setSpec`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `Repositories`
--

CREATE TABLE `Repositories` (
  `ID` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `isRunning` tinyint(1) NOT NULL DEFAULT '0',
  `harvestStatus` varchar(256) NOT NULL DEFAULT '',
  `baseURL` varchar(2048) NOT NULL,
  `metadataFormat` varchar(1024) NOT NULL DEFAULT 'oai_dc',
  `setSpec` varchar(16384) NOT NULL DEFAULT '',
  `dateFrom` varchar(64) NOT NULL DEFAULT '',
  `harvestInterval` int(11) NOT NULL DEFAULT '3000',
  `timeout` int(11) DEFAULT '180000',
  `resumptionToken` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `CountCache`
--

CREATE TABLE `CountCache` (
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MetaType` varchar(16) NOT NULL DEFAULT '',
  `fromdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `untildate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `SetSpec` varchar(64) NOT NULL DEFAULT '',
  `count` int(11) DEFAULT NULL,
  PRIMARY KEY (`MetaType`,`fromdate`,`untildate`,`SetSpec`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

SET character_set_client = @saved_cs_client;
