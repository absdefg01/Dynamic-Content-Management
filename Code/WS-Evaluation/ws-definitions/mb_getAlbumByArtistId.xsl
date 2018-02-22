<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mb="http://musicbrainz.org/ns/mmd-1.0#">
<xsl:template match="/">
<RESULT>
    <xsl:for-each select="*[local-name()='metadata']/*[local-name()='release-list']/*[local-name()='release']">

        		 <xsl:text>&#10;</xsl:text>
                 <RECORD>
                     <xsl:text>&#10; &#32;</xsl:text>  <ITEM ANGIE-VAR='?artistId'><xsl:value-of select="mb:artist/@id"/></ITEM>
                     <xsl:text>&#10; &#32;</xsl:text>  <ITEM ANGIE-VAR='?albumId'><xsl:value-of select="@id"/></ITEM>
                      <xsl:text>&#10; &#32;</xsl:text> <ITEM ANGIE-VAR='?albumName'><xsl:value-of select="mb:title"/></ITEM>
                     
                </RECORD>

    </xsl:for-each>  
</RESULT>
</xsl:template>
</xsl:stylesheet>
