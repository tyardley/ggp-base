package org.ggp.base.player.gamer.statemachine.utcsearch;

import java.util.List;
import java.util.Vector;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class Node {
	private MachineState state;
	private List<Move> move;
	private int visits;
	private double totalUtility;
	private Node parent;
	private List<Node> children;
	private boolean isTerminal = false;
	private boolean isMyMove = false;

	public Node(
			MachineState state,
			List<Move> move,
			boolean isTerminal,
			boolean isMyMove) {
		this.state = state;
		this.move = move;
		this.visits = 0;
		this.totalUtility = 0;
		this.children = new Vector<Node>();
		this.parent = null;
		this.isTerminal = isTerminal;
		this.isMyMove = isMyMove;
	}

	public Node(
			MachineState state,
			List<Move> move,
			Node parent,
			boolean isTerminal,
			boolean isMyMove) {
		this.state = state;
		this.move = move;
		this.visits = 0;
		this.totalUtility = 0;
		this.children = new Vector<Node>();
		this.parent = parent;
		this.isTerminal = isTerminal;
		this.isMyMove = isMyMove;
	}

	public MachineState getState() { return this.state; }

	public List<Move> getMove() { return this.move; }

	public int getVisits() { return this.visits; }

	public double getTotalUtility() { return this.totalUtility; }

	public double getAverageUtility() { return this.totalUtility / ((double)this.visits); }

	public Node getParent() { return this.parent; }

	public List<Node> getChildren() { return this.children; }

	public boolean getIsTerminal() { return this.isTerminal; }

	public boolean getIsMyMove() { return this.isMyMove; }

	public void addChild(Node node) {
		this.children.add(node);
	}

	public void visit(double utility) {
		this.visits++;
		this.totalUtility += utility;
	}
}
