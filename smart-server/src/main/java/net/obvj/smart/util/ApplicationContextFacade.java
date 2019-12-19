package net.obvj.smart.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A facade for Spring's Application Context, which holds the components defined in
 * S.M.A.R.T. base package.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ApplicationContextFacade
{
    private static ApplicationContext context = new AnnotationConfigApplicationContext("net.obvj.smart");

    private ApplicationContextFacade()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Return the bean instance that uniquely matches the given object type.
     * 
     * @param <T>
     * @param beanClass type the bean must match; can be an interface or superclass
     * @return an instance of the single bean matching the required type
     */
    public static <T> T getBean(Class<T> beanClass)
    {
        return context.getBean(beanClass);
    }
}
