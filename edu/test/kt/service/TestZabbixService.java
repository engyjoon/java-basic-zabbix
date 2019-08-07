package kt.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import kt.vo.HostVO;
import kt.vo.ItemVO;

public class TestZabbixService {

	@Test
	public void testSelectHostList() {
		ZabbixService service = ZabbixService.getInstance();
		ArrayList<HostVO> list = new ArrayList<>();
		list = service.selectHostList();
		int result = list.size();
		assertTrue(result > 0);
		System.out.println("host count : " + result);
	}
	
	@Test
	public void testSelectItemListByHostid() {
		ZabbixService service = ZabbixService.getInstance();
		ArrayList<ItemVO> list = new ArrayList<>();
		list = service.selectItemListByHostid(10084);
		int result = list.size();
		assertTrue(result > 0);
		System.out.println("item count : " + result);
	}
}
