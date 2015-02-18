package org.ggp.base.player.gamer.statemachine.utcsearch;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JsonPersister {

	public static String filePath =
			"/home/bluey/Desktop/uctsearchtree.js";

	public void Persist(Node root) {
		String data = "var treeData = [" + NodeToJson(root, true, 0) + "];";

		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			writer.write(data);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String NodeToJson(Node node, boolean isBest, int level) {
		String result = "{" + System.lineSeparator();

		String name =
				node.getMove() != null ?
						node.getMove().toString() :
							"root";

		name += " (" + node.getAverageUtility() + ")";
	    result += "'name': '" + name  + "'," + System.lineSeparator();

	    if (node.getParent() != null) {
	    	result += "'parent': '" +
	    			node.getParent().hashCode() + "'," +
	    			System.lineSeparator();
	    }
	    else {
	    	result += "'parent': 'null'," + System.lineSeparator();
	    }

	    double value = ((double)node.getVisits());
	    String colour = "steelblue";

	    if (node.getIsMyMove()) {
	    	colour = "yellow";
	    }

	    if (isBest) {
	    	colour = "orange";
	    }

	    result += "'value': " + value + "," + System.lineSeparator();
	    result += "'type': '" + "steelblue" + "'," + System.lineSeparator();
	    result += "'level': '" + colour + "'," + System.lineSeparator();

	    if (node.getChildren().size() > 0) {
	    	result += "'children': [";

	    	boolean isFirst = true;

	    	List<Node> children = node.getChildren();
	    	double best = getBestUtility(children);

	    	boolean bestFound = false;

	    	for (Node child : children) {

	    		if (!isFirst) {
	    			result += ",";
	    		}

	    		boolean isBestChild = child.getAverageUtility() >= best && isBest;

	    		if (isBestChild) {
	    			bestFound = true;
	    		}

	    		result += NodeToJson(child, isBestChild, level + 1) + System.lineSeparator();

	    		isFirst = false;
	    	}

	    	if (!bestFound && isBest)
	    	{
	    		System.out.println("no best child found: " + node.getMove() + " at level: " + level);
	    		System.out.println("best util: " + best);

		    	for (Node child : children) {
		    		System.out.println(child.getMove() + " util: " + child.getAverageUtility());
		    	}
	    	}

	    	result += "]";
	    }
	    result += "}" + System.lineSeparator();
		return result;
	}

	public double getBestUtility(List<Node> nodes) {

    	double best = -Double.MAX_VALUE;

    	for (Node node : nodes) {

    		if (node.getAverageUtility() > best) {
    			best = node.getAverageUtility();
    		}
    	}

    	return best;
	}
}
