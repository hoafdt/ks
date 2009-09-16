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
package org.exoplatform.forum;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.forum.service.BBCode;
import org.exoplatform.forum.service.ForumService;

/**
 * Created by The eXo Platform SAS
 * Author : Vu Duy Tu
 *          tu.duy@exoplatform.com
 * Apr 27, 2009 - 8:29:06 AM  
 */
public class BBCodeData {
	public BBCodeData() {
  }
	public static String getReplacementByBBcode(String s, List<BBCode> bbcodes, ForumService forumService) throws Exception {
		int lastIndex = 0, tagIndex = 0, clsIndex = 0;
		String start, end, bbc, str="", param, option;
		for (BBCode bbcode : bbcodes) {
			bbc = bbcode.getTagName();
			if(bbc.equals("URL")){
				s = StringUtils.replace(s, "[link", "[URL");
				s = StringUtils.replace(s, "[/link]", "[/URL]");
				s = StringUtils.replace(s, "[LINK", "[URL");
				s = StringUtils.replace(s, "[/LINK]", "[/URL]");
			}
			bbc = bbc.toLowerCase();
			if(!bbc.equals("list")){
				lastIndex = 0; tagIndex = 0;
				if(bbcode.isOption()){
					start = "[" + bbc + "=";
					end = "[/" + bbc + "]";
					s = StringUtils.replace(s, start.toUpperCase(), start);
					s = StringUtils.replace(s, end.toUpperCase(), end);
					while ((tagIndex = s.indexOf(start, lastIndex)) != -1) {
						lastIndex = tagIndex + 1;
						try {
							clsIndex = s.indexOf(end, tagIndex);
							str = bbcode.getReplacement();
							if(str == null || str.trim().length() == 0 || str.equals("null")) {
								bbcode.setReplacement(forumService.getBBcode(bbcode.getId()).getReplacement());
							}
							str = s.substring(tagIndex + start.length(), clsIndex);
							option = str.substring(0, str.indexOf("]"));
							if(option.indexOf("+")==0)option = option.replaceFirst("+", "");
							if(option.indexOf("\"")==0)option = option.replaceAll("\"", "");
							if(option.indexOf("&quot;")==0)option = option.replaceAll("&quot;", "");
							param = str.substring(str.indexOf("]")+1);
							param = StringUtils.replace(bbcode.getReplacement(), "{param}", param);
							param = StringUtils.replace(param, "{option}", option.trim());
							s = StringUtils.replace(s, start + str + end, param);
						} catch (Exception e) {
							continue;
						}
					}
				} else {
					start = "[" + bbc + "]";
					end = "[/" + bbc + "]";
					s = StringUtils.replace(s, start.toUpperCase(), start);
					s = StringUtils.replace(s, end.toUpperCase(), end);
					while ((tagIndex = s.indexOf(start, lastIndex)) != -1) {
						lastIndex = tagIndex + 1;
						try {
							clsIndex = s.indexOf(end, tagIndex);
							str = bbcode.getReplacement();
							if(str == null || str.trim().length() == 0 || str.equals("null")) {
								bbcode.setReplacement(forumService.getBBcode(bbcode.getId()).getReplacement());
							}
							str = s.substring(tagIndex + start.length(), clsIndex);
							param = StringUtils.replace(bbcode.getReplacement(), "{param}", str);
							s = StringUtils.replace(s, start + str + end, param);
						} catch (Exception e) {
							continue;
						}
					}
				}
	    } else {
	    	lastIndex = 0;
	  		tagIndex = 0;
	  		s = StringUtils.replace(s, "[LIST", "[list");
	  		s = StringUtils.replace(s, "[/LIST]", "[/list]");
	  		while ((tagIndex = s.indexOf("[list]", lastIndex)) != -1) {
	  			lastIndex = tagIndex + 1;
	  			try {
	  				clsIndex = s.indexOf("[/list]", tagIndex);
	  				str = s.substring(tagIndex + 6, clsIndex);
	  				String str_ =  "";
	  				str_ = StringUtils.replaceOnce(str, "[*]", "<li>");
	  				str_ = StringUtils.replace(str_, "[*]", "</li><li>");
	  				if(str_.lastIndexOf("</li><li>") > 0) {
	  					str_ = str_ + "</li>";
	  				}
	  				if(str_.indexOf("<br/>") >= 0) {
	  					str_ = StringUtils.replace(str_, "<br/>", "");
	  				}
	  				if(str_.indexOf("<p>") >= 0) {
	  					str_ = StringUtils.replace(str_, "<p>", "");
	  					str_ = StringUtils.replace(str_, "</p>", "");
	  				}
	  				s = StringUtils.replace(s, "[list]" + str + "[/list]", "<ul>" + str_ + "</ul>");
	  			} catch (Exception e) {
	  				continue;
	  			}
	  		}
	  		
	  		lastIndex = 0;
	  		tagIndex = 0;
	  		while ((tagIndex = s.indexOf("[list=", lastIndex)) != -1) {
	  			lastIndex = tagIndex + 1;
	  			
	  			try {
	  				clsIndex = s.indexOf("[/list]", tagIndex);
	  				String content = s.substring(tagIndex + 6, clsIndex);
	  				int clsType = content.indexOf("]");
	  				String type = content.substring(0, clsType);
	  				type.replaceAll("\"", "").replaceAll("'", "");
	  				str = content.substring(clsType + 1);
	  				String str_ =  "";
	  				str_ = StringUtils.replaceOnce(str, "[*]", "<li>");
	  				str_ = StringUtils.replace(str_, "[*]", "</li><li>");
	  				if(str_.lastIndexOf("</li><li>") > 0) {
	  					str_ = str_ + "</li>";
	  				}
	  				if(str_.indexOf("<br/>") >= 0) {
	  					str_ = StringUtils.replace(str_, "<br/>", "");
	  				}
	  				if(str_.indexOf("<p>") >= 0) {
	  					str_ = StringUtils.replace(str_, "<p>", "");
	  					str_ = StringUtils.replace(str_, "</p>", "");
	  				}
	  				s = StringUtils.replace(s, "[list=" + content + "[/list]", "<ol type=\""+type+"\">" + str_ + "</ol>");
	  			} catch (Exception e) {
	  				continue;
	  			}
	  		}
	    }
		}
		return s;
	}
}


















