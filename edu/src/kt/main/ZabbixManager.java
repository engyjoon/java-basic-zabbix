package kt.main;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import kt.service.ZabbixService;
import kt.vo.HistoryVO;
import kt.vo.HostVO;
import kt.vo.ItemVO;

public class ZabbixManager {
	
	ZabbixService zabbixService = ZabbixService.getInstance();
	
	private static final String SAVE_DIRECTORY = "/Users/engyjoon/temp/";
	
	public static void main(String[] args) {
		ZabbixManager zabbixManager = new ZabbixManager();
		
		Scanner scanner = new Scanner(System.in);
		boolean isLoop = true;
		
		System.out.println("--------------------------------------------------");
		System.out.println("                  Zabbix Manager                  ");
		System.out.println("--------------------------------------------------");
		
		while(isLoop) {
			printMenu();
			isLoop = zabbixManager.action(scanner.nextLine());
			
			if(!isLoop) exitProgram(scanner);
		}
		
	}
	
	private boolean action(String input) {
		boolean result = true;
		
		switch(input) {
			case "1":
				showHost();
				break;
			case "2":
				showItem();
				break;
			case "3":
				showHistory();
				break;
			case "4":
				exportHistory();
				break;
			case "q":
				result = false;
				break;
			default :
				System.out.println();
		}
		
		return result;
	}
	
	private void showHost() {
		ArrayList<HostVO> list = new ArrayList<>();
		list = zabbixService.selectHostList();
		
		System.out.println("---------------------------------------------------");
		System.out.println("hostid | host            | status | item");
		System.out.println("---------------------------------------------------");
		
		for(HostVO host : list) {
			System.out.printf("%-6d | %-15s | %-6d | %5d \n", host.getHostid(), host.getHost(), host.getStatus(), zabbixService.selectItemCountByHostid(host.getHostid()));
		}
		
		System.out.println("---------------------------------------------------");
		System.out.println();
	}
	
	private void showItem() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("hostid > ");
		String input = scanner.nextLine();
		
		if(input.contentEquals("")) {
			System.out.println();
			return;
		}
		
		long hostid = Long.parseLong(input);
		
		ArrayList<ItemVO> list = new ArrayList<>();
		list = zabbixService.selectItemListByHostid(hostid);
		
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("itemid | history | name");
		System.out.println("----------------------------------------------------------------------------------------");
		
		for(ItemVO item : list) {
			System.out.printf("%-6d | %7d | %s \n", item.getItemid(), zabbixService.selectHistoryCountByItemid(item), item.getName());
		}
		
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println();
	}
	
	private void showHistory() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("itemid > ");
		String input = scanner.nextLine();
		
		if(input.contentEquals("")) {
			System.out.println();
			return;
		}
		
		long itemid = Long.parseLong(input);
		
		ArrayList<HistoryVO> list = new ArrayList<>();
		list = zabbixService.selectHistoryList(zabbixService.selectItemByItemid(itemid));
		
		System.out.println("----------------------------------");
		System.out.println("clock               | value");
		System.out.println("----------------------------------");
		
		for(HistoryVO history : list) {
			System.out.printf("%s | %s \n", history.getClock(), history.getValue());
		}
		
		System.out.println("----------------------------------");
		System.out.println();
	}
	
	private void exportHistory() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		File file = new File(SAVE_DIRECTORY + "zabbix_" + now.format(formatter));
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("itemid > ");
		String input = scanner.nextLine();
		
		if(input.contentEquals("")) {
			System.out.println();
			return;
		}
		
		long itemid = Long.parseLong(input);
		
		boolean isSuccess = zabbixService.exportHistoryList(zabbixService.selectItemByItemid(itemid), file);
		
		if(isSuccess) System.out.println("Success");
		else System.out.println("Fail");
		
		System.out.println();
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
	
	private static void exitProgram(Scanner scanner) {
		System.out.println("");
		System.out.println("Exit Zabbix Manager");
	}
}
