package hgcore.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;

public class ARPT_Cast extends JFrame implements ChangeListener, ItemListener, ActionListener{
	
	HG_Core core = new HG_Core();
	
	private JPanel filterPane = new JPanel(); //west
	private JPanel contentPane;// = new JPanel(); //center
	private JPanel HG_RecPane = new JPanel(); //east
	private JPanel threshPane = new JPanel(); // south
	
	private JLabel filterLbl = new JLabel("FILTER");
	private JButton trueColorNonCVBtn = new JButton("True Color Non-CV");
	private JButton trueColorCVBtn = new JButton("True Color CV");
	private JButton backgroundSubtractionBtn = new JButton("Background Subtraction");
	private JButton CVBtn = new JButton("CV Filter");
	
	private JLabel HG_RecLbl = new JLabel("HAND RECOGNITION");
	private JCheckBox contourBtn = new JCheckBox("Contour");
	private JCheckBox convexHullBtn = new JCheckBox("Convex Hulls");
	private JCheckBox convexityDefectsBtn = new JCheckBox("Convexity Defects");
	private JCheckBox boundingRect = new JCheckBox("Bounding Box");
	private JCheckBox cogBtn = new JCheckBox("Center Of Gravity / Mass");
	
	JSlider threshSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
	JLabel tVal = new JLabel("Current Threshold: 50");
	
	

	public ARPT_Cast()	{
		super("ARPT: HG_Rec | B_Sub");
		setLayout(new BorderLayout());
		URL iconURL = getClass().getResource("/images/favi.png");
		// iconURL is null when not found
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
		
		//filter components
		add(filterPane, BorderLayout.WEST);
		filterPane.setBackground(Color.gray);
		filterPane.setLayout(new GridLayout(0,1));
		filterPane.add(filterLbl); filterLbl.setHorizontalAlignment(JLabel.CENTER);
		//btn
		filterPane.add(trueColorNonCVBtn);	trueColorNonCVBtn.setEnabled(false
				); trueColorNonCVBtn.addActionListener(this); 
		filterPane.add(trueColorCVBtn); trueColorCVBtn.addActionListener(this);
		filterPane.add(backgroundSubtractionBtn); backgroundSubtractionBtn.addActionListener(this);
		filterPane.add(CVBtn); CVBtn.addActionListener(this);
		
		
		
		core.start();
		contentPane = new JPanel()	{
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage dimg = core.getImage();
//                resizeB(dimg, 500, 700);
                g.drawImage(dimg, 0, 0, null);
                repaint();
            }
        };//pane
        
		//content components
		add(contentPane, BorderLayout.CENTER);
		
		
		//hg_rec component
		add(HG_RecPane, BorderLayout.EAST);
		HG_RecPane.setBackground(Color.gray);
		HG_RecPane.add(HG_RecLbl); HG_RecLbl.setHorizontalAlignment(JLabel.CENTER);
		HG_RecPane.setLayout(new GridLayout(0,1));
		HG_RecPane.add(contourBtn);	contourBtn.addItemListener(this);
		HG_RecPane.add(convexHullBtn);	convexHullBtn.addItemListener(this);
		HG_RecPane.add(convexityDefectsBtn);	convexityDefectsBtn.addItemListener(this);
		HG_RecPane.add(boundingRect);	boundingRect.addItemListener(this);
		HG_RecPane.add(cogBtn); cogBtn.addItemListener(this);
		
		
		//threshold
		add(threshPane, BorderLayout.SOUTH);
		threshPane.setLayout(new BorderLayout());
//		threshPane.setBackground(Color.BLUE);
        threshSlider.setMajorTickSpacing(5);
        threshSlider.setPaintTicks(true);
        //        
        threshPane.add(threshSlider);
        threshPane.add(tVal, BorderLayout.NORTH);
        
        threshSlider.addChangeListener(this);
        
		
	}//construct
	
	public BufferedImage resizeB(BufferedImage img, int newW, int newH) {  
	    int w = img.getWidth();  
	    int h = img.getHeight();  
	    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
	    Graphics2D g = dimg.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
	    g.dispose();  
	    return dimg;  
	}  
	
	public void actionPerformed(ActionEvent ae)	{
		
		Object src = ae.getSource();
		
		if(src == trueColorNonCVBtn)	{
			trueColorNonCVBtn.setEnabled(false);
		}//if
		else trueColorNonCVBtn.setEnabled(true);
		
		if(src == backgroundSubtractionBtn)	{
			core.backgroundSubtraction = true;
			core.filterCV = false;
			backgroundSubtractionBtn.setEnabled(false);
		}//if
		else	{
			core.backgroundSubtraction = false;
			backgroundSubtractionBtn.setEnabled(true);
		}//else
		
		// CV BUTTON!
		if(src == CVBtn)	{
			core.filterCV = true;
			CVBtn.setEnabled(false);
		}//if
		else	{
			core.filterCV = false;
			CVBtn.setEnabled(true); 
		}//else
		
		// TRUE COLOR CV BUTTON
		if(src == trueColorCVBtn)	{
			core.filterCV = true;
			core.trueColorCV = true;
			trueColorCVBtn.setEnabled(false);
		}//if
		else	if(src == CVBtn){
			core.filterCV = true;
			core.trueColorCV = false;
			trueColorCVBtn.setEnabled(true);
		}//else
			
	}//buttonStateChange
	
	public void itemStateChanged(ItemEvent e) {
	    Object source = e.getItemSelectable();
	    
	    if (source == contourBtn) {
	    	core.viewContour = !core.viewContour;
	    }//if
	    
	    else if (source == convexHullBtn) {
	    	core.viewConvexHull = !core.viewConvexHull;
	    }//else
	    
	    else if (source == convexityDefectsBtn) {
	    	core.viewConvexityDefects = !core.viewConvexityDefects;
	    }//else
	    
	    else if (source == boundingRect) {
	    	core.viewBoundingRect = !core.viewBoundingRect;
	    }//else
	    
	    else if (source == cogBtn) {
	    	core.viewCOG = !core.viewCOG;
	    }//else
	}//itemStateChanged
	
	public void stateChanged(ChangeEvent e)	{
		int value = threshSlider.getValue();
		core.setThresh((double)value);
		
		tVal.setText("Current Threshold: " + value);
	}//event
	
	public static void main(String[] args)	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (InstantiationException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (UnsupportedLookAndFeelException e) {
	        e.printStackTrace();
	    }

		ARPT_Cast	frame = new ARPT_Cast();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure to close this window?", "Close Application?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		            System.exit(-1);
		        }
		    }
		});
	}//main
	
}//class
