package JavaDataStreams;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DataProcessGUI extends JFrame{
    JPanel mainPnl = new JPanel(new BorderLayout());
    JPanel textViewerPnl, searchPnl,cmdPnl;
    JTextArea originalTextArea, filteredTextArea;
    JTextField searchFld;
    JButton searchBtn, quitBtn, uploadBtn;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    Font buttonFont = new Font("Arial", Font.BOLD, 20);
    Font regFont = new Font("Arial", Font.PLAIN, 15);

    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    String rec = "";

    public DataProcessGUI(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Search! - Find Your Words");

        add(mainPnl);

        generateSearchBar();
        mainPnl.add(searchPnl, BorderLayout.NORTH);

        generateTextViewerPnl();
        mainPnl.add(textViewerPnl, BorderLayout.CENTER);

        generateCmdPnl();
        mainPnl.add(cmdPnl,BorderLayout.SOUTH);

        pack();
        setSize((int)(.80*screenSize.width),(int)(.80*screenSize.height));
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void generateSearchBar(){
        searchPnl = new JPanel(new GridLayout(1,2));
        searchPnl.setPreferredSize(new Dimension(100,50));
        searchBtn = new JButton("Search!");
        searchBtn.setSize(100, 20000);
        searchBtn.setFont(buttonFont);
        searchFld = new JTextField("Enter your word here...",10);
        searchFld.setFont(regFont);
        searchPnl.add(searchFld);


        searchBtn.addActionListener(e -> {
            String searchTerm = new String(searchFld.getText());
            if(selectedFile == null){
                JOptionPane.showMessageDialog(textViewerPnl,"Please select a file first!","Uh-oh, did you select a file?",JOptionPane.ERROR_MESSAGE);
            } else {
                filteredTextArea.setText("");
                try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath()))) {
                    lines.filter(l -> l.toLowerCase()
                            .contains(searchTerm.toLowerCase()))
                            .forEach(l -> filteredTextArea.append(l.strip() + "\n\n"));
                } catch (IOException k) {
                    System.out.print("Exception: " + k.getMessage());
                } catch (SecurityException k) {
                    System.out.print("Exception: " + k.getMessage());
                }
            }
        });
        searchPnl.add(searchBtn);
    }
    private void generateTextViewerPnl(){
        textViewerPnl = new JPanel(new GridLayout(1,2));
        originalTextArea = new JTextArea("");
        originalTextArea.setFont(regFont);
        originalTextArea.setEditable(false);
        filteredTextArea = new JTextArea("");
        filteredTextArea.setFont(regFont);
        filteredTextArea.setEditable(false);
        originalTextArea.setMargin(new Insets(10,10,0,5));
        filteredTextArea.setMargin(new Insets(10,10,0,5));
        JScrollPane scroller1 = new JScrollPane(originalTextArea);
        JScrollPane scroller2 = new JScrollPane(filteredTextArea);

        textViewerPnl.add(scroller1);
        textViewerPnl.add(scroller2);

    }

   public void generateCmdPnl(){
        cmdPnl = new JPanel(new GridLayout(1,2));
        cmdPnl.setPreferredSize(new Dimension(100,50));
        uploadBtn = new JButton("Choose a file!");
        uploadBtn.setFont(buttonFont);
        uploadBtn.addActionListener(e ->{
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES (.txt)", "txt", "text");
            chooser.setFileFilter(filter);
            originalTextArea.setText("");
            try
            {
                File workingDirectory = new File(System.getProperty("user.dir"));

                chooser.setCurrentDirectory(workingDirectory);

                if(chooser.showOpenDialog(textViewerPnl) == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    // Finally we can read the file LOL!
                    while(reader.ready())
                    {
                        rec = reader.readLine();
                        originalTextArea.append(rec + "\n");
                    }
                    reader.close(); // must close the file to seal it and flush buffer
                }
            }
            catch (FileNotFoundException k)
            {
                System.out.println("File not found!!!");
                k.printStackTrace();
            }
            catch (IOException k)
            {
                k.printStackTrace();
            }
        });
        cmdPnl.add(uploadBtn);


       quitBtn = new JButton("Quit");
       quitBtn.setFont(buttonFont);
       quitBtn.addActionListener(e -> System.exit(0));
       cmdPnl.add(quitBtn);
    }
}
