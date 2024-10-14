package src.pas.chess.heuristics;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.move.CaptureMove;
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
				if (!left_support && !right_support)
				{
					score -= 0.5;
				}
			}
			return score;
		}
		// passed pawn only good for end game

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

		// call this method to get the Pawn Structure score/penalty
		public static double evaluatePawnStructure(DFSTreeNode node)
		{
			double score = 0.0;
			Set<Piece> pawns = node.getGame().getBoard().getPieces(CustomHeuristics.getMaxPlayer(node), PieceType.PAWN);
			score += getIsolatedPawns(pawns, node);
			score += getDoublePawns(pawns, node);
			score += getBackwardPawns(pawns, node);
			score += getCenterPawns(pawns, node);
			return score;

		}

	}


	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// please replace this!
		return DefaultHeuristics.getMaxPlayerHeuristicValue(node);
	}

}
