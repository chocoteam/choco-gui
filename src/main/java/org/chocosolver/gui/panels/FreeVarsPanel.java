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
public class FreeVarsPanel extends APanel implements IMonitorOpenNode {
    XYSeries series;

    public FreeVarsPanel(GUI frame) {
        super(frame);
        series = new XYSeries("Free variables");
        XYSeriesCollection scoll = new XYSeriesCollection();
        scoll.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Free variables", "Nodes", "free vars", scoll);
        this.setChart(chart);
        solver.plugMonitor(this);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("free vars", this);
    }


    @Override
    public void beforeOpenNode() {
    }

    @Override
    public void afterOpenNode() {
        if (frame.canUpdate() && activate) {
            series.add(solver.getMeasures().getNodeCount(), compute());
        }
        if (flush) {
            series.clear();
            flushDone();
        }
    }

    private double compute() {
        double lds = 0.0;
        for (int i = 0; i < solver.getNbVars(); i++) {
            lds += solver.getVar(i).isInstantiated() ? 1 : 0;
        }
        return solver.getNbVars() - lds;
    }
}