package ru.otus.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        final Object configInstance;
        try {
            configInstance = configClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot instantiate config class: " + configClass.getName(), e);
        }

        List<Method> componentMethods = new ArrayList<>();
        for (Method m : configClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(AppComponent.class)) {
                componentMethods.add(m);
            }
        }
        componentMethods.sort(
                Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()));

        for (Method method : componentMethods) {
            createComponent(configInstance, method);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void createComponent(Object configInstance, Method factoryMethod) {
        AppComponent meta = factoryMethod.getAnnotation(AppComponent.class);
        String name = meta.name();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("AppComponent name must not be empty: " + factoryMethod);
        }
        if (appComponentsByName.containsKey(name)) {
            throw new IllegalStateException("Duplicate component name: " + name);
        }

        if (!Modifier.isPublic(factoryMethod.getModifiers())) {
            throw new IllegalStateException("AppComponent factory method must be public: " + factoryMethod);
        }

        Class<?>[] paramTypes = factoryMethod.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = resolveByType(paramTypes[i], "Creating component '" + name + "'");
        }

        try {
            Object component = factoryMethod.invoke(configInstance, args);
            if (component == null) {
                throw new IllegalStateException("Factory method returned null for component: " + name);
            }
            appComponents.add(component);
            appComponentsByName.put(name, component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create component '" + name + "' via " + factoryMethod, e);
        }
    }

    private Object resolveByType(Class<?> requiredType, String ctx) {
        Object result = null;
        for (Object candidate : appComponents) {
            if (requiredType.isAssignableFrom(candidate.getClass())) {
                if (result != null) {
                    throw new IllegalStateException(
                            "Ambiguous dependency for type " + requiredType.getName() + ". " + ctx);
                }
                result = candidate;
            }
        }
        if (result == null) {
            throw new IllegalStateException("Unsatisfied dependency for type " + requiredType.getName() + ". " + ctx);
        }
        return result;
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        C result = null;
        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                if (result != null) {
                    throw new IllegalStateException(
                            "More than one component result for type: " + componentClass.getName());
                }
                result = (C) component;
            }
        }
        if (result == null) {
            throw new NoSuchElementException("No component result for type: " + componentClass.getName());
        }
        return result;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object bean = appComponentsByName.get(componentName);
        if (bean == null) {
            throw new NoSuchElementException("No component found with name: " + componentName);
        }
        return (C) bean;
    }
}
