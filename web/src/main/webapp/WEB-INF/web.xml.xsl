<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
	xmlns="http://java.sun.com/xml/ns/j2ee"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="j2ee:web-app/j2ee:filter[1]" priority="3">
		<filter>
			<filter-name>XACMLFilter</filter-name>
			<filter-class>com.soffid.addons.xacml.pep.XACMLFilter</filter-class>
		</filter>

		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="j2ee:web-app/j2ee:filter-mapping[1]" priority="3">
		<filter-mapping>
			<filter-name>XACMLFilter</filter-name>
			<url-pattern>/*</url-pattern>
			<dispatcher>REQUEST</dispatcher>
        	<dispatcher>FORWARD</dispatcher>
        	<dispatcher>INCLUDE</dispatcher>
		</filter-mapping>
		
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
			
	</xsl:template>
 

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>