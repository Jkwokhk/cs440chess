package src.pas.chess.moveorder;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;

import java.util.LinkedList;
import java.util.List;


// JAVA PROJECT IMPORTS
import src.pas.chess.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer
    extends Object
{

	/**
	 * TODO: implement me!
	 * This method should perform move ordering. Remember, move ordering is how alpha-beta pruning gets part of its power from.
	 * You want to see nodes which are beneficial FIRST so you can prune as much as possible during the search (i.e. be faster)
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
	 * @return The ordered nodes.
	 */
	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes)
	{
		// please replace this!

		List<DFSTreeNode> captureNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> movementNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> castleNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> promotepawnNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> enpassantNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> otherNodes = new LinkedList<DFSTreeNode>();
		List<DFSTreeNode> orderedNodes = new LinkedList<DFSTreeNode>();

		for(DFSTreeNode node : nodes)
		{
			if(node.getMove() != null)
			{
				switch (node.getMove().getType()) {
					case CAPTUREMOVE:
						captureNodes.add(node);
						break;
					case CASTLEMOVE:
						castleNodes.add(node);
					case PROMOTEPAWNMOVE:
						promotepawnNodes.add(node);
					default:
						otherNodes.add(node);
				}
			}
			else
			{
				otherNodes.add(node);
			}
		}

		orderedNodes.addAll(captureNodes);
		orderedNodes.addAll(castleNodes);
		orderedNodes.addAll(promotepawnNodes);
		orderedNodes.addAll(otherNodes);


		return orderedNodes;
	}

}
