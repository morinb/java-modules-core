package org.bm.modules.core.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import org.bm.modules.core.WindowsManager;
import org.bm.modules.shared.ModuleFrame;

public class PutToFrontModuleFrameListener implements ActionListener {

   private final ModuleFrame frame;

   private final WindowsManager wm;

   public PutToFrontModuleFrameListener(WindowsManager wm, ModuleFrame frame) {
      this.frame = frame;
      this.wm = wm;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (frame.isIcon()) {
         try {
            frame.setIcon(false);
         } catch (PropertyVetoException e1) {}
      }

      wm.setSelectedWindow(frame);
      frame.toFront();

   }

}
