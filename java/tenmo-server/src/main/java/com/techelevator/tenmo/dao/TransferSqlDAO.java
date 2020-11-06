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
	public Transfer[] getTransferHistory(int id) {
		List<Transfer> tempTransfer = new ArrayList<Transfer>();
		String sql = "SELECT * FROM transfers t "
				+ " JOIN accounts a ON t.account_from = a.account_id"
				+ " WHERE a.user_id = ?";

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
	public boolean sendBucks(Transfer transfer) {

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
						transfer.getAccountFrom(),
						transfer.getAmount(),
						transfer.getAccountTo()) == 1;
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
