/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
 */
package org.exoplatform.wiki.webui.control.action;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;
import org.exoplatform.wiki.commons.Utils;
import org.exoplatform.wiki.rendering.RenderingService;
import org.exoplatform.wiki.webui.UIWikiBottomArea;
import org.exoplatform.wiki.webui.UIWikiPageContainer;
import org.exoplatform.wiki.webui.UIWikiPageEditForm;
import org.exoplatform.wiki.webui.UIWikiPortlet;
import org.exoplatform.wiki.webui.UIWikiRichTextArea;
import org.exoplatform.wiki.webui.UIWikiSidePanelArea;
import org.exoplatform.wiki.webui.control.action.core.AbstractFormActionComponent;
import org.exoplatform.wiki.webui.control.filter.IsEditAddModeFilter;
import org.exoplatform.wiki.webui.control.filter.IsEditAddPageModeFilter;
import org.exoplatform.wiki.webui.control.listener.UIPageToolBarActionListener;
import org.xwiki.rendering.syntax.Syntax;

/**
 * Created by The eXo Platform SAS
 * Author : viet nguyen
 *          viet.nguyen@exoplatform.com
 * May 31, 2010  
 */
@ComponentConfig(
  template = "app:/templates/wiki/webui/control/action/AbstractActionComponent.gtmpl",               
  events = {
    @EventConfig(listeners = RichTextActionComponent.RichTextActionListener.class, phase = Phase.DECODE)
  }
)
public class RichTextActionComponent extends AbstractFormActionComponent {
  
  public static final String                   ACTION  = "RichText";

  private static final List<UIExtensionFilter> FILTERS = Arrays.asList(new UIExtensionFilter[] {
      new IsEditAddModeFilter(), new IsEditAddPageModeFilter() });

  @UIExtensionFilters
  public List<UIExtensionFilter> getFilters() {
    return FILTERS;
  }  

  @Override
  public String getActionName() {
    return ACTION;
  }

  @Override
  public boolean isAnchor() {
    return false;
  }

  @Override
  public boolean isSubmit() {
    return false;
  }
  
  public static class RichTextActionListener extends UIPageToolBarActionListener<RichTextActionComponent> {
    @Override
    protected void processEvent(Event<RichTextActionComponent> event) throws Exception {
      UIWikiPageEditForm wikiPageEditForm = event.getSource()
                                                 .getAncestorOfType(UIWikiPageEditForm.class);
      UIWikiPageContainer pageCotainer = wikiPageEditForm.getAncestorOfType(UIWikiPageContainer.class);
      UIWikiBottomArea bottomArea = pageCotainer.getChild(UIWikiBottomArea.class);
      UIWikiRichTextArea wikiRichTextArea = wikiPageEditForm.getChild(UIWikiRichTextArea.class);
      UIWikiSidePanelArea wikiSidePanelArea = wikiPageEditForm.getChild(UIWikiSidePanelArea.class);
      boolean isRichTextRendered = wikiRichTextArea.isRendered();
      wikiRichTextArea.setRendered(!isRichTextRendered);
      wikiPageEditForm.getUIFormTextAreaInput(UIWikiPageEditForm.FIELD_CONTENT).setRendered(isRichTextRendered);
      RenderingService renderingService = (RenderingService) PortalContainer.getComponent(RenderingService.class);
      if (isRichTextRendered) {
        String htmlContent = wikiRichTextArea.getUIFormTextAreaInput().getValue();
        String markupSyntax = wikiPageEditForm.getUIFormSelectBox(UIWikiPageEditForm.FIELD_SYNTAX).getValue();
        String markupContent = renderingService.render(htmlContent, Syntax.XHTML_1_0.toIdString(), markupSyntax, false);
        wikiPageEditForm.getUIFormTextAreaInput(UIWikiPageEditForm.FIELD_CONTENT).setValue(markupContent);        
        wikiSidePanelArea.setRendered(true);
        bottomArea.setRendered(true);
      } else {
        Utils.feedDataForWYSIWYGEditor(wikiPageEditForm,null);
        wikiSidePanelArea.setRendered(false);
        bottomArea.setRendered(false);
      }
      super.processEvent(event);
      event.getRequestContext().addUIComponentToUpdateByAjax(pageCotainer.getAncestorOfType(UIWikiPortlet.class));
    }
  }
  
}
