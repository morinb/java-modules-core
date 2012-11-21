package org.bm.modules.core.listeners;

import java.util.EventListener;

import org.bm.modules.shared.ModuleFrame;

public interface ModuleFrameListener extends EventListener {
   public void windowAdded(ModuleFrame frame);

   public void windowRemoved(ModuleFrame frame);
}
