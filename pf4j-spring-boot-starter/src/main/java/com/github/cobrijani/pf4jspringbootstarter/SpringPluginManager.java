package com.github.cobrijani.pf4jspringbootstarter;

import java.nio.file.Path;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware, InitializingBean {

  private ApplicationContext applicationContext;

  public SpringPluginManager() {
  }

  public SpringPluginManager(Path pluginsRoot) {
    super(pluginsRoot);
  }

  @Override
  protected ExtensionFactory createExtensionFactory() {
    return new SpringExtensionFactory(this);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * This method load, start plugins and inject extensions in Spring
   */

  @Override
  public void afterPropertiesSet() throws Exception {
    loadPlugins();
    startPlugins();

    AbstractAutowireCapableBeanFactory beanFactory =
        (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    ExtensionsInjector extensionsInjector = new ExtensionsInjector(this, beanFactory);
    extensionsInjector.injectExtensions();
  }

}
