<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:rfc1807="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1807.txt"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes="dc"
>

   <xsl:output method="html" indent="yes"/>
 
   <xsl:template match="rfc1807:rfc1807">
      <table>
         <xsl:apply-templates select="rfc1807:title"/>
         <xsl:apply-templates select="rfc1807:author"/>
	 <xsl:apply-templates select="rfc1807:cr-category"/>
	 <xsl:apply-templates select="rfc1807:entry"/>
	 <xsl:apply-templates select="rfc1807:type"/>
	 <xsl:apply-templates select="rfc1807:organization"/>
	 <xsl:apply-templates select="rfc1807:language"/>
         <xsl:apply-templates select="rfc1807:format"/>
         <xsl:apply-templates select="rfc1807:abstract"/>
         <xsl:apply-templates select="rfc1807:bib-version"/>
         <xsl:apply-templates select="rfc1807:id"/>
      </table>
   </xsl:template>

   <xsl:template match="rfc1807:title">
      <tr>
         <th>Title</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:bib-version">
      <tr>
         <th>Bib-version</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:id">
      <tr>
         <th>ID</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:creator">
      <tr>
         <th>Author</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:cr-category">
      <tr>
         <th>Subject</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:date">
      <tr>
         <th>Date</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>
   <xsl:template match="rfc1807:type">
      <tr>
         <th>Type</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:organization">
      <tr>
         <th>Organization</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:language">
      <tr>
         <th>Language</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:abstract">
      <tr>
         <th>Abstract</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="rfc1807:format">
      <tr>
         <th>Format</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

</xsl:stylesheet>