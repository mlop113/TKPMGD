package net.codejava.sound;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.Timer;
import java.awt.EventQueue;

import javax.swing.JPanel;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import javax.media.*;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;


import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.JSlider;


public class MediaPanel1 {
	//private SwingSoundRecorder recoder= new SwingSoundRecorder();
	private SoundRecordingUtil recorder = new SoundRecordingUtil();
	private AudioPlayer player = new AudioPlayer();
	private Thread playbackThread;
	private RecordTimer timer;
	private ImageIcon iconRecord = new ImageIcon(getClass().getResource(
			"/net/codejava/sound/images/Record.gif"));
	private ImageIcon iconStop = new ImageIcon(getClass().getResource(
			"/net/codejava/sound/images/Stop.gif"));
	private ImageIcon iconPlay = new ImageIcon(getClass().getResource(
			"/net/codejava/sound/images/Play.gif"));
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private JFrame frame;
	 String[] cols = {"Col 1", "Col2"};
	 	String[][]  data = {{"Time", "Description"},{"1:06", "How are you"},{"1:15","where are you from?"},{"2:20","Tell me about your life!"}};
	 	String[][] data1 = {{"Time", "Title"},{"1:06", "Record_001"}};
	 	String[][] data2 = {{"Name", "Time"},{"assa", "4:09"},{"Hello World", "10:37"},{"Internet","12:09"}};
	    DefaultTableModel model = new DefaultTableModel(data, cols);
	    DefaultTableModel model1 = new DefaultTableModel(data1, cols);
	    DefaultTableModel model2 = new DefaultTableModel(data2, cols);
	    JTable table = new JTable(model);
	    JButton button = new JButton("Add Notes");
	    JTextField text = new JTextField(15);
	     JTable table_1 = new JTable(model1);
	    private JTextField txtSearch;
	    private JTextField text2;
	    JLabel labelRecordTime = new JLabel("Record Time: 00:00:00");
	    JButton buttonPlay = new JButton("Play");
	    JButton buttonRecord = new JButton("Record");
	    private String saveFilePath;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				  chargerLibrairie();
				  new MediaPanel1(args);
				  
			}
		});
	}		
	 public static String formatTime(long value) {
	        value /= 1000;
	        int hours = (int) (value / 3600);
	        int remainder = (int) (value - hours * 3600);
	        int minutes = remainder / 60;
	        remainder = remainder - minutes * 60;
	        int seconds = remainder;
	        return String.format("%d:%02d:%02d", hours, minutes, seconds);
	    }
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		if (button == buttonRecord) {
			if (!isRecording) {
				startRecording();
			} else {
				stopRecording();
			}

		} else if (button == buttonPlay) {
			if (!isPlaying) {
				playBack();
			} else {
				stopPlaying();
			}
		}
	}
	private void stopRecording() {
		isRecording = false;
		try {
			timer.cancel();
			buttonRecord.setText("Record");
			buttonRecord.setIcon(iconRecord);
			
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			recorder.stop();

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			saveFile();

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(frame, "Error",
					"Error stopping sound recording!",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	private void setCursor(Cursor predefinedCursor) {
		// TODO Auto-generated method stub
		
	}
	private void playBack() {
		timer = new RecordTimer(labelRecordTime);
		timer.start();
		isPlaying = true;
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					buttonPlay.setText("Stop");
					buttonPlay.setIcon(iconStop);
					buttonRecord.setEnabled(false);

					player.play(saveFilePath);
					timer.reset();

					buttonPlay.setText("Play");
					buttonRecord.setEnabled(true);
					buttonPlay.setIcon(iconPlay);
					isPlaying = false;

				} catch (UnsupportedAudioFileException ex) {
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
		});

		playbackThread.start();
	}
	private void stopPlaying() {
		timer.reset();
		timer.interrupt();
		player.stop();
		playbackThread.interrupt();
	}
	
	private void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Sound file (*.WAV)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".wav");
				}
			}
		};

		fileChooser.setFileFilter(wavFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showSaveDialog(frame);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			saveFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!saveFilePath.toLowerCase().endsWith(".wav")) {
				saveFilePath += ".wav";
			}

			File wavFile = new File(saveFilePath);

			try {
				recorder.save(wavFile);

				JOptionPane.showMessageDialog(frame,
						"Saved recorded sound to:\n" + saveFilePath);

				buttonPlay.setEnabled(true);

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(frame, "Error",
						"Error saving to sound file!",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void startRecording() {
		Thread recordThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					isRecording = true;
					buttonRecord.setText("Stop");
					buttonRecord.setIcon(iconStop);
					buttonPlay.setEnabled(false);

					recorder.start();

				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(frame,
							"Error", "Could not start recording sound!",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});
		recordThread.start();
		timer = new RecordTimer(labelRecordTime);
		timer.start();
	}

	  static void chargerLibrairie(){
	         NativeLibrary.addSearchPath(
	                RuntimeUtil.getLibVlcLibraryName(), "lib");
	        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	        LibXUtil.initialise();
	        	    }
	  static void chargerLibrairie1(){
	         NativeLibrary.addSearchPath(
	                RuntimeUtil.getLibVlcLibraryName(), "lib");
	        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	        LibXUtil.initialise();
	        SwingUtilities.invokeLater(() -> {
	        	VLCPlayer vlcPlayer = new VLCPlayer();
	        });
	    }
	/**
	 * Create the application.
	 */
	
	/**
	 * Initialize the contents of the frame.
	 */
	private  MediaPanel1(String[] args) {
		
		frame = new JFrame()			;	
	    frame.setLocation(800	, 800);
        frame.setSize(820, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        Canvas c = new Canvas();

        c.setBackground(Color.black);
        JPanel p = new JPanel();
        p.setBounds(60, 40, 340, 238);
        frame.getContentPane().add(p);
        p.setLayout(new BorderLayout());
        frame.setVisible(true);
        p.add(c, BorderLayout.CENTER);
        frame.getContentPane().add(p, BorderLayout.CENTER);

        
      
      
       
        JTable table_2 = new JTable( model2);     
     JPanel panel1 = new JPanel();
     panel1.setBackground(Color.GRAY);
      panel1.setBounds(427, 71, 347, 137);
      frame.getContentPane().add(panel1);
      panel1.setLayout(new BorderLayout());
      table.setEnabled(false);
      table.setBackground(Color.WHITE);
      table.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel1.add(table, BorderLayout.NORTH);
      text.setBackground(Color.LIGHT_GRAY);
      panel1.add(text, BorderLayout.WEST);
      frame.getContentPane().add(panel1);
      
      text2 = new JTextField();
      text2.setToolTipText("add");
      text2.setBackground(Color.LIGHT_GRAY);
      panel1.add(text2, BorderLayout.CENTER);
      text2.setColumns(10);
      panel1.add(button, BorderLayout.SOUTH);
      
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    String value1 = text.getText().toString();
                    String value2=text2.getText();
                    
                    model.addRow(new  Object[] {value1,value2});
                }
           });
      
     
         
    	
      JPanel panel = new JPanel();
      panel.setBackground(Color.LIGHT_GRAY);
      panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel.setBounds(427, 285, 347, 224);
      frame.getContentPane().add(panel);
      panel.setLayout(new BorderLayout());
    
      
      panel.add(table_1, BorderLayout.NORTH);
      
      JLabel lblNewLabel = new JLabel("My  Record");
      lblNewLabel.setBounds(457, 268, 106, 14);
      frame.getContentPane().add(lblNewLabel);
      
      JLabel lblNewLabel_1 = new JLabel("My Video");
      lblNewLabel_1.setBounds(78, 15, 76, 14);
      frame.getContentPane().add(lblNewLabel_1);
      
      txtSearch = new JTextField();
      txtSearch.setText("Search");
      txtSearch.setBounds(441, 40, 120, 20);
      frame.getContentPane().add(txtSearch);
      txtSearch.setColumns(10);
      
      JPanel panel_1 = new JPanel();
      panel_1.setBackground(Color.LIGHT_GRAY);
      panel_1.setBounds(60, 343, 340, 170);
      frame.getContentPane().add(panel_1);
      panel_1.setLayout(new BorderLayout());
      
      panel_1.add(table_2, BorderLayout.NORTH);
        
      JLabel lblMyListVideo = new JLabel("My List Video");
      lblMyListVideo.setBounds(60, 318, 100, 14);
      frame.getContentPane().add(lblMyListVideo);
	      
      MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
      
      EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
      mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(c));
    
      mediaPlayer.toggleFullScreen();
   
      mediaPlayer.setEnableMouseInputHandling(false);
   
      mediaPlayer.setEnableKeyInputHandling(true);
     
     
      JSlider slider = new JSlider();
    
      slider.setMinorTickSpacing(2);
      slider.setBounds(88, 285, 312, 26);
      frame.getContentPane().add(slider);
      
      	JLabel label = new JLabel();

      
      	slider.setValue((int) mediaPlayer.getTime()/1000);
      label.setBounds(30, 285, 57, 14);
      frame.getContentPane().add(label);
     
      
      JButton btnNewButton_4 = new JButton("Open");
      btnNewButton_4.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent arg0) {
      		mediaPlayer.stop();
      		chargerLibrairie1();
      	}
      });
      btnNewButton_4.setBounds(140, 314, 70, 23);
      frame.getContentPane().add(btnNewButton_4);
      
      JButton btnNewButton_5 = new JButton("Play");
      btnNewButton_5.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		int row = table_2.getSelectedRow();
            int col = table_2.getSelectedColumn();          
	        	 
	             if (row == 1 || col == 1 ) {
	            	 
	             mediaPlayer.prepareMedia("asas.mp4");
	             mediaPlayer.play();          
	             
	         	long time= mediaPlayer.getTime();
	          	label.setText(formatTime(time));
	             }
	             if (row == 2 || col == 2) {
	            	
		             mediaPlayer.prepareMedia("video1.mp4");
		             mediaPlayer.play();          
	             }
	             if (row == 3 || col == 3) {
	            	 
		             mediaPlayer.prepareMedia("video2.mp4");
		             mediaPlayer.play();          
	             }
      	}
      });
      btnNewButton_5.setBounds(60, 524, 89, 23);
      frame.getContentPane().add(btnNewButton_5);
      
      JButton btnNewButton_3 = new JButton("Pause");
      btnNewButton_3.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		 mediaPlayer.pause();
      	}
      });
      btnNewButton_3.setBounds(189, 524, 89, 23);
      frame.getContentPane().add(btnNewButton_3);
      
      JButton btnNewButton_6 = new JButton("Stop");
      btnNewButton_6.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		mediaPlayer.stop();
      	}
      });
      btnNewButton_6.setBounds(311, 524, 89, 23);
      frame.getContentPane().add(btnNewButton_6);
      
      JPanel panel_2 = new JPanel();
      panel_2.setBounds(427, 512, 347, 35);
      frame.getContentPane().add(panel_2);
      panel_2.setLayout(null);
      buttonPlay.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		JButton button = (JButton) e.getSource();
    		if (button == buttonRecord) {
    			if (!isRecording) {
    				startRecording();
    			} else {
    				stopRecording();
    			}

    		} else if (button == buttonPlay) {
    			if (!isPlaying) {
    				playBack();
    			} else {
    				stopPlaying();
    			}
    		}
      	}
      });
      
    
      buttonPlay.setBounds(257, 0, 89, 35);
      panel_2.add(buttonPlay);
      buttonRecord.addActionListener(new ActionListener() {
      	public void actionPerformed(ActionEvent e) {
      		JButton button = (JButton) e.getSource();
    		if (button == buttonRecord) {
    			if (!isRecording) {
    				startRecording();
    			} else {
    				stopRecording();
    			}

    		} else if (button == buttonPlay) {
    			if (!isPlaying) {
    				playBack();
    			} else {
    				stopPlaying();
    			}
    		}
      	}
      });
      
   
      buttonRecord.setBounds(0, 0, 110, 35);
      panel_2.add(buttonRecord);
      
    
      labelRecordTime.setBounds(113, 0, 140, 35);
      panel_2.add(labelRecordTime);
      buttonRecord.setFont(new Font("Sans", Font.BOLD, 14));
		buttonRecord.setIcon(iconRecord);
		buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPlay.setIcon(iconPlay);
		buttonPlay.setEnabled(false);
		labelRecordTime.setFont(new Font("Sans", Font.BOLD, 12));
		
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLocationRelativeTo(null);
	}
}
