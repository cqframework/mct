package org.opencds.cqf.mct;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

   private static ApplicationContext context;

   public static <T> T getBean(Class<T> beanClass) {
      return context.getBean(beanClass);
   }

   public static <T> T getBean(String beanName, Class<T> beanClass) {
      return context.getBean(beanName, beanClass);
   }

   @Override
   public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
      setContext(context);
   }

   private static synchronized void setContext(ApplicationContext context) {
      SpringContext.context = context;
   }
}
