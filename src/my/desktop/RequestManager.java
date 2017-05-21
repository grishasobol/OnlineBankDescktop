package my.desktop;

/**
 *
 * @author Dmitry
 */
public class RequestManager {
    public static String delete(String... params){
        String str = "delete";
        switch(params[0]){
            case "all":
                str += " all";
                break;
            case "my":
                str += " my";
                break;
            case "id":
                str += (" id " + params[1]);
                break;
            default:
                break;
        }        
        return str;
    }
    
    public static String add(String... params){
        String str = "add";
        switch(params[0]){
            case "employee":
                str += " employee ";
                break;
            case "user":
                str += " user ";
                break;
            case "money":
                str += " money ";
                break;
        }
        str += (params[1] + " " + params[2]);
        
        if(params[0].equals("user")){
            str += " " + DesktopUI.pubkey;
        }
        
        if(params[0].equals("employee")){
            str += " " + params[3];
        }
        return str;
    }
    
    public static String info(String... params){
        String str = "info";
        switch(params[0]){
            case "all":
                str += " all";
                break;
            case "my":
                str += " my";
                break;
            case "user":
                str += (" user " + params[1]);
                break;
            default:
                break;
        }
        return str;
    }
}
