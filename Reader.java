import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reader {


    public static final Integer CARD_NUMBER_ =0;
    public static final Integer NAME_= 1;
    public static final Integer PHONE_= 2;
    public static final Integer BOOK_COUNT_ =3 ;
    public static final Integer BOOK_START_ =4 ;

    private Integer cardNumber;
    private String name;
    private String phone;
    private List<Book> books;

    public Reader(Integer cardNumber, String name, String phone) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.phone = phone;
        books = new ArrayList<Book>();
    }



public Code addBook(Book book){
    if(books.contains(book)){
    return Code.BOOK_ALREADY_CHECKED_OUT_ERROR;
    }
    else {
        books.add(book);
        return Code.SUCCESS;
    }
}

public Code removeBook(Book book) {
    if(books.contains(book)) {
        books.remove(book);
        return Code.SUCCESS;
    }
    else if (!books.contains(book)){
        return Code.READER_DOESNT_HAVE_BOOK_ERROR;
    }
    else{
        return Code.READER_COULD_NOT_REMOVE_BOOK_ERROR;
    }
}

public boolean hasBook(Book book) {
       return (books.contains(book));
}

public int getBookCount() {
        return books.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return Objects.equals(cardNumber, reader.cardNumber) && Objects.equals(name, reader.name) && Objects.equals(phone, reader.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, name, phone);
    }

    @Override
    public String toString() {
        return "Reader{" + "cardNumber=" + cardNumber + ", name='" + name + '\'' + ", books=" + books + '}';
    }


}
