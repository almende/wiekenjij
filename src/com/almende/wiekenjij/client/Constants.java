package com.almende.wiekenjij.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
	static Map<String, String> domains = null;
	static Map<String, Integer> frequencies = null;
	
	private static void init() {
		if (domains == null) {
			domains = new HashMap<String, String>();

			domains.put("", "black");
			domains.put("Buitenspelen", "red");
			domains.put("Buurtactiviteiten", "blue");
			domains.put("Cultuur/kunst", "green");
			domains.put("Familie", "magenta");
			domains.put("Opvang", "brown");
			domains.put("Religie", "orange");
			domains.put("School", "darkviolet");
			domains.put("Sport", "limegreen");
		}
		
		if (frequencies == null) {
			frequencies = new HashMap<String, Integer>();

			// value of frequencies is the number of contacts per year
			frequencies.put("", 0);
			frequencies.put("Bijna nooit", 1);
			frequencies.put("1x in 3 maanden", 4);
			frequencies.put("1x per maand", 12);
			frequencies.put("1x per week", 52);
			frequencies.put("2x per week", 104);
			frequencies.put("dagelijks", 365);
		}
	}
	
	static List<String> getDomainNames() {
		init();
		
		List<String> domainNames = new ArrayList<String>();
		for (String name : domains.keySet()) {
			domainNames.add(name);
		}
		Collections.sort(domainNames);
		
		return domainNames;
	}
	
	static Map<String, String> getDomains() {
		init();
		
		return domains;
	}
	
	static List<String> getFrequencyNames() {
		init();
		
		List<String> frequencieNames = new ArrayList<String>();
		for (String name : frequencies.keySet()) {
			frequencieNames.add(name);
		}
		//Collections.sort(frequencieNames); // do not sort!
		
		return frequencieNames;
	}
	
	static Map<String, Integer> getFrequencies() {
		init();
		
		return frequencies;
	}
	
}
