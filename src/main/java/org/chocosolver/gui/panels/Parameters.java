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

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 06/06/2014
 */
public class Parameters extends APanel {


    List<APanel> panels = new LinkedList<APanel>();
    List<JCheckBox> tpanels = new LinkedList<JCheckBox>();

    public Parameters(GUI frame) {
        super(frame);
    }

    @Override
    public void plug(JTabbedPane tabbedpanel) {
        panels.add(new FreeVarsPanel(frame));
        tpanels.add(addCheckbox("Free variables", 0, tabbedpanel));

        panels.add(new DepthPanel(frame));
        tpanels.add(addCheckbox("Depth", 1, tabbedpanel));

        panels.add(new ObjectivePanel(frame));
        tpanels.add(addCheckbox("Objective", 2, tabbedpanel));

        panels.add(new LeftRightBranchPanel(frame));
        tpanels.add(addCheckbox("LR decisions", 3, tabbedpanel));

        panels.add(new ColorVariablesPanel(frame));
        tpanels.add(addCheckbox("Domain state", 4, tabbedpanel));

        panels.add(new BinaryTreePanel(frame));
        tpanels.add(addCheckbox("Tree", 5, tabbedpanel));

        for (APanel panel : panels) {
            panel.plug(tabbedpanel);
        }

        if (!((ObjectivePanel) panels.get(2)).isOpt) {
            tpanels.get(2).setEnabled(false);
            panels.get(2).unplug(tabbedpanel);
        }

        tabbedpanel.addTab("Parameters", this);
    }


    private JCheckBox addCheckbox(String text, final int idx, final JTabbedPane tabbedpanel) {
        JCheckBox cbox = new JCheckBox(text);
        cbox.setSelected(true);
        cbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    panels.get(idx).plug(tabbedpanel);
                } else {
                    panels.get(idx).unplug(tabbedpanel);
                }
            }
        });
        add(cbox);
        return cbox;
    }

    @Override
    public void unplug(JTabbedPane tabbedpanel) {
    }

    public void flushNow() {
        for (APanel panel : panels) {
            panel.flushData();
        }
    }
}
