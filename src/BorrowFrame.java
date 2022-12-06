import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BorrowFrame extends JDialog {
    private JTextArea text = new JTextArea(3,100);
    private JTextArea systemM = new JTextArea(2,100);
    
    private Book book;
    private JPanel middleOut = new JPanel(new BorderLayout());
    private JPanel buttons = new JPanel(new FlowLayout());
    
    private JButton borrowButton = new JButton("Borrow");
    private JButton returnButton = new JButton("Return");
    private JButton reserveButton = new JButton("Reserve");
    private JButton waitButton = new JButton("Waiting Queue");
    private JButton addImageButton = new JButton("Add Image");
    private JButton seeImageButton = new JButton("Image Preview");

    public BorrowFrame(Frame frame, Book book) {
        super(frame,book.getTitle(),true);
        setSize(690, 500);
        this.book = book;
        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        text.setEditable(false);
        
        updateTextArea();
        updateButtonPanel();
        
        middleOut.add(buttons,BorderLayout.NORTH);
        this.add(text,BorderLayout.NORTH);
        this.add(middleOut,BorderLayout.CENTER);
        this.add(systemM,BorderLayout.SOUTH);
        
        seeImageButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialog = new JDialog();
                    dialog.setTitle(book.getTitle());
                    JLabel label = new JLabel( new ImageIcon(book.getImagePath()) );
                    dialog.add(label);
                    dialog.setModal(true);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setLocationRelativeTo(null);
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        );
        
        addImageButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser file = new JFileChooser();
                    File workingDirectory = new File(".");
                    file.setCurrentDirectory(workingDirectory);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
                    file.addChoosableFileFilter(filter);
                    int result = file.showSaveDialog(null);
                    if(result == JFileChooser.APPROVE_OPTION){
                        File selectedFile = file.getSelectedFile();
                        book.setImagePath(selectedFile.getAbsolutePath());
                    }
                }
            }
        );
        
        returnButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MyLinkedList<String> tempList = book.getReservedQueue().getList();

                    String temp = "The book is returned.";
                    if(tempList.isEmpty())
                    {
                        String x = book.getReservedQueue().dequeue();
                        book.setAvailable(true);
                        resetButtons();
                        updateButtonPanel();
                        updateTextArea();

                    }
                    else
                    {
                        String x = book.getReservedQueue().dequeue();
                        temp+="\nThe book is now borrowed by "+x+".";
                    }
                    updateSystemDisplay(temp);

                }
            }
        );
        
        waitButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String temp = "The waiting queue:\n";
                    MyLinkedList<String> tempList = book.getReservedQueue().getList();
                    for(int i = 0;i<tempList.size();i++)
                    {
                        temp+=tempList.get(i)+"\n";
                    }
                    updateSystemDisplay(temp);
                }
            }
        );
        
        reserveButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String tempUser = JOptionPane.showInputDialog("What`s your name?");
                    book.getReservedQueue().enqueue(tempUser);
                    updateSystemDisplay("The book is reserved by "+tempUser+".");
                }
            }
        );
        
        borrowButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    book.setAvailable(false);
                    updateTextArea();
                    toggleButtons();
                    updateSystemDisplay("The book is borrowed");
                }
            }
        );
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void clearTextArea(){
        text.setText("");
    }
    private void clearSystemDisplay(){
        systemM.setText("");
    }
    
    private void updateSystemDisplay(String str){
        clearSystemDisplay();
        systemM.setText(str);
    }
    
    private void updateTextArea(){
        clearTextArea();
        text.append("ISBN: "+book.getISBN()+"\n"+"Title: "+book.getTitle()+"\n"+"Available: "+book.isAvailable());
    }
    
    private void updateButtonPanel(){
        buttons.add(borrowButton);
        buttons.add(addImageButton);
        buttons.add(seeImageButton);
        buttons.add(returnButton);
        buttons.add(reserveButton);
        buttons.add(waitButton);
        toggleButtons();


    }
    private void resetButtons(){
        buttons.removeAll();
        buttons.revalidate();
        buttons.repaint();
    }
    private void toggleButtons(){
        if(book.isAvailable())
        {
            borrowButton.setEnabled(true);
            returnButton.setEnabled(false);
            reserveButton.setEnabled(false);
            waitButton.setEnabled(false);

        }
        else{
            borrowButton.setEnabled(false);
            returnButton.setEnabled(true);
            reserveButton.setEnabled(true);
            waitButton.setEnabled(true);
        }
    }

}
