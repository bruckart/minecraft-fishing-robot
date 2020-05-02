

import java.io.IOException;
import java.io.File;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;

import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import java.net.URL;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level; 
import java.util.logging.Logger; 
import java.util.Timer;
import java.util.TimerTask;


class FishCraft
{
    private static final Logger LOGGER = 
        Logger.getLogger(FishCraft.class.getName());

    private JFrame m_screenCaptureFrame = new JFrame("Image Capture");
    private JFrame m_controlPanelFrame = new JFrame("Control Panel");

	private JLabel m_label = new JLabel();

	private JPanel m_status = new JPanel();
	private JLabel m_statusLabel = new JLabel();

	private Robot      m_robot;
	private Rectangle  m_rect = new Rectangle(0,0,0,0);

	private Timer      m_timer = new Timer();

	private double m_percentImageDifference = 5.5;

	private double m_x;
	private double m_y;

	private float m_h;
	private float m_w;

    FishCraft()
    {
    	// showSplash();

    	LOGGER.fine("Initializing Robot"); 
	    try
	    {
	        m_robot = new Robot();
	    }
	    catch (Exception e)
	    {
	        System.err.println("We caught an exception: " + e.getMessage());
	    }

		LOGGER.fine("Instantiating Interface"); 
		buildScreenCaptureGui();

		buildControlPanelGui();

		m_x = m_screenCaptureFrame.getLocation().getX();
		m_y = m_screenCaptureFrame.getLocation().getY();
		m_h = m_screenCaptureFrame.getWidth();
		m_w = m_screenCaptureFrame.getHeight();

		m_rect.setRect(m_x, m_y, (double) m_w, (double) m_h);

		m_statusLabel.setText("X: " + m_x + ", Y: " + m_y + 
			                  " | Width: " + m_w + ", Height: " + m_h);
    }

    void updateImage()
    {
		m_rect.setRect(m_x, m_y+25, (double) m_w, (double) m_h-25);

    	m_timer.schedule(new TimerTask()
    	{
        	public void run()
        	{
        		LOGGER.info("Taking Snapshot at Coordinates: " + m_x + ", " + m_y);
        		LOGGER.info("Image Width: " + m_w + ", Image Height: " + m_h);

				captureScreenshot();
        	}
    	}, 3000);
    }

    private BufferedImage captureScreenshot()
    {
		BufferedImage bufferedImage = m_robot.createScreenCapture(m_rect);

	    ImageIcon icon = new ImageIcon(bufferedImage);
		m_label.setIcon(icon);	

		return bufferedImage;
    }

    void buildScreenCaptureGui()
    {
		m_screenCaptureFrame.addComponentListener(new ComponentAdapter()
		{
            public void componentMoved(ComponentEvent e)
            {
                m_x = m_screenCaptureFrame.getLocation().getX();
                m_y = m_screenCaptureFrame.getLocation().getY();
				m_statusLabel.setText("X: " + m_x + ", Y: " + m_y + 
					                  " | Width: " + m_w + ", Height: " + m_h);
            }
        });

		m_screenCaptureFrame.addComponentListener(new ComponentAdapter()
		{
            public void componentResized(ComponentEvent e)
            {
                m_w = m_screenCaptureFrame.getWidth();
                m_h = m_screenCaptureFrame.getHeight();
                m_statusLabel.setText("X: " + m_x + ", Y: " + m_y + 
		                              " | Width: " + m_w + ", Height: " + m_h);
            }
        });

		m_screenCaptureFrame.setPreferredSize(new Dimension(400, 400));
		m_screenCaptureFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_screenCaptureFrame.setLocationByPlatform(true);

        m_screenCaptureFrame.add(m_label);

		createMenuBar();

		createStatusBar();

        m_screenCaptureFrame.pack();
        m_screenCaptureFrame.setVisible(true);
    }

    void buildControlPanelGui()
    {
        m_controlPanelFrame.setLayout(new GridBagLayout());
		m_controlPanelFrame.setPreferredSize(new Dimension(160, 100));
		m_controlPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_controlPanelFrame.setLocationByPlatform(true);

		JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        JButton button1 = new JButton();
        button1.setText("Capture Image");
        button1.setToolTipText("Capture the Image to Process");
        button1.setPreferredSize(new Dimension(125, 100));
        pane.add(button1);
        button1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                LOGGER.fine("UpdateImage");
                updateImage();
            }          
        });

        JButton button2 = new JButton();
        button2.setText("Detect Change");
        button2.setToolTipText("Analyze Image Differences");
        button2.setPreferredSize(new Dimension(125, 100));
        pane.add(button2);
        button2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                // Once this button is pressed, wait 3 seconds and then
                // begin computing the differences of images every 1 second. 
                // If a difference is detected, right click.
                LOGGER.fine("Detect Change");

                m_robot.delay(3000);
                rightClick(); 
                m_robot.delay(5000);

                try
                {
                    detectionLoop();
                }
                catch (Exception e)
                {
                    System.err.println("We caught an Exception: " + e.getMessage());
                }
            }          
        });

		m_controlPanelFrame.add(pane);

        m_controlPanelFrame.pack();
        m_controlPanelFrame.setVisible(true);
    }

    private void createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Exit"); // , exitIcon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));

        fileMenu.add(eMenuItem);
        menuBar.add(fileMenu);

        m_screenCaptureFrame.setJMenuBar(menuBar);
    }

    private void createStatusBar()
    {
		m_status.setBorder(new BevelBorder(BevelBorder.LOWERED));
		m_screenCaptureFrame.add(m_status, BorderLayout.SOUTH);
		m_status.setPreferredSize(new Dimension(m_screenCaptureFrame.getWidth(), 16));
		m_status.setLayout(new BoxLayout(m_status, BoxLayout.X_AXIS));

		m_statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

		m_status.add(m_statusLabel);
    }

    private void rightClick()
    {
        // right click
        m_robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        m_robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    public float captureDifferences(BufferedImage ctrlImg, BufferedImage compImg)
    {
        // Check that the dimensions are equal.
        if ((ctrlImg.getHeight() != compImg.getHeight()) ||
            (ctrlImg.getWidth()  != compImg.getWidth()))
        {
            LOGGER.fine("The control and comparison images are different sizes.");
            System.exit(0);
        }

        float totalPixelDiff = 0;
        float totalPossiblePixelDiff = 255 * (ctrlImg.getHeight() * ctrlImg.getWidth());
        totalPossiblePixelDiff = totalPossiblePixelDiff * 3;
        
        int rPixelDiff = 0;
        int gPixelDiff = 0;
        int bPixelDiff = 0;
        
        for (int y=0; y < ctrlImg.getHeight(); y++)
        {
            for (int x=0; x < ctrlImg.getWidth(); x++)
            {
                int c0 = ctrlImg.getRGB(x,y);
                Color ctrlColor = new Color(c0);
                
                int c1 = compImg.getRGB(x,y);
                Color compColor = new Color(c1);

                int rDiff = ctrlColor.getRed()-compColor.getRed();
                int gDiff = ctrlColor.getGreen()-compColor.getGreen();
                int bDiff = ctrlColor.getBlue()-compColor.getBlue();
                
                rPixelDiff = rPixelDiff + Math.abs(rDiff);
                gPixelDiff = gPixelDiff + Math.abs(gDiff);
                bPixelDiff = bPixelDiff + Math.abs(bDiff);
            }
        }
        totalPixelDiff = rPixelDiff + gPixelDiff + bPixelDiff;

        float percPixelDiff = 100 * (totalPixelDiff / totalPossiblePixelDiff);
        LOGGER.fine("Percentage of Different Pixels: " + percPixelDiff);
        
        return percPixelDiff;
    }


	public void detectionLoop() throws Exception
	{
        BufferedImage controlImage = captureScreenshot();

        int count = 0;
        while (true)
        {
            Thread.sleep(1000);
            
            BufferedImage compareImage = captureScreenshot();
            float diff = captureDifferences(controlImage, compareImage);

            if (diff > m_percentImageDifference)
            {
                LOGGER.info("Did we catch a fish?");
                
                // Right click and pull that fish in!
                rightClick();
                
                // wait 5 seconds for the item to be retrieved...
                m_robot.delay(5000);
                
                // cast!
                rightClick();
                
                LOGGER.info("Waiting 5 seconds for the bobber to settle.");
                m_robot.delay(5000);
                
                // Take a new picture.
                compareImage = captureScreenshot();
            }
            
            // Set the comparison image as the control image.
            controlImage = compareImage;

            LOGGER.fine("Count: " + count);
            count = count + 1;
        }
	}

	private void showSplash()
	{
		URL url = this.getClass().getResource("/res/splashscreen.png");
        if (url == null)
        {

        	LOGGER.fine("SplashScreen Image Not Found!");
            return;
        }

    	JWindow window = new JWindow();

		Icon splashIcon = new ImageIcon(url); 
 		JLabel label = new JLabel(splashIcon);
		window.getContentPane().add(label, SwingConstants.CENTER);
        window.setBounds(500, 150, 300, 200);
		window.setVisible(true);

		try
		{
			Thread.sleep(3000);
		}
			catch(InterruptedException e)
		{
			e.printStackTrace();
		}

	    window.setVisible(false);
		window.dispose();
	}

	public static void main(String args[])
	{
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);

		LOGGER.setLevel(Level.ALL);
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		LOGGER.fine("Initializing");
		FishCraft fc = new FishCraft();
	}
}
