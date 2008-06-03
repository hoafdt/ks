/***************************************************************************
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.forum.webui;

import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.forum.ForumSessionUtils;
import org.exoplatform.forum.ForumUtils;
import org.exoplatform.forum.service.ForumSeach;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : Duy Tu
 *	  tu.duy@exoplatform.com
 * 14 Apr 2008, 02:57:05	
 */
@ComponentConfig(
		lifecycle = UIFormLifecycle.class,
		template = "app:/templates/forum/webui/UIQuickSeachForm.gtmpl",
		events = {
			@EventConfig(listeners = UIQuickSeachForm.SearchActionListener.class),			
			@EventConfig(listeners = UIQuickSeachForm.AdvancedSearchActionListener.class)			
		}
)
public class UIQuickSeachForm extends UIForm {
	final static	private String FIELD_SEARCHVALUE = "inputValue" ;
	
	public UIQuickSeachForm() throws Exception {
		addChild(new UIFormStringInput(FIELD_SEARCHVALUE, FIELD_SEARCHVALUE, null)) ;
		this.setSubmitAction(this.event("Search")) ;
	}

	
	static	public class SearchActionListener extends EventListener<UIQuickSeachForm> {
    public void execute(Event<UIQuickSeachForm> event) throws Exception {
			UIQuickSeachForm uiForm = event.getSource() ;
			UIFormStringInput formStringInput = uiForm.getUIStringInput(FIELD_SEARCHVALUE) ;
			String text = formStringInput.getValue() ;
			if(text != null && text.trim().length() > 0) {
				ForumService forumService = (ForumService)PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class) ;
				List<ForumSeach> list = null;
				try {
					list = forumService.getQuickSeach(ForumSessionUtils.getSystemProvider(), text+",,all", "");
				}catch (Exception e) {
					UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
					uiApp.addMessage(new ApplicationMessage("UIQuickSeachForm.msg.failure", null, ApplicationMessage.WARNING)) ;
					return ;
				}
				UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
				UICategories categories = forumPortlet.findFirstComponentOfType(UICategories.class);
				categories.setIsRenderChild(true) ;				
				UIForumListSeach listSeachEvent = categories.getChild(UIForumListSeach.class) ;
				listSeachEvent.setListSeachEvent(list) ;
				forumPortlet.getChild(UIBreadcumbs.class).setUpdataPath(ForumUtils.FIELD_EXOFORUM_LABEL) ;
				formStringInput.setValue("") ;
				event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet) ;
			} else {
				formStringInput.setValue("") ;
				Object[] args = { };
				throw new MessageException(new ApplicationMessage("UIQuickSeachForm.msg.checkEmpty", args, ApplicationMessage.WARNING)) ;
			}
		}
	}

	static	public class AdvancedSearchActionListener extends EventListener<UIQuickSeachForm> {
		public void execute(Event<UIQuickSeachForm> event) throws Exception {
			UIQuickSeachForm uiForm = event.getSource() ;
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			forumPortlet.updateIsRendered(ForumUtils.FIELD_SEARCHFORUM_LABEL) ;
			forumPortlet.getChild(UIBreadcumbs.class).setUpdataPath(ForumUtils.FIELD_EXOFORUM_LABEL) ;
			UISearchForm searchForm = forumPortlet.getChild(UISearchForm.class) ;
			searchForm.setUserProfile(forumPortlet.getUserProfile()) ;
			searchForm.setSelectType("forumCategory") ;
			searchForm.setValueOnchange(false, false, false, false, false, false, false) ;
			event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet) ;
		}
	}
}
