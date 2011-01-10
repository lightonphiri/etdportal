<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:index="http://mufasa.cs.uct.ac.za:8182/portal" 
 exclude-result-prefixes=""
>

   <xsl:template match="oai_dc:dc">
      <table>
         <xsl:apply-templates select="dc:title"/>
         <xsl:apply-templates select="dc:creator"/>
         <xsl:apply-templates select="dc:description"/>
      </table>
   </xsl:template>

   <xsl:template match="dc:title">
      <index:description><xsl:value-of select="."/></index:description>
   </xsl:template>
    
   <xsl:template match="dc:creator">
      <index:description><xsl:value-of select="."/></index:description>
   </xsl:template>

  <xsl:template match="dc:description">
      <index:description><xsl:value-of select="."/></index:description>
   </xsl:template>

</xsl:stylesheet>