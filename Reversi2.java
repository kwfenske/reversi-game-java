/*
  Java 1.1 AWT Applet - Reversi or Othello Game
  Written by: Keith Fenske, http://www.psc-consulting.ca/fenske/
  Thursday, 8 January 2004
  Java class name: Reversi2
  Copyright (c) 2004 by Keith Fenske.  Released under GNU Public License.

  This is a graphical Java 1.1 AWT (GUI) applet to play the board game of
  Reversi, also known as Othello.  The playing board is a rectangular grid.
  Your positions are shown as black circles.  Positions occupied by the
  computer are shown in white.  You move first.  A legal move is any empty
  position that traps one or more of the computer's positions in a straight
  line (horizontal, vertical, or diagonal) between the empty position and
  another of your positions.  The trapped positions are then "flipped"
  (reversed) and become your positions.  To help you, legal moves are
  highlighted when you hold the mouse over an empty position.  If you can't
  move, then you lose your turn and the computer moves next.  The game ends
  when nobody can move.  The player with the most occupied positions is the
  winner.  You may run this program as a stand-alone application, or as an
  applet on the following web page:

      Reversi or Othello Game - by: Keith Fenske
      http://www.psc-consulting.ca/fenske/revers2a.htm

  You can change the size of the game board.  The number of rows and columns
  may be changed independently.  The standard size is 8x8.  You may also select
  "easy" or "medium" difficulty.  On "easy" play, the computer moves randomly.
  On "medium" play, the computer flips the maximum number of positions.  A more
  difficult level has not been implemented.  This would require hundreds or
  thousands of lines of additional code to develop a better strategy.  The
  extra code would detract from this game's primary purpose, which is to
  demonstrate graphical Java programming.  Should you feel like doing the work,
  the program does contain hooks for two additional levels called "hard" and
  "expert".

  GNU General Public License (GPL)
  --------------------------------
  Reversi2 is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License or (at your option) any later
  version.  This program is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
  more details.

  You should have received a copy of the GNU General Public License along with
  this program.  If not, see the http://www.gnu.org/licenses/ web page.

  Restrictions and Limitations
  ----------------------------
  "Othello" is a registered trademark.  Since 1976 in the United States, the
  legal owner is Tsukuda Co. Ltd. of Japan, assigned to Anjar Co. in New York.
  Mattel Inc. once held a trademark for "Reversi" as a video game but that
  expired in 1989.  There is no current trademark in the United States for
  "Reversi" as a board game.

  -----------------------------------------------------------------------------

  Java Applet Notes:

  The recommended way of writing applets is to use Java Swing, according to Sun
  Microsystems, the creators and sponsors of Java.  Unfortunately, most web
  browsers don't support Swing unless you download a recent copy of the Java
  run-time environment from Sun.  This leaves a Java programmer with two
  choices:

  (1) Write applets using only old features found in the AWT interface.  The
      advantage, if you can see it this way, is that the programmer gets a
      detailed opportunity to interact with the graphical interface.  (Joke.)

  (2) Force users to visit http://java.sun.com/downloads/ to download and
      install a newer version of Java.  However, forcing anyone to download
      something before they can visit your web page is a poor idea.

  A worse idea is new browsers that don't have any Java support at all, unless
  the user first downloads Sun Java.  Microsoft stopped distributing their
  version of Java in 2003 starting with Windows XP SP1a (February), then
  Windows 2000 SP4 (June).  Until Microsoft and Sun resolve their various
  lawsuits -- or until Microsoft agrees to distribute an unaltered version of
  Sun Java -- there will be an increasing number of internet users that have
  *no* version of Java installed on their machines!

  The design considerations for this applet are as follows:

  (1) The applet should run on older browsers as-is, without needing any
      additional downloads and/or features.  The minimum target is JDK1.1 which
      is Microsoft Internet Explorer 5.0 (Windows 98) and Netscape 4.7/4.8 (JDK
      1.1.5 from 1997).

  (2) Unlike the previous Life3 and TicTacToe4 applets, this applet uses more
      than one class.  A second class, a subclass of Canvas, is used to better
      draw and accept mouse input on the game board.  To run this applet on a
      web page, Reversi2 should be loaded from a JAR (Java archive) file.

  (3) The default background in the Sun Java applet viewer is white, but most
      web browsers use light grey.  To get the background color that you want,
      you must setBackground() on components or fillRect() with the color of
      your choice.

  (4) A small main() method is included with a WindowAdapter subclass, so that
      this program can be run as an application.  The default window size and
      position won't please everyone.
*/

import java.applet.*;             // older Java applet support
import java.awt.*;                // older Java GUI support
import java.awt.event.*;          // older Java GUI event support

public class Reversi2
             extends Applet
             implements ActionListener, ItemListener, Runnable
{
  /* constants */

  static final String beginMessage = "Click the mouse on a position of your choice.  You are the black circles.";
  static final int canvasBorder = 10; // empty pixels around game board
  static final int DefCOLS = 10;  // default number of columns in game board
  static final int DefROWS = 6;   // default number of rows in game board
  static final String noMessage = " "; // message text when nothing to say
  static final String[] rowColumnCounters = {"2", "4", "6", "8", "10", "12",
    "16", "24"};
  static final String skipMessage = "You can't move, so please click the \"Skip Turn\" button.";

  static final Color BACKGROUND = new Color(255, 204, 204); // light pink
  static final Color ColorCOMPUTER = new Color(204, 255, 255); // light cyan
  static final Color ColorGRIDLINE = new Color(204, 153, 153); // darker pink
  static final Color ColorHOVER = new Color(102, 102, 255); // light blue
  static final Color ColorNONE = BACKGROUND;
  static final Color ColorUSER = new Color(51, 51, 51); // dark grey

  static final int LevelEASY = 1;     // computer moves randomly
  static final int LevelMEDIUM = 2;   // computer flips maximum positions
  static final int LevelHARD = 3;     // an actual strategy - not implemented
  static final int LevelEXPERT = 4;   // for tournament play - not implemented
  static final String LevelStringEASY = "Easy";
  static final String LevelStringMEDIUM = "Medium";
  static final String LevelStringHARD = "Hard";
  static final String LevelStringEXPERT = "Expert";

  static final int PlayCOMPUTER = 1;  // computer occupies this board position
  static final int PlayHOVER = 2;     // mouse hover on empty/valid user move
  static final int PlayINVALID = 3;   // illegal value for old game board
  static final int PlayNONE = 4;      // board position is empty
  static final int PlayUSER = 5;      // user occupies this board position

  /* class variables */

  /* instance variables, including shared GUI components */

  Canvas boardCanvas;             // where we draw the game board
  int[][] boardData;              // internal game board (PlayXXX)
  int[][] boardDataOld;           // previous game board data (PlayXXX)
  int[][] boardFlipsComp;         // count of valid flips for computer
  int[][] boardFlipsUser;         // count of valid flips for user
  int boardGridLine;              // width of board grid lines (in pixels), as
                                  // ... set by most recent boardUpdate()
  int boardGridStep;              // calculated size of each board position, as
                                  // ... set by most recent boardUpdate().
                                  // ... Includes inner borders and one set of
                                  // ... grid lines.
  int boardInnerBorder;           // pixels in each position's inner border, as
                                  // ... set by most recent boardUpdate()
  int boardLeftMargin;            // adjusted left margin to center game board,
                                  // ... as set by most recent boardUpdate()
  int boardSymbolSize;            // pixels for each position's symbol, as set
                                  // ... by most recent boardUpdate()
  int boardTopMargin;             // adjusted top margin to center game board,
                                  // ... as set by most recent boardUpdate()
  Thread clockThread;             // clock thread for delaying computer's move
  Choice columnCounter;           // column counter (number of columns)
  boolean eraseFlag;              // true if background should be erased on
                                  // ... next call to boardUpdate()
  int gameState;                  // state variable for current game
  int hoverCol;                   // column number of where mouse is over
  int hoverRow;                   // row number of where mouse is over
  Choice levelChoice;             // how user selects difficulty level
  int levelFlag;                  // how difficult the computer plays
  int maxFlipsComp;               // maximum number of flips for computer, as
                                  // ... set by scanBoard()
  int maxFlipsUser;               // maximum number of flips for user, as set
                                  // ... by scanBoard()
  Label messageText;              // information or status message for user
  int numCols = DefCOLS;          // number of columns in current game board
  int numRows = DefROWS;          // number of rows in current game board
  Choice rowCounter;              // row counter (number of rows)
  Button skipButton;              // "Skip Turn" button
  Button startButton;             // "New Game" button


/*
  init() method

  Initialize this applet (equivalent to the main() method in an application).
  Please note the following about writing applets:

  (1) An Applet is an AWT Component just like a Button, Frame, or Panel.  It
      has a width, a height, and you can draw on it (given a proper graphical
      context, as in the paint() method).

  (2) Applets shouldn't attempt to exit, such as by calling the System.exit()
      method, because this isn't allowed on a web page.
*/
  public void init()
  {
    /* Intialize our own data before creating the GUI interface. */

    boardGridLine = 1;            // in case mouse moves before board paints,
    boardGridStep = 1;            // ... these values will invalidate any
    boardInnerBorder = 999;       // ... conversion of mouse coordinates to
    boardLeftMargin = 0;          // ... board positions (row, column)
    boardSymbolSize = 1;
    boardTopMargin = 0;

    clearBoard();                 // clear (create) the game board

    /* Create the GUI interface as a series of little panels inside bigger
    panels.  The intermediate panel names (panel1, panel2, etc) are of no
    importance and hence are only numbered. */

    /* Make a horizontal panel to hold the difficulty level, row counter,
    and column counter. */

    levelChoice = new Choice();
    levelChoice.add(LevelStringEASY);
    levelChoice.add(LevelStringMEDIUM);
//  levelChoice.add(LevelStringHARD); // not implemented
//  levelChoice.add(LevelStringEXPERT); // not implemented
    levelChoice.select(LevelStringMEDIUM); // select must be same as flag
    levelFlag = LevelMEDIUM;      // flag must be same as choice select
    levelChoice.addItemListener((ItemListener) this);

    Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Label label1 = new Label("Rows: ", Label.RIGHT);
    label1.setBackground(BACKGROUND);
    panel1.add(label1);
    rowCounter = new Choice();
    for (int i = 0; i < rowColumnCounters.length; i ++)
      rowCounter.add(rowColumnCounters[i]);
    rowCounter.select(String.valueOf(DefROWS));
    rowCounter.addItemListener((ItemListener) this);
    panel1.add(rowCounter);

    Panel panel2 = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Label label2 = new Label("Columns: ", Label.RIGHT);
    label2.setBackground(BACKGROUND);
    panel2.add(label2);
    columnCounter = new Choice();
    for (int i = 0; i < rowColumnCounters.length; i ++)
      columnCounter.add(rowColumnCounters[i]);
    columnCounter.select(String.valueOf(DefCOLS));
    columnCounter.addItemListener((ItemListener) this);
    panel2.add(columnCounter);

    Panel panel3 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    panel3.add(levelChoice);
    panel3.add(panel1);
    panel3.add(panel2);

    /* Make a horizontal panel to hold two equally-spaced buttons. */

    Panel panel4 = new Panel(new GridLayout(1, 2, 20, 0));

    startButton = new Button("New Game");
    startButton.addActionListener((ActionListener) this);
    panel4.add(startButton);

    skipButton = new Button("Skip Turn");
    skipButton.addActionListener((ActionListener) this);
    panel4.add(skipButton);

    /* Put the counters and buttons together into one horizontal panel. */

    Panel panel5 = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 5));
    panel5.add(panel3);
    Label label3 = new Label(" "); // a cheap separator
    label3.setBackground(BACKGROUND);
    panel5.add(label3);
    panel5.add(panel4);
    panel5.setBackground(BACKGROUND); // for Netscape 4.7/4.8 (JDK1.1)

    /* Put the message field under the counters/buttons. */

    Panel panel6 = new Panel(new GridLayout(2, 1, 0, 5));
    panel6.add(panel5);
//  messageText = new Label(beginMessage, Label.CENTER);
    messageText = new Label("Reversi or Othello (Java applet).  Copyright (c) 2004 by Keith Fenske.  GNU Public License.", Label.CENTER);
    // JDK1.1 note: replace Font(null,...) with Font("Default",...)
    messageText.setFont(new Font("Default", Font.PLAIN, 14));
    messageText.setBackground(BACKGROUND);
    panel6.add(messageText);
    panel6.setBackground(BACKGROUND); // for Netscape 4.7/4.8 (JDK1.1)

    /* Put the buttons and message field on top of a canvas for the game board,
    giving the game board the remaining window space.  We set the applet to
    have a BorderLayout and put <boardCanvas> in the center, which allows the
    canvas to expand and contract with the applet's window size.  Note that the
    Reversi2Board class assumes that the Reversi2 applet is the parent
    container of <boardCanvas>. */

    this.setLayout(new BorderLayout(5, 5));
    this.add(panel6, BorderLayout.NORTH);
    boardCanvas = new Reversi2Board();
    boardCanvas.addMouseListener((MouseListener) boardCanvas);
    boardCanvas.addMouseMotionListener((MouseMotionListener) boardCanvas);
    this.add(boardCanvas, BorderLayout.CENTER);
    this.setBackground(BACKGROUND);
    this.validate();              // do the window layout

    /* Check if the game is already over (which can happen for the extreme case
    of only two rows or columns).  Then let the GUI interface run the game. */

    scanBoard();                  // check if game is already over

  } // end of init() method


/*
  main() method

  Applets only need an init() method to start execution.  This main() method is
  a wrapper that allows the same applet code to run as an application.
*/
  public static void main(String[] args)
  {
    Applet appletPanel;           // the target applet's window
    Frame mainFrame;              // this application's window

    mainFrame = new Frame("Reversi or Othello Game - by: Keith Fenske");
    mainFrame.addWindowListener(new Reversi2Window());
    mainFrame.setLayout(new BorderLayout(5, 5));
    mainFrame.setLocation(new Point(50, 50)); // top-left corner of app window
    mainFrame.setSize(700, 500);  // initial size of application window
    appletPanel = new Reversi2(); // create instance of target applet
    mainFrame.add(appletPanel, BorderLayout.CENTER); // give applet full frame
    mainFrame.validate();         // do the application window layout
    appletPanel.init();           // initialize applet
    mainFrame.setVisible(true);   // show the application window

  } // end of main() method

// ------------------------------------------------------------------------- //

/*
  actionPerformed() method

  This method is called when the user clicks on the "New Game" or "Skip Turn"
  buttons.
*/
  public void actionPerformed(ActionEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == skipButton)
    {
      /* The user clicked the "Skip Turn" button; the computer will move next
      instead. */

      switch (gameState)
      {
        case PlayCOMPUTER:
          messageText.setText("But it's the computer's turn to play!");
          break;

        case PlayNONE:
          messageText.setText("You must start a new game before you can skip your turn.");
          break;

        case PlayUSER:
//        messageText.setText("Skipping your turn: the computer will move next.");
          messageText.setText(noMessage);
          gameState = PlayCOMPUTER; // computer will move next, if possible
          moveComputer();         // check for winner, make computer's move
          boardCanvas.repaint();  // redraw the game board
          break;

        default:
          System.out.println("error in actionPerformed(): bad gameState = "
            + gameState);
      }
    }
    else if (source == startButton)
    {
      /* The user clicked the "New Game" button and wants to start over. */

      clearBoard();               // start a new game board
      messageText.setText(beginMessage);
      scanBoard();                // check if game is already over
      eraseFlag = true;           // boardUpdate() must redraw game board
      boardCanvas.repaint();      // redraw the game board
    }
    else
    {
      System.out.println(
        "error in actionPerformed(): ActionEvent not recognized: " + event);
    }
  } // end of actionPerformed() method


/*
  boardMouseClicked() method

  This method is called by our dummy Canvas class (Reversi2Board) to process
  mouse clicks on the game board, in the context of the main Reversi2 class.
  We must determine:

  (1) if the user is allowed to choose a board position (user's turn to move);
  (2) which position the mouse is pointing at;
  (3) if the position is empty (available); and
  (4) if choosing the position ends the game.

  Our calculations use several global variables set by the boardUpdate()
  method.
*/
  public void boardMouseClicked(MouseEvent event, Canvas canvas)
  {
    int col;                      // temporary column number (index)
    Point pos;                    // board position as (column, row)
    int row;                      // temporary row number (index)

    /* Convert mouse coordinates into row and column numbers. */

    pos = mouseToBoard(event.getPoint()); // convert (x, y) to (column, row)
    col = pos.x;                  // get column number
    row = pos.y;                  // get row number

    /* Now start checking if this mouse click is a legal move. */

    if (gameState == PlayNONE)
    {
      /* There is no active game, so mouse clicks aren't useful. */

      messageText.setText("This game is finished.  You must start a new game before you can move again.");
    }
    else if (gameState != PlayUSER)
    {
      /* It's the computer's turn to move, not the user's. */

      messageText.setText("Sorry, it's not your turn to move.  The computer is thinking.");
    }
    else if (maxFlipsUser == 0)
    {
      /* It's the user's turn, in a sense, but we're just waiting for him/her
      to see that they can't move and should click the "Skip Turn" button. */

      messageText.setText(skipMessage);
    }
    else if ((col < 0) || (row < 0)) // if click is outside game board
    {
      /* Ignore clicks that are not directly on a board position. */

      messageText.setText("Please click on a board position.");
    }
    else if ((boardData[row][col] != PlayNONE)
      && (boardData[row][col] != PlayHOVER))
    {
      /* The user clicked on a position that is already occupied. */

      messageText.setText("Sorry, that board position has already been chosen.");
    }
    else if (boardFlipsUser[row][col] == 0)
    {
      /* The user clicked on an empty position that is not a valid move. */

      messageText.setText("Choose an empty position that flips at least one computer position.");
    }
    else
    {
      if (boardFlipsUser[row][col] == 1)
        messageText.setText("Your move is row " + (row + 1) + " and column "
          + (col + 1) + " with one reversed position.");
      else
        messageText.setText("Your move is row " + (row + 1) + " and column "
          + (col + 1) + " with " + boardFlipsUser[row][col] + " reverses.");
      flipBoardPosition(PlayUSER, row, col, true); // change game board
      gameState = PlayCOMPUTER;   // computer will move next, if possible
      boardCanvas.repaint();      // redraw the game board
      clockThread = new Thread(this); // make computer's move after a delay
      clockThread.start();        // start the run() clock thread
    }

  } // end of boardMouseClicked() method


/*
  boardMouseMoved() method

  This method is called by our dummy Canvas class (Reversi2Board) to process
  mouse movement across the game board, in the context of the main Reversi2
  class.  If the user pauses over an empty position that is a valid move for
  the user, then we highlight that position in a different color.

  Our calculations use several global variables set by the boardUpdate()
  method.
*/
  public void boardMouseMoved(MouseEvent event, Canvas canvas)
  {
    int col;                      // temporary column number (index)
    Point pos;                    // board position as (column, row)
    boolean repaintFlag;          // true if board should be repainted
    int row;                      // temporary row number (index)

    /* Convert mouse coordinates into row and column numbers. */

    pos = mouseToBoard(event.getPoint()); // convert (x, y) to (column, row)
    col = pos.x;                  // get column number
    row = pos.y;                  // get row number

    /* Check if the hover position changes anything on the board display. */

    if ((row == hoverRow) && (col == hoverCol)) // same position as before?
    {
      /* No change as far as we are concerned.  Do nothing.  This method is
      called often, so we want to exit quickly if there is nothing to do. */
    }
    else
    {
      /* Mouse has moved to a new board position. */

      repaintFlag = false;        // assume board doesn't need to be updated

      /* Clear previous mouse hover, if any. */

      if ((hoverRow >= 0)         // was previous hover on a position?
        && (boardData[hoverRow][hoverCol] == PlayHOVER))
      {
        boardData[hoverRow][hoverCol] = PlayNONE; // return previous to empty
        repaintFlag = true;       // game board should be updated
      }

      /* Set new mouse hover, if this is a valid move for the user. */

      if ((gameState == PlayUSER) // is it the user's turn?
        && (row >= 0)             // is mouse over a real position?
        && (boardData[row][col] == PlayNONE) // is mouse over empty position?
        && (boardFlipsUser[row][col] > 0)) // is position valid move for user?
      {
        boardData[row][col] = PlayHOVER; // yes, mark for hover display
        repaintFlag = true;       // game board should be updated
      }

      /* Save current mouse position as the new hover position, so we can exit
      quickly from this method most of the time. */

      hoverCol = col;
      hoverRow = row;

      /* Redraw the game board, if necessary. */

      if (repaintFlag)            // should game board be updated?
        boardCanvas.repaint();    // yes, redraw the game board
    }
  } // end of boardMouseMoved() method


/*
  boardPaint() method

  This method is called by our dummy Canvas class (Reversi2Board) to redraw the
  game board, in the context of the main Reversi2 class.  There is a separation
  between the paint() and update() methods.  Paint() methods are called when a
  window is first created, resized, or needs to be redrawn.  Update() methods
  are called when the window exists and is valid, but the contents may have
  changed.

  Simple applets only have a paint() method, which erases the window and
  redraws all components each time.  However, this applet updates a dynamic
  game board.  If the background is erased each time, then the display will
  "flicker" because of a short period of time after the old game board
  disappears before the new game board is drawn.  Since the new game board goes
  in exactly the same place as the old board, erasing the background is not
  necessary.  To avoid flicker, this applet separates the paint() and update()
  methods.
 */
  void boardPaint(
    Graphics gr,                  // graphics context
    Canvas canvas)                // passed reference for <boardCanvas>
  {
    eraseFlag = true;             // boardUpdate() must redraw game board
    boardUpdate(gr, canvas);      // all work is done in boardUpdate() so that
                                  // ... the background is only erased and
                                  // ... redrawn when necessary
  } // end of boardPaint() method


/*
  boardUpdate() method

  This method is called by our dummy Canvas class (Reversi2Board) to update the
  game board, in the context of the main Reversi2 class.  There is a separation
  between the paint() and update() methods.  Paint() methods are called when a
  window is first created, resized, or needs to be redrawn.  Update() methods
  are called when the window exists and is valid, but the contents may have
  changed.

  Several global variables are set for later use by the mouse listener to
  determine where board positions are located.

  When an applet runs on a web page, the initial window size is chosen by the
  web page's HTML code and can't be changed by the applet.  Applets running
  outside of a web page (such as with Sun's applet viewer) can change their
  window size at any time.  The user may enlarge or reduce the window to make
  it fit better on his/her display.  Hence, while this applet doesn't attempt
  to change the window size, it must accept that the window size may be
  different each time the paint() method is called.  A good applet redraws its
  components to fit the window size.
*/
  void boardUpdate(
    Graphics gr,                  // graphics context
    Canvas canvas)                // passed reference for <boardCanvas>
  {
    int boardHeight;              // height (in pixels) of actual game board
    int boardWidth;               // width (in pixels) of actual game board
    int col;                      // temporary column number (index)
    int corner;                   // how much we round corners on rectangle
    int hz;                       // temporary number of horizontal pixels
    int row;                      // temporary row number (index)
    int vt;                       // temporary number of vertical pixels

    if (eraseFlag)                // only if requested
    {
      /* If the current message field is a complaint from us about the applet
      window being too small, then clear the message text.  Should the window
      problem persist, we will regenerate the error message anyway. */

      if (messageText.getText().startsWith("Applet window"))
        messageText.setText(noMessage);

      /* Clear the entire board canvas (including any defined borders) to our
      own background color. */

      gr.setClip(null);           // turn off clipping region, so that
                                  // ... everything we do here gets displayed
      gr.setColor(BACKGROUND);
      // JDK1.1 note: replace canvas.getWidth() with canvas.getSize().width
      // JDK1.1 note: replace canvas.getHeight() with canvas.getSize().height
      gr.fillRect(0, 0, canvas.getSize().width, canvas.getSize().height);

      /* Calculate the size of the game board (in pixels) using the size of
      <boardCanvas> minus a predefined border. */

      // JDK1.1 note: replace canvas.getWidth() with canvas.getSize().width
      boardWidth = canvas.getSize().width - (2 * canvasBorder);
      boardWidth = (boardWidth > 0) ? boardWidth : 0;
                                  // don't allow negative values
      // JDK1.1 note: replace canvas.getHeight() with canvas.getSize().height
      boardHeight = canvas.getSize().height - (2 * canvasBorder);
      boardHeight = (boardHeight > 0) ? boardHeight : 0;
                                  // don't allow negative values

      /* Estimate size of each board position, including inner borders and grid
      lines between positions.  Inner borders and grid lines are proportional,
      with a minimum size.  Board squares (positions) must be kept ... square.

      Note here and elsewhere that the number of grid lines is one more than
      the number of rows or columns, because the grid lines surround the entire
      game board. */

      hz = boardWidth / numCols;  // first estimate of pixels per column
      vt = boardHeight / numRows; // first estimate of pixels per row
      boardGridStep = (hz < vt) ? hz : vt; // minimum becomes first estimate

      boardGridLine = (int) (boardGridStep * 0.02);
                                  // width of grid lines in pixels
      boardGridLine = (boardGridLine > 1) ? boardGridLine : 1;
                                  // minimum of one pixel

      hz = (boardWidth - boardGridLine) / numCols; // second column estimate
      vt = (boardHeight - boardGridLine) / numRows; // second row estimate
      boardGridStep = (hz < vt) ? hz : vt; // second estimate per position

      boardInnerBorder = (int) (boardGridStep * 0.07);
                                  // pixels for inner borders
      boardInnerBorder = (boardInnerBorder > 2) ? boardInnerBorder : 2;
                                  // minimum of two pixels
      boardSymbolSize = boardGridStep - boardGridLine - (2 * boardInnerBorder);
                                  // pixels for each position's player symbol
      boardSymbolSize = (boardSymbolSize > 10) ? boardSymbolSize : 10;
                                  // minimum of ten pixels
      boardGridStep = boardSymbolSize + (2 * boardInnerBorder) + boardGridLine;
                                  // final step size will be positive

      /* Compute a new left margin and top margin so that our game board will
      be centered on the canvas. */

      hz = (boardWidth - boardGridLine - (numCols * boardGridStep)) / 2;
      if (hz < 0)
      {
        messageText.setText("Applet window is too narrow to display " + numCols
          + " columns.");
        hz = 0;                   // reset and continue
      }
      boardLeftMargin = canvasBorder + hz; // plus defined left border

      vt = (boardHeight - boardGridLine - (numRows * boardGridStep)) / 2;
      if (vt < 0)
      {
        messageText.setText("Applet window is too short to display " + numRows
          + " rows.");
        vt = 0;                   // reset and continue
      }
      boardTopMargin = canvasBorder + vt; // plus defined top border

      /* Draw vertical grid lines between columns. */

      gr.setColor(ColorGRIDLINE);
      hz = boardLeftMargin;       // x coordinate of first grid line
      vt = (numRows * boardGridStep) + boardGridLine; // height in pixels (constant)
      for (col = 0; col <= numCols; col ++)
      {
        gr.fillRect(hz, boardTopMargin, boardGridLine, vt);
        hz += boardGridStep;      // x coordinate for next column
      }

      /* Draw horizontal grid lines between rows. */

      vt = boardTopMargin;        // y coordinate of first grid line
      hz = (numCols * boardGridStep) + boardGridLine; // width in pixels (constant)
      for (row = 0; row <= numRows; row ++)
      {
        gr.fillRect(boardLeftMargin, vt, hz, boardGridLine);
        vt += boardGridStep;      // y coordinate for next row
      }

    } // end if eraseFlag

    /* Draw the board.  Don't draw positions that haven't changed. */

    corner = (int) (boardGridStep * 0.65); // rounded corners on rectangle
    vt = boardTopMargin + boardGridLine + boardInnerBorder;
                                  // y coordinate of first position symbol in
                                  // ... first column in first row
    for (row = 0; row < numRows; row ++)
    {
      hz = boardLeftMargin + boardGridLine + boardInnerBorder;
                                  // x coordinate of first position symbol in
                                  // ... first column in this row
      for (col = 0; col < numCols; col ++)
      {
        /* Display this game board position if (1) the background has been
        erased, or (2) if the position differs from last time we drew the
        board. */

        if (eraseFlag || (boardData[row][col] != boardDataOld[row][col]))
        {
          switch (boardData[row][col])
          {
            case PlayCOMPUTER:    // computer's position
              gr.setColor(ColorCOMPUTER);
              break;

            case PlayHOVER:       // mouse over valid user move
              gr.setColor(ColorHOVER);
              break;

            case PlayNONE:        // empty position
              gr.setColor(ColorNONE);
              break;

            case PlayUSER:        // user's position
              gr.setColor(ColorUSER);
              break;

            default:
              System.out.println("error in boardUpdate(): bad boardData["
                + row + "][" + col + "] = " + boardData[row][col]);
          }
//        gr.fillOval(hz, vt, boardSymbolSize, boardSymbolSize); // draw circle
          gr.fillRoundRect(hz, vt, boardSymbolSize, boardSymbolSize, corner,
            corner);              // draw positions as rounded rectangles
        }
        boardDataOld[row][col] = boardData[row][col]; // save for next time
        hz += boardGridStep;      // x coordinate for next column
      }
      vt += boardGridStep;        // y coordinate for next row
    }

    /* The game board has now been redrawn.  Clear the erase flag, if any. */

    eraseFlag = false;            // erased board has now been redrawn

  } // end of boardUpdate() method


/*
  clearBoard() method

  Create a new game board, or clear the existing game board to all empty
  positions.  This method should not do any GUI calls such as repaint()
  because:

  (1) clearBoard() is called by init() before the GUI interface is established;
      and
  (2) There are several methods that make changes to GUI objects after calling
      clearBoard() and before they are ready to repaint.  If clearBoard()
      forced a repaint, then too many unnecessary paint operations would be
      performed.
*/
  void clearBoard()
  {
    int col;                      // temporary column number (index)
    int row;                      // temporary row number (index)

    /* Allocate new arrays if there are no previous arrays, or if the sizes
    have changed. */

    if ((boardData == null)                 // if no previous game board
      || (boardData.length != numRows)      // if new number of rows
      || (boardData[0].length != numCols))  // if new number of columns
    {
      boardData = new int[numRows][numCols]; // array for board positions
      boardDataOld = new int[numRows][numCols]; // array for previous board
      boardFlipsComp = new int[numRows][numCols]; // array for computer flips
      boardFlipsUser = new int[numRows][numCols]; // array for user flips
    }

    /* Initialize the arrays to default values. */

    for (row = 0; row < numRows; row ++)
      for (col = 0; col < numCols; col ++)
      {
        boardData[row][col] = PlayNONE; // make all positions empty
        boardDataOld[row][col] = PlayINVALID; // invalidate previous board
        boardFlipsComp[row][col] = 0; // clear flip counters for computer
        boardFlipsUser[row][col] = 0; // clear flip counters for user
      }

    /* Set the initial game state. */

    gameState = PlayUSER;         // user's turn to move
    hoverCol = hoverRow = -1;     // mouse isn't over any board position

    row = (numRows / 2) - 1;      // top row of starting 2x2 positions
    col = (numCols / 2) - 1;      // left column of starting 2x2 positions
    boardData[row    ][col    ] = PlayCOMPUTER; // starting 2x2 positions
    boardData[row    ][col + 1] = PlayUSER;
    boardData[row + 1][col    ] = PlayUSER;
    boardData[row + 1][col + 1] = PlayCOMPUTER;

    /* It is possible that an initial game board is already finished; however,
    we can't call scanBoard() here, since clearBoard() is a non-GUI method. */

  } // end of clearBoard() method


/*
  flipBoardPath() method

  For a given player, board position, and direction, either count how many of
  the other player's positions can be flipped by moving to that position, or go
  ahead and move to that position.  Either way, we return the number of flips.
  If this result is zero, then the given player can't move to the given
  position.

  The direction is given by row and column increments.  The <flipFlag> should
  only be true if the caller already knows that the given board position and
  direction is a valid move (that is, has already scanned this path by a
  previous call with <flipFlag> set to false).
*/
  int flipBoardPath(
    int player,                   // must be <PlayCOMPUTER> or <PlayUSER>
    int startRow,                 // starting row number (index)
    int startCol,                 // starting column number (index)
    int rowIncr,                  // row increment (-1, 0, or +1)
    int colIncr,                  // column increment (-1, 0, or +1)
    boolean flipFlag)             // true if we change game board, false if we
                                  // ... are only looking
  {
    int col;                      // temporary column number (index)
    int flips;                    // number of flips in this direction (path)
    boolean matchFound;           // true if we find second <player> position
    int other;                    // PlayXXX for other player (not <player>)
    int row;                      // temporary row number (index)

    /* Figure out who the other player is. */

    other = (player == PlayCOMPUTER) ? PlayUSER : PlayCOMPUTER;

    /* We assume that the starting position is empty.  We don't check this. */

    row = startRow + rowIncr;     // row for next position
    col = startCol + colIncr;     // column for next position

    /* Count the number of positions belonging to the other player.  Stop when
    we find the matching position for this player, or hit the edge of the game
    board. */

    flips = 0;                    // start with no flips
    matchFound = false;           // assume we can't find matching <player>
    while ((row >= 0) && (row < numRows) && (col >= 0) && (col < numCols))
    {
      if (boardData[row][col] == other)
      {
        flips ++;                 // other player's position can be flipped
        if (flipFlag)             // do we actually do the flipping?
          boardData[row][col] = player; // yes, flip the board position
      }
      else if (boardData[row][col] == player)
      {
        matchFound = true;      // mark that we found second <player> position
        break;                  // exit from the while loop
      }
      else
      {
        flips = 0;              // hit an empty position: move is not valid
        break;                  // exit from the while loop
      }
      row += rowIncr;           // row index of next position
      col += colIncr;           // column index of next position
    }
    if (matchFound == false)    // if we didn't find second <player> position
      flips = 0;                // then clear number of flips found

    return flips;               // return zero, or number of flips found

  } // end of flipBoardPath() method


/*
  flipBoardPlayer() method

  For a given player, and for each empty position on the game board, count how
  many of the other player's positions can be "flipped" by moving to that empty
  position.  We return the maximum number of flips for a single position.  If
  this result is zero, then the given player has no legal moves.
*/
  int flipBoardPlayer(
    int player,                   // must be <PlayCOMPUTER> or <PlayUSER>
    int[][] flipArray)            // <boardFlipsComp> or <boardFlipsUser>
  {
    int col;                      // temporary column number (index)
    int flips;                    // number of flips for this position
    int maxFlips;                 // maximum flip value found
    int row;                      // temporary row number (index)

    maxFlips = 0;                 // assume that no moves are possible
    for (row = 0; row < numRows; row ++)
      for (col = 0; col < numCols; col ++)
      {
        flips = flipBoardPosition(player, row, col, false);
        flipArray[row][col] = flips;
        maxFlips = (flips > maxFlips) ? flips : maxFlips; // keep maximum
      }
    return maxFlips;              // return maximum value found

  } // end of flipBoardPlayer() method


/*
  flipBoardPosition() method

  For a given player and board position, either count how many of the other
  player's positions can be flipped by moving to that position, or go ahead and
  move to that position.  Either way, we return the number of flips.  If this
  result is zero, then the given player can't move to the given position.
*/
  int flipBoardPosition(
    int player,                   // must be <PlayCOMPUTER> or <PlayUSER>
    int row,                      // row number (index)
    int col,                      // column number (index)
    boolean flipFlag)             // true if we change game board, false if we
                                  // ... are only looking
  {
    int flips;                    // number of flips in one direction (path)
    int[][] offsets = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1},
      {1, 0}, {1, 1}};            // list of row, column increments
    int totalFlips;               // total number of flips found

    totalFlips = 0;               // assume that no moves are possible
    switch (boardData[row][col])  // what type of board position is this?
    {
      case PlayCOMPUTER:          // do nothing for occupied positions
      case PlayUSER:
        break;

      case PlayHOVER:             // if position is empty
      case PlayNONE:
        for (int i = 0; i < offsets.length; i ++)
        {
          flips = flipBoardPath(player, row, col,
            offsets[i][0], offsets[i][1], false); // first, only count flips
          totalFlips += flips;    // add direction's flips to total flips
          if (flipFlag && (flips > 0)) // only if this is a valid move
            flips = flipBoardPath(player, row, col,
              offsets[i][0], offsets[i][1], true); // second, make actual move
        }
        if (flipFlag)             // are we making a real move?
          boardData[row][col] = player; // yes, change starting board position
        break;

      default:
        System.out.println("error in flipBoardPosition(): bad boardData["
          + row + "][" + col + "] = " + boardData[row][col]);
        break;
    }
    return totalFlips;            // return number of flips found

  } // end of flipBoardPosition() method


/*
  itemStateChanged() method

  This method is called when the user changes the difficulty level, the number
  of rows, or the number of columns.  We assume that any value returned from
  the GUI is in the proper range.
*/
  public void itemStateChanged(ItemEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == columnCounter)  // new number of columns?
    {
      numCols = Integer.parseInt(columnCounter.getSelectedItem());
      clearBoard();               // start a new game board
      messageText.setText("New game board is " + numRows + " rows by "
        + numCols + " columns.");
      scanBoard();                // check if game is already over
      eraseFlag = true;           // boardUpdate() must redraw game board
      boardCanvas.repaint();      // redraw the game board
    }
    else if (source == levelChoice) // new level of difficulty
    {
      String level = levelChoice.getSelectedItem();
      if (level.equals(LevelStringEASY))
        levelFlag = LevelEASY;
      else if (level.equals(LevelStringMEDIUM))
        levelFlag = LevelMEDIUM;
      else if (level.equals(LevelStringHARD))
        levelFlag = LevelHARD;
      else if (level.equals(LevelStringEXPERT))
        levelFlag = LevelEXPERT;
      else
      {
        System.out.println(
          "error in itemStateChanged(): bad difficulty level \"" + level
          + "\"");
        levelFlag = LevelEASY;    // reset to easy level of difficulty
      }
    }
    else if (source == rowCounter) // new number of rows?
    {
      numRows = Integer.parseInt(rowCounter.getSelectedItem());
      clearBoard();               // start a new game board
      messageText.setText("New game board is " + numRows + " rows by "
        + numCols + " columns.");
      scanBoard();                // check if game is already over
      eraseFlag = true;           // boardUpdate() must redraw game board
      boardCanvas.repaint();      // redraw the game board
    }
    else
    {
      System.out.println(
        "error in itemStateChanged(): ItemEvent not recognized: " + event);
    }
  } // end of itemStateChanged() method


/*
  mouseToBoard() method

  Convert a mouse click with coordinates (x, y) on the board canvas into column
  and row indices.  If the click isn't directly on a board position, -1 is
  returned for both the column and the row.  Note that the Point value returned
  by this method keeps the convention that x is the column and y is the row.

  Our calculations use several global variables set by the boardUpdate()
  method.
*/
  Point mouseToBoard(Point mouse)
  {
    int col;                      // calculate column number
    int colExtra;                 // remainder from column calculation
    int pixels;                   // temporary number of pixels
    int row;                      // calculate row number
    int rowExtra;                 // remainder from row calculation

    /* Convert the (x, y) coordinates into row and column numbers, with a
    little extra information to tell us if the user was clicking on a board
    position, or if the clicks are on an inner or outer border. */

    pixels = mouse.x - boardLeftMargin - boardGridLine;
    col = pixels / boardGridStep;
    colExtra = pixels % boardGridStep;

    pixels = mouse.y - boardTopMargin - boardGridLine;
    row = pixels / boardGridStep;
    rowExtra = pixels % boardGridStep;

    if ((col < 0)                 // if click is outside game board
      || (col >= numCols)
      || (colExtra < boardInnerBorder) // or on inner border
      || (colExtra > (boardInnerBorder + boardSymbolSize))
      || (row < 0)                // if click is outside game board
      || (row >= numRows)
      || (rowExtra < boardInnerBorder) // or on inner border
      || (rowExtra > (boardInnerBorder + boardSymbolSize)))
    {
      col = row = -1;             // mouse is not on a board position
    }
    return new Point(col, row);   // return as (x, y) coordinates

  } // end of mouseToBoard() method


/*
  moveComputer() method

  This method is called after the user makes a move (that is, chooses a board
  position), or passes (that is, skips his/her turn).  We must do more than
  just make the computer's move.  We must:

  (1) do nothing if there is no active game (a sensible precaution);
  (2) check if the game is finished;
  (3) choose a move for the computer; and
  (4) check again if the game is finished.

  Note that this method doesn't need to know anything about the GUI interface
  (except maybe setting the message text), and doesn't need to do any painting.
  Both places where moveComputer() is called will do a repaint later anyway.
*/
  void moveComputer()
  {
    int col;                      // temporary column number (index)
    Point pos;                    // board position as (column, row)
    int row;                      // temporary row number (index)

    /* Check if we were called correctly.  If we were called out of turn, then
    issue a warning message but continue playing. */

    if (gameState != PlayCOMPUTER) // is it our turn?
    {
      System.out.println("error in moveComputer(): bad gameState = "
        + gameState);
    }

    /* Check if the game is finished.  This will also find the maximum number
    of "flips" for the computer and for the user. */

    scanBoard();                  // check if game is already over
    if (gameState == PlayNONE)    // if scanBoard() says game is over
      return;                     // ... then moveComputer() is done

    /* Make our move, if scanBoard() says that the computer can move.  If we
    can't move, then scanBoard() has already set a message string explaining
    why. */

    if ((gameState == PlayCOMPUTER) && (maxFlipsComp > 0)) // can we move?
    {
      /* Call different subroutines depending upon the increasing level of
      difficulty.  Unimplemented levels default to "medium". */

      switch (levelFlag)
      {
        case LevelEASY:           // the least difficult level
          pos = moveEasyMedium(boardFlipsComp, 1); // any legal move
          break;

        case LevelMEDIUM:         // more than "easy", less than "hard"
          pos = moveEasyMedium(boardFlipsComp, maxFlipsComp); // maximize flips
          break;

        case LevelHARD:           // more than "medium", less than "expert"
          pos = moveHard();       // not implemented - defaults to "medium"
          break;

        case LevelEXPERT:         // the most difficult level
          pos = moveExpert();     // not implemented - defaults to "medium"
          break;

        default:
          System.out.println("error in moveComputer(): bad levelFlag = "
            + levelFlag);
          pos = new Point(-1, -1); // invalidate computer's move
      }

      /* The selected empty position becomes the computer's move. */

      col = pos.x;                // get column number
      row = pos.y;                // get row number
      if (row >= 0)               // avoid error situation from above
      {
        if (boardFlipsComp[row][col] == 1)
          messageText.setText("Computer's move is row " + (row + 1)
            + " and column " + (col + 1) + " with one reversed position.");
        else
          messageText.setText("Computer's move is row " + (row + 1)
            + " and column " + (col + 1) + " with "
            + boardFlipsComp[row][col] + " reverses.");

        /* Flash the board position so that the user can see it better. */

        boardData[row][col] = PlayHOVER;
        boardCanvas.repaint();    // redraw the game board
        sleep(600);               // 600 milliseconds (0.6 seconds)
        boardData[row][col] = PlayNONE;
        boardCanvas.repaint();    // redraw the game board
        sleep(400);               // 400 milliseconds (0.4 seconds)
        boardData[row][col] = PlayHOVER;
        boardCanvas.repaint();    // redraw the game board
        sleep(600);               // 600 milliseconds (0.6 seconds)
        boardData[row][col] = PlayNONE;
        sleep(200);               // 200 milliseconds (0.2 seconds)

        /* Change the game board and flip adjoining positions. */

        flipBoardPosition(PlayCOMPUTER, row, col, true);
      }

      /* Get everything ready for the user to make a move.  There is no need to
      check the <gameState> after calling scanBoard() a second time, since we
      immediately return from moveComputer() anyway. */

      gameState = PlayUSER;         // user will move next, if possible
      scanBoard();                  // check if game is already over
    }
    else
    {
      /* The computer can't move, so turn the game over to the user.  Don't run
      scanBoard() a second time.  We know that the user has at least one valid
      move; otherwise, the first call to scanBoard() above would have ended the
      game. */

      gameState = PlayUSER;         // user has a valid move (computer doesn't)
    }
  } // end of moveComputer() method


/*
  moveEasyMedium() method

  This method returns the computer's move for both the "easy" and "medium"
  levels of difficulty.  At "easy" level, we randomly choose any valid move
  that will flip at least one of the user's positions.  At "medium" level, we
  consider only those positions with the maximum number of flips as counted by
  scanBoard().  The number of flips to consider is an argument, as is the array
  of flip counters, so this method could even be called for the user's moves!

  This method must return a valid move.  Note that the Point value returned
  keeps the convention that x is the column and y is the row.
*/
  Point moveEasyMedium(
    int[][] flipArray,            // <boardFlipsComp> or <boardFlipsUser>
    int minFlips)                 // minimum number of flips to consider
  {
    int col;                      // temporary column number (index)
    int current;                  // current occurence found
    int looking;                  // random number from 1 to <maxOccurs>
    int maxOccurs;                // how many times <maxFlipsComp> occurs
    int moveCol;                  // column of computer's next move
    int moveRow;                  // row of computer's next move
    int row;                      // temporary row number (index)

    maxOccurs = 0;                // no occurrences of maximum found yet
    moveCol = moveRow = -1;       // bad value just to make compiler happy

    for (row = 0; row < numRows; row ++)
      for (col = 0; col < numCols; col ++)
        if (flipArray[row][col] >= minFlips)
        {
          maxOccurs ++;           // count the number of occurences
          moveRow = row;          // remember last position with this maximum
          moveCol = col;
        }

    /* If more than one position has the same maximum value, then make a random
    selection because this is more interesting for the user. */

    if (maxOccurs > 1)            // make a selection if more than one choice
    {
      current = 0;                // current occurence found
      looking = 1 + (int) (Math.random() * maxOccurs);
                                  // random number from 1 to <maxOccurs>
      for (row = 0; row < numRows; row ++)
      {
        for (col = 0; col < numCols; col ++)
        {
          if (flipArray[row][col] >= minFlips)
          {
            current ++;           // found another occurence
            if (current == looking) // is this the one we're looking for?
            {
              moveRow = row;      // yes, save row number
              moveCol = col;      // save column number
              break;              // break out of for column loop
            }
          }
        }
        if (current == looking)   // did we find what we were looking for?
          break;                  // yes, break out of for row loop
      }
      if (current != looking)
        System.out.println("error in moveEasyMedium(): current " + current
          + " is not equal to looking " + looking);
    }
    return new Point(moveCol, moveRow); // return as (x, y) coordinates

  } // end of moveEasyMedium() method


/*
  moveExpert() method

  This method returns the computer's move for the "expert" level of difficulty
  (more difficult than "hard").  It is currently unimplemented and defaults to
  the same as the "medium" level.

  This method must return a valid move.  Note that the Point value returned
  keeps the convention that x is the column and y is the row.

  Any method to play at the "hard" or "expert" levels will be large and should
  be called as a static method in another Java class in a separate file.  This
  will distinguish the main graphical interface from the added non-graphical
  move strategy.  The called method should accept arguments such as a copy of
  <boardData> and <PlayCOMPUTER>, and should return a Point value with the
  chosen move.
*/
  Point moveExpert()
  {
    System.out.println("moveExpert() called, but not implemented");
    return moveEasyMedium(boardFlipsComp, maxFlipsComp); // default to "medium"

  } // end of moveExpert() method


/*
  moveHard() method

  This method returns the computer's move for the "hard" level of difficulty
  (more difficult than "medium" and easier than "expert").  It is currently
  unimplemented and defaults to the same as the "medium" level.

  This method must return a valid move.  Note that the Point value returned
  keeps the convention that x is the column and y is the row.

  Any method to play at the "hard" or "expert" levels will be large and should
  be called as a static method in another Java class in a separate file.  This
  will distinguish the main graphical interface from the added non-graphical
  move strategy.  The called method should accept arguments such as a copy of
  <boardData> and <PlayCOMPUTER>, and should return a Point value with the
  chosen move.
*/
  Point moveHard()
  {
    System.out.println("moveHard() called, but not implemented");
    return moveEasyMedium(boardFlipsComp, maxFlipsComp); // default to "medium"

  } // end of moveHard() method


/*
  run() method

  This method is a separate thread that executes in the same context as the
  applet.  It delays for a small amount of time and then makes the computer's
  move.  This gives the user the chance to see his/her move displayed before
  the computer may flip the position again.

  Note:  Please don't confuse the thread run() method with the applet start()
  and stop() methods.
*/
  public void run()
  {
    int delay;                    // delay in milliseconds

    delay = 1000 + (int) (Math.random() * 1500); // between 1.0 and 2.5 seconds
    try { Thread.sleep(delay); }  // sleep (delay)
        catch (InterruptedException e) { /* do nothing */ }

    moveComputer();               // check for winner, make computer's move
    boardCanvas.repaint();        // redraw the game board

  } // end of run() method


/*
  paint() method

  This applet doesn't have paint() or update() methods because all drawing is
  done by components (Button, Canvas, Panel, etc).  The game board is a
  subclass of Canvas that passes its paint() and update() calls to boardPaint()
  and boardUpdate() in the main Reversi2 class.
*/


/*
  scanBoard() method

  Mark valid moves in the <boardFlipsComp> and <boardFlipsUser> arrays.  Save
  the maximum flip values in <maxFlipsComp> and <maxFlipsUser>.  If no valid
  moves are found, then the game is over.
*/
  void scanBoard()
  {
    int col;                      // temporary column number (index)
    int row;                      // temporary row number (index)
    int totalComp;                // total positions occupied by computer
    int totalUser;                // total positions occupied by user

    /* Check if we were called correctly. */

    if ((gameState != PlayCOMPUTER) && (gameState != PlayUSER))
    {
      System.out.println("error in scanBoard(): bad gameState = " + gameState);
      return;                     // bad state ends scanBoard()
    }

    /* Count the number of flips for the computer, at each board position. */

    maxFlipsComp = flipBoardPlayer(PlayCOMPUTER, boardFlipsComp);

    /* Count the number of flips for the user, at each board position. */

    maxFlipsUser = flipBoardPlayer(PlayUSER, boardFlipsUser);

    /* Can anyone move?  Or is the game over? */

    if ((maxFlipsComp == 0) && (maxFlipsUser == 0))
    {
      /* Nobody can move.  The game is over.  Count board positions to find the
      winner. */

      totalComp = totalUser = 0;  // number of positions occupied by players
      for (row = 0; row < numRows; row ++)
        for (col = 0; col < numCols; col ++)
        {
          switch (boardData[row][col])
          {
            case PlayCOMPUTER:    // count computer's positions
              totalComp ++;
              break;

            case PlayUSER:        // count user's positions
              totalUser ++;
              break;
          }
        }

      /* Tell the user the final result. */

      if (totalComp > totalUser)
      {
        messageText.setText("Nobody can move.  Computer wins, " + totalComp
          + " positions to your " + totalUser + " positions.");
      }
      else if (totalComp < totalUser)
      {
        messageText.setText("Nobody can move.  You win with " + totalUser
          + " positions to the computer's " + totalComp + " positions.");
      }
      else // (totalComp == totalUser)
      {
        messageText.setText("Nobody can move.  Game is tied.  We both have "
          + totalComp + " positions.");
      }
      gameState = PlayNONE;       // game is over
    }

    else if ((gameState == PlayCOMPUTER) && (maxFlipsComp == 0))
    {
      /* It's the computer's turn and the computer can't move. */

      messageText.setText("The computer can't move, so it's your turn again.");
      gameState = PlayUSER;       // computer passes
    }
    else if ((gameState == PlayUSER) && (maxFlipsUser == 0))
    {
      /* It's the user's turn and the user can't move. */

      messageText.setText(skipMessage);
      gameState = PlayUSER;       // wait for user until "Skip Turn" button
    }
    else
    {
      /* Nothing special happened, so continue playing the game. */
    }

  } // end of scanBoard() method


/*
  sleep() method

  Sleep (delay) for the given number of milliseconds.  This method should only
  be called from a run() thread, and must not be called from the regular GUI
  thread; otherwise, the GUI thread will be blocked until this method returns.

  The delay must be at least 5 ms for Netscape 4.7/4.8 (JDK1.1).  Anything less
  is treated as no delay.
*/
  void sleep(int delay)
  {
    try { Thread.sleep(delay); }  // sleep (delay)
        catch (InterruptedException e) { /* do nothing */ }

  } // end of sleep() method


/*
  update() method

  This applet doesn't have paint() or update() methods because all drawing is
  done by components (Button, Canvas, Panel, etc).  The game board is a
  subclass of Canvas that passes its paint() and update() calls to boardPaint()
  and boardUpdate() in the main Reversi2 class.
*/

} // end of Reversi2 class

// ------------------------------------------------------------------------- //

/*
  Reversi2Board class

  Create a subclass of Canvas for the game board, so that we can take over from
  the regular mouse and paint routines.  These Canvas methods pass back their
  arguments to methods in the main Reversi2 class so that they can be processed
  in the context of the main Reversi2 class.

  Note that the Reversi2Board subclass assumes that the Reversi2 applet is the
  parent container of <boardCanvas>.
*/

class Reversi2Board
      extends Canvas
      implements MouseListener, MouseMotionListener
{
  public void mouseClicked(MouseEvent event)
  {
    ((Reversi2) this.getParent()).boardMouseClicked(event, this);
                                  // pass back (1) mouse event and (2) object
                                  // reference for <boardCanvas>
  }

  public void mouseDragged(MouseEvent event) { }
  public void mouseEntered(MouseEvent event) { }
  public void mouseExited(MouseEvent event) { }

  public void mouseMoved(MouseEvent event)
  {
    ((Reversi2) this.getParent()).boardMouseMoved(event, this);
                                  // pass back (1) mouse event and (2) object
                                  // reference for <boardCanvas>
  }

  public void mousePressed(MouseEvent event) { }
  public void mouseReleased(MouseEvent event) { }

  public void paint(Graphics gr)
  {
    ((Reversi2) this.getParent()).boardPaint(gr, this);
                                  // pass back (1) graphics context and (2)
                                  // object reference for <boardCanvas>
  }

  public void update(Graphics gr)
  {
    ((Reversi2) this.getParent()).boardUpdate(gr, this);
                                  // pass back (1) graphics context and (2)
                                  // object reference for <boardCanvas>
  }

} // end of Reversi2Board class

// ------------------------------------------------------------------------- //

/*
  Reversi2Window class

  This applet can also be run as an application by calling the main() method
  instead of the init() method.  As an application, it must exit when its main
  window is closed.  A window listener is necessary because EXIT_ON_CLOSE is a
  JFrame option in Java Swing, not a basic AWT Frame option.  It is easier to
  extend WindowAdapter here with one method than to implement all methods of
  WindowListener in the main applet.
*/

class Reversi2Window extends WindowAdapter
{
  public void windowClosing(WindowEvent event)
  {
    System.exit(0);               // exit from this application
  }
} // end of Reversi2Window class

/* Copyright (c) 2004 by Keith Fenske.  Released under GNU Public License. */
