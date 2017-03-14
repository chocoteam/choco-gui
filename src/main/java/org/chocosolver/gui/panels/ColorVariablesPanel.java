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
import org.chocosolver.solver.search.loop.monitors.IMonitorInitialize;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.variables.IntVar;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 05/06/2014
 */
public class ColorVariablesPanel extends APanel implements IMonitorOpenNode, IMonitorInitialize {

    int size;
    int psize = 10;

    BufferedImage image;
    JLabel theLabel;
    ImageIcon icon;
    IntVar[] ivars;
    float[] ids;


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
        afterInitialize();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int i = 0 ; i < ivars.length; i++){
            int x = i / size;
            int y = i % size;
            float[] rgb = rgb(ivars[i].getDomainSize() / ids[i]);
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            for (int j = 0; j < psize; j++)
                for (int k = 0; k < psize; k++)
                    image.setRGB(x * psize + j, y * psize + k, color.getRGB());
        }
    }

    private static float[] rgb(float value){
        int aR = 0;   int aG = 255; int aB=0;  // RGB for our 1st color (blue in this case).
        int bR = 0; int bG = 0; int bB=255;    // RGB for our 2nd color (red in this case).
        float[] rgb = new float[3];
        rgb[0]  = ((bR - aR) * value + aR) / 255f;      // Evaluated as -255*value + 255.
        rgb[1] = ((bG - aG) * value + aG) / 255f;      // Evaluates as 0.
        rgb[2]  =  ((bB - aB) * value + aB) / 255f;       // Evaluates as 255*value + 0.
        return rgb;
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        super.plug(tabbedpanel);
        tabbedpanel.addTab("Color map", this);
    }

    @Override
    public void afterInitialize() {
        ivars = model.retrieveIntVars(true);
        ids = new float[ivars.length];
        for(int i = 0 ; i < ivars.length; i++){
            ids[i] = 1.f * ivars[i].getDomainSize();
        }
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