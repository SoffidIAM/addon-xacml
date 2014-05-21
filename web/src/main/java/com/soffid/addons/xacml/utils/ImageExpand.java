package com.soffid.addons.xacml.utils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Box;

import es.caib.zkib.zkiblaf.ImageClic;

public class ImageExpand extends ImageClic implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean collapsed = true;
	
	public ImageExpand ()
	{
		super();
		setSclass("imageexpand"); //$NON-NLS-1$
	}
	
	public ImageExpand (String src)
	{
		super(src);
		setSclass("imageexpand"); //$NON-NLS-1$
	}
	
	
	public void afterCompose() {
		addEventListener (Events.ON_CLICK, new EventListener() {
			public void onEvent(Event event) throws Exception {
				ImageExpand target = (ImageExpand) event.getTarget();
				target.expandCollapse();
			}
		});
	}
	
	
	private void expandCollapse()
	{
		Box grandpa = (Box) getParent().getParent();
			
		if(collapsed)
		{
			setSrc("~./img/fletxa-baix.gif");
			collapsed = false;
		}
		else
		{
			setSrc("~./img/fletxa.gif");
			collapsed = true;
		}
		boolean first = true;
		for (Object obj: grandpa.getChildren())
		{
			Component box = (Component) obj;
			box.setVisible(first || !collapsed);
			first = false;
		}
	}

}
