<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:zul="http://www.zkoss.org/2005/zul">
	
	<xsl:template match="/zul:zk/zul:zscript" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
		<zul:zscript>
			boolean canViewXACML = es.caib.seycon.ng.utils.Security.isUserInRole("seu:xacml:show/*");
		</zul:zscript>
	</xsl:template>
	
	<xsl:template match="zul:tree/zul:treechildren/zul:treeitem[3]/@if" priority="3">
		<xsl:attribute name="if"><xsl:value-of select="substring(.,0,string-length(.))"></xsl:value-of> || canViewXACML}</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="zul:tree/zul:treechildren/zul:treeitem[3]/zul:treechildren/zul:treeitem[8]" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
			
			<zul:treeitem>
				<zul:treerow>
					<zul:apptreecell langlabel="xacml.menu" 
								pagina="addon/xacml/policySet.zul" >
								<xsl:attribute name="if">${canViewXACML}</xsl:attribute>
					</zul:apptreecell>
				</zul:treerow>
			</zul:treeitem>
			<zul:treeitem>
				<zul:treerow>
					<zul:apptreecell langlabel="xacml.apply" 
								pagina="addon/xacml/applyPolicySet.zul"> 
								<xsl:attribute name="if">${canViewXACML}</xsl:attribute>
					</zul:apptreecell>
				</zul:treerow>
			</zul:treeitem>
	</xsl:template>
 

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>