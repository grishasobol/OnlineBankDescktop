package my.desktop;

/**
 *
 * @author Dmitry
 */
public class BTDialogParser {
    private final static String SEPARATOR = ":";
    
    public static String recvSignSource(String response){
        String answer;
        String[] tokens = response.split(SEPARATOR);
        switch(tokens[0].toLowerCase()){
            case "bad_request":
                answer = "bad";
                break;
            case "sign":
                answer = tokens[1];
                break;
            default:
                answer = null;
                break;
        }
        return answer;
    }
    
    public static String sendSignSource(String response){
        String answer;
        String[] tokens = response.split(SEPARATOR);
        switch(tokens[0].toLowerCase()){
            case "bad_request":
                answer = "bad";
                break;
            case "answer":
                answer = "good";
                break;
            default:
                answer = null;
                break;
        }
        return answer; 
    }
}
