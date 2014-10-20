package org.ggp.base.player.gamer.statemachine.treesearch;

import java.util.Vector;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.junit.Assert;
import org.junit.Test;


public class NodeTests {

	@Test
	public void Visit_IncrementsVisitCount()
	{
		Node cut = new Node(new MachineState(), new Vector<Move>());

		cut.visit(2.3d);
		cut.visit(3.2d);
		cut.visit(1.0d);
		cut.visit(8.5d);

		Assert.assertEquals(4, cut.visits);
	}

	@Test
	public void Visit_AccumulatesScore()
	{
		Node cut = new Node(new MachineState(), new Vector<Move>());

		cut.visit(2.3d);
		cut.visit(3.2d);
		cut.visit(1.0d);
		cut.visit(8.5d);

		Assert.assertEquals(15, cut.utility, 0.0001);
	}

	@Test
	public void Propagate_IncrementsVisitCountOnCurrentAndAllParentNodes()
	{
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		Assert.assertEquals(2, child1.visits);
		Assert.assertEquals(2, child2.visits);
		Assert.assertEquals(4, parent.visits);
	}

	@Test
	public void Propagate_AccumulatesScoreOnCurrentAndAllParentNodes()
	{
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		Assert.assertEquals(5.5, child1.utility, 0.0001);
		Assert.assertEquals(9.5, child2.utility, 0.0001);
		Assert.assertEquals(15, parent.utility, 0.0001);
	}
}
