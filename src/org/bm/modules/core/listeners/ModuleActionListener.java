package org.bm.modules.core.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.bm.modules.shared.IModule;
import org.bm.modules.shared.IWindowsManager;
import org.bm.modules.shared.ModuleFrame;

public class ModuleActionListener implements ActionListener {
   private final IWindowsManager windowsManager;

   private final IModule module;

   public ModuleActionListener(IWindowsManager windowsManager, IModule module) {
      this.windowsManager = windowsManager;
      this.module = module;

   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (module.isActive()) {
         module.attach();
         final ModuleFrame moduleFrame = module.getModuleFrame();
         moduleFrame.setTitle(module.getName());
         if (moduleFrame != null) {
            windowsManager.addWindow(moduleFrame);
            windowsManager.centerOnContainer(moduleFrame);
         }
      } else {
         JOptionPane.showMessageDialog(null, "Module is disabled", "Error", JOptionPane.ERROR_MESSAGE);
      }

   }

}
