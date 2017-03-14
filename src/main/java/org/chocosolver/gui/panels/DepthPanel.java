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
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class DepthPanel extends APanel implements IMonitorOpenNode {
    XYSeries depth;

    public DepthPanel(GUI frame) {
        super(frame);
        depth = new XYSeries("Depth");
        XYSeriesCollection scoll = new XYSeriesCollection();
        scoll.addSeries(depth);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Depth", "Nodes", "Depth", scoll);
        this.setChart(chart);
        solver.plugMonitor(this);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("Depth", this);
    }

    @Override
    public void beforeOpenNode() {
    }

    @Override
    public void afterOpenNode() {
        if (frame.canUpdate() && activate) {
            depth.add(solver.getMeasures().getNodeCount(), solver.getMeasures().getCurrentDepth());
        }
        if (flush) {
            depth.clear();
            flushDone();
        }
    }
}