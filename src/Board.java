import java.util.ArrayList;

public class Board {
	public static final int W = 1;
	public static final int B = -1;
	public static final int EMPTY = 0;

	private int[][] gameBoard;

	private int lastPlayer;

	private Move lastMove;

	public static final int dimension = 8;

	/**
	 * Default constructor. Should only be called at the beginning of a game.
	 */
	public Board() {
		this.lastPlayer = W; // B always plays first
		this.lastMove = new Move();
		this.gameBoard = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				this.gameBoard[i][j] = EMPTY;
			}
		}
		this.gameBoard[3][3] = W;
		this.gameBoard[3][4] = B;
		this.gameBoard[4][3] = B;
		this.gameBoard[4][4] = W;
	}

	/**
	 * Copy constructor. Primarily called by getChildren().
	 */
	public Board(Board board) {
		this.lastPlayer = board.getLastPlayer();
		this.lastMove = board.getLastMove();
		this.gameBoard = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				this.gameBoard[i][j] = board.gameBoard[i][j];
			}
		}
	}

	public void print() {
		/* determine who's playing */
		int player = B;
		if (getLastPlayer() == B) {
			player = W;
		}
		switch (player) {
			case B:
				System.out.println("B's turn.");
				break;
			case W:
				System.out.println("W's turn.");
				break;
		}

		System.out.println("  0 1 2 3 4 5 6 7");
		for (int i = 0; i < dimension; i++) {
			System.out.print(i + " ");
			for (int j = 0; j < dimension; j++) {
				switch (this.gameBoard[i][j]) {
					case B -> System.out.print("B ");
					case W -> System.out.print("W ");
					case EMPTY -> System.out.print("- ");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Finds all possible moves for the player with given letter and stores
	 * them in a list.
	 *
	 * Very useful for checking if a state is terminal or if a player must play
	 * or pass his turn.
	 */
	ArrayList<Board> getChildren(int letter) {
		ArrayList<Board> children = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				Move move = new Move(i, j, letter);
				if (isValidMove(move)) {
					Board child = new Board(this);
					child.makeMove(move);
					children.add(child);
				}
			}
		}
		return children;
	}

	/**
	 * Heuristic function. Used by MiniMax algorithm, to compare game board
	 * states and select the optimal one.
	 */
	public int evaluate() {
    	int white_tiles = 0;
    	for (int i = 0; i < dimension; i++) {
    		for (int j = 0; j < dimension; j++) {
    			if (this.gameBoard[i][j] == W) {
    				white_tiles++;
    			}
    		}
    	}
    	return white_tiles;
	}

	/**
	 * Neither player has any available moves. The game board is not
	 * necessarily full.
	 */
	public boolean isTerminal() {
		return getChildren(B).isEmpty() && getChildren(W).isEmpty();
	}

	/**
	 * Applies given move to the game board and updates crucial values like
	 * lastMove and lastPlayer.
	 */
	public void makeMove(Move move) {
		this.gameBoard[move.getRow()][move.getCol()] = move.getValue();
		this.lastMove = move;
		this.lastPlayer = move.getValue();
		calcBoardAfterMove(move.getValue());
	}

	/**
	 * Three conditions need to be met for a move to be considered valid.
	 * 1) Coordinates are in bounds,
	 * 2) the corresponding tile is empty,
	 * 3) by placing the player's letter there, >=1 piece of the last player flips
	 */
	public boolean isValidMove(Move move) {
		if (!isInBounds(move.getRow(), move.getCol())) {
			return false;
		}

		if (this.gameBoard[move.getRow()][move.getCol()] != EMPTY) {
			return false;
		}

		if (!changesTiles(move)) {
			return false;
		}

		return true;
	}

	private boolean isInBounds(int x, int y) {
		return x < dimension && x >= 0 && y < dimension && y >= 0;
	}

	/**
	 * Helper method for makeMove(). Changes all tiles that need to be changed
	 * after making the last move. Very similar to the way we check for valid moves.
	 */
	private void calcBoardAfterMove(int letter) {
		int row = this.lastMove.getRow();
		int col = this.lastMove.getCol();
		int value = this.lastMove.getValue();
		int opp_value = Board.B;
		if (value == opp_value) {
			opp_value = Board.W;
		}

		int i, j, count; // these are used for indexing and counting

		/* flip all tiles above */
		i = row - 1;
		j = col;
		count = 0;
		while (i > 0 && this.gameBoard[i][j] == opp_value) {
			i--;
			count++;
		}
		if (i >= 0 && this.gameBoard[i][j] == value && count > 0) {
			i = row - 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i--;
				count--;
			}
		}

		/* flip all tiles below */
		i = row + 1;
		j = col;
		count = 0;
		while (i < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i++;
			count++;
		}
		if (i <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			i = row + 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i++;
				count--;
			}
		}

		/* flip all tiles left */
		i = row;
		j = col - 1;
		count = 0;
		while (j > 0 && this.gameBoard[i][j] == opp_value) {
			j--;
			count++;
		}
		if (j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			j = col - 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				j--;
				count--;
			}
		}

		/* flip all tiles right */
		i = row;
		j = col + 1;
		count = 0;
		while (j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			j++;
			count++;
		}
		if (j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			j = col + 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				j++;
				count--;
			}
		}

		/* flip all tiles up left */
		i = row - 1;
		j = col - 1;
		count = 0;
		while (i > 0 && j > 0 && this.gameBoard[i][j] == opp_value) {
			i--;
			j--;
			count++;
		}
		if (i >= 0 && j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			i = row - 1;
			j = col - 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i--;
				j--;
				count--;
			}
		}

		/* flip all tiles up right */
		i = row - 1;
		j = col + 1;
		count = 0;
		while (i > 0 && j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i--;
			j++;
			count++;
		}
		if (i >= 0 && j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			i = row - 1;
			j = col + 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i--;
				j++;
				count--;
			}
		}

		/* flip all tiles down left */
		i = row + 1;
		j = col - 1;
		count = 0;
		while (i < dimension - 1 && j > 0 && this.gameBoard[i][j] == opp_value) {
			i++;
			j--;
			count++;
		}
		if (i <= dimension - 1 && j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			i = row + 1;
			j = col - 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i++;
				j--;
				count--;
			}
		}

		/* flip all tiles down right */
		i = row + 1;
		j = col + 1;
		count = 0;
		while (i < dimension - 1 && j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i++;
			j++;
			count++;
		}
		if (i <= dimension - 1 && j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			i = row + 1;
			j = col + 1;
			while (count != 0) {
				this.gameBoard[i][j] = value;
				i++;
				j++;
				count--;
			}
		}
	}

	/**
	 * Helper method for validity check. A valid move is one where at least one
	 * piece in between two of the same color (letter) is reversed (flipped over)
	 */
	private boolean changesTiles(Move move) {
		int row = move.getRow();
		int col = move.getCol();
		int value = move.getValue();

		/* determine opponent value */
		int opp_value = Board.B;
		if (value == opp_value) {
			opp_value = Board.W;
		}

		int i, j, count; // these are used for indexing and counting

		/* move up */
		i = row - 1;
		j = col;
		count = 0;
		while (i > 0 && this.gameBoard[i][j] == opp_value) {
			i--;
			count++;
		}
		if (i >= 0 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move down */
		i = row + 1;
		j = col;
		count = 0;
		while (i < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i++;
			count++;
		}
		if (i <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move left */
		i = row;
		j = col - 1;
		count = 0;
		while (j > 0 && this.gameBoard[i][j] == opp_value) {
			j--;
			count++;
		}
		if (j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move right */
		i = row;
		j = col + 1;
		count = 0;
		while (j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			j++;
			count++;
		}
		if (j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move up left */
		i = row - 1;
		j = col - 1;
		count = 0;
		while (i > 0 && j > 0 && this.gameBoard[i][j] == opp_value) {
			i--;
			j--;
			count++;
		}
		if (i >= 0 && j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move up right */
		i = row - 1;
		j = col + 1;
		count = 0;
		while (i > 0 && j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i--;
			j++;
			count++;
		}
		if (i >= 0 && j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move down left */
		i = row + 1;
		j = col - 1;
		count = 0;
		while (i < dimension - 1 && j > 0 && this.gameBoard[i][j] == opp_value) {
			i++;
			j--;
			count++;
		}
		if (i <= dimension - 1 && j >= 0 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* move down right */
		i = row + 1;
		j = col + 1;
		count = 0;
		while (i < dimension - 1 && j < dimension - 1 && this.gameBoard[i][j] == opp_value) {
			i++;
			j++;
			count++;
		}
		if (i <= dimension - 1 && j <= dimension - 1 && this.gameBoard[i][j] == value && count > 0) {
			return true;
		}

		/* when all hope has faded away... */
		return false;
	}

	public Move getLastMove() {
		return this.lastMove;
	}

	public int getLastPlayer() {
		return this.lastPlayer;
	}

	public void setLastPlayer(int player) {
		this.lastPlayer = player;
	}
}
