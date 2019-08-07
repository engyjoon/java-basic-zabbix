package kt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import kt.common.JdbcUtil;
import kt.vo.HistoryVO;
import kt.vo.HostVO;
import kt.vo.ItemVO;

public class ZabbixDAO {
	
	public static final int INTEGER = 3;
	public static final int FLOAT = 0;
	public static final int CHARACTER = 1;

	private static ZabbixDAO zabbixDAO;
	
	private ZabbixDAO() {}
	
	public static ZabbixDAO getInstance() {
		if(zabbixDAO == null) zabbixDAO = new ZabbixDAO();
		return zabbixDAO;
	}
	
	public int selectHistoryCount(ItemVO itemVO) {
		StringBuffer query = new StringBuffer();
		query.append("select count(*) ");
		query.append("from " + getHistoryTable(itemVO.getValueType()) + " ");
		query.append("where itemid = ?");
		query.append("  and clock > extract(epoch from date_trunc('second', now() - interval '1 hour'))");
		
		int result = 0;
		
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setLong(1, itemVO.getItemid());
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				if(rs.next()) result = rs.getInt(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ArrayList<HostVO> selectHostList() {
		StringBuffer query = new StringBuffer();
		query.append("select hostid, host, name, status from hosts where available = '1'");
		
		ArrayList<HostVO> result = new ArrayList<>();
			
		try(Connection conn = JdbcUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query.toString());
				ResultSet rs = pstmt.executeQuery()) {
			while(rs.next()) {
				HostVO hostVO = new HostVO();
				hostVO.setHostid(rs.getLong("HOSTID"));
				hostVO.setHost(rs.getString("HOST"));
				hostVO.setName(rs.getString("NAME"));
				hostVO.setStatus(rs.getInt("STATUS"));
				result.add(hostVO);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public long selectHostidByHost(String host) {
		StringBuffer query = new StringBuffer();
		query.append("select hostid from hosts where host = ?");
		
		long result = 0;
		
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, host);
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				if(rs.next()) result = rs.getLong(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ArrayList<ItemVO> selectItemListByHostid(long hostid) {
		StringBuffer query = new StringBuffer();
		query.append("select itemid, type, hostid, name, key_, status, value_type from items ");
		query.append("where flags not in('1','2') and hostid = ?");
		
		ArrayList<ItemVO> result = new ArrayList<>();		

		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setLong(1, hostid);
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				while(rs.next()) {
					ItemVO itemVO = new ItemVO();
					itemVO.setItemid(rs.getLong("ITEMID"));
					itemVO.setType(rs.getInt("TYPE"));
					itemVO.setHostid(rs.getLong("HOSTID"));
					itemVO.setName(rs.getString("NAME"));
					itemVO.setKey(rs.getString("KEY_"));
					itemVO.setStatus(rs.getInt("STATUS"));
					itemVO.setValueType(rs.getInt("VALUE_TYPE"));
					result.add(itemVO);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int selectItemCountByHostid(long hostid) {
		StringBuffer query = new StringBuffer();
		query.append("select count(*) from items where flags not in('1','2') and hostid = ?");
		
		int result = 0;
		
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setLong(1, hostid);
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				if(rs.next()) result = rs.getInt(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ArrayList<HistoryVO> selectHistoryList(ItemVO itemVO) {
		StringBuffer query = new StringBuffer();
		query.append("select t2.clock, t2.value ");
		query.append("from items t1, " + getHistoryTable(itemVO.getValueType()) + " t2 ");
		query.append("where t1.itemid = t2.itemid ");
		query.append("  and t2.clock > extract(epoch from date_trunc('second', now() - interval '1 hour')) ");
		query.append("  and t1.itemid = ? ");
		query.append("order by t2.clock");
		
		ArrayList<HistoryVO> result = new ArrayList<>();
		
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setLong(1, itemVO.getItemid());
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				while(rs.next()) {
					HistoryVO historyVO = new HistoryVO();
					
					Instant instant = Instant.ofEpochSecond(rs.getInt("CLOCK"));
					LocalDateTime zdt = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
					
					historyVO.setClock(zdt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
					historyVO.setValue(getHistoryValue(rs, itemVO.getValueType()).toString());
					
					result.add(historyVO);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ItemVO selectItemByItemid(long itemid) {
		StringBuffer query = new StringBuffer();
		query.append("select itemid, type, hostid, name, key_, status, value_type from items ");
		query.append("where flags not in('1','2') and itemid = ?");
		
		ItemVO result = new ItemVO();
		
		try {
			Connection conn = JdbcUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			pstmt.setLong(1, itemid);
			ResultSet rs = pstmt.executeQuery();
			
			try(conn; pstmt; rs) {
				if(rs.next()) {
					result.setItemid(rs.getLong("ITEMID"));
					result.setType(rs.getInt("TYPE"));
					result.setHostid(rs.getLong("HOSTID"));
					result.setName(rs.getString("NAME"));
					result.setKey(rs.getString("KEY_"));
					result.setStatus(rs.getInt("STATUS"));
					result.setValueType(rs.getInt("VALUE_TYPE"));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String getHistoryTable(int valueType) {
		String result = null;
		
		if(valueType == INTEGER) result = "history_uint";
		else if(valueType == FLOAT) result = "history";
		else if(valueType == CHARACTER) result = "history_str";
		
		return result;
	}
	
	private Object getHistoryValue(ResultSet rs, int valueType) throws SQLException {
		Object result = null;
		
		if(valueType == INTEGER) result = rs.getInt("VALUE");
		else if(valueType == FLOAT) result = rs.getDouble("VALUE");
		else if(valueType == CHARACTER) result = rs.getString("VALUE");
		
		return result;
	}
}
