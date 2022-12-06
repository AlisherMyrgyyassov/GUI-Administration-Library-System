import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
All buttons list, so that you can copy it 


addButton
editButton
saveButton
deleteButton
searchButton
moreButton
loadTestDataButton
displayAllButton
displayByISBNButton
exitButton
saveDataButton
loadDataButton
 */

public class LabFrame extends JFrame {

   private MyLinkedList<Book> bookList = new MyLinkedList<>();
   
   private JTextArea text = new JTextArea(2, 50);
   
   //userPanel
   private JPanel userPanel = new JPanel(new GridLayout(3, 0));
   private JPanel userR1 = new JPanel();
   private JPanel userR2 = new JPanel();
   private JPanel userR3 = new JPanel();
   
   //text fields for panel
   private JTextField isbn = new JTextField(10);
   private JTextField title = new JTextField(15);
   
   
   //top-row buttons
   private JButton addButton = new JButton("Add");
   private JButton editButton = new JButton("Edit");
   private JButton saveButton = new JButton("Save");
   private JButton deleteButton = new JButton("Delete");
   private JButton searchButton = new JButton("Search");
   private JButton moreButton = new JButton("More>>");
   
   //bottom-row buttons
   private JButton loadTestDataButton = new JButton("Load Test Data");
   private JButton displayAllButton = new JButton("Display All");
   private JButton displayByISBNButton = new JButton("Display All by ISBN");
   private JButton displayByTitleButton = new JButton("Display All by Title");
   private JButton exitButton = new JButton("Exit");
   private JButton saveDataButton = new JButton("Save Data");
   private JButton loadDataButton = new JButton("Load Data");
   
   //jtable
   private JTable table;
   private String[] columns = {"ISBN", "Title", "Available"};
   
   
   //others
   private SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z YYYY", Locale.ENGLISH);
   private String tempISBN;
   private String dataPath = "src\\data.txt";
   private BorrowFrame dialog;
   
   //system
   private static boolean isbnReversed = false;
   private static boolean titleReversed = false;
   
   public LabFrame() {
	   //set frame properties
	   super("Library Admin System");
       setLayout(new GridLayout(3, 0));
       
       // Jtable for book records
       
       DefaultTableModel tableModel = new DefaultTableModel(columns,0) {
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           } //because we cannot edit none of the cells
       };
       table = new JTable(tableModel);
       JScrollPane scrollPane = new JScrollPane(table);
       table.getTableHeader().setReorderingAllowed(false);
       table.setFillsViewportHeight(true);
       
       table.addMouseListener(new MouseAdapter() { //configuring the mouse listener for table
           @Override
           public void mouseClicked(MouseEvent e) {
               DefaultTableModel target = (DefaultTableModel) table.getModel();
               int indexSelectedRow = table.getSelectedRow();
               
               if(!bookList.isEmpty()&&e.getClickCount()==1&&table.getSelectedRow()!=-1){
                   isbn.setText(target.getValueAt(indexSelectedRow,0).toString());
                   title.setText(target.getValueAt(indexSelectedRow,1).toString());
               }
           }
       });
       
       
       //studentID Panel
       text.setEditable(false);
       text.append("Student Name and ID: Alisher Myrgyyassov (20041751d)\nStudent Name and ID: LE Quang Son (21105398D)\n" + dateFormat.format(new Date()));
       
       
       //user panel
       userR1.add(new JLabel("ISBN"));
       userR1.add(isbn);
       userR1.add(new JLabel("Title"));
       userR1.add(title);
       //add buttons to the panel
       saveButton.setEnabled(false);
       userR2.add(addButton);
       userR2.add(editButton);
       userR2.add(saveButton);
       userR2.add(deleteButton);
       userR2.add(searchButton);
       userR2.add(moreButton);
       userR3.add(loadTestDataButton);
       userR3.add(displayAllButton);
       userR3.add(displayByISBNButton);
       userR3.add(displayByTitleButton);
       userR3.add(saveDataButton);
       userR3.add(loadDataButton);
       userR3.add(exitButton);
       userPanel.add(userR1);
       userPanel.add(userR2);
       userPanel.add(userR3);
       
       this.add(text);
       this.add(scrollPane);
       this.add(userPanel);   
       
       //adding listeners
       addButton.addActionListener(new MyEventListener());
       editButton.addActionListener(new MyEventListener());
       saveButton.addActionListener(new MyEventListener());
       deleteButton.addActionListener(new MyEventListener());
       searchButton.addActionListener(new MyEventListener());
       moreButton.addActionListener(new MyEventListener());
       loadTestDataButton.addActionListener(new MyEventListener());
       displayAllButton.addActionListener(new MyEventListener());
       displayByISBNButton.addActionListener(new MyEventListener());
       exitButton.addActionListener(new MyEventListener());
       saveDataButton.addActionListener(new MyEventListener());
       loadDataButton.addActionListener(new MyEventListener());
       
       
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(960, 500);
       setVisible(true);
       setLocationRelativeTo(null);
   }
   
   //Listener config
   class MyEventListener implements ActionListener {
       @Override
       public void actionPerformed(ActionEvent e) {
    	   
    	   if (e.getSource() == addButton) {

               if (getISBN().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank");
                   resetTextFields();
                   return;
               }
               if (getTITLE().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: title cannot be blank");
                   resetTextFields();
                   return;
               }

               Book temp = new Book();
               temp.setISBN(getISBN());
               
               if (!bookList.contains(temp)) {
                   temp.setTitle(getTITLE());
                   bookList.add(temp);
                   addRow(table, temp);
               } 
               else if (bookList.contains(temp)) {
                   JOptionPane.showMessageDialog(null, "Error: Book already exists");
                   resetTextFields();
                   return;
               }
               resetTextFields();
    	   }
    	   
    	   else if (e.getSource() == editButton)
           {
               if (bookList.isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: Database is empty");
                   return;
               }
               if (getISBN().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank");
                   resetTextFields();
                   return;
               }
               Book temp = new Book();
               temp.setISBN(getISBN());
               if (!bookList.isEmpty() && bookList.contains(temp)) {
                   Book book = bookList.get(bookList.indexOf(temp));
                   if (getTITLE().isEmpty())
                       title.setText(book.getTitle());
                   addButton.setEnabled(false);
                   editButton.setEnabled(false);
                   saveButton.setEnabled(true);
                   deleteButton.setEnabled(false);
                   searchButton.setEnabled(false);
                   moreButton.setEnabled(false);
                   loadTestDataButton.setEnabled(false);
                   displayAllButton.setEnabled(false);
                   displayByISBNButton.setEnabled(false);
                   exitButton.setEnabled(false);
                   saveDataButton.setEnabled(false);
                   loadDataButton.setEnabled(false);
                   tempISBN = temp.getISBN();
               } else {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN is not in the database");
               }

           }
    	   
    	   else if (e.getSource() == saveButton) {
               if (getISBN().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank");
                   return;
               }
               if (getTITLE().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: title cannot be blank");
                   return;
               }
               Book temp = new Book();
               temp.setISBN(tempISBN);
               Book temp2 = new Book();
               temp2.setISBN(getISBN());
               temp2.setTitle(getTITLE());
               if (!getISBN().equals(tempISBN) && bookList.contains(temp2)) {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN exists in the current database");
               } else {
                   Book book = bookList.get(bookList.indexOf(temp));
                   book.setISBN(getISBN());
                   book.setTitle(getTITLE());
                   DefaultTableModel model = (DefaultTableModel) table.getModel();
                   model.setValueAt(book.getISBN(), bookList.indexOf(book), 0);
                   model.setValueAt(book.getTitle(), bookList.indexOf(book), 1);
                   
                   addButton.setEnabled(true);
                   editButton.setEnabled(true);
                   saveButton.setEnabled(false);
                   deleteButton.setEnabled(true);
                   searchButton.setEnabled(true);
                   moreButton.setEnabled(true);
                   loadTestDataButton.setEnabled(true);
                   displayAllButton.setEnabled(true);
                   displayByISBNButton.setEnabled(true);
                   exitButton.setEnabled(true);
                   saveDataButton.setEnabled(true);
                   loadDataButton.setEnabled(true);
                   
                   resetTextFields();
               }
           }
    	   
    	   else if (e.getSource() == searchButton) {
               if (getISBN().isEmpty() && getTITLE().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: Both ISBN and Title cannot be blank");
                   resetTextFields();
                   return;
               }
               clearTable();
               if (!getISBN().isEmpty() && !getTITLE().isEmpty()) {
                   Iterator<Book> iterator = bookList.iterator();
                   Pattern patternISBN = Pattern.compile(getISBN());
                   Matcher matcherISBN;
                   Pattern patternTitle = Pattern.compile(getTITLE());
                   Matcher matcherTitle;
                   Set listOfIndexes = new HashSet();
                   while (iterator.hasNext()) {
                       Book temp = iterator.next();
                       matcherISBN = patternISBN.matcher(temp.getISBN());
                       matcherTitle = patternTitle.matcher(temp.getTitle());
                       if (matcherISBN.find())
                           listOfIndexes.add(bookList.indexOf(temp));
                       if (matcherTitle.find())
                           listOfIndexes.add(bookList.indexOf(temp));
                   }
                   Iterator <Integer> it = listOfIndexes.iterator();
                   while (it.hasNext()) {
                       Book temp = bookList.get(it.next());
                       DefaultTableModel model = (DefaultTableModel) table.getModel();
                       model.addRow(new Object[]{temp.getISBN(), temp.getTitle(), temp.isAvailable()});
                   }
               } else if (!getISBN().isEmpty()) {
                   Iterator <Book> iterator = bookList.iterator();
                   Pattern patternISBN = Pattern.compile(getISBN());
                   Matcher matcherISBN;
                   Set listOfIndexes = new HashSet();
                   while (iterator.hasNext()) {
                       Book temp = iterator.next();
                       matcherISBN = patternISBN.matcher(temp.getISBN());
                       if (matcherISBN.find())
                           listOfIndexes.add(bookList.indexOf(temp));
                   }
                   Iterator <Integer> it = listOfIndexes.iterator();
                   while (it.hasNext()) {
                       Book temp = bookList.get(it.next());
                       DefaultTableModel model = (DefaultTableModel) table.getModel();
                       model.addRow(new Object[]{temp.getISBN(), temp.getTitle(), temp.isAvailable()});
                   }
               } 
               
               else if (!getTITLE().isEmpty()) {
                   Iterator <Book> iterator = bookList.iterator();
                   Pattern patternTitle = Pattern.compile(getTITLE());
                   Matcher matcherTitle;
                   Set listOfIndexes = new HashSet();
                   while (iterator.hasNext()) {
                       Book temp = iterator.next();
                       matcherTitle = patternTitle.matcher(temp.getTitle());
                       if (matcherTitle.find())
                           listOfIndexes.add(bookList.indexOf(temp));
                   }
                   Iterator <Integer> it = listOfIndexes.iterator();
                   while (it.hasNext()) {
                       Book temp = bookList.get(it.next());
                       DefaultTableModel model = (DefaultTableModel) table.getModel();
                       model.addRow(new Object[]{temp.getISBN(), temp.getTitle(), temp.isAvailable()});
                   }
               }
                   resetTextFields();
           }
    	   
    	   else if (e.getSource() == loadTestDataButton) {

               Book test1 = new Book();
               Book test2 = new Book();
               Book test3 = new Book();
               test1.setISBN("0131450913");
               test2.setISBN("0131857576");
               test3.setISBN("0132222205");
               if (!bookList.contains(test1)) {
                   test1.setTitle("HTML How to Program");
                   bookList.add(test1);
               } else {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN" + test1.getISBN() + " is already in the database");
               }
               if (!bookList.contains(test2)) {
                   test2.setTitle("C++ How to Program");
                   bookList.add(test2);
               } else {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN" + test2.getISBN() + " is already in the database");
               }
               if (!bookList.contains(test3)) {
                   test3.setTitle("Java How to Program");
                   bookList.add(test3);
               } else {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN" + test2.getISBN() + " is already in the database");
               }
               showAllRecords(bookList);
               resetTextFields();
           } 
    	   
    	   else if (e.getSource() == deleteButton) {
               if (bookList.isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: Database is empty");
                   return;
               }
               if (getISBN().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "java.lang.Exception: Error: ISBN cannot be blank");
                   resetTextFields();
                   return;
               }
               Book temp = new Book();
               temp.setISBN(getISBN());
               if (!bookList.isEmpty() && bookList.contains(temp)) {
                   int tempIndex = bookList.indexOf(temp);
                   bookList.remove(tempIndex);
                   ((DefaultTableModel) table.getModel()).removeRow(tempIndex);
                   resetTextFields();
               }else {
                   JOptionPane.showMessageDialog(null, "Error: book ISBN is not in the database");
               }
               resetTextFields();
           }
    	   else if (e.getSource() == displayAllButton)
           {
               showAllRecords(bookList);

           } 
    	   
    	   else if (e.getSource() == displayByTitleButton) {
               Book[] temp = bookList.toArray(new Book[bookList.size()]);

               if (titleReversed) {
                   Arrays.sort(temp, Comparator.comparing(Book::getTitle).reversed());
               } else {
                   Arrays.sort(temp, Comparator.comparing(Book::getTitle));
               }
               titleReversed = !titleReversed;
               showAllRecords(new MyLinkedList<Book>(temp));
           } 
    	   
    	   else if (e.getSource() == displayByISBNButton) {
               Book[] temp = bookList.toArray(new Book[bookList.size()]);
               if (isbnReversed) {
                   Arrays.sort(temp, Collections.reverseOrder((s1, s2) -> {
                       BigInteger t1 = new BigInteger(s1.getISBN());
                       BigInteger t2 = new BigInteger(s2.getISBN());
                       return t1.compareTo(t2);
                   }));
                   showAllRecords(new MyLinkedList<Book>(temp));
               } else {
                   Arrays.sort(temp, (s1, s2) -> {
                       BigInteger t1 = new BigInteger(s1.getISBN());
                       BigInteger t2 = new BigInteger(s2.getISBN());
                       return t1.compareTo(t2);
                   });
                   showAllRecords(new MyLinkedList<Book>(temp));

               }
               isbnReversed=!isbnReversed;
           }
    	   
    	   else if (e.getSource() == exitButton) {
               dispose();
           } 
    	   
    	   else if(e.getSource() == moreButton){
               if (getISBN().isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: ISBN cannot be blank");
                   resetTextFields();
                   return;
               }
               Book temp = new Book();
               temp.setISBN(getISBN());
               if(!bookList.contains(temp)){
                   JOptionPane.showMessageDialog(null, "Error: book ISBN is not in the database");
                   resetTextFields();
                   return;
               }
               Book book = bookList.get(bookList.indexOf(temp));

               dialog= new BorrowFrame(null,book);
               resetTextFields();
               showAllRecords(bookList);
           }
    	   
    	   
    	   
    	   //additional features
    	   else if(e.getSource() == saveDataButton){
               if (bookList.isEmpty()) {
                   JOptionPane.showMessageDialog(null, "Error: Database is empty");
                   return;
               }

               BufferedWriter writer = null;
               try{
                   writer= new BufferedWriter(new FileWriter(dataPath));
                   for(Book entry: bookList){
                       writer.write(entry.getISBN()+"\n");
                       writer.write(entry.getTitle()+"\n");
                       writer.write(entry.isAvailable()+"\n");
                       writer.write(entry.getImagePath()+"\n");
                       if(entry.getReservedQueue().getList().isEmpty())
                       {
                           writer.write("\n");
                       }
                       else{
                           for(String name:entry.getReservedQueue().getList())
                           {
                               writer.write(name+";");
                           }
                           writer.write("\n");//extra
                       }

                   }
               }catch (IOException ex){
                   ex.printStackTrace();
               }
               finally {
                   if(writer!=null)
                   {
                       try {
                           writer.close();
                       } catch (IOException exception) {
                           exception.printStackTrace();
                       }
                   }
               }
           }
    	   
    	   else if(e.getSource()==loadDataButton)
           {
               if(!isFileEmpty())
               {
                   BufferedReader reader = null;
                   try{
                       reader = new BufferedReader(new FileReader(dataPath));
                       String line;
                       String tempisbn ="";
                       String temptitle="";
                       String tempavailable="";
                       String tempimagepath="";
                       String tempWaitQ = "";
                       int counter = 0;

                       while(true){
                           if(counter>4)
                           {
                               counter=0;
                               Book temp = new Book();
                               temp.setISBN(tempisbn);
                               temp.setTitle(temptitle);
                               temp.setAvailable(Boolean.parseBoolean(tempavailable));
                               temp.setImagePath(tempimagepath);
                               if(tempWaitQ.length()!=0)
                               {
                                   String[] arr = tempWaitQ.split(";");
                                   MyQueue<String> q = new MyQueue<>();
                                   for(String str:arr)
                                   {
                                       q.enqueue(str);
                                   }
                                   temp.setReservedQueue(q);
                               }
                               bookList.add(temp);
                           }
                           if((line=reader.readLine())==null)
                               break;

                           switch (counter){
                               case 0:
                                   tempisbn = line.trim();
                                   break;
                               case 1:
                                   temptitle = line.trim();
                                   break;
                               case 2:
                                   tempavailable = line.trim();
                                   break;
                               case 3:
                                   tempimagepath = line.trim();
                                   break;
                               case 4:
                                   tempWaitQ = line.trim();
                                   break;
                           }

                           counter++;
                       }
                       //System.out.println(bookList);
                       showAllRecords(bookList);
                   }
                   catch (IOException exception)
                   {
                       exception.printStackTrace();
                   }
                   finally {
                       if(reader!=null)
                       {
                           try {
                               reader.close();
                           } catch (IOException exception) {
                               exception.printStackTrace();
                           }
                       }
                   }
               }
           }
    	   
    	   
    	  
       }
   }
   
   //Organizational functions
   private String getTITLE(){
       return title.getText().trim();
   }
   private String getISBN(){
       return isbn.getText().trim();
   }
   private void clearTable() {
       while (table.getRowCount() > 0) {
           ((DefaultTableModel) table.getModel()).removeRow(0);
       }
   }
   private void addRow(JTable table, Book book) {
       DefaultTableModel model = (DefaultTableModel) table.getModel();
       model.addRow(new Object[]{book.getISBN(), book.getTitle(), book.isAvailable()});
   }
   private void showAllRecords(MyLinkedList<Book> bookLinkedList) {
       clearTable();
       for (Book entry : bookLinkedList) {
           DefaultTableModel model = (DefaultTableModel) table.getModel();
           model.addRow(new Object[]{entry.getISBN(), entry.getTitle(), entry.isAvailable()});
       }
   }
   private boolean isFileEmpty(){ //returns true if the file is empty
       File file = new File(dataPath);
       if(file.length()==0)
           return true;
       else
           return false;
   }
   private void resetTextFields() {
       isbn.setText("");
       title.setText("");
       table.getSelectionModel().clearSelection();
   }
}