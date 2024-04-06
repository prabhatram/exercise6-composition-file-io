package exercise6.composition.file.io;

import org.junit.BeforeClass;
import org.junit.Test;

import exercise6.composition.file.io.ModelClasses.Book;
import exercise6.composition.file.io.ModelClasses.BookCollection;
import exercise6.composition.file.io.ModelClasses.OrderDetails;
import exercise6.composition.file.io.ModelClasses.User;
import exercise6.composition.file.io.OrderProcessing.InvoiceGenerator;
import exercise6.composition.file.io.OrderProcessing.OrderProcessing;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Tests {

    
    static OrderProcessing orderProcessing;
    static String selectedFirstName;
    static String selectedLastName;
    static String userID;
    static String orderID;
    static User user;
    static OrderDetails orderDetails;
    static String selectedBookID;
    static String bookOrdered;
    static String selectedOrderType;
    static InvoiceGenerator invoiceGenerator;
    static BookCollection books;
    

    @BeforeClass
    public static void setUpClass() {
        
        
        books = new BookCollection();
        books.addCollection("BookCollection.csv");
        orderProcessing = new OrderProcessing();

        String [] firstName = {"Natalie", "Mitchell", "Bruce", "Katie"};
        String [] lastName = {"Moore", "Johnson", "Willis", "Hopkins"};

        Random random = new Random();
        int firstNameIndex = random.nextInt(firstName.length);
        int lastNameIndex = random.nextInt(lastName.length);

        selectedFirstName = firstName[firstNameIndex];
        selectedLastName = lastName[lastNameIndex];

        userID = orderProcessing.generateUserID(selectedFirstName, selectedLastName);

        orderID = orderProcessing.generateOrderID(userID);

        user = new User(selectedFirstName, selectedLastName, "randomemail@random.com", userID);
        orderDetails = new OrderDetails(orderID, LocalDate.now());

        String[] bookIDs = {"B001", "B002", "B003", "B004", "B005", "B006", "B007", "B008", "B009", "B010", "B011", "B012", "B013", "B014", "B015", "B016", "B017", "B018", "B019", "B020"};
        int bookIDIndex = random.nextInt(bookIDs.length);
        selectedBookID = bookIDs[bookIDIndex];

        Book book = books.searchBookByID(selectedBookID);

            if(book!=null){

                String[] orderTypes = {"Purchase", "Rent"};
                
                int orderTypeIndex = random.nextInt(orderTypes.length);
                selectedOrderType = orderTypes[orderTypeIndex];

                bookOrdered = "\n" + "Book ID:" + book.getBookID() + 
                    "\n" + " Book Name: " + book.getBookName() + 
                    "\n" + " Book Author: " + book.getBookAuthor() + 
                    "\n" + " Book Publisher: " + book.getBookPublisher() + 
                    "\n" + " Book Price: " + book.getBookPrice() + 
                    "\n" + " Order Type: " + selectedOrderType;

                orderDetails.setOrderTransactionAmount(book.getBookPrice());
                orderDetails.setOrderedBook(bookOrdered);
                orderDetails.setOrderType(selectedOrderType);
                orderProcessing.addUserOrders(user, orderDetails);

            }
        }

    @Test
    public void testOrderProcessing() {

        /* String hashMapValueOutput = orderProcessing.userOrders.values().toString();
        String hashMapKeyOutput = orderProcessing.userOrders.keySet().toString(); */

        String hashMapValueOutput = orderProcessing.getHashMapValues().toString();
        String hashMapKeyOutput = orderProcessing.getHashMapKey().toString();

        assertEquals("User ID must be available.", userID, user.getUserID());
        assertEquals("Order ID must be available.", orderID, orderDetails.getOrderID());
        
        assertTrue("HashMap value must contain an order ID", hashMapValueOutput.contains(orderID));
        assertTrue("HashMap key must contain an user ID", hashMapKeyOutput.contains(userID));
        assertEquals("HashMap must have one entry", false, orderProcessing.isHashMapEmpty());
        
    }

    @Test
    public void testInvoiceGeneration(){
        orderProcessing.prepareInvoiceDetails(user, orderDetails, orderID);
        boolean found = false;
        

        try{
            FileReader fileReader = new FileReader("Invoice.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String searchString1 = userID;
            String searchString2 = orderID;
            String searchString3 = selectedBookID;
            String searchString4 = "The Lord of the Rings";

            String currentLine;

            while((currentLine=reader.readLine()) != null){
                if(currentLine.contains(searchString1)){
                    found = true;
                    break;
                } 
            }
            assertTrue("User ID must be available in the invoice", found);

            while((currentLine=reader.readLine()) != null){
                found = false;
                if(currentLine.contains(searchString2)){
                    found = true;
                    break;
                } 
            }
            assertTrue("Order ID must be available in the invoice", found);

            while((currentLine=reader.readLine()) != null){
                found = false;
                if(currentLine.contains(searchString3)){
                    found = true;
                    break;
                } 
            }
            assertTrue("Selected book ID must be available in the invoice", found);

            while((currentLine=reader.readLine()) != null){
                found = false;
                if(currentLine.contains(searchString4)){
                    found = true;
                    break;
                } 
            }
            assertFalse("The searched string must not be present in the invoice.", found);

            reader.close();

        } catch(IOException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testSearchingUserOrders(){

        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        orderProcessing.searchUserOrders(userID);

        String expectedOutput = orderProcessing.getHashMapValues().toString();
        expectedOutput = expectedOutput.replace("[", "").replace("]", "").replace("\n", "");

        String actualOutput = outContent.toString().trim();
        actualOutput = actualOutput.replace("[", "").replace("]", "").replace("\n", "");

        assertEquals(expectedOutput, actualOutput);

        System.setOut(System.out);

    }

    @Test
    public void testDisplayingAllUserOrders(){

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        orderProcessing.displayAllUserOrders();

        String actualOutput = outContent.toString();
        
        assertTrue(actualOutput.contains(userID));
        assertTrue(actualOutput.contains(user.getFirstName()));
        assertTrue(actualOutput.contains(user.getLastName()));
        assertTrue(actualOutput.contains(orderID));
        assertTrue(actualOutput.contains(orderDetails.getOrderedBook().toString()));

        System.setOut(System.out);

    }

    @Test
    public void testRemovingOrders(){
        orderProcessing.removeUserOrders(userID);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        orderProcessing.searchUserOrders(userID);

        assertEquals("HashMap must be empty", true, orderProcessing.isHashMapEmpty());
        System.setOut(System.out);

    }
    
}
    