package src.pas.chess.heuristics;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.move.CaptureMove;
import edu.bu.chess.game.move.MovementMove;
import edu.bu.chess.game.move.PromotePawnMove;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.piece.Queen;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.search.DFSTreeNode;

import edu.bu.chess.utils.Coordinate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// JAVA PROJECT IMPORTS
import src.pas.chess.heuristics.DefaultHeuristics;
// heuristics : pawn weight , pawn structure, piece moves

public class CustomHeuristics
    extends Object
{

	/**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
	 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
	 * in DefaultHeuristics.java (which is in the same directory as this file)
	 */

	//  get maximizer player
	public static Player getMaxPlayer(DFSTreeNode node){
		return node.getMaxPlayer();
	}
	// get min player from node
	public static Player getMinPlayer(DFSTreeNode node){

		return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) ? node.getGame().getOtherPlayer() : node.getGame().getCurrentPlayer();
	}

	public static class OffensiveHeuristics extends Object{
		
		// method to find num pieces of enemy with weighted pieces
		
		public static int getWeightedThreat(DFSTreeNode node)
		{
			int weighted_threat = 0;
			// pieces value
			Map<PieceType, Integer> pieceValues = new HashMap<>();
			pieceValues.put(PieceType.BISHOP, 3);
			pieceValues.put(PieceType.KNIGHT, 3);
			pieceValues.put(PieceType.PAWN, 1);
			pieceValues.put(PieceType.QUEEN, 9);
			pieceValues.put(PieceType.ROOK, 5);
			// what should the king's weight be?
			// pieceValues.put(PieceType.KING, 999);
			// Get all pieces for maximizer / attacker
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))){
				List<Move> captureMoves = piece.getAllCaptureMoves(node.getGame());
				for (Move move : captureMoves){
					CaptureMove capturemove = (CaptureMove) move;
					Integer targetID = capturemove.getTargetPieceID();
					// should i get min?
					if (targetID != null){
					Piece target = node.getGame().getBoard().getPiece(CustomHeuristics.getMinPlayer(node), targetID);
					weighted_threat += pieceValues.getOrDefault(target.getType(), 0);
					}
				}
			}

			return weighted_threat;
		}
	}


	public static class DefensiveHeuristics extends Object{

		// check how many piece I own with weight
		public static int getNumberOfMaxAliveWeightedPieces(DFSTreeNode node)
		{
			int numMaxPlayersWeightedPiecesAlive = 0;
			Map<PieceType, Integer> pieceValues = new HashMap<>();
			pieceValues.put(PieceType.BISHOP, 3);
			pieceValues.put(PieceType.KNIGHT, 3);
			pieceValues.put(PieceType.PAWN, 1);
			pieceValues.put(PieceType.QUEEN, 9);
			pieceValues.put(PieceType.ROOK, 5);
			for (PieceType piecetype : PieceType.values())
			{
				Integer num_pieces = 0;
				num_pieces = node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMaxPlayer(node), piecetype);
				numMaxPlayersWeightedPiecesAlive += num_pieces * pieceValues.getOrDefault(piecetype, 0);
			}
			
			return numMaxPlayersWeightedPiecesAlive;
		}

		// check how many pieces min player / enemy owns with weight
		public static int getNumberOfMinAliveWeightedPieces(DFSTreeNode node)
		{
			int numMaxPlayersWeightedPiecesAlive = 0;
			Map<PieceType, Integer> pieceValues = new HashMap<>();
			pieceValues.put(PieceType.BISHOP, 3);
			pieceValues.put(PieceType.KNIGHT, 3);
			pieceValues.put(PieceType.PAWN, 1);
			pieceValues.put(PieceType.QUEEN, 9);
			pieceValues.put(PieceType.ROOK, 5);
			for (PieceType piecetype : PieceType.values())
			{
				Integer num_pieces = 0;
				num_pieces = node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node), piecetype);
				numMaxPlayersWeightedPiecesAlive += num_pieces * pieceValues.getOrDefault(piecetype, 0);
			}
			
			return numMaxPlayersWeightedPiecesAlive;
		}

		public static int getNumberOfWeightedPiecesThreateningMaxPlayer(DFSTreeNode node)
		{
			// how many piece are threatening me with weight
			// king weight??? inf? because king is being threatened
			int weighted_threat = 0;
			Map<PieceType, Integer> pieceValues = new HashMap<>();
			pieceValues.put(PieceType.BISHOP, 3);
			pieceValues.put(PieceType.KNIGHT, 3);
			pieceValues.put(PieceType.PAWN, 1);
			pieceValues.put(PieceType.QUEEN, 9);
			pieceValues.put(PieceType.ROOK, 5);
			// need kings weight because enemy is targeting king
			pieceValues.put(PieceType.KING, 99999);
			for(Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node)))
			{
				List<Move> captureMoves = piece.getAllCaptureMoves(node.getGame());
				for (Move move : captureMoves){
					CaptureMove capturemove = (CaptureMove) move;
					Integer targetID = capturemove.getTargetPieceID();
					// should i get min?
					if (targetID != null){
					Piece target = node.getGame().getBoard().getPiece(CustomHeuristics.getMaxPlayer(node), targetID);
					weighted_threat += pieceValues.getOrDefault(target.getType(), 0);
					}
				}
			}
			return weighted_threat;
		}

		public static int getClampedPieceValueTotalSurroundingMaxPlayersKing(DFSTreeNode node)
		{
			// what is the state of the pieces next to the king? add up the values of the neighboring pieces
			// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
			int maxPlayerKingSurroundingPiecesValueTotal = 0;

			Piece kingPiece = node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node), PieceType.KING).iterator().next();
			Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
			for(Direction direction : Direction.values())
			{
				Coordinate neightborPosition = kingPosition.getNeighbor(direction);
				if(node.getGame().getBoard().isInbounds(neightborPosition) && node.getGame().getBoard().isPositionOccupied(neightborPosition))
				{
					Piece piece = node.getGame().getBoard().getPieceAtPosition(neightborPosition);
					int pieceValue = Piece.getPointValue(piece.getType());
					if(piece != null && kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal -= pieceValue;
					} else if(piece != null && !kingPiece.isEnemyPiece(piece))
					{
						maxPlayerKingSurroundingPiecesValueTotal += pieceValue;
					}
				}
			}
			// kingSurroundingPiecesValueTotal cannot be < 0 b/c the utility of losing a game is 0, so all of our utility values should be at least 0
			maxPlayerKingSurroundingPiecesValueTotal = Math.max(maxPlayerKingSurroundingPiecesValueTotal, 0);
			return maxPlayerKingSurroundingPiecesValueTotal;
		}
	}
	// evaluating pawn structure, should i implement for both enemy and player?

	public static class PawnStructureHeuristics extends Object{

		public static double getIsolatedPawns(Set<Piece> pawns, DFSTreeNode node)
		{
			double score = 0.0;
			Set<Coordinate> pawnCoordinates = new HashSet<>();
			
			for(Piece pawn : pawns)
			{
				Coordinate pos = pawn.getCurrentPosition(node.getGame().getBoard());
				pawnCoordinates.add(pos);
			}
			for (Coordinate position : pawnCoordinates)
			{
				boolean isAdjacent = false;
				boolean isProtected = false;
				Integer pawn_x = position.getXPosition();
				Integer pawn_y = position.getYPosition();
				// Check if there are pawns in adjacent columns
				Coordinate left_pawn = new Coordinate (pawn_x-1, pawn_y);
				Coordinate right_pawn = new Coordinate (pawn_x+1, pawn_y);
				if(pawnCoordinates.contains(left_pawn)||pawnCoordinates.contains(right_pawn))
				{
					isAdjacent = true;
				}
				// Check if the pawn is being protected
				Coordinate left_protecting_pawn = new Coordinate(pawn_x-1, pawn_y-1);
				Coordinate right_protecting_pawn = new Coordinate(pawn_x+1, pawn_y-1);
				if(pawnCoordinates.contains(left_protecting_pawn)||pawnCoordinates.contains(right_protecting_pawn))
				{
					isProtected = true;
				}
				if(!isAdjacent && !isProtected)
				{
					score -= 0.5;
					System.out.println("Isolated pawn!");
				}

			}
			
			return score;
		}

		public static double getDoublePawns(Set<Piece> pawns, DFSTreeNode node)
		{
			double score = 0.0;

			Map<Integer, Integer> pawnCount = new HashMap<>();
			for(Piece pawn : pawns)
			{
				Coordinate pos = pawn.getCurrentPosition(node.getGame().getBoard());
				// counter for number of pawns in each column
				pawnCount.put(pos.getXPosition(), pawnCount.getOrDefault(pos.getXPosition(), 0)+1);
			}
			for (Integer count : pawnCount.values())
			{
				if (count > 1)
				{
					score -= (count - 1) * 0.5;
				}
			}
			System.out.println("double pawns!");
			return score;
		}

		// backward pawn?
		public static double getBackwardPawns(Set<Piece> pawns, DFSTreeNode node)
		{
			double score = 0.0;
			Set<Coordinate> pawnCoordinates = new HashSet<>();
			// get pawn positions
			for (Piece pawn : pawns)
			{
				Coordinate pos = pawn.getCurrentPosition(node.getGame().getBoard());
				pawnCoordinates.add(pos);
			}
			// check backward pawn
			for(Coordinate position : pawnCoordinates)
			{
				Integer pawn_x = position.getXPosition();
				Integer pawn_y = position.getYPosition();
				boolean left_support = false;
				boolean right_support = false;
				// check if there are friendly pawns in adj cols that are ahead
				for (Coordinate other_positions : pawnCoordinates)
				{
					if(other_positions.getXPosition()==pawn_x-1 && other_positions.getYPosition() > pawn_y)
					{
						left_support = true;
						break;
					}

				}
				for (Coordinate other_positions : pawnCoordinates)
				{
					if(other_positions.getXPosition()==pawn_x+1 && other_positions.getYPosition() > pawn_y)
					{
						right_support = true;
						break;
					}
				}

				// vvvvvvvvv   -----doesn't backwards pawn also mean that the pawn cannot safely advance?------ vvvvvvvvv

				// boolean canAdvanceSafely = true;

				// Coordinate forwardPosition = new Coordinate(pawn_x, pawn_y + 1); 
				// // Check if enemy pieces are threatening the square in front
				// for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
				// 	List<Move> movementMoves = piece.getAllMoves(node.getGame());
				// 	for (Move move : movementMoves) {
				// 		MovementMove movementmove = (MovementMove) move;
				// 		Coordinate targetSquare = movementmove.getTargetPosition();

				// 		// if there is an enemy attacking the square in front of the pawn
				// 		if ( targetSquare.equals(forwardPosition) ) {
				// 			break;
				// 		}
				// 	}
				// }

				// ^^^^^^^^^  -----doesn't backwards pawn also mean that the pawn cannot safely advance?------ ^^^^^^^^^^^^^
				
				/* 
				if (!left_support && !right_support && !canAdvanceSafely) {
					score -= 0.5;
				}
				*/


				if (!left_support && !right_support)
				{
					score -= 0.5;
				}
			}
			return score;
		}

		// check if center has a lot of pawns
		// question does this differentiate self or enemy?
		public static double getCenterPawns(Set<Piece> pawns, DFSTreeNode node)
		{
			double score = 0.0;
			Set<Coordinate> center = new HashSet<>();
			// set up center squares
			for (Integer x = 2; x<= 5; x++)
			{
				for (Integer y = 2; y<=5; y++)
				{
					center.add(new Coordinate(x,y));
				}
			}
			// count how many pawns in center
			Integer center_pawn_count = 0;
			for (Piece pawn : pawns)
			{
				Coordinate pos = pawn.getCurrentPosition(node.getGame().getBoard());
				if(center.contains(pos))
				{
					center_pawn_count+=1;
				}
			}
			score += center_pawn_count * 0.25;
			return score;
		}

		// checks for passed pawns
		public static double getPassedPawns(DFSTreeNode node) {
			double score = 0.0;
		
			for (Piece pawn : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.PAWN)) {
				if (isPassedPawn(pawn, node)) {
					score += 1;
				}
			}
		
			return score;
		}
		
		// helper func for getPassedPawns
		private static boolean isPassedPawn(Piece pawn, DFSTreeNode node) {
			Coordinate pos = pawn.getCurrentPosition(node.getGame().getBoard());
			int x = pos.getXPosition();
			int y = pos.getYPosition();

			
			for (int row = -1; row <= 1; row++) {

				int adjPos = x + row;
				if ( !(1 <= adjPos && adjPos <= 8) ) {
					continue;
				}

				for (int col = y; col <= 1; col++) {

					Coordinate checkPos = new Coordinate(adjPos, col);
					Piece blockingPawn = node.getGame().getBoard().getPieceAtPosition(checkPos);
               		if (blockingPawn != null && blockingPawn.getType() == PieceType.PAWN &&
                    	blockingPawn.getPlayer().equals(CustomHeuristics.getMinPlayer(node))) {
                    	return false;
					}
					
				}
			}
			return true;
			
		}
		
		

		// call this method to get the Pawn Structure score/penalty
		public static double evaluatePawnStructure(DFSTreeNode node)
		{
			double phase = getGamePhase(node);

			double score = 0.0;
			Set<Piece> pawns = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.PAWN);
			score += getIsolatedPawns(pawns, node);
			score += getDoublePawns(pawns, node);
			score += getBackwardPawns(pawns, node);
			score += getCenterPawns(pawns, node);
			score += getPassedPawns(node);

			return score * (1 - phase);

		}

	}
	public static class BoardControlHeuristics {

		public static final double[][] KNIGHT_POS = {
			{-5,  -4,  -3,   -3,     -3,   -3,   -4,   -5},
			{-4,  -2,   0,    0,     0,     0,   -2,   -4},
			{-3,   0,   1,    1.5,   1.5,   1,    0,   -3},
			{-3,   0.5, 1.5,  2,     2,     1.5,  0.5, -3},
			{-3,   0,   1.5,  2,     2,     1.5,  0,   -3},
			{-3,   0.5, 1,    1.5,   1.5,   1,    0.5, -3},
			{-4,  -2,   0,    0.5,   0.5,   0,   -2,   -4},
			{-5,  -4,  -3,    -3,   -3,    -3,   -4,   -5}
		};
		
		public static final double[][] QUEEN_POS = {
			{-5,  -4,  -3,   -3,     -3,   -3,   -4,   -5},
			{-4,  -2,   0,    0,     0,     0,   -2,   -4},
			{-3,   0,   1,    2,     2,     1,    0,   -3},
			{-3,   0.5, 2,    4,     4,     2,    0.5, -3},
			{-3,   0,   2,    4,     4,     2,    0,   -3},
			{-3,   0.5, 1,    2,     2,     1,    0.5, -3},
			{-4,  -2,   0,    0.5,   0.5,   0,   -2,   -4},
			{-5,  -4,  -3,    -3,   -3,    -3,   -4,   -5}
		};
		
		// check if the position of your pieces are near the center/advantageous
		public static double evaluateCenterControl(DFSTreeNode node) {
			double score = 0.0;
		
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
				Coordinate pos = piece.getCurrentPosition(node.getGame().getBoard());
				int x = pos.getXPosition();
				int y = pos.getYPosition();
				switch (piece.getType()) {
					case KNIGHT:
						score += KNIGHT_POS[y-1][x-1];
						break;
					case QUEEN:
						score += QUEEN_POS[y-1] [x-1];
						break;
					// Add cases for other piece types?
				}
			}
		
			// Subtract opponent's piece-square values
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
				Coordinate pos = piece.getCurrentPosition(node.getGame().getBoard());
				int x = pos.getXPosition();
				int y = pos.getYPosition();
				switch (piece.getType()) {
					case KNIGHT:
						score -= KNIGHT_POS[y-1] [x-1]; 
						break;
					case QUEEN:
						score -= QUEEN_POS [y-1] [x-1];
						break;
					// Add cases for other piece types?
				}
			}
		
			return score;
		}

		// check who has better mobility -- important in mid/beginning game
		public static double evaluateMobility(DFSTreeNode node) {
			double score = 0.0;
		
			int maxPlayerMobility = 0;
			int minPlayerMobility = 0;
		
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node))) {
				maxPlayerMobility += piece.getAllMoves(node.getGame()).size();
			}
		
			for (Piece piece : node.getGame().getBoard().getPieces(CustomHeuristics.getMinPlayer(node))) {
				minPlayerMobility += piece.getAllMoves(node.getGame()).size();
			}
		
			score += 0.1 * (maxPlayerMobility - minPlayerMobility);
		
			return score;
		}
		
		
		// Rooks on open files more stronger
		public static double evaluateRookPosition(DFSTreeNode node) {
			double score = 0.0;
		
			for (Piece rook : node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.ROOK)) {
				int x = rook.getCurrentPosition(node.getGame().getBoard()).getXPosition();
		
				boolean openFile = true;
				boolean semiOpenFile = true;
		
				for (int y = 0; y <= 7; y++) {
					Coordinate pos = new Coordinate(x, y);
					Piece piece = node.getGame().getBoard().getPieceAtPosition(pos);
					if (piece != null && piece.getType() == PieceType.PAWN) {
						if (piece.getPlayer().equals(CustomHeuristics.getMaxPlayer(node))) {
							openFile = false;
							semiOpenFile = false;
							break;
						} else {
							openFile = false;
						}
					}
				}
		
				if (openFile) {
					score += 0.5;
				} else if (semiOpenFile) {
					score += 0.25;
				}
			}
		
			return score;
		}
		

		public static double evaluateBoardControl(DFSTreeNode node)
		{
			double phase = getGamePhase(node);
			double score = 0.0;

			score += evaluateCenterControl(node);
			score += evaluateMobility(node) * phase;
			score += evaluateRookPosition(node);
			return score;

		}


	}


	// taken from default
	public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// remember the action has already taken affect at this point, so capture moves have already resolved
		// and the targeted piece will not exist inside the game anymore.
		// however this value was recorded in the amount of points that the player has earned in this node
		double damageDealtInThisNode = node.getGame().getBoard().getPointsEarned(DefaultHeuristics.getMaxPlayer(node));

		switch(node.getMove().getType())
		{
		case PROMOTEPAWNMOVE:
			PromotePawnMove promoteMove = (PromotePawnMove)node.getMove();
			damageDealtInThisNode += Piece.getPointValue(promoteMove.getPromotedPieceType());
			break;
		default:
			break;
		}
		// offense can typically include the number of pieces that our pieces are currently threatening
		int numPiecesWeAreThreatening = OffensiveHeuristics.getWeightedThreat(node);

		return damageDealtInThisNode + numPiecesWeAreThreatening;
	}

	// taken from default
	public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// how many pieces exist on our team?
		int numPiecesAlive = DefensiveHeuristics.getNumberOfMaxAliveWeightedPieces(node);

		// what is the state of the pieces next to the king? add up the values of the neighboring pieces
		// positive value for friendly pieces and negative value for enemy pieces (will clamp at 0)
		int kingSurroundingPiecesValueTotal = DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

		// how many pieces are threatening us?
		int numPiecesThreateningUs = DefensiveHeuristics.getNumberOfWeightedPiecesThreateningMaxPlayer(node);

		return numPiecesAlive + kingSurroundingPiecesValueTotal + numPiecesThreateningUs;
	}

	public static double getGamePhase(DFSTreeNode node) {
		int totalMaterial = 0;
		for (PieceType type : PieceType.values()) {
			if (type != PieceType.KING) {
				totalMaterial += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMaxPlayer(node), type) * Piece.getPointValue(type);
				totalMaterial += node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node), type) * Piece.getPointValue(type);
			}
		}
		// Maximum possible material excluding kings is 78
		double phase = (double) totalMaterial / 78.0;
		return phase; // Closer to 1.0 is opening, closer to 0.0 is endgame
	}
	

	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// please replace this!
		double defensiveHeuristicValue = DefaultHeuristics.getDefensiveMaxPlayerHeuristicValue(node);
		double pawnHeuristicValue = PawnStructureHeuristics.evaluatePawnStructure(node);
		double boardcontrolHeuristicValue = BoardControlHeuristics.evaluateBoardControl(node);
		return defensiveHeuristicValue + pawnHeuristicValue + boardcontrolHeuristicValue;
		
	}

}
