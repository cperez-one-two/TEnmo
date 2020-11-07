package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.web.client.ResourceAccessException;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.TransferServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.tenmo.services.UserServiceException;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER,
														 LOGIN_MENU_OPTION_LOGIN,
														 MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, 
														MAIN_MENU_OPTION_SEND_BUCKS,
														MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS,
														MAIN_MENU_OPTION_REQUEST_BUCKS,
														MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS,
														MAIN_MENU_OPTION_LOGIN,
														MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private UserService userService;
    private AuthenticationService authenticationService;
    private TransferService transferService;
    private int currentUserId;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out),
    			  new AuthenticationService(API_BASE_URL),
    			  new UserService(API_BASE_URL),
    			  new TransferService(API_BASE_URL));
    	app.run();
    }

    public App( ConsoleService console,
				AuthenticationService authenticationService,
				UserService userService,
				TransferService transferService
			  ) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.userService = userService;
		this.transferService = transferService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		UserService.AUTH_TOKEN = currentUser.getToken();
		currentUserId = currentUser.getUser().getId();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			try {
				String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
				if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
					viewCurrentBalance();
				} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
					viewTransferHistory();
				} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
					viewPendingRequests();
				} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
					sendBucks();
				} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
					requestBucks();
				} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
					login();
				} else {
					// the only other option on the main menu is to exit
					exitProgram();
				}
			} catch (ResourceAccessException ex) {
				System.out.println("Resource Access Exception");
			}  
		}
	}

	private void viewCurrentBalance() {
		try {
		System.out.println(String
						.format("Current Balance: %.02f TE Bucks", 
								userService.getBalance(currentUserId)));
		}catch (UserServiceException ex) {
			System.out.println("User Service Exception");
		}
	}

	// TODO :: Fix the From/To portion
	private void viewTransferHistory() {
		//Rest call to transfer and user which
		//should return list of transfers based on userID
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("Transfer History for " + currentUser.getUser().getUsername() + "\n");
		System.out.println("ID    From/To        Amount");
		System.out.println("--------------------------------------------------------------------------------");
		String[] transferHistory = null;
		try {
			transferHistory = transferService
					.getTransferHistoryById(currentUserId);
		} catch (Exception e) {

		} 
		console.displaySimpleMenu(transferHistory);
		boolean validSelection = false;
		Transfer transferSelection = null;

		// User selects id of transfer to view details. 0 to cancel
		while(!validSelection) {
			int transferId = (int)console.getUserInputInteger("\nPlease enter transfer ID to view details (0 to cancel): ");
			
			if(transferId == 0) {
				validSelection = true;
				continue;
			}else {
				for(String str : transferHistory) {
					if(str.startsWith(String.valueOf(transferId))){
						try {
							transferSelection = transferService.getTransferDetailsById(transferId);
							viewTransferDetails(transferSelection);
							
						} catch (TransferServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						validSelection = true;
						break;
					}
				} 
				if(validSelection) {
					continue;
				}
				System.out.println("Invalid transferID");
			}
		}
		
	}
	
	private void viewTransferDetails(Transfer tr) {
		String transferString = "";
		System.out.println("------------------------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("------------------------------------------------------------");
		try {
			transferString = String.format("Id: %d\nFrom: %s\nTo: %s\nType: %s\nStatus: %s\nAmount: $%.02f TEB ",
							tr.getTransferId(),
							transferService.getAccountHolderName(tr.getAccountFrom()),
							transferService.getAccountHolderName(tr.getAccountTo()),
							transferService.getTransferTypeName(tr.getTransferTypeId()),
							transferService.getTransferStatusName(tr.getTransferStatusId()),
							tr.getAmount());
			
		} catch (TransferServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(transferString);
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		
		// Select a user to send money to
		try {
			System.out.println("Please choose a user to send TE Bucks to:");
			User toUser;
			boolean isValidUser;
			do {
				toUser = (User)console.getChoiceFromOptions(userService.getUsers());
				isValidUser = ( toUser
								.getUsername()
								.equalsIgnoreCase(currentUser.getUser().getUsername())
							  ) ? false : true;

				if(!isValidUser) {
					System.out.println("You can not send money to yourself");
				}
			} while (!isValidUser);
			
			// Select an amount
			BigDecimal amount = new BigDecimal("0.00");
			boolean isValidAmount = false;
			do {
					try {
						amount = new BigDecimal(console.getUserInput("Enter An Amount:\n "));
					} catch (NumberFormatException e) {
						System.out.println("Please enter a numerical value");
						continue;
					}
					isValidAmount = 
							amount.doubleValue() < userService
													.getBalance(currentUserId)
													.doubleValue();
				if(!isValidAmount) {
					System.out.println("You can not send more money than you have available");
				}
			}
			while(!isValidAmount);

			// Create transfer object
			System.out.println("You entered: " + amount.toPlainString() + " TEB");
			
			// transferService to POST to server db
			Transfer transfer = new Transfer();
			transfer.setTransferTypeId(transferService.getTransferTypeId("Send"));
			transfer.setTransferStatusId(transferService.getTransferStatusId("Approved"));
			transfer.setAccountFrom(currentUser.getUser().getId());
			transfer.setAccountTo(toUser.getId());
			transfer.setAmount(amount);
			
			boolean hasSent = transferService.sendBucks(currentUserId, toUser.getId(), transfer);
			if (hasSent) {
				System.out.println("The code executed");
			}
		}catch(UserServiceException ex) {
			System.out.println("User Service Exception");
		}catch(TransferServiceException ex) {
			System.out.println("Transfer Service Exception");
		}
		
	}

	private void requestBucks() {
		// Select a user to send money to
		try {
			System.out.println("Please choose a user to request TE Bucks from:");
			User fromUser;
			boolean isValidUser;
			do {
				fromUser = (User)console.getChoiceFromOptions(userService.getUsers());
				isValidUser = ( fromUser
								.getUsername()
								.equalsIgnoreCase(currentUser.getUser().getUsername())
							  ) ? false : true;

				if(!isValidUser) {
					System.out.println("You can not request money from yourself");
				}
			} while (!isValidUser);
			
			// Select an amount
			BigDecimal amount = new BigDecimal("0.00");
			boolean isValidAmount = false;
			do {
					try {
						amount = new BigDecimal(console.getUserInput("Enter An Amount:\n "));
						isValidAmount = true;
					} catch (NumberFormatException e) {
						System.out.println("Please enter a numerical value");
						continue;
					}
//					isValidAmount = 
//							amount.doubleValue() < userService
//													.getBalance(currentUserId)
//													.doubleValue();
//				if(!isValidAmount) {
//					System.out.println("You can not send more money than you have available");
//				}
			} while(!isValidAmount);

			// Create transfer object
			System.out.println("You are requesting: " + amount.toPlainString() +
								" TEB from " + fromUser.getUsername());
			
			// transferService to POST to server db
			Transfer transfer = new Transfer();
			transfer.setTransferTypeId(transferService.getTransferTypeId("Request"));
			transfer.setTransferStatusId(transferService.getTransferStatusId("Pending"));
			transfer.setAccountFrom(currentUserId);
			transfer.setAccountTo(fromUser.getId());
			transfer.setAmount(amount);
			
			boolean hasSent = transferService.sendBucks(currentUserId, fromUser.getId(), transfer);
			if (hasSent) {
				System.out.println("TE Bucks successfully requested");
			}
		}catch(UserServiceException ex) {
			System.out.println("User Service Exception");
		}catch(TransferServiceException ex) {
			System.out.println("Transfer Service Exception");
		}
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
