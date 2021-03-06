<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes=""
>

   <xsl:template match="oai_dc:dc">
      <table>
         <xsl:apply-templates select="dc:title"/>
         <xsl:apply-templates select="dc:creator"/>
         <xsl:apply-templates select="dc:subject"/>
	 <xsl:apply-templates select="dc:date"/>
	 <xsl:apply-templates select="dc:type"/>
         <xsl:apply-templates select="dc:format"/>
         <xsl:apply-templates select="dc:description"/>
         <xsl:apply-templates select="dc:publisher"/>
         <xsl:apply-templates select="dc:identifier"/>
      </table>
   </xsl:template>

   <xsl:template match="dc:title">
      <tr>
         <th>Title</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>
    
    <xsl:template match="dc:subject">
      <tr>
         <th>Subject</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="dc:creator">
      <tr>
         <th>Author</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

    <xsl:template match="dc:date">
      <tr>
         <th>Date</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>
   <xsl:template match="dc:type">
      <tr>
         <th>Type</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

  <xsl:template match="dc:description">
      <tr>
         <th>Abstract</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="dc:format">
      <tr>
         <th>Format</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="dc:publisher">
      <tr>
         <th>Publisher</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="dc:identifier">
      <tr>
         <th>Identifier</th><td><a href="{.}"><xsl:value-of select="."/></a></td>
      </tr>
   </xsl:template>

</xsl:stylesheet>
