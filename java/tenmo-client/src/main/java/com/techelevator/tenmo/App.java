package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

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
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View Transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	//private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, 
														MAIN_MENU_OPTION_SEND_BUCKS,
														MAIN_MENU_OPTION_REQUEST_BUCKS,
														MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS,
														MAIN_MENU_OPTION_LOGIN,
														MENU_OPTION_EXIT };
	private static final String TRANSFER_MENU_OPTION_VIEW_PAST_TRANSFERS = "View Past Tranfers";
	private static final String TRANSFER_MENU_OPTION_VIEW_PENDING_TRANSFERS = "View Pending Transfers";
	private static final String TRANSFER_MENU_OPTION_VIEW_REJECTED_TRANSFERS = "View Rejected Transfers";

	
	private static final String[] TRANSFER_MENU_OPTIONS = { TRANSFER_MENU_OPTION_VIEW_PAST_TRANSFERS,
															TRANSFER_MENU_OPTION_VIEW_PENDING_TRANSFERS,
															TRANSFER_MENU_OPTION_VIEW_REJECTED_TRANSFERS };

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private UserService userService;
    private AuthenticationService authenticationService;
    private TransferService transferService;
    private int currentUserId;
	private	int approvedId = 0;
	private	int rejectedId = 0;

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
		try {
			approvedId = transferService.getTransferStatusId("Approved");
			rejectedId = transferService.getTransferStatusId("Rejected");
		} catch (TransferServiceException e) {
			System.out.println("Connectivity Issues.");
		}

		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			try {
				String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
				if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
					viewCurrentBalance();
				} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
					String transferChoice = (String)console.getChoiceFromOptions(TRANSFER_MENU_OPTIONS);
					if (TRANSFER_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(transferChoice)) {
						viewTransferHistory("Approved");
					} else if(TRANSFER_MENU_OPTION_VIEW_PENDING_TRANSFERS.equals(transferChoice)) {
						viewTransferHistory("Pending");
					} else if(TRANSFER_MENU_OPTION_VIEW_REJECTED_TRANSFERS.equals(transferChoice)) {
						viewTransferHistory("Rejected");
					}
					// Deprecated option
				} /*else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
					//viewTransferHistory();
					 
				}*/ else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
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
						.format("Current Balance: $ %.02f TE Bucks", 
								userService.getBalance(currentUserId)));
		}catch (UserServiceException ex) {
			System.out.println("User Service Exception");
		}
	}

	private void viewTransferHistory(String statusName) {
		//Rest call to transfer and user which
		//should return list of transfers based on userID
		String banner = "";
		String header = "";
		String approvedHeader = "ID    Sent From     Received By    Amount";
		String pendingHeader = "ID    Sent By     Recipient    Amount Requested";
		System.out.println("--------------------------------------------------------------------------------");
		switch (statusName) {
			case "Approved" :
				banner = "Transfer History";
				header = approvedHeader;
				break;
			case "Pending" :
				banner = "Pending transfers";
				header = pendingHeader;
				break;
			case "Rejected" :
				banner = "Rejected transfers";
				header = approvedHeader;
				break;
			default :
				System.out.println("Something went wrong!");
		}
		System.out.println(banner + " for " + currentUser.getUser().getUsername() + "\n");
		System.out.println(header);
		System.out.println("--------------------------------------------------------------------------------");
		String[] transfers = null;
		try {
			transfers = transferService
					.getTransferHistoryById(transferService.getTransferStatusId(statusName), currentUserId);
		} catch (Exception e) {

		} 
		console.displaySimpleMenu(transfers);

		switch (statusName) {
			case "Approved" :
			case "Rejected" :
				viewTransferDetails(transfers);
				break;
			case "Pending" :
				approveOrDenyRequest(transfers);
				break;
			default :
				System.out.println("Something went wrong");
				break;
		}
	}
	// User selects id of transfer to view details. 0 to cancel
	public void viewTransferDetails(String[] transfers) {
		boolean validSelection = false;
		Transfer transferSelection = null;
		while(!validSelection) {
			int transferId = (int)console.getUserInputInteger("\nPlease enter transfer ID to view details (0 to cancel): ");
			if(transferId == 0) {
				validSelection = true;
				continue;
			} else {
				for(String str : transfers) {
					if(str.startsWith(String.valueOf(transferId))){
						try {
							transferSelection = transferService
										.getTransferDetailsById(transferId);
							printTransferDetails(transferSelection);
						} catch (TransferServiceException e) {
							e.printStackTrace();
						}
						validSelection = true;
						break;
					}
				} 
				if(validSelection) {
					continue;
				}
				System.out.println("Invalid transfer ID");
			}
		}
		
	}
	
	private void printTransferDetails(Transfer tr) {
		String transferString = "";
		System.out.println("------------------------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("------------------------------------------------------------");
//		String[] details = new String[]{"Id: ", "From: ", "To: ", "Type: ", "Status: ", "Amount: $ "};
//		String id = "";
//		String from = "";
//		String to = "";
//		String type = "";
//		String status = "";
//		String amount = "";
		try {
//			id = Integer.toString(tr.getTransferId());
//			from = transferService.getAccountHolderName(tr.getAccountFrom());
//			to = transferService.getAccountHolderName(tr.getAccountTo());
//			type = transferService.getTransferTypeName(tr.getTransferTypeId());
//			status = transferService.getTransferStatusName(tr.getTransferStatusId());
//			amount = tr.getAmount().toString();
			transferString = String.format("Id:%17d\nFrom:%15s\nTo:%17s\nType:%15s\nStatus:%13s\nAmount:   $%.02fTEB",
							tr.getTransferId(),
							transferService.getAccountHolderName(tr.getAccountFrom()),
							transferService.getAccountHolderName(tr.getAccountTo()),
							transferService.getTransferTypeName(tr.getTransferTypeId()),
							transferService.getTransferStatusName(tr.getTransferStatusId()),
							tr.getAmount());
		} catch (TransferServiceException e) {
			e.printStackTrace();
		}
//		String[] category = new String[] {id, from, to, type, status, amount};
//		String[] fullTransferDetails = new String[details.length];
//		for(int i = 0; i<details.length; i++) {
//			fullTransferDetails[i] = details[i] + category[i];
//		}
//		int maxLength = 0;
//		for(int i = 0; i<details.length; i++) {
//			if (fullTransferDetails[i].length() > maxLength) {
//				maxLength = fullTransferDetails[i].length();
//			}
//		}
//		maxLength += 2;
//		
//		String formatter = "%-" + maxLength + "." + maxLength + "s";
		
		int spaces = Math.abs(transferString.length() - transferString.indexOf("Amount") - 20);
		String spacer = "";
		if (spaces > 0) {
			for (int i = 0; i<spaces; i++) {
				spacer += " ";
			}
		}
		transferString = transferString.substring(0, transferString.indexOf("$")) + spacer +
				transferString.substring(transferString.indexOf("$"));

		System.out.println(transferString);
		
	}

	private void approveOrDenyRequest(String[] pendingTransfers) {
		boolean validSelection = false;
		Transfer transferSelection = null;
		String approveOrDeny = "";

		// User selects id of transfer to view details. 0 to cancel
		while(!validSelection) {
			int transferId = (int)console.getUserInputInteger("\nPlease enter transfer ID to Approve/Deny request (0 to cancel): ");
			
			if(transferId == 0) {
				validSelection = true;
				continue;
			}else {
				for(String str : pendingTransfers) {
					if(str.startsWith(String.valueOf(transferId))){
						// TODO :: Change to Approve/Deny request
						boolean aOrbSelection = false;
						while (!aOrbSelection) {
							approveOrDeny = console.getUserInput("You have selected transfer #" +
								transferId + ". Type 'a' to approve, 'd' to deny. (0 to cancel)");
							if (approveOrDeny.equalsIgnoreCase("a") ||
								approveOrDeny.equalsIgnoreCase("d")) {
								aOrbSelection = true;
								continue;
							}
							else if (approveOrDeny.equalsIgnoreCase("0")) {
								mainMenu();
							}
							else {
								System.out.println("Invalid selection.");
							}
						} 
						List<String> pendingReq = new ArrayList<String>(Arrays.asList(str.split(" ")));
						pendingReq.get(3).replace("$", "");
						pendingReq.removeAll(Collections.singleton(null));
						pendingReq.removeAll(Collections.singleton(""));
						if(pendingReq.get(1).equalsIgnoreCase(currentUser.getUser().getUsername())) {
							System.out.println("You can not approve or deny a request that you sent.");
							break;
						}else {
							Transfer transfer = new Transfer();
							transfer.setTransferId(transferId);
							try {
								if (approveOrDeny.equalsIgnoreCase("a")) {
									transfer.setTransferStatusId(approvedId);
								}else {
									transfer.setTransferStatusId(rejectedId);
								}
								transfer.setAccountFrom(transferService.getAccountHolderIdByName(pendingReq.get(1)));
								transfer.setAccountFrom(transferService.getAccountHolderIdByName(pendingReq.get(2)));
								transfer.setAmount(new BigDecimal (pendingReq.get(4)));
								boolean hasSent;
								hasSent = transferService.transferUpdate(transferId, transfer);
							} catch (TransferServiceException e) {
								e.printStackTrace();
							}
						}
						if (approveOrDeny.equalsIgnoreCase("a")) {
							System.out.println("Transaction successfully approved!");
						} else {
							System.out.println("Transaction successfully denied.");
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
						amount = new BigDecimal(console.getUserInput("Enter An Amount (0 to cancel):\n "));
					} catch (NumberFormatException e) {
						System.out.println("Please enter a numerical value");
						continue;
					}
					isValidAmount = amount.doubleValue() < userService
															.getBalance(currentUserId)
															.doubleValue();
				if(!isValidAmount) {
					System.out.println("You can not send more money than you have available!");
				}
				if(amount.doubleValue() < 0.99 && amount.doubleValue() > 0.00) {
					System.out.println("You must send at least $0.99 TEB");
					isValidAmount = false;
				} else if (amount.doubleValue() == 0.00) {
					mainMenu();
				}
			} while(!isValidAmount);

			// Create transfer object
			boolean confirmedAmount = false;
			String confirm = "";
			Transfer transfer = null;
			while (!confirmedAmount) {
				confirm = console.getUserInput("You entered: " + amount.toPlainString() + " TEB. Is this correct? (y/n)");
				if (confirm.toLowerCase().startsWith("y")) {
					// transferService to POST to server db
					transfer = createTransferObj("Send", "Approved", currentUserId, toUser.getId(), amount);
					boolean hasSent = transferService.sendBucks(currentUserId, toUser.getId(), transfer);
					if (hasSent) {
						// TODO :: Test this
						System.out.println("The code executed");
					}
					confirmedAmount = true;
					continue;

				} else {
					System.out.println("Send canceled.");
					confirmedAmount = true;
					continue;
				} 
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
					amount = new BigDecimal(console.getUserInput("Enter An Amount (0 to cancel):\n "));
					isValidAmount = true;
				} catch (NumberFormatException e) {
					System.out.println("Please enter a numerical value");
					continue;
				}
				if(amount.doubleValue() < 0.99 && amount.doubleValue() > 0.00) {
					System.out.println("You cannot request less than $0.99 TEB");
					isValidAmount = false;
					continue;
				} else if (amount.doubleValue() == 0.00) {
					mainMenu();
				}
			} while(!isValidAmount);

			// Create transfer object
			boolean confirmedAmount = false;
			String confirm = "";
			Transfer transfer = null;
			while (!confirmedAmount) {
				confirm = console.getUserInput("You entered: " + amount.toPlainString() + " TEB. Is this correct? (y/n)");
				if (confirm.toLowerCase().startsWith("y")) {
					// transferService to POST to server db
					System.out.println("You are requesting: " + amount.toPlainString() +
							" TEB from " + fromUser.getUsername());
					transfer = createTransferObj("Request", "Pending",
									currentUserId, fromUser.getId(), amount);
					boolean hasSent = 
							transferService.sendBucks(currentUserId, fromUser.getId(), transfer);
					if (hasSent) {
						// TODO :: Test this
						System.out.println("The code executed");
					}
					confirmedAmount = true;
					continue;

				} else {
					System.out.println("Request canceled.");
					confirmedAmount = true;
					continue;
				} 	
			}
			
		}catch(UserServiceException ex) {
			System.out.println("User Service Exception");
		}catch(TransferServiceException ex) {
			System.out.println("Transfer Service Exception");
		}
		
	}
	private Transfer createTransferObj(String type, String status, int fromUser, int toUser, BigDecimal amount) throws TransferServiceException {
		Transfer tr = new Transfer();
		try {
			tr.setTransferTypeId(transferService.getTransferTypeId(type));
			tr.setTransferStatusId(transferService.getTransferStatusId(status));
			tr.setAccountFrom(fromUser);
			tr.setAccountTo(toUser);
			tr.setAmount(amount);
		} catch (TransferServiceException tse) {
			System.out.println(tse.getLocalizedMessage());
		}
		return tr;
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
