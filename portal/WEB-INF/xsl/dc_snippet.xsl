<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 exclude-result-prefixes=""
>
   <xsl:output method="html" indent="yes"/>

   <xsl:template match="oai_dc:dc">
      <div class="snippet">
         <span class="snippet_title">
            <a href="{concat ('?action=view&amp;identifier=', ../../oai:header/oai:identifier)}">
               <xsl:choose>
                  <xsl:when test="dc:title"><xsl:value-of select="dc:title"/></xsl:when>
                  <xsl:otherwise>No title</xsl:otherwise>
               </xsl:choose>
            </a>
         </span>
         <br/>
         <span class="snippet_citation">
            <xsl:choose>
               <xsl:when test="dc:creator"><xsl:value-of select="dc:creator"/></xsl:when>
               <xsl:otherwise>No author</xsl:otherwise>
            </xsl:choose>
            <xsl:if test="dc:date">, <xsl:value-of select="dc:date"/></xsl:if>
            <xsl:if test="dc:publisher">, <xsl:value-of select="dc:publisher"/></xsl:if>
         </span>
         <br/>
         <xsl:if test="dc:description">
            <span class="snippet_abstract"><xsl:value-of select="substring (dc:description, 1, 300)"/> ...</span>
         </xsl:if>
      </div>
   </xsl:template>

</xsl:stylesheet>