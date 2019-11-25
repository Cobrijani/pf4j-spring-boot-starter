package com.github.cobrijani.pf4jspringbootstarter;

import org.pf4j.ExtensionFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class SpringExtensionFactory implements ExtensionFactory {

  private static final Logger log = LoggerFactory.getLogger(SpringExtensionFactory.class);

  private PluginManager pluginManager;
  private boolean autowire;

  public SpringExtensionFactory(PluginManager pluginManager) {
    this(pluginManager, true);
  }

  public SpringExtensionFactory(PluginManager pluginManager, boolean autowire) {
    this.pluginManager = pluginManager;
    this.autowire = autowire;
  }

  @Override
  public <T> T create(Class<T> extensionClass) {
    T extension = createWithoutSpring(extensionClass);
    if (autowire && extension != null) {
      // test for SpringBean
      PluginWrapper pluginWrapper = pluginManager.whichPlugin(extensionClass);
      if (pluginWrapper != null) {
        Plugin plugin = pluginWrapper.getPlugin();
        if (plugin instanceof SpringPlugin) {
          // autowire
          ApplicationContext pluginContext = ((SpringPlugin) plugin).getApplicationContext();
          pluginContext.getAutowireCapableBeanFactory().autowireBean(extension);
        }
      }
    }

    return extension;
  }

  @SuppressWarnings("unchecked")
  protected <T> T createWithoutSpring(Class<?> extensionClass) {
    try {
      return (T) extensionClass.newInstance();
    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

}
