package open.dolphin.infomodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
public class SMSMessage implements Serializable {
    
    private List<String> numbers;
    
    private String message;

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }
    
    public void addNumber(String number) {
        if (numbers==null) {
            numbers = new ArrayList(1);
        }
        numbers.add(number);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
