import java.util.ArrayList;
import java.util.Random;

class Player {
	private int maxDepth;
	private int playerLetter;
	private boolean human;

	public Player() {}

	public Player(int maxDepth, int playerLetter, boolean human) {
		this.maxDepth = maxDepth;
		this.playerLetter = playerLetter;
		this.human = human;
	}

	public Move MiniMax(Board board) {
		/* human players should never call this */
		if (isHuman()) {
			return new Move(); // returns a move out of bounds. See: Move.java
		}

		ArrayList<Board> children = board.getChildren(playerLetter);

		/* player has to pass this turn */
		if (children.isEmpty()) {
			return null;
		}

		if (playerLetter == Board.W) {
			// if W plays then it wants to maximize the heuristics value
			return max(new Board(board), 0, children);
		}

		// if B plays then it wants to minimize the heuristics value
		return min(new Board(board), 0, children);
	}

	public Move max(Board board, int depth, ArrayList<Board> children) {
		Random r = new Random();
		if (board.isTerminal() || (depth == this.maxDepth)) {
			Move lastMove = board.getLastMove();
			return new Move(lastMove.getRow(), lastMove.getCol(), board.evaluate());
		}

		//ArrayList<Board> children = board.getChildren(Board.W);
		Move maxMove = new Move(Integer.MIN_VALUE);
		for (Board child: children) {
			Move move = min(child, depth + 1, child.getChildren(Board.B));
			if (move.getValue() >= maxMove.getValue()) {
				if (move.getValue() == maxMove.getValue()) {
					if (r.nextInt(2) == 0) {
						maxMove.setRow(child.getLastMove().getRow());
						maxMove.setCol(child.getLastMove().getCol());
						maxMove.setValue(move.getValue());
					}
				} else {
					maxMove.setRow(child.getLastMove().getRow());
					maxMove.setCol(child.getLastMove().getCol());
					maxMove.setValue(move.getValue());
				}
			}
		}
		return maxMove;
	}

	public Move min(Board board, int depth, ArrayList<Board> children) {
		Random r = new Random();
		if (board.isTerminal() || (depth == this.maxDepth)) {
			Move lastMove = board.getLastMove();
			return new Move(lastMove.getRow(), lastMove.getCol(), board.evaluate());
		}

		//ArrayList<Board> children = board.getChildren(Board.B);
		Move minMove = new Move(Integer.MAX_VALUE);
		for (Board child: children) {
			Move move = max(child, depth + 1, child.getChildren(Board.W));
			if (move.getValue() <= minMove.getValue()) {
				if (move.getValue() == minMove.getValue()) {
					if (r.nextInt(2) == 0) {
						minMove.setRow(child.getLastMove().getRow());
						minMove.setCol(child.getLastMove().getCol());
						minMove.setValue(move.getValue());
					}
				} else {
					minMove.setRow(child.getLastMove().getRow());
					minMove.setCol(child.getLastMove().getCol());
					minMove.setValue(move.getValue());
				}
			}
		}
		return minMove;
	}

	public boolean isHuman() {
		return this.human;
	}

	public int getLetter() {
		return this.playerLetter;
	}
}
