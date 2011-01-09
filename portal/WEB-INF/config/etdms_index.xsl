<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" 
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:index="http://mufasa.cs.uct.ac.za:8182/portal" 
 exclude-result-prefixes=""
>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="etdms:thesis">
      <table>
         <xsl:apply-templates select="etdms:title"/>
         <xsl:apply-templates select="etdms:creator"/>
         <xsl:apply-templates select="etdms:description"/>
       </table>
   </xsl:template>

   <xsl:template match="etdms:title">
      <index:title><xsl:value-of select="."/></index:title>
   </xsl:template>

   <xsl:template match="etdms:creator">
      <index:creator><xsl:value-of select="."/></index:creator>
   </xsl:template>

   <xsl:template match="etdms:description">
      <index:description><xsl:value-of select="."/></index:description>
   </xsl:template>

</xsl:stylesheet>