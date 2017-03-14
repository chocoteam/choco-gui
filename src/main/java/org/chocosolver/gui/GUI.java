/**
 * This file is part of choco-gui, https://github.com/chocoteam/choco-gui
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.gui;

import org.chocosolver.gui.panels.Parameters;
import org.chocosolver.solver.Model;
import org.jfree.ui.tabbedui.VerticalLayout;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.loop.monitors.IMonitorInitialize;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.util.tools.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 02/06/2014
 */
public class GUI extends JFrame implements IMonitorOpenNode, IMonitorInitialize, IMonitorSolution {

    Model model;
    Solver solver;
    Parameters parameters;

    // SWING
    JTabbedPane tabbedpanel = new JTabbedPane();

    JButton playB = new JButton("Run");
    JButton pauseB = new JButton("Pause");
    JButton flushallB = new JButton("Flush");
    JButton nextSolB = new JButton("Next solution");
    JButton nextNodeB = new JButton("Next node");

    AtomicBoolean play = new AtomicBoolean(false);
    AtomicBoolean nextSol = new AtomicBoolean(false);
    AtomicBoolean nextNode = new AtomicBoolean(false);

    String[] frequency = new String[]{"1", "10", "100", "1000", "10000"};
    JComboBox refreshCB = new JComboBox(frequency);
    AtomicInteger node_wait = new AtomicInteger(1);
    JPanel leftpanel = new JPanel(new VerticalLayout());
    JLabel[] statistics = new JLabel[10];

    private static final int VAR = 1, CSTR = 2, SOL = 3, FAI = 4, BCK = 5, NOD = 6, RES = 7, TIM = 8, NpS = 9;


    public GUI(Solver solver) {
        this.solver = solver;
        this.model = solver.getModel();
        init();
    }

    public void init() {
        parameters = new Parameters(this);
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
                nextNodeB.setEnabled(!play.get());
                nextSolB.setEnabled(!play.get());

                pauseB.setEnabled(play.get());

                nextNode.set(false);
                nextSol.set(false);

            }
        };
        playB.addActionListener(actionListener);
        pauseB.addActionListener(actionListener);

        leftpanel.add(nextSolB);
        nextSolB.setEnabled(true);
        nextSolB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextSol.set(true);

                pauseB.setEnabled(true);

                playB.setEnabled(false);
                nextSolB.setEnabled(false);
                nextNodeB.setEnabled(false);
            }
        });

        leftpanel.add(nextNodeB);
        nextSolB.setEnabled(true);
        nextNodeB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextNode.set(true);

                pauseB.setEnabled(true);

                playB.setEnabled(false);
                nextSolB.setEnabled(false);
                nextNodeB.setEnabled(false);
            }
        });


        leftpanel.add(flushallB);
        flushallB.setEnabled(true);
        flushallB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parameters.flushNow();
            }
        });

        /*leftpanel.add(samplingCB);
        samplingCB.setEnabled(true);
        samplingCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampling.set(!sampling.get());
                samplingCB.setSelected(sampling.get());
            }
        });*/
        leftpanel.add(new JLabel("Refresh freq. (p. node)"));
        leftpanel.add(refreshCB);
        refreshCB.setEnabled(true);
        refreshCB.setSelectedIndex(0);
        refreshCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String freq = frequency[refreshCB.getSelectedIndex()];
                node_wait.set(Integer.parseInt(freq));
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
        statistics[VAR].setText(pad(model.getNbVars() + " vars"));
        statistics[CSTR].setText(pad(model.getNbCstrs() + " cstrs"));
        statistics[SOL].setText(pad(solver.getMeasures().getSolutionCount() + " sols"));
        statistics[FAI].setText(pad(solver.getMeasures().getFailCount() + " fails"));
        statistics[BCK].setText(pad(solver.getMeasures().getBackTrackCount() + " bcks"));
        statistics[NOD].setText(pad(solver.getMeasures().getNodeCount() + " nodes"));
        statistics[RES].setText(pad(solver.getMeasures().getRestartCount() + " restarts"));
        statistics[TIM].setText(pad(String.format("%.1f s.", solver.getMeasures().getTimeCount())));
        statistics[NpS].setText(pad(String.format("%.2f n/s", solver.getMeasures().getNodeCount() / solver.getMeasures().getTimeCount())));
        //solver.getMeasures().updateTimeCount(); // to deal with the first print
    }

    private static String pad(String txt) {
        return StringUtils.pad(txt, -20, " ");
    }

    public Solver getSolver() {
        return solver;
    }

    public Model getModel() {
        return model;
    }

    private void refreshButtons() {
        playB.setEnabled(!play.get());
        pauseB.setEnabled(play.get());
        nextNodeB.setEnabled(!play.get());
        nextSolB.setEnabled(!play.get());
    }

    @Override
    public void beforeInitialize() {
        parameters.plug(tabbedpanel);
        while (!play.get() && !nextNode.get() && !nextSol.get()) ;
        refreshButtons();
    }

    @Override
    public void afterInitialize() {
    }

    @Override
    public void beforeOpenNode() {
        while (!play.get() && !nextNode.get() && !nextSol.get()) ;
        printStatistics();
        nextNode.set(false);
        refreshButtons();
    }


    @Override
    public void afterOpenNode() {
    }

    @Override
    public void onSolution() {
        nextSol.set(false);
        refreshButtons();
    }

    public boolean canUpdate() {
        return ((solver.getMeasures().getNodeCount() % node_wait.get()) == 0);
    }
}
