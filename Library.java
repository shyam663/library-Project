import com.sun.net.httpserver.Authenticator;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;


public class Library {

    public static  Integer LENDING_LIMIT = 5 ;
    private String name ;
    private static int libraryCard;
    private List readers;
    private HashMap <String, Shelf >shelves;
    private HashMap <Book, Integer> books;

    public Library(String name){
        this.name = name;
        this.libraryCard = 0;
        this.readers = new ArrayList<>();
        this.shelves = new HashMap<>();
        this.books = new HashMap<>();

    }



    public Code init(String filename) {

        Scanner scans;
        File f = new File(filename);

        try {
            scans = new Scanner(f);
        }

        catch (FileNotFoundException e) {
            return Code.FILE_NOT_FOUND_ERROR;

        }


        int rdrCount = convertInt ( scans.nextLine(), Code.READER_COUNT_ERROR) ;
        if ( rdrCount < 0) {
            return errorCode( rdrCount ) ;
        }
        initReader( rdrCount, scans ) ;

        int bkCount = convertInt ( scans.nextLine() , Code.BOOK_COUNT_ERROR) ;
        if (bkCount < 0) {
            return errorCode( bkCount ) ;
        }
        initBooks( bkCount, scans ) ;
        listBooks() ;

        int slfCount = convertInt ( scans.nextLine(), Code.SHELF_NUMBER_PARSE_ERROR) ;
        if ( slfCount < 0) {
            return errorCode ( slfCount ) ;
        }

        initShelves( slfCount , scans ) ;

        System.out.println("SUCCESS FROM INIT") ;
        return Code.SUCCESS ;

    }





        private Code initBooks ( int bookCount, Scanner scan){
            if(bookCount < 1) {
                return Code.LIBRARY_ERROR;
            }

            System.out.println("Parsing " + bookCount + " books");
            int count = 0 ;
            while(scan.hasNextLine() && count < bookCount) {
                String fileLine = scan.nextLine();
                String[] BKData = fileLine.split(",");

                String ISBN_CSV = BKData[Book.ISBN_];
                String TCSV = BKData[Book.TITLE_];
                String pageCountCSV = BKData[Book.PAGE_COUNT_];
                String dueDateCSV = BKData[Book.DUE_DATE_];
                String AuthCSV = BKData[Book.AUTHOR_];
                String SubCSV = BKData[Book.SUBJECT_];




                int inpgcsv = convertInt(pageCountCSV, Code.PAGE_COUNT_ERROR);
                LocalDate DuCSV = convertDate(dueDateCSV, Code.DATE_CONVERSION_ERROR);
                if (inpgcsv <= 0) {
                    return Code.PAGE_COUNT_ERROR;
                }
                
                else if(DuCSV == null) {
                    return Code.DATE_CONVERSION_ERROR;
                }

                Book book = new Book(ISBN_CSV, TCSV, SubCSV, inpgcsv, AuthCSV, DuCSV);
                addBook(book) ;
                count++ ;
            }
            System.out.println("SUCCESS FROM INIT BOOKS");
            return Code.SUCCESS;
        }









        private Code initShelves ( int shelfCount, Scanner scan){

            if (shelfCount < 1) {
                return Code.SHELF_COUNT_ERROR;
            }

            System.out.println( "\nparsing " +shelfCount+ " shelves") ;

            int count = 0 ;
            while(scan.hasNextLine() && count < shelfCount) {
                String fileLine = scan.nextLine() ;
                String[] shelfData = fileLine.split(",") ;
                String SubCSV = shelfData[Shelf.SUBJECT_] ;
                String slfNumber_CSV = shelfData[Shelf.SHELF_NUMBER_] ;

                int intslfNumber_CSV = convertInt(slfNumber_CSV, Code.SHELF_NUMBER_PARSE_ERROR) ;
                
                if (intslfNumber_CSV <= 0) {
                    return Code.SHELF_NUMBER_PARSE_ERROR ;
                }

                Shelf shelf = new Shelf() ;
                shelf.setShelfNumber(intslfNumber_CSV) ;
                addShelf(shelf) ;
                shelf.setSubject(SubCSV) ;
                count++ ;

            }

            if( shelves.size() != shelfCount)  {
                System.out.println( "Number of shelves doesn't match expected" ) ;
                return Code.SHELF_NUMBER_PARSE_ERROR ;
            }
            
            System.out.println( "SUCCESS FROM INIT SHELVES" ) ;
            return Code.SUCCESS ;

        }






        private Code initReader ( int readerCount, Scanner scan){

            if(readerCount <= 0) {
                return Code.READER_COUNT_ERROR;
            }

            System.out.println("\nparsing " + readerCount + " readers");

            int count = 0;
            while(scan.hasNextLine() && count < readerCount) {
                String fileLine = scan.nextLine();
                String[] readerData = fileLine.split(",");
                String cardNumCSV = readerData[ Reader.CARD_NUMBER_ ] ;
                String nameCSV = readerData[ Reader.NAME_ ]  ;
                String phoneCSV = readerData[ Reader.PHONE_ ] ;
                String bcountCSV = readerData[ Reader.BOOK_COUNT_ ] ;

                int intcardNum = convertInt( cardNumCSV, Code.SHELF_NUMBER_PARSE_ERROR ) ;
                int intBCount = convertInt( bcountCSV, Code.BOOK_COUNT_ERROR)  ;

                Reader read = new Reader( intcardNum, nameCSV, phoneCSV );
                addReader(read);

                int i = Reader.BOOK_START_;
                while (i < ( intBCount*2+4) ) {
                    Book books = getBookByISBN(readerData[i]);
                    if( books == null ) {
                        System.out.println( "ERROR" );
                    }

                    String Date = readerData[ i+1 ] ;
                    LocalDate due_Date = convertDate( Date, Code.DATE_CONVERSION_ERROR );
                    books.setDueDate( due_Date );
                    checkOutBook( read, books );
                     i+=2;
                }
                count++;
            }
            System.out.println("SUCCESS FROM INIT READER");
            return Code.SUCCESS;


        }





        public Code addBook ( Book newBook ) {
            if( books.containsKey( newBook ) ) {
                Integer cnt = books.get( newBook ) + 1;
                books.put( newBook, cnt );
                System.out.println( cnt + " copies of " + newBook.getTitle() + " in the stacks." ) ;
            }
            else {
                books.put( newBook, 1 ) ;
                System.out.println( newBook.getTitle() + " added to the stacks.") ;
            }

            if (shelves.containsKey ( newBook.getSubject() ) ) {
                shelves.get( newBook.getSubject() ).addBook( newBook ) ;
                return Code.SUCCESS ;
            }
            else {
                System.out.println("No shelf for " + newBook.getSubject() + " books.") ;
                return Code.SHELF_EXISTS_ERROR ;
            }
        }







        public Code returnBook ( Reader reader, Book book ){

        return Code.SUCCESS;
        }




        public Code returnBook ( Book book ) {
            if ( shelves.containsKey ( book.getSubject() ) ) {
                shelves.get ( book.getSubject() ).addBook( book ) ;
                return Code.SUCCESS ;
            }
            else {
                System.out.println ( "No shelf for " + book ) ;
                return Code.SHELF_EXISTS_ERROR ;
            }
        }





        private Code addBookToShelf (Book book, Shelf shelf){
            Code results = returnBook(book) ;

            if( !shelf.getSubject().equals( book.getSubject() ) ) {
                return Code.SHELF_SUBJECT_MISMATCH_ERROR ;
            }

            if (results.equals (Code.SUCCESS) ) {
                return Code.SUCCESS ;
            }

            Code codez = shelf.addBook(book) ;
            if (codez.equals(Code.SUCCESS) ) {
                System.out.println(book + " added to shelf.") ;
            }
            else {
                System.out.println("Could not add " + book + " to shelf.") ;
            }
            return codez ;
        }



        public int listBooks () {

            int bkCount = 0 ;
            for ( Book book: books.keySet() )
            {
                bkCount += books.get( book ) ;
                System.out.println( books.get(book) + " copies of " + book.toString() ) ;
            }

            return bkCount ;
        }

    public Code listShelves (boolean showbooks) {

        return Code.SUCCESS;


    }





        public Code checkOutBook (Reader reader, Book book){

            return Code.SUCCESS;
        }






        public Book getBookByISBN (String isbn) {

            for ( Book booked: books.keySet() ) {
                if ( booked.getISBN().equals(isbn ) ) {
                    return booked ;
                }
            }

            System.out.println( "ERROR: Could not find a book with isbn: " + isbn ) ;
            return null ;
    }





        public Code listShelves ( boolean showbooks ) {
            if (showbooks) {
                for (Shelf shelfs: shelves.values()) {
                    System.out.println( shelfs.listBooks() ) ;
                }
            }
            else {
                for ( Shelf shelfs : shelves.values() ) {
                    System.out.println( shelfs.toString() ) ;
                }
            }

            return Code.SUCCESS ;
    }






        public Code addShelf ( String shelfSubject ) {

            Shelf shelf = new Shelf () ;
            shelf.setShelfNumber ( shelves.size() + 1 ) ;
            shelf.setSubject ( shelfSubject ) ;
            return addShelf ( shelf ) ;
        }


        public Code addShelf (Shelf shelf) {
            if ( shelves.containsKey ( shelf.getSubject() ) ) {
                System.out.println ( "ERROR: Shelf already exists " + shelf ) ;

                return Code.SHELF_EXISTS_ERROR;
                shelves.put ( shelf.getSubject(), shelf ) ;

                for ( Map.Entry set: books.entrySet() ) {
                    Integer copie = (Integer)set.getValue() ;
                    Book booked = (Book)set.getKey() ;

                    int z = 0 ;
                    while ( z < copie) {
                        if ( booked.getSubject().equals ( shelf.getSubject() ) ) {
                            addBookToShelf( booked, shelf ) ;
                        }
                         z++ ;
                    }
                }
                return Code.SUCCESS ;
    }


       public Shelf getShelf(Integer shelfNumber){

              return null;
    }

        public Shelf getShelf(String subject) {

                return null;
            }
    }

        public int listReaders ( int subject ) {

        return subject;
            }


        public int listReaders ( boolean showBooks ) {

            return readers.size();
    }




        public Reader getReaderByCard ( int cardNumber ) {

            System.out.println ( "Could not find a reader with card #" + cardNumber );
            return null;
    }






        public Code addReader (Reader reader) {

            return Code.SUCCESS;
        }






        public Code removeReader (Reader reader ) {

            return Code.SUCCESS;
    }




        public int convertInt ( String recordCountString , Code code ){
            try {
                int number = Integer.parseInt( recordCountString ) ;
                return number ;
            }

            catch (NumberFormatException e) {
                System.out.println( "Value which caused the error: " + recordCountString ) ;
                System.out.println( "Error message: " + code.getMessage() ) ;
                return code.getCode() ;
            }
    }








        public LocalDate convertDate (String date, Code code){

        }



        public int getLibraryCardNumber () {
            return libraryCard++ ;
        }




        private Code errorCode ( int codeNumber ) {
            for ( Code codes: Code.values() ) {
                if ( codes.getCode()==codeNumber ) {
                    return codes ;
                }
            }
            return Code.UNKNOWN_ERROR ;
            }
        }


    }

