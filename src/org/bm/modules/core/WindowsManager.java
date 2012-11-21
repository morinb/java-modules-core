package org.bm.modules.core;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.bm.modules.shared.IWindowsManager;
import org.bm.modules.shared.ModuleFrame;
import org.bm.modules.shared.ModuleFrameListener;

public class WindowsManager implements IWindowsManager {
	private final JDesktopPane desktopPane;

	private final List<ModuleFrame> windows;

	private final EventListenerList listeners;

	public WindowsManager(JDesktopPane desktopPane) {
		this.desktopPane = desktopPane;
		windows = new ArrayList<ModuleFrame>();
		listeners = new EventListenerList();
	}

	@Override
	public void addWindow(final ModuleFrame w) {

		if(windows.contains(w)) {
			return;
		}
		
		desktopPane.add(w);
		windows.add(w);

		// To allow removing of the frame when closing it.
		w.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				super.internalFrameClosing(e);
				WindowsManager.this.removeWindow(w);
			}
		});
		w.setVisible(true);
		fireModuleFrameAdded(w);
	}

	@Override
	public void removeWindow(ModuleFrame w) {
		desktopPane.remove(w);
		windows.remove(w);
		w.setVisible(false);
		fireModuleFrameRemoved(w);
	}

	public void addModuleFrameListener(ModuleFrameListener l) {
		listeners.add(ModuleFrameListener.class, l);
	}

	public void removeModuleFrameListener(ModuleFrameListener l) {
		listeners.remove(ModuleFrameListener.class, l);
	}

	public void fireModuleFrameAdded(ModuleFrame frame) {
		for (ModuleFrameListener l : listeners
				.getListeners(ModuleFrameListener.class)) {
			l.windowAdded(frame);
		}
	}

	public void fireModuleFrameRemoved(ModuleFrame frame) {
		for (ModuleFrameListener l : listeners
				.getListeners(ModuleFrameListener.class)) {
			l.windowRemoved(frame);
		}
	}

	@Override
	public List<ModuleFrame> getWindows() {
		return windows;
	}

	public void setSelectedWindow(ModuleFrame frame) {

		try {
			for (ModuleFrame f : getWindows()) {
				f.setSelected(false);
			}
			desktopPane.setSelectedFrame(frame);
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
