/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ks.rss;

import java.util.Arrays;
import java.util.List;

import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

public class ForumRSSEventListener implements EventListener{
	private NodeHierarchyCreator nodeHierarchyCreator_;
	private String path_ ;
	private String workspace_ ;
	private String repository_ ; 
	private List<String> listPropertyNotGetEvent = Arrays.asList((new String[]{"exo:rssWatching", "ks.rss", "exo:emailWatching",
																																						 "exo:userWatching"}));
	
	public ForumRSSEventListener(NodeHierarchyCreator nodeHierarchyCreator, String ws, String repo) throws Exception {
		this.nodeHierarchyCreator_ = nodeHierarchyCreator;
		//RSSProcess process = new RSSProcess(this.nodeHierarchyCreator_);
		workspace_ = ws ;
		repository_ = repo ;
	}
	
  public String getSrcWorkspace(){  return workspace_ ; }
  public String getRepository(){ return repository_ ; }
  public String getPath(){ return path_ ; }
  public void setPath(String path){  path_  = path ; }
  
	public void onEvent(EventIterator evIter){		
		try{
			//ExoContainer container = ExoContainerContext.getCurrentContainer();
			RSSProcess process = new RSSProcess(this.nodeHierarchyCreator_);
			String path = null;
			while(evIter.hasNext()) {
				Event ev = evIter.nextEvent() ;
				path = ev.getPath();
				//System.out.println("\n\nType ==> "+ ev.getType());
				//System.out.println("Path ==> "+ ev.getPath());
				//if(listPropertyNotGetEvent.contains(path.substring(path.lastIndexOf("/") + 1))) continue;
				if(ev.getType() == Event.NODE_ADDED){
					//System.out.println("\n\n ==> Event.NODE_ADDED");
					process.generateRSS(ev.getPath(), Event.NODE_ADDED);
				}else if(ev.getType() == Event.PROPERTY_CHANGED) {
					//System.out.println("\n\n ==> Event.PROPERTY_CHANGED");
					process.generateRSS(path.substring(0, path.lastIndexOf("/")), Event.PROPERTY_CHANGED);
				}else if(ev.getType() == Event.NODE_REMOVED) {
					//System.out.println("\n\n ==> Event.NODE_REMOVED");					
					process.generateRSS(ev.getPath(), Event.NODE_REMOVED);
				}
				break ;								
			}
		}catch(Exception e) {
			e.printStackTrace() ;
		}		
	}  
}
