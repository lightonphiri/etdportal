<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" 
 xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:index="http://mufasa.cs.uct.ac.za:8182/portal" 
 exclude-result-prefixes=""
>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="oai_marc:oai_marc">
      <table>
         <xsl:apply-templates select="oai_marc:varfield[@id='245']"/><!--Title-->
         <xsl:apply-templates select="oai_marc:varfield[@id='100']"/><!--Author-->
         <xsl:apply-templates select="oai_marc:varfield[@id='520']"/><!--Description-->
       </table>
   </xsl:template>

<xsl:template match="oai_marc:varfield[@id='245']"><!--Title-->
 <index:title><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></index:title>
</xsl:template>

<xsl:template match="oai_marc:varfield[@id='100']"><!--Author-->
 <index:creator><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></index:creator>
</xsl:template>

<xsl:template match="oai_marc:varfield[@id='520']"><!--Abstract-->
    <index:description><xsl:value-of select="oai_marc:subfield[@label='a']"/></index:description>
</xsl:template> 

</xsl:stylesheet>