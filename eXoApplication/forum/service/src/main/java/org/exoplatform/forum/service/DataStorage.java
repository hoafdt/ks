/*
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
 */
package org.exoplatform.forum.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.forum.service.conf.InitializeForumPlugin;
import org.exoplatform.forum.service.conf.SendMessageInfo;
import org.exoplatform.ks.common.bbcode.InitBBCodePlugin;
import org.exoplatform.ks.common.conf.RoleRulesPlugin;
import org.exoplatform.ks.common.jcr.KSDataLocation;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Oct 17, 2009  
 */
public interface DataStorage {

  @Managed
  @ManagedDescription("repository for forum storage")
  String getRepository() throws Exception;

  @Managed
  @ManagedDescription("workspace for the forum storage")
  String getWorkspace() throws Exception;

  @Managed
  @ManagedDescription("data path for forum storage")
  String getPath() throws Exception;

  void addPlugin(ComponentPlugin plugin) throws Exception;

  void addRolePlugin(ComponentPlugin plugin) throws Exception;

  void addInitialDataPlugin(ComponentPlugin plugin) throws Exception;

  void addInitRssPlugin(ComponentPlugin plugin) throws Exception;

  void addRSSEventListenner() throws Exception;

  void addCalculateModeratorEventListenner() throws Exception;

  void initCategoryListener();

  boolean isAdminRole(String userName) throws Exception;

  void start();

  void setDefaultAvatar(String userName) throws Exception;

  ForumAttachment getUserAvatar(String userName) throws Exception;

  void saveUserAvatar(String userId, ForumAttachment fileAttachment) throws Exception;

  void saveForumAdministration(ForumAdministration forumAdministration) throws Exception;

  ForumAdministration getForumAdministration() throws Exception;

  SortSettings getForumSortSettings() throws Exception;

  SortSettings getTopicSortSettings() throws Exception;

  List<Category> getCategories() throws Exception;

  Category getCategory(String categoryId) throws Exception;

  String[] getPermissionTopicByCategory(String categoryId, String type) throws Exception;

  void saveCategory(Category category, boolean isNew) throws Exception;

  void saveModOfCategory(List<String> moderatorCate, String userId, boolean isAdd);

  void calculateModerator(String nodePath, boolean isNew) throws Exception;

  void registerListenerForCategory(String path) throws Exception;

  void unRegisterListenerForCategory(String path) throws Exception;

  Category removeCategory(String categoryId) throws Exception;

  List<Forum> getForums(String categoryId, String strQuery) throws Exception;

  List<Forum> getForumSummaries(String categoryId, String strQuery) throws Exception;

  Forum getForum(String categoryId, String forumId) throws Exception;

  void modifyForum(Forum forum, int type) throws Exception;

  void saveForum(String categoryId, Forum forum, boolean isNew) throws Exception;

  void saveModerateOfForums(List<String> forumPaths, String userName, boolean isDelete) throws Exception;

  Forum removeForum(String categoryId, String forumId) throws Exception;

  void moveForum(List<Forum> forums, String destCategoryPath) throws Exception;

  JCRPageList getPageTopic(String categoryId, String forumId, String strQuery, String strOrderBy) throws Exception;

  LazyPageList<Topic> getTopicList(String categoryId,
                                   String forumId,
                                   String xpathConditions,
                                   String strOrderBy,
                                   int pageSize) throws Exception;

  List<Topic> getTopics(String categoryId, String forumId) throws Exception;

  Topic getTopic(String categoryId, String forumId, String topicId, String userRead) throws Exception;

  Topic getTopicSummary(String topicPath, boolean isLastPost) throws Exception;

  Topic getTopicByPath(String topicPath, boolean isLastPost) throws Exception;

  JCRPageList getPageTopicOld(long date, String forumPatch) throws Exception;

  List<Topic> getAllTopicsOld(long date, String forumPatch) throws Exception;

  long getTotalTopicOld(long date, String forumPatch);

  JCRPageList getPageTopicByUser(String userName, boolean isMod, String strOrderBy) throws Exception;

  void modifyTopic(List<Topic> topics, int type) throws Exception;

  void saveTopic(String categoryId,
                 String forumId,
                 Topic topic,
                 boolean isNew,
                 boolean isMove,
                 String defaultEmailContent) throws Exception;

  Topic removeTopic(String categoryId, String forumId, String topicId) throws Exception;

  void moveTopic(List<Topic> topics, String destForumPath, String mailContent, String link) throws Exception;

  long getLastReadIndex(String path, String isApproved, String isHidden, String userLogin) throws Exception;

  JCRPageList getPosts(String categoryId,
                       String forumId,
                       String topicId,
                       String isApproved,
                       String isHidden,
                       String strQuery,
                       String userLogin) throws Exception;

  long getAvailablePost(String categoryId,
                        String forumId,
                        String topicId,
                        String isApproved,
                        String isHidden,
                        String userLogin) throws Exception;

  JCRPageList getPagePostByUser(String userName, String userId, boolean isMod, String strOrderBy) throws Exception;

  Post getPost(String categoryId, String forumId, String topicId, String postId) throws Exception;

  JCRPageList getListPostsByIP(String ip, String strOrderBy) throws Exception;

  void savePost(String categoryId,
                String forumId,
                String topicId,
                Post post,
                boolean isNew,
                String defaultEmailContent) throws Exception;

  void modifyPost(List<Post> posts, int type) throws Exception;

  Post removePost(String categoryId, String forumId, String topicId, String postId) throws Exception;

  void movePost(List<Post> posts,
                String destTopicPath,
                boolean isCreatNewTopic,
                String mailContent,
                String link) throws Exception;

  Poll getPoll(String categoryId, String forumId, String topicId) throws Exception;

  Poll removePoll(String categoryId, String forumId, String topicId) throws Exception;

  void savePoll(String categoryId,
                String forumId,
                String topicId,
                Poll poll,
                boolean isNew,
                boolean isVote) throws Exception;

  void setClosedPoll(String categoryId, String forumId, String topicId, Poll poll) throws Exception;

  void addTag(List<Tag> tags, String userName, String topicPath) throws Exception;

  void unTag(String tagId, String userName, String topicPath) throws Exception;

  Tag getTag(String tagId) throws Exception;

  List<String> getTagNameInTopic(String userAndTopicId) throws Exception;

  List<String> getAllTagName(String keyValue, String userAndTopicId) throws Exception;

  List<Tag> getAllTags() throws Exception;

  List<Tag> getMyTagInTopic(String[] tagIds) throws Exception;

  JCRPageList getTopicByMyTag(String userIdAndtagId, String strOrderBy) throws Exception;

  void saveTag(Tag newTag) throws Exception;

  JCRPageList getPageListUserProfile() throws Exception;

  JCRPageList searchUserProfile(String userSearch) throws Exception;

  UserProfile getDefaultUserProfile(String userName, String ip) throws Exception;

  UserProfile updateUserProfileSetting(UserProfile userProfile) throws Exception;

  String getScreenName(String userName) throws Exception;

  UserProfile getUserSettingProfile(String userName) throws Exception;

  void saveUserSettingProfile(UserProfile userProfile) throws Exception;

  UserProfile getLastPostIdRead(UserProfile userProfile, String isOfForum) throws Exception;

  void saveLastPostIdRead(String userId, String[] lastReadPostOfForum, String[] lastReadPostOfTopic) throws Exception;

  List<String> getUserModerator(String userName, boolean isModeCate) throws Exception;

  void saveUserModerator(String userName, List<String> ids, boolean isModeCate) throws Exception;

  UserProfile getUserInfo(String userName) throws Exception;

  List<UserProfile> getQuickProfiles(List<String> userList) throws Exception;

  UserProfile getQuickProfile(String userName) throws Exception;

  UserProfile getUserInformations(UserProfile userProfile) throws Exception;

  void saveUserProfile(UserProfile newUserProfile, boolean isOption, boolean isBan) throws Exception;

  UserProfile getUserProfileManagement(String userName) throws Exception;

  void saveUserBookmark(String userName, String bookMark, boolean isNew) throws Exception;

  void saveCollapsedCategories(String userName, String categoryId, boolean isAdd) throws Exception;

  void saveReadMessage(String messageId, String userName, String type) throws Exception;

  JCRPageList getPrivateMessage(String userName, String type) throws Exception;

  long getNewPrivateMessage(String userName) throws Exception;

  void savePrivateMessage(ForumPrivateMessage privateMessage) throws Exception;

  void removePrivateMessage(String messageId, String userName, String type) throws Exception;

  ForumSubscription getForumSubscription(String userId) throws Exception;

  void saveForumSubscription(ForumSubscription forumSubscription, String userId) throws Exception;

  ForumStatistic getForumStatistic() throws Exception;

  void saveForumStatistic(ForumStatistic forumStatistic) throws Exception;

  Calendar getGreenwichMeanTime();

  Object getObjectNameByPath(String path) throws Exception;

  Object getObjectNameById(String id, String type) throws Exception;

  List<ForumLinkData> getAllLink(String strQueryCate, String strQueryForum) throws Exception;

  List<ForumSearch> getQuickSearch(String textQuery,
                                   String type_,
                                   String pathQuery,
                                   String userId,
                                   List<String> listCateIds,
                                   List<String> listForumIds,
                                   List<String> forumIdsOfModerator) throws Exception;

  List<ForumSearch> getAdvancedSearch(ForumEventQuery eventQuery,
                                      List<String> listCateIds,
                                      List<String> listForumIds) throws Exception;

  void addWatch(int watchType, String path, List<String> values, String currentUser) throws Exception;

  void removeWatch(int watchType, String path, String values) throws Exception;

  void updateEmailWatch(List<String> listNodeId, String newEmailAdd, String userId) throws Exception;

  List<Watch> getWatchByUser(String userId) throws Exception;

  void updateForum(String path) throws Exception;

  SendMessageInfo getMessageInfo(String name) throws Exception;

  List<ForumSearch> getJobWattingForModerator(String[] paths) throws Exception;

  int getJobWattingForModeratorByUser(String userId) throws Exception;

  //	TODO: JUnit test is fall.
  void getTotalJobWatting(List<String> userIds);

  NodeIterator search(String queryString) throws Exception;

  void evaluateActiveUsers(String query) throws Exception;

  Object exportXML(String categoryId,
                   String forumId,
                   List<String> objectIds,
                   String nodePath,
                   ByteArrayOutputStream bos,
                   boolean isExportAll) throws Exception;

  void importXML(String nodePath, ByteArrayInputStream bis, int typeImport) throws Exception;

  void updateDataImported() throws Exception;

  void updateTopicAccess(String userId, String topicId) throws Exception;

  void updateForumAccess(String userId, String forumId) throws Exception;

  List<String> getBookmarks(String userName) throws Exception;

  List<String> getBanList() throws Exception;

  boolean addBanIP(String ip) throws Exception;

  void removeBan(String ip) throws Exception;

  List<String> getForumBanList(String forumId) throws Exception;

  boolean addBanIPForum(String ip, String forumId) throws Exception;

  void removeBanIPForum(String ip, String forumId) throws Exception;

  void updateStatisticCounts(long topicCount, long postCount) throws Exception;

  PruneSetting getPruneSetting(String forumPath) throws Exception;

  List<PruneSetting> getAllPruneSetting() throws Exception;

  void savePruneSetting(PruneSetting pruneSetting) throws Exception;

  void runPrune(String forumPath) throws Exception;

  void runPrune(PruneSetting pSetting) throws Exception;

  long checkPrune(PruneSetting pSetting) throws Exception;

  List<TopicType> getTopicTypes() throws Exception;

  TopicType getTopicType(String Id) throws Exception;

  void saveTopicType(TopicType topicType) throws Exception;

  void removeTopicType(String topicTypeId) throws Exception;

  JCRPageList getPageTopicByType(String type) throws Exception;
  
  /**
   * Create or update a forum user profile
   * @param user user whose profile must be created
   * @param isNew
   */
  public void populateUserProfile(User user, boolean isNew) throws Exception;;
  public void deleteUserProfile(User user) throws Exception;

  void initDefaultData() throws Exception;

  List<RoleRulesPlugin> getRulesPlugins();

  List<InitializeForumPlugin> getDefaultPlugins();

  List<InitBBCodePlugin> getDefaultBBCodePlugins();

  void initAutoPruneSchedules() throws Exception;

  void updateLastLoginDate(String userId) throws Exception;

  List<Post> getNewPosts(int number) throws Exception;

  Map<String, String> getServerConfig_();

  KSDataLocation getDataLocation();


}