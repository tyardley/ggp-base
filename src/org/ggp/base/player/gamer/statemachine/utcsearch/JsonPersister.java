package org.ggp.base.player.gamer.statemachine.utcsearch;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class JsonPersister {

	public static String filePath =
			"/home/bluey/Desktop/uctsearchtree.js";

	public void Persist(Node root) {
		String data = "var treeData = [" + NodeToJson(root) + "];";

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

	public String NodeToJson(Node node) {
		String result = "{" + System.lineSeparator();

		String name = node.getMove() != null ? node.getMove().toString() : "root";

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
	    result += "'value': " + value + "," + System.lineSeparator();
	    result += "'type': 'orange'," + System.lineSeparator();
	    result += "'level': 'steelblue '," + System.lineSeparator();

	    if (node.getChildren().size() > 0) {
	    	result += "'children': [";

	    	boolean isFirst = true;
	    	for (Node child : node.getChildren()) {

	    		if (!isFirst) {
	    			result += ",";
	    		}

	    		result += NodeToJson(child) + System.lineSeparator();
	    		isFirst = false;
	    	}

	    	result += "]";
	    }
	    result += "}" + System.lineSeparator();
		return result;
	}
}
