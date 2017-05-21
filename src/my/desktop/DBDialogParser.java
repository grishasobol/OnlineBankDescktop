package my.desktop;

/**
 *
 * @author Dmitry
 */
public class DBDialogParser {
    private final static String SEPARATOR = ";";
    public static String[] parse(String response){
        if(response == null){
            String[] a = new String[1];
            a[0] = "bad";
            return a;
        }
        
        String[] answer = new String[3];
        String[] split = response.split(SEPARATOR);
        switch (split[0].toLowerCase()){
            case "wrong_request":
                answer[0] = "bad";
                break;
            case "confirm_access":
                answer[0] = "confirm";
                answer[1] = split[1];
                answer[2] = split[2];
                break;
            case "response":
                answer[0] = "good";
                answer[1] = split[1];
                break;
            case "not_confirmed":
                answer[0] = "false";
                break;
            default:
                answer[0] = response;
                break;
        }
        return answer;        
    }
}
