package org.example.command.change.impl;

import org.example.command.change.Change;
import org.example.command.change.ChangeServiceNode;
import org.example.entity.enums.ChangeEnums;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChangeService implements ChangeServiceNode {
	private final Map<ChangeEnums, Change> changeMap;

	public ChangeService(List<Change> changeMap) {
		this.changeMap = changeMap.stream().collect(Collectors.toMap(Change ::getType, Function.identity()));
	}
	public Change change(ChangeEnums type){
		return changeMap.get(type);
	}
}
