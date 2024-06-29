//package org.example.command.menu2;
//
//
//import org.example.change.ChangeServiceNode;
//import org.example.change.impl.ChangeBinanceImpl;
//import org.example.dao.AccountBinanceDAO;
//import org.example.dao.NodeUserDAO;
//import org.example.entity.NodeUser;
//import org.example.entity.Account;
//import org.example.entity.account.AccountBinance;
//import org.example.entity.enams.UserState;
//import org.example.processServiceCommand.ProcessServiceCommand;
//import org.example.service.ProducerService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//
//@ExtendWith(MockitoExtension.class)
//
//class RegisterAccountImplTest {
//	private Account account;
//	private PasswordEncoder passwordEncoder;
//
//	@Mock
//	private NodeUserDAO nodeUserDAO;
//	@Mock
//	private ProcessServiceCommand processServiceCommand;
//	@Mock
//	private ProducerService producerService;
//	@Mock
//	private ChangeServiceNode changeServiceNode;
//	@Mock
//	private AccountBinanceDAO accountBinanceDAO;
//	@Mock
//	private NodeUser nodeUser;
//
//
//
//
//	@BeforeEach
//	void setUp() {
//
//
//		 nodeUser = new NodeUser();
//		 passwordEncoder = new BCryptPasswordEncoder(8);
//		 account = new AccountBinance();
//		 account.setNameChange("test");
//		 account.setPublicApiKey("testPublic");
//		 account.setSecretApiKey("testSecret");
//	}
//
//
//	@Test
//	void nameChangeSend() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl();
//		nodeUser.setAccount(account);
//		String send = registerAccount.send(nodeUser, "Имя аккаунта");
//		Assertions.assertEquals(send, "Ведите публичный ключ");
//		Assertions.assertEquals(nodeUser.getAccount().getNameChange(), "Имя аккаунта");
//
//	}
//
//	@Test
//	void publicApiKeyChangeSend() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//		nodeUser.setAccount(account);
//		String send = registerAccount.send(nodeUser, "Это публичный ключ");
//		Assertions.assertEquals(send, "ведите секретный ключ");
//		Assertions.assertNotNull(nodeUser.getAccount().getPublicApiKey());
//
//	}
//
//	@Test
//	void secretApiKeyChangeSend() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//		nodeUser.setAccount(account);
//		String send = registerAccount.send(nodeUser, "Это секретный ключ");
//		Assertions.assertEquals(send, "");
//		Assertions.assertNotNull(nodeUser.getAccount().getSecretApiKey());
//
//	}
//
//	@Test
//	void nullMenuStateChangeSend() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//
//		String send = registerAccount.send(nodeUser, "Имя аккаунта");
//		Assertions.assertEquals(send, "Ведите уникальное имя аккаунта");
//		Assertions.assertNull(nodeUser.getAccount());
//
//	}
//
//	@Test
//	void errorChangeSend() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//		String send = registerAccount.send(nodeUser, "Имя аккаунта");
//		Assertions.assertEquals(send, "Регистрация не успешна");
//		Assertions.assertNull(nodeUser.getAccount());
//
//	}
//
//	@Test
//	void accountName() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//
//		nodeUser.setAccount(account);
//
//		Assertions.assertEquals(s, "Ведите уникальное имя аккаунта");
//	}
//
//	@Test
//	void testAccountName() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//		nodeUser.setAccount(account);
//
//		Assertions.assertEquals(s, "Ведите публичный ключ");
//	}
//
//	@Test
//	void publicApi() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand , processServiceChangeCommands);
//		nodeUser.setAccount(account);
//		String s = registerAccount.publicApi(nodeUser, "binance");
//
//		Assertions.assertEquals(s, "ведите секретный ключ");
//	}
//
//	@Test
//	void secretApi() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand);
//		nodeUser.setAccount(account);
//
//
//		Assertions.assertEquals(nodeUser.getState(), UserState.ACCOUNT_USER);
//		Assertions.assertEquals(s, "");
//	}
//
//	@Test
//	void getType() {
//		RegisterAccountImpl registerAccount = new RegisterAccountImpl(nodeUserDAO, passwordEncoder, processServiceCommand );
//		Assertions.assertEquals(registerAccount.getType(), UserState.REGISTER_ACCOUNT);
//	}
//
//	@Test
//	void getAccount() {
//	}
//
//
//
//
//
//}