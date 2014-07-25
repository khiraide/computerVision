Keone Hiraide
ICS 683 homework 2.

-------------------
INSTRUCTIONS
-------------------

The "hiraide_hw2.zip" contains the directory which contains my source code for homework 2. 
1) Unzip "hiraide_hw2.zip"
3) Download & Install Eclipse Juno IDE.
4) Run Eclipse Juno IDE. 
5) Once Eclipse Juno IDE is running, goto File -> Import -> Existing Projects into Workspace -> Next ->
   Browse to select the root directory, "hiraide_hw2" -> Finish.
3) Click on "Main.java" and run the code!

Once run, my program will find the best threshold value from "balls.gif" using my
implemented Otsu's method. It will use this threshold
to create the binary image which is outputted as, "output.gif". output.gif is then used as input
for my erosion method, which by default uses a disk structuring element with a diameter of 4. 
Once the image has been eroded, the output file of the image is called, "morphology.gif". Next,
I run my classical component algorithm using morphology.gif as input. The amount of connected
components will be displayed to the console.