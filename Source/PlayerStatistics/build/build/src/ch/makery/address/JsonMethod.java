package ch.makery.address;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ch.makery.address.model.Player;
import ch.makery.address.view.PlayerOverviewController.MapEntry;

public class JsonMethod {

	public MainApp main;
	private FileWriter file;
	
	JsonMethod(MainApp main) {
		this.main = main;
	}
	
    public void getPlayerJSON() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		FileReader f = new FileReader("basket.json");
		JSONArray a = (JSONArray) parser.parse(f);

		for(Object o : a) {
			 JSONObject player = (JSONObject) o;
			 String prenom = (String) player.get("prenom");
			 String nom = (String) player.get("nom");
			 int record = (int)(long) player.get("record");
			 JSONArray scores = (JSONArray) player.get("scores");
			 Player p = new Player(prenom, nom, record);
			 HashMap<String, Integer> scoresMap = new HashMap<String, Integer>();
			 if(scores != null) {
			 for (Object s : scores) {
				 JSONObject sc = (JSONObject) s;
				 String date = (String) sc.get("date");
				 int score = (int)(long) sc.get("score");
				 scoresMap.put(date, score);
				
			 	}
			 
			 }
			 p.setScores(scoresMap);
			 mapSort(scoresMap, p);
			 main.playerData.add(p);
		}
	
		f.close();
    }
    

    
    public void addPlayer(Player p) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("basket.json"));
		JSONArray list = null;
		
		JSONObject o = new JSONObject();
		o.put("prenom", p.getFirstName());
		o.put("nom", p.getLastName());
		o.put("record", 0);
		o.put("scores", list);
		jsonArray.add(o);
		FileWriter file = new FileWriter("basket.json");
		file.write(jsonArray.toString());
		file.flush();
    }

    
    public void editPlayer(Player p, String prenom, String nom,  Map<String, Integer> scores) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
    	FileReader f = new FileReader("basket.json");
		JSONArray jsonArray = (JSONArray) parser.parse(f);
		JSONArray list = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("prenom", p.getFirstName());
		o.put("nom", p.getLastName());
		o.put("record", p.getRecord());
		for(String key : scores.keySet()) {
			JSONObject sc = new JSONObject();
			sc.put("date", key);
			sc.put("score", scores.get(key));
			list.add(sc);
		}
		o.put("scores", list);
		
		for(int i=0; i < jsonArray.size(); i++) {
			JSONObject player = (JSONObject) jsonArray.get(i);
			String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");	
			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {
				jsonArray.set(i, o);
				break;
			}			
		}
		FileWriter file = new FileWriter("basket.json");
		file.write(jsonArray.toString());
		file.flush();
		f.close();
    }
    
    
    public void deletePlayer(String prenom, String nom) throws FileNotFoundException, IOException, ParseException {
       	JSONParser parser = new JSONParser();
    		JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("basket.json"));
    		JSONArray list = jsonArray;
    		for(int i=0; i < jsonArray.size(); i++) {
    			JSONObject player = (JSONObject) jsonArray.get(i);
    			String prenomdel = (String) player.get("prenom");
    			String nomdel = (String) player.get("nom");	
    			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {
    				list.remove(i);
    				break;
    			}			
    		}
    		FileWriter file = new FileWriter("basket.json");
    		file.write(list.toString());
    		file.flush();
    }
    
    
    public void deleteScore(String prenom, String nom, MapEntry<String, Integer> s) throws FileNotFoundException, IOException, ParseException {
    	JSONParser parser = new JSONParser();
    	JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("basket.json"));
    	for(int i=0; i < jsonArray.size(); i++) {
    		JSONObject player = (JSONObject) jsonArray.get(i);
    		String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");				
			if(prenomdel.equals(prenom) && nomdel.equals(nom)) {
				JSONArray scores = (JSONArray) player.get("scores");
				if(scores != null) {
					 for (Object e : scores) {
						 JSONObject sc = (JSONObject) e;
						 String date = (String) sc.get("date");
						 if(date.equals(s.key)) {
							scores.remove(e);
							player.put("scores", scores);
							jsonArray.set(i, player);
							break; 
						 }				
					 }
				}
			}
			
    	}
		FileWriter file = new FileWriter("basket.json");
		file.write(jsonArray.toString());
		file.flush();
    
    }
    
    
 

	public void addNewScore(Player p) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
    	JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("basket.json"));
    	for(int i=0; i < jsonArray.size(); i++) {
    		JSONObject player = (JSONObject) jsonArray.get(i);
    		String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");				
			if(prenomdel.equals(p.getFirstName()) && nomdel.equals(p.getLastName())) {
				JSONArray scores = (JSONArray) player.get("scores");
				if(scores != null)
					scores.clear();
				else
					scores = new JSONArray();
				for(String key : p.getScores().keySet()) {
					JSONObject o = new JSONObject();
					o.put("date", key);
					o.put("score", p.getScores().get(key));				
					scores.add(o);
				}
				player.put("scores", scores);
				jsonArray.set(i, player);
			}
		}		
		FileWriter fil = new FileWriter("basket.json");
		fil.write(jsonArray.toString());
		fil.flush();
	}
    
	
	public void editScore(Player p, String date, int score) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
    	JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("basket.json"));
    	for(int i=0; i < jsonArray.size(); i++) {
    		JSONObject player = (JSONObject) jsonArray.get(i);
    		String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");				
			if(prenomdel.equals(p.getFirstName()) && nomdel.equals(p.getLastName())) {
				JSONArray scores = (JSONArray) player.get("scores");
				scores.clear();
				for(String key : p.getScores().keySet()) {
					JSONObject o = new JSONObject();
					o.put("date", key);
					o.put("score", p.getScores().get(key));				
					scores.add(o);
				}
				player.put("scores", scores);
				jsonArray.set(i, player);
			}
		}		
		FileWriter fil = new FileWriter("basket.json");
		fil.write(jsonArray.toString());
		fil.flush();
	}
	
	
	public void SetRecord(Player p) throws IOException, ParseException {
    	JSONParser parser = new JSONParser();
    	FileReader f = new FileReader("basket.json");
		JSONArray jsonArray = (JSONArray) parser.parse(f);
		for(int i=0; i < jsonArray.size(); i++) {
			JSONObject player = (JSONObject) jsonArray.get(i);
    		String prenomdel = (String) player.get("prenom");
			String nomdel = (String) player.get("nom");	
			if(prenomdel.equals(p.getFirstName()) && nomdel.equals(p.getLastName())) {
				player.put("record", p.getRecord());
				jsonArray.set(i, player);
			}
		}
		FileWriter fil = new FileWriter("basket.json");
		fil.write(jsonArray.toString());
		fil.flush();
	}
	
	
	public void mapSort(Map<String, Integer> map, Player player) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Map<String, Integer> scores = new TreeMap(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					Date d1 = dateFormat.parse(o1);
					Date d2 = dateFormat.parse(o2);
					return d1.compareTo(d2);
				} catch (java.text.ParseException e) {
					return 0;
				}
				
			}
          });
        for(String k : player.getScores().keySet()) {
        	scores.put(k, player.getScores().get(k));
        }
        player.setScores(scores);
	}
	
	

}
