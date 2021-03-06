<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" 
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes=""
>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="etdms:thesis">
      <table>
         <xsl:apply-templates select="etdms:title"/>
         <xsl:apply-templates select="etdms:creator"/>
         <xsl:apply-templates select="etdms:subject"/>
	 <xsl:apply-templates select="etdms:publisher"/>
	 <xsl:apply-templates select="etdms:date"/>
	 <xsl:apply-templates select="etdms:type"/>
	 <xsl:apply-templates select="etdms:rights"/>
	 <xsl:apply-templates select="etdms:language"/>
         <xsl:apply-templates select="etdms:format"/>
         <xsl:apply-templates select="etdms:contributor"/>
         <xsl:apply-templates select="etdms:description"/>
         <xsl:apply-templates select="etdms:degree"/>
         <xsl:apply-templates select="etdms:identifier"/>
      </table>
   </xsl:template>

   <xsl:template match="etdms:title">
      <tr>
         <th>Title</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>


   <xsl:template match="etdms:subject">
      <tr>
         <th>Subject</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:creator">
      <tr>
         <th>Author</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:publisher">
      <tr>
         <th>Publisher</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:date">
      <tr>
         <th>Date</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>
   <xsl:template match="etdms:type">
      <tr>
         <th>Type</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:rights">
      <tr>
         <th>Rights</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>
   <xsl:template match="etdms:language">
      <tr>
         <th>Language</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:contributor">
      <tr>
         <th>Contributor (<xsl:value-of select="@role"/>)</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:description">
      <tr>
         <th>Abstract</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:format">
      <tr>
         <th>Format</th><td><xsl:value-of select="."/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:degree">
      <tr>
         <th>Degree Name</th><td><xsl:value-of select="etdms:name"/></td>
      </tr><tr>
         <th>Degree Level</th><td><xsl:value-of select="etdms:level"/></td>
      </tr><tr>
         <th>Degree Discipline</th><td><xsl:value-of select="etdms:discipline"/></td>
      </tr><tr>
         <th>Degree Grantor</th><td><xsl:value-of select="etdms:grantor"/></td>
      </tr>
   </xsl:template>

   <xsl:template match="etdms:identifier">
      <tr>
         <th>Identifier</th><td><a href="{.}"><xsl:value-of select="."/></a></td>
      </tr>
   </xsl:template>


</xsl:stylesheet>