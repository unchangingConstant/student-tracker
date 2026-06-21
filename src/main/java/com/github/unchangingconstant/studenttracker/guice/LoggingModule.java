package com.github.unchangingconstant.studenttracker.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 * Logging Policy:
 *
 * -    Both gui and app packages get 1 MB of logging cache.
 * -    For method call, the following is printed:
 *      -   The arguments passed into it and their types
 * -    Each argument printed is not to exceed 100 characters.
 */
public class LoggingModule extends AbstractModule {

    private static final Executor loggingThread = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    // Ensures thread doesn't keep running once GUI is closed
                    thread.setDaemon(true);
                    return thread;
                }
            });;

    // Chat, is this efficiency?
    protected void configure() {
        var loggerInterceptor = new LoggerInterceptor();
        bindInterceptor(
                Matchers.inSubpackage("com.github.unchangingconstant.studenttracker.app"),
                new PackageMatcher("com.github.unchangingconstant.studenttracker.app"),
                loggerInterceptor);
        bindInterceptor(
                Matchers.inSubpackage("com.github.unchangingconstant.studenttracker.gui"),
                new PackageMatcher("com.github.unchangingconstant.studenttracker.gui"),
                loggerInterceptor);
    }

    static class LoggerInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {

            var args = methodInvocation.getArguments();

            // Method call print format
            var methodFormat = "Method Call: %s\n";
            var argFormat = "\t%s\n";

            // Array.stream doesn't print properly
            var logString = String.format(methodFormat, methodInvocation.getMethod().toString()) +
                    Arrays.stream(args).map((arg) -> String.format(argFormat, arg.toString().substring(0, 100)));

            loggingThread.execute(() -> System.out.println(logString));

            return methodInvocation.proceed();
        }
    }

    /*
     * Guice-provided matches will match all methods declared in super classes.
     * This matcher will ensure the method had to be declared in the target subclass.
     * Example:
     *
     * Let's say we try to match method declared under:
     *
     * StudentButton extends javafx.button
     *
     * Guice will intercept methods declared in StudentButton as well as javafx.button.
     *
     * This matcher will only intercept methods declared in StudentButton.
     */
    static class PackageMatcher implements Matcher<Method> {

        private final String matchingPackageStr;

        PackageMatcher(String matchingPackageStr) {
            this.matchingPackageStr = matchingPackageStr;
        }

        @Override
        public boolean matches(Method method) {
            var incomingMethodClassStr = method.getDeclaringClass().getPackage().getName();
            return incomingMethodClassStr.startsWith(matchingPackageStr + ".");
        }
    }

}
