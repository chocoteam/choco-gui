/**
 *  Copyright (c) 1999-2014, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package choco.panels;

import choco.GUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import solver.search.loop.monitors.IMonitorOpenNode;
import solver.variables.IntVar;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class LogDomSizePanel extends APanel implements IMonitorOpenNode {
    XYSeries series;

    public LogDomSizePanel(GUI frame) {
        super(frame);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        series = new XYSeries("Log. dom. size");
        XYSeriesCollection scoll = new XYSeriesCollection();
        scoll.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Log. Dom. Size", "Nodes", "log(dom.size)", scoll);
        this.setChart(chart);

        tabbedpanel.addTab("log(dom.size)", this);
        solver.plugMonitor(this);
    }

    @Override
    public void unplug() {
        //solver.unplugMonitor(this);
        this.setVisible(false);
    }

    @Override
    public void beforeOpenNode() {
    }

    @Override
    public void afterOpenNode() {
        if (frame.canUpdate()) {
            series.add(solver.getMeasures().getNodeCount(), logdomsizIt());
        }
        if(flush){
            series.clear();
            flushDone();
        }
    }

    private double logdomsizIt() {
        double lds = 0.0;
        for (int i = 0; i < solver.getNbVars(); i++) {
            lds += Math.log(((IntVar) solver.getVar(i)).getDomainSize());
        }
        return lds;
    }
}