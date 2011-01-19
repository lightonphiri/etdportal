SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `Archive` (
  `ID` varchar(64) NOT NULL,
  `Date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `MetaType` varchar(16) NOT NULL default '',
  `Source` varchar(2048) default NULL,
  `MetaData` blob,
  `Deleted` tinyint(1) NOT NULL default '0',
  `About` blob NOT NULL,
  `SetSpec` VARCHAR(64) NOT NULL DEFAULT 'None',
  PRIMARY KEY  (`ID`,`MetaType`),
  KEY `IDX_DATE` (`Date`),
  KEY `IDX_METATYPE` (`MetaType`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
                    
CREATE TABLE `Repositories` (
  `ID` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `isRunning` tinyint(1) NOT NULL default '0',
  `harvestStatus` varchar(256) NOT NULL default '',
  `baseURL` varchar(2048) NOT NULL,
  `metadataFormat` varchar(1024) NOT NULL default 'oai_dc',
  `setSpec` varchar(16384) NOT NULL default '',
  `dateFrom` varchar(64) NOT NULL default '',
  `harvestInterval` INT NOT NULL default '3000',
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;
                    