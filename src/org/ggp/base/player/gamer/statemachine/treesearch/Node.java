package org.ggp.base.player.gamer.statemachine.treesearch;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class Node {
	public Player player;
	public MachineState state;
	public List<Move> move;
	public int visits;
	public double utility;
	public Node parent;
	public List<Node> children;
	public boolean isTerminal = false;

	public Node(MachineState state, List<Move> move) {
		this(state, move, null);
	}

	public Node(
			MachineState state,
			List<Move> move,
			Node parent) {
		this.state = state;
		this.move = move;
		this.visits = 0;
		this.utility = 0;
		this.children = new Vector<Node>();
		this.parent = parent;
	}

	public void visit(double score) {
		visits = visits + 1;
		utility = utility + score;
	}

	public void propagate(double score) {

		this.visit(score);

		if (this.parent != null) {
			parent.propagate(score);
		}
	}

	public Node get(MachineState state) {

		if (this.state == state) {
			return this;
		}

		Node result = null;
		for (Node child : this.children) {
			result = child.get(state);

			if (result != null) {
				return result;
			}
		}

		return result;
	}

	public void addChild(Node child) {

		this.children.add(child);
	}

	public double getUpperConfidenceBounds() {
		return this.utility / (double)this.visits +
				Math.sqrt(2*Math.log(this.parent.visits) / this.visits);

	}

	public double getMiniMaxUtility(boolean maxi) {

		double miniMaxUtility = 0;

		if (this.children.size() == 0) {
			if (this.visits > 0) {
				return this.utility / (double)this.visits;
			}

			return 0;
		}

		for (Node node : this.children) {

			double childValue = node.getMiniMaxUtility(!maxi);

			if ((maxi && childValue > miniMaxUtility) ||
					(!maxi && childValue < miniMaxUtility)) {
				miniMaxUtility = childValue;
			}
		}

		return miniMaxUtility;
	}

	public int getDepth() {
		if (this.parent == null) {
			return 0;
		}

		return this.parent.getDepth() + 1;
	}

	public boolean isMaxi() {
		return this.getDepth() % 2 == 0;
	}

	public String toString(int level) {

		String result = "";

		for (int i = 0; i < level - 1; i++) {
			result = result + "  ";
		}

		if (this.move != null) {
			result += "|-" + this.move.toString() +
					", utility: " + this.utility +
					", visits: " + this.visits +
					", maxi: " + this.isMaxi() +
					", minimax: " + this.getMiniMaxUtility(this.isMaxi());
		}
		else {
			result += "no move defined here";
		}

		result += "\n\r";
		for (Node child : this.children) {
			result += child.toString(level + 1);
		}

		return result;
	}
}