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

/**
 * Created by The eXo Platform SAS
 * Author : Vu Duy Tu
 *          tu.duy@exoplatform.com
 * Apr 27, 2009 - 8:29:06 AM  
 */
public class BBCodeData {
	public BBCodeData() {
  }
	
	public static List<BBCode> createDefaultBBcode() {
		List<BBCode> bbcodes = new ArrayList<BBCode>();
		BBCode bbcode;
		
		//I/ B/ U
    bbcode = new BBCode();
    bbcode.setTagName("I");
    bbcode.setDescription("Set text in italic");
    bbcode.setReplacement("<i>{param}</i>");
    bbcode.setExample("[I] this text is italic[/I]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("B");
    bbcode.setDescription("Set text in bold");
    bbcode.setReplacement("<b>{param}</b>");
    bbcode.setExample("[B] this text is bold[/B]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("U");
    bbcode.setDescription("Set text in underline");
    bbcode.setReplacement("<u>{param}</u>");
    bbcode.setExample("[U] this text is underline[/U]");
    bbcodes.add(bbcode);
    //LEFT/ RIGHT/ CENTER/ JUSTIFY
    bbcode = new BBCode();
    bbcode.setTagName("LEFT");
    bbcode.setDescription("Change the left alignment of your text.");
    bbcode.setReplacement("<div align=\"left\">{param}</div>");
    bbcode.setExample("[LEFT]this text is left-aligned[/LEFT]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("RIGHT");
    bbcode.setDescription("Change the right alignment of your text.");
    bbcode.setReplacement("<div align=\"right\">{param}</div>");
    bbcode.setExample("[RIGHT]this text is right-aligned[/RIGHT]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("CENTER");
    bbcode.setDescription("Change the center alignment of your text.");
    bbcode.setReplacement("<div align=\"center\">{param}</div>");
    bbcode.setExample("[CENTER]this text is center-aligned[/CENTER]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("JUSTIFY");
    bbcode.setDescription("Change the justify alignment of your text.");
    bbcode.setReplacement("<div align=\"justify\">{param}</div>");
    bbcode.setExample("[JUSTIFY]this text is justify-aligned[/JUSTIFY]");
    bbcodes.add(bbcode);
    //URL
    bbcode = new BBCode();
    bbcode.setTagName("URL");
    bbcode.setDescription("The [url] tag allows you to link to other websites and files.");
    bbcode.setReplacement("<a target='_blank' href=\"{param}\">{param}</a>");
    bbcode.setExample("[URL]http://www.exoplatform.com[/URL]");
    bbcodes.add(bbcode);
    bbcode = new BBCode();
    bbcode.setTagName("URL");
    bbcode.setDescription("The [url] tag allows you to link to other websites and files.");
    bbcode.setReplacement("<a target='_blank' href=\"{option}\">{param}</a>");
    bbcode.setExample("[URL=http://www.exoplatform.com]Click goto exoplatform website.[/URL]");
    bbcode.setOption(true);
    bbcodes.add(bbcode);
    //
		return bbcodes;
  }
	
	public static String getReplacementByBBcode(String s, List<BBCode> bbcodes) throws Exception {
		int lastIndex = 0;
		int tagIndex = 0;
		String start, end, bbc, str="ss", param, option;
		for (BBCode bbcode : bbcodes) {
			bbc = bbcode.getTagName();
			System.out.println("\n\n tagName: " + bbcode.getTagName());
			System.out.println("\n\n getReplacement: " + bbcode.getReplacement());
			if(bbc.equals("URL")){
				s = StringUtils.replace(s, "[link", "[URL");
				s = StringUtils.replace(s, "[/link]", "[/URL]");
				s = StringUtils.replace(s, "[LINK", "[URL");
				s = StringUtils.replace(s, "[/LINK]", "[/URL]");
			}
			lastIndex = 0; tagIndex = 0;
			if(bbcode.isOption()){
				start = "[" + bbc + "=";
				end = "[/" + bbc + "]";
				s = StringUtils.replace(s, start.toLowerCase(), start);
				s = StringUtils.replace(s, end.toLowerCase(), end);
				while ((tagIndex = s.indexOf(start, lastIndex)) != -1) {
					lastIndex = tagIndex + 1;
					try {
						int clsIndex = s.indexOf(end, tagIndex);
						str = s.substring(tagIndex + start.length(), clsIndex);
						option = str.substring(0, str.indexOf("]"));
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
				s = StringUtils.replace(s, start.toLowerCase(), start);
				s = StringUtils.replace(s, end.toLowerCase(), end);
				while ((tagIndex = s.indexOf(start, lastIndex)) != -1) {
					lastIndex = tagIndex + 1;
					try {
						int clsIndex = s.indexOf(end, tagIndex);
						str = s.substring(tagIndex + start.length(), clsIndex);
						param = StringUtils.replace(bbcode.getReplacement(), "{param}", str);
						s = StringUtils.replace(s, start + str + end, param);
					} catch (Exception e) {
						continue;
					}
				}
			}
    }
		return s;
	}
	
}



















