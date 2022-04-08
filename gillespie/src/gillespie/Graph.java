package gillespie;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Graph extends JFrame {
	
	XYSeriesCollection dataset =  new XYSeriesCollection();
	HistogramDataset histDataset = new HistogramDataset();
	DefaultCategoryDataset catDataset = 
		      new DefaultCategoryDataset( );  
	
    public Graph() {
        initUI();
    }

    private void initUI() {

		
    	// Run first Algorithm
    	Gillespie g = new Gillespie();
    	g.algorithm(100);
    	XYSeries s = g.getSeries();

    	
    	int[] bins = g.buildHistogramDataset();
		addToDataset(s, bins); 	

    	ChartPanel catPanel = new ChartPanel(createHistBarChart(this.catDataset));
		ChartPanel chartPanel = new ChartPanel(createChart(this.dataset));
		
		
       	setPreferredSize(new Dimension(700, 700));

        JLabel avgSysSize = new JLabel("Avg. System Size: "+ String.valueOf(g.getAvgSystemSize()));
        JLabel leaps = new JLabel("Time: "+ String.format("%.2f", s.getX(s.getItemCount()-1)));
        JLabel nTimesInState = new JLabel("Nubmer of times in state "+String.valueOf(g.getAvgSystemSize())+": "+ String.valueOf(g.getTimes_in_state()));
        JLabel empty1 = new JLabel("");
        JLabel empty2 = new JLabel("");
        JLabel empty3 = new JLabel("");
        JLabel empty4 = new JLabel("");

        JButton addrun = new JButton("Add Run");
        JButton clear = new JButton("Clear");

        addrun.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { 
                Gillespie g = new Gillespie();
                g.algorithm(100);
                XYSeries series = g.getSeries();
                addToDataset(series, g.buildHistogramDataset());
                avgSysSize.setText("Avg. System Size: "+ String.valueOf(g.getAvgSystemSize()));
                leaps.setText("Time: "+ String.format("%.2f", series.getX(series.getItemCount()-1)));
                nTimesInState.setText("Numer of times in state "+String.valueOf(g.getAvgSystemSize())+": "+ String.valueOf(g.getTimes_in_state()));
         
            }
        });
        
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { 
                clearData();
               
            }
        });
        

        setLayout(new BorderLayout());
        JPanel charts = new JPanel(new GridLayout(0, 1, 3, 3));
        charts.add(chartPanel);
        charts.add(catPanel);
        
        charts.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        charts.setBackground(Color.white);
        add(charts, BorderLayout.CENTER);
        

        int gap = 3;
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, gap, gap));
        buttonPanel.add(addrun);
        buttonPanel.add(clear);
        buttonPanel.add(avgSysSize);
        buttonPanel.add(empty1);
        buttonPanel.add(nTimesInState);
        buttonPanel.add(empty2);
        buttonPanel.add(leaps);
        
        
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buttonPanel, BorderLayout.PAGE_END);
        

        pack();
        setTitle("Gillespie Charts");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
    }  
    

	public void addToDataset(XYSeries s, int[] bins) {
		
		
    	this.dataset.addSeries(s);
    	double val = 0.0;
		for (int bin = 0; bin < bins.length; bin++) {
			val += 0.2;
			String colName = String.valueOf(val).substring(0,3);
			this.catDataset.addValue(bins[bin], "Gillespie", colName);
		}
    	
    }
    

    
    public void clearData() {
    	this.dataset.removeAllSeries();
    	System.out.print(this.catDataset.getColumnKeys());
    	this.catDataset.removeRow("Gillespie");
    	

    }
    
private JFreeChart createHistBarChart(DefaultCategoryDataset d) {
    	
		JFreeChart barChart = ChartFactory.createBarChart(
	         "Histogram",           
	         "Time",            
	         "Frequency",            
	         d,          
	         PlotOrientation.VERTICAL,           
	         false, true, true);
		CategoryPlot plot = (CategoryPlot) barChart.getPlot();
		
		return barChart;
    }
    
    private JFreeChart createHistogramChart(double[] values) {
    	
		this.histDataset.addSeries("key", values, 20);
		JFreeChart histogramchart = ChartFactory.createHistogram(
				"Time Leap Histogram",
                "Data", 
                "Frequency", 
                this.histDataset, 
                PlotOrientation.VERTICAL, 
                false, false, false);
		XYPlot plot = (XYPlot) histogramchart.getPlot();
		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

        renderer.setPaint(new Color(0.0f, 1.0f, 0.0f, 0.3f));
		
		return histogramchart;
    }
    

    

    private JFreeChart createChart(final XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Coffee Shop Activity",
                "Time",
                "People in shop",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        XYPlot plot = chart.getXYPlot();
        NumberAxis domain = (NumberAxis) plot.getRangeAxis();
        domain.setRange(0.00, 5.00);
        domain.setTickUnit(new NumberTickUnit(1.00));
        domain.setVerticalTickLabels(true);
        var renderer = new XYLineAndShapeRenderer();

        renderer.setPaint(new Color(0.0f, 1.0f, 0.0f, 0.3f));

        
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(false);
  

        chart.setTitle(new TextTitle("Coffee Shop System",
                        new Font("Serif", Font.BOLD, 18))
        );

        return chart;
    }

}