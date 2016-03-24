package com.soffid.addons.xacml.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.zkoss.zul.Treeitem;

import com.soffid.iam.addons.xacml.common.Expression;
import com.soffid.iam.addons.xacml.common.Policy;
import com.soffid.iam.addons.xacml.common.PolicyIdReference;
import com.soffid.iam.addons.xacml.common.PolicySet;
import com.soffid.iam.addons.xacml.common.PolicySetIdReference;

import es.caib.zkib.binder.tree.TreeModelProxyNode;
import es.caib.zkib.component.DataTree;
import es.caib.zkib.datamodel.DataNode;

public class Sort {
	
	public Sort()
	{
		super();
	}
	
	public static void ordena(Treeitem item)
	{
		DataTree tree = (DataTree) item.getTree();
		if (item.isOpen())
		{
			TreeModelProxyNode node = tree.getModelProxyNode(item);
			if (node != null)
			{
				node.sortChildren( new Comparator<TreeModelProxyNode>() {
					int getorder (Object obj)
					{
						if (obj instanceof PolicySet)
							return ((PolicySet) obj).getOrder();
						if (obj instanceof Policy)
							return ((Policy) obj).getOrder();
						if (obj instanceof PolicySetIdReference)
							return ((PolicySetIdReference) obj).getOrder();
						if (obj instanceof PolicyIdReference)
							return ((PolicyIdReference) obj).getOrder();
						return -1;
					}
					public int compare(TreeModelProxyNode o1, TreeModelProxyNode o2) {
						Object oo1 = ((DataNode)o1.getValue()).getInstance();
						Object oo2 = ((DataNode)o2.getValue()).getInstance();
						if (oo1 == null && oo2 == null)
							return 0;
						if (oo1 == null)
							return -1;
						if (oo2 == null)
							return +1;
						return getorder (oo1) - getorder (oo2);
					}
				});
				item.unload();
				item.setOpen(true);
			}
		}
		
	}

	
	public static Integer calculaOrdre(es.caib.zkib.binder.BindContext bindCtx, PolicySet actual)
	{
		int number = 0;
		
		Iterator it = bindCtx.getDataSource().getJXPathContext().iteratePointers(bindCtx.getXPath()+"/policySetIdRef");
		while (it.hasNext()){
			it.next();
			number ++;
		}
		it = bindCtx.getDataSource().getJXPathContext().iteratePointers(bindCtx.getXPath()+"/policyIdRef");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		it = bindCtx.getDataSource().getJXPathContext().iteratePointers(bindCtx.getXPath()+"/policySet");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		it = bindCtx.getDataSource().getJXPathContext().iteratePointers(bindCtx.getXPath()+"/policy");
		while (it.hasNext()) {
			it.next ();
			number ++;
		}
		return number;
	}
	
	
	public static void modifyOrderSubExpression(XACMLExpressionComponent p, Integer ordre)
	{
		if(p != null)
		{
			Collection<Expression> colExpression = p.getSubexpression();
			if(p.getSubexpression() != null && !p.getSubexpression().isEmpty())
			{
				for(Expression exp: colExpression)
				{
					Integer i = exp.getOrder();
					if(i > ordre)
					{
						exp.setOrder(i - 1);
					}
				}
			}
		}
	}
}
