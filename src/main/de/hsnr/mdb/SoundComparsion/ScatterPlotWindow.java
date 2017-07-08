package de.hsnr.mdb.SoundComparsion;

import java.awt.Panel;

import javax.swing.BoxLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class ScatterPlotWindow extends ApplicationFrame {

	private static final long serialVersionUID = -2729746392204471906L;
	private DefaultCategoryDataset loudness;
	private DefaultCategoryDataset zeroCrossings;
	private DefaultCategoryDataset brightness;
	private DefaultCategoryDataset bandwidth;

	public ScatterPlotWindow(String title, float[] loudness, float[] zeroCrossings, float[] brightness, float[] bandwidth) {
		super(title);
		this.loudness = populateData(loudness, "loudness");
		this.zeroCrossings = populateData(zeroCrossings, "zeroCrossings");
		this.brightness = populateData(brightness, "brightness");
		this.bandwidth = populateData(bandwidth, "bandwidth");

		JFreeChart chartLoudness = ChartFactory.createLineChart("loudness", "time", "amplitude", this.loudness, PlotOrientation.VERTICAL, true,true,false);
		JFreeChart chartZeroCrossings = ChartFactory.createLineChart("Zero Crossings", "time", "amplitude", this.zeroCrossings, PlotOrientation.VERTICAL, true,true,false);
		JFreeChart chartBrightness = ChartFactory.createLineChart("Brightness", "time", "amplitude", this.brightness, PlotOrientation.VERTICAL, true,true,false);
		JFreeChart chartBandwidth = ChartFactory.createLineChart("Bandwidth", "time", "amplitude", this.bandwidth, PlotOrientation.VERTICAL, true,true,false);

		final ChartPanel chartPanelLoudness = new ChartPanel(chartLoudness, true);
		final ChartPanel chartPanelZeroCrossings = new ChartPanel(chartZeroCrossings, true);
		final ChartPanel chartPanelBrightness = new ChartPanel(chartBrightness, true);
		final ChartPanel chartPanelBandwidth = new ChartPanel(chartBandwidth, true);

		Panel panel = new Panel();	
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(chartPanelLoudness);
		panel.add(chartPanelZeroCrossings);
		panel.add(chartPanelBrightness);
		panel.add(chartPanelBandwidth);
		
		setContentPane(panel);
	}	
	
	
	private DefaultCategoryDataset populateData(float[] rawData, String name) {
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		for (int i = 0; i < rawData.length; i++) 
			data.addValue(rawData[i], name , i+"");
		
		return data;
	}
}
