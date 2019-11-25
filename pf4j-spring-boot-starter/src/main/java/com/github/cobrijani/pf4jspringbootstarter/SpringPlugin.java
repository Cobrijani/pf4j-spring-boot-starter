package com.github.cobrijani.pf4jspringbootstarter;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class SpringPlugin extends Plugin {

  private ApplicationContext applicationContext;

  public SpringPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  public final ApplicationContext getApplicationContext() {
    if (applicationContext == null) {
      applicationContext = createApplicationContext();
    }

    return applicationContext;
  }

  @Override
  public void stop() {
    // close applicationContext
    if ((applicationContext != null) && (applicationContext instanceof ConfigurableApplicationContext)) {
      ((ConfigurableApplicationContext) applicationContext).close();
    }
  }

  protected abstract ApplicationContext createApplicationContext();

}
