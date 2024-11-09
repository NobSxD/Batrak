package org.example.dto;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

public record NodeDAO(NodeOrdersDAO nodeOrdersDAO, NodeUserDAO nodeUserDAO) {
}
