package com.group14.findeyourfriend.chart;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Chart extends Application {

	public static String Title = "Stage";
	public static String xLabel = "Time";
	public static String yLabel = "Battery";
	public static String ChartTitle = "Battery Consumption over time";
	public static String SeriesName = "Time";
	public static ArrayList<XYChart.Data> DataPoints = new ArrayList<>();

	@Override
	public void start(Stage stage) {
		stage.setTitle(Title);
		// defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(xLabel);
		yAxis.setLabel(yLabel);
		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

		lineChart.setTitle(ChartTitle);
		// defining a series
		XYChart.Series series = new XYChart.Series();
		series.setName(SeriesName);
		// populating the series with data
		series.getData().addAll(DataPoints);

		// series.getData().add(new XYChart.Data(1, 23));
		// series.getData().add(new XYChart.Data(2, 14));
		// series.getData().add(new XYChart.Data(3, 15));
		// series.getData().add(new XYChart.Data(4, 24));
		// series.getData().add(new XYChart.Data(5, 34));
		// series.getData().add(new XYChart.Data(6, 36));
		// series.getData().add(new XYChart.Data(7, 22));
		// series.getData().add(new XYChart.Data(8, 45));
		// series.getData().add(new XYChart.Data(9, 43));
		// series.getData().add(new XYChart.Data(10, 17));
		// series.getData().add(new XYChart.Data(11, 29));
		// series.getData().add(new XYChart.Data(12, 25));

		Scene scene = new Scene(lineChart, 800, 600);
		lineChart.getData().add(series);

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}