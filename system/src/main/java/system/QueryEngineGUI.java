package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class QueryEngineGUI {
    private JFrame mainFrame;
    private JDialog resultsDialog;
    private JLabel title;
    private JTextField queryField;
    private JPanel buttonAndField;
    private JButton submitButton;
    private String indexFolder;
    private String stopWords;
    private QueryEngine queryEngine;
    private ArrayList<Result> info;
    
    /**
     * Constructor:
     */
    public QueryEngineGUI(){
        createGUI();
    }

    public static void main(String[] args) {
        new QueryEngineGUI();
    }
    
    /**
     * Prepares and shows the GUI:
     */
    private void createGUI(){
        // Create window and set properties:
        mainFrame = new JFrame("Document Searcher");
        mainFrame.setSize(700, 500);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(4,1));

        // Create widgets:
        title = new JLabel("Document Searcher", JLabel.CENTER);
        title.setFont(new Font("Lato", Font.PLAIN, 48));
        title.setForeground(Color.decode("#6A5ACD"));
        queryField = new JTextField(30);
        queryField.setFont(new Font("Lato", Font.PLAIN, 14));
        buttonAndField = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Lato", Font.PLAIN, 14));

        // Add widgets to frame:        
        buttonAndField.add(queryField);
        buttonAndField.add(submitButton);
        queryField.setPreferredSize(submitButton.getPreferredSize());
        mainFrame.add(new JPanel()); // Empty
        mainFrame.add(title);
        mainFrame.add(buttonAndField);

        // Add listeners:
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                try {
                    if(queryEngine != null)
                        queryEngine.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                System.exit(0);
            }        
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(indexFolder == null) {
                    JFileChooser chooser = new JFileChooser(); 
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle("Select the directory that contains the index files");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    if(chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        indexFolder = chooser.getSelectedFile().getAbsolutePath();
                    } else {
                        indexFolder = null;
                    }
                }

                if(stopWords == null) {
                    JFileChooser chooser = new JFileChooser(); 
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle("Select the file that contains the list of stop words");
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    if(chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                        stopWords = chooser.getSelectedFile().getAbsolutePath();
                    } else {
                        stopWords = null;
                    }
                }

                // The user can skip the file dialog
                if(stopWords != null && indexFolder != null) {
                    PerformQuery(queryField.getText());
                }
            }
        });

        mainFrame.setVisible(true);
    }

    /**
     * This method is executed when the button "Submit" is pressed
     * It performs the query using the QueryEngine and index
     * @param inputQuery The user's query
     */
    private void PerformQuery(String inputQuery) {
        try {
            if(!inputQuery.equals("")) {
                if(queryEngine == null) {
                    queryEngine = new QueryEngine(indexFolder, stopWords);
                }
                info = queryEngine.Find(inputQuery);
                ShowResultsView();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * Show the Documents found view after a succesful query.
     */
    private void ShowResultsView() {
        resultsDialog = new JDialog(mainFrame, true);
        resultsDialog.setTitle("Documents found");
        resultsDialog.setSize(500, 400);
        resultsDialog.setResizable(false);
        resultsDialog.setLocationRelativeTo(mainFrame);

        DefaultListModel<Result> dlm = new DefaultListModel<Result>();
        for (Result res : info) {
            dlm.addElement(res);
        }

        JList<Result> list = new JList<Result>(dlm);
        list.setFixedCellHeight(50);
        list.setFixedCellWidth(500);
        list.setCellRenderer(new ResultRender());

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    try {
                        Desktop.getDesktop().open(new File(info.get(index).getPath()));
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } 
            }
        });

        JScrollPane jScrollPane = new JScrollPane(list, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        resultsDialog.add(jScrollPane);
        resultsDialog.setVisible(true);
    }
}