package com.github.cobrijani.pf4jspringbootstarter;

import java.util.List;
import java.util.Set;

import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

public class ExtensionsInjector {
  private static final Logger log = LoggerFactory.getLogger(ExtensionsInjector.class);

  protected final PluginManager pluginManager;
  protected final AbstractAutowireCapableBeanFactory beanFactory;

  public ExtensionsInjector(PluginManager pluginManager, AbstractAutowireCapableBeanFactory beanFactory) {
    this.pluginManager = pluginManager;
    this.beanFactory = beanFactory;
  }

  public void injectExtensions() {
    // add extensions from classpath (non plugin)
    Set<String> extensionClassNames = pluginManager.getExtensionClassNames(null);
    for (String extensionClassName : extensionClassNames) {
      try {
        log.debug("Register extension '{}' as bean", extensionClassName);
        Class<?> extensionClass = getClass().getClassLoader().loadClass(extensionClassName);
        registerExtension(extensionClass);
      }
      catch (ClassNotFoundException e) {
        log.error(e.getMessage(), e);
      }
    }

    // add extensions for each started plugin
    List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
    for (PluginWrapper plugin : startedPlugins) {
      log.debug("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
      extensionClassNames = pluginManager.getExtensionClassNames(plugin.getPluginId());
      for (String extensionClassName : extensionClassNames) {
        try {
          log.debug("Register extension '{}' as bean", extensionClassName);
          Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
          registerExtension(extensionClass);
        }
        catch (ClassNotFoundException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  /**
   * Register an extension as bean. Current implementation register extension as singleton using
   * {@code beanFactory.registerSingleton()}. The extension instance is created using
   * {@code pluginManager.getExtensionFactory().create(extensionClass)}. The bean name is the extension class name. Override this
   * method if you wish other register strategy.
   */
  protected void registerExtension(Class<?> extensionClass) {
    Object extension = pluginManager.getExtensionFactory().create(extensionClass);
    beanFactory.registerSingleton(extension.getClass().getName(), extension);
  }
}
