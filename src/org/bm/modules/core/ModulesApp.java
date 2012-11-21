package org.bm.modules.core;

import javax.swing.SwingUtilities;

public class ModulesApp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ModulesFrame("Modules App");
            }
        });

    }

}
