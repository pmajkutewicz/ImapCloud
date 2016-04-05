package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Iterator;

abstract class AbstractVertexConverter {
	Vertex getParentVertex(Vertex v, String edge) {
		Iterator<Edge> iterator = v.getEdges(Direction.OUT, edge).iterator();
		if (iterator.hasNext()) {
			return iterator.next().getVertex(Direction.IN);
		}
		return null;
	}
}
