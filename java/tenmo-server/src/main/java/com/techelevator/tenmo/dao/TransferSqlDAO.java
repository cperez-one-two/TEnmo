package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.controller.TransferDAO;
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
}
