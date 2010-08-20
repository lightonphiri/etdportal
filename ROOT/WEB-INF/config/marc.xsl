<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes="dc"
>

   <xsl:output method="html" indent="yes"/>
 
   <xsl:template match="oai_marc:oai_marc">
      <table>
         <xsl:apply-templates select="oai_marc:varfield[@id='245']"/><!--Title-->
	 <xsl:apply-templates select="oai_marc:varfield[@id='246']"/><!--Title Alternative-->
	 <xsl:apply-templates select="oai_marc:varfield[@id='242']"/><!--Title Translated-->
	 <xsl:apply-templates select="oai_marc:varfield[@id='100']"/><!--Author-->
         <xsl:apply-templates select="oai_marc:varfield[@id='653']"/><!--Subject-->
         <xsl:apply-templates select="oai_marc:varfield[@id='260']"/><!--Publisher-->
         <xsl:apply-templates select="oai_marc:fixfield[@id='008']"/><!--Date and Language-->
         <xsl:apply-templates select="oai_marc:varfield[@id='655']"/><!--Type-->
         <xsl:apply-templates select="oai_marc:varfield[@id='540']"/><!--Rights-->
         <xsl:apply-templates select="oai_marc:varfield[@id='856']"/><!--Format and others-->
         <xsl:apply-templates select="oai_marc:varfield[@id='720']"/><!--Contributor-->
         <xsl:apply-templates select="oai_marc:varfield[@id='520']"/><!--Abstract-->
         <xsl:apply-templates select="oai_marc:varfield[@id='502']"/><!--Degree Name/Level-->
         <xsl:apply-templates select="oai_marc:varfield[@id='710']"/><!--Degree Grantor-->
      </table>
   </xsl:template>
<xsl:template match="oai_marc:varfield[@id='710']">
      <tr>
         <th>Degree Grantor</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
       </tr>
       <tr>
         <th>Degree Field</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='b']"/></td>
       </tr>
</xsl:template>    
<xsl:template match="oai_marc:varfield[@id='502']">
      <tr>
         <th>Degree Name/Level</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
       </tr>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='520']">
      <tr>
         <th>Abstract</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
       </tr>
</xsl:template>    
<xsl:template match="oai_marc:varfield[@id='720']">
      <tr>
         <th>Contributor(<xsl:apply-templates select="oai_marc:subfield[@label='e']"/>)</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
       </tr>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='856']">
      <tr>
         <th>Format</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='q']"/></td>
       </tr>
</xsl:template> 
<xsl:template match="oai_marc:fixfield[@id='008']">
      <tr>
         <th>Date</th>
	 <td><xsl:value-of select="substring (node(), 7, 4)"/></td>
      </tr>
      <tr>
         <th>Language</th>
	 <td><xsl:value-of select="substring (node(), 35, 3)"/></td>
      </tr>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='540']">
      <tr>
         <th>Rights</th>
	 <td><xsl:value-of select="."/></td>
      </tr>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='655']">
      <tr>
         <th>Type</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
      </tr>
</xsl:template> 
<xsl:template match="oai_marc:varfield[@id='245']">
      <tr>
         <th>Title</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
      </tr>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='246']">
      <tr>
         <th>Title Alternative</th>
	 <td><xsl:value-of select="."/></td>
      </tr>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='242']">
      <tr>
         <th>Title Translated</th>
	 <td><xsl:value-of select="."/></td>
      </tr>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='100']">
      <tr>
         <th>Author</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
      </tr>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='653']">
      <tr>
         <th>Subject</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
      </tr>
</xsl:template>
<xsl:template match="oai_marc:varfield[@id='260']">
      <tr>
         <th>Publisher</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='a']"/></td>
	 <th>Publishing Location</th>
	 <td><xsl:apply-templates select="oai_marc:subfield[@label='b']"/></td>
      </tr>
</xsl:template>
   
</xsl:stylesheet>