import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import java.util.HashMap;

public class spyder {
    public static void main(String[] args) throws Exception {
        processDataFromFile("Available_Rooms0309.html");
        HashMap<String, Integer> bedSpots = availableBedsPerRoom("availableRoomsData.csv");
        convertToJSON("availableRoomsData.csv", bedSpots);
    }

    public static void processDataFromFile(String filename) throws Exception {
        File grades = new File(filename);
        Scanner sc = new Scanner(grades);
        PrintWriter pw = new PrintWriter(new File("availableRoomsData.csv"));
        StringBuilder sb = new StringBuilder();
        String line;
        int i=0;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            i++;
            String beginningLine = "<script src=";
            boolean housingInfoLine = true;
            int k = 0;
            while (housingInfoLine) {
                if ((k < beginningLine.length()) && (k < line.length())) {
                    if (beginningLine.charAt(k) != line.charAt(k)) {
                        housingInfoLine = false;
                        break;
                    }
                } else if (k == beginningLine.length()) {
                    break;
                } else {
                    housingInfoLine = false;
                    break;
                }
                k++;
            }
            if (housingInfoLine) {
                //line = sc.nextLine();
                String[] words = line.split("tbody");
                String table = words[1];
                String[] rows = table.split("<tr");
                for (int j = 1; j < rows.length; j++) {
                    String[] data = rows[j].split("<td");
                    String building = data[1];
                    building = building.substring(17,building.length()-5);
                    String roomNumber = data[2];
                    roomNumber = roomNumber.substring(17,roomNumber.length()-5);
                    String gender = data[3];
                    gender = gender.substring(17,gender.length()-5);
                    String capacity = data[4];
                    capacity = capacity.substring(17,capacity.length()-5);
                    sb.append(building);
                    sb.append(',');
                    sb.append(roomNumber);
                    sb.append(',');
                    sb.append(gender);
                    sb.append(',');
                    sb.append(capacity);
                    sb.append('\n');
                }
                pw.write(sb.toString());
                pw.close();
            }
        }
    }

    public static HashMap<String, Integer> availableBedsPerRoom(String filename) throws Exception {
        File grades = new File(filename);
        Scanner sc = new Scanner(grades);
        String line;
        HashMap<String, Integer> availableRooms = new HashMap<>();
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] spots = line.split(",");
            String building = spots[0];
            String roomNumberAndBedSpot = spots[1];
            String gender = spots[2];
            String roomCapacity = spots[3];
            String roomNumber = roomNumberAndBedSpot.substring(0,roomNumberAndBedSpot.length()-1);
            if (availableRooms.containsKey(roomNumber)) {
                availableRooms.put(roomNumber, availableRooms.get(roomNumber) + 1);
            } else {
                availableRooms.put(roomNumber, 1);
            }
        }
        return availableRooms;
    }

    public static HashMap<String, String> genderPerRoom(String filename) throws Exception {
        File grades = new File(filename);
        Scanner sc = new Scanner(grades);
        String line;
        HashMap<String, String> roomGender = new HashMap<>();
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] spots = line.split(",");
            String building = spots[0];
            String roomNumberAndBedSpot = spots[1];
            String gender = spots[2];
            String roomCapacity = spots[3];
            String roomNumber = roomNumberAndBedSpot.substring(0,roomNumberAndBedSpot.length()-1);
            if (!roomGender.containsKey(roomNumber)) {
                roomGender.put(roomNumber, gender);
            }
        }
        return roomGender;
    }

    public static void convertToJSON(String filename, HashMap<String, Integer> bedSpots) throws Exception {
        File grades = new File(filename);
        Scanner sc = new Scanner(grades);
        PrintWriter pw = new PrintWriter(new File("data.json"));
        StringBuilder sb = new StringBuilder();
        String line;
        sb.append("[");
        Integer i = 1;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] spots = line.split(",");
            String building = spots[0];
            String roomNumberAndBedSpot = spots[1];
            String gender = spots[2];
            String roomCapacity = spots[3];
            String roomNumber = roomNumberAndBedSpot.substring(0,roomNumberAndBedSpot.length()-1);
            String toAppend = "{\"id\": \""+ i.toString() + "\",\"building\": \""+ building + "\",\"roomNumberAndBedSpot\": \"" + roomNumberAndBedSpot + "\",\"gender\": \""+gender+"\",\"roomCapacity\": \""+roomCapacity+"\",\"bedSpots\": \""+bedSpots.get(roomNumber).toString()+"\"},";
            sb.append(toAppend);
            i++;
        }
        StringBuilder sbNEW = new StringBuilder();
        sbNEW.append(sb.toString().substring(0,sb.toString().length()-1));
        sbNEW.append("]");
        pw.write(sbNEW.toString());
        pw.close();
    }
}
