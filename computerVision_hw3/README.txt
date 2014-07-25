Keone Hiraide
ICS 683 homework 3.

-------------------
INSTRUCTIONS
-------------------

The "hiraide_hw3.zip" contains the directory which contains my source code for homework 3 and my report. 
1) Unzip "hiraide_hw3.zip"
3) Download & Install Eclipse Juno IDE.
4) Run Eclipse Juno IDE. 
5) Once Eclipse Juno IDE is running, goto File -> Import -> Existing Projects into Workspace -> Next ->
   Browse to select the root directory, "computerVision_hw3" -> Finish.
3) Click on "Runner.java" and run the code!

In Runner.java, you can set the sigma value that will be used. By default, sigma = 3.5. Once run, my program will find take board.tif as input. Outputted are four jpg files. They are the smoothed image after applying the Gaussian Filter to board.tif, the gradientMagnitude of the smoothedImage, the non-maximum supression from the gradient magnitude, and lastly postHysteresis. 