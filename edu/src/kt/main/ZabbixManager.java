package kt.main;

import java.util.ArrayList;
import java.util.Scanner;

import kt.service.ZabbixService;
import kt.vo.HistoryVO;
import kt.vo.HostVO;
import kt.vo.ItemVO;

public class ZabbixManager {
	
	ArrayList<HostVO> listHostVO;
	ArrayList<ItemVO> listItemVO;
	ArrayList<HistoryVO> listHistoryVO;
	
	Scanner scanner;
	
	ZabbixService zabbixService = ZabbixService.getInstance();
	
	private static final String SAVE_DIRECTORY = "/Users/engyjoon/temp";
	//private static final String SAVE_DIRECTORY = "c:\\temp";
	
	public ZabbixManager() {
		scanner = new Scanner(System.in);
	}
	
	public static void main(String[] args) {
		ZabbixManager zabbixManager = new ZabbixManager();
		
		printTitle();
		
		boolean isLoop = true;
		while(isLoop) {
			printMenu();
			isLoop = zabbixManager.action(zabbixManager.scanner.nextLine());
			if(!isLoop) exitProgram(zabbixManager.scanner);
		}
	}
	
	private static void printTitle() {
		System.out.println("--------------------------------------------------");
		System.out.println("                  Zabbix Manager                  ");
		System.out.println("--------------------------------------------------");
	}
	
	private static void printMenu() {
		System.out.println("[Menu]");
		System.out.println("1) Show host");
		System.out.println("2) Show item");
		System.out.println("3) Show history");
		System.out.println("4) Export history");
		System.out.println("q) Exit");
		System.out.print("> ");
	}
	
	private boolean action(String input) {
		boolean result = true;
		
		switch(input) {
			case "1": showHost(); System.out.println();
				break;
			case "2": showItem(); System.out.println();
				break;
			case "3": showHistory(); System.out.println();
				break;
			case "4": exportHistory(); System.out.println();
				break;
			case "q": result = false; System.out.println();
				break;
			default : System.out.println();
		}
		
		return result;
	}
	
	private static void exitProgram(Scanner scanner) {
		System.out.println("Exit Zabbix Manager");
		scanner.close();
	}
	
	private void showHost() {
		listHostVO = new ArrayList<>();
		listHostVO = zabbixService.selectHostList();
		
		System.out.println("---------------------------------------------------");
		System.out.println("hostid | host            | status | item");
		System.out.println("---------------------------------------------------");
		
		for(HostVO hostVO : listHostVO) {
			System.out.printf("%-6d | %-15s | %-6d | %5d \n", hostVO.getHostid(), hostVO.getHost(), hostVO.getStatus(), zabbixService.selectItemCountByHostid(hostVO.getHostid()));
		}
		
		System.out.println("---------------------------------------------------");
	}
	
	private void showItem() {
		System.out.print("hostid > ");
		String input = scanner.nextLine();
		if(input.contentEquals("")) return;
		
		listItemVO = new ArrayList<>();
		listItemVO = zabbixService.selectItemListByHostid(Long.parseLong(input));
		
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("itemid | history | name");
		System.out.println("----------------------------------------------------------------------------------------");
		
		for(ItemVO itemVO : listItemVO) {
			System.out.printf("%-6d | %7d | %s \n", itemVO.getItemid(), zabbixService.selectHistoryCountByItemVO(itemVO), itemVO.getName());
		}
		
		System.out.println("----------------------------------------------------------------------------------------");
	}
	
	private void showHistory() {
		System.out.print("itemid > ");
		String input = scanner.nextLine();
		if(input.contentEquals("")) return;
		
		listHistoryVO = new ArrayList<>();
		listHistoryVO = zabbixService.selectHistoryList(zabbixService.selectItemByItemid(Long.parseLong(input)));
		
		System.out.println("----------------------------------");
		System.out.println("clock               | value");
		System.out.println("----------------------------------");
		
		for(HistoryVO historyVO : listHistoryVO) {
			System.out.printf("%s | %s \n", historyVO.getClock(), historyVO.getValue());
		}
		
		System.out.println("----------------------------------");
	}
	
	private void exportHistory() {
		System.out.print("itemid > ");
		String input = scanner.nextLine();
		if(input.contentEquals("")) return;
		
		boolean isSuccess = zabbixService.exportHistoryList(zabbixService.selectItemByItemid(Long.parseLong(input)), SAVE_DIRECTORY);
		
		if(isSuccess) System.out.println("Success");
		else System.out.println("Fail");
	}
}
