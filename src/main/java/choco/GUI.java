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
package choco;

import choco.panels.DepthPanel;
import choco.panels.ObjectivePanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.tabbedui.VerticalLayout;
import solver.Solver;
import solver.search.loop.monitors.IMonitorInitialize;
import solver.search.loop.monitors.IMonitorOpenNode;
import solver.variables.IntVar;
import util.tools.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 02/06/2014
 */
public class GUI extends JFrame implements IMonitorOpenNode, IMonitorInitialize {

    Solver solver;
    public static int DEFAULT_NODE_WAIT = 1000;
    XYSeries logdomsiz;


    // SWING
    JTabbedPane tabbedpanel = new JTabbedPane();

    JButton playB = new JButton("Run");
    JButton pauseB = new JButton("Pause");

    AtomicBoolean play = new AtomicBoolean(false);
    JCheckBox samplingCB = new JCheckBox("Sampling");
    AtomicBoolean sampling = new AtomicBoolean(false);
    JPanel leftpanel = new JPanel(new VerticalLayout());
    JLabel[] statistics = new JLabel[10];

    private static final int VAR = 1, CSTR = 2, SOL = 3, FAI = 4, BCK = 5, NOD = 6, TIM = 7, NpS = 8;


    public GUI(Solver solver) {
        this.solver = solver;
        init();
    }

    public void init() {
        createButtons();
        add(tabbedpanel, BorderLayout.CENTER);
        //add(, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(80, 80, 800, 800);
        setVisible(true);

    }

    private void createButtons() {
        leftpanel.add(playB);
        playB.setEnabled(true);
        leftpanel.add(pauseB);
        pauseB.setEnabled(false);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play.set(!play.get());
                playB.setEnabled(!play.get());
                pauseB.setEnabled(play.get());
            }
        };
        playB.addActionListener(actionListener);
        pauseB.addActionListener(actionListener);

        leftpanel.add(samplingCB);
        samplingCB.setEnabled(true);
        samplingCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampling.set(!sampling.get());
                samplingCB.setSelected(sampling.get());
            }
        });
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = new JLabel();
            statistics[i].setHorizontalAlignment(JTextField.RIGHT);
            leftpanel.add(statistics[i]);
        }
        printStatistics();
        add(leftpanel, BorderLayout.WEST);
    }

    private void printStatistics() {
        statistics[VAR].setText(pad(solver.getNbVars() + " vars"));
        statistics[CSTR].setText(pad(solver.getNbCstrs() + " cstrs"));
        statistics[SOL].setText(pad(solver.getMeasures().getSolutionCount() + " sols"));
        statistics[FAI].setText(pad(solver.getMeasures().getFailCount() + " fails"));
        statistics[BCK].setText(pad(solver.getMeasures().getBackTrackCount() + " bcks"));
        statistics[NOD].setText(pad(solver.getMeasures().getNodeCount() + " nodes"));
        statistics[TIM].setText(pad(String.format("%.1f s.", solver.getMeasures().getTimeCount())));
        statistics[NpS].setText(pad(String.format("%.2f n/s", solver.getMeasures().getNodeCount() / solver.getMeasures().getTimeCount())));
        solver.getMeasures().updateTimeCount(); // to deal with the first print
    }

    private static String pad(String txt) {
        return StringUtils.pad(txt, -20, " ");
    }

    @Override
    public void beforeOpenNode() {
        while (!play.get()) ;
        printStatistics();
    }

    @Override
    public void afterOpenNode() {
    }

    private double logdomsizIt() {
        double lds = 0.0;
        for (int i = 0; i < solver.getNbVars(); i++) {
            lds += Math.log(((IntVar) solver.getVar(i)).getDomainSize());
        }
        return lds;
    }

    public Solver getSolver() {
        return solver;
    }

    public AtomicBoolean getSampling() {
        return sampling;
    }

    @Override
    public void beforeInitialize() {
        new DepthPanel(this).plug(tabbedpanel);
        new ObjectivePanel(this).plug(tabbedpanel);
        tabbedpanel.setSelectedIndex(-1);
        tabbedpanel.setSelectedIndex(1);
        while (!play.get()) ;

    }

    @Override
    public void afterInitialize() {

    }

    public boolean canUpdate() {
        return (!sampling.get() ||
                ((solver.getMeasures().getNodeCount() % GUI.DEFAULT_NODE_WAIT) == 0));
    }
}
