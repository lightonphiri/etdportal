DROP TABLE IF EXISTS `Archive`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `Archive` (
  `ID` varchar(64) NOT NULL,
  `Date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `MetaType` varchar(16) NOT NULL default '',
  `Source` varchar(256) default NULL,
  `MetaData` blob,
  `Deleted` tinyint(1) NOT NULL default '0',
  `About` blob NOT NULL,
  PRIMARY KEY  (`ID`,`MetaType`),
  KEY `IDX_DATE` (`Date`),
  KEY `IDX_METATYPE` (`MetaType`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
                    
                    