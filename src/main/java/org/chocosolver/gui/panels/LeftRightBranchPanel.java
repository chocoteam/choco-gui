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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.chocosolver.solver.search.loop.monitors.IMonitorDownBranch;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.search.loop.monitors.IMonitorRestart;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class LeftRightBranchPanel extends APanel implements IMonitorDownBranch, IMonitorRestart, IMonitorOpenNode {
    XYSeries serie1, serie2;
    int counter;

    public LeftRightBranchPanel(GUI frame) {
        super(frame);
        serie1 = new XYSeries("Left-Right decisions");
        serie2 = new XYSeries("Depth");
        XYSeriesCollection scoll = new XYSeriesCollection();
        scoll.addSeries(serie1);
        scoll.addSeries(serie2);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "LR decisions", "Nodes", "Left-Right decisions", scoll);
        this.setChart(chart);
        solver.plugMonitor(this);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("LR decisions", this);
    }


    @Override
    public void beforeDownLeftBranch() {

    }

    @Override
    public void afterDownLeftBranch() {
        counter++;
        if (frame.canUpdate() && activate) {
            serie1.add(solver.getMeasures().getNodeCount(), counter);
        }
        if (flush) {
            serie1.clear();
            flushDone();
        }
    }

    @Override
    public void beforeDownRightBranch() {

    }

    @Override
    public void afterDownRightBranch() {
        counter--;
        if (frame.canUpdate() && activate) {
            serie1.add(solver.getMeasures().getNodeCount(), counter);
        }
        if (flush) {
            serie1.clear();
            serie2.clear();
            flushDone();
        }
    }

    @Override
    public void beforeRestart() {
        counter = 0;
    }

    @Override
    public void afterRestart() {

    }

    @Override
    public void beforeOpenNode() {
        if (frame.canUpdate() && activate) {
            serie2.add(solver.getMeasures().getNodeCount(), solver.getMeasures().getCurrentDepth());
        }
        if (flush) {
            serie1.clear();
            serie2.clear();
            flushDone();
        }
    }

    @Override
    public void afterOpenNode() {

    }
}