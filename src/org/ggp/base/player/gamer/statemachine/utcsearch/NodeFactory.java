package org.ggp.base.player.gamer.statemachine.utcsearch;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;

public class NodeFactory {

	private StateMachine stateMachine;
	
	public NodeFactory(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	public Node Create(MachineState state, List<Move> move, Node parent) {
		
		boolean isTerminal = stateMachine.isTerminal(state);

		return new Node(state, move, parent, isTerminal);
	}
}
