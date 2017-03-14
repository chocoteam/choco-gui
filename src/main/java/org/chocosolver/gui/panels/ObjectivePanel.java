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
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.variables.IntVar;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class ObjectivePanel extends APanel implements IMonitorOpenNode {
    XYSeries objective, bounds;
    boolean isOpt;
    boolean isMax;

    public ObjectivePanel(GUI frame) {
        super(frame);
        isOpt = solver.getObjectiveManager().getPolicy() != ResolutionPolicy.SATISFACTION;
        isMax = solver.getObjectiveManager().getPolicy() == ResolutionPolicy.MAXIMIZE;
        objective = new XYSeries("Best value");
        bounds = new XYSeries(isMax ? "Upper bound" : "Lower bound");
        XYSeriesCollection coll = new XYSeriesCollection();
        coll.addSeries(objective);
        coll.addSeries(bounds);

        JFreeChart dchart = ChartFactory.createXYLineChart(
                "Objective", "Nodes", "Objective", coll);

        this.setChart(dchart);
        if (isOpt) {
            solver.plugMonitor(this);
        }
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        if (isOpt) {
            tabbedpanel.addTab("Objective", this);
        }
    }

    @Override
    public void beforeOpenNode() {

    }

    @Override
    public void afterOpenNode() {
        if (frame.canUpdate() && isOpt && activate) {
            long ncount = solver.getMeasures().getNodeCount();
            if (solver.getMeasures().getSolutionCount() > 0) {
                objective.add(ncount, solver.getObjectiveManager().getBestSolutionValue());
            }
            bounds.add(ncount, isMax ?
                    ((IntVar) solver.getObjectiveManager().getObjective()).getUB() :
                    ((IntVar) solver.getObjectiveManager().getObjective()).getLB()
            );
        }
        if (flush) {
            objective.clear();
            bounds.clear();
            flushDone();
        }
    }
}
