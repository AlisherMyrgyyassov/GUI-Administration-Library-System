# GUI-Administration-Library-System
Graphical User Interface (GUI) providing the user with all the necessary tools to edit the existing and add new data to the system, save and load the data, and search book records and sort them in the desired way

The project's objective is to create a library administration system with the Graphical User Interface (GUI) providing the user with all the necessary tools to edit the existing and add new data to the system, save and load the data, and search book records and sort them in the desired way. According to the requirements set, the program should provide an interface for the user to be able to perform actions such as borrowing, returning, and reserving books. The program also has to support images and allow the user to attach an image to a book and save and load the book records from a text file as additional features. In order to achieve these objectives, the program implements several data structures including LinkedList and Queue. 

Our team could successfully finish the source code of the program satisfying all the requirements listed in the steps above. Based on the inputs from the user to the GUI, the program will perform the actions accordingly.

======================================================================================================================================================

MyLinkedList
Class Structure
MyLinkedList is a class implementing the MyList interface, which also extends another interface called Collection. MyLinkedList uses nodes of generic type. For its implementation, the class uses the Node class which itself has 2 different variables, one called “element” storing the data of a particular node, and another variable called “next” of Node<E> type pointing to the node next to the given node.

MyLinkedList is a custom class implementing another custom MyList interface which itself has some default built-in methods already existing in the Collection interface. This class is the essential structural element of the entire program.

MyQueue 
Class Structure
MyQueue class creates a new variable at the beginning of its implementation called “list” which has a type and all the properties of the MyLinkedList Class. The declaration of the variable is given below:
private MyLinkedList<E> list = new MyLinkedList<E>();
The purpose of the class is to maximize the efficiency of the manipulation with the list of data by allowing the user to use only the effective methods from the MyLinkedList class.
