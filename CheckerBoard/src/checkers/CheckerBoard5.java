package checkers;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Project : Create a Game of Checkers
 * Program:  CheckerBoard2.java
 * Programmer:  Thuy Nguyen
 * Date:  April 6, 2014
 * Description:  I made this program for fun where two people can play a game of checkers.  
 * 					   The Keycodes are : 1 = Move left down, 2 = Move right down, 9 = Move left up, 0 = Move right up
 * 						D - change the board's color, W - show the winner, B - show the board
 * 					   The game checks for valid moves and only allows users to move according to the rules : regular pieces
 * 					   on the north side can only move down diagonally.  Regular pieces on the south side can only move 
 * 					   up diagonally.  The pieces  automatically become King when they reach their opponent's side of the board.
 * 					   And Kings can move north or south.
 * 
 * 						This version of the checker game: CheckerBoard5, has an automatic kingMe and allows the user to 
 * 						give the checkers board a dimension at least 8x8.  Instead of using String to identify the color and players
 * 						I made private enums CheckerColor and Player to make the variable names clearer and tailored to this Checkers 
 * 						game.  This version also has a class that implements KeyEventDispatcher so that the class itself
 * 						can listen to keyboard events without having every button being registered with a KeyListener.
 * 					    When the Green or Blue King has won, a label with firework images announces the winner.  
 * 						The label shows due to CardLayout of the mainPanel that has added the firework-image labels.
 */
public class CheckerBoard5 extends JPanel
{
	private Checkers[][] board;
	private Color redColor = Color.red;
	private Color blackColor = Color.black;
	private Color selectedColor = Color.yellow;
	private Icon greenChecker = new ImageIcon(getClass().getResource("Green.png"));
	private Icon blueChecker = new ImageIcon(getClass().getResource("Blue.png"));
	private Icon greenKing = new ImageIcon(getClass().getResource("GreenKing3.png"));
	private Icon blueKing = new ImageIcon(getClass().getResource("BlueKing2.png"));
	private Random generator = new Random();
	private String[] kingMeCongratulations = {"You are Mightier than ever!", "May your power spread beyond the board!", 
			"You are so cool and powerful!", "Long live the King!", "You are ruler of the board!",
			"May your power be ever greater", "Congratulations your Highness, amazing!"};
	private int boardSize, rowsOfPlayers, totalGreenCheckers, totalBlueCheckers;
	private JPanel mainPanel, boardPanel = new JPanel();
	private JLabel blueKingWinsLabel, greenKingWinsLabel; 
	private CardLayout cardLayout = new CardLayout();
	private Player winner;

	public CheckerBoard5()
	{
		//Ask the user for a board dimension and whether they want the black-red board color or a random color
		setUpBoardSizeAndColor();
		// fill the board with checker squares : red and black
		drawBoard();
		// identify the upper half of the checker board with green checkers, the lower half with blue checkers
		identifyGreenBlueCheckers();
		// this for loop gives each checker its appropriate image: green, blue, or space
		setImages();	
		// create blueKingWins and greenKingWins labels with images
		makeKingWinsLabel();
		// create mainPanel, set layout to CardLayout and add the win labels, board panel,
		// and set cardLayout to show mainPanel's checkerboard;
		makeMainPanel();
		// CheckersListener implements ActionListener in which the checker gets highlighted yellow when selected
		//Movements extends KeyAdapter in which the keys: 1, 2, 9, 0 make each checker move down or up
		addCheckersListener();
		requestFocusInWindow();
		setFocusable(true);
		setLayout(new BorderLayout());  // set to BorderLayout to we can add the mainPanel to the center and have it expand and fill up the whole screen
		this.add(mainPanel, BorderLayout.CENTER);

		// register this panel with a class that implements KeyEventDispatcher in order to listen to keyboard events
		// WITHOUT registering each JButton(checker piece)
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new CheckersDispatcher());

	}

	private void setUpBoardSizeAndColor()
	{
		JOptionPane.showMessageDialog(null, "CheckerBoard4 Instructions: Click to highlight your piece.  Key codes: 1 = move down left, 2 = move down right, 9 = move up left, 0 = move up right");
		// set up the board's parameters
		try
		{
			String answer = JOptionPane.showInputDialog("What board size would you like? (at least 8 x 8)");

			if (answer != null)
			{
				int dimensions = Integer.parseInt(answer);

				if (isValidBoardSize(dimensions))
				{
					boardSize = dimensions;
				}
				else
				{
					JOptionPane.showMessageDialog(null,  "Please enter a valid board size dimension (at least 8x8)", "Invalid Board Size Dimension", JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}

			}
			else
			{
				JOptionPane.showMessageDialog(null,  "Good bye, play again soon!");
				System.exit(0);
			}
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null,  "Please enter a number", "Not valid board size", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}

		rowsOfPlayers = (int) (3.0/8.0 * boardSize);  // the ratio 3.0/8.0 is the ratio of rows occuppied by each player
		board = new Checkers[boardSize][boardSize];
		boardPanel.setLayout(new GridLayout(boardSize, boardSize, 5, 5)); 

		int confirm = JOptionPane.showConfirmDialog(null, "Would you like to change the black-red board color?","Random board color",  JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.OK_OPTION)
		{
			blackColor = new Color(generator.nextInt(128), generator.nextInt(128), generator.nextInt(128));
			redColor = new Color(generator.nextInt(128)+128, generator.nextInt(128)+128, generator.nextInt(128)+128);
		}
	}

	/**
	 * The following methods draw the checker board and implements the algorithm to move the
	 * checker pieces up or down.  The kingMe algorithm gives the pieces the ability to move up and down.  
	 */

	private boolean isValidBoardSize(int dimensions)
	{
		return dimensions >= 4;
	}

	private void drawBoard()
	{
		for (int row = 0; row < board.length; row++)
		{ 
			JPanel panel = new JPanel(new GridLayout(1, boardSize, 5, 5));
			for (int col = 0; col < board.length; col++)
			{
				board[row][col] = new Checkers();  // fill the board with new Checker pieces (buttons)
				if (row %2 == 0)  // if it's an even row then color the checkers black, red, black....
				{
					board[row][col].setBackground((col%2 == 0)? blackColor : redColor);
					board[row][col].color = (col%2 == 0)? CheckerColor.BLACK : CheckerColor.RED;
				}
				else  // if the row is odd, color the checkers red, black, red....
				{
					board[row][col].setBackground( (col%2 == 0)? redColor : blackColor);
					board[row][col].color = (col%2 == 0)? CheckerColor.RED : CheckerColor.BLACK;
				}

				panel.add(board[row][col]);

			}
			boardPanel.add(panel);
		}
	}

	private void identifyGreenBlueCheckers()
	{
		int lastRow = board.length-1;
		for (int firstPlayer = 0; firstPlayer < rowsOfPlayers; firstPlayer++)
		{
			for (int col = 0; col < board.length; col++)
			{
				if (board[firstPlayer][col].color == CheckerColor.BLACK)  // I put a semiColon at the end of this if statement and it suppressed the if check.  So if your if check is not working check for a semicolon at the end of it
				{
					board[firstPlayer][col].player = Player.GREEN; 
					totalGreenCheckers++;
				}
				if (board[lastRow][col].color == CheckerColor.BLACK)
				{
					board[lastRow][col].player = Player.BLUE; 
					totalBlueCheckers++;
				}
			}

			lastRow--;
		}
	}

	// set each checker with an image of either: blue circle, green circle, or emptiness
	private void setImages()
	{
		for (Checkers[] column : board)
		{
			for (Checkers square : column)
			{
				if (square.player == Player.GREEN) 
				{
					square.setIcon(greenChecker);
				}
				else if (square.player  == Player.BLUE) 
				{
					square.setIcon(blueChecker);
				}
				else
				{
					square.setIcon(null);
				}

			}

		}
	}

	private void makeKingWinsLabel()
	{
		blueKingWinsLabel = new JLabel();
		greenKingWinsLabel = new JLabel();
		greenKingWinsLabel.setHorizontalAlignment(JLabel.CENTER);
		blueKingWinsLabel.setHorizontalAlignment(JLabel.CENTER);

		blueKingWinsLabel.setOpaque(true);
		greenKingWinsLabel.setOpaque(true);
		blueKingWinsLabel.setBackground(Color.black);
		greenKingWinsLabel.setBackground(Color.black);

		blueKingWinsLabel.setIcon(new ImageIcon(getClass().getResource("FireworksBlue3a.png")));
		greenKingWinsLabel.setIcon(new ImageIcon(getClass().getResource("FireworksGreen3a.png")));
	}

	private void makeMainPanel()
	{
		mainPanel = new JPanel(cardLayout);
		mainPanel.add(blueKingWinsLabel, "blue wins");
		mainPanel.add(greenKingWinsLabel, "green wins");
		mainPanel.add(boardPanel, "checker board");
		cardLayout.show(mainPanel, "checker board");
	}

	private enum Player
	{
		GREEN, BLUE, SPACE;
	}
	private enum CheckerColor
	{
		BLACK, RED;
	}

	// our own custom Button extending JButton
	private class Checkers extends JButton
	{
		public CheckerColor color;
		public Player player = Player.SPACE; 
		public boolean isKing, isSelected;
		public Checkers()
		{
			setHorizontalAlignment(JButton.CENTER);
			setBorder(BorderFactory.createRaisedBevelBorder());
		}

		@Override
		public boolean isSelected()
		{
			return isSelected;
		}
	}

	public void addCheckersListener()
	{
		for (Checkers[] column : board)
		{
			for (Checkers piece : column)
			{
				if (piece.color == CheckerColor.BLACK) 
				{
					piece.addActionListener(new CheckersListener());
				}
				else
				{
					piece.setEnabled(false);
				}
			}
		}
	}

	private class CheckersListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{  
			Checkers square = (Checkers) e.getSource();

			if (!square.isSelected)  // on the odd clicks, the square becomes selected and the background changes to selectedColor (yellow)
			{  
				square.isSelected = true;
				square.setBackground(selectedColor);
			}
			else 
			{
				square.isSelected = false;
				square.setBackground(blackColor);

			}

		}
	}




	private void changeBoardColor()
	{
		blackColor = new Color(generator.nextInt(256), generator.nextInt(256), generator.nextInt(256));
		redColor = new Color(generator.nextInt(256), generator.nextInt(256), generator.nextInt(256));

		for (Checkers[] column : board)
		{
			for (Checkers square : column)
			{
				if (square.isSelected)
				{
					continue;
				}
				if (square.color == CheckerColor.BLACK)
				{
					square.setBackground(blackColor);
				}
				else
				{
					square.setBackground(redColor);
				}
			}
		}
	}
	private void showWinner()
	{
		if (winner == Player.GREEN)
		{
			cardLayout.show(mainPanel, "green wins");
		}
		else if (winner == Player.BLUE)
		{
			cardLayout.show(mainPanel, "blue wins");
		}
		else 
		{
			JOptionPane.showMessageDialog(null, "The Kings are still battling!");
		}
	}


	public boolean checkOnly1Selected()
	{
		int selected = 0;
		for (Checkers[] column : board)
		{
			for (Checkers square : column)
			{
				if (square.isSelected)
				{
					selected++;
				}
			}
		}
		if (selected > 1)
		{
			JOptionPane.showMessageDialog(null, "Please select only one checker piece");
		}

		return selected == 1;
	}

	public void moveLeftDown()
	{
		int rowsMoved = 1;   // move 1 row down  
		int colsMoved = -1;    // and move 1 col to the left
		// only regular green pieces can move down
		Player currentPlayer = Player.GREEN;
		moveCheckerPiece(rowsMoved, colsMoved, currentPlayer);
	}

	public void moveRightDown()
	{
		int rowsMoved = 1;  // move 1 row down
		int colsMoved = 1;   // and move 1 col to the right
		// green checkers move down
		Player currentPlayer = Player.GREEN;
		moveCheckerPiece(rowsMoved, colsMoved, currentPlayer);
	}

	public void moveLeftUp()
	{
		int rowsMoved = -1;  // move up 1 row
		int colsMoved = -1;   // move 1 col to the left
		// only regular blue checker pieces can move up
		Player currentPlayer = Player.BLUE;
		moveCheckerPiece(rowsMoved, colsMoved, currentPlayer); 
	}

	public void moveRightUp()
	{
		int rowsMoved = -1; // move up 1 row
		int colsMoved = 1;   // move 1 col to the right
		// blue checker pieces move up
		Player currentPlayer = Player.BLUE;
		moveCheckerPiece(rowsMoved, colsMoved, currentPlayer);
	}

	public void moveCheckerPiece(int rowsMoved, int colsMoved, Player currentPlayer)
	{
		mainLoop:
			for (int row = 0; row < board.length; row++)
			{
				for (int col = 0; col < board.length; col++)
				{
					if (board[row][col].isSelected())  // when we found the checker selected
					{

						if (isOpponent(row, col, currentPlayer) && !board[row][col].isKing)  // if it's a regular opponent piece (not King), then it can't move.
						{
							JOptionPane.showMessageDialog(null, "Invalid move");
							return;
						}

						if (board[row][col].isKing)   //*** This is very important:  the current player changes if the piece is a King
						{
							currentPlayer = board[row][col].player;
						}
						try
						{
							int targetRow = row + rowsMoved;
							int targetCol = col + colsMoved;
							if (isEmptySpace(targetRow, targetCol))  // if the diagonal lower left neighbor is a space
							{
								if (board[row][col].isKing)  // check to see if the checker is a king, if so, then keep its identity once it moves
								{
									board[targetRow][targetCol].isKing = true;
								}
								moveToEmptySpace(targetRow, targetCol, currentPlayer);
								moveCurrentPlayer(row, col);
								automaticKingMe(targetRow, targetCol);
							}

							else if (isOpponent(targetRow, targetCol, currentPlayer) && isEmptySpace(targetRow+rowsMoved, targetCol+colsMoved))  // else if there is an opponent at the lower left
							{
								int rowJumpedTo = targetRow+rowsMoved;  // the checker piece jumps to the next diagonal empty square if there is an opponent at the target row and target col
								int colJumpedTo = targetCol+colsMoved;

								if (board[row][col].isKing)
								{
									board[rowJumpedTo][colJumpedTo].isKing = true;
								}
								moveToEmptySpace(rowJumpedTo, colJumpedTo, currentPlayer);
								moveCurrentPlayer(row, col);		
								eatOpponent(targetRow, targetCol);
								automaticKingMe(rowJumpedTo, colJumpedTo);
								checkWin();
							}						
						} catch (ArrayIndexOutOfBoundsException e)
						{
							JOptionPane.showMessageDialog(null,  "Out of Bounds");
						}
						break mainLoop;
					}

				}
			}
	}

	public void moveCurrentPlayer(int row, int col)
	{
		board[row][col].player = Player.SPACE; 
		board[row][col].setIcon(null);// every time a component resets an icon/text/etc., the screen is refreshed to show the changes automatically.
		deselectCurrentPlayer(row, col);
		board[row][col].isKing = false;
	}

	public void deselectCurrentPlayer(int row, int col)
	{ 
		board[row][col].setBackground( blackColor);
		board[row][col].isSelected = false;
	}

	public void moveToEmptySpace(int targetRow, int targetCol, Player currentPlayer)
	{
		board[targetRow][targetCol].player = currentPlayer;

		if (board[targetRow][targetCol].isKing)  // we already made the target row and target col King if the current row and col is King in the moveCheckerPiece() method
		{
			board[targetRow][targetCol].setIcon((currentPlayer == Player.GREEN)? greenKing : blueKing);
		}
		else
		{
			board[targetRow][targetCol].setIcon((currentPlayer == Player.GREEN)? greenChecker : blueChecker);
		}
	}

	public boolean isOpponent(int row, int col, Player currentPlayer)
	{	
		return !(board[row][col].player == currentPlayer);  
	}

	public boolean isEmptySpace(int row, int col)
	{
		return board[row][col].player == Player.SPACE; 
	}

	public void eatOpponent(int opponentRow, int opponentCol)
	{
		if (board[opponentRow][opponentCol].player == Player.GREEN)
		{
			totalGreenCheckers--;
		}
		else
		{
			totalBlueCheckers--;
		}

		board[opponentRow][opponentCol].player = Player.SPACE; 
		board[opponentRow][opponentCol].setIcon(null);
	}

	public void automaticKingMe(int row, int col)
	{
		if (!board[row][col].isKing)
		{
			if (row == 0 || row == board.length-1)
			{
				Player currentPlayer = board[row][col].player;
				board[row][col].isKing = true;
				board[row][col].setIcon((currentPlayer == Player.GREEN)? greenKing : blueKing);
				JOptionPane.showMessageDialog(null,  kingMeCongratulations[generator.nextInt(7)], "You are King!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void checkWin()
	{
		if (totalGreenCheckers == 0)
		{
			cardLayout.show(mainPanel, "blue wins");
			JOptionPane.showMessageDialog(null, "Congratulations Blue King! (press B to see the board)");
			winner= Player.BLUE;
		}
		else if (totalBlueCheckers == 0)
		{
			cardLayout.show(mainPanel, "green wins");
			JOptionPane.showMessageDialog(null, "Congratulations Green King! (press B to see the board)");
			winner = Player.GREEN;
		}
	}


	public class CheckersDispatcher implements KeyEventDispatcher
	{
		@Override
		public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
			{
				int keyCode = e.getKeyCode();

				if (checkOnly1Selected())
				{
					if (keyCode == KeyEvent.VK_1)
					{
						moveLeftDown();
					}
					else if (keyCode == KeyEvent.VK_2)
					{
						moveRightDown();
					}
					else if (keyCode == KeyEvent.VK_9)
					{
						moveLeftUp();
					}
					else if (keyCode == KeyEvent.VK_0)
					{
						moveRightUp();
					}
				}


				if (keyCode == KeyEvent.VK_C)
				{
					System.out.println(blackColor);
					System.out.println(redColor);
				}

				if (keyCode == KeyEvent.VK_D)
				{
					changeBoardColor();
				}

				if (keyCode == KeyEvent.VK_B)
				{
					cardLayout.show(mainPanel, "checker board");
				}

				if (keyCode == KeyEvent.VK_W)
				{
					showWinner();
				}
				repaint();
			}


			return false;
		}
	}

}

