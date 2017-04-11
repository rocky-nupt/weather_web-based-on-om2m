/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
package org.eclipse.om2m.ipe.sample.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.ipe.sample.model.SampleModel;
import org.eclipse.om2m.ipe.sample.monitor.SampleMonitor;
import org.osgi.framework.FrameworkUtil;

/**
 * The Graphical User Interface of the IPE sample.
 */
public class GUI extends JFrame {
    /** Logger */
    static Log LOGGER = LogFactory.getLog(GUI.class);
    /** Serial Version UID */
    private static final long serialVersionUID = 1L;
    /** GUI Content Panel */
    private JPanel contentPanel;
    /** LAMP_ON Icon */
    static ImageIcon iconLampON = new ImageIcon(FrameworkUtil.getBundle(GUI.class).getResource("images/Lamp_ON.png"));
    /** LAMP_OFF Icon */
    static ImageIcon iconLampOFF = new ImageIcon(FrameworkUtil.getBundle(GUI.class).getResource("images/Lamp_OFF.png"));
    /** BUTTON_ON Icon */
    static ImageIcon iconButtonON = new ImageIcon(FrameworkUtil.getBundle(GUI.class).getResource("images/Btn_ON.png"));
    /** BUTTON_OFF Icon */
    static ImageIcon iconButtonOFF = new ImageIcon(FrameworkUtil.getBundle(GUI.class).getResource("images/Btn_OFF.png"));
    /** GUI Frame */
    static GUI frame;
    /** LAMP_0 LABEL */
    static JLabel LABEL_LAMP_0 = new JLabel("");
    /** LAMP_1 LABEL */
    static JLabel LABEL_LAMP_1 = new JLabel("");
    /** LAMP_0 ID */
    static String LAMP_0 = "LAMP_0";
    /** LAMP_1 ID */
    static String LAMP_1 = "LAMP_1";
    static SampleModel.LampObserver lampObserver;
    /**
     * Initiate The GUI.
     */
    public static void init() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame = new GUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.error("GUI init Error", e);
                }
            }
        });
    }

    /**
     * Stop the GUI.
     */
    public static void stop() {
    	SampleModel.deleteObserver(lampObserver);
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * Creates the frame.
     */
    public GUI() {
        setLocationByPlatform(true);
        setVisible(false);
        setResizable(false);
        setTitle("Sample Simulated IPE");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-500)/2, (screenSize.height-570)/2, 497, 570);

        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);
        contentPanel.setLayout(null);

        // Lamp0 Switcher0
        JPanel panel_Lamp0 = new JPanel();
        panel_Lamp0.setBounds(10, 5, 319, 260);
        contentPanel.add(panel_Lamp0);
        panel_Lamp0.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_Lamp0.setLayout(null);
        LABEL_LAMP_0.setIcon(iconLampOFF);
        LABEL_LAMP_0.setHorizontalTextPosition(SwingConstants.CENTER);
        LABEL_LAMP_0.setHorizontalAlignment(SwingConstants.CENTER);
        LABEL_LAMP_0.setBounds(10, 9, 149, 240);
        panel_Lamp0.add(LABEL_LAMP_0);

        // Lamp0 Switch Button
        JButton button_Lamp0 = new JButton();
        button_Lamp0.setOpaque(false);
        button_Lamp0.setPressedIcon(iconButtonON);
        button_Lamp0.setIcon(iconButtonOFF);
        button_Lamp0.setBounds(187, 44, 122, 155);
        panel_Lamp0.add(button_Lamp0);
        button_Lamp0.setMinimumSize(new Dimension(30, 23));
        button_Lamp0.setMaximumSize(new Dimension(30, 23));
        button_Lamp0.setPreferredSize(new Dimension(30, 23));

        JLabel labelSwitcher0 = new JLabel("Switch LAMP_0");
        labelSwitcher0.setFont(new Font("Vani", Font.BOLD | Font.ITALIC, 14));
        labelSwitcher0.setFocusCycleRoot(true);
        labelSwitcher0.setBorder(null);
        labelSwitcher0.setAutoscrolls(true);
        labelSwitcher0.setBounds(187, 199, 118, 29);
        panel_Lamp0.add(labelSwitcher0);
        // Listener for Lamp0 Switch Button
        button_Lamp0.addActionListener(new java.awt.event.ActionListener() {
            // Button Clicked
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Change Lamp0 State
                new Thread(){
                    public void run() {
                        // Send switch request to switch lamp0 state
                    	SampleMonitor.switchLamp(LAMP_0);
                    }
                }.start();
            }
        });


        // Lamp1 Switcher 1
        JPanel panel_Lamp1 = new JPanel();
        panel_Lamp1.setBounds(10, 271, 319, 260);
        contentPanel.add(panel_Lamp1);
        panel_Lamp1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        panel_Lamp1.setLayout(null);

        LABEL_LAMP_1.setIcon(iconLampOFF);
        LABEL_LAMP_1.setHorizontalTextPosition(SwingConstants.CENTER);
        LABEL_LAMP_1.setHorizontalAlignment(SwingConstants.CENTER);
        LABEL_LAMP_1.setBounds(10, 9, 154, 240);
        panel_Lamp1.add(LABEL_LAMP_1);

        // Lamp1 Switch Button
        JButton button_Lamp1 = new JButton();
        button_Lamp1.setOpaque(false);
        button_Lamp1.setPressedIcon(iconButtonON);
        button_Lamp1.setIcon(iconButtonOFF);
        button_Lamp1.setBounds(187, 44, 122, 156);
        panel_Lamp1.add(button_Lamp1);
        button_Lamp1.setMinimumSize(new Dimension(30, 23));
        button_Lamp1.setMaximumSize(new Dimension(30, 23));
        button_Lamp1.setPreferredSize(new Dimension(30, 23));

        JLabel labelSwitcher1 = new JLabel("Switch LAMP_1");
        labelSwitcher1.setFont(new Font("Vani", Font.BOLD | Font.ITALIC, 14));
        labelSwitcher1.setFocusCycleRoot(true);
        labelSwitcher1.setBorder(null);
        labelSwitcher1.setAutoscrolls(true);
        labelSwitcher1.setBounds(187, 199, 118, 29);
        panel_Lamp1.add(labelSwitcher1);
        // Listener for Lamp1 Switch Button
        button_Lamp1.addActionListener(new java.awt.event.ActionListener() {
            //Switch Button clicked
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Change Lamp1 State
                new Thread(){
                    public void run() {
                        // Send switch request to switch lamp1 state
                    	SampleMonitor.switchLamp(LAMP_1);
                    }
                }.start();
            }
        });

        // Switcher All lamps
        JButton buttonAllLamp = new JButton();
        buttonAllLamp.setOpaque(false);
        buttonAllLamp.setPressedIcon(iconButtonON);
        buttonAllLamp.setIcon(iconButtonOFF);
        buttonAllLamp.setBounds(339, 190, 145, 168);
        contentPanel.add(buttonAllLamp);
        buttonAllLamp.setMinimumSize(new Dimension(30, 23));
        buttonAllLamp.setMaximumSize(new Dimension(30, 23));
        buttonAllLamp.setPreferredSize(new Dimension(30, 23));

        JLabel labelSwitchAll = new JLabel("Switch All");
        labelSwitchAll.setAutoscrolls(true);
        labelSwitchAll.setFont(new Font("Vani", Font.BOLD | Font.ITALIC, 14));
        labelSwitchAll.setFocusCycleRoot(true);
        labelSwitchAll.setBorder(null);
        labelSwitchAll.setBounds(371, 369, 85, 29);
        contentPanel.add(labelSwitchAll);
        // Listener of Switch all Button
        buttonAllLamp.addActionListener(new java.awt.event.ActionListener() {
            // Switch Button Clicked
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Change all lamps states
                new Thread(){
                    public void run(){
                        // Send switch all request to create a content with the current State
                    	SampleMonitor.switchAll();
                    }
                }.start();
            }
        });
        
        lampObserver = new SampleModel.LampObserver() {
			
			@Override
			public void onLampStateChange(String lampId, boolean state) {
				setLabel(lampId, state);
			}
		};
        SampleModel.addObserver(lampObserver);
    }

    /**
     * Sets the LampIcon to ON or OFF depending on the newState
     * @param appId - The LAMP AppId
     * @param newState - The new LAMP State
     */
    public static void setLabel(String appId, boolean newState) {
        JLabel label = new JLabel("");
        if ("LABEL_LAMP_0".endsWith(appId)) {
            label = LABEL_LAMP_0;
        }
        if ("LABEL_LAMP_1".endsWith(appId)) {
            label = LABEL_LAMP_1;
        }
        if(newState) {
            label.setIcon(iconLampON);
        } else {
            label.setIcon(iconLampOFF);
        }
    }

}
