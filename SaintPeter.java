import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;



import java.util.Date;


////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////
public class SaintPeter
{
    
    public float diffImg(BufferedImage ctrlImg, BufferedImage compImg)
    {
        // Check that the dimensions are equal.
        if ((ctrlImg.getHeight() != compImg.getHeight()) ||
            (ctrlImg.getWidth()  != compImg.getWidth()))
        {
            System.out.println("The control and comparison images are different.");
            System.exit(0);
        }
        
        // Assume that x and y map to cooresponging pixels in the
        // specified images.
        // System.out.println("Writing out control image.");
        File ctrlFile = new File(new String("/Users/robert/code/projects/minecraft_fishing/ctrl.png"));
        try
        {
            ImageIO.write(ctrlImg, "png", ctrlFile);
        }
        catch(IOException ioe)
        {
        }
        
        // System.out.println("Writing out comparison image.");
        File compFile = new File(new String("/Users/robert/code/projects/minecraft_fishing/comp.png"));
        try
        {
            ImageIO.write(compImg, "png", compFile);
        }
        catch(IOException ioe)
        {
        }
        
        
        //
        float totalPixelDiff = 0;
        float totalPossiblePixelDiff = 255 * (ctrlImg.getHeight() * ctrlImg.getWidth());
        totalPossiblePixelDiff = totalPossiblePixelDiff * 3; // 3 pixels
        
        int rPixelDiff = 0;
        int gPixelDiff = 0;
        int bPixelDiff = 0;
        
        for (int y=0; y < ctrlImg.getHeight(); y++)
        {
            for (int x=0; x < ctrlImg.getWidth(); x++)
            {
                // System.out.println("Pixel Coordinate: " + y + ", " + x);
                int c0 = ctrlImg.getRGB(x,y);
                Color ctrlColor = new Color(c0);
                
                int c1 = compImg.getRGB(x,y);
                Color compColor = new Color(c1);
                
                //int  red = (c & 0x0000FFFF) >> 16;
                //int  green = (c & 0x0000FFFF) >> 8;
                //int  blue = c & 0x0000FFFF;
                /*
                 System.out.println("Color: " +
                 ctrlColor.getRed() + ", " +
                 ctrlColor.getGreen() + ", " +
                 ctrlColor.getBlue());
                 
                 System.out.println("Color: " +
                 compColor.getRed() + ", " +
                 compColor.getGreen() + ", " +
                 compColor.getBlue());
                 */
                // sRGB
                
                int rDiff = ctrlColor.getRed()-compColor.getRed();
                int gDiff = ctrlColor.getGreen()-compColor.getGreen();
                int bDiff = ctrlColor.getBlue()-compColor.getBlue();
                
                rPixelDiff = rPixelDiff + Math.abs(rDiff);
                gPixelDiff = gPixelDiff + Math.abs(gDiff);
                bPixelDiff = bPixelDiff + Math.abs(bDiff);
                
                // System.out.println("- " + rDiff + ", " + gDiff + ", " + bDiff);
                
                // System.out.println("Red:   " + color.getRed());
                // System.out.println("Green: " + color.getGreen());
                // System.out.println("Blue:  " + color.getBlue());
                // System.out.println("");
            }
        }
        totalPixelDiff = rPixelDiff + gPixelDiff + bPixelDiff;
        
        // System.out.println("R Difference: " + rPixelDiff);
        // System.out.println("G Difference: " + gPixelDiff);
        // System.out.println("B Difference: " + bPixelDiff);
        float percPixelDiff = 100 * (totalPixelDiff / totalPossiblePixelDiff);
        // System.out.println("Number of different pixels: " + totalPixelDiff);
        System.out.println("Percentage of Different Pixels: " + percPixelDiff);
        
        return percPixelDiff;
    }
    
    
	public void captureScreen() throws Exception
	{
        System.out.println("captureScreen");
        
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(screenSize.toString());
        
		// Rectangle screenRectangle = new Rectangle(screenSize);
        // height, width, x, y (origin in upper-left corner)
        
        //
        Rectangle smallRect = new Rectangle(600,350,50,50);
		Robot robot = new Robot();
        
        // BUTTON1_MASK   left mouse
        int mask = InputEvent.BUTTON3_MASK; // right mouse
        
        robot.delay(3000);
        
        // initial cast
        robot.mousePress(mask);
        robot.mouseRelease(mask);
        robot.delay(5000);
        
        BufferedImage ctrlImg = robot.createScreenCapture(smallRect);
        
        // PrintWriter writer = new PrintWriter("the-file-name.csv", "UTF-8");
        // writer.println("Time, Difference");
        int count = 0;
        while (true)
        {
            Thread.sleep(200); // .20 seconds
            
            BufferedImage compImg = robot.createScreenCapture(smallRect);
            float diff = diffImg(ctrlImg, compImg);
            
            if (diff > 2.2)
            {
                System.out.println("Did we catch a fish?");
                
                // Right click and pull that fish in!
                robot.mousePress(mask);
                robot.mouseRelease(mask);
                
                // wait 5 seconds for the item to be retrieved...
                robot.delay(5000);
                
                // cast!
                robot.mousePress(mask);
                robot.mouseRelease(mask);
                
                // wait 10 seconds
                System.out.println("Waiting 5 seconds for the bobber to settle.");
                robot.delay(5000);
                
                // Take a new picture.
                compImg = robot.createScreenCapture(smallRect);
            }
            
            // Set the comparison image as the control image.
            ctrlImg = compImg;
            
            // Date date = new Date();
            // writer.println(date.toString() + ", " + diff);
            /*
             // testing
             if (count == 500)
             {
             System.out.println("Limit Encountered: Breaking");
             break;
             }
             */
            
            System.out.println("Count: " + count);
            count = count + 1;
        }
        // writer.close();
	}
    
    
    public static void main(String args[])
    {
        try
        {
            ScreenCapture sc = new ScreenCapture();
            sc.captureScreen();
        }
        catch (Exception e)
        {
            System.err.println("We caught an exception: " + e.getMessage());
        }
        
    }
}