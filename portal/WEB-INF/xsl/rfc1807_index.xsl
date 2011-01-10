<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:rfc1807="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1807.txt"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:index="http://mufasa.cs.uct.ac.za:8182/portal" 
 exclude-result-prefixes="dc"
>

   <xsl:output method="html" indent="yes"/>
 
   <xsl:template match="rfc1807:rfc1807">
      <table>
         <xsl:apply-templates select="rfc1807:title"/>
         <xsl:apply-templates select="rfc1807:author"/>
	 <xsl:apply-templates select="rfc1807:abstract"/>
      </table>
   </xsl:template>

   <xsl:template match="rfc1807:title">
     <index:title><xsl:value-of select="."/></index:title>
    </xsl:template>

   <xsl:template match="rfc1807:author">
      <index:creator><xsl:value-of select="."/></index:creator>
   </xsl:template>

   <xsl:template match="rfc1807:abstract">
       <index:description><xsl:value-of select="."/></index:description>
  </xsl:template>

</xsl:stylesheet>