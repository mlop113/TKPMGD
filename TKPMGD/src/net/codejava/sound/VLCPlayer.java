package net.codejava.sound;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCPlayer {

    private static JFileChooser filechooser = new JFileChooser();
//This is the path for libvlc.dll

    public static void main(String[] args) {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        SwingUtilities.invokeLater(() -> {
            VLCPlayer vlcPlayer = new VLCPlayer();
        });

    }

    VLCPlayer() {

//MAXIMIZE TO SCREEN
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame = new JFrame();
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

        Canvas c = new Canvas();
        c.setBackground(Color.black);
        JPanel p = new JPanel();
        c.setBounds(100, 500, 1050, 500);
        p.setLayout(new BorderLayout());
        p.add(c, BorderLayout.CENTER);
        p.setBounds(100, 50, 1050, 600);
        frame.add(p, BorderLayout.NORTH);
        JPanel p1 = new JPanel();

        p1.setBounds(100, 900, 105, 200);
        frame.add(p1, BorderLayout.SOUTH);

        JButton stopbutton = new JButton("stop");

        //playbutton.setIcon(new ImageIcon("C:/Users/biznis/Desktop/Newspaper/sangbadpratidin/d/download.png"));
        stopbutton.setBounds(50, 50, 150, 100);
        // playbutton.addActionListener((ActionListener) this);
        p1.add(stopbutton);

        JButton pausebutton = new JButton("pause");

        //  pausebutton.setIcon(new ImageIcon("pics/pausebutton.png"));
        pausebutton.setBounds(80, 50, 150, 100);

        p1.add(pausebutton);
        File ourfile;

        filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        filechooser.showSaveDialog(null);
        ourfile = filechooser.getSelectedFile();
        String mediaPath = ourfile.getAbsolutePath();

        EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(c));
        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        mediaPlayer.playMedia(mediaPath);
        pausebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                mediaPlayer.pause();
                // or mediaPlayer.pause() depending on what works.
                final long time = mediaPlayer.getTime();
                System.out.println(time);
            }
        });
        stopbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                mediaPlayer.stop();
                frame.setVisible(false);
            }
        });
    }
}