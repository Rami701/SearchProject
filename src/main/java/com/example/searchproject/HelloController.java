package com.example.searchproject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HelloController {

    private Graph graph;
    private File citiesFile;
    private File roadsFile;
    private File airDistanceFile;

    private ArrayList<City> cities = new ArrayList<>(); // this list will hase all cities objects to make things easier

    @FXML
    private Button searchBtn;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private Button airDistanceFileChooserBtn;

    @FXML
    private Button citiesFileChooserBtn;

    @FXML
    private Button roadsFileChooserBtn;

    @FXML
    private ComboBox<String> sourceComboBox;

    @FXML
    private ComboBox<String> destinationComboBox;

    @FXML
    private ComboBox<String> algorithmComboBox;

    @FXML
    private Text pathString;
    @FXML
    private Text txtTotalCost;
    @FXML
    private Text txtTotalTime;

    @FXML
    void choseCitiesFile(ActionEvent event) {

    }
    @FXML
    void openCitiesFileChooser(ActionEvent event) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        File initialDirectory = new File("C:\\Users\\r4mim\\IdeaProjects\\SearchProject\\src\\main\\resources");
        fc.setInitialDirectory(initialDirectory);
        citiesFile = fc.showOpenDialog(null);

        if (citiesFile != null){
            readCitiesFile(citiesFile);
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
            roadsFileChooserBtn.setDisable(false);
        }
    }

    @FXML
    void openRoadsFileChooser(ActionEvent event) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        File initialDirectory = new File("C:\\Users\\r4mim\\IdeaProjects\\SearchProject\\src\\main\\resources");
        fc.setInitialDirectory(initialDirectory);
        roadsFile = fc.showOpenDialog(null);

        if (roadsFile != null){
            readRoadsFile();
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
            airDistanceFileChooserBtn.setDisable(false);
        }
    }

    @FXML
    void openAirDistanceFileChooser(ActionEvent event) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        File initialDirectory = new File("C:\\Users\\r4mim\\IdeaProjects\\SearchProject\\src\\main\\resources");
        fc.setInitialDirectory(initialDirectory);
        airDistanceFile = fc.showOpenDialog(null);
        // after choosing the file, this code will check if its valid using the isValidFile method
        if (airDistanceFile != null){
            readAirDistanceFile();
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
        }
    }

    void readCitiesFile(File selectedFile) throws FileNotFoundException { // this method will read the cities file
        Scanner scanner = new Scanner(selectedFile);
        try{
            while (scanner.hasNext()){
                String currentLine = scanner.nextLine(); // scan each line
                String[] lineParts = currentLine.split(", "); // split the line values (name, y, x)
                int cityX = findX(Double.parseDouble(lineParts[2])); // calculate the city position on the image for x and y
                int cityY = findY(Double.parseDouble(lineParts[1]));
                City city = new City(lineParts[0], cityX, cityY); // make a new City object with the values we found
                cities.add(city); // add the new city to the array of all cities
            }
            putOnMap();
        }catch (Exception e){
                System.out.println("Error reading cities file: " + e);
        }
    }

    void readRoadsFile() throws FileNotFoundException {
        // This method will read the roads file and get the data from it
        // then for each city of the cities, the code will add the adjacent cities for that city in its hash map with the cost for each one.
        Scanner scanner = new Scanner(roadsFile);
        try {
            while (scanner.hasNext()){
                // first we extract the cities names and the cost from each line
                String currentLine = scanner.nextLine();
                String[] lineParts = currentLine.split(",");
                if (lineParts.length < 1){
                    break;
                }
                String cityName = lineParts[0].replaceAll("[^\\p{Print}]", "");
                String adjacentCityName = lineParts[1].replaceAll("[^\\p{Print}]", "");

                int cost = Integer.parseInt(lineParts[2]);

                City city = null, adjacentCity = null;
                for (City tempCity : cities) { // search for the city that in the file in the cities ArrayList

                    if (tempCity.getName().trim().equals(cityName.trim())) {
                        city = tempCity;
                    } else if (tempCity.getName().equals(adjacentCityName)) {
                        adjacentCity = tempCity;
                    }
                }
                city.adjacentCities.put(adjacentCity, cost); // add the adjacency
            }

        }catch (Exception e){
            System.out.println("Error reading roads file: " + e);
        }
    }

    void readAirDistanceFile() throws FileNotFoundException {
        // This method will read the air distance file
        // for every city from all cities the method will add the air distance from the city to all other cities in the hash map
        Scanner scanner = new Scanner(airDistanceFile);
        try {
            while (scanner.hasNext()){
                // extract the information from the file
                String currentLine = scanner.nextLine();
                String[] lineParts = currentLine.split(", ");
                String firstCityName = lineParts[0].replaceAll("[^\\p{Print}]", "");
                String secondCityName = lineParts[1].replaceAll("[^\\p{Print}]", "");
                int cost = Integer.parseInt(lineParts[2]);

                // these variables are going to hold the City objects of the first and second cities in the current line of the file
                City firstCity = null;
                City secondCity = null;

                // now we loop to find the right City objects
                for (City city : cities){
                    if (city.getName().equals(firstCityName)){
                        firstCity = city;
                    }else if(city.getName().equals(secondCityName)){
                        secondCity = city;
                    }
                }

                // we put the two cities in each other's hash map for air distance with the cost
                firstCity.airDistance.put(secondCity, cost);
                secondCity.airDistance.put(firstCity, cost);
            }
        }catch (Exception e){
            System.out.println("Error reading air distance file: " + e);
        }
    }

    public int findX(double x){ // this method will take the longitude (x) of the city globally and calculate the position of the city on the image(pixels)
        double finalX;
        finalX = (x - 33.84339) / (36.558532 - 33.84339);
        return (int) (finalX * 1650);
    }

    public int findY(double y){
        double finalY;
        finalY = (33.478121 - y) * 623;
        return (int) finalY;
    }

    void putOnMap(){
        // put the buttons and labels for each city on the map
        for(City city : cities){
            Button button = new Button("");
            button.setLayoutX(city.getX());
            button.setLayoutY(city.getY());
            button.setPrefHeight(10);
            button.setMinHeight(10);
            button.setPrefWidth(10);
            button.setMinWidth(10);
            button.setStyle("-fx-background-color: #00C4FF; " +
                    "-fx-background-radius: 100px; ");

            Label label = new Label(city.getName());
            label.setLayoutX(city.getX() + 5);
            label.setLayoutY(city.getY() + 10);
            mapPane.getChildren().add(label);
            mapPane.getChildren().add(button);
            fillComboBox(sourceComboBox, destinationComboBox, algorithmComboBox);
        }
    }

    void fillComboBox(ComboBox<String> sourceBox, ComboBox<String> destinationBox, ComboBox<String> algoBox){
        // This method is going to fill the source and destination comboBoxes with the cities names

        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        for(City city : cities){ // put all names in ArrayLIst
            list.add(city.getName());
        }

        list2.add("A*");
        list2.add("BFS");

        // add all the names from the list to the boxes
        sourceBox.setItems(FXCollections.observableList(list));
        destinationBox.setItems(FXCollections.observableList(list));
        algoBox.setItems(FXCollections.observableList(list2));

        // after filling all comboBoxes, we give each one a listener to listen for any change from the comboBox
        // because each time the user changes any of the inputs the system will check if all inputs are selected, so it enables the search button
        sourceBox.setOnAction((event) -> {
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
        });

        destinationBox.setOnAction((event) -> {
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
        });

        algoBox.setOnAction((event) -> {
            if (isAllInputsSelected()){
                searchBtn.setDisable(false);
            }
        });

    }

    boolean isAllInputsSelected(){ // this method is going to check if all inputs needed to start searching are set.
        if (citiesFile != null && roadsFile != null && airDistanceFile != null && sourceComboBox.getValue() != null && destinationComboBox.getValue() != null && algorithmComboBox.getValue() != null){
            return true;
        }
        return false;
    }

//    List<City> AStar(City start, City goal){
//        long startTime = System.nanoTime(); // capture the starting time
//
//        HashMap<City, Integer> openList = new HashMap<>();
//        HashMap<City, Integer> closedList = new HashMap<>();
//        HashMap<City, City> parent = new HashMap<>();
//
//        openList.put(start, 0);
//
//        City currentCity = null;
//        while (!openList.isEmpty()){
//            currentCity = findMinCost(openList);
//
//            int currentCityCost = openList.get(currentCity);
//
//            openList.remove(currentCity);
//
//            if (currentCity == goal){
//                System.out.println("We found a solution!!");
//                // Capture the end time
//                long endTime = System.nanoTime();
//
//                // Calculate the elapsed time
//                long elapsedTime = endTime - startTime;
//                System.out.println(elapsedTime);
//                txtTotalTime.setText("Total time: " + elapsedTime);
//                return reconstructPath(parent, goal);
//            }
//
//            for (Map.Entry<City, Integer> neighbor : currentCity.adjacentCities.entrySet()){
//                int currentNeighborCost = findCost(currentCityCost, currentCity, neighbor.getKey(), goal);
//
//                if (openList.containsKey(neighbor.getKey())){
//                    if (openList.get(neighbor.getKey()) <= currentNeighborCost){
//                        continue;
//                    }
//                }else if(closedList.containsKey(neighbor.getKey())){
//                    if (closedList.get(neighbor.getKey()) <= currentNeighborCost){
//                        continue;
//                    }
//                    openList.put(neighbor.getKey(), neighbor.getValue());
//                    closedList.remove(neighbor.getKey());
//                }else{
//                    openList.put(neighbor.getKey(), currentNeighborCost);
//                }
//                parent.put(neighbor.getKey(), currentCity);
//            }
//            closedList.put(currentCity, currentCityCost);
//        }
//        if (currentCity != goal){
//            System.out.println("Error finding path!!!!!!!!!!!!!!!!!");
//        }
//        return null;
//    }
//    implementation with priority queue
    public List<City> AStar(City start, City goal) {
        double startTime = System.nanoTime(); // capture the starting time
        Map<City, Integer> costSoFar = new HashMap<>();
        Map<City, City> parent = new HashMap<>();

        PriorityQueue<City> openList = new PriorityQueue<>(Comparator.comparingInt(costSoFar::get));
        Set<City> closedList = new HashSet<>();

        costSoFar.put(start, 0);
        openList.add(start);

        while (!openList.isEmpty()) {
            City currentCity = openList.poll();

            if (currentCity == goal) {
                System.out.println("We found a solution!!");
                // Capture the end time
                double endTime = System.nanoTime();

                // Calculate the elapsed time
                double elapsedTime = (endTime - startTime) / 1000;
                System.out.println(elapsedTime);
                txtTotalTime.setText("Total time: " + elapsedTime + " micro seconds" );
                return reconstructPath(parent, goal);
            }

            closedList.add(currentCity);

            for (Map.Entry<City, Integer> neighbor : currentCity.adjacentCities.entrySet()) {
                City neighborCity = neighbor.getKey();
                int currentCityCost = costSoFar.getOrDefault(currentCity, Integer.MAX_VALUE);
                int currentNeighborCost = findCost(currentCityCost, currentCity, neighborCity, goal);

                if (closedList.contains(neighborCity)) {
                    if (currentNeighborCost >= costSoFar.getOrDefault(neighborCity, Integer.MAX_VALUE)) {
                        continue;
                    }
                    closedList.remove(neighborCity);
                }

                if (currentNeighborCost < costSoFar.getOrDefault(neighborCity, Integer.MAX_VALUE)) {
                    costSoFar.put(neighborCity, currentNeighborCost);
                    parent.put(neighborCity, currentCity);
                    openList.add(neighborCity);
                }
            }
        }

        System.out.println("Error finding path!");
        return null;
    }

    City findMinCost(HashMap<City, Integer> openList){
        int min = Integer.MAX_VALUE;
        City tempCity = null;

        for (Map.Entry<City, Integer> city : openList.entrySet()){
            if (city.getValue() < min){
                min = city.getValue();
                tempCity = city.getKey();
            }
        }
        return tempCity;
    }

    int findCost(int currentCityCost, City currentCity, City neighbor, City goal){
        int cost = 0;
        int airDistance;
        if (neighbor == goal){
            airDistance = 0;
        }else{
            airDistance = neighbor.airDistance.get(goal);
        }
        try {

            cost += currentCityCost + currentCity.adjacentCities.get(neighbor) + airDistance;
        }catch (Exception e){
            System.out.println("Current city: " + currentCity.getName() + ",  neighbor: " + neighbor.getName());
        }

        return cost;
    }


    @FXML
    void searchBtnOnClick(ActionEvent event) {
        // when we click on the search button the algorithm will start

        // first we extract the start and goal cities form the combo boxes so we can give them to the algorithm
        String startCityName = sourceComboBox.getValue();
        String goalCityName = destinationComboBox.getValue();
        City startCity = null;
        City goalCity = null;

        for (City city : cities){
            if (city.getName().equals(startCityName)){
                startCity = city;
                continue;
            }
            if (city.getName().equals(goalCityName)){
                goalCity = city;
            }
        }

        List<City> path =  AStar(startCity, goalCity);
    }

    private List<City> reconstructPath(Map<City, City> parent, City goal) {
        // this method will reconstruct the path and store it in a List
        List<City> path = new ArrayList<>();
        City currentCity = goal;

        while (currentCity != null) {
            path.add(currentCity);
            currentCity = parent.get(currentCity);
        }

        Collections.reverse(path);
        constructPathString(path);
        calculateTotalCost(path);
        drawPath(path);
        return path;
    }

    void calculateTotalCost(List<City> path){
        int totalCost = 0;
        for (int i = 0; i < path.size(); i++){
            if (i == (path.size() - 1)){
                break;
            }
            totalCost += path.get(i).adjacentCities.get(path.get(i + 1));
        }
        txtTotalCost.setText("Total cost: " + totalCost);
    }

    void constructPathString(List<City> path){
        String finalString = "Path: ";

        for (int i = 0; i < path.size(); i++){
            if (i == path.size() - 1){
                finalString = finalString.concat(path.get(i).getName());
                break;
            }
            int cost = path.get(i).adjacentCities.get(path.get(i + 1));
            finalString = finalString.concat(" " + path.get(i).getName() + " -" + cost + "Km-> ");
        }

        System.out.println(finalString);
        pathString.setText(finalString);
    }

    void drawPath(List<City> path){
        ArrayList<Line> prevLines = new ArrayList<>();

        for (Object obj : mapPane.getChildren()){
            if (obj.getClass() == Line.class){
                prevLines.add((Line) obj);
            }
        }

        for (Line line : prevLines){
            mapPane.getChildren().remove(line);
        }

        for (int i = 0; i < path.size(); i++){
            if (i == path.size() - 1){
                break;
            }
            City city1 = path.get(i);
            City city2 = path.get(i + 1);

            Line line = new Line(city1.getX(), city1.getY(), city2.getX(), city2.getY());
            line.setStroke(Color.BLUE);
            line.setStrokeWidth(3);
            mapPane.getChildren().add(line);
        }
    }
}