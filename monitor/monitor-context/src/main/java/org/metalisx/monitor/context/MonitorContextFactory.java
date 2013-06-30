package org.metalisx.monitor.context;

/**
 * Factory for creating and returning an implementation of
 * {@link InterfaceMonitorContext}. By default an instance of the
 * {@link MonitorContextSimple} located in this artifact is returned. However if
 * a class with this package and the name MonitorContext exists then a new
 * instance of this class will be returned. The MonitorContext class should
 * extend the InterfaceMonitorContext. A simple approach to create your own
 * implementation is to extend the SimpleMonitorContext. One implementation
 * exposes the MonitorContext properties in the MDC context of slf4j.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class MonitorContextFactory {

    private static ThreadLocal<InterfaceMonitorContext> instance = new ThreadLocal<InterfaceMonitorContext>();

    private static String monitorContextClassName = MonitorContextFactory.class.getPackage().getName() + ".MonitorContext";

    private MonitorContextFactory() {
    }

    public static InterfaceMonitorContext getCurrentInstance() {
        return instance.get();
    }

    public static InterfaceMonitorContext getInstance(String key) {
        if (instance.get() == null) {
            try {
                InterfaceMonitorContext monitorContext = null;
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(monitorContextClassName);
                } catch (ClassNotFoundException e) {
                    // silent
                }
                if (clazz != null) {
                    monitorContext = (InterfaceMonitorContext) clazz.newInstance();
                } else {
                    monitorContext = new MonitorContextSimple();
                }
                monitorContext.setKey(key);
                instance.set(monitorContext);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new IllegalStateException(e.getMessage());
            }
        }
        return instance.get();
    }
    
    public static void clear(String key) {
    	if (instance.get() != null && instance.get().getKey().equals(key)) {
	        if (instance.get() != null) {
	            instance.get().clear();
	        }
	        instance.remove();
    	}
    }

}
