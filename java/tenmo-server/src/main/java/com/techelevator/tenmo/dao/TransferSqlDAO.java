package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Transfer;

@Service
public class TransferSqlDAO implements TransferDAO{

    private JdbcTemplate jdbcTemplate;

	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public Integer getTransferTypeId(String type) {
		Integer transferType = null;
		String sql = "SELECT transfer_type_id " +
					 "FROM transfer_types " +
					 "WHERE transfer_type_desc = ?";
		transferType = jdbcTemplate.queryForObject(sql, Integer.class, type);
		return transferType;
		
	}

	@Override
	public Integer getTransferStatusId(String status) {
		Integer transferStatus = null;
		String sql = "SELECT transfer_status_id " +
					 "FROM transfer_statuses " +
					 "WHERE transfer_status_desc = ?";
		transferStatus = jdbcTemplate.queryForObject(sql, Integer.class, status);
		return transferStatus;
	}		

	@Override
	public Transfer[] getTransferHistory(String status, int id) {
		List<Transfer> tempTransfer = new ArrayList<Transfer>();
		String sql = "SELECT * FROM transfers t "
				+ " JOIN accounts a ON t.account_from = a.account_id"
				+ " JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id"
				+ " WHERE a.user_id = ?"
				+ " AND ts.transfer_status_desc = ?"
				+ " UNION"
				+ " SELECT * FROM transfers t "
				+ " JOIN accounts a ON t.account_to = a.account_id"
				+ " JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id"
				+ " WHERE a.user_id = ?"
				+ " AND ts.transfer_status_desc = ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, status, id, status);
		while (results.next()) {
			Transfer result = mapRowToTransfer(results);
			tempTransfer.add(result);
		}
		Transfer[] transferHistory = new Transfer[tempTransfer.size()];
		tempTransfer.toArray(transferHistory);
		return transferHistory;
	}

	@Override
	public Transfer[] getPendingTransfers(int id) {
		List<Transfer> tempTransfer = new ArrayList<Transfer>();
		String sql = "SELECT * FROM transfers t "
				+ " JOIN accounts a ON t.account_from = a.account_id"
				+ " JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id"
				+ " WHERE a.user_id = ?"
				+ " AND ts.transfer_status_desc = 'Pending'";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		while (results.next()) {
			Transfer result = mapRowToTransfer(results);
			tempTransfer.add(result);
		}
		Transfer[] transferHistory = new Transfer[tempTransfer.size()];
		tempTransfer.toArray(transferHistory);
		return transferHistory;
	}

	@Override
	public String getAccountHolderName(int id) {
		String username = "";
		String sql = "SELECT username FROM users u"
				+ " JOIN accounts a ON u.user_id = a.user_id"
				+ " WHERE a.account_id = ?";
		username = jdbcTemplate.queryForObject(sql, String.class, id);
		return username;

	}
	
	@Override
	public Integer getAccountHolderIdByName(String name) {
		Integer accountId = 0;
		String sql = "SELECT account_id FROM accounts a "
				+ "JOIN users u ON a.user_id = u.user_id "
				+ "WHERE username = ?";
		
		accountId = jdbcTemplate.queryForObject(sql, Integer.class, name);
		return accountId;
		
		
	}
	
	@Override
	public String getTransferTypeName(int id) {
		String typeName = "";
		String sql = "SELECT transfer_type_desc FROM transfer_types tt"				
					+ " WHERE transfer_type_id = ?";
		typeName = jdbcTemplate.queryForObject(sql, String.class, id);
		return typeName;

	}
	
	@Override
	public String getTransferStatusName(int id) {
		String statusName = "";
		String sql = "SELECT transfer_status_desc FROM transfer_statuses ts"				
					+ " WHERE transfer_status_id = ?";
		statusName = jdbcTemplate.queryForObject(sql, String.class, id);
		return statusName;

	}
	
	@Override
	public Transfer getTransferDetailsById(int id) {
		Transfer details = null;
		String sql = "SELECT * FROM transfers t"
				+ " WHERE transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		while(results.next()) {
			details = mapRowToTransfer(results);
		}
		return details;
	}
	@Override
	public boolean sendBucks(int fromId, int toId, Transfer transfer) {

		String sql = "BEGIN; " +
					 "INSERT INTO transfers " +
					 "(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
					 "VALUES (DEFAULT, ?, ?, ?, ?, ?); " +
					 "UPDATE accounts " +
					 "SET balance = balance - ? " +
					 "WHERE user_id = ?; " +
					 "UPDATE accounts " +
					 "SET balance = balance + ? " +
					 "WHERE user_id = ?; " +
					 "COMMIT";
		
		return jdbcTemplate
					.update(sql,
						transfer.getTransferTypeId(),
						transfer.getTransferStatusId(),
						transfer.getAccountFrom(),
						transfer.getAccountTo(),
						transfer.getAmount(),
						transfer.getAmount(),
						fromId,
						transfer.getAmount(),
						toId) == 3;
	}
	@Override
	public boolean requestBucks(int fromId, int toId, Transfer transfer) {

		String sql = "INSERT INTO transfers " +
					 "(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
					 "VALUES (DEFAULT, ?, ?, ?, ?, ?)";
		
		return jdbcTemplate
					.update(sql,
						transfer.getTransferTypeId(),
						transfer.getTransferStatusId(),
						transfer.getAccountFrom(),
						transfer.getAccountTo(),
						transfer.getAmount()
						) == 1;
	}
	
	@Override
	public boolean transferUpdate(int id, Transfer transfer) {
		if(transfer.getTransferStatusId() == getTransferStatusId("Approved")) {
			String sql = "BEGIN;"
					+ " UPDATE transfers t SET transfer_status_id = ?"
					+ " WHERE transfer_id = ?;"
					+ " UPDATE accounts " +
					 "SET balance = balance - ? " +
					 "WHERE account_id = ?; " +
					 "UPDATE accounts " +
					 "SET balance = balance + ? " +
					 "WHERE account_id = ?; " +
					 "COMMIT";
			
			return jdbcTemplate.update(sql, transfer.getTransferStatusId(), 
					id, transfer.getAmount(), transfer.getAccountFrom(), 
					transfer.getAmount(), transfer.getAccountTo()) == 3;
		}
		
		String sql = "UPDATE transfers t SET transfer_status_id = ?"
				+ " WHERE transfer_id = ?";
		
		return jdbcTemplate.update(sql, transfer.getTransferStatusId(), id) == 1;
		
	}
	
	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer tr = new Transfer();
		tr.setTransferId(rs.getInt("transfer_id"));
		tr.setTransferTypeId(rs.getInt("transfer_type_id"));
		tr.setTransferStatusId(rs.getInt("transfer_status_id"));
		tr.setAccountFrom(rs.getInt("account_from"));
		tr.setAccountTo(rs.getInt("account_to"));
		tr.setAmount(rs.getBigDecimal("amount"));
		
		return tr;
	}
}
