package org.bm.modules.core;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.bm.modules.core.criteria.Arrays;
import org.bm.modules.core.criteria.Function;
import org.bm.modules.core.i18n.Messages;
import org.bm.modules.core.listeners.ModuleActionListener;
import org.bm.modules.core.listeners.PutToFrontModuleFrameListener;
import org.bm.modules.core.loader.ModulesLoader;
import org.bm.modules.shared.IModule;
import org.bm.modules.shared.ModuleFrame;
import org.bm.modules.shared.ModuleFrameListener;

import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;

public class ModulesFrame extends JFrame {

   private final JDesktopPane desktopPane = new JDesktopPane();

   private ComponentContainer componentContainer;

   private JMenu menuWindows;

   private Collection<IModule> loadedModules;

   public ModulesFrame() throws HeadlessException {
      super();
      init();
   }

   public ModulesFrame(GraphicsConfiguration gc) {
      super(gc);
      init();
   }

   public ModulesFrame(String title, GraphicsConfiguration gc) {
      super(title, gc);
      init();
   }

   public ModulesFrame(String title) throws HeadlessException {
      super(title);
      init();
   }

   private void init() {
      initLaF();
      componentContainer = new ComponentContainer();
      componentContainer.setWindowManager(new WindowsManager(desktopPane));

      this.setContentPane(desktopPane);

      loadedModules = ModulesLoader.loadModules();
      for (IModule m : loadedModules) {
         if (null != m.getModuleFrame()) {
            m.getModuleFrame().setComponentContainer(componentContainer);
         }
      }

      createMenuBar();

      componentContainer.getWindowManager().addModuleFrameListener(new ModuleFrameListener() {
         @Override
         public void windowRemoved(ModuleFrame frame) {
            updateWindowsMenu();
         }

         @Override
         public void windowAdded(ModuleFrame frame) {
            updateWindowsMenu();
         }
      });

      displayAndCenterOnScreen();
   }

   private void initLaF() {
      try {
         UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void updateWindowsMenu() {

      menuWindows.removeAll();

      populateMenu(menuWindows, IModule.MENU_WINDOWS);

      menuWindows.addSeparator();

      int index = 1;
      for (ModuleFrame frame : componentContainer.getWindowManager().getWindows()) {
         JMenuItem menuItem = new JMenuItem(index + ". " + frame.getTitle());
         menuWindows.add(menuItem);
         menuItem.addActionListener(new PutToFrontModuleFrameListener(componentContainer.getWindowManager(), frame));
         index++;
      }

      menuWindows.revalidate();

   }

   private void createMenuBar() {
      JMenuBar menubar = new JMenuBar();

      JMenu menuFile = new JMenu(Messages.getString("ModulesFrame.menuFile.title")); //$NON-NLS-1$
      menuFile.setMnemonic(KeyEvent.VK_F);

      populateMenu(menuFile, IModule.MENU_FILE);
      addExitMenuItem(menuFile);

      JMenu menuOptions = new JMenu(Messages.getString("ModulesFrame.menuOptions.title")); //$NON-NLS-1$
      menuOptions.setMnemonic(KeyEvent.VK_O);
      populateMenu(menuOptions, IModule.MENU_OPTIONS);

      menuWindows = new JMenu(Messages.getString("ModulesFrame.menuWindows.title"));
      menuWindows.setMnemonic(KeyEvent.VK_W);
      populateMenu(menuWindows, IModule.MENU_WINDOWS);

      JMenu menuHelp = new JMenu(Messages.getString("ModulesFrame.menuHelp.title")); //$NON-NLS-1$
      menuHelp.setMnemonic(KeyEvent.VK_H);
      populateMenu(menuHelp, IModule.MENU_HELP);

      menubar.add(menuFile);
      menubar.add(menuOptions);
      menubar.add(menuWindows);
      menubar.add(Box.createHorizontalGlue());
      menubar.add(menuHelp);

      this.setJMenuBar(menubar);
   }

   private void populateMenu(final JMenu menu, final int menuIndex) {
      // Keeps only file menu modules.
      Collection<IModule> modulesForMenu = Arrays.filter(false, loadedModules, new Function<IModule, IModule>() {
         @Override
         public IModule apply(IModule input) {
            if (input.getMenuIndex() == menuIndex) {
               return input;
            }
            return null;
         }
      });

      // sort modules.
      modulesForMenu = Arrays.sort(modulesForMenu, new Comparator<IModule>() {
         @Override
         public int compare(IModule o1, IModule o2) {
            if (o1.getMenuIndex() < o2.getMenuIndex()) {
               return -1;
            } else if (o1.getMenuIndex() > o2.getMenuIndex()) {
               return 1;
            }

            return 0;
         }
      });

      for (IModule module : modulesForMenu) {
         JMenuItem menuItem = new JMenuItem(module.getName());
         if (module.hasMnemonic()) {
            menuItem.setMnemonic(module.getMnemonic());
         }
         if (module.hasAccelerator()) {
            menuItem.setAccelerator(module.getAccelerator());
         }

         menuItem.addActionListener(new ModuleActionListener(componentContainer.getWindowManager(), module));

         menu.add(menuItem);
      }
   }

   private void addExitMenuItem(JMenu menu) {

      menu.addSeparator();

      JMenuItem menuItemExit = new JMenuItem("Exit");
      menuItemExit.setMnemonic(KeyEvent.VK_X);
      menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
      menuItemExit.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            ModulesFrame.this.dispose();
         }
      });

      menu.add(menuItemExit);

   }

   private void displayAndCenterOnScreen() {
      Toolkit tk = Toolkit.getDefaultToolkit();
      Dimension screenSize = tk.getScreenSize();
      Insets screenInsets = tk.getScreenInsets(this.getGraphicsConfiguration());

      int x = 0 + screenInsets.left;
      int y = 0 + screenInsets.top;
      int width = (int) screenSize.getWidth() - screenInsets.right - screenInsets.left;
      int height = (int) screenSize.getHeight() - screenInsets.bottom - screenInsets.top;

      this.setBounds(x, y, width, height);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setVisible(true);
   }

}
