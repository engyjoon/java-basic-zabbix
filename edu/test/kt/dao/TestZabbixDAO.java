package kt.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import kt.vo.HistoryVO;
import kt.vo.HostVO;
import kt.vo.ItemVO;

public class TestZabbixDAO {

	@Test
	public void testSelectHistoryCount() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		ItemVO itemVO = new ItemVO();
		itemVO.setItemid(23287);
		itemVO.setValueType(ZabbixDAO.INTEGER);
		int result = zabbixDAO.selectHistoryCount(itemVO);
		System.out.println("[testSelectHistoryCount] item count : " + result);
	}

	@Test
	public void testSelectHostList() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		ArrayList<HostVO> list = new ArrayList<>();
		list = zabbixDAO.selectHostList();
		int result = list.size();
		assertTrue(result > 0);
		System.out.println("host count : " + result);
	}
	
	@Test
	public void testSelectHostidByHost() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		long result = zabbixDAO.selectHostidByHost("Zabbix server");
		assertFalse(result == 0);
		System.out.println("hostid : " + result);
	}
	
	@Test
	public void testSelectItemListByHostid() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		ArrayList<ItemVO> list = new ArrayList<>();
		list = zabbixDAO.selectItemListByHostid(zabbixDAO.selectHostidByHost("Zabbix server"));
		int result = list.size();
		assertTrue(result > 0);
		System.out.println("item count : " + result);
	}
	
	@Test
	public void testSelectHistoryList() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		ArrayList<HistoryVO> list = new ArrayList<>();
		ItemVO itemVO = new ItemVO();
		itemVO.setItemid(23306);
		itemVO.setValueType(ZabbixDAO.FLOAT);
		list = zabbixDAO.selectHistoryList(itemVO);
		int result = list.size();
		assertTrue(result > 0);
		System.out.println("[testSelectHistoryList] history count : " + result);
	}
	
	@Test
	public void testSelectItemByItemid() {
		ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
		ItemVO itemVO = new ItemVO();
		itemVO = zabbixDAO.selectItemByItemid(28623);
		assertNotNull(itemVO);
		System.out.println("[testSelectItemByItemid] valueType : " + itemVO.getValueType());
	}
}
