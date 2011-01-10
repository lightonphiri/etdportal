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
      <div class="snippet">
         <span class="snippet_title"><a href="{concat ('?action=view&amp;identifier=', ../../oai:header/oai:identifier)}"><xsl:value-of select="rfc1807:title"/></a></span>
         <br/>
         <span class="snippet_citation"><xsl:value-of select="rfc1807:author"/>,<xsl:value-of select="rfc1807:type"/> , <xsl:value-of select="rfc1807:entry"/>, <xsl:value-of select="rfc1807:organization"/></span>
         <br/>
         <span class="snippet_abstract"><xsl:value-of select="substring (rfc1807:abstract, 1, 300)"/> ...</span>
      </div>
      </xsl:template>


</xsl:stylesheet>