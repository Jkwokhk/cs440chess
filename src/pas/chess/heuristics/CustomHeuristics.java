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
import java.util.List;
import java.util.Map;

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






	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// please replace this!
		return DefaultHeuristics.getMaxPlayerHeuristicValue(node);
	}

}
