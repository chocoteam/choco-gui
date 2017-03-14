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
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class ColorVariablesPanel extends APanel implements IMonitorOpenNode {

    int size;
    int psize = 10;

    BufferedImage image;
    JLabel theLabel;
    ImageIcon icon;


    public ColorVariablesPanel(GUI frame) {
        super(frame);
        solver.plugMonitor(this);
        size = (int) Math.ceil(Math.sqrt(model.getNbVars()));
        psize = 600 / size;
        image = new BufferedImage(size * psize, size * psize, BufferedImage.TYPE_INT_ARGB);
        solver.plugMonitor(this);
        icon = new ImageIcon(image);
        theLabel = new JLabel(new ImageIcon(image));
        add(theLabel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < model.getNbVars(); i++) {
            int x = i / size;
            int y = i % size;
            Color color = model.getVar(i).isInstantiated() ? Color.GREEN : Color.BLUE;
            for (int j = 0; j < psize; j++)
                for (int k = 0; k < psize; k++)
                    image.setRGB(x * psize + j, y * psize + k, color.getRGB());
        }
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("Color map", this);
    }

    @Override
    public void beforeOpenNode() {
    }


    @Override
    public void afterOpenNode() {
        if (frame.canUpdate() && activate) {
            repaint();
        }
    }
}