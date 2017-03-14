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
import org.chocosolver.solver.search.loop.monitors.IMonitorDownBranch;
import org.chocosolver.solver.search.loop.monitors.IMonitorRestart;
import org.chocosolver.solver.search.loop.monitors.IMonitorUpBranch;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * http://cs.lmu.edu/~ray/notes/binarytrees/
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class BinaryTreePanel extends APanel implements IMonitorDownBranch, IMonitorUpBranch, IMonitorRestart {

    int[] nodes = new int[16];
    int cnode = 1;

    public BinaryTreePanel(GUI frame) {
        super(frame);
        solver.plugMonitor(this);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("Tree search", this);
        Arrays.fill(nodes, 0);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the binary tree, based on the explored tree
        int diameter = 20;//600 / (cnode + 1);
        for (int i = cnode-1; i>0; i--) {
            Graphics2D g2d = (Graphics2D) g;
            int value = nodes[i];
            String data = value == 1?"L":"R("+value+")";
            FontMetrics fm = g.getFontMetrics();
            Rectangle r = fm.getStringBounds(data, g).getBounds();
            int x = 100;
            int y = i* diameter;
            r.setLocation(x - r.width/2, y - r.height/2);
            g.setColor(Color.WHITE);
            g.fillRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
            g.setColor(value == 1?Color.GREEN:Color.BLUE);
            g.drawString(data, r.x, r.y + r.height);
        }
    }

    @Override
    public void beforeDownLeftBranch() {
        resize();
        nodes[cnode++] = 1;
        if (frame.canUpdate() && activate) {
            repaint();
        }
        if (flush) {
            flushDone();
        }
    }

    @Override
    public void afterDownLeftBranch() {

    }

    @Override
    public void beforeDownRightBranch() {
        nodes[cnode++]++;
        if (frame.canUpdate() && activate) {
            repaint();
        }
        if (flush) {
            flushDone();
        }

    }

    @Override
    public void afterDownRightBranch() {

    }

    @Override
    public void beforeUpBranch() {
        nodes[cnode - 1] += nodes[cnode];
        nodes[cnode] = 0;
        cnode--;
        if (frame.canUpdate() && activate) {
            repaint();
        }
    }

    @Override
    public void afterUpBranch() {
    }

    private void resize() {
        if (cnode == nodes.length - 2) {
            int[] tmp = nodes;
            nodes = new int[cnode * 3 / 2 + 1];
            System.arraycopy(tmp, 0, nodes, 0, cnode);
            Arrays.fill(nodes, cnode, nodes.length, 0);
        }
    }

    @Override
    public void beforeRestart() {

    }

    @Override
    public void afterRestart() {
        cnode = 1;
        Arrays.fill(nodes, 0);
    }
}