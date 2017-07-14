package checkers;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PlayCheckers5 extends JFrame
{
	private CheckerBoard5 checkerBoard = new CheckerBoard5();
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new PlayCheckers5();
			}
		});

	}

	public PlayCheckers5()
	{
		super("Play Checkers!");
		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		add(checkerBoard);

	}
}
