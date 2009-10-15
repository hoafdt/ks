package org.exoplatform.forum.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ks.common.conf.ManagedPlugin;
import org.exoplatform.ks.common.conf.RoleRulesPlugin;
import org.exoplatform.ks.common.user.ContactProvider;
import org.exoplatform.management.ManagementAware;
import org.exoplatform.management.ManagementContext;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.services.log.ExoLogger;

@Managed
@NameTemplate(@Property(key="service", value="forum"))
@ManagedDescription("Forum management")
public class ForumServiceManaged implements ManagementAware {
  
  private static final Log log = ExoLogger.getLogger(ForumServiceManaged.class);

  private ForumServiceImpl  forumService;

  private ManagementContext context;

  public ForumServiceManaged(ForumServiceImpl forumService) {
    this.forumService = forumService;
    this.forumService.managed = this;
  }

  public void setContext(ManagementContext context) {
    this.context = context;
  }

  @Managed
  @ManagedDescription("list of currently connected users")
  public List<String> getOnlineUsers() throws Exception {
    return forumService.onlineUserList_;
  }
  
  @Managed
  @ManagedDescription("number of currently connected users")
  public int countOnlineUsers() throws Exception {
    return forumService.onlineUserList_.size();
  }
  
  @Managed
  @ManagedDescription("rules that define administrators")
  public List<String> getAdminRules() {
    List<String> adminRules = new ArrayList<String>();
    List<RoleRulesPlugin>  plugins = forumService.storage_.rulesPlugins_;
    
    for (RoleRulesPlugin plugin : plugins) {
      Collection<List<String>> allrules = plugin.getAllRules().values();
      for (List<String> rules : allrules) {
        if (rules != null) {
          adminRules.addAll(rules);
        }
      }
    }
    return adminRules;
  }
  
  @Managed
  @ManagedDescription("evaluate is a user has administrator role")
  public boolean hasForumAdminRole(String username) throws Exception {
    return forumService.storage_.isAdminRole(username);
  }
  
  
  @Managed
  @ManagedDescription("recalculate forum statistics")
  public void updateStatistics() throws Exception {
    forumService.updateForumStatistic();
  }

  @Managed
  @ManagedDescription("get the configuration of the mail service used for notifications in KS")
  public Map<String,String> getMailServiceConfig() {
    return forumService.storage_.serverConfig_;
  }
  
  @Managed
  @ManagedDescription("Get the ContactProvider implementation")
  public String getContactProvider() {
    return ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ContactProvider.class).toString();
  }
  
  @Managed
  @ManagedDescription("Set the ContactProvider implementation")
  public void setContactProvider(String fqn) {
    Object instance = null;
    try {
    instance = Class.forName(fqn);
    }
    catch(Exception e) {
      log.error("Failed to register contact provider for " + fqn  + ": " + e.getMessage());
      return;
    }
    ExoContainerContext.getContainerByName("portal").registerComponentInstance(ContactProvider.class, instance);
  }
  
  
  public void registerPlugin(ManagedPlugin plugin) {
    if (context != null) {
      context.register(plugin);
    }
 
  }

  public void registerStorageManager(JCRDataStorage storage) {
    if (context != null) {
      context.register(storage);
    }
  }

  public void registerJobManager(JobManager jobManager) {
    if (context != null) {
      context.register(jobManager);
    }
  }

}
