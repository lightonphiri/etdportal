<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:exslt="http://exslt.org/common"
 xmlns:encoder="xalan://java.net.URLEncoder"
 exclude-result-prefixes=""
>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="etdms:thesis">
      <div class="snippet">
         <span class="snippet_title"><a href="{concat ('?action=view&amp;identifier=', encoder:encode(../../oai:header/oai:identifier))}"><xsl:value-of select="etdms:title"/></a></span>
         <br/>
         <span class="snippet_citation"><xsl:value-of select="etdms:creator"/>, <xsl:value-of select="etdms:degree/etdms:name"/>, <xsl:value-of select="etdms:date"/>, <xsl:value-of select="etdms:publisher"/></span>
         <br/>
         <span class="snippet_abstract"><xsl:value-of select="substring (etdms:description, 1, 300)"/> ...</span>
      </div>
   </xsl:template>

</xsl:stylesheet>