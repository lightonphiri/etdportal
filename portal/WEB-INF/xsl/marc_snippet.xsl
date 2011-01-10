<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc"
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes=""
>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="oai_marc:oai_marc">
      <div class="snippet">
         <span class="snippet_title"><a href="{concat ('?action=view&amp;identifier=', ../../oai:header/oai:identifier)}"><xsl:apply-templates select="oai_marc:varfield[@id='245']"/></a></span>
         <br/>
         <span class="snippet_citation"><xsl:apply-templates select="oai_marc:varfield[@id='100']"/>, <xsl:apply-templates select="oai_marc:varfield[@id='502']"/>, <xsl:apply-templates select="oai_marc:varfield[@id='710']"/>,<xsl:apply-templates select="oai_marc:fixfield[@id='008']"/></span>
         <br/>
         <span class="snippet_abstract"><xsl:apply-templates select="oai_marc:varfield[@id='520']"/> ...</span>
      </div>
   </xsl:template>
<xsl:template match="oai_marc:varfield[@id='245']"><!--Title-->
 <xsl:apply-templates select="oai_marc:subfield[@label='a']"/>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='100']"><!--Author-->
  <xsl:apply-templates select="oai_marc:subfield[@label='a']"/>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='710']"><!--Degree Grantor-->
  <xsl:apply-templates select="oai_marc:subfield[@label='a']"/>,
  <xsl:apply-templates select="oai_marc:subfield[@label='b']"/>
</xsl:template>    
<xsl:template match="oai_marc:varfield[@id='502']"><!--Degree Name/Level-->
  <xsl:apply-templates select="oai_marc:subfield[@label='a']"/>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='520']"><!--Abstract-->
    <xsl:value-of select="substring (oai_marc:subfield[@label='a'], 1,300 )"/>
</xsl:template> 
<xsl:template match="oai_marc:fixfield[@id='008']"><!--Date-->
  <xsl:value-of select="substring (node(), 7, 4)"/>
</xsl:template> 
</xsl:stylesheet>