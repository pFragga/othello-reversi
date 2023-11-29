import java.util.Scanner;

public class Main {
	private static final Scanner in = new Scanner(System.in);

	private static void playTurn(Player p, Board b) {
		String letter = "W";
		if (p.getLetter() == Board.B) {
			letter = "B";
		}

		Move move;
		if (p.isHuman()) {
			if (b.getChildren(p.getLetter()).isEmpty()) {
				b.setLastPlayer(p.getLetter());
				System.out.println("No moves for " + letter + ". PASS");
			} else {
				System.out.print("Give X Cordinates: ");
				int x_pos = in.nextInt();
				System.out.print("Give Y Cordinates: ");
				int y_pos = in.nextInt();
				move = new Move(x_pos, y_pos, p.getLetter());

				if (b.isValidMove(move)) {
					b.makeMove(move);
				}
			}
		} else {
			move = p.MiniMax(b);
			if (move == null) {
				b.setLastPlayer(p.getLetter());
				System.out.println("No moves for " + letter + ". PASS");
			} else {
				System.out.println("Played at " + move.getRow() + "," + move.getCol());
				Move newMove = new Move(move.getRow(), move.getCol(), p.getLetter());
				if (b.isValidMove(newMove)) { // just to make sure
					b.makeMove(newMove);
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Welcome to Othello (Reversi)!");
		System.out.print("Enter a maximum depth for MiniMax: ");
		int maxDepth = in.nextInt();
		System.out.print("Choose a color (B goes first): [B/W] ");
		String input = in.next();

		int userLetter = Board.B;
		int computerLetter = Board.W;
		if (input.equals("W")) {
			userLetter = Board.W;
			computerLetter = Board.B;
		}

		Player user = new Player(maxDepth, userLetter, true);
		Player computer = new Player(maxDepth, computerLetter, false);
		Board b = new Board();
		b.print();

		while (!b.isTerminal()) {
			if (b.getLastPlayer() == user.getLetter()) {
				playTurn(computer, b);
			} else {
				playTurn(user, b);
			}
			b.print();
		}

		in.close();
	}
}
