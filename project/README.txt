UoA.Engineering.SE751-2016

MAIN INSTRUCTIONS:
- Checkout group26 directory from SVN
- Import the project from the /project folder
- Run the GUI program by running the gui.TSPApplication.java file
- If necessary, use the filter textbox to help find which problem to solve
 	(Not all problems will work, needs to be a EUC_2D problem, see below for recommendations)
- Select a reasonably sized problem to run.
	We recommend the speedupXX.tsp files, testXX.tsp files or any problem with less than 40 cities (burma14, ulysses16, etc.)
- Select an algorithm to run, either a sequential or parallel algorithm
- Press the start button
- Wait for the finished popup to display
- Repeat with different problems and different algorithms

Optional:
- The old swing GUI can be used to randomly create test problems
- This is accessed by running tsp.MainRunner.java (OLD GUI)
- Enter the number of cities into the text box and click create, a new graph will be created and stored as "data/tsp/prob/random.tsp"

RESULTS and REPORT
- located in both the root folder and /res folder
- includes main report, presentation slides, spreadsheet of results, and graphs pdf
- main report includes table of contributions

Test problems known to work with gui.TSPApplication.java
data/tsp/prob/
- burma14
- test17
- test26
- speedup10
- speedup15
- speedup20
- speedup25
- speedup30
- ulysses16
- random
- create your own random problem using above optional instructions