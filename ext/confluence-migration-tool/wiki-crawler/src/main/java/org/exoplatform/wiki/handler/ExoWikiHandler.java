/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.wiki.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.exoplatform.wiki.IWikiHandler;

/**
 * Created by The eXo Platform SAS
 * Author: Dimitri BAELI
 *         dbaeli@exoplatform.com
 * Feb 02, 2012
 * 
 * A class to interact with the ExoWikiRestService See Wikiloader.groovy class
 */
public class ExoWikiHandler implements IWikiHandler {
  public static final String CHAR_TO_REPLACE = " *'\"+?&";
  
  private static final Logger log = Logger.getLogger(ExoWikiHandler.class.toString());

  private DefaultHttpClient httpClient;
  private String targetHost;

  public ExoWikiHandler(String targetHost) {
    this.targetHost = targetHost;
  }

  public String getTargetHost() {
    return targetHost;
  }

  public void setTargetHost(String targetHost) {
    this.targetHost = targetHost;
  }

  public void start(String targetUser, String targetPwd) {
    httpClient = buildHttpClientOnTarget(targetUser, targetPwd);
  }

  public void stop() {
    httpClient.getConnectionManager().shutdown();
  }

  private DefaultHttpClient buildHttpClientOnTarget(String targetUser, String targetPwd) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    UsernamePasswordCredentials creds = new UsernamePasswordCredentials(targetUser, targetPwd);
    httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
    return httpclient;
  }

  public boolean checkTargetPageUrl(String targetSpace, String targetPage) {
    try {
      int statusCode = getHttpStatusOfPageOnTarget(targetSpace, targetPage);
      if (statusCode != 200) {
        log.warning("Target page not found or error : " + statusCode);
        return false;
      } else if (statusCode == 404) {
        log.info(String.format("Target page ok : %s/%s", targetSpace, targetPage));
        return true;
      } else {
        log.warning(String.format("Can't check target page, FAILURE with Http code %s : %s/%s", statusCode, targetSpace, targetPage));
      }
    } catch (IOException e) {
      log.warning("Target Page not found target: " + targetPage);
    }
    return false;
  }

  /**
   * Create a new Page, returns the new name, or null if already exists.
   * 
   * @param path
   * @param name
   * @return page name
   */
  public String createPage(String path, String name, boolean hasChildren) {
    String pageName = normalizePageName(name, CHAR_TO_REPLACE, '_');
    try {
      int statusCode = getHttpStatusOfPageOnTarget(path, pageName);
      if (statusCode == 404) {

        log.info(String.format("[Create] %s//%s", path, pageName));
        HttpGet httpGet = new HttpGet(targetHost + "/rest/private/wikiloader/create?path=" + URLEncoder.encode(path, "UTF-8") + "&name=" + URLEncoder.encode(pageName, "UTF-8"));
        HttpResponse responseCreate = httpClient.execute(httpGet);
        int statusCodeRC1 = responseCreate.getStatusLine().getStatusCode();
        if (statusCodeRC1 == 200) {
          pageName = IOUtils.toString(responseCreate.getEntity().getContent());
        } else {
          String message = IOUtils.toString(responseCreate.getEntity().getContent());
          log.warning(String.format("[Create] ERROR During Page creation : %s/%s (Cause: %s)", path, pageName, message));
          return null;
        }

        HttpGet httpCheckCreated = new HttpGet(targetHost + "/rest/private/wikiloader/check?path="
            + URLEncoder.encode(path + "/" + pageName, "UTF-8"));
        HttpResponse responseCheck2 = httpClient.execute(httpCheckCreated);
        int statusCodeRC2 = responseCheck2.getStatusLine().getStatusCode();
        responseCheck2.getEntity().consumeContent();
        if (statusCodeRC2 != 200) {
          log.warning(String.format("[Create] ERROR Created page %s/%s not found.", path, pageName));
          return pageName;
        }
        return pageName;
      } else if (statusCode == 200) {
        log.warning(String.format("[Create] WARNING page already exists : %s : %s/%s", statusCode, path, pageName));
        return null;
      }
    } catch (Exception exception) {
      log.warning(String.format("[Create] Error creating WIKI_PAGE : %s/%s", path, pageName));
    }
    return null;
  }

  public int getHttpStatusOfPageOnTarget(String path, String pageName) throws IOException {
    HttpGet httpCheck = new HttpGet(targetHost + "/rest/private/wikiloader/check?path=" + URLEncoder.encode(path + "/" + pageName, "UTF-8"));
    HttpResponse responseCheck = httpClient.execute(httpCheck);
    int statusCode = responseCheck.getStatusLine().getStatusCode();
    responseCheck.getEntity().consumeContent();
    return statusCode;
  }

  public boolean transfertContent(String content, String path) {
    try {
      HttpPost httppost = new HttpPost(targetHost + "/rest/private/wikiloader/content");
      List<NameValuePair> formparams = new ArrayList<NameValuePair>();
      formparams.add(new BasicNameValuePair("path", path));
      formparams.add(new BasicNameValuePair("content", content));
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
      httppost.setEntity(entity);
      HttpResponse response = httpClient.execute(httppost);
      response.getEntity().consumeContent();
    } catch (Exception exception) {
      log.warning("[Transfert] Error uploading content to : " + path);
    }
    return true;
  }

  public boolean checkPageExists(String path) {
    try {
      HttpGet httpGet = new HttpGet(targetHost + "/rest/private/wikiloader/pageurl?path=" + URLEncoder.encode(path, "UTF-8"));
      HttpResponse responseGet = httpClient.execute(httpGet);
      responseGet.getEntity().consumeContent();
      return responseGet.getStatusLine().getStatusCode() == 200;
    } catch (Exception exception) {
      log.warning("[URL] Error getting Url for : " + path);
    }
    return false;
  }

  public void uploadAttachment(String targetSpace, String pageName, String attachmentName, String contentType, byte[] data) {
    try {
      httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
      HttpPost post = new HttpPost(targetHost + "/rest/wikiloader/upload/" + targetSpace + "/" + pageName + "/");
      MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      multipartEntity.addPart("File", new InputStreamBody(new ByteArrayInputStream(data), contentType, attachmentName));
      post.setEntity(multipartEntity);

      HttpResponse response = httpClient.execute(post);
      String result = IOUtils.toString(response.getEntity().getContent());
      response.getEntity().consumeContent();
    } catch (Exception exception) {
      log.warning("Upload attachment fail");
    }
  }

  /**
   * Change the name of a page to an acceptable one for the wiki engine
   * 
   * @param title
   * @param replacedChars
   * @param replacementChar
   * @return new title
   */
  public static String normalizePageName(String title, String replacedChars, char replacementChar) {
    String newContent = title;
    for (int i = 0; i < replacedChars.length(); i++) {
      newContent = newContent.replace(replacedChars.charAt(i), replacementChar);
    }
    return newContent;
  }
}