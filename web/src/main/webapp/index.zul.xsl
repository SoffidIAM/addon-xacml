<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:template match="zul:vbox/zul:application | zul:div/zul:application" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
		<zul:hbox id="xacmltesting" visible="false" style="zindex: 1; position:fixed; top: 3em; right: 10px; "><label style="color:red;" id="xacmltestinglabel"/>
		</zul:hbox>
		<zul:zscript>
			new com.soffid.addons.xacml.pep.WebPolicyManager().generateMessage();			
		</zul:zscript>	
	</xsl:template>
 

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>