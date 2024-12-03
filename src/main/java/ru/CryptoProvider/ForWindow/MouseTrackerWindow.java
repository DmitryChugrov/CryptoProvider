package ru.CryptoProvider.ForWindow;

import ru.CryptoProvider.MouseMovement.MouseTrackerLogic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
public class MouseTrackerWindow extends JFrame implements MouseMotionListener {
    private final MouseTrackerLogic trackerLogic;
    private final JProgressBar progressBar;
    private volatile boolean isTracking = true; 

    public MouseTrackerWindow() {
        
        setTitle("Генерация ключа");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        
        trackerLogic = new MouseTrackerLogic();

        
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(50, 50, 300, 30);
        progressBar.setStringPainted(true);
        add(progressBar);

        
        setLayout(null);

        
        addMouseMotionListener(this);

        
        new Thread(this::updateProgressBar).start();
    }

    
    public MouseTrackerLogic getTrackerLogic() {
        return trackerLogic;
    }

    
    public void waitForTrackingToComplete() {
        while (trackerLogic.getMouseMovesPercentage() < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isTracking = false;
    }

    
    private void updateProgressBar() {
        while (isTracking) { 
            int progress = trackerLogic.getMouseMovesPercentage();
            progressBar.setValue(progress);

            if (progress >= 100) {
//                JOptionPane.showMessageDialog(this, "Mouse Tracking Complete!", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        if (isTracking) {
            trackerLogic.setMouseCoordinates(e.getX(), e.getY());
            trackerLogic.incrementMouseMoves();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }
}
