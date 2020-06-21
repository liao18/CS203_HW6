import java.awt.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * Class Life - this program creates a variant of Conway's Game of Life
 * 
 * @Jonathan Liao
 * @2.22.2015 
 */
public class Life implements KeyEventDispatcher
{

    /*======================================================================
     * Constants
     *----------------------------------------------------------------------
     */

    //Size of the board
    public static final int WINDOW_SIZE = 500;  //square window with this many
    //  pixels on a side
    public static final int NUM_ROWS_COLS = 50; //number of squares per row/col
    public static final int DELAY = 100;        //number of milliseconds between
    //  generations

    /*======================================================================
     * Instance Variables
     *----------------------------------------------------------------------
     */
    protected int cellSize = WINDOW_SIZE / NUM_ROWS_COLS;
    protected boolean[][] board;               // 2D array to store the game board

    /*======================================================================
     * Methods
     *----------------------------------------------------------------------
     */

    /**
     * clearBoard
     *
     * sets all cells in the board to DEAD (false).
     *
     */
    protected void clearBoard()
    {
        for(int i = 0; i < NUM_ROWS_COLS; i++) {
            for(int j = 0; j < NUM_ROWS_COLS; j++) {
                board[i][j] = false;
            }
        }
    }//clearBoard

    /**
     * drawBoard
     *
     * This methods draws the current state of the board on a given canvas.
     * 
     * @param  g   the Graphics object for this application
     */
    public void drawBoard(Graphics g)
    {
        //
        for(int j = 0; j < NUM_ROWS_COLS; j++) {
            for(int i = 0; i < NUM_ROWS_COLS; i++) {
                if(j == 0 || j == NUM_ROWS_COLS -1 || i == 0 || i == NUM_ROWS_COLS -1) { //draw dead zone border
                    g.setColor(Color.black);
                    g.fillRect(i*cellSize,j*cellSize,cellSize,cellSize);
                }
                else if(board[j][i] == true) { //draw alive dots
                    g.setColor(Color.red.brighter().brighter());
                    g.fillOval(i*cellSize,j*cellSize,cellSize,cellSize);
                }
                else {//draw dead dots
                    g.setColor(Color.green.darker());
                    g.fillOval(i*cellSize,j*cellSize,cellSize,cellSize);
                }
            }
        }
    }//drawBoard

    /**
     * initTenInARow
     *
     * clears the board using the clearBoard method and then and places a line
     * of 10 living cells in a row in the center of it
     */
    protected void initTenInARow()
    {
        clearBoard();
        int middle = (int)NUM_ROWS_COLS/2; //if there is an even number of rows, the upper row will be selected
        for(int i = middle -(10/2); i < middle +(10/2); i++) {
            board[middle][i] = true;
        }

    }//initTenInARow

    /**
     * handleKeyPress
     *
     * this method takes a single character command.  If that character is one
     * of the valid command characters, an appropriate add or init method is
     * called to change the board appropriately.  Invalid characters are
     * ignored.  Valid characters and the associated methods are:
     *   c - calls clearBoard
     *   t - calls initTenInARow
     *   r - calls initRandom
     *   g - calls addGlider
     *
     * @param key  the character command to process
     */
    protected void handleKeyPress(char key)
    {
        if(key == 'c')
            clearBoard();
        else if(key == 't')
            initTenInARow();
        else if(key == 'r')
            initRandom();
        else if(key == 'g')
            addGlider();

    }//handleKeyPress

    /**
     * initRandom
     *
     * This method initializes all cells on the board (except the dead zone)
     * to random values.
     *
     * Recommendation:  aim for a ratio of 1 alive cell per 3 dead ones.
     */
    protected void initRandom()
    {
        for(int i = 1; i < NUM_ROWS_COLS - 1; i++) {
            for(int j = 1; j < NUM_ROWS_COLS - 1; j++) {
                int rand = (int)(4 * Math.random()); //only four possible values: 0, 1, 2, 3

                if (rand == 0) //if rand is [0,1)
                    board[i][j] = true;
                else if (rand == 1 || rand == 2 || rand == 3) //if rand is [1,4)
                    board[i][j] = false;
                else
                    System.out.println("error in initRandom() method");
            }
        }
    }//initRandom

    /**
     * addGlider
     *
     * adds a "glider" configuration to the board:
     *
     *        oo
     *       o o
     *         o
     *
     * The glider is placed in a random location on the board.
     */
    protected void addGlider()
    {

        int randX = (int)((NUM_ROWS_COLS -4) * Math.random()) + 2;
        int randY = (int)((NUM_ROWS_COLS -4) * Math.random()) + 2;

        board[randY][randX-1] = true;
        board[randY-1][randX] = true;
        board[randY-1][randX+1] = true;
        board[randY][randX+1] = true;
        board[randY+1][randX+1] = true;

    }//addGlider
    /**
     * nextGeneration
     *
     * this method creates a new board array to replace the old one.  The new
     * board is initialized based upon the old board using the rules for
     * Conway's game of life:
     *   - a cell with 1 or 0 neighbor dies (loneliness),
     *   - a cell with 4 or more neighbors dies (overcrowding),
     *   - a cell with 2 or 3 neighbors survives (stability),
     *   - an empty space with 3 neighbors sprouts a new cell (birth).
     *
     * cells in the dead zone are always left with value 'false'
     */
    protected void nextGeneration()
    {
        boolean[][] board1; 
        board1 = new boolean[board.length][board.length];

        for (int  x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                int count = 0;

                for (int k = -1; k <= 1; k++) {
                    for (int i = -1; i <= 1; i++) {
                        if(i == 0 && k == 0) {
                            //do nothing, that is the center dot
                        }
                        else if(y+k >= board.length || y+k <= 0) {
                            //do nothing, out of bounds
                        }
                        else if(x+i >= board.length || x+i <= 0) {
                            //do nothing, out of bounds
                        }
                        else if (board[y+k][x+i] == true){
                            count++;
                        }
                    }
                }

                if(count == 0 || count == 1) {
                    board1[y][x] = false;
                }
                else if((count == 2 || count == 3) && board[y][x] == true) {
                    board1[y][x] = true;
                }
                else if (count == 3 && board[y][x] == false) {
                    board1[y][x] = true;
                }
                else{
                    board1[y][x] = false;
                }

            }
        }

        board = board1;

    }//nextGeneration

    /*======================================================================
     *                    ATTENTION STUDENTS!
     *
     * The code below this point should not be edited.  However, you are
     * encouraged to examine the code to learn a little about how the rest
     * of the game was implemented.
     * ----------------------------------------------------------------------
     */

    /**
     * the contructor allocates the board and sets all the spaces therein to
     * zero.
     */
    public Life(Canvas initCanvas)
    {
        //create an empty board
        board = new boolean[NUM_ROWS_COLS][NUM_ROWS_COLS];
        clearBoard();

        //save a pointer to the graphics canvas
        myCanvas = initCanvas;

    }//Life ctor

    /**
     * dispatchKeyEvent
     *
     * when the user presses a key, this method determines the character
     * associated with that key and passes it to the handleKeyPress method
     */
    private char lastKey = '\0';
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        //Ignore KEY_RELEASED events (we only care when the key is pressed)
        String params = e.paramString();
        if (params.contains("KEY_RELEASED"))
        {
            return false;
        }

        //Record the keystroke for later use
        lastKey = e.getKeyChar();
        return true; 
    }//dispatchKeyEvent

    /*
     * These variables are used for double buffering and are initialized in
     * the main() method at the bottom of this file.
     */
    private Canvas myCanvas = null;
    private BufferStrategy strategy = null;
    private boolean paused = false;  //is the animation is paused or not?

    /**
     * this method contains the main run loop for the game
     *
     */
    public void run()
    {
        //Setup double-buffering
        myCanvas.createBufferStrategy(2);
        strategy = myCanvas.getBufferStrategy();

        //For the rest of the game just keep creating new generations
        while(true)
        {
            //Pause for a moment so the user can see the current generation
            try
            {
                Thread.sleep(DELAY);
            }
            catch(Exception e)
            {
                //don't care if the sleep fails
            }

            //Create the next generation
            if (!paused)
            {
                nextGeneration();
            }

            //Handle the most recent keypress (space = PAUSE)
            if (lastKey == 'p')  //pause
            {
                paused = !paused;
            }
            else if (lastKey == ' ') //single step generation
            {
                if (paused)
                {
                    nextGeneration();
                }
            }
            else if (lastKey != '\0') //let the student handle anything else
            {
                handleKeyPress(lastKey);
            }
            lastKey = '\0';  //reset for next keypress

            //Retrieve the canvas used for double buffering 
            Graphics hiddenCanvas = strategy.getDrawGraphics();

            //Start with a black pen on a white background
            hiddenCanvas.setColor(Color.white);
            hiddenCanvas.fillRect(0,0,WINDOW_SIZE,WINDOW_SIZE);
            hiddenCanvas.setColor(Color.black);

            //Let drawBoard draw the cells
            drawBoard(hiddenCanvas);

            //Display the new canvas to the user
            strategy.show();

        }//while

    }//run

    /**
     * This method creates a window frame and displays the Life
     * game inside of it.  
     */
    public static void main(String[] args)
    {
        //Create a properly sized window for this program
        final JFrame myFrame = new JFrame();
        myFrame.setSize(WINDOW_SIZE+10, WINDOW_SIZE+30);

        //Tell this Window to close when someone presses the close button
        myFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });

        //Display a canvas in the window
        Canvas myCanvas = new Canvas();
        myFrame.getContentPane().add(myCanvas);
        myFrame.setVisible(true);

        //Init the internal game board
        Life myLifeGame = new Life(myCanvas);
        myLifeGame.clearBoard();

        //Ask the computer to tell me about what keys the user presses on the keyboard.  
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(myLifeGame);

        //Run the game!
        myLifeGame.run();

    }//main

}//class Life
