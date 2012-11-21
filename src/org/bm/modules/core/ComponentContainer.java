package org.bm.modules.core;

import java.util.Collection;

import org.bm.modules.core.loader.ModulesLoader;
import org.bm.modules.shared.IComponentContainer;
import org.bm.modules.shared.IModule;
import org.bm.modules.shared.IWindowsManager;

public class ComponentContainer implements IComponentContainer {
   private IWindowsManager wm;

   @Override
   public IWindowsManager getWindowManager() {
      return wm;
   }

   @Override
   public void setWindowManager(IWindowsManager wm) {
      this.wm = wm;
   }

   @Override
   public Collection<IModule> getModulesList() {
      return ModulesLoader.loadModules();
   }

}
