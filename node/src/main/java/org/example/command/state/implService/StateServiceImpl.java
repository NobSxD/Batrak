package org.example.command.state.implService;

import org.example.command.state.State;
import org.example.command.state.StateService;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StateServiceImpl implements StateService {

	private final Map<UserState, State> stateMap;

	public StateServiceImpl(List<State> states) {
		this.stateMap = states.stream().collect(Collectors.toMap(State::getType, Function.identity()));
	}
	@Override
	public String send(NodeUser nodeUser, String text){
		State state = stateMap.get(nodeUser.getState());
		return state.send(nodeUser, text);
	}

	@Override
	public Account getAccount(Account account, UserState userState) {
		State state = stateMap.get(userState);
		return null;
	}

}
