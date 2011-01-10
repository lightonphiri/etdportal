<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
 xmlns:dc="http://purl.org/dc/elements/1.1/" 
 xmlns:oai="http://www.openarchives.org/OAI/2.0/"
 xmlns:rfc1807="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1807.txt"
 xmlns:oai_marc="http://www.openarchives.org/OAI/1.1/oai_marc" 
 xmlns:etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" 
 xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
 xmlns="http://www.w3.org/1999/xhtml" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
>
    
   <xsl:include href="webapps/ROOT/WEB-INF/config/marc.xsl"/>
   <xsl:include href="webapps/ROOT/WEB-INF/config/etdms.xsl"/>
   <xsl:include href="webapps/ROOT/WEB-INF/config/rfc1807.xsl"/>
   <xsl:include href="webapps/ROOT/WEB-INF/config/dc.xsl"/>

   <xsl:output method="html" indent="yes"/>

   <xsl:template match="records">
      <xsl:choose>
	 <xsl:when test="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/oai_marc:oai_marc">
            <xsl:apply-templates select="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/oai_marc:oai_marc"/>
         </xsl:when>
         <xsl:when test="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/etdms:thesis">
            <xsl:apply-templates select="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/etdms:thesis"/>
         </xsl:when>
         <xsl:when test="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/rfc1807:rfc1807">
            <xsl:apply-templates select="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/rfc1807:rfc1807"/>
         </xsl:when>
         <xsl:when test="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/oai_dc:dc">
            <xsl:apply-templates select="oai:OAI-PMH/oai:GetRecord/oai:record/oai:metadata/oai_dc:dc"/>
         </xsl:when>
      </xsl:choose>
   </xsl:template>

</xsl:stylesheet>
