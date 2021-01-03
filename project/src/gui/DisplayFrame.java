package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.TSPPanel;

import generator.RandomGraphGenerator;
import net.miginfocom.swing.MigLayout;
import tsp.MainRunner;

/**
 * Creates a GUI representation of the graph
 * 
 * @author Chang Kon Han
 * @author Wesley Yep
 * @author John Law
 */
public class DisplayFrame {
	JFrame frame;
	private final String[] problems = {"test4.tsp", "burma14.tsp", "test17.tsp", "test27.tsp", "test30.tsp", "test40.tsp", "test50.tsp", "ulysses22.tsp", "random.tsp"
										, "speedup10.tsp", "speedup15.tsp", "speedup20.tsp", "speedup25.tsp", "speedup30.tsp"
										, "speedup35.tsp"};
	//comboboxes
	private final JComboBox<String> sequentialComboBox = new JComboBox<String>(MainRunner.sequentialAlgorithms);
	private final JComboBox<String> parallelComboBox = new JComboBox<String>(MainRunner.parallelAlgorithms);
	private final JComboBox<String> problemsComboBox = new JComboBox<String>(problems);
	private TSPPanel solutionPanel;
	private TSPPanel workingPanel;
	private static TSPInstance instance;
	private MainRunner main;
	private JCheckBox showWorkingCheckBox;
	
	public DisplayFrame(String name, MainRunner main) {
		this.main = main;
		//setup default
		String fp = System.getProperty("user.dir") + File.separator + "data" + File.separator + "tsp" + File.separator + "prob" + File.separator + problemsComboBox.getSelectedItem().toString();
		try {
			instance = new TSPInstance(new File(fp));
		} catch (IOException e) {e.printStackTrace(); return;}
		solutionPanel = new TSPPanel(instance);
		solutionPanel.setAutoRepaint(false);
		workingPanel = new TSPPanel(instance);
		workingPanel.setAutoRepaint(true);
		
		JPanel controlsPanel = new JPanel(new MigLayout());
		controlsPanel.setSize(500, 200);		
		
		JTextField dimensionForRandomField = new JTextField(5);
		JButton createRandomButton = new JButton("Create");
		
		//problem select
		controlsPanel.add(new JLabel("Problem: "));
		controlsPanel.add(problemsComboBox);
		controlsPanel.add(new JLabel(" or Create Random (Enter size): "));
		controlsPanel.add(dimensionForRandomField);
		controlsPanel.add(createRandomButton, "wrap");
		
		// parallel/sequential controls
		ButtonGroup parallelGroup = new ButtonGroup();
		JRadioButton sequentialRadio = new JRadioButton("Sequential"); 
		JRadioButton parallelRadio = new JRadioButton("Parallel");
		parallelGroup.add(sequentialRadio);
		parallelGroup.add(parallelRadio);
		controlsPanel.add(new JLabel("Type: "), "split 2");
		controlsPanel.add(sequentialRadio);
		controlsPanel.add(parallelRadio, "wrap");
		sequentialRadio.setSelected(true);
		sequentialRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sequentialComboBox.setEnabled(true);
				parallelComboBox.setEnabled(false);
			}
		});
		parallelRadio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sequentialComboBox.setEnabled(false);
				parallelComboBox.setEnabled(true);
			}
		});
		
		//add comboboxes
		sequentialComboBox.setEnabled(true);
		parallelComboBox.setEnabled(false);
		controlsPanel.add(sequentialComboBox, "w 150!");
		controlsPanel.add(parallelComboBox, "w 150!, wrap");
		
		//start button
		JButton startButton = new JButton("Start");
		controlsPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type;
				if (sequentialRadio.isSelected()) {
					type = sequentialComboBox.getSelectedItem().toString();
				} else {
					type = parallelComboBox.getSelectedItem().toString();
				}
				startButton.setEnabled(false);
				main.runAlgorithm(problemsComboBox.getSelectedItem().toString(), type, startButton, instance.getDistanceTable(), workingPanel, solutionPanel);
			}
		});
		
		//show working checkbox
		showWorkingCheckBox = new JCheckBox("Show working");
		controlsPanel.add(showWorkingCheckBox);
		
		//action listener for problem select
		problemsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//setup default
				String fp = System.getProperty("user.dir") + File.separator + "data" + File.separator + "tsp" + File.separator + "prob" + File.separator + problemsComboBox.getSelectedItem().toString();
				try {
					instance = new TSPInstance(new File(fp));
				} catch (IOException ex) {ex.printStackTrace(); return;}
				frame.getContentPane().removeAll();
				solutionPanel = new TSPPanel(instance);
				workingPanel = new TSPPanel(instance);
				frame.getContentPane().add(workingPanel, "w 500!, h 400!");		
				frame.getContentPane().add(solutionPanel, "w 500!, h 400!");
				frame.getContentPane().add(controlsPanel, "dock south");
				frame.validate();
				frame.repaint();
				System.out.println("changed to " + problemsComboBox.getSelectedItem().toString());
			}
		});
		
		createRandomButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RandomGraphGenerator.createRandomGraph(Integer.parseInt(dimensionForRandomField.getText()));
					problemsComboBox.setSelectedItem("random.tsp");
				} catch (NumberFormatException ex) {
					System.out.println("Please enter a valid size");
				}catch (IOException ex1) {
					ex1.printStackTrace();
				}
				
			}
		});
		
		frame = new JFrame(name);
		frame.getContentPane().setLayout(new MigLayout());
		frame.getContentPane().add(workingPanel, "w 500!, h 400!");		
		frame.getContentPane().add(solutionPanel, "w 500!, h 400!");
		frame.getContentPane().add(controlsPanel, "dock south");
	}
	
	public boolean showWorkingEnabled() {
		return showWorkingCheckBox.isSelected();
	}

	public void setPanels(TSPPanel solutionPanel, TSPPanel workingPanel) {
		this.solutionPanel = solutionPanel;
		this.workingPanel = workingPanel;
	}
	
	public void display() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(1025, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static TSPInstance getInstance() {
		return instance;
	}
	
}
