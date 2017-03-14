/**
 * This file is part of choco-gui, https://github.com/chocoteam/choco-gui
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.gui.panels;

import org.chocosolver.gui.GUI;
import org.jfree.chart.ChartPanel;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public abstract class APanel extends ChartPanel {

    Solver solver;
    Model model;
    GUI frame;
    boolean flush;
    boolean activate;

    public APanel(GUI frame) {
        super(null);
        this.frame = frame;
        this.solver = frame.getSolver();
        this.model = frame.getModel();
    }

    public void plug(JTabbedPane tabbedpanel){
        activate = true;
    }

    public void unplug(JTabbedPane tabbedpanel) {
        tabbedpanel.remove(this);
        activate = false;
    }

    public final void flushData() {
        flush = true;
    }

    protected final void flushDone() {
        flush = false;
    }
}
