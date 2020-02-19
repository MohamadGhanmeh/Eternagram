package controllers;

import formatters.DateTimeFormats;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

@Singleton
public class SessionController {
	private static SortedMap<String, Node> sessionManager = new TreeMap<>();
	private static final int TIMEOUT = 30;  // in minutes
	private static final int ID_SIZE = 16;
	private static class Node {
		private String userId;
		private LocalDateTime timestamp;
		private String sessionID;

		Node(String userId){
			this.userId = userId;
			this.timestamp = LocalDateTime.now();
			this.sessionID = generateSessionID();
		}
	}

	private static String generateSessionID(){
		String options = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
		StringBuilder builder = new StringBuilder(ID_SIZE);
		for(int i = 0; i<ID_SIZE; i++) {builder.append(options.charAt((int)(options.length() * Math.random())));}
		return builder.toString();
	}
	static SortedMap<String, String> loginUser(Long userId){return loginUser(String.valueOf(userId));}
	static SortedMap<String, String> loginUser(String userId){
		Node newNode = new Node(userId);
		sessionManager.put(userId, newNode);
		SortedMap<String, String> answer = new TreeMap<>();
		answer.put("user",userId);
		answer.put("sessionID", newNode.sessionID);
		return answer;
	}
	static void logoutUser(String userId) {sessionManager.remove(userId);}
	static int verifySession(String userId, String sessionID){
		Node oldNode = sessionManager.get(userId);
		if (oldNode == null) return 0;                                                          // there is no login information (userName is null or not on the list of sessions)
		if (oldNode.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) {
			sessionManager.remove(userId);
			return -2;                                                                           // session has expired, remove it from the list.
		}
		if (!oldNode.sessionID.equals(sessionID)) return -1;                                    // sessionID is different but still valid (currently logged in from a different browser)
		oldNode.timestamp = LocalDateTime.now();
		sessionManager.put(userId, oldNode);
		return 1;                                                                               // session is valid, timestamp was updated (same sessionID is kept)
	}
	//private static void cleanSessions(){for (Node n : sessionManager.values()) {if (n.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) sessionManager.remove(n.userName);}}
	public static SortedMap<String, String> getActiveSessions(){
		SortedMap<String, String> answer = new TreeMap<>();
		for (Node n : sessionManager.values()) {
			if (n.timestamp.isBefore(LocalDateTime.now().minusMinutes(TIMEOUT))) sessionManager.remove(n.userId);
			else answer.put(n.userId, n.timestamp.format(DateTimeFormats.DATE_TIME_24) + " (" + n.sessionID + ")");
		}
		return answer;
	}
}
