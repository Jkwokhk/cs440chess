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
			pieceValues.put(PieceType.ROOK, 4);
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
			pieceValues.put(PieceType.ROOK, 4);
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
			pieceValues.put(PieceType.ROOK, 4);
			for (PieceType piecetype : PieceType.values())
			{
				Integer num_pieces = 0;
				num_pieces = node.getGame().getNumberOfAlivePieces(CustomHeuristics.getMinPlayer(node), piecetype);
				numMaxPlayersWeightedPiecesAlive += num_pieces * pieceValues.getOrDefault(piecetype, 0);
			}
			
			return numMaxPlayersWeightedPiecesAlive;
		}

		

	}






	public static double getMaxPlayerHeuristicValue(DFSTreeNode node)
	{
		// please replace this!
		return DefaultHeuristics.getMaxPlayerHeuristicValue(node);
	}

}
