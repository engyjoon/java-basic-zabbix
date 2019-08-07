package kt.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;

import kt.dao.ZabbixDAO;
import kt.vo.HistoryVO;
import kt.vo.HostVO;
import kt.vo.ItemVO;

public class ZabbixService {

	private static ZabbixService zabbixService;
	
	private ZabbixService() {}
	
	public static ZabbixService getInstance() {
		if(zabbixService == null) zabbixService = new ZabbixService();
		return zabbixService;
	}
	
	ZabbixDAO zabbixDAO = ZabbixDAO.getInstance();
	
	public ArrayList<HostVO> selectHostList() {
		return zabbixDAO.selectHostList();
	}
	
	public int selectItemCountByHostid(long hostid) {
		return zabbixDAO.selectItemCountByHostid(hostid);
	}
	
	public ArrayList<ItemVO> selectItemListByHostid(long hostid) {
		return zabbixDAO.selectItemListByHostid(hostid);
	}
	
	public int selectHistoryCountByItemid(ItemVO itemVO) {
		return zabbixDAO.selectHistoryCount(itemVO);
	}
	
	public ItemVO selectItemByItemid(long itemid) {
		return zabbixDAO.selectItemByItemid(itemid);
	}
	
	public ArrayList<HistoryVO> selectHistoryList(ItemVO itemVO) {
		return zabbixDAO.selectHistoryList(itemVO);
	}
	
	public boolean exportHistoryList(ItemVO itemVO, File file) {
		ArrayList<HistoryVO> list = new ArrayList<>();
		list = zabbixDAO.selectHistoryList(itemVO);
		
		try {
			FileWriter writer = new FileWriter(file);
			
			StringJoiner sj = new StringJoiner("\n");
			
			for(HistoryVO history : list) {
				sj.add(history.getClock() + "," + history.getValue());
			}
			writer.write(sj.toString());
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
