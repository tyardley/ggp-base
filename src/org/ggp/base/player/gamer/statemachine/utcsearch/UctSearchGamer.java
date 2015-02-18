package org.ggp.base.player.gamer.statemachine.utcsearch;

import java.util.List;
import java.util.logging.Logger;

import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class UctSearchGamer extends SampleGamer {

	private Logger logger =
		Logger.getLogger(this.getClass().getCanonicalName());
	private StateMachine stateMachine;
	private NodeFactory nodeFactory;
	private JsonPersister persister;
	private final double ExplorationFactor = 100; // 1 / Math.sqrt(2);
	private Role role;
	private int iterations = 0;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		logger.entering(
				this.getClass().getCanonicalName(),
				"stateMachineSelectMove");

		this.iterations = 0;
		this.stateMachine = getStateMachine();
		this.nodeFactory = new NodeFactory(stateMachine);
		this.persister = new JsonPersister();
		this.role = getRole();


		System.out.println("role: " + this.role.toString());
		System.out.println("role index: " + stateMachine.getRoles().indexOf(role));

		boolean isMyMove = true;

		MachineState currentState = getCurrentState();
		Node root = nodeFactory.Create(currentState, null, null, isMyMove);

		boolean withinBudget = true;

		while (withinBudget) {
			Node selected = TreePolicy(root);
			double reward = DefaultPolicy(selected);

			BackUp(selected, reward);
			iterations++;
			withinBudget = CheckWithinBudget();
		}

		Node bestChild = BestChild(root, 0);
		int roleIndex = stateMachine.getRoles().indexOf(role);
		Move chosenMove = bestChild.getMove().get(roleIndex);
		System.out.println(
				"**[CHOSE MOVE]**: " +
				chosenMove.toString() +
				" (" + iterations + " iterations)");

		if (chosenMove.toString() != "noop") {
			persister.Persist(root);
		}

		return chosenMove;
	}

	public Node TreePolicy(Node node)
			throws MoveDefinitionException,
			TransitionDefinitionException {
		Node current = node;

		while (!current.getIsTerminal()) {
			if (!isFullyExpanded(current)) {
				return Expand(current);
			}
			else {
				current = BestChild(current, ExplorationFactor);
			}
		}

		return current;
	}

	public boolean isFullyExpanded(Node node)
			throws MoveDefinitionException,
			TransitionDefinitionException {

		MachineState state = node.getState();
		List<List<Move>> moves = stateMachine.getLegalJointMoves(state);
		boolean allFound = true;

		for (List<Move> move : moves)
		{
			MachineState nextState = stateMachine.getNextState(state, move);

			boolean foundChild = false;
			for (Node child : node.getChildren()) {
				if (child.getState().equals(nextState)) {
					foundChild = true;
				}
			}

			if (!foundChild) {
				allFound = false;
			}
		}

		return allFound;
	}

	public Node Expand(Node node)
			throws MoveDefinitionException,
			TransitionDefinitionException {

		MachineState state = node.getState();
		List<List<Move>> moves = stateMachine.getLegalJointMoves(state);

		for (List<Move> move : moves)
		{
			MachineState nextState = stateMachine.getNextState(state, move);

			boolean foundChild = false;
			for (Node child : node.getChildren()) {
				if (child.getState().equals(nextState)) {
					foundChild = true;
				}
			}

			if (!foundChild) {
				// add the new child and return the child
				Node newChild = nodeFactory.Create(nextState, move, node, !node.getIsMyMove());
				node.addChild(newChild);
				return newChild;
			}
		}

		throw new IllegalStateException("Cannot expand a fully expanded node");
	}

	public Node BestChild(Node node, double explorationFactor) {

		Node bestChild = null;
		List<Node> children = node.getChildren();

		for (Node child : children) {

			if (bestChild == null) {
				bestChild = child;
			}
			else {
				double childUcb = GetUcb(child, explorationFactor);
				double bestChildUcb = GetUcb(bestChild, explorationFactor);

				if (childUcb > bestChildUcb) {
					bestChild = child;
				}
			}
		}

		return bestChild;
	}

	public double GetUcb(Node node, double explorationFactor) {

		// TODO: node must have a parent
		double nodeUtility = node.getTotalUtility() / ((double)node.getVisits());
		double explorationNumerator =
				2 * Math.log(node.getParent().getVisits());
		double exploration =
				explorationFactor *
				Math.sqrt(explorationNumerator / ((double)node.getVisits()));

		return nodeUtility + exploration;
	}

	public double DefaultPolicy(Node node)
			throws TransitionDefinitionException,
			MoveDefinitionException,
			GoalDefinitionException {

		MachineState endState =
				stateMachine.performDepthCharge(node.getState(), null);

		int result = stateMachine.getGoal(endState, role);

		if (node.getIsMyMove()) {
			result = -result;
		}

		return result;
	}

	public void BackUp(Node node, double reward) {
		while (node != null)
		{
			node.visit(reward);
			node = node.getParent();
			reward = -reward;
		}
	}

	public boolean CheckWithinBudget() {
		if (iterations > 100000) {
			return false;
		}

		return true;
	}
}
