package de.hsnr.mdb.SoundComparsion;

import java.awt.RenderingHints;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.ApplicationFrame;

public class ScatterPlotWindow extends ApplicationFrame {

	private static final long serialVersionUID = -2729746392204471906L;
	private DefaultCategoryDataset  data;
	//private float[][] data;	
	
	private float[] rawData;



	public ScatterPlotWindow(String title, float[] rawData) {
		super(title);
		this.rawData = rawData;
		

		
		final NumberAxis domainAxis = new NumberAxis("time");
		domainAxis.setAutoRangeIncludesZero(false);
		final NumberAxis rangeAxis = new NumberAxis("amplitude");
		rangeAxis.setAutoRangeIncludesZero(false);
		
		populateData();		
	/*	final FastScatterPlot plot = new FastScatterPlot(this.data, domainAxis, rangeAxis);
		final JFreeChart chart = new JFreeChart("Fast Scatter Plot", plot);
		chart.getRenderingHints().put
			(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
*/
		JFreeChart chart = ChartFactory.createLineChart(title, "time", "amplitude", data, PlotOrientation.VERTICAL, true,true,false);

	
		final ChartPanel panel = new ChartPanel(chart, true);
		
		panel.setMinimumDrawHeight(10);
		panel.setMaximumDrawHeight(2000);
		panel.setMinimumDrawWidth(20);
		panel.setMaximumDrawWidth(2000);
		
		setContentPane(panel);
	}	
	
	
	private void populateData() {
		data = new DefaultCategoryDataset();
		//data = new float[2][rawData.length];
		
		/*for (int i = 0; i < this.rawData.length; i++) {
		    final float x = (float) i;
		    this.data[0][i] = x;
		    this.data[1][i] = rawData[i];
		}*/
		
		for (int i = 0; i < this.rawData.length; i++) {
		    final float x = (float) i;

		    data.addValue(rawData[i], "schools" , ""+x);
		}
	}


}
