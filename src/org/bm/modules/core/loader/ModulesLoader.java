package org.bm.modules.core.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.bm.modules.shared.IModule;

public class ModulesLoader {
    private static ClassLoader classLoader;

    private static Collection<IModule> loadedModules;

    public static Collection<IModule> loadModules() {
        if (loadedModules == null) {
            loadedModules = reloadModules();
        }

        return loadedModules;
    }

    public static Collection<IModule> reloadModules() {
        List<IModule> modulesList = new ArrayList<IModule>();

        File[] files = new File("modules").listFiles(new ModulesFilter());
        final List<URL> modulesFilesURL = new ArrayList<URL>();
        List<String> modulesClassnames = new ArrayList<String>();
        for (File moduleFile : files) {
            JarFile jarFile = null;

            try {
                jarFile = new JarFile(moduleFile);

                Manifest manifest = jarFile.getManifest();

                String moduleClassname = manifest.getMainAttributes().getValue("Module-Class");

                modulesClassnames.add(moduleClassname);
                modulesFilesURL.add(moduleFile.toURI().toURL());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                classLoader = new URLClassLoader(modulesFilesURL.toArray(new URL[modulesFilesURL.size()]), ModulesLoader.class
                        .getClassLoader());

                return null;
            }
        });

        if (classLoader != null) {
            for (String clazz : modulesClassnames) {

                try {
                    Class<?> moduleClass = Class.forName(clazz, true, classLoader);

                    if (IModule.class.isAssignableFrom(moduleClass)) {
                        @SuppressWarnings("unchecked")
                        Class<IModule> castedClass = (Class<IModule>) moduleClass;

                        IModule module = castedClass.newInstance();

                        modulesList.add(module);
                    }

                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

        return modulesList;
    }

    static class ModulesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar");
        }
    }

}
