/***************************************************************************
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.faq.webui.popup;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.faq.service.Answer;
import org.exoplatform.faq.service.Comment;
import org.exoplatform.faq.service.FAQService;
import org.exoplatform.faq.service.FAQSetting;
import org.exoplatform.faq.service.Question;
import org.exoplatform.faq.webui.FAQUtils;
import org.exoplatform.faq.webui.UIFAQPortlet;
import org.exoplatform.forum.service.Forum;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.Post;
import org.exoplatform.forum.service.Topic;
import org.exoplatform.portal.webui.util.SessionProviderFactory;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SAS Author : Vu Duy Tu tu.duy@exoplatform.com
 * 14-01-2009 - 04:20:05
 */
@ComponentConfig(
		lifecycle = UIFormLifecycle.class, 
		template = "app:/templates/faq/webui/popup/UISelectForumForm.gtmpl", 
		events = {
	    @EventConfig(listeners = UISelectForumForm.CloseActionListener.class, phase = Phase.DECODE),
	    @EventConfig(listeners = UISelectForumForm.SelectForumActionListener.class, phase = Phase.DECODE) 
		}
)
    
public class UISelectForumForm extends UIForm implements UIPopupComponent {
	private String questionId;
	private String categoryId;
	private List<Forum> listForum;
	public UISelectForumForm() {

	}

	public void activate() throws Exception {
	}
	public void deActivate() throws Exception {
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public List<Forum> getListForum() {
		FAQSetting faqSetting = new FAQSetting();
		FAQUtils.getPorletPreference(faqSetting);
		String catePath = faqSetting.getPathNameCategoryForum();
		listForum = new ArrayList<Forum>();
		if (catePath.indexOf(";") > 0) {
			catePath = catePath.substring(0, catePath.indexOf(";"));
			categoryId = catePath.substring(catePath.lastIndexOf("/") + 1);
			SessionProvider sProvider = SessionProviderFactory.createSystemProvider();
			try {
				ForumService forumService = (ForumService) PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class);
				String strQuery = "@exo:isClosed='false' and @exo:isLock='false'";
				listForum = forumService.getForums(sProvider, categoryId, strQuery);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sProvider.close();
			}
		}
		return listForum;
	}
	
	static public class CloseActionListener extends EventListener<UISelectForumForm> {
		public void execute(Event<UISelectForumForm> event) throws Exception {
			UISelectForumForm uiForm = event.getSource();
			UIFAQPortlet portlet = uiForm.getAncestorOfType(UIFAQPortlet.class);
			portlet.cancelAction();
		}
	}

	static public class SelectForumActionListener extends EventListener<UISelectForumForm> {
		public void execute(Event<UISelectForumForm> event) throws Exception {
			UISelectForumForm uiForm = event.getSource();
			String forumId = event.getRequestContext().getRequestParameter(OBJECTID);
			SessionProvider sProvider = SessionProviderFactory.createSystemProvider();
			try {
				FAQService faqService = (FAQService)PortalContainer.getInstance().getComponentInstanceOfType(FAQService.class) ;
				Question question = faqService.getQuestionById(uiForm.questionId, sProvider);
				Topic topic = new Topic();
				topic.setOwner(question.getAuthor());
				topic.setTopicName(question.getQuestion());
				topic.setDescription(question.getDetail());
				topic.setIcon("IconsView");
				ForumService forumService = (ForumService) PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class);
				forumService.saveTopic(sProvider, uiForm.categoryId, forumId, topic, true, false, "");
				faqService.savePathDiscussQuestion(uiForm.questionId, uiForm.categoryId+"/"+forumId+"/"+topic.getId(), sProvider);
				Post post;
				Answer[] AllAnswer = question.getAnswers();
				if(AllAnswer != null && AllAnswer.length > 0) {
					for (int i = 0; i < AllAnswer.length; i++) {
		        post = new Post();
		        post.setIcon("IconsView");
		        post.setName("Re: " + question.getQuestion());
		        post.setMessage(AllAnswer[i].getResponses());
		        post.setOwner(AllAnswer[i].getResponseBy());
		        forumService.savePost(sProvider, uiForm.categoryId, forumId, topic.getId(), post, true, "");
		        AllAnswer[i].setPostId(post.getId());
		        System.out.println(AllAnswer[i].getPostId());
	        }
					faqService.saveAnswer(uiForm.questionId, AllAnswer, sProvider);
				}
				Comment[] comments = question.getComments();
				for (int i = 0; i < comments.length; i++) {
					post = new Post();
					post.setIcon("IconsView");
					post.setName("Re: " + question.getQuestion());
					post.setMessage(comments[i].getComments());
					post.setOwner(comments[i].getCommentBy());
					forumService.savePost(sProvider, uiForm.categoryId, forumId, topic.getId(), post, true, "");
					comments[i].setPostId(post.getId());
					faqService.saveComment(uiForm.questionId, comments[i], false, sProvider);
				}
      } catch (Exception e) {
	      e.printStackTrace();
      } finally {
      	sProvider.close();
      }
			UIFAQPortlet portlet = uiForm.getAncestorOfType(UIFAQPortlet.class);
			portlet.cancelAction();
			event.getRequestContext().addUIComponentToUpdateByAjax(portlet);
		}
	}
}
