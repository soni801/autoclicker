package com.autoclicker.main;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;

/**
 * @author Soni
 */
public class Autoclicker implements NativeKeyListener
{
    public Autoclicker()
    {
        JFrame frame = new JFrame();
        
        frame.setTitle("Soni's Autoclicker");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent)
    {
    
    }
    
    public static void main(String[] args)
    {
        new Autoclicker();
    }
}