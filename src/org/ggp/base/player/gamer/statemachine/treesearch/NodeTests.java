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

		Assert.assertEquals(4, cut.getVisits());
	}

	@Test
	public void Visit_AccumulatesScore()
	{
		Node cut = new Node(new MachineState(), new Vector<Move>());

		cut.visit(2.3d);
		cut.visit(3.2d);
		cut.visit(1.0d);
		cut.visit(8.5d);

		Assert.assertEquals(15, cut.getUtility(), 0.0001);
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

		Assert.assertEquals(2, child1.getVisits());
		Assert.assertEquals(2, child2.getVisits());
		Assert.assertEquals(4, parent.getVisits());
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

		Assert.assertEquals(5.5, child1.getUtility(), 0.0001);
		Assert.assertEquals(9.5, child2.getUtility(), 0.0001);
		Assert.assertEquals(15, parent.getUtility(), 0.0001);
	}

	@Test
	public void GetUpperConfidenceBounds_ReturnsCorrectValue()
	{
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		// value is 5.5 / 2 = 2.75
		// visits is 2 and parent visits is 4
		// visit factor = 0.832554611
		// UCB = 2.75 + 0.832554611 = 3.582554611
		Assert.assertEquals(3.582554611, child1.getUpperConfidenceBounds(), 0.0001);

		// value is 9.5 / 2 = 4.75
		// visits is 2 and parent visits is 4
		// visit factor = 0.832554611
		// UCB = 4.75 + 0.832554611 = 5.582554611
		Assert.assertEquals(5.582554611, child2.getUpperConfidenceBounds(), 0.0001);
	}

	@Test
	public void GetMiniMaxUtility_ReturnsNodeUtility_WithNoChildren() {
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		Assert.assertEquals(
				2.75,
				child1.getMiniMaxUtility(false), 0.0001);
		Assert.assertEquals(
				4.75,
				child2.getMiniMaxUtility(false), 0.0001);
	}

	@Test
	public void GetMiniMaxUtility_ReturnsHighestChildUtility_WhenMaxiWithChildren() {
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		Assert.assertEquals(
				4.75,
				parent.getMiniMaxUtility(true), 0.0001);
	}

	@Test
	public void GetMiniMaxUtility_ReturnsLowestChildUtility_WhenMiniWithChildren() {
		Node parent = new Node(new MachineState(), new Vector<Move>());
		Node child1 = new Node(new MachineState(), new Vector<Move>(), parent);
		Node child2 = new Node(new MachineState(), new Vector<Move>(), parent);

		child1.propagate(2.3d);
		child1.propagate(3.2d);
		child2.propagate(1.0d);
		child2.propagate(8.5d);

		Assert.assertEquals(
				2.75,
				parent.getMiniMaxUtility(false), 0.0001);
	}
}
