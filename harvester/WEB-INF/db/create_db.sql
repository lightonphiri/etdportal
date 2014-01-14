SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;

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
) 
ENGINE=MyISAM DEFAULT CHARSET=utf8;   

--
-- Table structure for table `Archive2`
--

CREATE TABLE `Archive2` (                                                                                                                                                                                                                                                      
  `identifier` char(255) NOT NULL,                                                                                                                                                                                                                                             
  `dateStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `metadataPrefix` char(16) NOT NULL,
  `setSpec` char(64) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`identifier`,`metadataPrefix`),
  KEY `setSpecdeletedidentifier` (`setSpec`,`deleted`,`identifier`),
  KEY `metadataPrefixdatestamp` (`metadataPrefix`,`dateStamp`),
  KEY `dateStampmetadataPrefix` (`dateStamp`,`metadataPrefix`),
  KEY `metadataPrefixdateStampidentifier` (`metadataPrefix`,`dateStamp`,`identifier`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `Archive_Errors`
--

CREATE TABLE `Archive_Errors` (
  `ID` varchar(256) DEFAULT NULL,
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MetaType` varchar(16) DEFAULT NULL,
  `Source` varchar(2048) DEFAULT NULL,
  `MetaData` blob,
  `Deleted` tinyint(1) DEFAULT NULL,
  `About` blob,
  `SetSpec` varchar(64) DEFAULT NULL
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
-- Table structure for table `countcache`
--

CREATE TABLE `countcache` (
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MetaType` varchar(16) NOT NULL DEFAULT '',
  `fromdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `untildate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `SetSpec` varchar(64) NOT NULL DEFAULT '',
  `count` int(11) DEFAULT NULL,
  PRIMARY KEY (`MetaType`,`fromdate`,`untildate`,`SetSpec`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

SET character_set_client = @saved_cs_client;
