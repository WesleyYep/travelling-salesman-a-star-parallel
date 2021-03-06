package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ArrayUtils;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.MouseManager;
import org.moeaframework.problem.tsplib.DisplayDataType;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.TSPPanel;
import org.moeaframework.problem.tsplib.Tour;

import heuristic.Heuristic;
import heuristic.MinimumSpanningTree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import net.miginfocom.swing.MigLayout;
import pttask.AStarBlackboardSolver;
import pttask.AStarHDA;
import pttask.AStarSolverParallel;
import pttask.IDAStarSolverParallel;
import tsp.AStarSolver;
import tsp.AbstractSolver;
import tsp.EvolutionaryAlgorithmSolver;
import tsp.IDAStarSolver;
import tsp.NearestNeighbourApproximationSolver;
import tsp.SolverObserver;
import tsp.State1;

public class TSPApplication extends Application implements SolverObserver {

	// directory to TSP files
	private static String PROBLEM_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "tsp" + File.separator + "prob";
	
	private TextField filterField = new TextField();
	private TableView<TSPInstance> tspTable = new TableView<TSPInstance>();
	private TableColumn<TSPInstance, String> filenameColumn = new TableColumn<TSPInstance, String>();
	private ObservableList<TSPInstance> masterData = FXCollections.observableArrayList();
	private SwingNode graphNode = new SwingNode();
	
	// choice box
	private ChoiceBox algorithmChoiceBox = new ChoiceBox();
	
	// currently selected problem
	private TSPInstance instance = null;
	
	private void display() throws IOException {
		if (instance == null) {
			// set empty jpanel. white background
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			graphNode.setContent(panel);
		} else {
			// check selected algorithm type to determine how much needs to be shown
			String type = algorithmChoiceBox.getValue().toString();
			boolean sequential = false;
			
			switch(type) {
			case "A*":
			case "IDA*":
			case "Genetic Algorithm":
				// sequential
				sequential = true;
			case "A* Centralised":
			case "A* Blackboard":
			case "IDA* Parallel":
			case "HDA* Parallel":
				// parallel
				// determine how many panels to show
				int numOfPanel = (sequential) ? 1 : Runtime.getRuntime().availableProcessors();
				JPanel mainPanel = new JPanel(new MigLayout("wrap 3, insets 0 0 0 0"));
				for (int i = 0; i < numOfPanel; i++) {
					TSPPanel panel = new TSPPanel(instance);
					panel.setAutoRepaint(true);
					mainPanel.add(panel, "push, grow");
				}
				graphNode.setContent(mainPanel);
				break;
			}
		}
	}
	
	public void displayTour(Tour tour, int id, boolean finished) {
		JPanel mainPanel = (JPanel)graphNode.getContent();
		TSPPanel panel = (TSPPanel)mainPanel.getComponent(id);
		panel.clearTours();
		
		if (finished) {
			panel.displayTour(tour, Color.BLACK, new BasicStroke(2.0f));
		} else {
			panel.displayTour(tour);
		}
	}
	
	/**
	 * Adds possible tsp files into the path
	 * @param path
	 * @throws IOException 
	 */
	
	private void initData(String path) throws IOException {
		File root = new File(path);
		File[] list = root.listFiles();
		
		for (File f : list) {
			if (f.isFile()) {
				try {
					TSPInstance instance = new TSPInstance(f);
					// ignore no display data types
					if (instance.getDisplayDataType() == DisplayDataType.NO_DISPLAY) {
						continue;
					}
					
					masterData.add(instance);
				} catch (Exception e) {
					// do nothing
				}
			}
		}
		
	}
	
	/**
	 * @throws IOException 
	 * @see <a>http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering</a>
	 */
	
	private void initialise(String path) throws IOException {
		// 0. initalize the data
		initData(path);
		
		// 0.5 initialize the columns
		filenameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<TSPInstance> filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(file -> {
                // If filter text is empty, display all files
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (file.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<TSPInstance> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tspTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tspTable.setItems(sortedData);
        
        // 6. add column
        tspTable.getColumns().setAll(filenameColumn);
        
        // 7. set resize policy
        tspTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // 8. Add row listener
        tspTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        	try {
        		instance = newSelection;
        		SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							display();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
        			
        		});
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
	}

	/**
	 * Define constraints here
	 * @param pane
	 */
	
	private void setConstraints(GridPane pane) {
		// set column constraint
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(20);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(80);
		
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100);
		
		pane.getRowConstraints().add(row1);
		pane.getColumnConstraints().addAll(col1, col2);
	}
	
	private Pane createAndSetupGUI() throws IOException {
		GridPane root = new GridPane();
		
		// set panes to add
		Pane leftPane = setupTable();
		Pane centerPane = setupMainContent();
		
		root.add(leftPane, 0, 0);
		root.add(centerPane, 1, 0);
		
		// set constraints
		setConstraints(root);
		
		return root;
	}
	
	/**
	 * Sets up table pane
	 * @return
	 * @throws IOException
	 */
	
	private Pane setupTable() throws IOException {
		// setup table
		initialise(PROBLEM_PATH);
		// Set pane
		BorderPane leftPane = new BorderPane();
		leftPane.setTop(filterField);
		leftPane.setCenter(tspTable);
		
		return leftPane;
	}
	
	/**
	 * Sets up main pane
	 * @return
	 * @throws IOException
	 */
	
	private Pane setupMainContent() throws IOException {
		// set control pane
		BorderPane controlPane = new BorderPane();
		HBox settingPane = new HBox();
		Label typeLabel = new Label("Type:");
		
		HBox settingChildPane = new HBox();
		
		algorithmChoiceBox.setItems(FXCollections.observableArrayList(
			"A*",
			"NN",
			"IDA*",
		//	"Genetic Algorithm",
			new Separator(),
			"A* Centralised",
			"A* Blackboard",
			"IDA* Parallel",
			"HDA* Parallel"
		));
		
		// set default
		algorithmChoiceBox.getSelectionModel().select(0);
		
		algorithmChoiceBox.setOnAction((event) -> {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						display();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			});
		});
		
		Button startButton = new Button("Start");
		startButton.setOnAction((event) -> {
			runAlgorithm();
		});
		
		// set spacing
		settingChildPane.setSpacing(10);
		settingChildPane.getChildren().add(algorithmChoiceBox);
		settingChildPane.getChildren().add(startButton);
		
		// set spacing for parent container
		settingPane.setSpacing(100);
		settingPane.getChildren().add(typeLabel);
		settingPane.getChildren().add(settingChildPane);
		
		//Label timeLabel = new Label("00:00:00.000");
		
		// set control pane
		controlPane.setCenter(settingPane);
		//controlPane.setRight(timeLabel);
		
		// set margin of control pane
		BorderPane.setMargin(controlPane, new Insets(12, 12, 12, 12));
		
		// setup graph
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					display();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		// set pane
		BorderPane centerPane = new BorderPane();
		centerPane.setTop(controlPane);
		centerPane.setCenter(graphNode);
		
		return centerPane;
	}

	
	private void runAlgorithm() {

		// check if needed to run
		if (algorithmChoiceBox.getValue() == null || instance == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("TSP algorithm");
			alert.setHeaderText(null);
			alert.setContentText("Choose a problem or solver");
			alert.showAndWait();
			return;
		}
		
		TSPApplication app = this;
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				String type = algorithmChoiceBox.getValue().toString();
				Heuristic mst = new MinimumSpanningTree();
				
				AbstractSolver solver;
				DistanceTable table = instance.getDistanceTable();
				
				switch (type) {
				case "A*":
					solver = new AStarSolver(mst, table);
					break;
				case "NN":
					solver = new NearestNeighbourApproximationSolver(mst, table);
					break;
				case "IDA*":
					solver = new IDAStarSolver(mst, table);
					break;
				case "Genetic Algorithm":
					solver = new EvolutionaryAlgorithmSolver(mst, table);
					break;
				case "A* Centralised":
					solver = new AStarSolverParallel(mst, table);
					break;
				case "A* Blackboard":
					solver = new AStarBlackboardSolver(mst, table);
					break;
				case "IDA* Parallel":
					solver = new IDAStarSolverParallel(mst, table);
					break;
				case "HDA* Parallel":
					solver = new AStarHDA(mst, table);
					break;
				default:
					solver = new AStarSolver(mst, table);
					break;
				}
				
				// add itself to observer list
				solver.addObserver(app);
				
				long startTime = System.currentTimeMillis();
				// blocking call
				State1 finalState = solver.search();
				long finishTime = System.currentTimeMillis();
				
				Integer[] cities = finalState.getCities().toArray(new Integer[]{});
				Tour best = Tour.createTour(ArrayUtils.toPrimitive(cities));
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						displayTour(best, 0, true);
					}
					
				});
				
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("TSP algorithm");
						alert.setHeaderText(null);
						alert.setContentText("Solver finished in " + (finishTime - startTime) + "ms. Tour is " + best.toString() + " - cost: " + finalState.getCurrentTourDistance());
						alert.showAndWait();
					}
					
				});
			}
			
		});
		
		thread.start();
	}
	
	@Override
	public void update(Tour tour, int id) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				displayTour(tour, id, false);
			}
			
		});
	}
	
	public static void main(String[] args) {
		// javafx approach
		launch(args);
	}

	/**
	 * javafx approach
	 */
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Travelling Salesman A Star");
		primaryStage.setOnCloseRequest(e -> Platform.exit());
		Pane root = createAndSetupGUI();
		primaryStage.setScene(new Scene(root, 1300, 700));
		primaryStage.show();
	}
	
	class DummyMouseManager implements MouseManager {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void init(GraphicGraph arg0, View arg1) {
		}

		@Override
		public void release() {
		}
		
	}


}
