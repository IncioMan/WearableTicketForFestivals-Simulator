package com.group14.findeyourfriend.chart;

import javafx.scene.Scene;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;


public class Chart extends Application {

	public static String Title = "Stage";
	public static String xLabel = "Time";
	public static String yLabel = "Battery";
	public static String ChartTitle = "Battery Consumption over time";
	public static String SeriesName = "Time";
//	public static ArrayList<XYChart.Data> DataPoints = new ArrayList<>();
	public static HashMap<Integer, ArrayList<XYChart.Data>> DataPointsMap = new HashMap<>();
	@Override public void start(Stage stage) {
		stage.setTitle(Title);
		//defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(xLabel);
		yAxis.setLabel(yLabel);
		//creating the chart
		final LineChart<Number,Number> lineChart =
				new LineChart<>(xAxis, yAxis);

		lineChart.setTitle(ChartTitle);
		//defining a series
        ArrayList<XYChart.Series> seriesArrayList = new ArrayList<>();
		//populating the series with data
        for (Integer id: DataPointsMap.keySet()) {
            ArrayList<XYChart.Data> DataPoints = DataPointsMap.get(id);
            XYChart.Series series = new XYChart.Series();
            series.setName("Person: " + id);
            series.getData().addAll(DataPoints);
            seriesArrayList.add(series);
			lineChart.setCreateSymbols(false);
            lineChart.getData().add(series);

        }

//        series.getData().add(new XYChart.Data(1, 23));
//        series.getData().add(new XYChart.Data(2, 14));
//        series.getData().add(new XYChart.Data(3, 15));
//        series.getData().add(new XYChart.Data(4, 24));
//        series.getData().add(new XYChart.Data(5, 34));
//        series.getData().add(new XYChart.Data(6, 36));
//        series.getData().add(new XYChart.Data(7, 22));
//        series.getData().add(new XYChart.Data(8, 45));
//        series.getData().add(new XYChart.Data(9, 43));
//        series.getData().add(new XYChart.Data(10, 17));
//        series.getData().add(new XYChart.Data(11, 29));
//        series.getData().add(new XYChart.Data(12, 25));

        Scene scene  = new Scene(lineChart,800,600);


		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}