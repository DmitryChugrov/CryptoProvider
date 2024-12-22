package ru.CryptoProvider.MouseMovement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MouseTrackerLogic {
    private int mouseX = 0;
    private int mouseY = 0;
    private int mouseMoves = 0;
    private final int maxMouseMoves = 1000; 
    private boolean trackingComplete = false; 

    
    public synchronized void incrementMouseMoves() {
        if (mouseMoves < maxMouseMoves) {
            mouseMoves++;
        }
    }

    
    public synchronized int getMouseMovesPercentage() {
        return (int) ((mouseMoves / (double) maxMouseMoves) * 100);
    }

    public synchronized void setMouseCoordinates(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public synchronized int getSumOfCoordinates() {
        return mouseX + mouseY;
    }

    
    public synchronized String hashSumOfCoordinates() {
        int sum = getSumOfCoordinates(); 
        try {
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(String.valueOf(sum).getBytes());

            
            StringBuilder hashString = new StringBuilder();
//            for (byte b : hashBytes) {
//                hashString.append(String.format("%02x", b));
//            }
            trackingComplete = true; 
            return hashString.toString(); 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public synchronized boolean isTrackingComplete() {
        return trackingComplete;
    }
}
