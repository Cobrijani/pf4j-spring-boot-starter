package com.github.cobrijani.pf4jspringbootstarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PF4JAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(SpringPluginManager.class)
  public SpringPluginManager pluginManager() {
    return new SpringPluginManager();
  }
}
